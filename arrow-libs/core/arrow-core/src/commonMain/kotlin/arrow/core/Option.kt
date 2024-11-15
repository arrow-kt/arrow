@file:OptIn(ExperimentalContracts::class)
package arrow.core

import arrow.core.raise.EagerEffect
import arrow.core.raise.Effect
import arrow.core.raise.SingletonRaise
import arrow.core.raise.option
import arrow.core.raise.recover
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * <!--- TEST_NAME OptionKnitTest -->
 *
 * If you have worked with Java at all in the past, it is very likely that you have come across a `NullPointerException` at some time (other languages will throw similarly named errors in such a case). Usually this happens because some method returns `null` when you weren't expecting it and, thus, isn't dealing with that possibility in your client code. A value of `null` is often abused to represent an absent optional value.
 * Kotlin tries to solve the problem by getting rid of `null` values altogether, and providing its own special syntax [Null-safety machinery based on `?`](https://kotlinlang.org/docs/reference/null-safety.html).
 *
 * Arrow models the absence of values through the `Option` datatype similar to how Scala, Haskell, and other FP languages handle optional values.
 *
 * `Option<A>` is a container for an optional value of type `A`. If the value of type `A` is present, the `Option<A>` is an instance of `Some<A>`, containing the present value of type `A`. If the value is absent, the `Option<A>` is the object `None`.
 *
 * ```kotlin
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
 * <!--- KNIT example-option-01.kt -->
 *
 * Let's write a function that may or may not give us a string, thus returning `Option<String>`:
 *
 * ```kotlin
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * //sampleStart
 * fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 *  if (flag) Some("Found value") else None
 * //sampleEnd
 * ```
 * <!--- KNIT example-option-02.kt -->
 *
 * Using `getOrElse`, we can provide a default value `"No value"` when the optional argument `None` does not exist:
 *
 * ```kotlin
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
 * <!--- KNIT example-option-03.kt -->
 *
 * ```kotlin
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
 * <!--- KNIT example-option-04.kt -->
 *
 * Checking whether option has value:
 *
 * ```kotlin
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
 * <!--- KNIT example-option-05.kt -->
 * Creating a `Option<T>` of a `T?`. Useful for working with values that can be nullable:
 *
 * ```kotlin
 * import arrow.core.Option
 *
 * //sampleStart
 * val myString: String? = "Nullable string"
 * val option: Option<String> = Option.fromNullable(myString)
 * //sampleEnd
 * fun main () {
 *  println("option = $option")
 * }
 * ```
 * <!--- KNIT example-option-06.kt -->
 *
 * Option can also be used with when statements:
 *
 * ```kotlin
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * //sampleStart
 * val someValue: Option<Double> = Some(20.0)
 * val value = when(someValue) {
 *  is Some -> someValue.value
 *  is None -> 0.0
 * }
 * //sampleEnd
 * fun main () {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-option-07.kt -->
 *
 * ```kotlin
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 *
 * //sampleStart
 * val noValue: Option<Double> = None
 * val value = when(noValue) {
 *  is Some -> noValue.value
 *  is None -> 0.0
 * }
 * //sampleEnd
 * fun main () {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-option-08.kt -->
 *
 * An alternative for pattern matching is folding. This is possible because an option could be looked at as a collection or foldable structure with either one or zero elements.
 *
 * One of these operations is `map`. This operation allows us to map the inner value to a different type while preserving the option:
 *
 * ```kotlin
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
 * <!--- KNIT example-option-09.kt -->
 * Another operation is `fold`. This operation will extract the value from the option, or provide a default if the value is `None`
 *
 * ```kotlin
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
 * <!--- KNIT example-option-10.kt -->
 *
 * ```kotlin
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
 * <!--- KNIT example-option-11.kt -->
 *
 * Arrow also adds syntax to all datatypes so you can easily lift them into the context of `Option` where needed.
 *
 * ```kotlin
 * import arrow.core.some
 * import arrow.core.none
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
 * <!--- KNIT example-option-12.kt -->
 *
 * ```kotlin
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
 * <!--- KNIT example-option-13.kt -->
 *
 * You can easily convert between `A?` and `Option<A>` by using the `toOption()` extension or `Option.fromNullable` constructor.
 *
 * ```kotlin
 * import arrow.core.firstOrNone
 * import arrow.core.toOption
 * import arrow.core.Option
 *
 * //sampleStart
 * val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")
 *
 * val empty = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toOption()
 * val filled = Option.fromNullable(foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() })
 *
 * //sampleEnd
 * fun main() {
 *  println("empty = $empty")
 *  println("filled = $filled")
 * }
 * ```
 * <!--- KNIT example-option-14.kt -->
 *
 * ### Transforming the inner contents
 *
 * ```kotlin
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
 * <!--- KNIT example-option-15.kt -->
 *
 * ## Credits
 *
 * Contents partially adapted from [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options)
 * Originally based on the Scala Koans.
 */
public sealed class Option<out A> {

  public companion object {

    @JvmStatic
    public fun <A> fromNullable(a: A?): Option<A> = if (a != null) Some(a) else None

    @JvmStatic
    public operator fun <A> invoke(a: A): Option<A> = Some(a)

    @JvmStatic
    @JvmName("tryCatchOrNone")
    /**
     * Ignores exceptions and returns None if one is thrown
     */
    public inline fun <A> catch(f: SingletonRaise<None>.() -> A): Option<A> {
      contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
      return catch({ None }, f)
    }

    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <A> catch(recover: (Throwable) -> Option<A>, f: SingletonRaise<None>.() -> A): Option<A> {
      contract {
        callsInPlace(f, InvocationKind.AT_MOST_ONCE)
        callsInPlace(recover, InvocationKind.AT_MOST_ONCE)
      }
      return recover({ f(SingletonRaise(this)).some() }, { None }, recover)
    }
  }

  /**
   * The given function is applied as a fire and forget effect
   * if this is a `None`.
   * When applied the result is ignored and the original
   * None value is returned
   *
   * Example:
   * ```kotlin
   * import arrow.core.Some
   * import arrow.core.none
   *
   * fun main() {
   *   Some(12).onNone { println("flower") } // Result: Some(12)
   *   none<Int>().onNone { println("flower") }  // Result: prints "flower" and returns: None
   * }
   * ```
   * <!--- KNIT example-option-16.kt -->
   */
  public inline fun onNone(action: () -> Unit): Option<A>  {
    contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isNone()) action() }
  }

  /**
   * The given function is applied as a fire and forget effect
   * if this is a `some`.
   * When applied the result is ignored and the original
   * Some value is returned
   *
   * Example:
   * ```kotlin
   * import arrow.core.Some
   * import arrow.core.none
   *
   * fun main() {
   *   Some(12).onSome { println("flower") } // Result: prints "flower" and returns: Some(12)
   *   none<Int>().onSome { println("flower") }  // Result: None
   * }
   * ```
   * <!--- KNIT example-option-17.kt -->
   */
  public inline fun onSome(action: (A) -> Unit): Option<A>  {
    contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isSome()) action(it.value) }
  }

  /**
   * Returns true if the option is [None], false otherwise.
   * @note Used only for performance instead of fold.
   */
  public fun isNone(): Boolean {
    contract {
      returns(false) implies (this@Option is Some<A>)
      returns(true) implies (this@Option is None)
    }
    return this@Option is None
  }

  /**
   * Returns true if the option is [Some], false otherwise.
   * @note Used only for performance instead of fold.
   */
  public fun isSome(): Boolean {
    contract {
      returns(true) implies (this@Option is Some<A>)
      returns(false) implies (this@Option is None)
    }
    return this@Option is Some<A>
  }

  /**
   * Returns true if this option is nonempty '''and''' the predicate
   * $p returns true when applied to this $option's value.
   * Otherwise, returns false.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Some
   * import arrow.core.None
   * import arrow.core.Option
   *
   * fun main() {
   *   Some(12).isSome { it > 10 } // Result: true
   *   Some(7).isSome { it > 10 }  // Result: false
   *
   *   val none: Option<Int> = None
   *   none.isSome { it > 10 }      // Result: false
   * }
   * ```
   * <!--- KNIT example-option-18.kt -->
   *
   * @param predicate the predicate to test
   */
  public inline fun isSome(predicate: (A) -> Boolean): Boolean {
    contract {
      callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
      returns(true) implies (this@Option is Some<A>)
    }
    return this@Option is Some<A> && predicate(value)
  }

  /**
   * Returns the encapsulated value [A] if this instance represents [Some] or `null` if it is [None].
   *
   * ```kotlin
   * import arrow.core.None
   * import arrow.core.Some
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *   Some(12).getOrNull() shouldBe 12
   *   None.getOrNull() shouldBe null
   * }
   * ```
   * <!--- KNIT example-option-19.kt -->
   */
  public fun getOrNull(): A? {
    contract {
      returns(null) implies (this@Option is None)
      returnsNotNull() implies (this@Option is Some<A>)
    }
    return getOrElse { null }
  }

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
  public inline fun <B> map(f: (A) -> B): Option<B> {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return flatMap { a -> Some(f(a)) }
  }

  public inline fun <R> fold(ifEmpty: () -> R, ifSome: (A) -> R): R {
    contract {
      callsInPlace(ifEmpty, InvocationKind.AT_MOST_ONCE)
      callsInPlace(ifSome, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
      is None -> ifEmpty()
      is Some<A> -> ifSome(value)
    }
  }

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
  public inline fun <B> flatMap(f: (A) -> Option<B>): Option<B> {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
      is None -> this
      is Some -> f(value)
    }
  }

  /**
   * Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns true. Otherwise, return $none.
   *
   *  @param predicate the predicate used for testing.
   */
  public inline fun filter(predicate: (A) -> Boolean): Option<A> {
    contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
    return flatMap { a -> if (predicate(a)) Some(a) else None }
  }

  /**
   * Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns false. Otherwise, return $none.
   *
   * @param predicate the predicate used for testing.
   */
  public inline fun filterNot(predicate: (A) -> Boolean): Option<A> {
    contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
    return flatMap { a -> if (!predicate(a)) Some(a) else None }
  }

  public inline fun <L> toEither(ifEmpty: () -> L): Either<L, A> {
    contract { callsInPlace(ifEmpty, InvocationKind.AT_MOST_ONCE) }
    return fold({ ifEmpty().left() }, { it.right() })
  }

  public fun toList(): List<A> = fold(::emptyList) { listOf(it) }

  override fun toString(): String = fold(
    { "Option.None" },
    { "Option.Some($it)" }
  )
}

public object None : Option<Nothing>() {
  override fun toString(): String = "Option.None"
}

public data class Some<out T>(val value: T) : Option<T>() {
  override fun toString(): String = "Option.Some($value)"

  public companion object
}

/**
 * Returns the option's value if the option is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
public inline fun <T> Option<T>.getOrElse(default: () -> T): T {
  contract { callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return when (this) {
    is Some -> value
    else -> default()
  }
}

public fun <T> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

/** Run the [Effect] by returning [Option] of [A], or [None] if raised with [None]. */
public suspend fun <A> Effect<None, A>.toOption(): Option<A> = option { invoke() }

/** Run the [EagerEffect] by returning [Option] of [A], or [None] if raised with [None]. */
public fun <A> EagerEffect<None, A>.toOption(): Option<A> = option { invoke() }

public fun <A> A.some(): Option<A> = Some(this)

public fun <A> none(): Option<A> = None

/**
 * Returns an Option containing all elements that are instances of specified type parameter [B].
 */
public inline fun <reified B> Option<*>.filterIsInstance(): Option<B> =
  flatMap {
    when (it) {
      is B -> Some(it)
      else -> None
    }
  }

public fun <A> Option<Option<A>>.flatten(): Option<A> =
  flatMap(::identity)

public fun <K, V> Option<Pair<K, V>>.toMap(): Map<K, V> = this.toList().toMap()

public inline fun <A> Option<A>.combine(other: Option<A>, combine: (A, A) -> A): Option<A> {
  contract { callsInPlace(combine, InvocationKind.AT_MOST_ONCE) }
  return when (this) {
    is Some -> when (other) {
      is Some -> Some(combine(value, other.value))
      None -> this
    }

    None -> other
  }
}

public operator fun <A : Comparable<A>> Option<A>.compareTo(other: Option<A>): Int = fold(
  { other.fold({ 0 }, { -1 }) },
  { a1 ->
    other.fold({ 1 }, { a2 -> a1.compareTo(a2) })
  }
)

/**
 * Recover from any [None] if encountered.
 *
 * The recover DSL allows you to recover from any [None] value by:
 *  - Computing a fallback value [A]
 *  - Shifting a _new error_ of [None] into the [Option].
 *
 * ```kotlin
 * import arrow.core.Option
 * import arrow.core.none
 * import arrow.core.Some
 * import arrow.core.recover
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val error: Option<Int> = none()
 *   val fallback: Option<Int> = error.recover { 5 }
 *   fallback shouldBe Some(5)
 * }
 * ```
 * <!--- KNIT example-option-20.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * When shifting a new error [None] into the [Option]:
 *
 * ```kotlin
 * import arrow.core.Option
 * import arrow.core.none
 * import arrow.core.Some
 * import arrow.core.recover
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val error: Option<Int> = none()
 *   fun fallback(): Option<Int> = Some(5)
 *   fun failure(): Option<Int> = none()
 *
 *   error.recover { fallback().bind() } shouldBe Some(5)
 *   error.recover { failure().bind() } shouldBe none()
 * }
 * ```
 * <!--- KNIT example-option-21.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline fun <A> Option<A>.recover(recover: SingletonRaise<None>.() -> A): Option<A> {
  contract { callsInPlace(recover, InvocationKind.AT_MOST_ONCE) }
  return when (this@recover) {
    is None -> option { recover() }
    is Some -> this@recover
  }
}
