package arrow.core

import arrow.core.Either.Right
import arrow.core.Maybe.Companion.Nothing
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 *
 *
 * If you have worked with Java at all in the past, it is very likely that you have come across a `NullPointerException` at some time (other languages will throw similarly named errors in such a case). Usually this happens because some method returns `null` when you weren't expecting it and, thus, isn't dealing with that possibility in your client code. A value of `null` is often abused to represent an absent optional value.
 * Kotlin tries to solve the problem by getting rid of `null` values altogether, and providing its own special syntax [Null-safety machinery based on `?`](https://kotlinlang.org/docs/reference/null-safety.html).
 *
 * Arrow models the absence of values through the `Maybe` datatype similar to how Scala, Haskell, and other FP languages handle optional values.
 *
 * `Maybe<A>` is a container for an optional value of type `A`. If the value of type `A` is present, the `Maybe<A>` is an instance of `Just<A>`, containing the present value of type `A`. If the value is absent, the `Maybe<A>` is the object `Nil`.
 *
 * ```kotlin
 * import arrow.core.Maybe
 * import arrow.core.Just
 * import arrow.core.nil
 *
 * //sampleStart
 * val someValue: Maybe<String> = Just("I am wrapped in something")
 * val emptyValue: Maybe<String> = nil()
 * //sampleEnd
 * fun main() {
 *  println("value = $someValue")
 *  println("emptyValue = $emptyValue")
 * }
 * ```
 * <!--- KNIT example-maybe-01.kt -->
 *
 * Let's write a function that may or may not give us a string, thus returning `Maybe<String>`:
 *
 * ```kotlin
 * import arrow.core.Nil
 * import arrow.core.Maybe
 * import arrow.core.Just
 *
 * //sampleStart
 * fun maybeItWillReturnSomething(flag: Boolean): Maybe<String> =
 *  if (flag) Just("Found value") else Nil
 * //sampleEnd
 * ```
 * <!--- KNIT example-maybe-02.kt -->
 *
 * Using `getOrElse`, we can provide a default value `"No value"` when the optional argument `Nil` does not exist:
 *
 * ```kotlin
 * import arrow.core.Nil
 * import arrow.core.Maybe
 * import arrow.core.Just
 * import arrow.core.getOrElse
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Maybe<String> =
 *  if (flag) Just("Found value") else Nil
 *
 * val value1 =
 * //sampleStart
 *  maybeItWillReturnSomething(true)
 *     .getOrElse { "No value" }
 * //sampleEnd
 * fun main() {
 *  println(value1)
 * }
 * ```
 * <!--- KNIT example-maybe-03.kt -->
 *
 * ```kotlin
 * import arrow.core.Nil
 * import arrow.core.Maybe
 * import arrow.core.Just
 * import arrow.core.getOrElse
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Maybe<String> =
 *  if (flag) Just("Found value") else Nil
 *
 * val value2 =
 * //sampleStart
 *  maybeItWillReturnSomething(false)
 *   .getOrElse { "No value" }
 * //sampleEnd
 * fun main() {
 *  println(value2)
 * }
 * ```
 * <!--- KNIT example-maybe-04.kt -->
 *
 * Checking whether maybe has value:
 *
 * ```kotlin
 * import arrow.core.Nil
 * import arrow.core.Maybe
 * import arrow.core.Just
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Maybe<String> =
 *  if (flag) Just("Found value") else Nil
 *
 *  //sampleStart
 * val valueJust = maybeItWillReturnSomething(true) is Nil
 * val valueNil = maybeItWillReturnSomething(false) is Nil
 * //sampleEnd
 * fun main() {
 *  println("valueJust = $valueJust")
 *  println("valueNil = $valueNil")
 * }
 * ```
 * <!--- KNIT example-maybe-05.kt -->
 * Creating a `Maybe<T>` of a `T?`. Useful for working with values that can be nullable:
 *
 * ```kotlin
 * import arrow.core.Maybe
 *
 * //sampleStart
 * val myString: String? = "Nullable string"
 * val maybe: Maybe<String> = Maybe.fromNullable(myString)
 * //sampleEnd
 * fun main () {
 *  println("maybe = $maybe")
 * }
 * ```
 * <!--- KNIT example-maybe-06.kt -->
 *
 * Maybe can also be used with when statements:
 *
 * ```kotlin
 * import arrow.core.Nil
 * import arrow.core.Maybe
 * import arrow.core.Just
 *
 * //sampleStart
 * val justValue: Maybe<Double> = Just(20.0)
 * val value = when(justValue) {
 *  isJust -> justValue.value
 *  is Nil -> 0.0
 * }
 * //sampleEnd
 * fun main () {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-maybe-07.kt -->
 *
 * ```kotlin
 * import arrow.core.Nil
 * import arrow.core.Maybe
 * import arrow.core.Just
 *
 * //sampleStart
 * val noValue: Maybe<Double> = Nil
 * val value = when(noValue) {
 *  isJust -> noValue.value
 *  is Nil -> 0.0
 * }
 * //sampleEnd
 * fun main () {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-maybe-08.kt -->
 *
 * An alternative for pattern matching is folding. This is possible because an maybe could be looked at as a collection or foldable structure with either one or zero elements.
 *
 * One of these operations is `map`. This operation allows us to map the inner value to a different type while preserving the maybe:
 *
 * ```kotlin
 * import arrow.core.Nil
 * import arrow.core.Maybe
 * import arrow.core.Just
 *
 * //sampleStart
 * val number: Maybe<Int> = Just(3)
 * val noNumber: Maybe<Int> = Nil
 * val mappedResult1 = number.map { it * 1.5 }
 * val mappedResult2 = noNumber.map { it * 1.5 }
 * //sampleEnd
 * fun main () {
 *  println("number = $number")
 *  println("noNumber = $noNumber")
 *  println("mappedResult1 = $mappedResult1")
 *  println("mappedResult2 = $mappedResult2")
 * }
 * ```
 * <!--- KNIT example-maybe-09.kt -->
 * Another operation is `fold`. This operation will extract the value from the maybe, or provide a default if the value is `Nil`
 *
 * ```kotlin
 * import arrow.core.Maybe
 * import arrow.core.Just
 *
 * val fold =
 * //sampleStart
 *  Just(3).fold({ 1 }, { it * 3 })
 * //sampleEnd
 * fun main () {
 *  println(fold)
 * }
 * ```
 * <!--- KNIT example-maybe-10.kt -->
 *
 * ```kotlin
 * import arrow.core.Maybe
 * import arrow.core.nil
 *
 * val fold =
 * //sampleStart
 *  nil<Int>().fold({ 1 }, { it * 3 })
 * //sampleEnd
 * fun main () {
 *  println(fold)
 * }
 * ```
 * <!--- KNIT example-maybe-11.kt -->
 *
 * Arrow also adds syntax to all datatypes so you can easily lift them into the context of `Maybe` where needed.
 *
 * ```kotlin
 * import arrow.core.just
 * import arrow.core.nil
 *
 * //sampleStart
 *  val just = 1.just()
 *  val nil = nil<String>()
 * //sampleEnd
 * fun main () {
 *  println("just = $just")
 *  println("nil = $nil")
 * }
 * ```
 * <!--- KNIT example-maybe-12.kt -->
 *
 * ```kotlin
 * import arrow.core.toMaybe
 *
 * //sampleStart
 * val nullString: String? = null
 * val valueFromNull = nullString.toMaybe()
 *
 * val helloString: String? = "Hello"
 * val valueFromStr = helloString.toMaybe()
 * //sampleEnd
 * fun main () {
 *  println("valueFromNull = $valueFromNull")
 *  println("valueFromStr = $valueFromStr")
 * }
 * ```
 * <!--- KNIT example-maybe-13.kt -->
 *
 * You can easily convert between `A?` and `Maybe<A>` by using the `toMaybe()` extension or `Maybe.fromNullable` constructor.
 *
 * ```kotlin
 * import arrow.core.firstOrNil
 * import arrow.core.toMaybe
 * import arrow.core.Maybe
 *
 * //sampleStart
 * val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")
 *
 * val empty = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toMaybe()
 * val filled = Maybe.fromNullable(foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() })
 *
 * //sampleEnd
 * fun main() {
 *  println("empty = $empty")
 *  println("filled = $filled")
 * }
 * ```
 * <!--- KNIT example-maybe-14.kt -->
 *
 * ### Transforming the inner contents
 *
 * ```kotlin
 * import arrow.core.Just
 *
 * fun main() {
 * val value =
 *  //sampleStart
 *    Just(1).map { it + 1 }
 *  //sampleEnd
 *  println(value)
 * }
 * ```
 * <!--- KNIT example-maybe-15.kt -->
 *
 * ### Computing over independent values
 *
 * ```kotlin
 * import arrow.core.Just
 *
 *  val value =
 * //sampleStart
 *  Just(1).zip(Just("Hello"), Just(20.0), ::Triple)
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 * <!--- KNIT example-maybe-16.kt -->
 *
 * ### Computing over dependent values ignoring absence
 *
 * ```kotlin
 * import arrow.core.computations.maybe
 * import arrow.core.Just
 * import arrow.core.Maybe
 *
 * suspend fun value(): Maybe<Int> =
 * //sampleStart
 *  maybe {
 *    val a = Just(1).bind()
 *    val b = Just(1 + a).bind()
 *    val c = Just(1 + b).bind()
 *    a + b + c
 * }
 * //sampleEnd
 * suspend fun main() {
 *  println(value())
 * }
 * ```
 * <!--- KNIT example-maybe-17.kt -->
 *
 * ```kotlin
 * import arrow.core.computations.maybe
 * import arrow.core.Just
 * import arrow.core.nil
 * import arrow.core.Maybe
 *
 * suspend fun value(): Maybe<Int> =
 * //sampleStart
 *  maybe {
 *    val x = nil<Int>().bind()
 *    val y = Just(1 + x).bind()
 *    val z = Just(1 + y).bind()
 *    x + y + z
 *  }
 * //sampleEnd
 * suspend fun main() {
 *  println(value())
 * }
 * ```
 * <!--- KNIT example-maybe-18.kt -->
 *
 * ## Credits
 *
 * Contents partially adapted from [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options)
 * Originally based on the Scala Koans.
 */

@RequiresOptIn(message = "This declaration is part of the internals of Maybe. Great caution must be taken to avoid ClassCastExceptions")
internal annotation class MaybeInternals

@JvmInline
public value class Maybe<out A : Any> @MaybeInternals private constructor(
  @property:MaybeInternals
  @PublishedApi internal val underlying: Any
) {

  public companion object {
    // This is simply an alias to constructor-impl
    @PublishedApi
    @MaybeInternals
    internal fun <A : Any> construct(value: Any): Maybe<A> = Maybe(value)

    @OptIn(MaybeInternals::class)
    public val Nothing: Maybe<Nothing> = construct(Nil(0))

    @JvmStatic
    public fun <A : Any> fromNullable(a: A?): Maybe<A> = if (a != null) Just(a) else Nothing

    @JvmStatic
    public fun <A : Any> fromNullable(a: Maybe<A>?): Maybe<Maybe<A>> = if (a != null) Just(a) else Nothing

    @JvmStatic
    public operator fun <A : Any> invoke(a: A): Maybe<A> = Just(a)

    @JvmStatic
    public operator fun <A : Any> invoke(a: Maybe<A>): Maybe<Maybe<A>> = Just(a)

    @JvmStatic
    @JvmName("tryCatchOrNothing")
    /**
     * Ignores exceptions and returns Nothing if one is thrown
     */
    public inline fun <A : Any> catch(f: () -> A): Maybe<A> {
      val recover: (Throwable) -> Maybe<A> = { Nothing }
      return catch(recover, f)
    }

    @JvmStatic
    @JvmName("tryCatchOrNothingMaybe")
    /**
     * Ignores exceptions and returns Nothing if one is thrown
     */
    public inline fun <A : Any> catchMaybe(f: () -> Maybe<A>): Maybe<Maybe<A>> {
      val recover: (Throwable) -> Maybe<Maybe<A>> = { Nothing }
      return catchMaybe(recover, f)
    }

    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <A : Any> catch(recover: (Throwable) -> Maybe<A>, f: () -> A): Maybe<A> =
      try {
        Just(f())
      } catch (t: Throwable) {
        recover(t.nonFatalOrThrow())
      }

    @JvmStatic
    @JvmName("tryCatchMaybe")
    public inline fun <A : Any> catchMaybe(
      recover: (Throwable) -> Maybe<Maybe<A>>,
      f: () -> Maybe<A>
    ): Maybe<Maybe<A>> =
      try {
        Just(f())
      } catch (t: Throwable) {
        recover(t.nonFatalOrThrow())
      }

    @JvmStatic
    public inline fun <reified A : Any, B : Any> lift(crossinline f: (A) -> B): (Maybe<A>) -> Maybe<B> =
      { it.map(f) }
  }

  /**
   * The given function is applied as a fire and forget effect
   * if this is a `Nil`.
   * When applied the result is ignored and the original
   * Nil value is returned
   *
   * Example:
   * ```kotlin
   * import arrow.core.Just
   * import arrow.core.nil
   *
   * fun main() {
   *   Just(12).tapNil { println("flower") } // Result: Just(12)
   *   nil<Int>().tapNil { println("flower") }  // Result: prints "flower" and returns: Nil
   * }
   * ```
   * <!--- KNIT example-maybe-19.kt -->
   */
  public inline fun tapNothing(f: () -> Unit): Maybe<A> = this.also { if (isEmpty()) f() }

  /**
   * Returns true if the maybe is [Nothing], false otherwise.
   * @note Used only for performance instead of fold.
   */
  public fun isEmpty(): Boolean = fold<Any, Boolean>({ true }, { false })

  public fun isNotEmpty(): Boolean = !isEmpty()

  /**
   * alias for [isDefined]
   */
  public val isJust: Boolean get() = isDefined()

  /**
   * alias for [isEmpty]
   */
  public val isNil: Boolean get() = isEmpty()

  /**
   * alias for [isDefined]
   */
  public fun nonEmpty(): Boolean = isDefined()

  /**
   * Returns true if the maybe is an instance of [Just], false otherwise.
   * @note Used only for performance instead of fold.
   */
  public fun isDefined(): Boolean = isNotEmpty()

  override fun toString(): String = fold<Any, String>(
    { "Maybe.Nil" },
    { "Maybe.Just($it)" }
  )
}

@PublishedApi
internal inline fun <reified T> isTypeMaybe(): Boolean =
  boxedJustUnit is T && Unit !is T // Checks that type is not Any but is Maybe

@PublishedApi
@MaybeInternals
internal inline val <reified A : Any> Maybe<A>.value: A
  get() {
    val trueValue = when (underlying) {
      is Nil -> Nil(underlying.nesting - 1).also { if (it.nesting < 0) error("Unexpected nesting value for Nil") }
      else -> underlying
    }
    return (if (isTypeMaybe<A>()) Maybe.construct<Any>(trueValue) else trueValue) as A
  }

@get:JvmName("getMaybeValue")
@PublishedApi
@MaybeInternals
internal inline val <A : Any> Maybe<Maybe<A>>.value: Maybe<A>
  get() =
    Maybe.construct((this as Maybe<*>).value) // Should not box since its return type is Maybe

public inline fun <reified A : Any, reified B : Any> Maybe<A>.zip(other: Maybe<B>): Maybe<Pair<A, B>> =
  zip(other, ::Pair)

public inline fun <reified A : Any, reified B : Any, C : Any> Maybe<A>.zip(
  b: Maybe<B>,
  map: (A, B) -> C
): Maybe<C> =
  zip(
    b,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit
  ) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

public inline fun <reified A : Any, reified B : Any, reified C : Any, D : Any> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  map: (A, B, C) -> D
): Maybe<D> =
  zip(
    b,
    c,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit
  ) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

public inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, E : Any> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  map: (A, B, C, D) -> E
): Maybe<E> =
  zip(
    b,
    c,
    d,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit,
    justUnit
  ) { a, b, c, d, _, _, _, _, _, _ -> map(a, b, c, d) }

public inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, F : Any> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  map: (A, B, C, D, E) -> F
): Maybe<F> =
  zip(b, c, d, e, justUnit, justUnit, justUnit, justUnit, justUnit) { a, b, c, d, e, f, _, _, _, _ ->
    map(
      a,
      b,
      c,
      d,
      e
    )
  }

public inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, G : Any> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  f: Maybe<F>,
  map: (A, B, C, D, E, F) -> G
): Maybe<G> =
  zip(b, c, d, e, f, justUnit, justUnit, justUnit, justUnit) { a, b, c, d, e, f, _, _, _, _ ->
    map(
      a,
      b,
      c,
      d,
      e,
      f
    )
  }

public inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, H : Any> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  f: Maybe<F>,
  g: Maybe<G>,
  map: (A, B, C, D, E, F, G) -> H
): Maybe<H> =
  zip(b, c, d, e, f, g, justUnit, justUnit, justUnit) { a, b, c, d, e, f, g, _, _, _ -> map(a, b, c, d, e, f, g) }

public inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any, I : Any> Maybe<A>.zip(
  b: Maybe<B>,
  c: Maybe<C>,
  d: Maybe<D>,
  e: Maybe<E>,
  f: Maybe<F>,
  g: Maybe<G>,
  h: Maybe<H>,
  map: (A, B, C, D, E, F, G, H) -> I
): Maybe<I> =
  zip(b, c, d, e, f, g, h, justUnit, justUnit) { a, b, c, d, e, f, g, h, _, _ -> map(a, b, c, d, e, f, g, h) }

public inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any, reified I : Any, J : Any> Maybe<A>.zip(
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
  zip(b, c, d, e, f, g, h, i, justUnit) { a, b, c, d, e, f, g, h, i, _ -> map(a, b, c, d, e, f, g, h, i) }

public inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any, reified I : Any, reified J : Any, K : Any> Maybe<A>.zip(
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
): Maybe<K> =
  @OptIn(MaybeInternals::class) // For simplicity of implementation
  if (this.isJust && b.isJust && c.isJust && d.isJust && e.isJust && f.isJust && g.isJust && h.isJust && i.isJust && j.isJust) {
    Just(map(this.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value))
  } else {
    Nothing
  }

/**
 * Returns the result of applying $f to this $maybe's value if
 * this $maybe is nonempty.
 * Returns $nil if this $maybe is empty.
 * Slightly different from `map` in that $f is expected to
 * return an $maybe (which could be $nil).
 *
 * @param f the function to apply
 * @see map
 */
public inline fun <reified A : Any, B : Any> Maybe<A>.flatMap(f: (A) -> Maybe<B>): Maybe<B> =
  fold({ Nothing }, f)

/**
 * Returns the result of applying $f to this $maybe's value if
 * this $maybe is nonempty.
 * Returns $nil if this $maybe is empty.
 * Slightly different from `map` in that $f is expected to
 * return an $maybe (which could be $nil).
 *
 * @param f the function to apply
 * @see map
 */
@JvmName("maybeFlatMap")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.flatMap(f: (Maybe<A>) -> Maybe<B>): Maybe<B> =
  fold({ Nothing }, f)

@OptIn(MaybeInternals::class)
public inline fun <reified A : Any, R> Maybe<A>.fold(ifEmpty: () -> R, ifJust: (A) -> R): R = when (this) {
  Nothing -> ifEmpty()
  else -> ifJust(value)
}

@OptIn(MaybeInternals::class)
@JvmName("maybeFold")
public inline fun <A : Any, R> Maybe<Maybe<A>>.fold(ifEmpty: () -> R, ifJust: (Maybe<A>) -> R): R = when (this) {
  Nothing -> ifEmpty()
  else -> ifJust(value)
}
@OptIn(MaybeInternals::class)
public inline fun <reified A : Any, R : Any> Maybe<A>.foldMaybe(
  ifEmpty: () -> Maybe<R>,
  ifJust: (A) -> Maybe<R>
): Maybe<R> = when (this) {
  Nothing -> ifEmpty()
  else -> ifJust(value)
}

@OptIn(MaybeInternals::class)
@JvmName("maybeFoldMaybe")
public inline fun <A : Any, R : Any> Maybe<Maybe<A>>.foldMaybe(
  ifEmpty: () -> Maybe<R>,
  ifJust: (Maybe<A>) -> Maybe<R>
): Maybe<R> = when (this) {
  Nothing -> ifEmpty()
  else -> ifJust(value)
}

/**
 * The given function is applied as a fire and forget effect
 * if this is a `just`.
 * When applied the result is ignored and the original
 * Just value is returned
 *
 * Example:
 * ```kotlin
 * import arrow.core.Just
 * import arrow.core.nil
 *
 * fun main() {
 *   Just(12).tap { println("flower") } // Result: prints "flower" and returns: Just(12)
 *   nil<Int>().tap { println("flower") }  // Result: Nil
 * }
 * ```
 * <!--- KNIT example-maybe-20.kt -->
 */
public inline fun <reified A : Any> Maybe<A>.tap(f: (A) -> Unit): Maybe<A> =
  this.also { fold({}, f) }

@JvmName("maybeTap")
public inline fun <A : Any> Maybe<Maybe<A>>.tap(f: (Maybe<A>) -> Unit): Maybe<Maybe<A>> =
  this.also { fold({}, f) }

public inline fun <reified A : Any> Maybe<A>.orNull(): A? = fold({ null }, ::identity)

@JvmName("orNullMaybe")
public fun <A : Any> Maybe<Maybe<A>>.orNull(): Maybe<A>? = foldMaybe({ return null }, ::identity)

/**
 * Returns a [Just<$B>] containing the result of applying $f to this $maybe's
 * value if this $maybe is nonempty. Otherwise return $nil.
 *
 * @note This is similar to `flatMap` except here,
 * $f does not need to wrap its result in a $maybe.
 *
 * @param f the function to apply
 * @see flatMap
 */
public inline fun <reified A : Any, B : Any> Maybe<A>.map(f: (A) -> B): Maybe<B> =
  flatMap { a -> Just(f(a)) }

@JvmName("maybeMap")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.map(f: (Maybe<A>) -> B): Maybe<B> =
  flatMap { a -> Just(f(a)) }

public inline fun <reified A : Any, B : Any> Maybe<A>.mapMaybe(f: (A) -> Maybe<B>): Maybe<Maybe<B>> =
  flatMap { a -> Just(f(a)) }

@JvmName("maybeMapMaybe")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.mapMaybe(f: (Maybe<A>) -> Maybe<B>): Maybe<Maybe<B>> =
  flatMap { a -> Just(f(a)) }

/**
 * Returns $nil if the result of applying $f to this $maybe's value is null.
 * Otherwise returns the result.
 *
 * @note This is similar to `.flatMap { Maybe.fromNullable(null)) }`
 * and primarily for convenience.
 *
 * @param f the function to apply.
 * */
public inline fun <reified A : Any, B : Any> Maybe<A>.mapNotNull(f: (A) -> B?): Maybe<B> =
  flatMap { a -> Maybe.fromNullable(f(a)) }

/**
 * Returns $nil if the result of applying $f to this $maybe's value is null.
 * Otherwise returns the result.
 *
 * @note This is similar to `.flatMap { Maybe.fromNullable(null)) }`
 * and primarily for convenience.
 *
 * @param f the function to apply.
 * */

@JvmName("maybeMapNotNull")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.mapNotNull(f: (Maybe<A>) -> B?): Maybe<B> =
  flatMap { a -> Maybe.fromNullable(f(a)) }

/**
 * Returns $nil if the result of applying $f to this $maybe's value is null.
 * Otherwise returns the result.
 *
 * @note This is similar to `.flatMap { Maybe.fromNullable(null)) }`
 * and primarily for convenience.
 *
 * @param f the function to apply.
 * */
public inline fun <reified A : Any, B : Any> Maybe<A>.mapMaybeNotNull(f: (A) -> Maybe<B>?): Maybe<Maybe<B>> =
  flatMap { a -> Maybe.fromNullable(f(a)) }

/**
 * Returns $nil if the result of applying $f to this $maybe's value is null.
 * Otherwise returns the result.
 *
 * @note This is similar to `.flatMap { Maybe.fromNullable(null)) }`
 * and primarily for convenience.
 *
 * @param f the function to apply.
 * */

@JvmName("maybeMapMaybeNotNull")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.mapMaybeNotNull(f: (Maybe<A>) -> Maybe<B>?): Maybe<Maybe<B>> =
  flatMap { a -> Maybe.fromNullable(f(a)) }


/**
 * Align two maybes (`this` on the left and [b] on the right) as one Maybe of [Ior].
 */
public inline infix fun <reified A : Any, reified B : Any> Maybe<A>.align(b: Maybe<B>): Maybe<Ior<A, B>> =
  fold({
    b.fold(
      { Nothing },
      { b -> Just(b.rightIor()) })
  },
    { a ->
      b.fold(
        { Just(a.leftIor()) },
        { b -> Just(Pair(a, b).bothIor()) })
    }
  )


/**
 * Align two maybes (`this` on the left and [b] on the right) as one Maybe of [Ior], and then, if it's not [Nothing], map it using [f].
 *
 * @note This function works like a regular `align` function, but is then mapped by the `map` function.
 */
public inline fun <reified A : Any, reified B : Any, C : Any> Maybe<A>.align(
  b: Maybe<B>,
  f: (Ior<A, B>) -> C
): Maybe<C> =
  align(b).map(f)

/**
 * Align two maybes (`this` on the left and [b] on the right) as one Maybe of [Ior], and then, if it's not [Nothing], map it using [f].
 *
 * @note This function works like a regular `align` function, but is then mapped by the `map` function.
 */
public inline fun <reified A : Any, reified B : Any, C : Any> Maybe<A>.alignMaybe(
  b: Maybe<B>,
  f: (Ior<A, B>) -> Maybe<C>
): Maybe<Maybe<C>> =
  align(b).mapMaybe(f)

/**
 * Returns true if this maybe is empty '''or''' the predicate
 * $predicate returns true when applied to this $maybe's value.
 *
 * @param predicate the predicate to test
 */
public inline fun <reified A : Any> Maybe<A>.all(predicate: (A) -> Boolean): Boolean =
  fold({ true }, predicate)

/**
 * Returns true if this maybe is empty '''or''' the predicate
 * $predicate returns true when applied to this $maybe's value.
 *
 * @param predicate the predicate to test
 */
@JvmName("maybeAll")
public inline fun <A : Any> Maybe<Maybe<A>>.all(predicate: (Maybe<A>) -> Boolean): Boolean =
  fold({ true }, predicate)

public inline fun <reified A : Any, B : Any> Maybe<A>.crosswalk(f: (A) -> Maybe<B>): Maybe<Maybe<B>> =
  fold(
    { Nothing },
    { value -> f(value).mapMaybe<Any, B> { Just(it) as Maybe<B> } })

@JvmName("maybeCrosswalk")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.crosswalk(f: (Maybe<A>) -> Maybe<B>): Maybe<Maybe<B>> =
  fold(
    { Nothing },
    { value -> f(value).mapMaybe<Any, B> { Just(it) as Maybe<B> } })

public inline fun <reified A : Any, K, V : Any> Maybe<A>.crosswalkMap(f: (A) -> Map<K, V>): Map<K, Maybe<V>> =
  fold(
    { emptyMap() },
    { value -> f(value).mapValues { Just(it.value) } }
  )

@JvmName("maybeCrosswalkMap")
public inline fun <A : Any, K, V : Any> Maybe<Maybe<A>>.crosswalkMap(f: (Maybe<A>) -> Map<K, V>): Map<K, Maybe<V>> =
  fold(
    { emptyMap() },
    { value -> f(value).mapValues { Just(it.value) } }
  )

public inline fun <reified A : Any, B : Any> Maybe<A>.crosswalkNull(f: (A) -> B?): Maybe<B>? =
  fold(
    { null },
    { value -> f(value)?.let { Just(it) } }
  )

@JvmName("maybeCrosswalkNull")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.crosswalkNull(f: (Maybe<A>) -> B?): Maybe<B>? =
  fold(
    { null },
    { value -> f(value)?.let { Just(it) } }
  )

public inline fun <reified A : Any, B : Any> Maybe<A>.crosswalkNullMaybe(f: (A) -> Maybe<B>?): Maybe<Maybe<B>>? =
  fold(
    { null },
    { value -> f(value)?.let { Just(it) } }
  )

@JvmName("maybeCrosswalkNullMaybe")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.crosswalkNullMaybe(f: (Maybe<A>) -> Maybe<B>?): Maybe<Maybe<B>>? =
  fold(
    { null },
    { value -> f(value)?.let { Just(it) } }
  )

/**
 * Returns this $maybe if it is nonempty '''and''' applying the predicate $p to
 * this $maybe's value returns true. Otherwise, return $nil.
 *
 *  @param predicate the predicate used for testing.
 */
public inline fun <reified A : Any> Maybe<A>.filter(predicate: (A) -> Boolean): Maybe<A> =
  flatMap { a -> if (predicate(a)) Just(a) else Nothing }

/**
 * Returns this $maybe if it is nonempty '''and''' applying the predicate $p to
 * this $maybe's value returns true. Otherwise, return $nil.
 *
 *  @param predicate the predicate used for testing.
 */
@JvmName("maybeFilter")
public inline fun <A : Any> Maybe<Maybe<A>>.filter(predicate: (Maybe<A>) -> Boolean): Maybe<Maybe<A>> =
  flatMap { a -> if (predicate(a)) Just(a) else Nothing }

/**
 * Returns this $maybe if it is nonempty '''and''' applying the predicate $p to
 * this $maybe's value returns false. Otherwise, return $nil.
 *
 * @param predicate the predicate used for testing.
 */
public inline fun <reified A : Any> Maybe<A>.filterNot(predicate: (A) -> Boolean): Maybe<A> =
  flatMap { a -> if (!predicate(a)) Just(a) else Nothing }

/**
 * Returns this $maybe if it is nonempty '''and''' applying the predicate $p to
 * this $maybe's value returns false. Otherwise, return $nil.
 *
 * @param predicate the predicate used for testing.
 */
@JvmName("maybeFilterNot")
public inline fun <A : Any> Maybe<Maybe<A>>.filterNot(predicate: (Maybe<A>) -> Boolean): Maybe<Maybe<A>> =
  flatMap { a -> if (!predicate(a)) Just(a) else Nothing }

/**
 * Returns true if this maybe is nonempty '''and''' the predicate
 * $p returns true when applied to this $maybe's value.
 * Otherwise, returns false.
 *
 * Example:
 * ```kotlin
 * import arrow.core.Just
 * import arrow.core.Nil
 * import arrow.core.Maybe
 *
 * fun main() {
 *   Just(12).exists { it > 10 } // Result: true
 *   Just(7).exists { it > 10 }  // Result: false
 *
 *   val nil: Maybe<Int> = Nil
 *   nil.exists { it > 10 }      // Result: false
 * }
 * ```
 * <!--- KNIT example-maybe-21.kt -->
 *
 * @param predicate the predicate to test
 */
public inline fun <reified A : Any> Maybe<A>.exists(predicate: (A) -> Boolean): Boolean = fold({ false }, predicate)

/**
 * Returns true if this maybe is nonempty '''and''' the predicate
 * $p returns true when applied to this $maybe's value.
 * Otherwise, returns false.
 *
 * Example:
 * ```kotlin
 * import arrow.core.Just
 * import arrow.core.Nil
 * import arrow.core.Maybe
 *
 * fun main() {
 *   Just(12).exists { it > 10 } // Result: true
 *   Just(7).exists { it > 10 }  // Result: false
 *
 *   val nil: Maybe<Int> = Nil
 *   nil.exists { it > 10 }      // Result: false
 * }
 * ```
 * <!--- KNIT example-maybe-21.kt -->
 *
 * @param predicate the predicate to test
 */
@JvmName("maybeExists")
public inline fun <A : Any> Maybe<Maybe<A>>.exists(predicate: (Maybe<A>) -> Boolean): Boolean =
  fold({ false }, predicate)

/**
 * Returns the $maybe's value if this maybe is nonempty '''and''' the predicate
 * $p returns true when applied to this $maybe's value.
 * Otherwise, returns null.
 *
 * Example:
 * ```kotlin
 * import arrow.core.Just
 * import arrow.core.Nil
 * import arrow.core.Maybe
 *
 * fun main() {
 *   Just(12).exists { it > 10 } // Result: 12
 *   Just(7).exists { it > 10 }  // Result: null
 *
 *   val nil: Maybe<Int> = Nil
 *   nil.exists { it > 10 }      // Result: null
 * }
 * ```
 * <!--- KNIT example-maybe-22.kt -->
 */
public inline fun <reified A : Any> Maybe<A>.findOrNull(predicate: (A) -> Boolean): A? =
  fold(
    { null },
    { if (predicate(it)) it else null }
  )

/**
 * Returns the $maybe's value if this maybe is nonempty '''and''' the predicate
 * $p returns true when applied to this $maybe's value.
 * Otherwise, returns null.
 *
 * Example:
 * ```kotlin
 * import arrow.core.Just
 * import arrow.core.Nil
 * import arrow.core.Maybe
 *
 * fun main() {
 *   Just(12).exists { it > 10 } // Result: 12
 *   Just(7).exists { it > 10 }  // Result: null
 *
 *   val nil: Maybe<Int> = Nil
 *   nil.exists { it > 10 }      // Result: null
 * }
 * ```
 * <!--- KNIT example-maybe-22.kt -->
 */

@JvmName("maybeFindOrNull")
public inline fun <A : Any> Maybe<Maybe<A>>.findOrNull(predicate: (Maybe<A>) -> Boolean): Maybe<A>? =
  fold(
    { null },
    { if (predicate(it)) it else null }
  )

public inline fun <reified A : Any, B> Maybe<A>.foldMap(MB: Monoid<B>, f: (A) -> B): B = MB.run {
  foldLeft(empty()) { b, a -> b.combine(f(a)) }
}

@JvmName("maybeFoldMap")
public inline fun <A : Any, B> Maybe<Maybe<A>>.foldMap(MB: Monoid<B>, f: (Maybe<A>) -> B): B = MB.run {
  foldLeft(empty()) { b, a -> b.combine(f(a)) }
}

public inline fun <reified A : Any, B : Any> Maybe<A>.foldMapMaybe(MB: Monoid<Maybe<B>>, f: (A) -> Maybe<B>): Maybe<B> =
  MB.run {
    foldLeftMaybe(empty()) { b, a -> b.combine(f(a)) }
  }

@JvmName("maybeFoldMapMaybe")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.foldMapMaybe(
  MB: Monoid<Maybe<B>>,
  f: (Maybe<A>) -> Maybe<B>
): Maybe<B> = MB.run {
  foldLeftMaybe(empty()) { b, a -> b.combine(f(a)) }
}

public inline fun <reified A : Any, B> Maybe<A>.foldLeft(initial: B, operation: (B, A) -> B): B =
  fold(
    { initial },
    { operation(initial, it) }
  )

@JvmName("maybeFoldLeft")
public inline fun <A : Any, B> Maybe<Maybe<A>>.foldLeft(initial: B, operation: (B, Maybe<A>) -> B): B =
  fold(
    { initial },
    { operation(initial, it) }
  )

public inline fun <reified A : Any, B : Any> Maybe<A>.foldLeftMaybe(
  initial: Maybe<B>,
  operation: (Maybe<B>, A) -> Maybe<B>
): Maybe<B> =
  fold(
    { initial },
    { operation(initial, it) }
  )

@JvmName("maybeFoldLeftMaybe")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.foldLeftMaybe(
  initial: Maybe<B>,
  operation: (Maybe<B>, Maybe<A>) -> Maybe<B>
): Maybe<B> =
  fold(
    { initial },
    { operation(initial, it) }
  )

public inline fun <reified A : Any, reified B : Any> Maybe<A>.padZip(other: Maybe<B>): Maybe<Pair<A?, B?>> =
  padZip(other, ::Pair)

public inline fun <reified A : Any, reified B : Any, C : Any> Maybe<A>.padZip(
  other: Maybe<B>,
  f: (A?, B?) -> C
): Maybe<C> =
  foldMaybe({
    other.foldMaybe({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.foldMaybe({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

@JvmName("maybeNormalPadZip")
public inline fun <A : Any, reified B : Any, C : Any> Maybe<Maybe<A>>.padZip(
  other: Maybe<B>,
  f: (Maybe<A>?, B?) -> C
): Maybe<C> =
  foldMaybe({
    other.foldMaybe({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.foldMaybe({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

@JvmName("normalMaybePadZip")
public inline fun <reified A : Any, B : Any, C : Any> Maybe<A>.padZip(
  other: Maybe<Maybe<B>>,
  f: (A?, Maybe<B>?) -> C
): Maybe<C> =
  foldMaybe({
    other.foldMaybe({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.foldMaybe({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

@JvmName("maybeMaybePadZip")
public inline fun <A : Any, B : Any, C : Any> Maybe<Maybe<A>>.padZip(
  other: Maybe<Maybe<B>>,
  f: (Maybe<A>?, Maybe<B>?) -> C
): Maybe<C> =
  foldMaybe({
    other.foldMaybe({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.foldMaybe({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

@JvmName("maybeMaybePadZip2")
public inline fun <A : Any, B : Any, C : Any> Maybe<Maybe<A>>.padZip2(
  other: Maybe<Maybe<B>>,
  f: (Maybe<A>?, Maybe<B>?) -> C
): Maybe<C> =
  fold({
    other.fold({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.fold({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

public inline fun <reified A : Any, reified B : Any, C : Any> Maybe<A>.padZipMaybe(
  other: Maybe<B>,
  f: (A?, B?) -> Maybe<C>
): Maybe<Maybe<C>> =
  foldMaybe({
    other.foldMaybe({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.foldMaybe({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

@JvmName("maybeNormalPadZipMaybe")
public inline fun <A : Any, reified B : Any, C : Any> Maybe<Maybe<A>>.padZipMaybe(
  other: Maybe<B>,
  f: (Maybe<A>?, B?) -> Maybe<C>
): Maybe<Maybe<C>> =
  foldMaybe({
    other.foldMaybe({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.foldMaybe({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

@JvmName("normalMaybePadZipMaybe")
public inline fun <reified A : Any, B : Any, C : Any> Maybe<A>.padZipMaybe(
  other: Maybe<Maybe<B>>,
  f: (A?, Maybe<B>?) -> Maybe<C>
): Maybe<Maybe<C>> =
  foldMaybe({
    other.foldMaybe({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.foldMaybe({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

@JvmName("maybeMaybePadZipMaybe")
public inline fun <A : Any, B : Any, C : Any> Maybe<Maybe<A>>.padZipMaybe(
  other: Maybe<Maybe<B>>,
  f: (Maybe<A>?, Maybe<B>?) -> Maybe<C>
): Maybe<Maybe<C>> =
  foldMaybe({
    other.foldMaybe({
      Nothing
    }, { b ->
      Just(f(null, b))
    })
  }, { a ->
    other.foldMaybe({
      Just(f(a, null))
    }, { b ->
      Just(f(a, b))
    })
  })

public inline fun <reified A : Any, B> Maybe<A>.reduceOrNull(initial: (A) -> B, operation: (acc: B, A) -> B): B? =
  fold(
    { null },
    { operation(initial(it), it) }
  )

@JvmName("maybeReduceOrNull")
public inline fun <A : Any, B> Maybe<Maybe<A>>.reduceOrNull(
  initial: (Maybe<A>) -> B,
  operation: (acc: B, Maybe<A>) -> B
): B? =
  fold(
    { null },
    { operation(initial(it), it) }
  )

public inline fun <reified A : Any, B : Any> Maybe<A>.reduceOrNullMaybe(
  initial: (A) -> Maybe<B>,
  operation: (acc: Maybe<B>, A) -> Maybe<B>
): Maybe<B>? =
  fold(
    { null },
    { operation(initial(it), it) }
  )

@JvmName("maybeReduceOrNullMaybe")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.reduceOrNullMaybe(
  initial: (Maybe<A>) -> Maybe<B>,
  operation: (acc: Maybe<B>, Maybe<A>) -> Maybe<B>
): Maybe<B>? =
  fold(
    { null },
    { operation(initial(it), it) }
  )

public inline fun <reified A : Any, B> Maybe<A>.reduceRightEvalOrNull(
  initial: (A) -> B,
  operation: (A, acc: Eval<B>) -> Eval<B>
): Eval<B?> =
  fold(
    { Eval.now(null) },
    { operation(it, Eval.now(initial(it))) }
  )

@JvmName("maybeReduceRightEvalOrNull")
public inline fun <A : Any, B> Maybe<Maybe<A>>.reduceRightEvalOrNull(
  initial: (Maybe<A>) -> B,
  operation: (Maybe<A>, acc: Eval<B>) -> Eval<B>
): Eval<B?> =
  fold(
    { Eval.now(null) },
    { operation(it, Eval.now(initial(it))) }
  )

public inline fun <reified A : Any> Maybe<A>.replicate(n: Int): Maybe<List<A>> =
  if (n <= 0) Just(emptyList()) else map { a -> List(n) { a } }

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <reified A : Any, B : Any> Maybe<A>.traverse(fa: (A) -> Iterable<B>): List<Maybe<B>> =
  fold({ emptyList() }, { a -> fa(a).map { Just(it) } })

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("maybeTraverse")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.traverse(fa: (Maybe<A>) -> Iterable<B>): List<Maybe<B>> =
  fold({ emptyList() }, { a -> fa(a).map { Just(it) } })

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <reified A : Any, AA, B : Any> Maybe<A>.traverse(fa: (A) -> Either<AA, B>): Either<AA, Maybe<B>> =
  fold(
    { Right(Nothing) },
    { value -> fa(value).map { Just(it) } }
  )

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("maybeTraverse")
public inline fun <A : Any, AA, B : Any> Maybe<Maybe<A>>.traverse(fa: (Maybe<A>) -> Either<AA, B>): Either<AA, Maybe<B>> =
  fold(
    { Right(Nothing) },
    { value -> fa(value).map { Just(it) } }
  )

@Deprecated("traverseEither is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
public inline fun <reified A : Any, AA, B : Any> Maybe<A>.traverseEither(fa: (A) -> Either<AA, B>): Either<AA, Maybe<B>> =
  traverse(fa)

@JvmName("maybeTraverseEither")
@Deprecated("traverseEither is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
public inline fun <A : Any, AA, B : Any> Maybe<Maybe<A>>.traverseEither(fa: (Maybe<A>) -> Either<AA, B>): Either<AA, Maybe<B>> =
  traverse(fa)

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <reified A : Any, AA, B : Any> Maybe<A>.traverse(fa: (A) -> Validated<AA, B>): Validated<AA, Maybe<B>> =
  fold(
    { Valid(Nothing) },
    { value -> fa(value).map { Just(it) } }
  )

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("maybeTraverse")
public inline fun <A : Any, AA, B : Any> Maybe<Maybe<A>>.traverse(fa: (Maybe<A>) -> Validated<AA, B>): Validated<AA, Maybe<B>> =
  fold(
    { Valid(Nothing) },
    { value -> fa(value).map { Just(it) } }
  )

@Deprecated("traverseValidated is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
public inline fun <reified A : Any, AA, B : Any> Maybe<A>.traverseValidated(fa: (A) -> Validated<AA, B>): Validated<AA, Maybe<B>> =
  traverse(fa)

@JvmName("maybeTraverseValidated")
@Deprecated("traverseValidated is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
public inline fun <A : Any, AA, B : Any> Maybe<Maybe<A>>.traverseValidated(fa: (Maybe<A>) -> Validated<AA, B>): Validated<AA, Maybe<B>> =
  traverse(fa)

public inline fun <reified A : Any, L> Maybe<A>.toEither(ifEmpty: () -> L): Either<L, A> =
  fold({ ifEmpty().left() }, { it.right() })

public inline fun <reified A : Any> Maybe<A>.toList(): List<A> = fold(::emptyList) { listOf(it) }

public fun Maybe<*>.void(): Maybe<Unit> = justUnit

public inline fun <reified A : Any, L> Maybe<A>.pairLeft(left: L): Maybe<Pair<L, A>> = this.map { left to it }

public inline fun <reified A : Any, R> Maybe<A>.pairRight(right: R): Maybe<Pair<A, R>> = this.map { it to right }

public infix fun <A : Any, X : Any> Maybe<A>.and(value: Maybe<X>): Maybe<X> = if (isEmpty()) {
  Nothing
} else {
  value
}

@PublishedApi
internal data class Nil private constructor(val nesting: Int) {
  companion object {
    private val cache = Array(22) { Nil(it) }
    operator fun invoke(nesting: Int) = cache.getOrElse(nesting, ::Nil)
  }

  override fun toString(): String = buildString("Maybe.Just()".length + "Maybe.Nothing".length) {
    repeat(nesting) { this.append("Maybe.Just(") }
    append("Maybe.Nothing")
    repeat(nesting) { this.append(")") }
  }
}

@OptIn(MaybeInternals::class)
public inline fun <T : Any> Just(value: T): Maybe<T> =
  if (value is Maybe<*>) {
    Just(value) as Maybe<T> // Should rarely ever happen, but just in case this ensures that there *never* is a boxed Maybe inside a Maybe
  } else
    Maybe.construct(if (value is Nil) Nil(value.nesting + 1) else value)

@OptIn(MaybeInternals::class)
public inline fun <T : Any> Just(value: Maybe<T>): Maybe<Maybe<T>> = (value as Maybe<*>).underlying.let {
  // .value will remove 1 level of nesting, but we're inside 2 levels here, so we use underlying instead to get the raw value, and then we add 1 level of nesting
  Maybe.construct(if (it is Nil) Nil(it.nesting + 1) else it)
}

@PublishedApi
internal val justUnit: Maybe<Unit> = Just(Unit)
@PublishedApi
internal val boxedJustUnit: Any = justUnit

/**
 * Returns the maybe's value if the maybe is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
public inline fun <reified T : Any> Maybe<T>.getOrElse(default: () -> T): T = fold({ default() }, ::identity)

/**
 * Returns the maybe's value if the maybe is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
@JvmName("maybeGetOrElse")
public inline fun <T : Any> Maybe<Maybe<T>>.getOrElse(default: () -> Maybe<T>): Maybe<T> =
  foldMaybe({ default() }, ::identity)

/**
 * Returns this maybe's if the maybe is nonempty, otherwise
 * returns another maybe provided lazily by `default`.
 *
 * @param alternative the default maybe if this is empty.
 */
public inline fun <A : Any> Maybe<A>.orElse(alternative: () -> Maybe<A>): Maybe<A> =
  if (isEmpty()) alternative() else this

public infix fun <T : Any> Maybe<T>.or(value: Maybe<T>): Maybe<T> = if (isEmpty()) {
  value
} else {
  this
}

public fun <T : Any> T?.toMaybe(): Maybe<T> = this?.let { Just(it) } ?: Nothing
public fun <T : Any> Maybe<T>?.toMaybe(): Maybe<Maybe<T>> = this?.let { Just(it) } ?: Nothing

public inline fun <A : Any> Boolean.maybe2(f: () -> A): Maybe<A> =
  if (this) {
    Just(f())
  } else {
    Nothing
  }

public fun <A : Any> A.just(): Maybe<A> = Just(this)
public fun <A : Any> Maybe<A>.just(): Maybe<Maybe<A>> = Just(this)

public fun <A : Any> nothing(): Maybe<A> = Nothing

public inline fun <reified A : Any> Monoid.Companion.maybe(MA: Semigroup<A>): Monoid<Maybe<A>> =
  if (isTypeMaybe<A>()) MaybeMonoidNested(MA as Semigroup<Maybe<A>>) as Monoid<Maybe<A>> else MaybeMonoid(MA)

@JvmName("maybeMaybe")
public inline fun <A : Any> Monoid.Companion.maybe(MA: Semigroup<Maybe<A>>): Monoid<Maybe<Maybe<A>>> =
  MaybeMonoidNested(MA)

@PublishedApi
internal class MaybeMonoid<A : Any>(
  private val MA: Semigroup<A>
) : Monoid<Maybe<A>> {

  override fun Maybe<A>.combine(b: Maybe<A>): Maybe<A> =
    combine(MA as Semigroup<Any>, b) as Maybe<A>

  override fun Maybe<A>.maybeCombine(b: Maybe<A>?): Maybe<A> =
    b?.let { combine(MA as Semigroup<Any>, it) as Maybe<A> } ?: this

  override fun empty(): Maybe<A> = Nothing
}

@PublishedApi
internal class MaybeMonoidNested<A : Any>(
  private val MA: Semigroup<Maybe<A>>
) : Monoid<Maybe<Maybe<A>>> {

  override fun Maybe<Maybe<A>>.combine(b: Maybe<Maybe<A>>): Maybe<Maybe<A>> =
    combine(MA, b)

  override fun Maybe<Maybe<A>>.maybeCombine(b: Maybe<Maybe<A>>?): Maybe<Maybe<A>> =
    b?.let { combine(MA, it) } ?: this

  override fun empty(): Maybe<Maybe<A>> = Nothing
}

@Deprecated("use fold instead", ReplaceWith("fold(Monoid.maybe(MA))", "arrow.core.fold", "arrow.typeclasses.Monoid"))
public inline fun <reified A : Any> Iterable<Maybe<A>>.combineAll(MA: Monoid<A>): Maybe<A> =
  fold(Monoid.maybe(MA))

@Deprecated("use getOrElse instead", ReplaceWith("getOrElse { MA.empty() }"))
public inline fun <reified A : Any> Maybe<A>.combineAll(MA: Monoid<A>): A =
  getOrElse { MA.empty() }

@JvmName("maybeCombineAll")
@Deprecated("use getOrElse instead", ReplaceWith("getOrElse { MA.empty() }"))
public fun <A : Any> Maybe<Maybe<A>>.combineAll(MA: Monoid<Maybe<A>>): Maybe<A> =
  getOrElse { MA.empty() }

public inline fun <reified A : Any> Maybe<A>.ensure(error: () -> Unit, predicate: (A) -> Boolean): Maybe<A> =
  fold(
    { this },
    {
      if (predicate(it))
        this
      else {
        error()
        Nothing
      }
    }
  )

@JvmName("maybeEnsure")
public inline fun <A : Any> Maybe<Maybe<A>>.ensure(
  error: () -> Unit,
  predicate: (Maybe<A>) -> Boolean
): Maybe<Maybe<A>> =
  fold(
    { this },
    {
      if (predicate(it))
        this
      else {
        error()
        Nothing
      }
    }
  )

/**
 * Returns a Maybe containing all elements that are instances of specified type parameter [B].
 */
public inline fun <reified B : Any> Maybe<*>.filterIsInstance(): Maybe<B> =
  flatMap {
    when (it) {
      is B -> Just(it)
      else -> Nothing
    }
  }

@JvmName("maybeFilterIsInstance")
public inline fun <reified B : Any> Maybe<Maybe<*>>.filterIsInstance(): Maybe<B> =
  flatMap {
    when (it) {
      is B -> Just(it) as Maybe<B>
      else -> Nothing
    }
  }

public inline fun <A : Any> Maybe<A>.handleError(f: (Unit) -> A): Maybe<A> =
  handleErrorWith { Just(f(Unit)) }

@JvmName("maybeHandleError")
public inline fun <A : Any> Maybe<Maybe<A>>.handleError(f: (Unit) -> Maybe<A>): Maybe<Maybe<A>> =
  handleErrorWith { Just(f(Unit)) }

public inline fun <A : Any> Maybe<A>.handleErrorWith(f: (Unit) -> Maybe<A>): Maybe<A> =
  if (isEmpty()) f(Unit) else this

public fun <A : Any> Maybe<Maybe<A>>.flatten(): Maybe<A> =
  flatMap(::identity)

public inline fun <reified A : Any, B : Any> Maybe<A>.redeem(fe: (Unit) -> B, fb: (A) -> B): Maybe<B> =
  map(fb).handleError(fe)

@JvmName("maybeRedeem")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.redeem(fe: (Unit) -> B, fb: (Maybe<A>) -> B): Maybe<B> =
  map(fb).handleError(fe)

public inline fun <reified A : Any, B : Any> Maybe<A>.redeemMaybe(
  fe: (Unit) -> Maybe<B>,
  fb: (A) -> Maybe<B>
): Maybe<Maybe<B>> =
  mapMaybe(fb).handleError(fe)

@JvmName("maybeRedeemMaybe")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.redeemMaybe(
  fe: (Unit) -> Maybe<B>,
  fb: (Maybe<A>) -> Maybe<B>
): Maybe<Maybe<B>> =
  mapMaybe(fb).handleError(fe)

public inline fun <reified A : Any, B : Any> Maybe<A>.redeemWith(
  fe: (Unit) -> Maybe<B>,
  fb: (A) -> Maybe<B>
): Maybe<B> =
  flatMap(fb).handleErrorWith(fe)

@JvmName("maybeRedeemWith")
public inline fun <A : Any, B : Any> Maybe<Maybe<A>>.redeemWith(
  fe: (Unit) -> Maybe<B>,
  fb: (Maybe<A>) -> Maybe<B>
): Maybe<B> =
  flatMap(fb).handleErrorWith(fe)

public inline fun <reified A : Any> Maybe<A>.replicate(n: Int, MA: Monoid<A>): Maybe<A> = MA.run {
  if (n <= 0) Just(empty())
  else map { a ->
    var result = empty()
    repeat(n) {
      result += a
    }
    result
  }
}

@JvmName("maybeReplicate")
public fun <A : Any> Maybe<Maybe<A>>.replicate(n: Int, MA: Monoid<Maybe<A>>): Maybe<Maybe<A>> = MA.run {
  if (n <= 0) Just(empty())
  else map { a ->
    var result = empty()
    repeat(n) {
      result += a
    }
    result
  }
}


public fun <A : Any> Maybe<Either<Unit, A>>.rethrow(): Maybe<A> =
  flatMap { it.fold({ Nothing }, { a -> Just(a) }) }

public inline fun <reified A : Any> Maybe<A>.salign(SA: Semigroup<A>, b: Maybe<A>): Maybe<A> =
  align(b) {
    it.fold(::identity, ::identity) { a, b ->
      SA.run { a.combine(b) }
    }
  }

@JvmName("maybeSalign")
public fun <A : Any> Maybe<Maybe<A>>.salign(SA: Semigroup<Maybe<A>>, b: Maybe<Maybe<A>>): Maybe<Maybe<A>> =
  align(b) {
    it.fold(::identity, ::identity) { a, b ->
      SA.run { a.combine(b) }
    }
  }

/**
 * Separate the inner [Either] value into the [Either.Left] and [Either.Right].
 *
 * @receiver Maybe of Either
 * @return a tuple containing Maybe of [Either.Left] and another Maybe of its [Either.Right] value.
 */
public fun <A : Any, B : Any> Maybe<Either<A, B>>.separateEither(): Pair<Maybe<A>, Maybe<B>> {
  val asep = flatMap { gab -> gab.fold({ Just(it) }, { Nothing }) }
  val bsep = flatMap { gab -> gab.fold({ Nothing }, { Just(it) }) }
  return asep to bsep
}

/**
 * Separate the inner [Validated] value into the [Validated.Invalid] and [Validated.Valid].
 *
 * @receiver Maybe of Either
 * @return a tuple containing Maybe of [Validated.Invalid] and another Maybe of its [Validated.Valid] value.
 */
public fun <A : Any, B : Any> Maybe<Validated<A, B>>.separateValidated(): Pair<Maybe<A>, Maybe<B>> {
  val asep = flatMap { gab -> gab.fold({ Just(it) }, { Nothing }) }
  val bsep = flatMap { gab -> gab.fold({ Nothing }, { Just(it) }) }
  return asep to bsep
}

public fun <A : Any> Maybe<Iterable<A>>.sequence(): List<Maybe<A>> =
  traverse(::identity)

@Deprecated(
  "sequenceEither is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B : Any> Maybe<Either<A, B>>.sequenceEither(): Either<A, Maybe<B>> =
  sequence()

public fun <A, B : Any> Maybe<Either<A, B>>.sequence(): Either<A, Maybe<B>> =
  traverse(::identity)

@Deprecated(
  "sequenceValidated is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B : Any> Maybe<Validated<A, B>>.sequenceValidated(): Validated<A, Maybe<B>> =
  sequence()

public fun <A, B : Any> Maybe<Validated<A, B>>.sequence(): Validated<A, Maybe<B>> =
  traverse(::identity)

public fun <A : Any, B : Any> Maybe<Ior<A, B>>.unalign(): Pair<Maybe<A>, Maybe<B>> =
  unalign(::identity)

public inline fun <A : Any, B : Any, reified C : Any> Maybe<C>.unalign(f: (C) -> Ior<A, B>): Pair<Maybe<A>, Maybe<B>> =
  this.map(f).fold(
    { Nothing to Nothing },
    {
      when (it) {
        is Ior.Left -> Just(it.value) to Nothing
        is Ior.Right -> Nothing to Just(it.value)
        is Ior.Both -> Just(it.leftValue) to Just(it.rightValue)
      }
    }
  )

@JvmName("maybeUnalign")
public inline fun <A : Any, B : Any, C : Any> Maybe<Maybe<C>>.unalign(f: (Maybe<C>) -> Ior<A, B>): Pair<Maybe<A>, Maybe<B>> =
  this.map(f).unalign()

public fun <A : Any> Maybe<Iterable<A>>.unite(MA: Monoid<A>): Maybe<A> =
  map { iterable ->
    iterable.fold(MA)
  }

public fun <A, B : Any> Maybe<Either<A, B>>.uniteEither(): Maybe<B> =
  flatMap { either ->
    either.fold({ Nothing }, { b -> Just(b) })
  }

public fun <A, B : Any> Maybe<Validated<A, B>>.uniteValidated(): Maybe<B> =
  flatMap { validated ->
    validated.fold({ Nothing }, { b -> Just(b) })
  }

public fun <A : Any, B : Any> Maybe<Pair<A, B>>.unzip(): Pair<Maybe<A>, Maybe<B>> =
  unzip(::identity)

public inline fun <A : Any, B : Any, reified C : Any> Maybe<C>.unzip(f: (C) -> Pair<A, B>): Pair<Maybe<A>, Maybe<B>> =
  fold(
    { Nothing to Nothing },
    { f(it).let { pair -> Just(pair.first) to Just(pair.second) } }
  )

@JvmName("maybeUnzip")
public inline fun <A : Any, B : Any, C : Any> Maybe<Maybe<C>>.unzip(f: (Maybe<C>) -> Pair<A, B>): Pair<Maybe<A>, Maybe<B>> =
  fold(
    { Nothing to Nothing },
    { f(it).let { pair -> Just(pair.first) to Just(pair.second) } }
  )

/**
 *  Given [A] is a sub type of [B], re-type this value from Maybe<A> to Maybe<B>
 *
 *  Maybe<A> -> Maybe<B>
 *
 *  ```kotlin
 *  import arrow.core.Maybe
 *  import arrow.core.just
 *  import arrow.core.widen
 *
 *  fun main(args: Array<String>) {
 *   val result: Maybe<CharSequence> =
 *   //sampleStart
 *   "Hello".just().map({ "$it World" }).widen()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 * <!--- KNIT example-maybe-23.kt -->
 */
public fun <B : Any, A : B> Maybe<A>.widen(): Maybe<B> =
  this

public fun <K, V> Maybe<Pair<K, V>>.toMap(): Map<K, V> = this.toList().toMap()

public inline fun <reified A : Any> Maybe<A>.combine(SGA: Semigroup<A>, b: Maybe<A>): Maybe<A> =
  fold(
    { b },
    { first ->
      b.fold(
        { this },
        { second ->
          Just(SGA.run { first.combine(second) })
        }
      )
    }
  )

@JvmName("maybeCombine")
public fun <A : Any> Maybe<Maybe<A>>.combine(SGA: Semigroup<Maybe<A>>, b: Maybe<Maybe<A>>): Maybe<Maybe<A>> =
  fold(
    { b },
    { first ->
      b.fold(
        { this },
        { second ->
          Just(SGA.run { first.combine(second) })
        }
      )
    }
  )

public inline operator fun <reified A : Comparable<A>> Maybe<A>.compareTo(other: Maybe<A>): Int = fold(
  { other.fold({ 0 }, { -1 }) },
  { a1 ->
    other.fold({ 1 }, { a2 -> a1.compareTo(a2) })
  }
)
