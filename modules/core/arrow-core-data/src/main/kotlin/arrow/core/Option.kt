package arrow.core

import arrow.Kind
import arrow.higherkind

/**
 *
 * ank_macro_hierarchy(arrow.core.Option)
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
 * [Functor](/docs/arrow/typeclasses/functor/)
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
 * [Applicative](/docs/arrow/typeclasses/applicative/)
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
 * [Monad](/docs/arrow/typeclasses/monad/)
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
 * ### Supported type classes
 *
 * ```kotlin:ank:replace
 * import arrow.reflect.DataType
 * import arrow.reflect.tcMarkdownList
 * import arrow.core.Option
 *
 * DataType(Option::class).tcMarkdownList()
 * ```
 *
 * ## Credits
 *
 * Contents partially adapted from [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options)
 * Originally based on the Scala Koans.
 */

@higherkind
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
    fun <A> just(a: A): Option<A> = Some(a)

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> OptionOf<Either<A, B>>): Option<B> {
      val option = f(a).fix()
      return when (option) {
        is Some -> {
          when (option.t) {
            is Either.Left -> tailRecM(option.t.a, f)
            is Either.Right -> Some(option.t.b)
          }
        }
        is None -> None
      }
    }

    fun <A> fromNullable(a: A?): Option<A> = if (a != null) Some(a) else None

    operator fun <A> invoke(a: A): Option<A> = Some(a)

    fun <A> empty(): Option<A> = None
  }

  /**
   * Returns true if the option is [None], false otherwise.
   * @note Used only for performance instead of fold.
   */
  abstract fun isEmpty(): Boolean

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
  fun <B> map(f: (A) -> B): Option<B> =
    flatMap { a -> Some(f(a)) }

  fun <B, R> map2(fb: Kind<ForOption, B>, f: (Tuple2<A, B>) -> R): Option<R> =
    flatMap { a: A -> fb.fix().map { b -> f(a toT b) } }

  fun <B> filterMap(f: (A) -> Option<B>): Option<B> =
    flatMap { a -> f(a).fold({ empty<B>() }, { just(it) }) }

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
  fun <B> mapNotNull(f: (A) -> B?): Option<B> =
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
  fun <B> flatMap(f: (A) -> OptionOf<B>): Option<B> =
    when (this) {
      is None -> this
      is Some -> f(t).fix()
    }

  fun <B> ap(ff: OptionOf<(A) -> B>): Option<B> =
    ff.fix().flatMap { this.fix().map(it) }

  /**
   * Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns true. Otherwise, return $none.
   *
   *  @param predicate the predicate used for testing.
   */
  fun filter(predicate: Predicate<A>): Option<A> =
    flatMap { a -> if (predicate(a)) Some(a) else None }

  /**
   * Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns false. Otherwise, return $none.
   *
   * @param predicate the predicate used for testing.
   */
  fun filterNot(predicate: Predicate<A>): Option<A> =
    flatMap { a -> if (!predicate(a)) Some(a) else None }

  /**
   * Returns true if this option is nonempty '''and''' the predicate
   * $p returns true when applied to this $option's value.
   * Otherwise, returns false.
   *
   * @param predicate the predicate to test
   */
  fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { a -> predicate(a) })

  /**
   * Returns true if this option is empty '''or''' the predicate
   * $p returns true when applied to this $option's value.
   *
   * @param p the predicate to test
   */
  fun forall(p: Predicate<A>): Boolean = fold({ true }, p)

  fun <B> foldLeft(initial: B, operation: (B, A) -> B): B =
    fix().let { option ->
      when (option) {
        is Some -> operation(initial, option.t)
        is None -> initial
      }
    }

  fun <B> foldRight(initial: Eval<B>, operation: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().let { option ->
      when (option) {
        is Some -> operation(option.t, initial)
        is None -> initial
      }
    }

  fun <L> toEither(ifEmpty: () -> L): Either<L, A> =
    fold({ ifEmpty().left() }, { it.right() })

  fun toList(): List<A> = fold(::emptyList) { listOf(it) }

  infix fun <X> and(value: Option<X>): Option<X> = if (isEmpty()) {
    None
  } else {
    value
  }
}

object None : Option<Nothing>() {
  override fun isEmpty() = true

  override fun toString(): String = "None"
}

data class Some<out T>(val t: T) : Option<T>() {
  override fun isEmpty() = false

  override fun toString(): String = "Some($t)"
}

/**
 * Returns the option's value if the option is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
fun <T> Option<T>.getOrElse(default: () -> T): T = fold({ default() }, ::identity)

/**
 * Returns this option's if the option is nonempty, otherwise
 * returns another option provided lazily by `default`.
 *
 * @param alternative the default option if this is empty.
 */
fun <A> OptionOf<A>.orElse(alternative: () -> Option<A>): Option<A> = if (fix().isEmpty()) alternative() else fix()

infix fun <T> OptionOf<T>.or(value: Option<T>): Option<T> = if (fix().isEmpty()) {
  value
} else {
  fix()
}

fun <T> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

fun <A> Boolean.maybe(f: () -> A): Option<A> =
  if (this) {
    Some(f())
  } else {
    None
  }

fun <A> A.some(): Option<A> = Some(this)

fun <A> none(): Option<A> = None

fun <T> Iterable<T>.firstOrNone(): Option<T> = this.firstOrNull().toOption()

fun <T> Iterable<T>.firstOrNone(predicate: (T) -> Boolean): Option<T> = this.firstOrNull(predicate).toOption()

fun <T> Iterable<T>.singleOrNone(): Option<T> = this.singleOrNull().toOption()

fun <T> Iterable<T>.singleOrNone(predicate: (T) -> Boolean): Option<T> = this.singleOrNull(predicate).toOption()

fun <T> Iterable<T>.lastOrNone(): Option<T> = this.lastOrNull().toOption()

fun <T> Iterable<T>.lastOrNone(predicate: (T) -> Boolean): Option<T> = this.lastOrNull(predicate).toOption()

fun <T> Iterable<T>.elementAtOrNone(index: Int): Option<T> = this.elementAtOrNull(index).toOption()

fun <A, B> Option<Either<A, B>>.select(f: OptionOf<(A) -> B>): Option<B> =
  flatMap { it.fold({ l -> Option.just(l).ap(f) }, { r -> Option.just(r) }) }
