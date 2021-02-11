package arrow.core

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Either.Companion.resolve
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.hashWithSalt

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)class ForEither private constructor() {
  companion object
}
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)typealias EitherOf<A, B> = arrow.Kind2<ForEither, A, B>
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)typealias EitherPartialOf<A> = arrow.Kind<ForEither, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)inline fun <A, B> EitherOf<A, B>.fix(): Either<A, B> =
  this as Either<A, B>

/**
 *
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
 * ```kotlin:ank:playground
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
 *
 * Assume we happily throw exceptions in our code. Looking at the types of the functions above, any could throw a number of exceptions -- we do not know. When we compose, exceptions from any of the constituent
 * functions can be thrown. Moreover, they may throw the same kind of exception
 * (e.g., `IllegalArgumentException`) and, thus, it gets tricky tracking exactly where an exception came from.
 *
 * How then do we communicate an error? By making it explicit in the data type we return.
 *
 * ## Either vs Validated
 *
 * In general, `Validated` is used to accumulate errors, while `Either` is used to short-circuit a computation
 * upon the first error. For more information, see the `Validated` vs `Either` section of the `Validated` documentation.
 *
 * By convention, the right side of an `Either` is used to hold successful values.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 *
 * val right: Either<String, Int> =
 * //sampleStart
 *  Either.Right(5)
 * //sampleEnd
 * fun main() {
 *  println(right)
 * }
 * ```
 *
 * ```kotlin:ank:playground
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
 * Because `Either` is right-biased, it is possible to define a Monad instance for it.
 *
 * Since we only ever want the computation to continue in the case of `Right` (as captured by the right-bias nature),
 * we fix the left type parameter and leave the right one free.
 *
 * So, the map and flatMap methods are right-biased:
 *
 * ```kotlin:ank:playground
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
 *
 * ```kotlin:ank:playground
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
 * ```kotlin:ank
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
 *
 * Instead, let's make the fact that some of our functions can fail explicit in the return type.
 *
 * ```kotlin:ank
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
 *
 * These calls to `parse` return a `Left` and `Right` value
 *
 * ```kotlin:ank:playground
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
 *
 * Now, using combinators like `flatMap` and `map`, we can compose our functions together.
 *
 * ```kotlin:ank:playground
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
 *
 * In the following exercise, we pattern-match on every case in which the `Either` returned by `magic` can be in.
 * Note the `when` clause in the `Left` - the compiler will complain if we leave that out because it knows that,
 * given the type `Either[Exception, String]`, there can be inhabitants of `Left` that are not
 * `NumberFormatException` or `IllegalArgumentException`. You should also notice that we are using
 * [SmartCast](https://kotlinlang.org/docs/reference/typecasts.html#smart-casts) for accessing `Left` and `Right`
 * values.
 *
 * ```kotlin:ank:playground
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
 *   is Either.Left -> when (x.a){
 *     is NumberFormatException -> "Not a number!"
 *     is IllegalArgumentException -> "Can't take reciprocal of 0!"
 *     else -> "Unknown error"
 *   }
 *   is Either.Right -> "Got reciprocal: ${x.b}"
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * Instead of using exceptions as our error value, let's instead enumerate explicitly the things that
 * can go wrong in our program.
 *
 * ```kotlin:ank
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
 *
 * For our little module, we enumerate any and all errors that can occur. Then, instead of using
 * exception classes as error values, we use one of the enumerated cases. Now, when we pattern match,
 * we are able to comphrensively handle failure without resulting in an `else` branch; moreover,
 * since Error is sealed, no outside code can add additional subtypes that we might fail to handle.
 *
 * ```kotlin:ank:playground
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
 *   is Either.Left -> when (x.a){
 *     is Error.NotANumber -> "Not a number!"
 *     is Error.NoZeroReciprocal -> "Can't take reciprocal of 0!"
 *   }
 *   is Either.Right -> "Got reciprocal: ${x.b}"
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * ## Either.catch exceptions
 *
 * Sometimes you do need to interact with code that can potentially throw exceptions. In such cases, you should mitigate the possibility that an exception can be thrown. You can do so by using the `catch` function.
 *
 * Example:
 *
 * ```kotlin:ank:playground
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
 *
 * ## Resolve Either into one type of value
 * In some cases you can not use Either as a value. For instance, when you need to respond to an HTTP request. To resolve Either into one type of value, you can use the resolve function.
 * In the case of an HTTP endpoint you most often need to return some (framework specific) response object which holds the result of the request. The result can be expected and positive, this is the success flow.
 * Or the result can be expected but negative, this is the error flow. Or the result can be unexpected and negative, in this case an unhandled exception was thrown.
 * In all three cases, you want to use the same kind of response object. But probably you want to respond slightly different in each case. This can be achieved by providing specific functions for the success, error and throwable cases.
 *
 * Example:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.flatMap
 * import arrow.core.left
 * import arrow.core.right
 *
 * //sampleStart
 * suspend fun httpEndpoint(request: String = "Hello?") =
 *   Either.resolve(
 *     f = {
 *       if (request == "Hello?") "HELLO WORLD!".right()
 *       else Error.SpecificError.left()
 *     },
 *     success = { a -> handleSuccess({ a: Any -> log(Level.INFO, "This is a: $a") }, a) },
 *     error = { e -> handleError({ e: Any -> log(Level.WARN, "This is e: $e") }, e) },
 *     throwable = { throwable -> handleThrowable({ throwable: Throwable -> log(Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
 *     unrecoverableState = { _ -> Unit.right() }
 *   )
 * //sampleEnd
 * suspend fun main() {
 *  println("httpEndpoint().status = ${httpEndpoint().status}")
 * }
 *
 * @Suppress("UNUSED_PARAMETER")
 * suspend fun <A> handleSuccess(log: suspend (a: A) -> Either<Throwable, Unit>, a: A): Either<Throwable, Response> =
 *   Either.catch {
 *     Response.Builder(HttpStatus.OK)
 *       .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
 *       .body(a)
 *       .build()
 *   }
 *
 * @Suppress("UNUSED_PARAMETER")
 * suspend fun <E> handleError(log: suspend (e: E) -> Either<Throwable, Unit>, e: E): Either<Throwable, Response> =
 *   createErrorResponse(HttpStatus.NOT_FOUND, ErrorResponse("$ERROR_MESSAGE_PREFIX $e"))
 *
 * suspend fun handleThrowable(log: suspend (throwable: Throwable) -> Either<Throwable, Unit>, throwable: Throwable): Either<Throwable, Response> =
 *   log(throwable)
 *     .flatMap { createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponse("$THROWABLE_MESSAGE_PREFIX $throwable")) }
 *
 * suspend fun createErrorResponse(httpStatus: HttpStatus, errorResponse: ErrorResponse): Either<Throwable, Response> =
 *   Either.catch {
 *     Response.Builder(httpStatus)
 *       .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
 *       .body(errorResponse)
 *       .build()
 *   }
 *
 * suspend fun log(level: Level, message: String): Either<Throwable, Unit> =
 *   Unit.right() // Should implement logging.
 *
 * enum class HttpStatus(val value: Int) { OK(200), NOT_FOUND(404), INTERNAL_SERVER_ERROR(500) }
 *
 * class Response private constructor(
 *   val status: HttpStatus,
 *   val headers: Map<String, String>,
 *   val body: Any?
 * ) {
 *
 *   data class Builder(
 *     val status: HttpStatus,
 *     var headers: Map<String, String> = emptyMap(),
 *     var body: Any? = null
 *   ) {
 *     fun header(key: String, value: String) = apply { this.headers = this.headers + mapOf<String, String>(key to value) }
 *     fun body(body: Any?) = apply { this.body = body }
 *     fun build() = Response(status, headers, body)
 *   }
 * }
 *
 * val CONTENT_TYPE = "Content-Type"
 * val CONTENT_TYPE_APPLICATION_JSON = "application/json"
 * val ERROR_MESSAGE_PREFIX = "An error has occurred. The error is:"
 * val THROWABLE_MESSAGE_PREFIX = "An exception was thrown. The exception is:"
 * sealed class Error {
 *   object SpecificError : Error()
 * }
 * data class ErrorResponse(val errorMessage: String)
 * enum class Level { INFO, WARN, ERROR }
 * ```
 *
 * There are far more use cases for the resolve function, the HTTP endpoint example is just one of them.
 *
 * ## Syntax
 *
 * Either can also map over the `left` value with `mapLeft`, which is similar to map, but applies on left instances.
 *
 * ```kotlin:ank:playground
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
 *
 * `Either<A, B>` can be transformed to `Either<B,A>` using the `swap()` method.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Right
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
 *
 * For using Either's syntax on arbitrary data types.
 * This will make possible to use the `left()`, `right()`, `contains()`, `getOrElse()` and `getOrHandle()` methods:
 *
 * ```kotlin:ank:playground
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
 *
 * ```kotlin:ank:playground
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
 *
 * ```kotlin:ank:playground
 * import arrow.core.right
 * import arrow.core.contains
 *
 * //sampleStart
 * val x = 7.right()
 * val contains7 = x.contains(7)
 * //sampleEnd
 * fun main() {
 *  println("contains7 = $contains7")
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.left
 * import arrow.core.getOrElse
 *
 * //sampleStart
 * val x = "hello".left()
 * val getOr7 = x.getOrElse { 7 }
 * //sampleEnd
 * fun main() {
 *  println("getOr7 = $getOr7")
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.left
 * import arrow.core.getOrHandle
 *
 * //sampleStart
 * val x = "hello".left()
 * val value = x.getOrHandle { "$it world!" }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * For creating Either instance based on a predicate, use `Either.conditionally()` method. It will evaluate an expression
 * passed as first parameter, in case the expression evaluates to `false` it will give an `Either.Left<L>` build from the second parameter.
 * If the expression evaluates to a `true` it will take the third parameter and give an `Either.Right<R>`:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 *
 * val value =
 * //sampleStart
 *  Either.conditionally(true, { "Error" }, { 42 })
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 *
 * val value =
 * //sampleStart
 *  Either.conditionally(false, { "Error" }, { 42 })
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * Another operation is `fold`. This operation will extract the value from the Either, or provide a default if the value is `Left`
 *
 * ```kotlin:ank:playground
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
 *
 * ```kotlin:ank:playground
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
 *
 * The `getOrHandle()` operation allows the transformation of an `Either.Left` value to a `Either.Right` using
 * the value of `Left`. This can be useful when mapping to a single result type is required like `fold()`, but without
 * the need to handle `Either.Right` case.
 *
 * As an example, we want to map an `Either<Throwable, Int>` to a proper HTTP status code:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.getOrHandle
 *
 * //sampleStart
 * val r: Either<Throwable, Int> = Either.Left(NumberFormatException())
 * val httpStatusCode = r.getOrHandle {
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
 *
 * The ```leftIfNull``` operation transforms a null `Either.Right` value to the specified ```Either.Left``` value.
 * If the value is non-null, the value wrapped into a non-nullable ```Either.Right``` is returned (very useful to
 * skip null-check further down the call chain).
 * If the operation is called on an ```Either.Left```, the same ```Either.Left``` is returned.
 *
 * See the examples below:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Right
 * import arrow.core.leftIfNull
 *
 * val value =
 * //sampleStart
 *  Right(12).leftIfNull({ -1 })
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Right
 * import arrow.core.leftIfNull
 *
 * val value =
 * //sampleStart
 *  Right(null).leftIfNull({ -1 })
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Left
 * import arrow.core.leftIfNull
 *
 * val value =
 * //sampleStart
 *  Left(12).leftIfNull({ -1 })
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * Another useful operation when working with null is `rightIfNotNull`.
 * If the value is null, it will be transformed to the specified `Either.Left` and, if it's not null, the type will
 * be wrapped to `Either.Right`.
 *
 * Example:
 *
 * ```kotlin:ank:playground
 * import arrow.core.rightIfNotNull
 *
 * val value =
 * //sampleStart
 *  "value".rightIfNotNull { "left" }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.rightIfNotNull
 *
 * val value =
 * //sampleStart
 *  null.rightIfNotNull { "left" }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * The inverse of `rightIfNotNull`, `rightIfNull`.
 * If the value is null it will be transformed to the specified `Either.right` and the type will be `Nothing?`.
 * If the value is not null than it will be transformed to the specified `Either.Left`.
 *
 * Example:
 *
 * ```kotlin:ank:playground
 * import arrow.core.rightIfNull
 *
 * val value =
 * //sampleStart
 *  "value".rightIfNull { "left" }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.rightIfNull
 *
 * val value =
 * //sampleStart
 *  null.rightIfNull { "left" }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * Arrow contains `Either` instances for many useful typeclasses that allows you to use and transform right values.
 * Option does not require a type parameter with the following functions, but it is specifically used for Either.Left
 *
 * [`Functor`](../../../../arrow/typeclasses/functor/)
 *
 * Transforming the inner contents
 *
 * ```kotlin:ank:playground
 * import arrow.core.Right
 *
 * val value =
 * //sampleStart
 *  Right(1).map{ it + 1 }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * [`Applicative`](../../../../arrow/typeclasses/applicative/)
 *
 * Computing over independent values
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.extensions.either.apply.tupled
 *
 * val value =
 * //sampleStart
 *  tupled(Either.Right(1), Either.Right("a"), Either.Right(2.0))
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
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.computations.either
 *
 * suspend fun main() {
 * val value =
 * //sampleStart
 *  either<Int, Int> {
 *   val a = Either.Right(1)()
 *   val b = Either.Right(1 + a)()
 *   val c = Either.Right(1 + b)()
 *   a + b + c
 *  }
 * //sampleEnd
 *  println(value)
 * }
 * ```
 *
 */
sealed class Either<out A, out B> : EitherOf<A, B> {

  /**
   * Returns `true` if this is a [Right], `false` otherwise.
   * Used only for performance instead of fold.
   */
  internal abstract val isRight: Boolean

  /**
   * Returns `true` if this is a [Left], `false` otherwise.
   * Used only for performance instead of fold.
   */
  internal abstract val isLeft: Boolean

  fun isLeft(): Boolean = isLeft

  fun isRight(): Boolean = isRight

  /**
   * Applies `ifLeft` if this is a [Left] or `ifRight` if this is a [Right].
   *
   * Example:
   * ```
   * val result: Either<Exception, Value> = possiblyFailingOperation()
   * result.fold(
   *      { log("operation failed with $it") },
   *      { log("operation succeeded with $it") }
   * )
   * ```
   *
   * @param ifLeft the function to apply if this is a [Left]
   * @param ifRight the function to apply if this is a [Right]
   * @return the results of applying the function
   */
  inline fun <C> fold(ifLeft: (A) -> C, ifRight: (B) -> C): C = when (this) {
    is Right -> ifRight(b)
    is Left -> ifLeft(a)
  }

  inline fun <C> foldLeft(initial: C, rightOperation: (C, B) -> C): C =
    fix().let { either ->
      when (either) {
        is Right -> rightOperation(initial, either.b)
        is Left -> initial
      }
    }

  inline fun <C> foldRight(initial: Eval<C>, crossinline rightOperation: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().let { either ->
      when (either) {
        is Right -> Eval.defer { rightOperation(either.b, initial) }
        is Left -> initial
      }
    }

  fun <C> foldMap(MN: Monoid<C>, f: (B) -> C): C = MN.run {
    foldLeft(MN.empty()) { b, a -> b.combine(f(a)) }
  }

  inline fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fold({ f(c, it) }, { g(c, it) })

  inline fun <C> bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ f(it, c) }, { g(it, c) })

  inline fun <C> bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C): C = MN.run {
    bifoldLeft(MN.empty(), { c, a -> c.combine(f(a)) }, { c, b -> c.combine(g(b)) })
  }

  /**
   * If this is a `Left`, then return the left value in `Right` or vice versa.
   *
   * Example:
   * ```
   * Left("left").swap()   // Result: Right("left")
   * Right("right").swap() // Result: Left("right")
   * ```
   */
  fun swap(): Either<B, A> =
    fold({ Right(it) }, { Left(it) })

  /**
   * The given function is applied if this is a `Right`.
   *
   * Example:
   * ```
   * Right(12).map { "flower" } // Result: Right("flower")
   * Left(12).map { "flower" }  // Result: Left(12)
   * ```
   */
  inline fun <C> map(f: (B) -> C): Either<A, C> =
    flatMap { Right(f(it)) }

  /**
   * The given function is applied if this is a `Left`.
   *
   * Example:
   * ```
   * Right(12).mapLeft { "flower" } // Result: Right(12)
   * Left(12).mapLeft { "flower" }  // Result: Left("flower)
   * ```
   */
  inline fun <C> mapLeft(f: (A) -> C): Either<C, B> =
    fold({ Left(f(it)) }, { Right(it) })

  /**
   * Map over Left and Right of this Either
   */
  inline fun <C, D> bimap(leftOperation: (A) -> C, rightOperation: (B) -> D): Either<C, D> =
    fold({ Left(leftOperation(it)) }, { Right(rightOperation(it)) })

  /**
   * Returns `false` if [Left] or returns the result of the application of
   * the given predicate to the [Right] value.
   *
   * Example:
   * ```
   * Right(12).exists { it > 10 } // Result: true
   * Right(7).exists { it > 10 }  // Result: false
   *
   * val left: Either<Int, Int> = Left(12)
   * left.exists { it > 10 }      // Result: false
   * ```
   */
  inline fun exists(predicate: (B) -> Boolean): Boolean =
    fold({ false }, predicate)

  /**
   * Returns a [Some] containing the [Right] value
   * if it exists or a [None] if this is a [Left].
   *
   * Example:
   * ```
   * Right(12).toOption() // Result: Some(12)
   * Left(12).toOption()  // Result: None
   * ```
   */
  @Deprecated("Deprecated, use `orNull` instead", ReplaceWith("orNull()"))
  fun toOption(): Option<B> =
    fold({ None }, { Some(it) })

  /**
   * Returns the right value if it exists, otherwise null
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.Right
   * import arrow.core.Left
   *
   * //sampleStart
   * val right = Right(12).orNull() // Result: 12
   * val left = Left(12).orNull()   // Result: null
   * //sampleEnd
   * fun main() {
   *   println("right = $right")
   *   println("left = $left")
   * }
   * ```
   */
  fun orNull(): B? = fold({ null }, { it })

  /**
   *  Applies [f] to an [B] inside [Either] and returns the [Either] structure with a tuple of the [B] value and the
   *  computed [C] value as result of applying [f]
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".right().fproduct<String>({ "$it World" })
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> fproduct(f: (B) -> C): Either<A, Pair<B, C>> =
    map { b -> b to f(b) }

  /**
   *  Pairs [C] with [B] returning a Either<A, Pair<C, B>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".left().tupleLeft<String>("World")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> tupleLeft(c: C): Either<A, Pair<C, B>> =
    map { b -> c to b }

  /**
   *  Pairs [C] with [B] returning a Either<A, Pair<B, C>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".left().tupleRight<String>("World")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> tupleRight(c: C): Either<A, Pair<B, C>> =
    map { b -> b to c }

  fun replicate(n: Int): Either<A, List<B>> =
    if (n <= 0) emptyList<B>().right()
    else when (this) {
      is Left -> this
      is Right -> List(n) { this.b }.right()
    }

  inline fun <C> traverse(fa: (B) -> Iterable<C>): List<Either<A, C>> =
    fold({ emptyList() }, { fa(it).map { Right(it) } })

  inline fun <C> traverse_(fa: (B) -> Iterable<C>): List<Unit> =
    fold({ emptyList() }, { fa(it).void() })

  inline fun <AA, C> traverseValidated(fa: (B) -> Validated<AA, C>): Validated<AA, Either<A, C>> =
    when (this) {
      is Right -> fa(this.b).map { Right(it) }
      is Left -> this.valid()
    }

  inline fun <AA, C> traverseValidated_(fa: (B) -> Validated<AA, C>): Validated<AA, Unit> =
    fold({ Valid(Unit) }, { fa(it).void() })

  inline fun <AA, C> bitraverse(fe: (A) -> Iterable<AA>, fa: (B) -> Iterable<C>): List<Either<AA, C>> =
    fold({ fe(it).map { Left(it) } }, { fa(it).map { Right(it) } })

  inline fun <AA, C, D> bitraverseValidated(
    fe: (A) -> Validated<AA, C>,
    fa: (B) -> Validated<AA, D>
  ): Validated<AA, Either<C, D>> =
    fold({ fe(it).map { Left(it) } }, { fa(it).map { Right(it) } })

  inline fun findOrNull(predicate: (B) -> Boolean): B? =
    when (this) {
      is Right -> if (predicate(this.b)) this.b else null
      is Left -> null
    }

  inline fun all(predicate: (B) -> Boolean): Boolean =
    fold({ true }, predicate)

  fun isEmpty(): Boolean = isLeft

  fun isNotEmpty(): Boolean = isRight

  /**
   * The left side of the disjoint union, as opposed to the [Right] side.
   */
  @Suppress("DataClassPrivateConstructor")
  data class Left<out A> @PublishedApi internal constructor(
    @Deprecated("Use value instead", ReplaceWith("value"))
    val a: A
  ) : Either<A, Nothing>() {
    val value: A = a
    override val isLeft = true
    override val isRight = false

    override fun toString(): String = "Either.Left($a)"

    companion object {
      operator fun <A> invoke(a: A): Either<A, Nothing> = Left(a)
    }
  }

  /**
   * The right side of the disjoint union, as opposed to the [Left] side.
   */
  @Suppress("DataClassPrivateConstructor")
  data class Right<out B> @PublishedApi internal constructor(
    @Deprecated("Use value instead", ReplaceWith("value"))
    val b: B
  ) : Either<Nothing, B>() {
    val value: B = b
    override val isLeft = false
    override val isRight = true

    override fun toString(): String = "Either.Right($b)"

    companion object {
      operator fun <B> invoke(b: B): Either<Nothing, B> = Right(b)
    }
  }

  @Deprecated(ShowDeprecation)
  fun show(SL: Show<A>, SR: Show<B>): String = fold(
    { "Left(${SL.run { it.show() }})" },
    { "Right(${SR.run { it.show() }})" }
  )

  override fun toString(): String = fold(
    { "Either.Left($it)" },
    { "Either.Right($it)" }
  )

  fun toValidatedNel(): ValidatedNel<A, B> =
    fold({ Validated.invalidNel(it) }, ::Valid)

  fun toValidated(): Validated<A, B> =
    fold({ it.invalid() }, { it.valid() })

  companion object {

    fun <L> left(left: L): Either<L, Nothing> = Left(left)

    fun <R> right(right: R): Either<Nothing, R> = Right(right)

    val leftUnit: Either<Unit, Nothing> = left(Unit)

    val unit: Either<Nothing, Unit> = right(Unit)

    fun <L> unit(): Either<L, Unit> = unit

    fun <A> fromNullable(a: A?): Either<Unit, A> = a?.right() ?: Unit.left()

    tailrec fun <L, A, B> tailRecM(a: A, f: (A) -> Kind<EitherPartialOf<L>, Either<A, B>>): Either<L, B> {
      val ev: Either<L, Either<A, B>> = f(a).fix()
      return when (ev) {
        is Left -> Left(ev.a)
        is Right -> {
          when (val b = ev.b) {
            is Left -> tailRecM(b.a, f)
            is Right -> Right(b.b)
          }
        }
      }
    }

    @Deprecated(
      message = "Use conditionally since the parameter order is consistent with Either class",
      replaceWith = ReplaceWith(
        "Either.conditionally(test, ifFalse, ifTrue)",
        "arrow.core.Either.conditionally"
      )
    )
    fun <L, R> cond(test: Boolean, ifTrue: () -> R, ifFalse: () -> L): Either<L, R> =
      conditionally(test, ifFalse, ifTrue)

    /**
     * Will create an [Either] from the result of evaluating the first parameter using the functions
     * provided on second and third parameters. Second parameter represents function for creating
     * an [Either.Left] in case of a false result of evaluation and third parameter will be used
     * to create a [Either.Right] in case of a true result.
     *
     * @param test expression to evaluate and build an [Either]
     * @param ifFalse function to create a [Either.Left] in case of false result of test
     * @param ifTrue function to create a [Either.Right] in case of true result of test
     *
     * @return [Either.Right] if evaluation succeed, [Either.Left] otherwise
     */
    inline fun <L, R> conditionally(test: Boolean, ifFalse: () -> L, ifTrue: () -> R): Either<L, R> =
      if (test) right(ifTrue()) else left(ifFalse())

    @Deprecated("Use the inline version. Hidden for binary compat", level = DeprecationLevel.HIDDEN)
    suspend inline fun <R> catch(f: suspend () -> R): Either<Throwable, R> =
      catch { f() }

    inline fun <R> catch(f: () -> R): Either<Throwable, R> =
      try {
        f().right()
      } catch (t: Throwable) {
        t.nonFatalOrThrow().left()
      }

    @Deprecated("Use the inline version. Hidden for binary compat", level = DeprecationLevel.HIDDEN)
    suspend inline fun <R> catchAndFlatten(f: suspend () -> Either<Throwable, R>): Either<Throwable, R> =
      catchAndFlatten { f() }

    inline fun <R> catchAndFlatten(f: () -> Either<Throwable, R>): Either<Throwable, R> =
      catch(f).fold({ it.left() }, { it })

    @Deprecated("Use the inline version. Hidden for binary compat", level = DeprecationLevel.HIDDEN)
    suspend fun <L, R> catch(fe: (Throwable) -> L, f: suspend () -> R): Either<L, R> =
      try {
        f().right()
      } catch (t: Throwable) {
        fe(t.nonFatalOrThrow()).left()
      }

    inline fun <L, R> catch(fe: (Throwable) -> L, f: () -> R): Either<L, R> =
      try {
        f().right()
      } catch (t: Throwable) {
        fe(t.nonFatalOrThrow()).left()
      }

    @Deprecated("Use the inline version. Hidden for binary compat", level = DeprecationLevel.HIDDEN)
    suspend inline fun <E, A, B> resolve(
      f: suspend () -> Either<E, A>,
      success: suspend (a: A) -> Either<Throwable, B>,
      error: suspend (e: E) -> Either<Throwable, B>,
      throwable: suspend (throwable: Throwable) -> Either<Throwable, B>,
      unrecoverableState: suspend (throwable: Throwable) -> Either<Throwable, Unit>
    ): B =
      catch { f() }
        .fold({ t: Throwable -> throwable(t) }, { it.fold({ e: E -> catchAndFlatten { error(e) } }, { a: A -> catchAndFlatten { success(a) } }) })
        .fold({ t: Throwable -> throwable(t) }, { b: B -> b.right() })
        .fold({ t: Throwable -> unrecoverableState(t); throw t }, { b: B -> b })

    /**
     * The resolve function can resolve any suspended function that yields an Either into one type of value.
     *
     * @param f the function that needs to be resolved.
     * @param success the function to apply if [f] yields a success of type [A].
     * @param error the function to apply if [f] yields an error of type [E].
     * @param throwable the function to apply if [f] throws a [Throwable].
     * Throwing any [Throwable] in the [throwable] function will render the [resolve] function nondeterministic.
     * @param unrecoverableState the function to apply if [resolve] is in an unrecoverable state.
     * @return the result of applying the [resolve] function.
     */
    inline fun <E, A, B> resolve(
      f: () -> Either<E, A>,
      success: (a: A) -> Either<Throwable, B>,
      error: (e: E) -> Either<Throwable, B>,
      throwable: (throwable: Throwable) -> Either<Throwable, B>,
      unrecoverableState: (throwable: Throwable) -> Either<Throwable, Unit>
    ): B =
      catch(f)
        .fold({ t: Throwable -> throwable(t) }, { it.fold({ e: E -> catchAndFlatten { error(e) } }, { a: A -> catchAndFlatten { success(a) } }) })
        .fold({ t: Throwable -> throwable(t) }, { b: B -> b.right() })
        .fold({ t: Throwable -> unrecoverableState(t); throw t }, { b: B -> b })

    /**
     *  Lifts a function `(B) -> C` to the [Either] structure returning a polymorphic function
     *  that can be applied over all [Either] values in the shape of Either<A, B>
     *
     *  ```kotlin:ank:playground
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
     */
    fun <A, B, C> lift(f: (B) -> C): (Either<A, B>) -> Either<A, C> =
      { it.map(f) }

    fun <A, B, C, D> lift(fa: (A) -> C, fb: (B) -> D): (Either<A, B>) -> Either<C, D> =
      { it.bimap(fa, fb) }

    inline fun <A, B, C, D> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      map: (B, C) -> D
    ): Either<A, D> =
      mapN(b, c, unit, unit, unit, unit, unit, unit, unit, unit) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

    inline fun <A, B, C, D, E> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      d: Either<A, D>,
      map: (B, C, D) -> E
    ): Either<A, E> =
      mapN(b, c, d, unit, unit, unit, unit, unit, unit, unit) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

    inline fun <A, B, C, D, E, F> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      d: Either<A, D>,
      e: Either<A, E>,
      map: (B, C, D, E) -> F
    ): Either<A, F> =
      mapN(b, c, d, e, unit, unit, unit, unit, unit, unit) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

    inline fun <A, B, C, D, E, F, G> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      d: Either<A, D>,
      e: Either<A, E>,
      f: Either<A, F>,
      map: (B, C, D, E, F) -> G
    ): Either<A, G> =
      mapN(b, c, d, e, f, unit, unit, unit, unit, unit) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

    inline fun <A, B, C, D, E, F, G, H> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      d: Either<A, D>,
      e: Either<A, E>,
      f: Either<A, F>,
      g: Either<A, G>,
      map: (B, C, D, E, F, G) -> H
    ): Either<A, H> =
      mapN(b, c, d, e, f, g, unit, unit, unit, unit) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }

    inline fun <A, B, C, D, E, F, G, H, I> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      d: Either<A, D>,
      e: Either<A, E>,
      f: Either<A, F>,
      g: Either<A, G>,
      h: Either<A, H>,
      map: (B, C, D, E, F, G, H) -> I
    ): Either<A, I> =
      mapN(b, c, d, e, f, g, h, unit, unit, unit) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }

    inline fun <A, B, C, D, E, F, G, H, I, J> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      d: Either<A, D>,
      e: Either<A, E>,
      f: Either<A, F>,
      g: Either<A, G>,
      h: Either<A, H>,
      i: Either<A, I>,
      map: (B, C, D, E, F, G, H, I) -> J
    ): Either<A, J> =
      mapN(b, c, d, e, f, g, h, i, unit, unit) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }

    inline fun <A, B, C, D, E, F, G, H, I, J, K> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      d: Either<A, D>,
      e: Either<A, E>,
      f: Either<A, F>,
      g: Either<A, G>,
      h: Either<A, H>,
      i: Either<A, I>,
      j: Either<A, J>,
      map: (B, C, D, E, F, G, H, I, J) -> K
    ): Either<A, K> =
      mapN(b, c, d, e, f, g, h, i, j, unit) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

    inline fun <A, B, C, D, E, F, G, H, I, J, K, L> mapN(
      b: Either<A, B>,
      c: Either<A, C>,
      d: Either<A, D>,
      e: Either<A, E>,
      f: Either<A, F>,
      g: Either<A, G>,
      h: Either<A, H>,
      i: Either<A, I>,
      j: Either<A, J>,
      k: Either<A, K>,
      map: (B, C, D, E, F, G, H, I, J, K) -> L
    ): Either<A, L> =
      b.flatMap { bb ->
        c.flatMap { cc ->
          d.flatMap { dd ->
            e.flatMap { ee ->
              f.flatMap { ff ->
                g.flatMap { gg ->
                  h.flatMap { hh ->
                    i.flatMap { ii ->
                      j.flatMap { jj ->
                        k.map { kk ->
                          map(bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
  }

  /**
   *  Replaces [B] inside [Either] with [C] resulting in a Either<A, C>
   *
   *  Kind<F, A> -> Kind<F, B>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World".left().mapConst<String>("...")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> mapConst(c: C): Either<A, C> =
    map { c }

  fun void(): Either<A, Unit> =
    mapConst(Unit)

  fun hash(HL: Hash<A>, HR: Hash<B>): Int =
    fold(
      { HL.run { it.hashWithSalt(0) } },
      { HR.run { it.hashWithSalt(1) } }
    )

  fun hashWithSalt(HL: Hash<A>, HR: Hash<B>, salt: Int): Int =
    fold(
      { l -> HL.run { l.hashWithSalt(salt.hashWithSalt(0)) } },
      { r -> HR.run { r.hashWithSalt(salt.hashWithSalt(1)) } }
    )
}

fun <L> Left(left: L): Either<L, Nothing> = Left(left)

fun <R> Right(right: R): Either<Nothing, R> = Right(right)

fun <A, B> Hash.Companion.either(HA: Hash<A>, HB: Hash<B>): Hash<Either<A, B>> =
  EitherHash(HA, HB)

fun <A, B> Show.Companion.either(SA: Show<A>, SB: Show<B>): Show<Either<A, B>> =
  EitherShow(SA, SB)

fun <A, B> Semigroup.Companion.either(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Either<A, B>> =
  EitherSemigroup(SA, SB)

fun <A, B> Monoid.Companion.either(MA: Monoid<A>, MB: Monoid<B>): Monoid<Either<A, B>> =
  EitherMonoid(MA, MB)

/**
 * Binds the given function across [Either.Right].
 *
 * @param f The function to bind across [Either.Right].
 */
inline fun <A, B, C> EitherOf<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
  fix().let {
    when (it) {
      is Right -> f(it.b)
      is Left -> it
    }
  }

fun <A, B> Either<A, Either<A, B>>.flatten(): Either<A, B> =
  flatMap(::identity)

/**
 * Returns the value from this [Either.Right] or the given argument if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).getOrElse(17) // Result: 12
 * Left(12).getOrElse(17)  // Result: 17
 * ```
 */
inline fun <B> EitherOf<*, B>.getOrElse(default: () -> B): B =
  fix().fold({ default() }, ::identity)

/**
 * Returns the value from this [Either.Right] or null if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).orNull() // Result: 12
 * Left(12).orNull()  // Result: null
 * ```
 */
fun <B> EitherOf<*, B>.orNull(): B? =
  getOrElse { null }

/**
 * Returns the value from this [Either.Right] or allows clients to transform [Either.Left] to [Either.Right] while providing access to
 * the value of [Either.Left].
 *
 * Example:
 * ```
 * Right(12).getOrHandle { 17 } // Result: 12
 * Left(12).getOrHandle { it + 5 } // Result: 17
 * ```
 */
inline fun <A, B> EitherOf<A, B>.getOrHandle(default: (A) -> B): B =
  fix().fold({ default(it) }, ::identity)

/**
 * Returns [Either.Right] with the existing value of [Either.Right] if this is a [Either.Right] and the given predicate
 * holds for the right value.<br>
 *
 * Returns `Left(default)` if this is a [Either.Right] and the given predicate does not
 * hold for the right value.<br>
 *
 * Returns [Either.Left] with the existing value of [Either.Left] if this is a [Either.Left].<br>
 *
 * Example:
 * ```
 * Right(12).filterOrElse({ it > 10 }, { -1 }) // Result: Right(12)
 * Right(7).filterOrElse({ it > 10 }, { -1 })  // Result: Left(-1)
 *
 * val left: Either<Int, Int> = Left(12)
 * left.filterOrElse({ it > 10 }, { -1 })      // Result: Left(12)
 * ```
 */
inline fun <A, B> EitherOf<A, B>.filterOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
  flatMap { if (predicate(it)) Right(it) else Left(default()) }

/**
 * Returns [Either.Right] with the existing value of [Either.Right] if this is a [Either.Right] and the given
 * predicate holds for the right value.<br>
 *
 * Returns `Left(default({right}))` if this is a [Either.Right] and the given predicate does not
 * hold for the right value. Useful for error handling where 'default' returns a message with context on why the value
 * did not pass the filter<br>
 *
 * Returns [Either.Left] with the existing value of [Either.Left] if this is a [Either.Left].<br>
 *
 * Example:
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * import arrow.core.*
 *
 * Right(12).filterOrOther({ it > 10 }, { -1 })
 * ```
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * Right(7).filterOrOther({ it > 10 }, { "Value '$it' not greater than 10" })
 * ```
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * val left: Either<Int, Int> = Left(12)
 * left.filterOrOther({ it > 10 }, { -1 })
 * ```
 */
inline fun <A, B> EitherOf<A, B>.filterOrOther(predicate: (B) -> Boolean, default: (B) -> A): Either<A, B> =
  flatMap {
    if (predicate(it)) Right(it)
    else Left(default(it))
  }

/**
 * Returns the value from this [Either.Right] or [Either.Left].
 *
 * Example:
 * ```
 * Right(12).merge() // Result: 12
 * Left(12).merge() // Result: 12
 * ```
 */
inline fun <A> EitherOf<A, A>.merge(): A =
  fix().fold(::identity, ::identity)

/**
 * Returns [Either.Right] with the existing value of [Either.Right] if this is an [Either.Right] with a non-null value.
 * The returned Either.Right type is not nullable.
 *
 * Returns `Left(default())` if this is an [Either.Right] and the existing value is null
 *
 * Returns [Either.Left] with the existing value of [Either.Left] if this is an [Either.Left].
 *
 * Example:
 * ```
 * Right(12).leftIfNull({ -1 })   // Result: Right(12)
 * Right(null).leftIfNull({ -1 }) // Result: Left(-1)
 *
 * Left(12).leftIfNull({ -1 })    // Result: Left(12)
 * ```
 */
inline fun <A, B> EitherOf<A, B?>.leftIfNull(default: () -> A): Either<A, B> =
  fix().flatMap { it.rightIfNotNull { default() } }

/**
 * Returns `true` if this is a [Either.Right] and its value is equal to `elem` (as determined by `==`),
 * returns `false` otherwise.
 *
 * Example:
 * ```
 * Right("something").contains("something") // Result: true
 * Right("something").contains("anything")  // Result: false
 * Left("something").contains("something")  // Result: false
 *  ```
 *
 * @param elem the element to test.
 * @return `true` if the option has an element that is equal (as determined by `==`) to `elem`, `false` otherwise.
 */
fun <A, B> EitherOf<A, B>.contains(elem: B): Boolean =
  fix().exists { it == elem }

fun <A, B, C> EitherOf<A, B>.ap(ff: EitherOf<A, (B) -> C>): Either<A, C> =
  flatMap { a -> ff.fix().map { f -> f(a) } }

fun <A, B, C> Either<A, B>.apEval(ff: Eval<Either<A, (B) -> C>>): Eval<Either<A, C>> =
  ff.map { this.ap(it) }

fun <A, B> EitherOf<A, B>.combineK(y: EitherOf<A, B>): Either<A, B> =
  when (this) {
    is Left -> y.fix()
    else -> fix()
  }

fun <A> A.left(): Either<A, Nothing> = Left(this)

fun <A> A.right(): Either<Nothing, A> = Right(this)

/**
 * Returns [Either.Right] if the value of type B is not null, otherwise the specified A value wrapped into an
 * [Either.Left].
 *
 * Example:
 * ```
 * "value".rightIfNotNull { "left" } // Right(b="value")
 * null.rightIfNotNull { "left" }    // Left(a="left")
 * ```
 */
inline fun <A, B> B?.rightIfNotNull(default: () -> A): Either<A, B> = when (this) {
  null -> Left(default())
  else -> Right(this)
}

/**
 * Returns [Either.Right] if the value of type Any? is null, otherwise the specified A value wrapped into an
 * [Either.Left].
 */
inline fun <A> Any?.rightIfNull(default: () -> A): Either<A, Nothing?> = when (this) {
  null -> Right(null)
  else -> Left(default())
}

/**
 * Applies the given function `f` if this is a [Left], otherwise returns this if this is a [Right].
 * This is like `flatMap` for the exception.
 */
inline fun <A, B, C> EitherOf<A, B>.handleErrorWith(f: (A) -> EitherOf<C, B>): Either<C, B> =
  when (val either = fix()) {
    is Left -> f(either.a).fix()
    is Right -> either
  }

inline fun <A, B> Either<A, B>.handleError(f: (A) -> B): Either<A, B> =
  when (this) {
    is Left -> f(a).right()
    is Right -> this
  }

inline fun <A, B, C> Either<A, B>.redeem(fe: (A) -> C, fa: (B) -> C): Either<A, C> =
  when (this) {
    is Left -> fe(a).right()
    is Right -> map(fa)
  }

operator fun <A : Comparable<A>, B : Comparable<B>> Either<A, B>.compareTo(other: Either<A, B>): Int =
  fold(
    { a1 -> other.fold({ a2 -> a1.compareTo(a2) }, { -1 }) },
    { b1 -> other.fold({ 1 }, { b2 -> b1.compareTo(b2) }) }
  )

fun <A, B> Either<A, B>.combine(SGA: Semigroup<A>, SGB: Semigroup<B>, b: Either<A, B>): Either<A, B> =
  when (this) {
    is Left -> when (b) {
      is Left -> Left(SGA.run { a.combine(b.a) })
      is Right -> this
    }
    is Right -> when (b) {
      is Left -> b
      is Right -> Either.right(SGB.run { this@combine.b.combine(b.b) })
    }
  }

fun <A, B> Iterable<Either<A, B>>.combineAll(MA: Monoid<A>, MB: Monoid<B>): Either<A, B> =
  fold(Right(MB.empty()) as Either<A, B>) { acc, e ->
    acc.combine(MA, MB, e)
  }

/**
 * Given [B] is a sub type of [C], re-type this value from Either<A, B> to Either<A, B>
 *
 * ```kotlin:ank:playground:extension
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val string: Either<Int, String> = "Hello".left()
 *   val chars: Either<Int, CharSequence> =
 *     string.widen<Int, CharSequence, String>()
 *   //sampleEnd
 *   println(chars)
 * }
 * ```
 */
fun <A, C, B : C> Either<A, B>.widen(): Either<A, C> =
  this

fun <AA, A : AA, B> Either<A, B>.leftWiden(): Either<AA, B> =
  this

fun <A, B, C, D> Either<A, B>.zip(fb: Either<A, C>, f: (B, C) -> D): Either<A, D> =
  flatMap { b ->
    fb.map { c -> f(b, c) }
  }

fun <A, B, C> Either<A, B>.zip(fb: Either<A, C>): Either<A, Pair<B, C>> =
  flatMap { a ->
    fb.map { b -> Pair(a, b) }
  }

fun <A, B, C, Z> Either<A, B>.zipEval(fb: Eval<Either<A, C>>, f: (B, C) -> Z): Eval<Either<A, Z>> =
  fb.map { zip(it, f) }

fun <A, B> Either<A, B>.replicate(n: Int, MB: Monoid<B>): Either<A, B> =
  if (n <= 0) MB.empty().right()
  else MB.run {
    when (this@replicate) {
      is Left -> this@replicate
      is Right -> List(n) { this@replicate.b }.combineAll().right()
    }
  }

inline fun <A, B, C> Either<A, B>.mproduct(f: (B) -> Either<A, C>): Either<A, Pair<B, C>> =
  flatMap { a ->
    f(a).map { b -> a to b }
  }

inline fun <A, B> Either<A, Boolean>.ifM(ifTrue: () -> Either<A, B>, ifFalse: () -> Either<A, B>): Either<A, B> =
  flatMap { if (it) ifTrue() else ifFalse() }

fun <A, B, C> Either<A, Either<B, C>>.selectM(f: Either<A, (B) -> C>): Either<A, C> =
  flatMap { it.fold({ a -> f.map { ff -> ff(a) } }, { b -> b.right() }) }

inline fun <A, B> Either<A, B>.ensure(error: () -> A, predicate: (B) -> Boolean): Either<A, B> =
  when (this) {
    is Right -> if (predicate(this.b)) this else error().left()
    is Left -> this
  }

inline fun <A, B, C, D> Either<A, B>.redeemWith(fa: (A) -> Either<C, D>, fb: (B) -> Either<C, D>): Either<C, D> =
  when (this) {
    is Left -> fa(this.a)
    is Right -> fb(this.b)
  }

fun <A, B> Either<A, Iterable<B>>.sequence(): List<Either<A, B>> =
  traverse(::identity)

fun <A, B> Either<A, Iterable<B>>.sequence_(): List<Unit> =
  traverse_(::identity)

fun <A, B, C> Either<A, Validated<B, C>>.sequenceValidated(): Validated<B, Either<A, C>> =
  traverseValidated(::identity)

fun <A, B, C> Either<A, Validated<B, C>>.sequenceValidated_(): Validated<B, Unit> =
  traverseValidated_(::identity)

fun <A, B> Either<Iterable<A>, Iterable<B>>.bisequence(): List<Either<A, B>> =
  bitraverse(::identity, ::identity)

fun <A, B, C> Either<Validated<A, B>, Validated<A, C>>.bisequenceValidated(): Validated<A, Either<B, C>> =
  bitraverseValidated(::identity, ::identity)

private class EitherHash<L, R>(
  private val HL: Hash<L>,
  private val HR: Hash<R>
) : Hash<Either<L, R>> {
  override fun Either<L, R>.hash(): Int =
    hash(HL, HR)

  override fun Either<L, R>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HL, HR, salt)
}

private class EitherShow<L, R>(
  private val SL: Show<L>,
  private val SR: Show<R>
) : Show<Either<L, R>> {
  override fun Either<L, R>.show(): String =
    show(SL, SR)
}

private open class EitherSemigroup<L, R>(
  private val SGL: Semigroup<L>,
  private val SGR: Semigroup<R>
) : Semigroup<Either<L, R>> {

  override fun Either<L, R>.combine(b: Either<L, R>): Either<L, R> =
    combine(SGL, SGR, b)

  override fun Either<L, R>.maybeCombine(b: Either<L, R>?): Either<L, R> =
    b?.let { combine(SGL, SGR, it) } ?: this
}

private class EitherMonoid<L, R>(
  private val MOL: Monoid<L>,
  private val MOR: Monoid<R>
) : Monoid<Either<L, R>>, EitherSemigroup<L, R>(MOL, MOR) {
  override fun empty(): Either<L, R> = Right(MOR.empty())

  override fun Collection<Either<L, R>>.combineAll(): Either<L, R> =
    combineAll(MOL, MOR)

  override fun combineAll(elems: List<Either<L, R>>): Either<L, R> =
    elems.combineAll(MOL, MOR)
}
