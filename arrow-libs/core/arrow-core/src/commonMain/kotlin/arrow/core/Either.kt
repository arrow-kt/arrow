@file:OptIn(ExperimentalContracts::class)

package arrow.core

import arrow.core.Either.Companion.resolve
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Either.Right.Companion.unit
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidDeprecation
import arrow.typeclasses.Semigroup
import arrow.typeclasses.combine
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

public typealias EitherNel<E, A> = Either<NonEmptyList<E>, A>

/**
 * <!--- TEST_NAME EitherKnitTest -->
 *
 * In day-to-day programming, it is fairly common to find ourselves writing functions that can fail.
 * For instance, querying a service may result in a connection issue, or some unexpected JSON response.
 *
 * To communicate these errors, it has become common practice to throw exceptions; however,
 * exceptions are not tracked in any way, shape, or form by the compiler. To see what
 * kind of exceptions (if any) a function may throw, we have to dig through the source code.
 * Then, to handle these exceptions, we have to make sure we catch them at the call site. This
 * all becomes even more unwieldy when we try to compose exception-throwing procedures.
 *
 * ```kotlin
 * import arrow.core.andThen
 *
 * //sampleStart
 * val throwsSomeStuff: (Int) -> Double = {x -> x.toDouble()}
 * val throwsOtherThings: (Double) -> String = {x -> x.toString()}
 * val moreThrowing: (String) -> List<String> = {x -> listOf(x)}
 * val magic = throwsSomeStuff.andThen(throwsOtherThings).andThen(moreThrowing)
 * //sampleEnd
 * fun main() {
 *  println ("magic = $magic")
 * }
 * ```
 * <!--- KNIT example-either-01.kt -->
 *
 * Assume we happily throw exceptions in our code. Looking at the types of the functions above, any could throw a number of exceptions -- we do not know. When we compose, exceptions from any of the constituent
 * functions can be thrown. Moreover, they may throw the same kind of exception
 * (e.g., `IllegalArgumentException`) and, thus, it gets tricky tracking exactly where an exception came from.
 *
 * How then do we communicate an error? By making it explicit in the data type we return.
 *
 *
 * ```kotlin
 * import arrow.core.Either
 *
 * val left: Either<String, Int> =
 * //sampleStart
 *  Either.Left("Something went wrong")
 * //sampleEnd
 * fun main() {
 *  println(left)
 * }
 * ```
 * <!--- KNIT example-either-02.kt -->
 *
 * Because `Either` is right-biased, it is possible to define a Monad instance for it.
 *
 * Since we only ever want the computation to continue in the case of [Right] (as captured by the right-bias nature),
 * we fix the left type parameter and leave the right one free.
 *
 * So, the map and flatMap methods are right-biased:
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.flatMap
 *
 * //sampleStart
 * val right: Either<String, Int> = Either.Right(5)
 * val value = right.flatMap{ Either.Right(it + 1) }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-either-03.kt -->
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.flatMap
 *
 * //sampleStart
 * val left: Either<String, Int> = Either.Left("Something went wrong")
 * val value = left.flatMap{ Either.Right(it + 1) }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-either-04.kt -->
 *
 * ## Using Either instead of exceptions
 *
 * As a running example, we will have a series of functions that will:
 *
 * * Parse a string into an integer
 * * Calculate the reciprocal
 * * Convert the reciprocal into a string
 *
 * Using exception-throwing code, we could write something like this:
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.flatMap
 *
 * //sampleStart
 * fun parse(s: String): Int =
 *   if (s.matches(Regex("-?[0-9]+"))) s.toInt()
 *   else throw NumberFormatException("$s is not a valid integer.")
 *
 * fun reciprocal(i: Int): Double =
 *   if (i == 0) throw IllegalArgumentException("Cannot take reciprocal of 0.")
 *   else 1.0 / i
 *
 * fun stringify(d: Double): String = d.toString()
 * //sampleEnd
 * ```
 * <!--- KNIT example-either-05.kt -->
 *
 * Instead, let's make the fact that some of our functions can fail explicit in the return type.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.flatMap
 * import arrow.core.left
 * import arrow.core.right
 *
 * //sampleStart
 * // Either Style
 * fun parse(s: String): Either<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(NumberFormatException("$s is not a valid integer."))
 *
 * fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
 *   if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
 *   else Either.Right(1.0 / i)
 *
 * fun stringify(d: Double): String = d.toString()
 *
 * fun magic(s: String): Either<Exception, String> =
 *   parse(s).flatMap { reciprocal(it) }.map { stringify(it) }
 * //sampleEnd
 * ```
 * <!--- KNIT example-either-06.kt -->
 *
 * These calls to `parse` return a [Left] and [Right] value
 *
 * ```kotlin
 * import arrow.core.Either
 *
 * fun parse(s: String): Either<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(NumberFormatException("$s is not a valid integer."))
 *
 * //sampleStart
 * val notANumber = parse("Not a number")
 * val number2 = parse("2")
 * //sampleEnd
 * fun main() {
 *  println("notANumber = $notANumber")
 *  println("number2 = $number2")
 * }
 * ```
 * <!--- KNIT example-either-07.kt -->
 *
 * Now, using combinators like `flatMap` and `map`, we can compose our functions together.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.flatMap
 *
 * fun parse(s: String): Either<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(NumberFormatException("$s is not a valid integer."))
 *
 * fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
 *   if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
 *   else Either.Right(1.0 / i)
 *
 * fun stringify(d: Double): String = d.toString()
 *
 * fun magic(s: String): Either<Exception, String> =
 *   parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }
 *
 * //sampleStart
 * val magic0 = magic("0")
 * val magic1 = magic("1")
 * val magicNotANumber = magic("Not a number")
 * //sampleEnd
 * fun main() {
 *  println("magic0 = $magic0")
 *  println("magic1 = $magic1")
 *  println("magicNotANumber = $magicNotANumber")
 * }
 * ```
 * <!--- KNIT example-either-08.kt -->
 *
 * In the following exercise, we pattern-match on every case in which the `Either` returned by `magic` can be in.
 * Note the `when` clause in the [Left] - the compiler will complain if we leave that out because it knows that,
 * given the type `Either[Exception, String]`, there can be inhabitants of [Left] that are not
 * `NumberFormatException` or `IllegalArgumentException`. You should also notice that we are using
 * [SmartCast](https://kotlinlang.org/docs/reference/typecasts.html#smart-casts) for accessing [Left] and [Right]
 * values.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.flatMap
 *
 * fun parse(s: String): Either<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(NumberFormatException("$s is not a valid integer."))
 *
 * fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
 *   if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
 *   else Either.Right(1.0 / i)
 *
 * fun stringify(d: Double): String = d.toString()
 *
 * fun magic(s: String): Either<Exception, String> =
 *   parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }
 *
 * //sampleStart
 * val x = magic("2")
 * val value = when(x) {
 *   is Either.Left -> when (x.value) {
 *     is NumberFormatException -> "Not a number!"
 *     is IllegalArgumentException -> "Can't take reciprocal of 0!"
 *     else -> "Unknown error"
 *   }
 *   is Either.Right -> "Got reciprocal: ${x.value}"
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-either-09.kt -->
 *
 * Instead of using exceptions as our error value, let's instead enumerate explicitly the things that
 * can go wrong in our program.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.flatMap
 * //sampleStart
 * // Either with ADT Style
 *
 * sealed class Error {
 *   object NotANumber : Error()
 *   object NoZeroReciprocal : Error()
 * }
 *
 * fun parse(s: String): Either<Error, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(Error.NotANumber)
 *
 * fun reciprocal(i: Int): Either<Error, Double> =
 *   if (i == 0) Either.Left(Error.NoZeroReciprocal)
 *   else Either.Right(1.0 / i)
 *
 * fun stringify(d: Double): String = d.toString()
 *
 * fun magic(s: String): Either<Error, String> =
 *   parse(s).flatMap{reciprocal(it)}.map{ stringify(it) }
 * //sampleEnd
 * ```
 * <!--- KNIT example-either-10.kt -->
 *
 * For our little module, we enumerate any and all errors that can occur. Then, instead of using
 * exception classes as error values, we use one of the enumerated cases. Now, when we pattern match,
 * we are able to comphrensively handle failure without resulting in an `else` branch; moreover,
 * since Error is sealed, no outside code can add additional subtypes that we might fail to handle.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.flatMap
 *
 * sealed class Error {
 *  object NotANumber : Error()
 *  object NoZeroReciprocal : Error()
 * }
 *
 * fun parse(s: String): Either<Error, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(Error.NotANumber)
 *
 * fun reciprocal(i: Int): Either<Error, Double> =
 *   if (i == 0) Either.Left(Error.NoZeroReciprocal)
 *   else Either.Right(1.0 / i)
 *
 * fun stringify(d: Double): String = d.toString()
 *
 * fun magic(s: String): Either<Error, String> =
 *   parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }
 *
 * //sampleStart
 * val x = magic("2")
 * val value = when(x) {
 *   is Either.Left -> when (x.value) {
 *     is Error.NotANumber -> "Not a number!"
 *     is Error.NoZeroReciprocal -> "Can't take reciprocal of 0!"
 *   }
 *   is Either.Right -> "Got reciprocal: ${x.value}"
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-either-11.kt -->
 *
 * ## Either.catch exceptions
 *
 * Sometimes you do need to interact with code that can potentially throw exceptions. In such cases, you should mitigate the possibility that an exception can be thrown. You can do so by using the `catch` function.
 *
 * Example:
 *
 * ```kotlin
 * import arrow.core.Either
 *
 * //sampleStart
 * fun potentialThrowingCode(): String = throw RuntimeException("Blow up!")
 *
 * suspend fun makeSureYourLogicDoesNotHaveSideEffects(): Either<Error, String> =
 *   Either.catch { potentialThrowingCode() }.mapLeft { Error.SpecificError }
 * //sampleEnd
 * suspend fun main() {
 *   println("makeSureYourLogicDoesNotHaveSideEffects().isLeft() = ${makeSureYourLogicDoesNotHaveSideEffects().isLeft()}")
 * }
 *
 * sealed class Error {
 *   object SpecificError : Error()
 * }
 * ```
 * <!--- KNIT example-either-12.kt -->
 *
 * ## Syntax
 *
 * Either can also map over the [Left] value with `mapLeft`, which is similar to map, but applies on left instances.
 *
 * ```kotlin
 * import arrow.core.Either
 *
 * //sampleStart
 * val r : Either<Int, Int> = Either.Right(7)
 * val rightMapLeft = r.mapLeft {it + 1}
 * val l: Either<Int, Int> = Either.Left(7)
 * val leftMapLeft = l.mapLeft {it + 1}
 * //sampleEnd
 * fun main() {
 *  println("rightMapLeft = $rightMapLeft")
 *  println("leftMapLeft = $leftMapLeft")
 * }
 * ```
 * <!--- KNIT example-either-13.kt -->
 *
 * `Either<A, B>` can be transformed to `Either<B,A>` using the `swap()` method.
 *
 * ```kotlin
 * import arrow.core.Either.Left
 * import arrow.core.Either
 *
 * //sampleStart
 * val r: Either<String, Int> = Either.Right(7)
 * val swapped = r.swap()
 * //sampleEnd
 * fun main() {
 *  println("swapped = $swapped")
 * }
 * ```
 * <!--- KNIT example-either-14.kt -->
 *
 * For using Either's syntax on arbitrary data types.
 * This will make possible to use the `left()`, `right()`, `getOrElse()` methods:
 *
 * ```kotlin
 * import arrow.core.right
 *
 * val right7 =
 * //sampleStart
 *   7.right()
 * //sampleEnd
 * fun main() {
 *  println(right7)
 * }
 * ```
 * <!--- KNIT example-either-15.kt -->
 *
 * ```kotlin
 * import arrow.core.left
 *
 *  val leftHello =
 * //sampleStart
 *  "hello".left()
 * //sampleEnd
 * fun main() {
 *  println(leftHello)
 * }
 * ```
 * <!--- KNIT example-either-16.kt -->
 *
 * ```kotlin
 * import arrow.core.left
 * import arrow.core.getOrElse
 *
 * //sampleStart
 * val x = "hello".left()
 * val value = x.getOrElse { "$it world!" }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 * <!--- KNIT example-either-17.kt -->
 *
 * Another operation is `fold`. This operation will extract the value from the Either, or provide a default if the value is [Left]
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.right
 *
 * //sampleStart
 * val x : Either<Int, Int> = 7.right()
 * val fold = x.fold({ 1 }, { it + 3 })
 * //sampleEnd
 * fun main() {
 *  println("fold = $fold")
 * }
 * ```
 * <!--- KNIT example-either-18.kt -->
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.left
 *
 * //sampleStart
 * val y : Either<Int, Int> = 7.left()
 * val fold = y.fold({ 1 }, { it + 3 })
 * //sampleEnd
 * fun main() {
 *  println("fold = $fold")
 * }
 * ```
 * <!--- KNIT example-either-19.kt -->
 *
 * The `getOrHandle()` operation allows the transformation of an `Either.Left` value to a `Either.Right` using
 * the value of [Left]. This can be useful when mapping to a single result type is required like `fold()`, but without
 * the need to handle `Either.Right` case.
 *
 * As an example, we want to map an `Either<Throwable, Int>` to a proper HTTP status code:
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.getOrElse
 *
 * //sampleStart
 * val r: Either<Throwable, Int> = Either.Left(NumberFormatException())
 * val httpStatusCode = r.getOrElse {
 *   when(it) {
 *     is NumberFormatException -> 400
 *     else -> 500
 *   }
 * }
 * //sampleEnd
 * fun main() {
 *  println("httpStatusCode = $httpStatusCode")
 * }
 * ```
 * <!--- KNIT example-either-20.kt -->
 */
public sealed class Either<out A, out B> {

  /**
   * Returns `true` if this is a [Right], `false` otherwise.
   * Used only for performance instead of fold.
   */
  @Deprecated(
    RedundantAPI + "Use isRight()",
    ReplaceWith("isRight()")
  )
  @JsName("_isRight")
  internal abstract val isRight: Boolean

  /**
   * Returns `true` if this is a [Left], `false` otherwise.
   * Used only for performance instead of fold.
   */
  @Deprecated(
    RedundantAPI + "Use isLeft()",
    ReplaceWith("isLeft()")
  )
  @JsName("_isLeft")
  internal abstract val isLeft: Boolean

  public fun isLeft(): Boolean {
    contract {
      returns(true) implies (this@Either is Left<A>)
      returns(false) implies (this@Either is Right<B>)
    }
    return this@Either is Left<A>
  }

  public fun isRight(): Boolean {
    contract {
      returns(true) implies (this@Either is Right<B>)
      returns(false) implies (this@Either is Left<A>)
    }
    return this@Either is Right<B>
  }

  /**
   * Returns `false` if [Right]
   * or returns the result of the given [predicate] to the [Left] value.
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.Either.Left
   * import arrow.core.Either.Right
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *  Left(12).isLeft { it > 10 } shouldBe true
   *  Left(7).isLeft { it > 10 } shouldBe false
   *
   *  val right: Either<Int, String> = Right("Hello World")
   *  right.isLeft { it > 10 } shouldBe false
   * }
   * ```
   * <!--- KNIT example-either-21.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public inline fun isLeft(predicate: (A) -> Boolean): Boolean {
    contract { returns(true) implies (this@Either is Left<A>) }
    return this@Either is Left<A> && predicate(value)
  }

  /**
   * Returns `false` if [Left]
   * or returns the result of the given [predicate] to the [Right] value.
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.Either.Left
   * import arrow.core.Either.Right
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *  Right(12).isRight { it > 10 } shouldBe true
   *  Right(7).isRight { it > 10 } shouldBe false
   *
   *  val left: Either<String, Int> = Left("Hello World")
   *  left.isRight { it > 10 } shouldBe false
   * }
   * ```
   * <!--- KNIT example-either-22.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public inline fun isRight(predicate: (B) -> Boolean): Boolean {
    contract { returns(true) implies (this@Either is Right<B>) }
    return this@Either is Right<B> && predicate(value)
  }

  /**
   * Transform an [Either] into a value of [C].
   * Alternative to using `when` to fold an [Either] into a value [C].
   *
   * ```kotlin
   * import arrow.core.Either
   * import io.kotest.matchers.shouldBe
   * import io.kotest.assertions.fail
   *
   * fun test() {
   *   Either.Right(1)
   *     .fold({ fail("Cannot be left") }, { it + 1 }) shouldBe 2
   *
   *   Either.Left(RuntimeException("Boom!"))
   *     .fold({ -1 }, { fail("Cannot be right") }) shouldBe -1
   * }
   * ```
   * <!--- KNIT example-either-23.kt -->
   * <!--- TEST lines.isEmpty() -->
   *
   * @param ifLeft transform the [Either.Left] type [A] to [C].
   * @param ifRight transform the [Either.Right] type [B] to [C].
   * @return the transformed value [C] by applying [ifLeft] or [ifRight] to [A] or [B] respectively.
   */
  public inline fun <C> fold(ifLeft: (left: A) -> C, ifRight: (right: B) -> C): C {
    contract {
      callsInPlace(ifLeft, InvocationKind.AT_MOST_ONCE)
      callsInPlace(ifRight, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
      is Right -> ifRight(value)
      is Left -> ifLeft(value)
    }
  }

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("this.fold<C>({ initial }) { rightOperation(initial, it) }")
  )
  public inline fun <C> foldLeft(initial: C, rightOperation: (C, B) -> C): C =
    fold({ initial }) { rightOperation(initial, it) }

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("fold({ ifLeft }, f)")
  )
  public fun <C> foldMap(MN: Monoid<C>, f: (B) -> C): C =
    fold({ MN.empty() }, f)

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("fold({ f(c, it) }, { g(c, it) })")
  )
  public inline fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fold({ f(c, it) }, { g(c, it) })

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("fold(f, g)")
  )
  public inline fun <C> bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C): C =
    fold(f, g)

  /**
   * Swap the generic parameters [A] and [B] of this [Either].
   *
   * ```kotlin
   * import arrow.core.Either
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *   Either.Left("left").swap() shouldBe Either.Right("left")
   *   Either.Right("right").swap() shouldBe Either.Left("right")
   * }
   * ```
   * <!--- KNIT example-either-24.kt -->
   * <!-- TEST lines.isEmpty() -->
   */
  public fun swap(): Either<B, A> =
    fold({ Right(it) }, { Left(it) })

  /**
   * Map, or transform, the right value [B] of this [Either] to a new value [C].
   *
   * ```kotlin
   * import arrow.core.Either
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *   Either.Right(12).map { _: Int ->"flower" } shouldBe Either.Right("flower")
   *   Either.Left(12).map { _: Nothing -> "flower" } shouldBe Either.Left(12)
   * }
   * ```
   * <!--- KNIT example-either-25.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public inline fun <C> map(f: (right: B) -> C): Either<A, C> {
    contract {
      callsInPlace(f, InvocationKind.AT_MOST_ONCE)
    }
    return flatMap { Right(f(it)) }
  }


  /**
   * Map, or transform, the left value [A] of this [Either] to a new value [C].
   *
   * ```kotlin
   * import arrow.core.Either
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *  Either.Right(12).mapLeft { _: Nothing -> "flower" } shouldBe Either.Right(12)
   *  Either.Left(12).mapLeft { _: Int -> "flower" }  shouldBe Either.Left("flower")
   * }
   * ```
   * <!--- KNIT example-either-26.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public inline fun <C> mapLeft(f: (A) -> C): Either<C, B> {
    contract {
      callsInPlace(f, InvocationKind.AT_MOST_ONCE)
    }
    return fold({ Left(f(it)) }, { Right(it) })
  }

  @Deprecated(
    "tapLeft is being renamed to onLeft to be more consistent with the Kotlin Standard Library naming",
    ReplaceWith("onLeft(f)")
  )
  public inline fun tapLeft(f: (left: A) -> Unit): Either<A, B> =
    onLeft(f)

  @Deprecated(
    "tap is being renamed to onRight to be more consistent with the Kotlin Standard Library naming",
    ReplaceWith("onRight(f)")
  )
  public inline fun tap(f: (right: B) -> Unit): Either<A, B> =
    onRight(f)

  /**
   * Performs the given [action] on the encapsulated [B] value if this instance represents [Either.Right].
   * Returns the original [Either] unchanged.
   *
   * ```kotlin
   * import arrow.core.Either
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *   Either.Right(1).onRight(::println) shouldBe Either.Right(1)
   * }
   * ```
   * <!--- KNIT example-either-27.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public inline fun onRight(action: (right: B) -> Unit): Either<A, B> {
    contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isRight()) action(it.value) }
  }

  /**
   * Performs the given [action] on the encapsulated [A] if this instance represents [Either.Left].
   * Returns the original [Either] unchanged.
   *
   * ```kotlin
   * import arrow.core.Either
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *   Either.Left(2).onLeft(::println) shouldBe Either.Left(2)
   * }
   * ```
   * <!--- KNIT example-either-28.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public inline fun onLeft(action: (left: A) -> Unit): Either<A, B> {
    contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isLeft()) action(it.value) }
  }

  /**
   * Map over Left and Right of this Either
   */
  @Deprecated(
    NicheAPI + "Prefer using the Either DSL, or map + mapLeft",
    ReplaceWith("map(rightOperation).mapLeft(leftOperation)")
  )
  public inline fun <C, D> bimap(leftOperation: (left: A) -> C, rightOperation: (right: B) -> D): Either<C, D> =
    map(rightOperation).mapLeft(leftOperation)

  /**
   * Returns `false` if [Left] or returns the result of the application of
   * the given predicate to the [Right] value.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.Either.Left
   *
   * fun main() {
   *  Either.Right(12).exists { it > 10 } // Result: true
   *  Either.Right(7).exists { it > 10 }  // Result: false
   *
   *  val left: Either<Int, Int> = Left(12)
   *  left.exists { it > 10 }      // Result: false
   * }
   * ```
   * <!--- KNIT example-either-29.kt -->
   */
  @Deprecated(
    NicheAPI + "Prefer isRight",
    ReplaceWith("isRight(predicate)")
  )
  public inline fun exists(predicate: (B) -> Boolean): Boolean =
    fold({ false }, predicate)

  @Deprecated(
    "Facilitates the migration from Validated to Either.",
    ReplaceWith("isRight(predicate)")
  )
  public inline fun exist(predicate: (B) -> Boolean): Boolean =
    exists(predicate)

  /**
   * Returns `true` if [Left] or returns the result of the application of
   * the given predicate to the [Right] value.
   *
   * Example:
   * ```
   * Right(12).all { it > 10 } // Result: true
   * Right(7).all { it > 10 }  // Result: false
   *
   * val left: Either<Int, Int> = Left(12)
   * left.all { it > 10 }      // Result: true
   * ```
   */
  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("fold({ true }, predicate)")
  )
  public inline fun all(predicate: (B) -> Boolean): Boolean =
    fold({ true }, predicate)

  @Deprecated(
    "orNull is being renamed to getOrNull to be more consistent with the Kotlin Standard Library naming",
    ReplaceWith("getOrNull()")
  )
  public fun orNull(): B? {
    contract {
      returns(null) implies (this@Either is Left<A>)
      returnsNotNull() implies (this@Either is Right<B>)
    }
    return fold({ null }, { it })
  }

  /**
   * Returns the unwrapped value [B] of [Either.Right] or `null` if it is [Either.Left].
   *
   * ```kotlin
   * import arrow.core.Either
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *   Either.Right(12).getOrNull() shouldBe 12
   *   Either.Left(12).getOrNull() shouldBe null
   * }
   * ```
   * <!--- KNIT example-either-30.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public fun getOrNull(): B? {
    contract {
      returns(null) implies (this@Either is Left<A>)
      returnsNotNull() implies (this@Either is Right<B>)
    }
    return getOrElse { null }
  }

  /**
   * Returns the unwrapped value [A] of [Either.Left] or `null` if it is [Either.Right].
   *
   * ```kotlin
   * import arrow.core.Either
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *   Either.Right(12).leftOrNull() shouldBe null
   *   Either.Left(12).leftOrNull() shouldBe 12
   * }
   * ```
   * <!--- KNIT example-either-31.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public fun leftOrNull(): A? {
    contract {
      returnsNotNull() implies (this@Either is Left<A>)
      returns(null) implies (this@Either is Right<B>)
    }
    return fold(::identity) { null }
  }

  @Deprecated(
    "orNone is being renamed to getOrNone to be more consistent with the Kotlin Standard Library naming",
    ReplaceWith("getOrNone()")
  )
  public fun orNone(): Option<B> = getOrNone()

  /**
   * Transforms [Either] into [Option],
   * where the encapsulated value [B] is wrapped in [Some] when this instance represents [Either.Right],
   * or [None] if it is [Either.Left].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.Some
   * import arrow.core.None
   * import io.kotest.matchers.shouldBe
   *
   * fun test() {
   *   Either.Right(12).getOrNone() shouldBe Some(12)
   *   Either.Left(12).getOrNone() shouldBe None
   * }
   * ```
   * <!--- KNIT example-either-32.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public fun getOrNone(): Option<B> = fold({ None }, { Some(it) })

  @Deprecated(
    NicheAPI + "Prefer using the Either DSL, or map",
    ReplaceWith("if (n <= 0) Right(emptyList()) else map { b -> List(n) { b } }")
  )
  public fun replicate(n: Int): Either<A, List<B>> =
    if (n <= 0) Right(emptyList()) else map { b -> List(n) { b } }

  @Deprecated(
    NicheAPI + "Prefer using the Either DSL, or explicit fold or when",
    ReplaceWith(
      "fold({ listOf(it.left()) }, { fa(it).map(::Right) })", 
      "arrow.core.Either.Right",
      "arrow.core.Either.left"
    )
  )
  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <C> traverse(fa: (B) -> Iterable<C>): List<Either<A, C>> =
    fold({ listOf(it.left()) }, { fa(it).map(::Right) })

  @Deprecated(
    NicheAPI + "Prefer using the Either DSL, or explicit fold or when",
    ReplaceWith(
      "fold({ Some(it.left()) }, { right -> fa(right).map(::Right) })",
      "arrow.core.Either.Right",
      "arrow.core.Some",
      "arrow.core.left"
    )
  )
  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <C> traverse(fa: (B) -> Option<C>): Option<Either<A, C>> =
    fold({ Some(it.left()) }, { right -> fa(right).map(::Right) })

  @Deprecated("traverseOption is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <C> traverseOption(fa: (B) -> Option<C>): Option<Either<A, C>> =
    traverse(fa)

  @Deprecated(
    RedundantAPI + "Use orNull() and Kotlin nullable types",
    ReplaceWith("fold({ it.left() }) { fa(it)?.right() }", "arrow.core.left", "arrow.core.right")
  )
  public inline fun <C> traverseNullable(fa: (B) -> C?): Either<A, C>? =
    fold({ it.left() }) { fa(it)?.right() }

  @Deprecated(
    NicheAPI + "Prefer using the Either DSL, or explicit fold or when",
    ReplaceWith("fold({ it.left().valid() }, { fa(it).map(::Right) })")
  )
  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <AA, C> traverse(fa: (B) -> Validated<AA, C>): Validated<AA, Either<A, C>> =
    fold({ it.left().valid() }, { fa(it).map(::Right) })

  @Deprecated("traverseValidated is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <AA, C> traverseValidated(fa: (B) -> Validated<AA, C>): Validated<AA, Either<A, C>> =
    traverse(fa)

  @Deprecated(
    NicheAPI + "Prefer explicit fold instead",
    ReplaceWith("fold({ fe(it).map { aa -> Left(aa) } }, { fa(it).map { c -> Right(c) } })")
  )
  public inline fun <AA, C> bitraverse(fe: (A) -> Iterable<AA>, fa: (B) -> Iterable<C>): List<Either<AA, C>> =
    fold({ fe(it).map { aa -> Left(aa) } }, { fa(it).map { c -> Right(c) } })

  @Deprecated(
    NicheAPI + "Prefer explicit fold instead",
    ReplaceWith("fold({ fl(it).map(::Left) }, { fr(it).map(::Right) })")
  )
  public inline fun <AA, C> bitraverseOption(fl: (A) -> Option<AA>, fr: (B) -> Option<C>): Option<Either<AA, C>> =
    fold({ fl(it).map(::Left) }, { fr(it).map(::Right) })

  @Deprecated(
    NicheAPI + "Prefer explicit fold instead",
    ReplaceWith("fold({ fl(it)?.let(::Left) }, { fr(it)?.let(::Right) })")
  )
  public inline fun <AA, C> bitraverseNullable(fl: (A) -> AA?, fr: (B) -> C?): Either<AA, C>? =
    fold({ fl(it)?.let(::Left) }, { fr(it)?.let(::Right) })

  @Deprecated(
    NicheAPI + "Prefer explicit fold instead",
    ReplaceWith("fold({ fe(it).map { Left(it) } }, { fa(it).map { Right(it) } })")
  )
  public inline fun <AA, C, D> bitraverseValidated(
    fe: (A) -> Validated<AA, C>,
    fa: (B) -> Validated<AA, D>,
  ): Validated<AA, Either<C, D>> =
    fold({ fe(it).map { Left(it) } }, { fa(it).map { Right(it) } })

  @Deprecated(
    NicheAPI + "Prefer Kotlin nullable syntax instead",
    ReplaceWith("orNull()?.takeIf(predicate)")
  )
  public inline fun findOrNull(predicate: (B) -> Boolean): B? =
    orNull()?.takeIf(predicate)

  /**
   * Returns `true` if [Left]
   *
   * Example:
   * ```kotlin
   * import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   //sampleStart
   *   Either.Left("foo").isEmpty()  // Result: true
   *   Either.Right("foo").isEmpty() // Result: false
   * }
   * ```
   * <!--- KNIT example-either-33.kt -->
   */
  @Deprecated(
    RedundantAPI + "Use isLeft()",
    ReplaceWith("isLeft()")
  )
  public fun isEmpty(): Boolean = isLeft

  /**
   * Returns `true` if [Right]
   *
   * Example:
   * ```kotlin
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   //sampleStart
   *   Either.Left("foo").isNotEmpty()  // Result: false
   *   Either.Right("foo").isNotEmpty() // Result: true
   *   //sampleEnd
   * }
   * ```
   * <!--- KNIT example-either-34.kt -->
   */
  @Deprecated(
    RedundantAPI + "Use isRight()",
    ReplaceWith("isRight()")
  )
  public fun isNotEmpty(): Boolean = isRight

  /**
   * The left side of the disjoint union, as opposed to the [Right] side.
   */
  public data class Left<out A> constructor(val value: A) : Either<A, Nothing>() {
    override val isLeft = true
    override val isRight = false

    override fun toString(): String = "Either.Left($value)"

    public companion object {
      @Deprecated("Unused, will be removed from bytecode in Arrow 2.x.x", ReplaceWith("Left(Unit)"))
      @PublishedApi
      internal val leftUnit: Either<Unit, Nothing> = Left(Unit)
    }
  }

  /**
   * The right side of the disjoint union, as opposed to the [Left] side.
   */
  public data class Right<out B> constructor(val value: B) : Either<Nothing, B>() {
    override val isLeft = false
    override val isRight = true

    override fun toString(): String = "Either.Right($value)"

    public companion object {
      @PublishedApi
      internal val unit: Either<Nothing, Unit> = Right(Unit)
    }
  }

  override fun toString(): String = fold(
    { "Either.Left($it)" },
    { "Either.Right($it)" }
  )

  @Deprecated(ValidatedDeprMsg + "ValidatedNel is being replaced by EitherNel")
  public fun toValidatedNel(): ValidatedNel<A, B> =
    fold({ Validated.invalidNel(it) }, ::Valid)

  @Deprecated(ValidatedDeprMsg + "You can find more details about how to migrate on the Github release page, or the 1.2.0 release post.")
  public fun toValidated(): Validated<A, B> =
    fold({ it.invalid() }, { it.valid() })

  public fun toIor(): Ior<A, B> =
    fold({ Ior.Left(it) }, { Ior.Right(it) })

  public companion object {

    @Deprecated(
      RedundantAPI + "Prefer Kotlin nullable syntax, or ensureNotNull inside Either DSL",
      ReplaceWith("a?.right() ?: Unit.left()")
    )
    @JvmStatic
    public fun <A> fromNullable(a: A?): Either<Unit, A> = a?.right() ?: Unit.left()

    /**
     * Will create an [Either] from the result of evaluating the first parameter using the functions
     * provided on second and third parameters. Second parameter represents function for creating
     * an [Left] in case of a false result of evaluation and third parameter will be used
     * to create a [Right] in case of a true result.
     *
     * @param test expression to evaluate and build an [Either]
     * @param ifFalse function to create a [Left] in case of false result of test
     * @param ifTrue function to create a [Right] in case of true result of test
     *
     * @return [Right] if evaluation succeed, [Left] otherwise
     */
    @Deprecated(
      RedundantAPI + "Prefer explicit if-else statements, or ensure inside Either DSL",
      ReplaceWith("if (test) Right(ifTrue()) else Left(ifFalse())")
    )
    @JvmStatic
    public inline fun <L, R> conditionally(test: Boolean, ifFalse: () -> L, ifTrue: () -> R): Either<L, R> =
      if (test) Right(ifTrue()) else Left(ifFalse())

    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <R> catch(f: () -> R): Either<Throwable, R> =
      arrow.core.raise.catch({ f().right() }) { it.left() }

    @JvmStatic
    public inline fun <reified T : Throwable, R> catchOrThrow(f: () -> R): Either<T, R> =
      arrow.core.raise.catch<T, Either<T, R>>({ f().right() }) { it.left() }

    @Deprecated(
      RedundantAPI + "Compose catch with flatten instead",
      ReplaceWith("catch(f).flatten()")
    )
    @JvmStatic
    @JvmName("tryCatchAndFlatten")
    public inline fun <R> catchAndFlatten(f: () -> Either<Throwable, R>): Either<Throwable, R> =
      catch(f).flatten()

    @Deprecated(
      RedundantAPI + "Compose catch with mapLeft instead",
      ReplaceWith("catch(f).mapLeft(fe)")
    )
    @JvmStatic
    @JvmName("tryCatch")
    public inline fun <L, R> catch(fe: (Throwable) -> L, f: () -> R): Either<L, R> {
      contract { callsInPlace(fe, InvocationKind.AT_MOST_ONCE) }
      return catch(f).mapLeft(fe)
    }

    /**
     * The resolve function can resolve any function that yields an Either into one type of value.
     *
     * @param f the function that needs to be resolved.
     * @param success the function to apply if [f] yields a success of type [A].
     * @param error the function to apply if [f] yields an error of type [E].
     * @param throwable the function to apply if [f] throws a [Throwable].
     * Throwing any [Throwable] in the [throwable] function will render the [resolve] function nondeterministic.
     * @param unrecoverableState the function to apply if [resolve] is in an unrecoverable state.
     * @return the result of applying the [resolve] function.
     */
    @Deprecated(NicheAPI + "Prefer using recover, catch and the either DSL to work with errors")
    @JvmStatic
    public inline fun <E, A, B> resolve(
      f: () -> Either<E, A>,
      success: (a: A) -> Either<Throwable, B>,
      error: (e: E) -> Either<Throwable, B>,
      throwable: (throwable: Throwable) -> Either<Throwable, B>,
      unrecoverableState: (throwable: Throwable) -> Either<Throwable, Unit>,
    ): B {
      contract {
        callsInPlace(success, InvocationKind.AT_MOST_ONCE)
        callsInPlace(error, InvocationKind.AT_MOST_ONCE)
        callsInPlace(throwable, InvocationKind.AT_MOST_ONCE)
        callsInPlace(unrecoverableState, InvocationKind.AT_MOST_ONCE)
      }
      return catch(f)
        .fold(
          { t: Throwable -> throwable(t) },
          { it.fold({ e: E -> catchAndFlatten { error(e) } }, { a: A -> catchAndFlatten { success(a) } }) })
        .fold({ t: Throwable -> throwable(t) }, { b: B -> b.right() })
        .fold({ t: Throwable -> unrecoverableState(t); throw t }, { b: B -> b })
    }

    /**
     *  Lifts a function `(B) -> C` to the [Either] structure returning a polymorphic function
     *  that can be applied over all [Either] values in the shape of Either<A, B>
     *
     *  ```kotlin
     *  import arrow.core.*
     *
     *  fun main(args: Array<String>) {
     *   //sampleStart
     *   val f = Either.lift<Int, CharSequence, String> { s: CharSequence -> "$s World" }
     *   val either: Either<Int, CharSequence> = "Hello".right()
     *   val result = f(either)
     *   //sampleEnd
     *   println(result)
     *  }
     *  ```
     * <!--- KNIT example-either-35.kt -->
     */
    @JvmStatic
    @Deprecated(
      RedundantAPI + "Prefer explicitly creating lambdas",
      ReplaceWith("{ it.map(f) }")
    )
    public fun <A, B, C> lift(f: (B) -> C): (Either<A, B>) -> Either<A, C> =
      { it.map(f) }

    @JvmStatic
    @Deprecated(
      RedundantAPI + "Prefer explicitly creating lambdas",
      ReplaceWith("{ it.bimap(fa, fb) }")
    )
    public fun <A, B, C, D> lift(fa: (A) -> C, fb: (B) -> D): (Either<A, B>) -> Either<C, D> =
      { it.bimap(fa, fb) }


    public inline fun <E, A, B, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      transform: (A, B) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(combine, a, b, unit, unit, unit, unit, unit, unit, unit, unit) { aa, bb, _, _, _, _, _, _, _, _ ->
        transform(aa, bb)
      }
    }

    public inline fun <E, A, B, C, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      transform: (A, B, C) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(combine, a, b, c, unit, unit, unit, unit, unit, unit, unit) { aa, bb, cc, _, _, _, _, _, _, _ ->
        transform(aa, bb, cc)
      }
    }

    public inline fun <E, A, B, C, D, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      transform: (A, B, C, D) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(combine, a, b, c, d, unit, unit, unit, unit, unit, unit) { aa, bb, cc, dd, _, _, _, _, _, _ ->
        transform(aa, bb, cc, dd)
      }
    }

    public inline fun <E, A, B, C, D, EE, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      transform: (A, B, C, D, EE) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(combine, a, b, c, d, e, unit, unit, unit, unit, unit) { aa, bb, cc, dd, ee, _, _, _, _, _ ->
        transform(aa, bb, cc, dd, ee)
      }
    }

    public inline fun <E, A, B, C, D, EE, FF, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, FF>,
      transform: (A, B, C, D, EE, FF) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(combine, a, b, c, d, e, f, unit, unit, unit, unit) { aa, bb, cc, dd, ee, ff, _, _, _, _ ->
        transform(aa, bb, cc, dd, ee, ff)
      }
    }

    public inline fun <E, A, B, C, D, EE, F, G, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, F>,
      g: Either<E, G>,
      transform: (A, B, C, D, EE, F, G) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(combine, a, b, c, d, e, f, g, unit, unit, unit) { aa, bb, cc, dd, ee, ff, gg, _, _, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg)
      }
    }

    public inline fun <E, A, B, C, D, EE, F, G, H, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, F>,
      g: Either<E, G>,
      h: Either<E, H>,
      transform: (A, B, C, D, EE, F, G, H) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(combine, a, b, c, d, e, f, g, h, unit, unit) { aa, bb, cc, dd, ee, ff, gg, hh, _, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg, hh)
      }
    }

    public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, F>,
      g: Either<E, G>,
      h: Either<E, H>,
      i: Either<E, I>,
      transform: (A, B, C, D, EE, F, G, H, I) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(combine, a, b, c, d, e, f, g, h, i, unit) { aa, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg, hh, ii)
      }
    }

    @Suppress("DuplicatedCode")
    public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> zipOrAccumulate(
      combine: (E, E) -> E,
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, F>,
      g: Either<E, G>,
      h: Either<E, H>,
      i: Either<E, I>,
      j: Either<E, J>,
      transform: (A, B, C, D, EE, F, G, H, I, J) -> Z,
    ): Either<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return if (a is Right && b is Right && c is Right && d is Right && e is Right && f is Right && g is Right && h is Right && i is Right && j is Right) {
        Right(transform(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value))
      } else {
        var accumulatedError: Any? = EmptyValue
        accumulatedError = if (a is Left) a.value else accumulatedError
        accumulatedError = if (b is Left) EmptyValue.combine(accumulatedError, b.value, combine) else accumulatedError
        accumulatedError = if (c is Left) EmptyValue.combine(accumulatedError, c.value, combine) else accumulatedError
        accumulatedError = if (d is Left) EmptyValue.combine(accumulatedError, d.value, combine) else accumulatedError
        accumulatedError = if (e is Left) EmptyValue.combine(accumulatedError, e.value, combine) else accumulatedError
        accumulatedError = if (f is Left) EmptyValue.combine(accumulatedError, f.value, combine) else accumulatedError
        accumulatedError = if (g is Left) EmptyValue.combine(accumulatedError, g.value, combine) else accumulatedError
        accumulatedError = if (h is Left) EmptyValue.combine(accumulatedError, h.value, combine) else accumulatedError
        accumulatedError = if (i is Left) EmptyValue.combine(accumulatedError, i.value, combine) else accumulatedError
        accumulatedError = if (j is Left) EmptyValue.combine(accumulatedError, j.value, combine) else accumulatedError

        @Suppress("UNCHECKED_CAST")
        (Left(accumulatedError as E))
      }
    }

    public inline fun <E, A, B, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      transform: (A, B) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, unit, unit, unit, unit, unit, unit, unit, unit) { aa, bb, _, _, _, _, _, _, _, _ ->
        transform(aa, bb)
      }
    }

    public inline fun <E, A, B, C, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      transform: (A, B, C) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, unit, unit, unit, unit, unit, unit, unit) { aa, bb, cc, _, _, _, _, _, _, _ ->
        transform(aa, bb, cc)
      }
    }

    public inline fun <E, A, B, C, D, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      transform: (A, B, C, D) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, unit, unit, unit, unit, unit, unit) { aa, bb, cc, dd, _, _, _, _, _, _ ->
        transform(aa, bb, cc, dd)
      }
    }

    public inline fun <E, A, B, C, D, EE, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      transform: (A, B, C, D, EE) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, unit, unit, unit, unit, unit) { aa, bb, cc, dd, ee, _, _, _, _, _ ->
        transform(aa, bb, cc, dd, ee)
      }
    }

    public inline fun <E, A, B, C, D, EE, FF, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, FF>,
      transform: (A, B, C, D, EE, FF) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, f, unit, unit, unit, unit) { aa, bb, cc, dd, ee, ff, _, _, _, _ ->
        transform(aa, bb, cc, dd, ee, ff)
      }
    }

    public inline fun <E, A, B, C, D, EE, F, G, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, F>,
      g: Either<E, G>,
      transform: (A, B, C, D, EE, F, G) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, f, g, unit, unit, unit) { aa, bb, cc, dd, ee, ff, gg, _, _, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg)
      }
    }

    public inline fun <E, A, B, C, D, EE, F, G, H, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, F>,
      g: Either<E, G>,
      h: Either<E, H>,
      transform: (A, B, C, D, EE, F, G, H) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, f, g, h, unit, unit) { aa, bb, cc, dd, ee, ff, gg, hh, _, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg, hh)
      }
    }

    public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, F>,
      g: Either<E, G>,
      h: Either<E, H>,
      i: Either<E, I>,
      transform: (A, B, C, D, EE, F, G, H, I) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, f, g, h, i, unit) { aa, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg, hh, ii)
      }
    }

    @Suppress("DuplicatedCode")
    public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> zipOrAccumulate(
      a: Either<E, A>,
      b: Either<E, B>,
      c: Either<E, C>,
      d: Either<E, D>,
      e: Either<E, EE>,
      f: Either<E, F>,
      g: Either<E, G>,
      h: Either<E, H>,
      i: Either<E, I>,
      j: Either<E, J>,
      transform: (A, B, C, D, EE, F, G, H, I, J) -> Z,
    ): Either<NonEmptyList<E>, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return if (a is Right && b is Right && c is Right && d is Right && e is Right && f is Right && g is Right && h is Right && i is Right && j is Right) {
        Right(transform(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value))
      } else {
        val list = buildList(9) {
          if (a is Left) add(a.value)
          if (b is Left) add(b.value)
          if (c is Left) add(c.value)
          if (d is Left) add(d.value)
          if (e is Left) add(e.value)
          if (f is Left) add(f.value)
          if (g is Left) add(g.value)
          if (h is Left) add(h.value)
          if (i is Left) add(i.value)
          if (j is Left) add(j.value)
        }
        Left(NonEmptyList(list[0], list.drop(1)))
      }
    }

    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      transform: (A, B) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, unit, unit, unit, unit, unit, unit, unit, unit) { aa, bb, _, _, _, _, _, _, _, _ ->
        transform(aa, bb)
      }
    }

    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, C, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      c: EitherNel<E, C>,
      transform: (A, B, C) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, unit, unit, unit, unit, unit, unit, unit) { aa, bb, cc, _, _, _, _, _, _, _ ->
        transform(aa, bb, cc)
      }
    }

    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, C, D, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      c: EitherNel<E, C>,
      d: EitherNel<E, D>,
      transform: (A, B, C, D) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, unit, unit, unit, unit, unit, unit) { aa, bb, cc, dd, _, _, _, _, _, _ ->
        transform(aa, bb, cc, dd)
      }
    }

    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, C, D, EE, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      c: EitherNel<E, C>,
      d: EitherNel<E, D>,
      e: EitherNel<E, EE>,
      transform: (A, B, C, D, EE) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, unit, unit, unit, unit, unit) { aa, bb, cc, dd, ee, _, _, _, _, _ ->
        transform(aa, bb, cc, dd, ee)
      }
    }

    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, C, D, EE, FF, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      c: EitherNel<E, C>,
      d: EitherNel<E, D>,
      e: EitherNel<E, EE>,
      f: EitherNel<E, FF>,
      transform: (A, B, C, D, EE, FF) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, f, unit, unit, unit, unit) { aa, bb, cc, dd, ee, ff, _, _, _, _ ->
        transform(aa, bb, cc, dd, ee, ff)
      }
    }

    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, C, D, EE, F, G, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      c: EitherNel<E, C>,
      d: EitherNel<E, D>,
      e: EitherNel<E, EE>,
      f: EitherNel<E, F>,
      g: EitherNel<E, G>,
      transform: (A, B, C, D, EE, F, G) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, f, g, unit, unit, unit) { aa, bb, cc, dd, ee, ff, gg, _, _, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg)
      }
    }

    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, C, D, EE, F, G, H, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      c: EitherNel<E, C>,
      d: EitherNel<E, D>,
      e: EitherNel<E, EE>,
      f: EitherNel<E, F>,
      g: EitherNel<E, G>,
      h: EitherNel<E, H>,
      transform: (A, B, C, D, EE, F, G, H) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, f, g, h, unit, unit) { aa, bb, cc, dd, ee, ff, gg, hh, _, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg, hh)
      }
    }

    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, C, D, EE, F, G, H, I, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      c: EitherNel<E, C>,
      d: EitherNel<E, D>,
      e: EitherNel<E, EE>,
      f: EitherNel<E, F>,
      g: EitherNel<E, G>,
      h: EitherNel<E, H>,
      i: EitherNel<E, I>,
      transform: (A, B, C, D, EE, F, G, H, I) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return zipOrAccumulate(a, b, c, d, e, f, g, h, i, unit) { aa, bb, cc, dd, ee, ff, gg, hh, ii, _ ->
        transform(aa, bb, cc, dd, ee, ff, gg, hh, ii)
      }
    }

    @Suppress("DuplicatedCode")
    @JvmName("zipOrAccumulateNonEmptyList")
    public inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> zipOrAccumulate(
      a: EitherNel<E, A>,
      b: EitherNel<E, B>,
      c: EitherNel<E, C>,
      d: EitherNel<E, D>,
      e: EitherNel<E, EE>,
      f: EitherNel<E, F>,
      g: EitherNel<E, G>,
      h: EitherNel<E, H>,
      i: EitherNel<E, I>,
      j: EitherNel<E, J>,
      transform: (A, B, C, D, EE, F, G, H, I, J) -> Z,
    ): EitherNel<E, Z> {
      contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
      return if (a is Right && b is Right && c is Right && d is Right && e is Right && f is Right && g is Right && h is Right && i is Right && j is Right) {
        Right(transform(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value))
      } else {
        val list = buildList {
          if (a is Left) addAll(a.value)
          if (b is Left) addAll(b.value)
          if (c is Left) addAll(c.value)
          if (d is Left) addAll(d.value)
          if (e is Left) addAll(e.value)
          if (f is Left) addAll(f.value)
          if (g is Left) addAll(g.value)
          if (h is Left) addAll(h.value)
          if (i is Left) addAll(i.value)
          if (j is Left) addAll(j.value)
        }
        Left(NonEmptyList(list[0], list.drop(1)))
      }
    }
  }

  @Deprecated(
    RedundantAPI + "Map with Unit",
    ReplaceWith("map { }")
  )
  public fun void(): Either<A, Unit> =
    map { }

  @Deprecated(
    "Facilitates the migration from Validated to Either, you can simply remove this method call.",
    ReplaceWith("this")
  )
  public inline fun toEither(): Either<A, B> =
    this
}

/**
 * Map, or transform, the right value [B] of this [Either] into a new [Either] with a right value of type [C].
 * Returns a new [Either] with either the original left value of type [A] or the newly transformed right value of type [C].
 *
 * @param f The function to bind across [Right].
 */
public inline fun <A, B, C> Either<A, B>.flatMap(f: (right: B) -> Either<A, C>): Either<A, C> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return when (this) {
    is Right -> f(this.value)
    is Left -> this
  }
}

public fun <A, B> Either<A, Either<A, B>>.flatten(): Either<A, B> =
  flatMap(::identity)

@Deprecated(
  RedundantAPI + "This API is overloaded with an API with a single argument",
  level = DeprecationLevel.HIDDEN
)
public inline fun <B> Either<*, B>.getOrElse(default: () -> B): B =
  fold({ default() }, ::identity)

/**
 * Get the right value [B] of this [Either],
 * or compute a [default] value with the left value [A].
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.getOrElse
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   Either.Left(12) getOrElse { it + 5 } shouldBe 17
 * }
 * ```
 * <!--- KNIT example-either-36.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline infix fun <A, B> Either<A, B>.getOrElse(default: (A) -> B): B {
  contract { callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return fold(default, ::identity)
}

/**
 * Returns the value from this [Right] or null if this is a [Left].
 *
 * Example:
 * ```kotlin
 * import arrow.core.Either.Right
 * import arrow.core.Either.Left
 *
 * fun main() {
 *   Right(12).orNull() // Result: 12
 *   Left(12).orNull()  // Result: null
 * }
 * ```
 * <!--- KNIT example-either-37.kt -->
 */
@Deprecated(
  "Duplicated API. Please use Either's member function orNull. This will be removed towards Arrow 2.0",
  ReplaceWith("orNull()")
)
public fun <B> Either<*, B>.orNull(): B? =
  orNull()

/**
 * Returns the value from this [Right] or allows clients to transform the value from [Left] with the [default] lambda.
 *
 * Example:
 * ```kotlin
 * import arrow.core.Either.Right
 * import arrow.core.Either.Left
 * import arrow.core.getOrHandle
 *
 * fun main() {
 *   Right(12).getOrHandle { 17 } // Result: 12
 *   Left(12).getOrHandle { it + 5 } // Result: 17
 * }
 * ```
 * <!--- KNIT example-either-38.kt -->
 */
@Deprecated(
  RedundantAPI + "Use other getOrElse signature",
  ReplaceWith("getOrElse(default)")
)
public inline fun <A, B> Either<A, B>.getOrHandle(default: (A) -> B): B =
  fold({ default(it) }, ::identity)

/**
 * Returns [Right] with the existing value of [Right] if this is a [Right] and the given predicate
 * holds for the right value.<br>
 *
 * Returns `Left(default)` if this is a [Right] and the given predicate does not
 * hold for the right value.<br>
 *
 * Returns [Left] with the existing value of [Left] if this is a [Left].<br>
 *
 * Example:
 * ```kotlin
 * import arrow.core.Either.*
 * import arrow.core.Either
 * import arrow.core.filterOrElse
 *
 * fun main() {
 *   Right(12).filterOrElse({ it > 10 }, { -1 }) // Result: Right(12)
 *   Right(7).filterOrElse({ it > 10 }, { -1 })  // Result: Left(-1)
 *
 *   val left: Either<Int, Int> = Left(12)
 *   left.filterOrElse({ it > 10 }, { -1 })      // Result: Left(12)
 * }
 * ```
 * <!--- KNIT example-either-39.kt -->
 */
@Deprecated(
  RedundantAPI + "Prefer if-else statement inside either DSL, or replace with explicit flatMap",
  ReplaceWith("this.flatMap { if (predicate(it)) Either.Right(it) else Either.Left(default(it)) }")
)
public inline fun <A, B> Either<A, B>.filterOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
  flatMap { if (predicate(it)) Right(it) else Left(default()) }

/**
 * Returns [Right] with the existing value of [Right] if this is a [Right] and the given
 * predicate holds for the right value.<br>
 *
 * Returns `Left(default({right}))` if this is a [Right] and the given predicate does not
 * hold for the right value. Useful for error handling where 'default' returns a message with context on why the value
 * did not pass the filter<br>
 *
 * Returns [Left] with the existing value of [Left] if this is a [Left].<br>
 *
 * Example:
 *
 * ```kotlin
 * import arrow.core.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   Either.Right(7).filterOrOther({ it == 10 }, { "Value '$it' is not equal to 10" })
 *     .let(::println) // Either.Left(Value '7' is not equal to 10")
 *
 *   Either.Right(10).filterOrOther({ it == 10 }, { "Value '$it' is not equal to 10" })
 *     .let(::println) // Either.Right(10)
 *
 *   Either.Left(12).filterOrOther({ str: String -> str.contains("impossible") }, { -1 })
 *     .let(::println) // Either.Left(12)
 *   //sampleEnd
 * }
 * ```
 * <!--- KNIT example-either-40.kt -->
 */
@Deprecated(
  RedundantAPI + "Prefer if-else statement inside either DSL, or replace with explicit flatMap",
  ReplaceWith("this.flatMap { if (predicate(it)) Either.Right(it) else Either.Left(default()) }")
)
public inline fun <A, B> Either<A, B>.filterOrOther(predicate: (B) -> Boolean, default: (B) -> A): Either<A, B> =
  flatMap { if (predicate(it)) Right(it) else Left(default(it)) }

/**
 * Returns the value from this [Right] or [Left].
 *
 * Example:
 * ```kotlin
 * import arrow.core.Either.Left
 * import arrow.core.Either.Right
 * import arrow.core.merge
 *
 * fun test() {
 *   Right(12).merge() // Result: 12
 *   Left(12).merge() // Result: 12
 * }
 * ```
 * <!--- KNIT example-either-41.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public inline fun <A> Either<A, A>.merge(): A =
  fold(::identity, ::identity)

/**
 * Returns [Right] with the existing value of [Right] if this is an [Right] with a non-null value.
 * The returned Either.Right type is not nullable.
 *
 * Returns `Left(default())` if this is an [Right] and the existing value is null
 *
 * Returns [Left] with the existing value of [Left] if this is an [Left].
 *
 * Example:
 * ```kotlin
 * import arrow.core.Either.*
 * import arrow.core.leftIfNull
 *
 * fun main() {
 *   Right(12).leftIfNull({ -1 })   // Result: Right(12)
 *   Right(null).leftIfNull({ -1 }) // Result: Left(-1)
 *
 *   Left(12).leftIfNull({ -1 })    // Result: Left(12)
 * }
 * ```
 * <!--- KNIT example-either-42.kt -->
 */
@Deprecated(
  RedundantAPI + "Prefer Kotlin nullable syntax inside either DSL, or replace with explicit flatMap",
  ReplaceWith("flatMap { b -> b?.right() ?: default().left() }")
)
public inline fun <A, B> Either<A, B?>.leftIfNull(default: () -> A): Either<A, B> =
  flatMap { b -> b?.right() ?: default().left() }

/**
 * Returns `true` if this is a [Right] and its value is equal to `elem` (as determined by `==`),
 * returns `false` otherwise.
 *
 * Example:
 * ```kotlin
 * import arrow.core.Either.Right
 * import arrow.core.Either.Left
 * import arrow.core.contains
 *
 * fun main() {
 *   Right("something").contains("something") // Result: true
 *   Right("something").contains("anything")  // Result: false
 *   Left("something").contains("something")  // Result: false
 * }
 * ```
 * <!--- KNIT example-arrow-core-either-contains-01.kt -->
 *
 * @param elem the element to test.
 * @return `true` if the option has an element that is equal (as determined by `==`) to `elem`, `false` otherwise.
 */
@Deprecated(
  RedundantAPI + "Prefer the Either DSL, or replace with explicit fold",
  ReplaceWith("fold({ false }) { it == elem }")
)
public fun <A, B> Either<A, B>.contains(elem: B): Boolean =
  fold({ false }) { it == elem }

@Deprecated(
  RedundantAPI + "Prefer the Either DSL, or new recover API",
  ReplaceWith("recover { y.bind() }")
)
public fun <A, B> Either<A, B>.combineK(y: Either<A, B>): Either<A, B> =
  recover { y.bind() }

public fun <A> A.left(): Either<A, Nothing> = Left(this)

public fun <A> A.right(): Either<Nothing, A> = Right(this)

/**
 * Returns [Right] if the value of type B is not null, otherwise the specified A value wrapped into an
 * [Left].
 *
 * Example:
 * ```kotlin
 * import arrow.core.rightIfNotNull
 *
 * fun main() {
 *   "value".rightIfNotNull { "left" } // Right(b="value")
 *   null.rightIfNotNull { "left" }    // Left(a="left")
 * }
 * ```
 * <!--- KNIT example-either-43.kt -->
 */
@Deprecated(
  RedundantAPI + "Prefer Kotlin nullable syntax",
  ReplaceWith("this?.right() ?: default().left()")
)
public inline fun <A, B> B?.rightIfNotNull(default: () -> A): Either<A, B> =
  this?.right() ?: default().left()

/**
 * Returns [Right] if the value of type Any? is null, otherwise the specified A value wrapped into an
 * [Left].
 */
@Deprecated(
  RedundantAPI + "Prefer Kotlin nullable syntax",
  ReplaceWith("this?.let { default().left() } ?: null.right()")
)
public inline fun <A> Any?.rightIfNull(default: () -> A): Either<A, Nothing?> =
  this?.let { default().left() } ?: null.right()

@Deprecated(
  RedundantAPI + "Prefer the new recover API",
  ReplaceWith(
    "recover { a -> f(a).bind() }",
    "arrow.core.recover"
  )
)
public inline fun <A, B, C> Either<A, B>.handleErrorWith(f: (A) -> Either<C, B>): Either<C, B> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return recover { a -> f(a).bind() }
}

@Deprecated(
  RedundantAPI + "Prefer resolving the error with getOrElse.",
  ReplaceWith("getOrElse(f).right()", "arrow.core.right", "arrow.core.getOrElse")
)
public inline fun <A, B> Either<A, B>.handleError(f: (A) -> B): Either<A, B> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return getOrElse(f).right()
}

@Deprecated(
  RedundantAPI + "Prefer using the Either DSL or explicit fold with right",
  ReplaceWith(
    "fold({ a -> fe(a) }, fa).right()",
    "arrow.core.right"
  )
)
public inline fun <A, B, C> Either<A, B>.redeem(fe: (A) -> C, fa: (B) -> C): Either<A, C> {
  contract {
    callsInPlace(fe, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
  }
  return fold({ a -> fe(a) }, fa).right()
}

public operator fun <A : Comparable<A>, B : Comparable<B>> Either<A, B>.compareTo(other: Either<A, B>): Int =
  fold(
    { a1 -> other.fold({ a2 -> a1.compareTo(a2) }, { -1 }) },
    { b1 -> other.fold({ 1 }, { b2 -> b1.compareTo(b2) }) }
  )

/**
 * Combine two [Either] values.
 * If both are [Right] then combine both [B] values using [combineRight] or if both are [Left] then combine both [A] values using [combineLeft],
 * otherwise it returns the `this` or fallbacks to [other] in case `this` is [Left].
 */
public fun <A, B> Either<A, B>.combine(other: Either<A, B>, combineLeft: (A, A) -> A, combineRight: (B, B) -> B): Either<A, B> =
  when (val one = this) {
    is Left -> when (other) {
      is Left -> Left(combineLeft(one.value, other.value))
      is Right -> one
    }

    is Right -> when (other) {
      is Left -> other
      is Right -> Right(combineRight(one.value, other.value))
    }
  }

@Deprecated(
  RedundantAPI + "Prefer zipOrAccumulate",
  ReplaceWith("Either.zipOrAccumulate<A, B, B, B>({ a:A, bb:A -> a + bb }, this, b) { a:B, bb:B -> a + bb }")
)
public fun <A, B> Either<A, B>.combine(SGA: Semigroup<A>, SGB: Semigroup<B>, b: Either<A, B>): Either<A, B> =
  combine(b, SGA::combine, SGB::combine)


@Deprecated(
  MonoidDeprecation,
  ReplaceWith(
    "this.fold<Either<A, B>, Either<A, B>>(initialValue.right()) { x, y -> Either.zipOrAccumulate<A, B, B, B>({a1, a2 -> a1 + a2}, x, y, {b1, b2 -> b1 + b2}) }"
  )
)
public fun <A, B> Iterable<Either<A, B>>.combineAll(MA: Monoid<A>, MB: Monoid<B>): Either<A, B> =
  fold<Either<A, B>, Either<A, B>>(MB.empty().right()) { x, y -> Either.zipOrAccumulate(MA::combine, x, y, MB::combine) }

/**
 * Given [B] is a sub type of [C], re-type this value from Either<A, B> to Either<A, C>
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val string: Either<Int, String> = "Hello".right()
 *   val chars: Either<Int, CharSequence> =
 *     string.widen<Int, CharSequence, String>()
 *   //sampleEnd
 *   println(chars)
 * }
 * ```
 * <!--- KNIT example-either-44.kt -->
 */
public fun <A, C, B : C> Either<A, B>.widen(): Either<A, C> =
  this

public fun <AA, A : AA, B> Either<A, B>.leftWiden(): Either<AA, B> =
  this

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { f(this.bind(), fb.bind()) }",
    "arrow.core.raise.either"
  )
)
public fun <A, B, C, D> Either<A, B>.zip(fb: Either<A, C>, f: (B, C) -> D): Either<A, D> {
  contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
  return either { f(bind(), fb.bind()) }
}

@Deprecated(
  "Prefer using the inline arrow.core.raise.either DSL",
  ReplaceWith(
    "either { Pair(this.bind(), fb.bind()) }",
    "arrow.core.raise.either"
  )
)
public fun <A, B, C> Either<A, B>.zip(fb: Either<A, C>): Either<A, Pair<B, C>> = either {
  Pair(bind(), fb.bind())
}

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { map(this.bind(), c.bind(), d.bind()) }",
    "arrow.core.raise.either"
  )
)
public inline fun <A, B, C, D, E> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  map: (B, C, D) -> E,
): Either<A, E> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return either { map(bind(), c.bind(), d.bind()) }
}

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { map(this.bind(), c.bind(), d.bind(), e.bind()) }",
    "arrow.core.raise.either"
  )
)
public inline fun <A, B, C, D, E, F> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  map: (B, C, D, E) -> F,
): Either<A, F> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return either { map(bind(), c.bind(), d.bind(), e.bind()) }
}

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { map(this.bind(), c.bind(), d.bind(), e.bind(), f.bind()) }",
    "arrow.core.raise.either"
  )
)
public inline fun <A, B, C, D, E, F, G> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  map: (B, C, D, E, F) -> G,
): Either<A, G> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return either { map(bind(), c.bind(), d.bind(), e.bind(), f.bind()) }
}

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { map(this.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind()) }",
    "arrow.core.raise.either"
  )
)
public inline fun <A, B, C, D, E, F, G, H> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  g: Either<A, G>,
  map: (B, C, D, E, F, G) -> H,
): Either<A, H> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return either { map(bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind()) }
}

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { map(this.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind()) }",
    "arrow.core.raise.either"
  )
)
public inline fun <A, B, C, D, E, F, G, H, I> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  g: Either<A, G>,
  h: Either<A, H>,
  map: (B, C, D, E, F, G, H) -> I,
): Either<A, I> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return either { map(bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind()) }
}

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { map(this.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind()) }",
    "arrow.core.raise.either"
  )
)
public inline fun <A, B, C, D, E, F, G, H, I, J> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  g: Either<A, G>,
  h: Either<A, H>,
  i: Either<A, I>,
  map: (B, C, D, E, F, G, H, I) -> J,
): Either<A, J> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return either { map(bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind()) }
}

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { map(this.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind()) }",
    "arrow.core.raise.either"
  )
)
public inline fun <A, B, C, D, E, F, G, H, I, J, K> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  g: Either<A, G>,
  h: Either<A, H>,
  i: Either<A, I>,
  j: Either<A, J>,
  map: (B, C, D, E, F, G, H, I, J) -> K,
): Either<A, K> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return either { map(bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind()) }
}

@Deprecated(
  "Prefer using the inline either DSL",
  ReplaceWith(
    "either { map(this.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind(), k.bind()) }",
    "arrow.core.raise.either"
  )
)
public inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  g: Either<A, G>,
  h: Either<A, H>,
  i: Either<A, I>,
  j: Either<A, J>,
  k: Either<A, K>,
  map: (B, C, D, E, F, G, H, I, J, K) -> L,
): Either<A, L> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return either { map(bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind(), k.bind()) }
}

@Deprecated(
  NicheAPI + "Prefer using the Either DSL, or map",
  ReplaceWith("if (n <= 0) Either.Right(initial) else this.map { b -> List(n) { b }.fold(initial){r, t -> r + t} }")
)
public fun <A, B> Either<A, B>.replicate(n: Int, MB: Monoid<B>): Either<A, B> =
  map { b -> List(n) { b }.fold(MB.empty(), MB::combine) }

@Deprecated(
  RedundantAPI + "Prefer if-else statement inside either DSL, or replace with explicit flatMap",
  ReplaceWith("flatMap { b -> b.takeIf(predicate)?.right() ?: error().left() }")
) // TODO open-question: should we expose `ensureNotNull` or `ensure` DSL API on Either or Companion?
public inline fun <A, B> Either<A, B>.ensure(error: () -> A, predicate: (B) -> Boolean): Either<A, B> =
  flatMap { b -> b.takeIf(predicate)?.right() ?: error().left() }

@Deprecated(
  NicheAPI + "Prefer using a simple fold, or when expression",
  ReplaceWith("fold(fa, fb)")
)
public inline fun <A, B, C, D> Either<A, B>.redeemWith(fa: (A) -> Either<C, D>, fb: (B) -> Either<C, D>): Either<C, D> {
  contract {
    callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
    callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
  }
  return fold(fa, fb)
}

@Deprecated(
  "Prefer Kotlin nullable syntax inside either DSL, or replace with explicit fold",
  ReplaceWith(
    "fold({ listOf<Either<A, B>>(it.left()) }, { iterable -> iterable.map<B, Either<A, B>> { it.right() } })",
    "arrow.core.right", "arrow.core.left"
  )
)
public fun <A, B> Either<A, Iterable<B>>.sequence(): List<Either<A, B>> =
  fold({ listOf(it.left()) }, { iterable -> iterable.map { it.right() } })

@Deprecated(
  "Prefer Kotlin nullable syntax inside either DSL, or replace with explicit fold",
  ReplaceWith(
    "this.fold<Option<Either<A, B>>>({ Some(it.left()) }, { iterable -> iterable.map<B, Either<A, B>> { it.right() } })",
    "arrow.core.Either",
    "arrow.core.Option",
    "arrow.core.Some",
    "arrow.core.left",
    "arrow.core.right"
  )
)
public fun <A, B> Either<A, Option<B>>.sequenceOption(): Option<Either<A, B>> =
  sequence()

@Deprecated(
  "Prefer Kotlin nullable syntax inside either DSL, or replace with explicit fold",
  ReplaceWith(
    "this.fold<Option<Either<A, B>>>({ Some(it.left()) }, { iterable -> iterable.map<B, Either<A, B>> { it.right() } })",
    "arrow.core.Either",
    "arrow.core.Option",
    "arrow.core.Some",
    "arrow.core.left",
    "arrow.core.right"
  )
)
public fun <A, B> Either<A, Option<B>>.sequence(): Option<Either<A, B>> =
  fold({ Some(it.left()) }) { it.map { it.right() } }

@Deprecated(
  "Prefer Kotlin nullable syntax inside either DSL, or replace with explicit fold",
  ReplaceWith(
    "this.fold<Either<A, B>?>({ it.left() }, { it?.right() })",
    "arrow.core.Either",
    "arrow.core.right",
    "arrow.core.left"
  )
)
public fun <A, B> Either<A, B?>.sequenceNullable(): Either<A, B>? =
  sequence()

@Deprecated(
  "Prefer Kotlin nullable syntax",
  ReplaceWith(
    "this.fold<Either<A, B>?>({ it.left() }, { it?.right() })",
    "arrow.core.Either",
    "arrow.core.right",
    "arrow.core.left"
  )
)
public fun <A, B> Either<A, B?>.sequence(): Either<A, B>? =
  this.fold<Either<A, B>?>({ it.left() }, { it?.right() })

@Deprecated(
  "sequenceValidated is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B, C> Either<A, Validated<B, C>>.sequenceValidated(): Validated<B, Either<A, C>> =
  sequence()

@Deprecated(
  ValidatedDeprMsg + NicheAPI,
  ReplaceWith("fold({ Valid(Left(it)) }) { it.fold({ Invalid(it) }) { Valid(Right(it)) } }")
)
public fun <A, B, C> Either<A, Validated<B, C>>.sequence(): Validated<B, Either<A, C>> =
  traverse(::identity)

public fun <A, B> Either<Iterable<A>, Iterable<B>>.bisequence(): List<Either<A, B>> =
  bitraverse(::identity, ::identity)

public fun <A, B> Either<Option<A>, Option<B>>.bisequenceOption(): Option<Either<A, B>> =
  bitraverseOption(::identity, ::identity)

public fun <A, B> Either<A?, B?>.bisequenceNullable(): Either<A, B>? =
  bitraverseNullable(::identity, ::identity)

@Deprecated(
  ValidatedDeprMsg + NicheAPI,
  ReplaceWith("fold({ it.fold({ Invalid(it) }) { Valid(Left(it)) } }) { it.fold({ Invalid(it) }) { Valid(Right(it)) } }")
)
public fun <A, B, C> Either<Validated<A, B>, Validated<A, C>>.bisequenceValidated(): Validated<A, Either<B, C>> =
  bitraverseValidated(::identity, ::identity)

public const val NicheAPI: String =
  "This API is niche and will be removed in the future. If this method is crucial for you, please let us know on the Arrow Github. Thanks!\n https://github.com/arrow-kt/arrow/issues\n"

public const val RedundantAPI: String =
  "This API is considered redundant. If this method is crucial for you, please let us know on the Arrow Github. Thanks!\n https://github.com/arrow-kt/arrow/issues\n"

public fun <E, A> Either<E, A>.toEitherNel(): EitherNel<E, A> =
  mapLeft { nonEmptyListOf(it) }

public fun <E> E.leftNel(): EitherNel<E, Nothing> =
  nonEmptyListOf(this).left()

/**
 * Recover from any [Either.Left] if encountered.
 *
 * The recover DSL allows you to recover from any [Either.Left] value by:
 *  - Computing a fallback value [A], and resolve the left type [E] to [Nothing].
 *  - Shifting a _new error_ of type [EE] into the [Either.Left] channel.
 *
 * When providing a fallback value [A],
 * the [Either.Left] type is discarded because the error was handled correctly.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.recover
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val error: Either<String, Int> = Either.Left("error")
 *   val fallback: Either<Nothing, Int> = error.recover { it.length }
 *   fallback shouldBe Either.Right(5)
 * }
 * ```
 * <!--- KNIT example-either-45.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * When shifting a new error [EE] into the [Either.Left] channel,
 * the [Either.Left] is _transformed_ from [E] to [EE] since we shifted a _new error_.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.recover
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val error: Either<String, Int> = Either.Left("error")
 *   val listOfErrors: Either<List<Char>, Int> = error.recover { raise(it.toList()) }
 *   listOfErrors shouldBe Either.Left(listOf('e', 'r', 'r', 'o', 'r'))
 * }
 * ```
 * <!--- KNIT example-either-46.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <E, EE, A> Either<E, A>.recover(@BuilderInference recover: Raise<EE>.(E) -> A): Either<EE, A> {
  contract { callsInPlace(recover, InvocationKind.AT_MOST_ONCE) }
  return when (this) {
    is Left -> either { recover(this, value) }
    is Right -> this@recover
  }
}

/**
 * Variant of [Either.catchOrThrow] constructor that allows for working with `Either<Throwable, A>`
 * by transforming or recovering from [Throwable] as [T] in the [Either.Left] side. This API is the same as [recover].
 * This is useful when working with results of [Either.catch] since this API offers a `reified` variant.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.catch
 * import io.kotest.assertions.throwables.shouldThrowUnit
 * import io.kotest.matchers.shouldBe
 *
 * fun test() {
 *   val left: Either<Throwable, Int> = Either.catch { throw RuntimeException("Boom!") }
 *
 *   val caught: Either<Nothing, Int> = left.catch { _: RuntimeException -> 1 }
 *   val failure: Either<String, Int> = left.catch { _: RuntimeException -> raise("failure") }
 *
 *   shouldThrowUnit<RuntimeException> {
 *     val caught2: Either<Nothing, Int> = left.catch { _: IllegalStateException -> 1 }
 *   }
 *
 *   caught shouldBe Either.Right(1)
 *   failure shouldBe Either.Left("failure")
 * }
 * ```
 * <!--- KNIT example-either-47.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <E, reified T : Throwable, A> Either<Throwable, A>.catch(@BuilderInference catch: Raise<E>.(T) -> A): Either<E, A> {
  contract { callsInPlace(catch, InvocationKind.AT_MOST_ONCE) }
  return recover { e -> if (e is T) catch(e) else throw e }
}
