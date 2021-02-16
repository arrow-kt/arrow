package arrow.core

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Either.Right
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
class ForOption private constructor() { companion object }
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias OptionOf<A> = arrow.Kind<ForOption, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A> OptionOf<A>.fix(): Option<A> = this as Option<A>

/**
 *
 *
 * If you have worked with Java at all in the past, it is very likely that you have come across a `NullPointerException` at some time (other languages will throw similarly named errors in such a case). Usually this happens because some method returns `null` when you weren't expecting it and, thus, isn't dealing with that possibility in your client code. A value of `null` is often abused to represent an absent optional value.
 * Kotlin tries to solve the problem by getting rid of `null` values altogether, and providing its own special syntax [Null-safety machinery based on `?`](https://kotlinlang.org/docs/reference/null-safety.html).
 *
 * Arrow models the absence of values through the `Option` datatype similar to how Scala, Haskell, and other FP languages handle optional values.
 *
 * `Option<A>` is a container for an optional value of type `A`. If the value of type `A` is present, the `Option<A>` is an instance of `Some<A>`, containing the present value of type `A`. If the value is absent, the `Option<A>` is the object `None`.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.none
 *
 * //sampleStart
 * val someValue: Option<String> = Some("I am wrapped in something")
 * val emptyValue: Option<String> = none()
 * //sampleEnd
 * fun main() {
 *  println("value = $someValue")
 *  println("emptyValue = $emptyValue")
 * }
 * ```
 *
 * Let's write a function that may or may not give us a string, thus returning `Option<String>`:
 *
 * ```kotlin:ank
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * //sampleStart
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
 * //sampleEnd
 * ```
 *
 * Using `getOrElse`, we can provide a default value `"No value"` when the optional argument `None` does not exist:
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.getOrElse
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
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
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.getOrElse
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
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
 *
 * Checking whether option has value:
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
 *
 *  //sampleStart
 * val valueSome = maybeItWillReturnSomething(true) is None
 * val valueNone = maybeItWillReturnSomething(false) is None
 * //sampleEnd
 * fun main() {
 *  println("valueSome = $valueSome")
 *  println("valueNone = $valueNone")
 * }
 * ```
 * Creating a `Option<T>` of a `T?`. Useful for working with values that can be nullable:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 *
 *
 * //sampleStart
 * val myString: String? = "Nullable string"
 * val option: Option<String> = Option.fromNullable(myString)
 * //sampleEnd
 * fun main () {
 *  println("option = $option")
 * }
 * ```
 *
 * Option can also be used with when statements:
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * //sampleStart
 * val someValue: Option<Double> = Some(20.0)
 * val value = when(someValue) {
 *  is Some -> someValue.t
 *  is None -> 0.0
 * }
 * //sampleEnd
 * fun main () {
 *  println("value = $value")
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * //sampleStart
 * val noValue: Option<Double> = None
 * val value = when(noValue) {
 *  is Some -> noValue.t
 *  is None -> 0.0
 * }
 * //sampleEnd
 * fun main () {
 *  println("value = $value")
 * }
 * ```
 *
 * An alternative for pattern matching is performing Functor/Foldable style operations. This is possible because an option could be looked at as a collection or foldable structure with either one or zero elements.
 *
 * One of these operations is `map`. This operation allows us to map the inner value to a different type while preserving the option:
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * //sampleStart
 * val number: Option<Int> = Some(3)
 * val noNumber: Option<Int> = None
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
 * Another operation is `fold`. This operation will extract the value from the option, or provide a default if the value is `None`
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * val fold =
 * //sampleStart
 *  Some(3).fold({ 1 }, { it * 3 })
 * //sampleEnd
 * fun main () {
 *  println(fold)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.none
 *
 * val fold =
 * //sampleStart
 *  none<Int>().fold({ 1 }, { it * 3 })
 * //sampleEnd
 * fun main () {
 *  println(fold)
 * }
 * ```
 *
 * Arrow also adds syntax to all datatypes so you can easily lift them into the context of `Option` where needed.
 *
 * ```kotlin:ank:playground
 * import arrow.core.some
 *
 * //sampleStart
 *  val some = 1.some()
 *  val none = none<String>()
 * //sampleEnd
 * fun main () {
 *  println("some = $some")
 *  println("none = $none")
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.toOption
 *
 * //sampleStart
 * val nullString: String? = null
 * val valueFromNull = nullString.toOption()
 *
 * val helloString: String? = "Hello"
 * val valueFromStr = helloString.toOption()
 * //sampleEnd
 * fun main () {
 *  println("valueFromNull = $valueFromNull")
 *  println("valueFromStr = $valueFromStr")
 * }
 * ```
 *
 * Some Iterable extensions are available, so you can maintain a friendly API syntax while avoiding null handling (`firstOrNull()`)
 *
 * ```kotlin:ank:playground
 * import arrow.core.firstOrNone
 *
 * //sampleStart
 * val myList: List<Int> = listOf(1,2,3,4)
 *
 * val first4 = myList.firstOrNone { it == 4 }
 * val first5 = myList.firstOrNone { it == 5 }
 * //sampleEnd
 * fun main () {
 *  println("first4 = $first4")
 *  println("first5 = $first5")
 * }
 * ```
 *
 * Sample usage
 *
 * ```kotlin:ank:playground
 * import arrow.core.firstOrNone
 * import arrow.core.toOption
 *
 * //sampleStart
 * val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")
 *
 * val ugly = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toOption()
 * val pretty = foxMap.entries.firstOrNone { it.key == 5 }.map { it.value.toCharArray() }
 * //sampleEnd
 * fun main() {
 *  println("ugly = $ugly")
 *  println("pretty = $pretty")
 * }
 * ```
 *
 * Arrow contains `Option` instances for many useful typeclasses that allow you to use and transform optional values
 *
 * [`Functor`](../../../../arrow/typeclasses/functor/)
 *
 * Transforming the inner contents
 *
 * ```kotlin:ank:playground
 * import arrow.core.Some
 *
 * fun main() {
 * val value =
 *  //sampleStart
 *    Some(1).map { it + 1 }
 *  //sampleEnd
 *  println(value)
 * }
 * ```
 *
 * [`Applicative`](../../../../arrow/typeclasses/applicative/)
 *
 * Computing over independent values
 *
 * ```kotlin:ank:playground
 * import arrow.core.Some
 * import arrow.core.extensions.option.apply.tupled
 *
 *  val value =
 * //sampleStart
 *  tupled(Some(1), Some("Hello"), Some(20.0))
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * [`Monad`](../../../../arrow/typeclasses/monad/)
 *
 * Computing over dependent values ignoring absence
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.fx
 * import arrow.core.Some
 * import arrow.core.Option
 *
 * val value =
 * //sampleStart
 *  Option.fx {
 *  val (a) = Some(1)
 *  val (b) = Some(1 + a)
 *  val (c) = Some(1 + b)
 *  a + b + c
 * }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.fx
 * import arrow.core.Some
 * import arrow.core.none
 * import arrow.core.Option
 *
 * val value =
 * //sampleStart
 *  Option.fx {
 *    val (x) = none<Int>()
 *    val (y) = Some(1 + x)
 *    val (z) = Some(1 + y)
 *    x + y + z
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## Credits
 *
 * Contents partially adapted from [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options)
 * Originally based on the Scala Koans.
 */
@Deprecated(
  "Option will be deleted soon as it promotes the wrong message of using a slower and memory unfriendly " +
    "abstraction when the lang provides a better one. Alternatively, if you can't support nulls, consider aliasing Either<Unit, A> " +
    "as described here https://github.com/arrow-kt/arrow-core/issues/114#issuecomment-641211639",
  ReplaceWith("A?")
)
sealed class Option<out A> : OptionOf<A> {

  companion object {

    /**
     * Lifts a pure [A] value to [Option]
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.core.Option
     * fun main(args: Array<String>) {
     * //sampleStart
     * val result: Option<Int> = Option.just(1)
     * //sampleEnd
     * println(result)
     * }
     * ```
     *
     */
    @Deprecated(
      "just is deprecated, and will be removed in 0.13.0. Please use Some instead.",
      ReplaceWith(
        "Some(a)",
        "arrow.core.Some"
      ),
      DeprecationLevel.WARNING
    )
    fun <A> just(a: A): Option<A> = Some(a)

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> OptionOf<Either<A, B>>): Option<B> =
      when (val option = f(a).fix()) {
        is Some -> {
          when (option.t) {
            is Either.Left -> tailRecM(option.t.a, f)
            is Either.Right -> Some(option.t.b)
          }
        }
        is None -> None
      }

    fun <A> fromNullable(a: A?): Option<A> = if (a != null) Some(a) else None

    operator fun <A> invoke(a: A): Option<A> = Some(a)

    inline fun <A> catch(recover: (Throwable) -> Unit, f: () -> A): Option<A> =
      try {
        Some(f())
      } catch (t: Throwable) {
        recover(t.nonFatalOrThrow())
        None
      }

    @Deprecated(
      "empty is deprecated, and will be removed in 0.13.0. Please use None instead.",
      ReplaceWith(
        "None",
        "arrow.core.None"
      ),
      DeprecationLevel.WARNING
    )
    fun <A> empty(): Option<A> = None

    @PublishedApi
    internal val unit: Option<Unit> = Some(Unit)

    inline fun <A, B, C> mapN(
      a: Option<A>,
      b: Option<B>,
      map: (A, B) -> C
    ): Option<C> =
      mapN(a, b, unit, unit, unit, unit, unit, unit, unit, unit) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

    inline fun <A, B, C, D> mapN(
      a: Option<A>,
      b: Option<B>,
      c: Option<C>,
      map: (A, B, C) -> D
    ): Option<D> =
      mapN(a, b, c, unit, unit, unit, unit, unit, unit, unit) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

    inline fun <A, B, C, D, E> mapN(
      a: Option<A>,
      b: Option<B>,
      c: Option<C>,
      d: Option<D>,
      map: (A, B, C, D) -> E
    ): Option<E> =
      mapN(a, b, c, d, unit, unit, unit, unit, unit, unit) { a, b, c, d, _, _, _, _, _, _ -> map(a, b, c, d) }

    inline fun <A, B, C, D, E, F> mapN(
      a: Option<A>,
      b: Option<B>,
      c: Option<C>,
      d: Option<D>,
      e: Option<E>,
      map: (A, B, C, D, E) -> F
    ): Option<F> =
      mapN(a, b, c, d, e, unit, unit, unit, unit, unit) { a, b, c, d, e, f, _, _, _, _ -> map(a, b, c, d, e) }

    inline fun <A, B, C, D, E, F, G> mapN(
      a: Option<A>,
      b: Option<B>,
      c: Option<C>,
      d: Option<D>,
      e: Option<E>,
      f: Option<F>,
      map: (A, B, C, D, E, F) -> G
    ): Option<G> =
      mapN(a, b, c, d, e, f, unit, unit, unit, unit) { a, b, c, d, e, f, _, _, _, _ -> map(a, b, c, d, e, f) }

    inline fun <A, B, C, D, E, F, G, H, I> mapN(
      a: Option<A>,
      b: Option<B>,
      c: Option<C>,
      d: Option<D>,
      e: Option<E>,
      f: Option<F>,
      g: Option<G>,
      map: (A, B, C, D, E, F, G) -> H
    ): Option<H> =
      mapN(a, b, c, d, e, f, g, unit, unit, unit) { a, b, c, d, e, f, g, _, _, _ -> map(a, b, c, d, e, f, g) }

    inline fun <A, B, C, D, E, F, G, H, I> mapN(
      a: Option<A>,
      b: Option<B>,
      c: Option<C>,
      d: Option<D>,
      e: Option<E>,
      f: Option<F>,
      g: Option<G>,
      h: Option<H>,
      map: (A, B, C, D, E, F, G, H) -> I
    ): Option<I> =
      mapN(a, b, c, d, e, f, g, h, unit, unit) { a, b, c, d, e, f, g, h, _, _ -> map(a, b, c, d, e, f, g, h) }

    inline fun <A, B, C, D, E, F, G, H, I, J> mapN(
      a: Option<A>,
      b: Option<B>,
      c: Option<C>,
      d: Option<D>,
      e: Option<E>,
      f: Option<F>,
      g: Option<G>,
      h: Option<H>,
      i: Option<I>,
      map: (A, B, C, D, E, F, G, H, I) -> J
    ): Option<J> =
      mapN(a, b, c, d, e, f, g, h, i, unit) { a, b, c, d, e, f, g, h, i, _ -> map(a, b, c, d, e, f, g, h, i) }

    inline fun <A, B, C, D, E, F, G, H, I, J, K> mapN(
      a: Option<A>,
      b: Option<B>,
      c: Option<C>,
      d: Option<D>,
      e: Option<E>,
      f: Option<F>,
      g: Option<G>,
      h: Option<H>,
      i: Option<I>,
      j: Option<J>,
      map: (A, B, C, D, E, F, G, H, I, J) -> K
    ): Option<K> =
      if (a is Some && b is Some && c is Some && d is Some && e is Some && f is Some && g is Some && h is Some && i is Some && j is Some)
        Some(map(a.t, b.t, c.t, d.t, e.t, f.t, g.t, h.t, i.t, j.t))
      else
        None
  }

  /**
   * Returns true if the option is [None], false otherwise.
   * @note Used only for performance instead of fold.
   */
  abstract fun isEmpty(): Boolean

  fun isNotEmpty(): Boolean = !isEmpty()

  /**
   * alias for [isDefined]
   */
  fun nonEmpty(): Boolean = isDefined()

  /**
   * Returns true if the option is an instance of [Some], false otherwise.
   * @note Used only for performance instead of fold.
   */
  fun isDefined(): Boolean = !isEmpty()

  fun orNull(): A? = fold({ null }, ::identity)

  /**
   * Returns a [Some<$B>] containing the result of applying $f to this $option's
   * value if this $option is nonempty. Otherwise return $none.
   *
   * @note This is similar to `flatMap` except here,
   * $f does not need to wrap its result in an $option.
   *
   * @param f the function to apply
   * @see flatMap
   */
  inline fun <B> map(f: (A) -> B): Option<B> =
    flatMap { a -> Some(f(a)) }

  /**
   *  Replaces [A] inside [Option] with [B] resulting in an Option<B>
   *
   *  Option<A> -> Option<B>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.some
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World".some().mapConst("...")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <B> mapConst(b: B): Option<B> =
    map { b }

  @Deprecated(
    "map2 will be renamed to zip to be consistent with Kotlin Std's naming, please use zip instead of map2",
    ReplaceWith(
      "zip(fb) { b, c -> f(Tuple2(b, c)) }",
      "arrow.core.Tuple2",
      "arrow.core.zip"
    )
  )
  fun <B, R> map2(fb: Kind<ForOption, B>, f: (Tuple2<A, B>) -> R): Option<R> =
    flatMap { a: A -> fb.fix().map { b -> f(a toT b) } }

  @Deprecated(
    "filterMap will be renamed to mapNotNull to be consistent with Kotlin Std's naming, please use mapNotNull instead of filterMap",
    ReplaceWith(
      "this.mapNotNull(f.andThen { it.orNull() })",
      "arrow.core.andThen"
    ),
    level = DeprecationLevel.WARNING
  )
  fun <B> filterMap(f: (A) -> Option<B>): Option<B> =
    flatMap(f)

  inline fun <R> fold(ifEmpty: () -> R, ifSome: (A) -> R): R = when (this) {
    is None -> ifEmpty()
    is Some<A> -> ifSome(t)
  }

  /**
   * Returns $none if the result of applying $f to this $option's value is null.
   * Otherwise returns the result.
   *
   * @note This is similar to `.flatMap { Option.fromNullable(null)) }`
   * and primarily for convenience.
   *
   * @param f the function to apply.
   * */
  inline fun <B> mapNotNull(f: (A) -> B?): Option<B> =
    flatMap { a -> fromNullable(f(a)) }

  /**
   * Returns the result of applying $f to this $option's value if
   * this $option is nonempty.
   * Returns $none if this $option is empty.
   * Slightly different from `map` in that $f is expected to
   * return an $option (which could be $none).
   *
   * @param f the function to apply
   * @see map
   */
  inline fun <B> flatMap(f: (A) -> OptionOf<B>): Option<B> =
    when (this) {
      is None -> this
      is Some -> f(t).fix()
    }

  fun <B> align(b: Option<B>): Option<Ior<A, B>> =
    Ior.fromOptions(this, b)

  inline fun <B, C> align(b: Option<B>, f: (Ior<A, B>) -> C): Option<C> =
    Ior.fromOptions(this, b).map(f)

  /**
   * Returns true if this option is empty '''or''' the predicate
   * $predicate returns true when applied to this $option's value.
   *
   * @param predicate the predicate to test
   */
  inline fun all(predicate: (A) -> Boolean): Boolean =
    fold({ true }, predicate)

  fun <B> ap(ff: OptionOf<(A) -> B>): Option<B> =
    ff.fix().flatMap { this.fix().map(it) }

  fun <B> apEval(ff: Eval<Option<(A) -> B>>): Eval<Option<B>> =
    ff.map { ap(it) }

  inline fun <B> crosswalk(f: (A) -> Option<B>): Option<Option<B>> =
    when (this) {
      is None -> empty<B>().map { empty() }
      is Some -> f(t).map { Some(it) }
    }

  inline fun <K, V> crosswalkMap(f: (A) -> Map<K, V>): Map<K, Option<V>> =
    when (this) {
      is None -> emptyMap()
      is Some -> f(t).mapValues { Some(it.value) }
    }

  inline fun <B> crosswalkNull(f: (A) -> B?): Option<B>? =
    when (this) {
      is None -> null
      is Some -> f(t)?.let { Some(it) }
    }

  /**
   * Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns true. Otherwise, return $none.
   *
   *  @param predicate the predicate used for testing.
   */
  inline fun filter(predicate: (A) -> Boolean): Option<A> =
    flatMap { a -> if (predicate(a)) Some(a) else None }

  inline fun <AA> filterEither(predicate: (A) -> Either<AA, Boolean>): Either<AA, Option<A>> =
    traverseFilterEither { a -> predicate(a).map { if (it) Some(a) else None } }

  inline fun filterIterable(predicate: (A) -> Iterable<Boolean>): Iterable<Option<A>> =
    traverseFilter { a -> predicate(a).map { if (it) Some(a) else None } }

  inline fun <AA> filterValidated(predicate: (A) -> Validated<AA, Boolean>): Validated<AA, Option<A>> =
    traverseFilterValidated { a -> predicate(a).map { if (it) Some(a) else None } }

  /**
   * Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns false. Otherwise, return $none.
   *
   * @param predicate the predicate used for testing.
   */
  inline fun filterNot(predicate: (A) -> Boolean): Option<A> =
    flatMap { a -> if (!predicate(a)) Some(a) else None }

  /**
   * Returns true if this option is nonempty '''and''' the predicate
   * $p returns true when applied to this $option's value.
   * Otherwise, returns false.
   *
   * Example:
   * ```
   * Some(12).exists { it > 10 } // Result: true
   * Some(7).exists { it > 10 }  // Result: false
   *
   * val none: Option<Int> = None
   * none.exists { it > 10 }      // Result: false
   * ```
   *
   * @param predicate the predicate to test
   */
  inline fun exists(predicate: (A) -> Boolean): Boolean = fold({ false }, predicate)

  /**
   * Returns true if this option is empty '''or''' the predicate
   * $p returns true when applied to this $option's value.
   *
   * @param p the predicate to test
   */
  @Deprecated(
    "forall will be renamed to all to be consistent with Kotlin Std's naming, please use all instead of forall",
    ReplaceWith(
      "this.all(p)"
    ),
    DeprecationLevel.WARNING
  )
  inline fun forall(p: (A) -> Boolean): Boolean = fold({ true }, p)

  /**
   * Returns the $option's value if this option is nonempty '''and''' the predicate
   * $p returns true when applied to this $option's value.
   * Otherwise, returns null.
   *
   * Example:
   * ```
   * Some(12).exists { it > 10 } // Result: 12
   * Some(7).exists { it > 10 }  // Result: null
   *
   * val none: Option<Int> = None
   * none.exists { it > 10 }      // Result: null
   * ```
   */
  inline fun findOrNull(predicate: (A) -> Boolean): A? =
    when (this) {
      is Some -> if (predicate(t)) t else null
      is None -> null
    }

  inline fun <B> flatTraverse(f: (A) -> Iterable<Option<B>>): List<Option<B>> =
    fold(
      { emptyList() },
      { f(it).toList() }
    )

  inline fun <E, B> flatTraverseEither(f: (A) -> Either<E, Option<B>>): Either<E, Option<B>> =
    fold(
      { Right(empty()) },
      { f(it) }
    )

  inline fun <E, B> flatTraverseValidated(f: (A) -> Validated<E, Option<B>>): Validated<E, Option<B>> =
    fold(
      { Valid(empty()) },
      { f(it) }
    )

  inline fun <B> foldMap(MB: Monoid<B>, f: (A) -> B): B = MB.run {
    foldLeft(empty()) { b, a -> b.combine(f(a)) }
  }

  inline fun <B> foldLeft(initial: B, operation: (B, A) -> B): B =
    when (this) {
      is Some -> operation(initial, t)
      is None -> initial
    }

  inline fun <B> foldRight(initial: Eval<B>, crossinline operation: (A, Eval<B>) -> Eval<B>): Eval<B> =
    when (this) {
      is Some -> Eval.defer { operation(t, initial) }
      is None -> initial
    }

  /**
   *  Applies [f] to an [A] inside [Option] and returns the [Option] structure with a pair of the [A] value and the
   *  computed [B] value as result of applying [f]
   *
   *  Option<A> -> Option<Pair<A, B>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.some
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".some().fproduct({ "$it World" })
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  inline fun <B> fproduct(f: (A) -> B): Option<Pair<A, B>> =
    map { a -> Pair(a, f(a)) }

  fun <B> padZip(other: Option<B>): Option<Pair<A?, B?>> =
    align(other) { ior ->
      ior.fold(
        { it to null },
        { null to it },
        { a, b -> a to b }
      )
    }

  inline fun <B, C> padZip(other: Option<B>, f: (A?, B?) -> C): Option<C> =
    align(other) { ior ->
      ior.fold(
        { f(it, null) },
        { f(null, it) },
        { a, b -> f(a, b) }
      )
    }

  inline fun <B> reduceOrNull(initial: (A) -> B, operation: (acc: B, A) -> B): B? =
    when (this) {
      is None -> null
      is Some -> operation(initial(t), t)
    }

  inline fun <B> reduceRightEvalOrNull(
    initial: (A) -> B,
    operation: (A, acc: Eval<B>) -> Eval<B>
  ): Eval<B?> =
    when (this) {
      is None -> Eval.now(null)
      is Some -> operation(t, Eval.now(initial(t)))
    }

  fun replicate(n: Int): Option<List<A>> =
    if (n <= 0) Some(emptyList()) else map { a -> List(n) { a } }

  inline fun <B> traverse(fa: (A) -> Iterable<B>): List<Option<B>> =
    fold({ emptyList() }, { a -> fa(a).map { Some(it) } })

  inline fun <B> traverse_(fa: (A) -> Iterable<B>): List<Unit> =
    fold({ emptyList() }, { fa(it).void() })

  inline fun <AA, B> traverseEither(fa: (A) -> Either<AA, B>): Either<AA, Option<B>> =
    when (this) {
      is Some -> fa(t).map { Some(it) }
      is None -> Right(this)
    }

  inline fun <AA, B> traverseEither_(fa: (A) -> Either<AA, B>): Either<AA, Unit> =
    fold({ Right(Unit) }, { fa(it).void() })

  inline fun <AA, B> traverseValidated(fa: (A) -> Validated<AA, B>): Validated<AA, Option<B>> =
    when (this) {
      is Some -> fa(t).map { Some(it) }
      is None -> Valid(this)
    }

  inline fun <AA, B> traverseValidated_(fa: (A) -> Validated<AA, B>): Validated<AA, Unit> =
    fold({ Valid(Unit) }, { fa(it).void() })

  inline fun <B> traverseFilter(f: (A) -> Iterable<Option<B>>): List<Option<B>> =
    this.fold({ emptyList() }, { f(it).toList() })

  inline fun <AA, B> traverseFilterEither(f: (A) -> Either<AA, Option<B>>): Either<AA, Option<B>> =
    this.fold({ Right(empty()) }, f)

  inline fun <AA, B> traverseFilterValidated(f: (A) -> Validated<AA, Option<B>>): Validated<AA, Option<B>> =
    this.fold({ Valid(empty()) }, f)

  inline fun <L> toEither(ifEmpty: () -> L): Either<L, A> =
    fold({ ifEmpty().left() }, { it.right() })

  fun toList(): List<A> = fold(::emptyList) { listOf(it) }

  /**
   *  Pairs [B] with [A] returning an Option<Pair<B, A>>
   *
   *  Option<A> -> Option<Pair<B, A>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.some
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".some().tupleLeft("World")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <B> tupleLeft(b: B): Option<Pair<B, A>> =
    map { a -> Pair(b, a) }

  /**
   *  Pairs [A] with [B] returning an Option<Pair<A, B>>
   *
   *  Option<A> -> Option<Pair<A, B>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.some
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".some().tupleRight("World")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <B> tupleRight(b: B): Option<Pair<A, B>> =
    map { a -> Pair(a, b) }

  fun void(): Option<Unit> =
    mapConst(Unit)

  fun <B> zip(other: Option<B>): Option<Pair<A, B>> =
    mapN(this, other) { a, b -> a to b}

  inline fun <B, C> zip(other: Option<B>, f: (A, B) -> C): Option<C> =
    zip(other).map { a -> f(a.first, a.second)}

  inline fun <B, C> zipEval(other: Eval<Option<B>>, crossinline f: (A, B) -> C): Eval<Option<C>> =
    other.map {zip(it).map { a -> f(a.first, a.second) }}

  infix fun <X> and(value: Option<X>): Option<X> = if (isEmpty()) {
    None
  } else {
    value
  }

  @Deprecated(
    "Show typeclass is deprecated, and will be removed in 0.13.0. Please use the toString method instead.",
    ReplaceWith(
      "toString()"
    ),
    DeprecationLevel.WARNING
  )
  fun show(SA: Show<A>): String = fold(
    { "None" },
    { "Some(${SA.run { it.show() }})" }
  )

  override fun toString(): String = fold(
      { "Option.None" },
      { "Option.Some($it)" }
    )
}

object None : Option<Nothing>() {
  override fun isEmpty() = true

  override fun toString(): String = "Option.None"
}

data class Some<out T>(
  @Deprecated("Use value instead", ReplaceWith("value"))
  val t: T
) : Option<T>() {
  val value: T = t
  override fun isEmpty() = false

  override fun toString(): String = "Option.Some($t)"
}

/**
 * Returns the option's value if the option is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
inline fun <T> Option<T>.getOrElse(default: () -> T): T = fold({ default() }, ::identity)

/**
 * Returns this option's if the option is nonempty, otherwise
 * returns another option provided lazily by `default`.
 *
 * @param alternative the default option if this is empty.
 */
inline fun <A> OptionOf<A>.orElse(alternative: () -> Option<A>): Option<A> = if (fix().isEmpty()) alternative() else fix()

infix fun <T> OptionOf<T>.or(value: Option<T>): Option<T> = if (fix().isEmpty()) {
  value
} else {
  fix()
}

fun <T> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

inline fun <A> Boolean.maybe(f: () -> A): Option<A> =
  if (this) {
    Some(f())
  } else {
    None
  }

fun <A> A.some(): Option<A> = Some(this)

fun <A> none(): Option<A> = None

fun <A> Iterable<Option<A>>.combineAll(MA: Monoid<A>): Option<A> =
  fold(Option(MA.empty())) { acc, a ->
    acc.combine(MA, a)
  }

fun <T> Iterable<T>.firstOrNone(): Option<T> = this.firstOrNull().toOption()

inline fun <T> Iterable<T>.firstOrNone(predicate: (T) -> Boolean): Option<T> = this.firstOrNull(predicate).toOption()

fun <T> Iterable<T>.singleOrNone(): Option<T> = this.singleOrNull().toOption()

inline fun <T> Iterable<T>.singleOrNone(predicate: (T) -> Boolean): Option<T> = this.singleOrNull(predicate).toOption()

fun <T> Iterable<T>.lastOrNone(): Option<T> = this.lastOrNull().toOption()

fun <T> Iterable<T>.lastOrNone(predicate: (T) -> Boolean): Option<T> = this.lastOrNull(predicate).toOption()

fun <T> Iterable<T>.elementAtOrNone(index: Int): Option<T> = this.elementAtOrNull(index).toOption()

fun <A, B> Option<Either<A, B>>.select(f: OptionOf<(A) -> B>): Option<B> =
  branch(f.fix(), Some(::identity))

fun <A, B, C> Option<Either<A, B>>.branch(fa: Option<(A) -> C>, fb: Option<(B) -> C>): Option<C> =
  flatMap { it.fold(
    { a -> Some(a).ap(fa) },
    { b -> Some(b).ap(fb) }
  )}

private fun Option<Boolean>.selector(): Option<Either<Unit, Unit>> =
  map { bool -> if (bool) Either.right(Unit) else Either.left(Unit) }

fun <A> Option<Boolean>.whenS(x: Option<() -> Unit>): Option<Unit> =
  selector().select(x.map { f -> { _: Unit -> f() } })

fun <A> Option<Boolean>.ifS(fl: Option<A>, fr: Option<A>): Option<A> =
  selector().branch(fl.map { { _: Unit -> it } }, fr.map { { _: Unit -> it } })

fun Option<Boolean>.orS(f: Option<Boolean>): Option<Boolean> =
  ifS(Some(true), f)

fun Option<Boolean>.andS(f: Option<Boolean>): Option<Boolean> =
  ifS(f, Some(false))

fun <A> Option<A>.combineAll(MA: Monoid<A>): A = MA.run {
  foldLeft(empty()) { acc, a -> acc.combine(a) } }

inline fun <A> Option<A>.ensure(error: () -> Unit, predicate: (A) -> Boolean): Option<A> =
  when (this) {
    is Some ->
      if (predicate(t)) this
      else {
        error()
        None
      }
    is None -> this
  }

/**
 * Returns an Option containing all elements that are instances of specified type parameter R.
 */
inline fun <reified B> Option<*>.filterIsInstance(): Option<B> {
  val f: (Any?) -> B? = { it as? B }
  return this.mapNotNull(f)
}

inline fun <A> Option<A>.handleError(f: (Unit) -> A): Option<A> =
  handleErrorWith { Some(f(Unit)) }

inline fun <A> Option<A>.handleErrorWith(f: (Unit) -> Option<A>): Option<A> =
  if (isEmpty()) f(Unit) else this

inline fun <reified B> Option<*>.traverseFilterIsInstance(): List<Option<B>> =
  filterIterable { a -> listOf(a is B) }.map { it.map { a -> a as B } }

inline fun <E, reified B> Option<*>.traverseFilterIsInstanceEither(): Either<E, Option<B>> =
  filterEither { a -> Right(a is B) }.map { it.map { a -> a as B } }

inline fun <E, reified B> Option<*>.traverseFilterIsInstanceValidated(): Validated<E, Option<B>> =
  filterValidated { a -> Valid(a is B) }.map { it.map { a -> a as B } }

fun <A> Option<Option<A>>.flatten(): Option<A> =
  flatMap(::identity)

inline fun <A, B> Option<A>.mproduct(f: (A) -> Option<B>): Option<Pair<A, B>> =
  flatMap { a ->
    f(a).map { b -> a to b }
  }

inline fun <A> Option<Boolean>.ifM(ifTrue: () -> Option<A>, ifFalse: () -> Option<A>): Option<A> =
  flatMap { if (it) ifTrue() else ifFalse() }

fun <A, B> Option<Either<A, B>>.selectM(f: Option<(A) -> B>): Option<B> =
  flatMap { it.fold(
    { a -> Some(a).ap(f) },
    { b -> Some(b) }
  )}

inline fun <A, B> Option<A>.redeem(fe: (Unit) -> B, fb: (A) -> B): Option<B> =
  map(fb).handleError(fe)

inline fun <A, B> Option<A>.redeemWith(fe: (Unit) -> Option<B>, fb: (A) -> Option<B>): Option<B> =
  flatMap(fb).handleErrorWith(fe)

fun <A> Option<A>.replicate(n: Int, MA: Monoid<A>): Option<A> = MA.run {
  if (n <= 0) Some(empty())
  else map { a -> List(n) { a }.fold(empty()) { acc, v -> acc + v } }}

fun <A> Option<Either<Unit, A>>.rethrow(): Option<A> =
  flatMap { it.fold({ None }, { a -> Some(a) }) }

fun <A> Option<A>.salign(SA: Semigroup<A>, b: Option<A>): Option<A> =
  align(b) { it.fold(::identity, ::identity) { a, b ->
    SA.run { a.combine(b) }
  }}

/**
 * Separate the inner [Either] value into the [Either.Left] and [Either.Right].
 *
 * @receiver Option of Either
 * @return a tuple containing Option of [Either.Left] and another Option of its [Either.Right] value.
 */
fun <A, B> Option<Either<A, B>>.separateEither(): Pair<Option<A>, Option<B>> {
  val asep = flatMap { gab -> gab.fold({ Some(it) }, { None }) }
  val bsep = flatMap { gab -> gab.fold({ None }, { Some(it) }) }
  return asep to bsep
}

/**
 * Separate the inner [Validated] value into the [Validated.Invalid] and [Validated.Valid].
 *
 * @receiver Option of Either
 * @return a tuple containing Option of [Validated.Invalid] and another Option of its [Validated.Valid] value.
 */
fun <A, B> Option<Validated<A, B>>.separateValidated(): Pair<Option<A>, Option<B>> {
  val asep = flatMap { gab -> gab.fold({ Some(it) }, { None }) }
  val bsep = flatMap { gab -> gab.fold({ None }, { Some(it) }) }
  return asep to bsep
}

fun <A> Option<Iterable<A>>.sequence(): List<Option<A>> =
  traverse(::identity)

fun <A> Option<Iterable<A>>.sequence_(): List<Unit> =
  traverse_(::identity)

fun <A, B> Option<Either<A, B>>.sequenceEither(): Either<A, Option<B>> =
  traverseEither(::identity)

fun <A, B> Option<Either<A, B>>.sequenceEither_(): Either<A, Unit> =
  traverseEither_(::identity)

fun <A, B> Option<Validated<A, B>>.sequenceValidated(): Validated<A, Option<B>> =
  traverseValidated(::identity)

fun <A, B> Option<Validated<A, B>>.sequenceValidated_(): Validated<A, Unit> =
  traverseValidated_(::identity)

fun <A, B> Option<Ior<A, B>>.unalign(): Pair<Option<A>, Option<B>> =
  unalign(::identity)

inline fun <A, B, C> Option<C>.unalign(f: (C) -> Ior<A, B>): Pair<Option<A>, Option<B>> =
  when (val option = this.map(f)) {
    is None -> None to None
    is Some -> when (val v = option.t) {
      is Ior.Left -> Some(v.value) to None
      is Ior.Right -> None to Some(v.value)
      is Ior.Both -> Some(v.leftValue) to Some(v.rightValue)
    }
  }

fun <A> Option<Iterable<A>>.unite(MA: Monoid<A>): Option<A> =
  map { iterable ->
    iterable.fold(MA)
  }

fun <A, B> Option<Either<A, B>>.uniteEither(): Option<B> =
  flatMap { either ->
    either.fold({ None }, { b -> Some(b) })
  }

fun <A, B> Option<Validated<A, B>>.uniteValidated(): Option<B> =
  flatMap { validated ->
    validated.fold({ None }, { b -> Some(b) })
  }

fun <A, B> Option<Pair<A, B>>.unzip(): Pair<Option<A>, Option<B>> =
  unzip(::identity)

inline fun <A, B, C> Option<C>.unzip(f: (C) -> Pair<A, B>): Pair<Option<A>, Option<B>> =
  fold(
    { Option.empty<A>() to Option.empty() },
    { f(it).let { pair -> Some(pair.first) to Some(pair.second) }}
  )

/**
 *  Given [A] is a sub type of [B], re-type this value from Option<A> to Option<B>
 *
 *  Option<A> -> Option<B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.Option
 *  import arrow.core.some
 *  import arrow.core.widen
 *
 *  fun main(args: Array<String>) {
 *   val result: Option<CharSequence> =
 *   //sampleStart
 *   "Hello".some().map({ "$it World" }).widen()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
fun <B, A : B> Option<A>.widen(): Option<B> =
  this

fun <A> Option<A>.combine(SGA: Semigroup<A>, b: Option<A>): Option<A> =
  when (this) {
    is Some -> when (b) {
      is Some -> Some(SGA.run { t.combine(b.t) })
      None -> this
    }
    None -> b
  }

fun <A> Monoid.Companion.option(MA: Monoid<A>): Monoid<Option<A>> =
  OptionMonoid(MA)

fun <A> Semigroup.Companion.option(SGA: Semigroup<A>): Semigroup<Option<A>> =
  OptionSemigroup(SGA)

private class OptionMonoid<A>(
  private val MA: Monoid<A>
) : Monoid<Option<A>> {

  override fun Option<A>.combine(b: Option<A>): Option<A> =
    combine(MA, b)

  override fun Option<A>.maybeCombine(b: Option<A>?): Option<A> =
    b?.let { combine(MA, it) } ?: this

  override fun empty(): Option<A> = None
}

private class OptionSemigroup<A>(
  private val SGA: Semigroup<A>
) : Semigroup<Option<A>> {

  override fun Option<A>.combine(b: Option<A>): Option<A> =
    combine(SGA, b)

  override fun Option<A>.maybeCombine(b: Option<A>?): Option<A> =
    b?.let { combine(SGA, it) } ?: this
}

operator fun <A : Comparable<A>> Option<A>.compareTo(other: Option<A>): Int = fold(
  { other.fold({ 0 }, { -1 }) },
  { a1 -> other.fold({ 1 }, { a2 -> a1.compareTo(a2) })
  })
