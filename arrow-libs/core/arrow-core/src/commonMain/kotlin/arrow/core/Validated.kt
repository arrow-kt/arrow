package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.SemigroupDeprecation
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

public typealias ValidatedNel<E, A> = Validated<Nel<E>, A>
public typealias Valid<A> = Validated.Valid<A>
public typealias Invalid<E> = Validated.Invalid<E>

public sealed class Validated<out E, out A> {

  public companion object {

    @JvmStatic
    public fun <E, A> invalidNel(e: E): ValidatedNel<E, A> = Invalid(nonEmptyListOf(e))

    @JvmStatic
    public fun <E, A> validNel(a: A): ValidatedNel<E, A> = Valid(a)

    /**
     * Converts an `Either<E, A>` to a `Validated<E, A>`.
     */
    @JvmStatic
    public fun <E, A> fromEither(e: Either<E, A>): Validated<E, A> = e.fold({ Invalid(it) }, { Valid(it) })

    /**
     * Converts an `Option<A>` to a `Validated<E, A>`, where the provided `ifNone` output value is returned as [Invalid]
     * when the specified `Option` is `None`.
     */
    @JvmStatic
    public inline fun <E, A> fromOption(o: Option<A>, ifNone: () -> E): Validated<E, A> =
      o.fold(
        { Invalid(ifNone()) },
        { Valid(it) }
      )

    /**
     * Converts a nullable `A?` to a `Validated<E, A>`, where the provided `ifNull` output value is returned as [Invalid]
     * when the specified value is null.
     */
    @JvmStatic
    public inline fun <E, A> fromNullable(value: A?, ifNull: () -> E): Validated<E, A> =
      value?.let(::Valid) ?: Invalid(ifNull())

    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <A> catch(f: () -> A): Validated<Throwable, A> =
      try {
        f().valid()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalid()
      }

    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <E, A> catch(recover: (Throwable) -> E, f: () -> A): Validated<E, A> =
      catch(f).mapLeft(recover)

    @JvmStatic
    public inline fun <A> catchNel(f: () -> A): ValidatedNel<Throwable, A> =
      try {
        f().validNel()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalidNel()
      }

    @JvmStatic
    public inline fun <E, A, B> lift(crossinline f: (A) -> B): (Validated<E, A>) -> Validated<E, B> =
      { fa -> fa.map(f) }

    /**
     * Lifts two functions to the Bifunctor type.
     *
     * ```kotlin
     * import arrow.core.*
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val f = Validated.lift(String::toUpperCase, Int::inc)
     *   val res1 = f("test".invalid())
     *   val res2 = f(1.valid())
     *   //sampleEnd
     *   println("res1: $res1")
     *   println("res2: $res2")
     * }
     * ```
     * <!--- KNIT example-validated-01.kt -->
     */
    @JvmStatic
    public inline fun <A, B, C, D> lift(
      crossinline fl: (A) -> C,
      crossinline fr: (B) -> D
    ): (Validated<A, B>) -> Validated<C, D> =
      { fa -> fa.bimap(fl, fr) }
  }

  /**
   * Discards the [A] value inside [Validated] signaling this container may be pointing to a noop
   * or an effect whose return value is deliberately ignored. The singleton value [Unit] serves as signal.
   *
   * ```kotlin
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World".valid().void()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   * <!--- KNIT example-validated-02.kt -->
   */
  public fun void(): Validated<E, Unit> =
    map { Unit }

  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <B> traverse(fa: (A) -> Iterable<B>): List<Validated<E, B>> =
    fold({ emptyList() }, { a -> fa(a).map { Valid(it) } })

  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <EE, B> traverse(fa: (A) -> Either<EE, B>): Either<EE, Validated<E, B>> =
    when (this) {
      is Valid -> fa(this.value).map { Valid(it) }
      is Invalid -> this.right()
    }

  @Deprecated("traverseEither is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <EE, B> traverseEither(fa: (A) -> Either<EE, B>): Either<EE, Validated<E, B>> =
    traverse(fa)

  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <B> traverse(fa: (A) -> Option<B>): Option<Validated<E, B>> =
    when (this) {
      is Valid -> fa(this.value).map { Valid(it) }
      is Invalid -> None
    }

  @Deprecated("traverseOption is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <B> traverseOption(fa: (A) -> Option<B>): Option<Validated<E, B>> =
    traverse(fa)

  public inline fun <B> traverseNullable(fa: (A) -> B?): Validated<E, B>? =
    when (this) {
      is Valid -> fa(this.value)?.let { Valid(it) }
      is Invalid -> null
    }

  public inline fun <B> bifoldLeft(
    c: B,
    fe: (B, E) -> B,
    fa: (B, A) -> B
  ): B =
    fold({ fe(c, it) }, { fa(c, it) })

  public inline fun <B> bifoldMap(MN: Monoid<B>, g: (E) -> B, f: (A) -> B): B = MN.run {
    bifoldLeft(MN.empty(), { c, b -> c.combine(g(b)) }) { c, a -> c.combine(f(a)) }
  }

  public inline fun <EE, B> bitraverse(fe: (E) -> Iterable<EE>, fa: (A) -> Iterable<B>): List<Validated<EE, B>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  public inline fun <EE, B, C> bitraverseEither(
    fe: (E) -> Either<EE, B>,
    fa: (A) -> Either<EE, C>
  ): Either<EE, Validated<B, C>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  public inline fun <B, C> bitraverseOption(
    fe: (E) -> Option<B>,
    fa: (A) -> Option<C>
  ): Option<Validated<B, C>> =
    fold({ fe(it).map(::Invalid) }, { fa(it).map(::Valid) })

  public inline fun <B, C> bitraverseNullable(
    fe: (E) -> B?,
    fa: (A) -> C?
  ): Validated<B, C>? =
    fold({ fe(it)?.let(::Invalid) }, { fa(it)?.let(::Valid) })

  public inline fun <B> foldMap(MB: Monoid<B>, f: (A) -> B): B =
    fold({ MB.empty() }, f)

  override fun toString(): String = fold(
    { "Validated.Invalid($it)" },
    { "Validated.Valid($it)" }
  )

  public data class Valid<out A>(val value: A) : Validated<Nothing, A>() {
    override fun toString(): String = "Validated.Valid($value)"

    public companion object {
      @PublishedApi
      internal val unit: Validated<Nothing, Unit> =
        Validated.Valid(Unit)
    }
  }

  public data class Invalid<out E>(val value: E) : Validated<E, Nothing>() {
    override fun toString(): String = "Validated.Invalid($value)"
  }

  public inline fun <B> fold(fe: (E) -> B, fa: (A) -> B): B =
    when (this) {
      is Valid -> fa(value)
      is Invalid -> (fe(value))
    }

  public val isValid: Boolean =
    fold({ false }, { true })
  public val isInvalid: Boolean =
    fold({ true }, { false })

  /**
   * Is this Valid and matching the given predicate
   */
  public inline fun exist(predicate: (A) -> Boolean): Boolean =
    fold({ false }, predicate)

  public inline fun findOrNull(predicate: (A) -> Boolean): A? =
    when (this) {
      is Valid -> if (predicate(this.value)) this.value else null
      is Invalid -> null
    }

  public inline fun all(predicate: (A) -> Boolean): Boolean =
    fold({ true }, predicate)

  public fun isEmpty(): Boolean = isInvalid

  public fun isNotEmpty(): Boolean = isValid

  /**
   * Converts the value to an Either<E, A>
   */
  public fun toEither(): Either<E, A> =
    fold(::Left, ::Right)

  /**
   * Returns Valid values wrapped in Some, and None for Invalid values
   */
  public fun toOption(): Option<A> =
    fold({ None }, ::Some)

  /**
   * Convert this value to a single element List if it is Valid,
   * otherwise return an empty List
   */
  public fun toList(): List<A> =
    fold({ listOf() }, ::listOf)

  /** Lift the Invalid value into a NonEmptyList. */
  public fun toValidatedNel(): ValidatedNel<E, A> =
    fold({ invalidNel(it) }, ::Valid)

  /**
   * Convert to an Either, apply a function, convert back. This is handy
   * when you want to use the Monadic properties of the Either type.
   */
  public inline fun <EE, B> withEither(f: (Either<E, A>) -> Either<EE, B>): Validated<EE, B> =
    fromEither(f(toEither()))

  /**
   * From [arrow.typeclasses.Bifunctor], maps both types of this Validated.
   *
   * Apply a function to an Invalid or Valid value, returning a new Invalid or Valid value respectively.
   */
  public inline fun <EE, B> bimap(fe: (E) -> EE, fa: (A) -> B): Validated<EE, B> =
    fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

  /**
   * Apply a function to a Valid value, returning a new Valid value
   */
  public inline fun <B> map(f: (A) -> B): Validated<E, B> =
    bimap(::identity, f)

  /**
   * Apply a function to an Invalid value, returning a new Invalid value.
   * Or, if the original valid was Valid, return it.
   */
  public inline fun <EE> mapLeft(f: (E) -> EE): Validated<EE, A> =
    bimap(f, ::identity)

  /**
   * The given function is applied as a fire and forget effect
   * if this is `Invalid`.
   * When applied the result is ignored and the original
   * Validated value is returned
   *
   * Example:
   * ```kotlin
   * import arrow.core.Validated
   *
   * fun main() {
   *   Validated.Valid(12).tapInvalid { println("flower") } // Result: Valid(12)
   *   Validated.Invalid(12).tapInvalid { println("flower") }  // Result: prints "flower" and returns: Invalid(12)
   * }
   * ```
   * <!--- KNIT example-validated-03.kt -->
   */
  public inline fun tapInvalid(f: (E) -> Unit): Validated<E, A> =
    when (this) {
      is Invalid -> {
        f(this.value)
        this
      }
      is Valid -> this
    }

  /**
   * The given function is applied as a fire and forget effect
   * if this is `Valid`.
   * When applied the result is ignored and the original
   * Validated value is returned
   *
   * Example:
   * ```kotlin
   * import arrow.core.Validated
   *
   * fun main() {
   *   Validated.Valid(12).tap { println("flower") } // Result: prints "flower" and returns: Valid(12)
   *   Validated.Invalid(12).tap { println("flower") }  // Result: Invalid(12)
   * }
   * ```
   * <!--- KNIT example-validated-04.kt -->
   */
  public inline fun tap(f: (A) -> Unit): Validated<E, A> =
    when (this) {
      is Invalid -> this
      is Valid -> {
        f(this.value)
        this
      }
    }

  /**
   * apply the given function to the value with the given B when
   * valid, otherwise return the given B
   */
  public inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    fold({ b }, { f(b, it) })

  public fun swap(): Validated<A, E> =
    fold(::Valid, ::Invalid)
}

public inline fun <E, A, B> Validated<E, A>.zip(combineError: (E, E) -> E, fb: Validated<E, B>): Validated<E, Pair<A, B>> =
  zip(combineError, fb, ::Pair)

public inline fun <E, A, B, Z> Validated<E, A>.zip(combineError: (E, E) -> E, fb: Validated<E, B>, f: (A, B) -> Z): Validated<E, Z> =
  when (this) {
    is Invalid -> when (fb) {
      is Invalid -> Invalid(combineError(this.value, fb.value))
      is Valid -> Invalid(this.value)
    }
    is Valid -> when (fb) {
      is Invalid -> Invalid(fb.value)
      is Valid -> Valid(f(this.value, fb.value))
    }
  }

@Deprecated(SemigroupDeprecation, ReplaceWith("SE.run { zip({ x, y -> x + y }, fb, ::Pair) }"))
public fun <E, A, B> Validated<E, A>.zip(SE: Semigroup<E>, fb: Validated<E, B>): Validated<E, Pair<A, B>> =
  SE.run { zip({ x, y -> x + y }, fb, ::Pair) }

@Deprecated(SemigroupDeprecation, ReplaceWith("SE.run { zip({ x, y -> x + y }, b, f) }"))
public inline fun <E, A, B, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  f: (A, B) -> Z
): Validated<E, Z> =
  SE.run { zip({ x, y -> x + y }, b, f) }

public inline fun <E, A, B, C, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  f: (A, B, C) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, _, _, _, _, _, _, _ ->
    f(a, b, c)
  }

public inline fun <E, A, B, C, D, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  f: (A, B, C, D) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    d,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, _, _, _, _, _, _ ->
    f(a, b, c, d)
  }

public inline fun <E, A, B, C, D, EE, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  f: (A, B, C, D, EE) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    d,
    e,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, e, _, _, _, _, _ ->
    f(a, b, c, d, e)
  }

public inline fun <E, A, B, C, D, EE, FF, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, FF>,
  f: (A, B, C, D, EE, FF) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    d,
    e,
    ff,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, e, ff, _, _, _, _ ->
    f(a, b, c, d, e, ff)
  }

public inline fun <E, A, B, C, D, EE, F, G, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  f: (A, B, C, D, EE, F, G) -> Z
): Validated<E, Z> =
  zip(SE, b, c, d, e, ff, g, Valid.unit, Valid.unit, Valid.unit) { a, b, c, d, e, ff, g, _, _, _ ->
    f(a, b, c, d, e, ff, g)
  }

public inline fun <E, A, B, C, D, EE, F, G, H, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  f: (A, B, C, D, EE, F, G, H) -> Z
): Validated<E, Z> =
  zip(SE, b, c, d, e, ff, g, h, Valid.unit, Valid.unit) { a, b, c, d, e, ff, g, h, _, _ ->
    f(a, b, c, d, e, ff, g, h)
  }

public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  i: Validated<E, I>,
  f: (A, B, C, D, EE, F, G, H, I) -> Z
): Validated<E, Z> =
  zip(SE, b, c, d, e, ff, g, h, i, Valid.unit) { a, b, c, d, e, ff, g, h, i, _ ->
    f(a, b, c, d, e, ff, g, h, i)
  }

public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  i: Validated<E, I>,
  j: Validated<E, J>,
  f: (A, B, C, D, EE, F, G, H, I, J) -> Z
): Validated<E, Z> =
  if (this is Validated.Valid && b is Validated.Valid && c is Validated.Valid && d is Validated.Valid && e is Validated.Valid && ff is Validated.Valid && g is Validated.Valid && h is Validated.Valid && i is Validated.Valid && j is Validated.Valid) {
    Validated.Valid(f(this.value, b.value, c.value, d.value, e.value, ff.value, g.value, h.value, i.value, j.value))
  } else SE.run {
    var accumulatedError: Any? = EmptyValue
    accumulatedError =
      if (this@zip is Validated.Invalid) this@zip.value else accumulatedError
    accumulatedError =
      if (b is Validated.Invalid) emptyCombine(accumulatedError, b.value) else accumulatedError
    accumulatedError =
      if (c is Validated.Invalid) emptyCombine(accumulatedError, c.value) else accumulatedError
    accumulatedError =
      if (d is Validated.Invalid) emptyCombine(accumulatedError, d.value) else accumulatedError
    accumulatedError =
      if (e is Validated.Invalid) emptyCombine(accumulatedError, e.value) else accumulatedError
    accumulatedError =
      if (ff is Validated.Invalid) emptyCombine(accumulatedError, ff.value) else accumulatedError
    accumulatedError =
      if (g is Validated.Invalid) emptyCombine(accumulatedError, g.value) else accumulatedError
    accumulatedError =
      if (h is Validated.Invalid) emptyCombine(accumulatedError, h.value) else accumulatedError
    accumulatedError =
      if (i is Validated.Invalid) emptyCombine(accumulatedError, i.value) else accumulatedError
    accumulatedError =
      if (j is Validated.Invalid) emptyCombine(accumulatedError, j.value) else accumulatedError

    Validated.Invalid(accumulatedError as E)
  }

public inline fun <E, A, B, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  f: (A, B) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, f)

public inline fun <E, A, B, C, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  f: (A, B, C) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, f)

public inline fun <E, A, B, C, D, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  f: (A, B, C, D) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, f)

public inline fun <E, A, B, C, D, EE, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  f: (A, B, C, D, EE) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, f)

public inline fun <E, A, B, C, D, EE, FF, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, FF>,
  f: (A, B, C, D, EE, FF) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, f)

public inline fun <E, A, B, C, D, EE, F, G, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  f: (A, B, C, D, EE, F, G) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, f)

public inline fun <E, A, B, C, D, EE, F, G, H, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  f: (A, B, C, D, EE, F, G, H) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, f)

public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  i: ValidatedNel<E, I>,
  f: (A, B, C, D, EE, F, G, H, I) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, i, f)

public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  i: ValidatedNel<E, I>,
  j: ValidatedNel<E, J>,
  f: (A, B, C, D, EE, F, G, H, I, J) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, i, j, f)

/**
 * Given [A] is a sub type of [B], re-type this value from Validated<E, A> to Validated<E, B>
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val string: Validated<Int, String> = "Hello".valid()
 *   val chars: Validated<Int, CharSequence> =
 *     string.widen<Int, CharSequence, String>()
 *   //sampleEnd
 *   println(chars)
 * }
 * ```
 * <!--- KNIT example-validated-05.kt -->
 */
public fun <E, B, A : B> Validated<E, A>.widen(): Validated<E, B> =
  this

public fun <EE, E : EE, A> Validated<E, A>.leftWiden(): Validated<EE, A> =
  this

public fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int): Validated<E, List<A>> =
  if (n <= 0) emptyList<A>().valid()
  else this.zip(SE, replicate(SE, n - 1)) { a, xs -> listOf(a) + xs }

public fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int, MA: Monoid<A>): Validated<E, A> =
  if (n <= 0) MA.empty().valid()
  else this@replicate.zip(SE, replicate(SE, n - 1, MA)) { a, xs -> MA.run { a + xs } }

public fun <E, A> Validated<Iterable<E>, Iterable<A>>.bisequence(): List<Validated<E, A>> =
  bitraverse(::identity, ::identity)

public fun <E, A, B> Validated<Either<E, A>, Either<E, B>>.bisequenceEither(): Either<E, Validated<A, B>> =
  bitraverseEither(::identity, ::identity)

public fun <A, B> Validated<Option<A>, Option<B>>.bisequenceOption(): Option<Validated<A, B>> =
  bitraverseOption(::identity, ::identity)

public fun <A, B> Validated<A?, B?>.bisequenceNullable(): Validated<A, B>? =
  bitraverseNullable(::identity, ::identity)

public fun <E, A> Validated<E, A>.fold(MA: Monoid<A>): A = MA.run {
  foldLeft(empty()) { acc, a -> acc.combine(a) }
}

@Deprecated("use fold instead", ReplaceWith("fold(MA)", "arrow.core.fold"))
public fun <E, A> Validated<E, A>.combineAll(MA: Monoid<A>): A =
  fold(MA)

public fun <E, A> Validated<E, Iterable<A>>.sequence(): List<Validated<E, A>> =
  traverse(::identity)

@Deprecated("sequenceEither is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <E, A, B> Validated<A, Either<E, B>>.sequenceEither(): Either<E, Validated<A, B>> =
  sequence()

public fun <E, A, B> Validated<A, Either<E, B>>.sequence(): Either<E, Validated<A, B>> =
  traverse(::identity)

@Deprecated("sequenceOption is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B> Validated<A, Option<B>>.sequenceOption(): Option<Validated<A, B>> =
  sequence()

public fun <A, B> Validated<A, Option<B>>.sequence(): Option<Validated<A, B>> =
  traverse(::identity)

@Deprecated("sequenceNullable is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B> Validated<A, B?>.sequenceNullable(): Validated<A, B>? =
  sequence()

public fun <A, B> Validated<A, B?>.sequence(): Validated<A, B>? =
  traverseNullable(::identity)

public operator fun <E : Comparable<E>, A : Comparable<A>> Validated<E, A>.compareTo(other: Validated<E, A>): Int =
  fold(
    { l1 -> other.fold({ l2 -> l1.compareTo(l2) }, { -1 }) },
    { r1 -> other.fold({ 1 }, { r2 -> r1.compareTo(r2) }) }
  )

/**
 * Return the Valid value, or the default if Invalid
 */
public inline fun <E, A> Validated<E, A>.getOrElse(default: () -> A): A =
  fold({ default() }, ::identity)

/**
 * Return the Valid value, or null if Invalid
 */
public fun <E, A> Validated<E, A>.orNull(): A? =
  getOrElse { null }

public fun <E, A> Validated<E, A>.orNone(): Option<A> =
  fold({ None }, { Some(it) })

/**
 * Return the Valid value, or the result of f if Invalid
 */
public inline fun <E, A> Validated<E, A>.valueOr(f: (E) -> A): A =
  fold({ f(it) }, ::identity)

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [orElse] except that here failures are accumulated.
 */
public inline fun <E, A> Validated<E, A>.findValid(SE: Semigroup<E>, that: () -> Validated<E, A>): Validated<E, A> =
  fold(
    { e ->
      that().fold(
        { ee -> Invalid(SE.run { e.combine(ee) }) },
        { Valid(it) }
      )
    },
    { Valid(it) }
  )

/**
 * Apply a function to a Valid value, returning a new Validation that may be valid or invalid
 *
 * Example:
 * ```kotlin
 * import arrow.core.Validated
 * import arrow.core.andThen
 *
 * fun main() {
 *   Validated.Valid(5).andThen { Validated.Valid(10) } // Result: Valid(10)
 *   Validated.Valid(5).andThen { Validated.Invalid(10) } // Result: Invalid(10)
 *   Validated.Invalid(5).andThen { Validated.Valid(10) } // Result: Invalid(5)
 * }
 * ```
 * <!--- KNIT example-validated-06.kt -->
 */
public inline fun <E, A, B> Validated<E, A>.andThen(f: (A) -> Validated<E, B>): Validated<E, B> =
  when (this) {
    is Validated.Valid -> f(value)
    is Validated.Invalid -> this
  }

/**
 * Return this if it is Valid, or else fall back to the given default.
 * The functionality is similar to that of [findValid] except for failure accumulation,
 * where here only the error on the right is preserved and the error on the left is ignored.
 */
public inline fun <E, A> Validated<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> =
  fold(
    { default() },
    { Valid(it) }
  )

public inline fun <E, A> Validated<E, A>.handleErrorWith(f: (E) -> Validated<E, A>): Validated<E, A> =
  when (this) {
    is Validated.Valid -> this
    is Validated.Invalid -> f(this.value)
  }

public inline fun <E, A> Validated<E, A>.handleError(f: (E) -> A): Validated<Nothing, A> =
  when (this) {
    is Validated.Valid -> this
    is Validated.Invalid -> Valid(f(this.value))
  }

public inline fun <E, A, B> Validated<E, A>.redeem(fe: (E) -> B, fa: (A) -> B): Validated<E, B> =
  when (this) {
    is Validated.Valid -> map(fa)
    is Validated.Invalid -> Valid(fe(this.value))
  }

public fun <E, A> Validated<E, A>.attempt(): Validated<Nothing, Either<E, A>> =
  map { Right(it) }.handleError { Left(it) }

public inline fun <A> Validated<A, A>.merge(): A =
  fold(::identity, ::identity)

public fun <E, A> Validated<E, A>.combine(
  SE: Semigroup<E>,
  SA: Semigroup<A>,
  y: Validated<E, A>
): Validated<E, A> =
  when {
    this is Valid && y is Valid -> Valid(SA.run { value.combine(y.value) })
    this is Invalid && y is Invalid -> Invalid(SE.run { value.combine(y.value) })
    this is Invalid -> this
    else -> y
  }

public fun <E, A> Validated<E, A>.combineK(SE: Semigroup<E>, y: Validated<E, A>): Validated<E, A> {
  return when (this) {
    is Valid -> this
    is Invalid -> when (y) {
      is Invalid -> Invalid(SE.run { this@combineK.value.combine(y.value) })
      is Valid -> y
    }
  }
}

/**
 * Converts the value to an Ior<E, A>
 */
public fun <E, A> Validated<E, A>.toIor(): Ior<E, A> =
  fold({ Ior.Left(it) }, { Ior.Right(it) })

public inline fun <A> A.valid(): Validated<Nothing, A> =
  Valid(this)

public inline fun <E> E.invalid(): Validated<E, Nothing> =
  Invalid(this)

public inline fun <A> A.validNel(): ValidatedNel<Nothing, A> =
  Validated.validNel(this)

public inline fun <E> E.invalidNel(): ValidatedNel<E, Nothing> =
  Validated.invalidNel(this)
