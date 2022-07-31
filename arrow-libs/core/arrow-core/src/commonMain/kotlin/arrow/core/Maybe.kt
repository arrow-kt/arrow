package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Maybe.Companion.Nothing
import arrow.core.continuations.EagerEffect
import arrow.core.continuations.EagerEffectScope
import arrow.core.continuations.Effect
import arrow.core.continuations.EffectScope
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

@RequiresOptIn(
  message =
    "This declaration is part of the internals of Maybe. Great caution must be taken to avoid ClassCastExceptions"
)
internal annotation class MaybeInternals

@JvmInline
public value class Maybe<out A>
@MaybeInternals
private constructor(@property:MaybeInternals @PublishedApi internal val underlying: Any) {

  public companion object {
    // This is simply an alias to constructor-impl
    @PublishedApi @MaybeInternals internal fun <A> construct(value: Any): Maybe<A> = Maybe(value)

    @OptIn(MaybeInternals::class) public val Nothing: Maybe<Nothing> = construct(NestedNothing(0U))

    @JvmStatic
    public inline fun <A> fromNullable(a: A?): Maybe<A> = if (a != null) Just(a) else Nothing
    @JvmStatic public inline fun fromNullable(a: Nothing?): Maybe<Nothing> = Nothing

    @JvmStatic
    public inline fun <A> fromNullable(a: Maybe<A>?): Maybe<Maybe<A>> =
      if (a != null) Just(a) else Nothing

    @JvmStatic public inline operator fun <A> invoke(a: A): Maybe<A> = Just(a)

    @JvmStatic
    @JvmName("tryCatchOrNothing")
    /** Ignores exceptions and returns Nothing if one is thrown */
    public inline fun <A> catch(f: () -> A): Maybe<A> {
      return catch({ Nothing }, f)
    }

    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <A> catch(recover: (Throwable) -> Maybe<A>, f: () -> A): Maybe<A> =
      try {
        Just(f())
      } catch (t: Throwable) {
        recover(t.nonFatalOrThrow())
      }

    @JvmStatic
    public inline fun <reified A, B> lift(crossinline f: (A) -> B): (Maybe<A>) -> Maybe<B> = {
      it.map(f)
    }
  }

  public inline fun tapNothing(f: () -> Unit): Maybe<A> = this.also { if (isEmpty()) f() }

  /** Returns true if the maybe is [Nothing], false otherwise. */
  public fun isEmpty(): Boolean = fold<Any?, Boolean>({ true }, { false })

  public fun isNotEmpty(): Boolean = !isEmpty()

  /** alias for [isDefined] */
  public val isJust: Boolean
    get() = isDefined()

  /** alias for [isEmpty] */
  public val isNothing: Boolean
    get() = isEmpty()

  /** alias for [isDefined] */
  public fun nonEmpty(): Boolean = isDefined()

  /** Returns true if the maybe is an instance of [Just], false otherwise. */
  public fun isDefined(): Boolean = isNotEmpty()

  override fun toString(): String = fold<Any?, String>({ "Maybe.Nothing" }, { "Maybe.Just($it)" })
}

@PublishedApi
internal inline fun <reified T> isTypeMaybe(): Boolean =
  boxedJustUnit is T && Unit !is T // Checks that type is not Any but is Maybe

@PublishedApi
@MaybeInternals
internal val <A> Maybe<A>.value: A
  get() =
    when (underlying) {
      is NestedNull -> if (underlying.nesting == 0U) null else NestedNull(underlying.nesting - 1U)
      is NestedNothing ->
        NestedNothing(underlying.nesting - 1U).also {
          if (it.nesting < 0U) error("Cannot unpack the value of a Maybe.Nothing")
        }
      else -> underlying
    }
      as A

public inline fun <reified A, reified B> Maybe<A>.zip(other: Maybe<B>): Maybe<Pair<A, B>> =
  zip(other, ::Pair)

public inline fun <reified A, reified B, C> Maybe<A>.zip(b: Maybe<B>, map: (A, B) -> C): Maybe<C> =
  zip(b, justUnit, justUnit, justUnit, justUnit, justUnit, justUnit, justUnit, justUnit) {
    b,
    c,
    _,
    _,
    _,
    _,
    _,
    _,
    _,
    _ ->
    map(b, c)
  }

public inline fun <reified A, reified B, reified C, D> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  map: (A, B, C) -> D
): Maybe<D> =
  zip(b, c, justUnit, justUnit, justUnit, justUnit, justUnit, justUnit, justUnit) {
    b,
    c,
    d,
    _,
    _,
    _,
    _,
    _,
    _,
    _ ->
    map(b, c, d)
  }

public inline fun <reified A, reified B, reified C, reified D, E> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  map: (A, B, C, D) -> E
): Maybe<E> =
  zip(b, c, d, justUnit, justUnit, justUnit, justUnit, justUnit, justUnit) {
    a,
    b,
    c,
    d,
    _,
    _,
    _,
    _,
    _,
    _ ->
    map(a, b, c, d)
  }

public inline fun <reified A, reified B, reified C, reified D, reified E, F> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  map: (A, B, C, D, E) -> F
): Maybe<F> =
  zip(b, c, d, e, justUnit, justUnit, justUnit, justUnit, justUnit) { a, b, c, d, e, f, _, _, _, _
    ->
    map(a, b, c, d, e)
  }

public inline fun <reified A, reified B, reified C, reified D, reified E, reified F, G> Maybe<A>
  .zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  f: Maybe<F>,
  map: (A, B, C, D, E, F) -> G
): Maybe<G> =
  zip(b, c, d, e, f, justUnit, justUnit, justUnit, justUnit) { a, b, c, d, e, f, _, _, _, _ ->
    map(a, b, c, d, e, f)
  }

public inline fun <
  reified A, reified B, reified C, reified D, reified E, reified F, reified G, H> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  f: Maybe<F>,
  g: Maybe<G>,
  map: (A, B, C, D, E, F, G) -> H
): Maybe<H> =
  zip(b, c, d, e, f, g, justUnit, justUnit, justUnit) { a, b, c, d, e, f, g, _, _, _ ->
    map(a, b, c, d, e, f, g)
  }

public inline fun <
  reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, I> Maybe<
  A>
  .zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  f: Maybe<F>,
  g: Maybe<G>,
  h: Maybe<H>,
  map: (A, B, C, D, E, F, G, H) -> I
): Maybe<I> =
  zip(b, c, d, e, f, g, h, justUnit, justUnit) { a, b, c, d, e, f, g, h, _, _ ->
    map(a, b, c, d, e, f, g, h)
  }

public inline fun <
  reified A,
  reified B,
  reified C,
  reified D,
  reified E,
  reified F,
  reified G,
  reified H,
  reified I,
  J
> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  f: Maybe<F>,
  g: Maybe<G>,
  h: Maybe<H>,
  i: Maybe<I>,
  map: (A, B, C, D, E, F, G, H, I) -> J
): Maybe<J> =
  zip(b, c, d, e, f, g, h, i, justUnit) { a, b, c, d, e, f, g, h, i, _ ->
    map(a, b, c, d, e, f, g, h, i)
  }

public inline fun <
  reified A,
  reified B,
  reified C,
  reified D,
  reified E,
  reified F,
  reified G,
  reified H,
  reified I,
  reified J,
  K
> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  f: Maybe<F>,
  g: Maybe<G>,
  h: Maybe<H>,
  i: Maybe<I>,
  j: Maybe<J>,
  map: (A, B, C, D, E, F, G, H, I, J) -> K
): Maybe<K> {
  val a =
    this.getOrElse {
      return Nothing
    }
  val b =
    b.getOrElse {
      return Nothing
    }
  val c =
    c.getOrElse {
      return Nothing
    }
  val d =
    d.getOrElse {
      return Nothing
    }
  val e =
    e.getOrElse {
      return Nothing
    }
  val f =
    f.getOrElse {
      return Nothing
    }
  val g =
    g.getOrElse {
      return Nothing
    }
  val h =
    h.getOrElse {
      return Nothing
    }
  val i =
    i.getOrElse {
      return Nothing
    }
  val j =
    j.getOrElse {
      return Nothing
    }
  return Just(map(a, b, c, d, e, f, g, h, i, j))
}

/**
 * Returns the result of applying [f] to this $maybe's value if this $maybe is nonempty. Returns
 * $nothing if this $maybe is empty. Slightly different from `map` in that $f is expected to return
 * a $maybe (which could be $nothing).
 *
 * @param f the function to apply
 * @see map
 */
public inline fun <reified A, B> Maybe<A>.flatMap(f: (A) -> Maybe<B>): Maybe<B> =
  fold({ Nothing }, f)

@OptIn(MaybeInternals::class)
public inline fun <reified A, R> Maybe<A>.fold(ifEmpty: () -> R, ifJust: (A) -> R): R =
  when (this) {
    Nothing -> ifEmpty()
    else -> {
      val value = value
      if (isTypeMaybe<A>() && value != null) Maybe.construct<Any>(value).invokeBlockOnMaybe(ifJust)
      else ifJust(value)
    }
  }

@PublishedApi
internal inline fun <R, A> Maybe<*>.invokeBlockOnMaybe(block: (A) -> R): R {
  return block(this as A)
}

public inline fun <reified A> Maybe<A>.tap(f: (A) -> Unit): Maybe<A> = this.also { fold({}, f) }

public inline fun <reified A> Maybe<A>.orNull(): A? =
  fold(
    {
      return null
    },
    ::identity
  )

@JvmName("orNullMaybe")
public fun <A> Maybe<Maybe<A>>.orNull(): Maybe<A>? =
  fold(
    {
      return null
    },
    ::identity
  )

/**
 * Returns a [Just<$B>] containing the result of applying $f to this $maybe's value if this $maybe
 * is nonempty. Otherwise return $nothing.
 *
 * @note This is similar to `flatMap` except here, $f does not need to wrap its result in a $maybe.
 *
 * @param f the function to apply
 * @see flatMap
 */
public inline fun <reified A, B> Maybe<A>.map(f: (A) -> B): Maybe<B> = flatMap { a -> Just(f(a)) }

/**
 * Returns $nothing if the result of applying $f to this $maybe's value is null. Otherwise returns
 * the result.
 *
 * @note This is similar to `.flatMap { Maybe.fromNullable(null)) }` and primarily for convenience.
 *
 * @param f the function to apply.
 */
public inline fun <reified A, B> Maybe<A>.mapNotNull(f: (A) -> B?): Maybe<B> = flatMap { a ->
  Maybe.fromNullable(f(a))
}

/** Align two maybes (`this` on the left and [b] on the right) as one Maybe of [Ior]. */
public inline infix fun <reified A, reified B> Maybe<A>.align(b: Maybe<B>): Maybe<Ior<A, B>> =
  fold(
    { b.fold({ Nothing }, { b -> Just(b.rightIor()) }) },
    { a -> b.fold({ Just(a.leftIor()) }, { b -> Just(Pair(a, b).bothIor()) }) }
  )

/**
 * Align two maybes (`this` on the left and [b] on the right) as one Maybe of [Ior], and then, if
 * it's not [Nothing], map it using [f].
 *
 * @note This function works like a regular `align` function, but is then mapped by the `map`
 * function.
 */
public inline fun <reified A, reified B, C> Maybe<A>.align(
  b: Maybe<B>,
  f: (Ior<A, B>) -> C
): Maybe<C> = align(b).map(f)

/**
 * Returns true if this maybe is empty '''or''' the predicate $predicate returns true when applied
 * to this $maybe's value.
 *
 * @param predicate the predicate to test
 */
public inline fun <reified A> Maybe<A>.all(predicate: (A) -> Boolean): Boolean =
  fold({ true }, predicate)

public inline fun <reified A, B> Maybe<A>.crosswalk(f: (A) -> Maybe<B>): Maybe<Maybe<B>> =
  fold({ Nothing }, { value -> f(value).map<Any?, Maybe<B>> { Just(it) as Maybe<B> } })

public inline fun <reified A, K, V> Maybe<A>.crosswalkMap(f: (A) -> Map<K, V>): Map<K, Maybe<V>> =
  fold({ emptyMap() }, { value -> f(value).mapValues { Just(it.value) } })

public inline fun <reified A, B> Maybe<A>.crosswalkNull(f: (A) -> B?): Maybe<B>? =
  fold(
    {
      return null
    },
    { value ->
      return f(value)?.let { Just(it) }
    }
  )

/**
 * Returns this $maybe if it is nonempty '''and''' applying the predicate $p to this $maybe's value
 * returns true. Otherwise, return $nothing.
 *
 * @param predicate the predicate used for testing.
 */
public inline fun <reified A> Maybe<A>.filter(predicate: (A) -> Boolean): Maybe<A> = flatMap { a ->
  if (predicate(a)) Just(a) else Nothing
}

/**
 * Returns this $maybe if it is nonempty '''and''' applying the predicate $p to this $maybe's value
 * returns false. Otherwise, return $nothing.
 *
 * @param predicate the predicate used for testing.
 */
public inline fun <reified A> Maybe<A>.filterNot(predicate: (A) -> Boolean): Maybe<A> =
  flatMap { a ->
    if (!predicate(a)) Just(a) else Nothing
  }

public inline fun <reified A> Maybe<A>.exists(predicate: (A) -> Boolean): Boolean =
  fold({ false }, predicate)

public inline fun <reified A> Maybe<A>.findOrNull(predicate: (A) -> Boolean): A? =
  fold(
    {
      return null
    },
    {
      return if (predicate(it)) it else null
    }
  )

@JvmName("maybeFindOrNull")
public inline fun <A> Maybe<Maybe<A>>.findOrNull(predicate: (Maybe<A>) -> Boolean): Maybe<A>? =
  fold(
    {
      return null
    },
    {
      return if (predicate(it)) it else null
    }
  )

public inline fun <reified A, B> Maybe<A>.foldMap(MB: Monoid<B>, f: (A) -> B): B =
  MB.run { foldLeft(empty()) { b, a -> b.combine(f(a)) } }

@JvmName("foldMapMaybe")
public inline fun <reified A, B> Maybe<A>.foldMap(
  MB: Monoid<Maybe<B>>,
  f: (A) -> Maybe<B>
): Maybe<B> = MB.run { foldLeft(emptyMaybe()) { b, a -> combineToMaybe(b, f(a)).flatten() } }

public inline fun <reified A, B> Maybe<A>.foldLeft(initial: B, operation: (B, A) -> B): B =
  fold({ initial }, { operation(initial, it) })

public inline fun <reified A, B> Maybe<A>.foldLeft(
  initial: Maybe<B>,
  operation: (Maybe<B>, A) -> Maybe<B>
): Maybe<B> = fold({ initial }, { operation(initial, it) })

public inline fun <reified A, reified B> Maybe<A>.padZip(other: Maybe<B>): Maybe<Pair<A?, B?>> =
  padZip(other, ::Pair)

public inline fun <reified A, reified B, C> Maybe<A>.padZip(
  other: Maybe<B>,
  f: (A?, B?) -> C
): Maybe<C> =
  fold(
    { other.fold({ Nothing }, { b -> Just(f(null, b)) }) },
    { a -> other.fold({ Just(f(a, null)) }, { b -> Just(f(a, b)) }) }
  )

public inline fun <reified A, B> Maybe<A>.reduceOrNull(
  initial: (A) -> B,
  operation: (acc: B, A) -> B
): B? =
  fold(
    {
      return null
    },
    { operation(initial(it), it) }
  )

public inline fun <reified A, B> Maybe<A>.reduceOrNullMaybe(
  initial: (A) -> Maybe<B>,
  operation: (acc: Maybe<B>, A) -> Maybe<B>
): Maybe<B>? =
  fold(
    {
      return null
    },
    { operation(initial(it), it) }
  )

public inline fun <reified A> Maybe<A>.replicate(n: Int): Maybe<List<A>> =
  if (n <= 0) Just(emptyList()) else map { a -> List(n) { a } }

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <reified A, B> Maybe<A>.traverse(fa: (A) -> Iterable<B>): List<Maybe<B>> =
  fold({ emptyList() }, { a -> fa(a).map { Just(it) } })

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <reified A, AA, B> Maybe<A>.traverse(
  fa: (A) -> Either<AA, B>
): Either<AA, Maybe<B>> = fold({ Right(Nothing) }, { value -> fa(value).map { Just(it) } })

@Deprecated(
  "traverseEither is being renamed to traverse to simplify the Arrow API",
  ReplaceWith("traverse(fa)")
)
public inline fun <reified A, AA, B> Maybe<A>.traverseEither(
  fa: (A) -> Either<AA, B>
): Either<AA, Maybe<B>> = traverse(fa)

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <reified A, AA, B> Maybe<A>.traverse(
  fa: (A) -> Validated<AA, B>
): Validated<AA, Maybe<B>> = fold({ Valid(Nothing) }, { value -> fa(value).map { Just(it) } })

@Deprecated(
  "traverseValidated is being renamed to traverse to simplify the Arrow API",
  ReplaceWith("traverse(fa)")
)
public inline fun <reified A, AA, B> Maybe<A>.traverseValidated(
  fa: (A) -> Validated<AA, B>
): Validated<AA, Maybe<B>> = traverse(fa)

public inline fun <reified A, L> Maybe<A>.toEither(ifEmpty: () -> L): Either<L, A> =
  fold({ ifEmpty().left() }, { it.right() })

public inline fun <reified A> Maybe<A>.toList(): List<A> = fold(::emptyList) { listOf(it) }

public fun Maybe<*>.void(): Maybe<Unit> = justUnit

public inline fun <reified A, L> Maybe<A>.pairLeft(left: L): Maybe<Pair<L, A>> =
  this.map { left to it }

public inline fun <reified A, R> Maybe<A>.pairRight(right: R): Maybe<Pair<A, R>> =
  this.map { it to right }

public infix fun <A, X> Maybe<A>.and(value: Maybe<X>): Maybe<X> =
  if (isEmpty()) {
    Nothing
  } else {
    value
  }

@PublishedApi
internal data class NestedNothing private constructor(val nesting: UInt) {
  companion object {
    private val cache = Array(22) { NestedNothing(it.toUInt()) }
    operator fun invoke(nesting: UInt) =
      cache.getOrElse(nesting.toInt()) { NestedNothing(it.toUInt()) }
  }

  override fun toString(): String =
    nesting.toInt().coerceAtLeast(0).let { nesting ->
      buildString("Maybe.Just()".length * nesting + "Maybe.Nothing".length) {
        repeat(nesting) { this.append("Maybe.Just(") }
        append("Maybe.Nothing")
        repeat(nesting) { this.append(")") }
      }
    }
}

@PublishedApi
internal data class NestedNull private constructor(val nesting: UInt) {
  companion object {
    private val cache = Array(22) { NestedNull(it.toUInt()) }
    operator fun invoke(nesting: UInt) =
      cache.getOrElse(nesting.toInt()) { NestedNull(it.toUInt()) }
  }

  override fun toString(): String =
    nesting.toInt().coerceAtLeast(0).let { nesting ->
      buildString("Maybe.Just()".length * nesting + "null".length) {
        repeat(nesting) { this.append("Maybe.Just(") }
        append("null")
        repeat(nesting) { this.append(")") }
      }
    }
}

@OptIn(MaybeInternals::class)
public inline fun <T> Just(value: T): Maybe<T> {
  // This `is` check gets inlined by the compiler if `value` is statically known to be Maybe
  return _Just(if (value is Maybe<*>) value.underlying else value)
}

@MaybeInternals
@PublishedApi
internal fun <T> _Just(trueValue: Any?): Maybe<T> =
  Maybe.construct(
    when (trueValue) {
      is NestedNothing -> NestedNothing(trueValue.nesting + 1U)
      is NestedNull -> NestedNull(trueValue.nesting + 1U)
      else -> trueValue ?: NestedNull(0U)
    }
  )

@PublishedApi internal val justUnit: Maybe<Unit> = Just(Unit)

@PublishedApi internal val boxedJustUnit: Any = justUnit

/**
 * Returns the maybe's value if the maybe is nonempty, otherwise return the result of evaluating
 * `default`.
 *
 * @param default the default expression.
 */
public inline fun <reified T> Maybe<T>.getOrElse(default: () -> T): T =
  fold({ default() }, ::identity)

/**
 * Returns the maybe's value if the maybe is nonempty, otherwise return the result of evaluating
 * `default`.
 *
 * @param default the default expression.
 */
@JvmName("maybeGetOrElse")
public inline fun <T> Maybe<Maybe<T>>.getOrElse(default: () -> Maybe<T>): Maybe<T> =
  fold({ default() }, ::identity)

/**
 * Returns this maybe's if the maybe is nonempty, otherwise returns another maybe provided lazily by
 * `default`.
 *
 * @param alternative the default maybe if this is empty.
 */
public inline fun <A> Maybe<A>.orElse(alternative: () -> Maybe<A>): Maybe<A> =
  if (isEmpty()) alternative() else this

public infix fun <T> Maybe<T>.or(value: Maybe<T>): Maybe<T> =
  if (isEmpty()) {
    value
  } else {
    this
  }

public inline fun <T> T?.toMaybe(): Maybe<T> = this?.let { Just(it) } ?: Nothing

public inline fun Nothing?.toMaybe(): Maybe<Nothing> = Nothing

public fun <T> Maybe<T>?.toMaybe(): Maybe<Maybe<T>> = this?.let { Just(it) } ?: Nothing

public inline fun <A> Boolean.maybe2(f: () -> A): Maybe<A> =
  if (this) {
    Just(f())
  } else {
    Nothing
  }

public inline fun <A> A.just(): Maybe<A> = Just(this)

public fun <A> nothing(): Maybe<A> = Nothing

public inline fun <reified A> Monoid.Companion.maybe(MA: Semigroup<A>): Monoid<Maybe<A>> =
  if (isTypeMaybe<A>()) MaybeMonoidNested(MA as Semigroup<Maybe<A>>) as Monoid<Maybe<A>>
  else MaybeMonoid(MA)

@JvmName("maybeMaybe")
public inline fun <A> Monoid.Companion.maybe(MA: Semigroup<Maybe<A>>): Monoid<Maybe<Maybe<A>>> =
  MaybeMonoidNested(MA)

@PublishedApi
internal open class MaybeMonoid<A>(protected val MA: Semigroup<A>) : Monoid<Maybe<A>> {

  override fun Maybe<A>.combine(b: Maybe<A>): Maybe<A> =
    combine(MA as Semigroup<Any?>, b) as Maybe<A>

  override fun Maybe<A>.maybeCombine(b: Maybe<A>?): Maybe<A> =
    b?.let { combine(MA as Semigroup<Any?>, it) as Maybe<A> } ?: this

  override fun empty(): Maybe<A> = Nothing
}

@PublishedApi
internal class MaybeMonoidNested<A>(MA: Semigroup<Maybe<A>>) : MaybeMonoid<Maybe<A>>(MA) {

  override fun Maybe<Maybe<A>>.combine(b: Maybe<Maybe<A>>): Maybe<Maybe<A>> = combine(MA, b)

  override fun Maybe<Maybe<A>>.maybeCombine(b: Maybe<Maybe<A>>?): Maybe<Maybe<A>> =
    b?.let { combine(MA, it) } ?: this

  override fun empty(): Maybe<Maybe<A>> = Nothing
}

@Deprecated(
  "use fold instead",
  ReplaceWith("fold(Monoid.maybe(MA))", "arrow.core.fold", "arrow.typeclasses.Monoid")
)
public inline fun <reified A> Iterable<Maybe<A>>.combineAll(MA: Monoid<A>): Maybe<A> =
  fold(Monoid.maybe(MA))

@Deprecated("use getOrElse instead", ReplaceWith("getOrElse { MA.empty() }"))
public inline fun <reified A> Maybe<A>.combineAll(MA: Monoid<A>): A = getOrElse { MA.empty() }

@JvmName("maybeCombineAll")
@Deprecated("use getOrElse instead", ReplaceWith("getOrElse { MA.empty() }"))
public fun <A> Maybe<Maybe<A>>.combineAll(MA: Monoid<Maybe<A>>): Maybe<A> = getOrElse {
  MA.emptyMaybe()
}

public inline fun <reified A> Maybe<A>.ensure(
  error: () -> Unit,
  predicate: (A) -> Boolean
): Maybe<A> =
  fold(
    { this },
    {
      if (predicate(it)) this
      else {
        error()
        Nothing
      }
    }
  )

/** Returns a Maybe containing all elements that are instances of specified type parameter [B]. */
public inline fun <reified B> Maybe<*>.filterIsInstance(): Maybe<B> = flatMap {
  when (it) {
    is B -> Just(it)
    else -> Nothing
  }
}

@JvmName("maybeFilterIsInstance")
public inline fun <reified B> Maybe<Maybe<*>>.filterIsInstance(): Maybe<B> = flatMap {
  when (it) {
    is B -> Just(it)
    else -> Nothing
  }
}

public inline fun <A> Maybe<A>.handleError(f: (Unit) -> A): Maybe<A> = handleErrorWith {
  Just(f(Unit))
}

public inline fun <A> Maybe<A>.handleErrorWith(f: (Unit) -> Maybe<A>): Maybe<A> =
  if (isEmpty()) f(Unit) else this

public fun <A> Maybe<Maybe<A>>.flatten(): Maybe<A> = flatMap(::identity)

public inline fun <reified A, B> Maybe<A>.redeem(fe: (Unit) -> B, fb: (A) -> B): Maybe<B> =
  map(fb).handleError(fe)

public inline fun <reified A, B> Maybe<A>.redeemWith(
  fe: (Unit) -> Maybe<B>,
  fb: (A) -> Maybe<B>
): Maybe<B> = flatMap(fb).handleErrorWith(fe)

public inline fun <reified A> Maybe<A>.replicate(n: Int, MA: Monoid<A>): Maybe<A> =
  MA.run {
    if (n <= 0) Just(empty())
    else
      map { a ->
        var result = empty()
        repeat(n) { result += a }
        result
      }
  }

@JvmName("replicateMaybe")
public fun <A> Maybe<Maybe<A>>.replicate(n: Int, MA: Monoid<Maybe<A>>): Maybe<Maybe<A>> =
  MA.run {
    if (n <= 0) Just(emptyMaybe())
    else
      map { a ->
        var result = emptyMaybe()
        repeat(n) { result = MA.combineToMaybe(result, a).flatten() }
        result
      }
  }

@PublishedApi
internal inline fun <A> Monoid<Maybe<A>>.emptyMaybe(): Maybe<A> =
  when (this) {
    is MaybeMonoid<*> -> (this as MaybeMonoid<*>).empty()
    else -> this.empty()
  }
    as Maybe<A>

public fun <A> Maybe<Either<Unit, A>>.rethrow(): Maybe<A> = flatMap {
  it.fold({ Nothing }, { a -> Just(a) })
}

public inline fun <reified A> Maybe<A>.salign(SA: Semigroup<A>, b: Maybe<A>): Maybe<A> {
  map { a ->
    b.map { b ->
      return SA.combineToMaybe(a, b)
    }
  }
  return this or b
}

/**
 * Separate the inner [Either] value into the [Either.Left] and [Either.Right].
 *
 * @receiver Maybe of Either
 * @return a tuple containing Maybe of [Either.Left] and another Maybe of its [Either.Right] value.
 */
public fun <A, B> Maybe<Either<A, B>>.separateEither(): Pair<Maybe<A>, Maybe<B>> {
  val asep = flatMap { gab -> gab.fold({ Just(it) }, { Nothing }) }
  val bsep = flatMap { gab -> gab.fold({ Nothing }, { Just(it) }) }
  return asep to bsep
}

/**
 * Separate the inner [Validated] value into the [Validated.Invalid] and [Validated.Valid].
 *
 * @receiver Maybe of Either
 * @return a tuple containing Maybe of [Validated.Invalid] and another Maybe of its
 * [Validated.Valid] value.
 */
public fun <A, B> Maybe<Validated<A, B>>.separateValidated(): Pair<Maybe<A>, Maybe<B>> {
  val asep = flatMap { gab -> gab.fold({ Just(it) }, { Nothing }) }
  val bsep = flatMap { gab -> gab.fold({ Nothing }, { Just(it) }) }
  return asep to bsep
}

public fun <A> Maybe<Iterable<A>>.sequence(): List<Maybe<A>> = traverse(::identity)

@Deprecated(
  "sequenceEither is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B> Maybe<Either<A, B>>.sequenceEither(): Either<A, Maybe<B>> = sequence()

public fun <A, B> Maybe<Either<A, B>>.sequence(): Either<A, Maybe<B>> = traverse(::identity)

@Deprecated(
  "sequenceValidated is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B> Maybe<Validated<A, B>>.sequenceValidated(): Validated<A, Maybe<B>> = sequence()

public fun <A, B> Maybe<Validated<A, B>>.sequence(): Validated<A, Maybe<B>> = traverse(::identity)

public fun <A, B> Maybe<Ior<A, B>>.unalign(): Pair<Maybe<A>, Maybe<B>> = unalign(::identity)

public inline fun <A, B, reified C> Maybe<C>.unalign(
  f: (C) -> Ior<A, B>
): Pair<Maybe<A>, Maybe<B>> =
  this.map(f)
    .fold(
      { Nothing to Nothing },
      {
        when (it) {
          is Ior.Left -> Just(it.value) to Nothing
          is Ior.Right -> Nothing to Just(it.value)
          is Ior.Both -> Just(it.leftValue) to Just(it.rightValue)
        }
      }
    )

public fun <A> Maybe<Iterable<A>>.unite(MA: Monoid<A>): Maybe<A> = map { iterable ->
  iterable.fold(MA)
}

public fun <A, B> Maybe<Either<A, B>>.uniteEither(): Maybe<B> = flatMap { either ->
  either.fold({ Nothing }, { b -> Just(b) })
}

public fun <A, B> Maybe<Validated<A, B>>.uniteValidated(): Maybe<B> = flatMap { validated ->
  validated.fold({ Nothing }, { b -> Just(b) })
}

public fun <A, B> Maybe<Pair<A, B>>.unzip(): Pair<Maybe<A>, Maybe<B>> = unzip(::identity)

public inline fun <A, B, reified C> Maybe<C>.unzip(f: (C) -> Pair<A, B>): Pair<Maybe<A>, Maybe<B>> =
  fold({ Nothing to Nothing }, { f(it).let { pair -> Just(pair.first) to Just(pair.second) } })

public fun <B, A : B> Maybe<A>.widen(): Maybe<B> = this

public fun <K, V> Maybe<Pair<K, V>>.toMap(): Map<K, V> = this.toList().toMap()

public inline fun <reified A> Maybe<A>.combine(SGA: Semigroup<A>, b: Maybe<A>): Maybe<A> =
  fold({ b }, { first -> b.fold({ this }, { second -> SGA.combineToMaybe(first, second) }) })

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <A> Semigroup<A>.combineToMaybe(first: A, second: A): Maybe<A> =
  when {
    this is MaybeMonoid<*> ->
      (this as MaybeMonoid<Any?>).run {
        Just((first as Maybe<*>).combine(second as Maybe<*>)) as Maybe<A>
      }
    first is Maybe<*> || second is Maybe<*> ->
      (this as Semigroup<Maybe<*>>).run {
        Just((first as Maybe<*>).rebox().combine((second as Maybe<*>).rebox()) as A)
      }
    else -> Just(first.combine(second))
  }

/**
 * Purposefully NOT inline because, as the name suggests, this will cause the [this] to be reboxed,
 * which convinces the compiler that [this] Maybe is not being used in an Any or generic context
 */
@PublishedApi internal fun <A> Maybe<A>.rebox(): Maybe<A> = this

public inline operator fun <reified A : Comparable<A>> Maybe<A>.compareTo(other: Maybe<A>): Int =
  fold({ other.fold({ 0 }, { -1 }) }, { a1 -> other.fold({ 1 }, { a2 -> a1.compareTo(a2) }) })

public fun <A, B> Either<A, B>.orNothing(): Maybe<B> = fold({ Nothing }, { Just(it) })

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, B, reified C> Either<A, B>.traverse(
  fa: (B) -> Maybe<C>
): Maybe<Either<A, C>> = fold({ Nothing }, { right -> fa(right).map(::Right) })

public inline fun <A, B, reified AA, reified C> Either<A, B>.bitraverseMaybe(
  fl: (A) -> Maybe<AA>,
  fr: (B) -> Maybe<C>
): Maybe<Either<AA, C>> = fold({ fl(it).map(::Left) }, { fr(it).map(::Right) })

public inline fun <A, reified B> Either<A, Maybe<B>>.sequence(): Maybe<Either<A, B>> =
  traverse(::identity)

public inline fun <reified A, reified B> Either<Maybe<A>, Maybe<B>>.bisequenceMaybe():
  Maybe<Either<A, B>> = bitraverseMaybe(::identity, ::identity)

public inline fun <A, B, reified C, reified D> Ior<A, B>.bitraverseMaybe(
  fa: (A) -> Maybe<C>,
  fb: (B) -> Maybe<D>
): Maybe<Ior<C, D>> =
  fold(
    { fa(it).map { Ior.Left(it) } },
    { fb(it).map { Ior.Right(it) } },
    { a, b -> fa(a).flatMap { aa -> fb(b).map { c -> Ior.Both(aa, c) } } }
  )

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, B, reified C> Ior<A, B>.traverse(fa: (B) -> Maybe<C>): Maybe<Ior<A, C>> =
  fold(
    { a -> Just(Ior.Left(a)) },
    { b -> fa(b).map { Ior.Right(it) } },
    { a, b -> fa(b).map { Ior.Both(a, it) } }
  )

public inline fun <reified B, reified C> Ior<Maybe<B>, Maybe<C>>.bisequenceMaybe():
  Maybe<Ior<B, C>> = bitraverseMaybe(::identity, ::identity)

public inline fun <A, reified B> Ior<A, Maybe<B>>.sequence(): Maybe<Ior<A, B>> =
  traverse(::identity)

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, reified B> Iterable<A>.traverse(f: (A) -> Maybe<B>): Maybe<List<B>> {
  val destination = ArrayList<B>(collectionSizeOrDefault(10))
  for (item in this) {
    f(item)
      .fold(
        {
          return Nothing
        },
        { destination.add(it) }
      )
  }
  return destination.just()
}

public inline fun <reified A> Iterable<Maybe<A>>.sequence(): Maybe<List<A>> = traverse(::identity)

/**
 * Returns the first element as [Just(element)][Just], or [Maybe.Nothing] if the iterable is empty.
 */
public fun <T> Iterable<T>.firstOrNothing(): Maybe<T> =
  when (this) {
    is Collection ->
      if (!isEmpty()) {
        Just(first())
      } else {
        Nothing
      }
    else -> {
      iterator().nextOrNothing()
    }
  }

private fun <T> Iterator<T>.nextOrNothing(): Maybe<T> =
  if (hasNext()) {
    Just(next())
  } else {
    Nothing
  }

/**
 * Returns the first element as [Just(element)][Just] matching the given [predicate], or
 * [Maybe.Nothing] if element was not found.
 */
public inline fun <T> Iterable<T>.firstOrNothing(predicate: (T) -> Boolean): Maybe<T> {
  for (element in this) {
    if (predicate(element)) {
      return Just(element)
    }
  }
  return Nothing
}

/**
 * Returns single element as [Just(element)][Just], or [Maybe.Nothing] if the iterable is empty or
 * has more than one element.
 */
public fun <T> Iterable<T>.singleOrNothing(): Maybe<T> =
  when (this) {
    is Collection ->
      when (size) {
        1 -> firstOrNothing()
        else -> Nothing
      }
    else -> {
      iterator().run { nextOrNothing().filter<Any?> { !hasNext() } as Maybe<T> }
    }
  }

/**
 * Returns the single element as [Just(element)][Just] matching the given [predicate], or
 * [Maybe.Nothing] if element was not found or more than one element was found.
 */
public inline fun <T> Iterable<T>.singleOrNothing(predicate: (T) -> Boolean): Maybe<T> {
  var result: Maybe<T> = Nothing
  for (element in this) {
    if (predicate(element)) {
      if (result.isNotEmpty()) {
        return Nothing
      }
      result = Just(element)
    }
  }
  return result
}

/**
 * Returns the last element as [Just(element)][Just], or [Maybe.Nothing] if the iterable is empty.
 */
public fun <T> Iterable<T>.lastOrNothing(): Maybe<T> =
  when (this) {
    is Collection ->
      if (!isEmpty()) {
        Just(last())
      } else {
        Nothing
      }
    else ->
      iterator().run {
        if (hasNext()) {
          var last: T
          do last = next() while (hasNext())
          Just(last)
        } else {
          Nothing
        }
      }
  }

/**
 * Returns the last element as [Just(element)][Just] matching the given [predicate], or
 * [Maybe.Nothing] if no such element was found.
 */
public inline fun <T> Iterable<T>.lastOrNothing(predicate: (T) -> Boolean): Maybe<T> {
  var value: Maybe<T> = Nothing
  for (element in this) {
    if (predicate(element)) {
      value = Just(element)
    }
  }
  return value
}

/**
 * Returns an element as [Just(element)][Just] at the given [index] or [Maybe.Nothing] if the
 * [index] is out of bounds of this iterable.
 */
public fun <T> Iterable<T>.elementAtOrNothing(index: Int): Maybe<T> =
  when {
    index < 0 -> Nothing
    this is Collection ->
      when (index) {
        in indices -> Just(elementAt(index))
        else -> Nothing
      }
    else -> iterator().skip(index).nextOrNothing()
  }

private tailrec fun <T> Iterator<T>.skip(count: Int): Iterator<T> =
  when {
    count > 0 && hasNext() -> {
      next()
      skip(count - 1)
    }
    else -> this
  }

public inline fun <reified T> Iterable<Maybe<T>>.filterMaybe(): List<T> =
  mapNotNull(Maybe<T>::orNull)

public inline fun <reified T> Iterable<Maybe<T>>.flattenMaybe(): List<T> = filterMaybe()

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <K, A, reified B> Map<K, A>.traverse(f: (A) -> Maybe<B>): Maybe<Map<K, B>> {
  val acc = mutableMapOf<K, B>()
  forEach { (k, v) ->
    f(v)
      .fold(
        {
          return@traverse Nothing
        },
        { acc[k] = it }
      )
  }
  return acc.just()
}

public inline fun <K, reified V> Map<K, Maybe<V>>.sequence(): Maybe<Map<K, V>> =
  traverse(::identity)

public inline fun <K, reified A> Map<K, Maybe<A>>.filterOption(): Map<K, A> = filterMap {
  it.orNull()
}

public fun <K, V> Map<K, V>.getOrNothing(key: K): Maybe<V> = this[key].toMaybe()

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, reified B> NonEmptyList<A>.traverse(
  f: (A) -> Maybe<B>
): Maybe<NonEmptyList<B>> =
  f(head).map { newHead ->
    val acc = mutableListOf<B>()
    tail.forEach { a ->
      f(a)
        .fold(
          {
            return@traverse Nothing
          },
          { acc.add(it) }
        )
    }
    NonEmptyList(newHead, acc)
  }

public inline fun <reified A> NonEmptyList<Maybe<A>>.sequence(): Maybe<NonEmptyList<A>> =
  traverse(::identity)

public inline fun <reified A> Sequence<Maybe<A>>.sequence(): Maybe<List<A>> = traverse(::identity)

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, reified B> Sequence<A>.traverse(f: (A) -> Maybe<B>): Maybe<List<B>> {
  // Note: Using a mutable list here avoids the stackoverflows one can accidentally create when
  // using
  //  Sequence.plus instead. But we don't convert the sequence to a list beforehand to avoid
  //  forcing too much of the sequence to be evaluated.
  val acc = mutableListOf<B>()
  forEach { a ->
    f(a)
      .fold(
        {
          return@traverse Nothing
        },
        { acc.add(it) }
      )
  }
  return Just(acc)
}

public inline fun <reified A> Sequence<Maybe<A>>.filterMaybe(): Sequence<A> =
  mapNotNull(Maybe<A>::orNull)

/**
 * Converts an `Maybe<A>` to a `Validated<E, A>`, where the provided `ifNothing` output value is
 * returned as [Invalid] when the specified `Maybe` is `Nothing`.
 */
public inline fun <E, reified A> Validated.Companion.fromMaybe(
  o: Maybe<A>,
  ifNothing: () -> E
): Validated<E, A> = o.fold({ Validated.Invalid(ifNothing()) }, { Validated.Valid(it) })

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <E, A, reified B> Validated<E, A>.traverse(
  fa: (A) -> Maybe<B>
): Maybe<Validated<E, B>> =
  when (this) {
    is Validated.Valid -> fa(this.value).map { Validated.Valid(it) }
    is Validated.Invalid -> Nothing
  }

public inline fun <E, A, reified B, reified C> Validated<E, A>.bitraverseMaybe(
  fe: (E) -> Maybe<B>,
  fa: (A) -> Maybe<C>
): Maybe<Validated<B, C>> = fold({ fe(it).map(::Invalid) }, { fa(it).map(::Valid) })

/** Returns Valid values wrapped in Just, and Nothing for Invalid values */
public fun <E, A> Validated<E, A>.toMaybe(): Maybe<A> = fold({ Nothing }, ::Just)

public inline fun <reified A, reified B> Validated<Maybe<A>, Maybe<B>>.bisequenceMaybe():
  Maybe<Validated<A, B>> = bitraverseMaybe(::identity, ::identity)

public inline fun <A, reified B> Validated<A, Maybe<B>>.sequence(): Maybe<Validated<A, B>> =
  traverse(::identity)

public fun <E, A> Validated<E, A>.orNothing(): Maybe<A> = fold({ Nothing }, { Just(it) })

/**
 * [fold] the [EagerEffect] into a [Maybe]. Where the shifted value [R] is mapped to [Maybe] by the
 * provided function [orElse], and result value [A] is mapped to [Just].
 */
@OptIn(MaybeInternals::class)
public inline fun <R, A> EagerEffect<R, A>.toMaybe(crossinline orElse: (R) -> Maybe<A>): Maybe<A> {
  // An effect's fold is not inline, so it will box the maybe.
  // That is, unless we pass the underlying values instead and construct the maybe on the outside
  // Also, orElse is crossinline because a normal function would just box
  return Maybe.construct(fold({ orElse(it).underlying }, { Just(it).underlying }))
}

/**
 * [fold] the [Effect] into an [Maybe]. Where the shifted value [R] is mapped to [Maybe] by the
 * provided function [orElse], and result value [A] is mapped to [Just].
 */
@OptIn(MaybeInternals::class)
public suspend inline fun <R, A> Effect<R, A>.toMaybe(
  crossinline orElse: (R) -> Maybe<A>
): Maybe<A> {
  // An effect's fold is not inline, so it will box the maybe.
  // That is, unless we pass the underlying values instead and construct the maybe on the outside
  // Also, orElse is crossinline because a normal function would just box
  return Maybe.construct(fold({ orElse(it).underlying }, { Just(it).underlying }))
}

public suspend inline fun <R, reified B> EagerEffectScope<R>.bind(
  maybe: Maybe<B>,
  shift: () -> R
): B = maybe.fold({ shift(shift()) }, { it })

@JvmName("bindMaybe")
public suspend fun <R, B> EagerEffectScope<R>.bind(
  maybe: Maybe<Maybe<B>>,
  shift: () -> R
): Maybe<B> {
  // Similar in spirit to EagerEffectScope.bind(shift)
  // We don't want the maybe argument to "hang around" after the suspension point of the shift
  // And so instead we use the maybe argument immediately, get its contents or null, and *then*
  // if those contents are null, we shift.
  return maybe.orNull() ?: shift(shift())
}

public suspend inline fun <R, reified B> EffectScope<R>.bind(maybe: Maybe<B>, shift: () -> R): B =
  maybe.fold({ shift(shift()) }, { it })

@JvmName("bindMaybe")
public suspend fun <R, B> EffectScope<R>.bind(maybe: Maybe<Maybe<B>>, shift: () -> R): Maybe<B> {
  // Similar in spirit to EagerEffectScope.bind(shift)
  // We don't want the maybe argument to "hang around" after the suspension point of the shift
  // And so instead we use the maybe argument immediately, get its contents or null, and *then*
  // if those contents are null, we shift.
  return maybe.orNull() ?: shift(shift())
}
