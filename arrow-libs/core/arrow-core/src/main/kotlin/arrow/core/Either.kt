package arrow.core

import arrow.core.Either.Companion.resolve
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

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
 * import arrow.core.Either.Right
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
 * import arrow.core.Either.Right
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
 * import arrow.core.Either.Left
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
 */
sealed class Either<out A, out B> {

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
    is Right -> ifRight(value)
    is Left -> ifLeft(value)
  }

  inline fun <C> foldLeft(initial: C, rightOperation: (C, B) -> C): C =
    when (this) {
      is Right -> rightOperation(initial, value)
      is Left -> initial
    }

  @Deprecated(FoldRightDeprecation)
  inline fun <C> foldRight(initial: Eval<C>, crossinline rightOperation: (B, Eval<C>) -> Eval<C>): Eval<C> =
    when (this) {
      is Right -> Eval.defer { rightOperation(value, initial) }
      is Left -> initial
    }

  fun <C> foldMap(MN: Monoid<C>, f: (B) -> C): C = MN.run {
    foldLeft(MN.empty()) { b, a -> b.combine(f(a)) }
  }

  inline fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fold({ f(c, it) }, { g(c, it) })

  @Deprecated(FoldRightDeprecation)
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
   * Returns the right value if it exists, otherwise null
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.Either.Left
   * import arrow.core.Either.Right
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

  fun replicate(n: Int): Either<A, List<B>> =
    if (n <= 0) emptyList<B>().right()
    else when (this) {
      is Left -> this
      is Right -> List(n) { this.value }.right()
    }

  inline fun <C> traverse(fa: (B) -> Iterable<C>): List<Either<A, C>> =
    fold({ emptyList() }, { fa(it).map { Right(it) } })

  inline fun <AA, C> traverseValidated(fa: (B) -> Validated<AA, C>): Validated<AA, Either<A, C>> =
    when (this) {
      is Right -> fa(this.value).map { Right(it) }
      is Left -> this.valid()
    }

  inline fun <AA, C> bitraverse(fe: (A) -> Iterable<AA>, fa: (B) -> Iterable<C>): List<Either<AA, C>> =
    fold({ fe(it).map { Left(it) } }, { fa(it).map { Right(it) } })

  inline fun <AA, C, D> bitraverseValidated(
    fe: (A) -> Validated<AA, C>,
    fa: (B) -> Validated<AA, D>
  ): Validated<AA, Either<C, D>> =
    fold({ fe(it).map { Left(it) } }, { fa(it).map { Right(it) } })

  inline fun findOrNull(predicate: (B) -> Boolean): B? =
    when (this) {
      is Right -> if (predicate(this.value)) this.value else null
      is Left -> null
    }

  inline fun all(predicate: (B) -> Boolean): Boolean =
    fold({ true }, predicate)

  fun isEmpty(): Boolean = isLeft

  fun isNotEmpty(): Boolean = isRight

  /**
   * The left side of the disjoint union, as opposed to the [Right] side.
   */
  data class Left<out A> constructor(val value: A) : Either<A, Nothing>() {
    override val isLeft = true
    override val isRight = false

    override fun toString(): String = "Either.Left($value)"

    companion object {
      @PublishedApi
      internal val leftUnit: Either<Unit, Nothing> =
        Left(Unit)
   }
  }

  /**
   * The right side of the disjoint union, as opposed to the [Left] side.
   */
  data class Right<out B> constructor(val value: B) : Either<Nothing, B>() {
    override val isLeft = false
    override val isRight = true

    override fun toString(): String = "Either.Right($value)"

    companion object {
      @PublishedApi
      internal val unit: Either<Nothing, Unit> = Right(Unit)
    }
  }

  override fun toString(): String = fold(
    { "Either.Left($it)" },
    { "Either.Right($it)" }
  )

  fun toValidatedNel(): ValidatedNel<A, B> =
    fold({ Validated.invalidNel(it) }, ::Valid)

  fun toValidated(): Validated<A, B> =
    fold({ it.invalid() }, { it.valid() })

  companion object {

    @JvmStatic
    fun <A> fromNullable(a: A?): Either<Unit, A> = a?.right() ?: Unit.left()

    @Deprecated(TailRecMDeprecation)
    tailrec fun <L, A, B> tailRecM(a: A, f: (A) -> Either<L, Either<A, B>>): Either<L, B> {
      return when (val ev = f(a)) {
        is Left -> Left(ev.value)
        is Right -> {
          when (val b = ev.value) {
            is Left -> tailRecM(b.value, f)
            is Right -> Right(b.value)
          }
        }
      }
    }

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
    @JvmStatic
    inline fun <L, R> conditionally(test: Boolean, ifFalse: () -> L, ifTrue: () -> R): Either<L, R> =
      if (test) Right(ifTrue()) else Left(ifFalse())

    @JvmStatic
    @JvmName("tryCatch")
    inline fun <R> catch(f: () -> R): Either<Throwable, R> =
      try {
        f().right()
      } catch (t: Throwable) {
        t.nonFatalOrThrow().left()
      }

    @JvmStatic
    @JvmName("tryCatchAndFlatten")
    inline fun <R> catchAndFlatten(f: () -> Either<Throwable, R>): Either<Throwable, R> =
      catch(f).fold({ it.left() }, { it })

    @JvmStatic
    @JvmName("tryCatch")
    inline fun <L, R> catch(fe: (Throwable) -> L, f: () -> R): Either<L, R> =
      try {
        f().right()
      } catch (t: Throwable) {
        fe(t.nonFatalOrThrow()).left()
      }

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
    @JvmStatic
    inline fun <E, A, B> resolve(
      f: () -> Either<E, A>,
      success: (a: A) -> Either<Throwable, B>,
      error: (e: E) -> Either<Throwable, B>,
      throwable: (throwable: Throwable) -> Either<Throwable, B>,
      unrecoverableState: (throwable: Throwable) -> Either<Throwable, Unit>
    ): B =
      catch(f)
        .fold(
          { t: Throwable -> throwable(t) },
          { it.fold({ e: E -> catchAndFlatten { error(e) } }, { a: A -> catchAndFlatten { success(a) } }) })
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
    @JvmStatic
    fun <A, B, C> lift(f: (B) -> C): (Either<A, B>) -> Either<A, C> =
      { it.map(f) }

    @JvmStatic
    fun <A, B, C, D> lift(fa: (A) -> C, fb: (B) -> D): (Either<A, B>) -> Either<C, D> =
      { it.bimap(fa, fb) }
  }

  fun void(): Either<A, Unit> =
    map { Unit }
}

/**
 * Binds the given function across [Either.Right].
 *
 * @param f The function to bind across [Either.Right].
 */
inline fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
  when (this) {
    is Right -> f(this.value)
    is Left -> this
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
inline fun <B> Either<*, B>.getOrElse(default: () -> B): B =
  fold({ default() }, ::identity)

/**
 * Returns the value from this [Either.Right] or null if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).orNull() // Result: 12
 * Left(12).orNull()  // Result: null
 * ```
 */
fun <B> Either<*, B>.orNull(): B? =
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
inline fun <A, B> Either<A, B>.getOrHandle(default: (A) -> B): B =
  fold({ default(it) }, ::identity)

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
inline fun <A, B> Either<A, B>.filterOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
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
 * import arrow.core.Either.Right
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
 * import arrow.core.Either.Left
 *
 * val left: Either<Int, Int> = Left(12)
 * left.filterOrOther({ it > 10 }, { -1 })
 * ```
 */
inline fun <A, B> Either<A, B>.filterOrOther(predicate: (B) -> Boolean, default: (B) -> A): Either<A, B> =
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
inline fun <A> Either<A, A>.merge(): A =
  fold(::identity, ::identity)

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
inline fun <A, B> Either<A, B?>.leftIfNull(default: () -> A): Either<A, B> =
  flatMap { it.rightIfNotNull { default() } }

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
fun <A, B> Either<A, B>.contains(elem: B): Boolean =
  exists { it == elem }

fun <A, B> Either<A, B>.combineK(y: Either<A, B>): Either<A, B> =
  when (this) {
    is Left -> y
    else -> this
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
inline fun <A, B, C> Either<A, B>.handleErrorWith(f: (A) -> Either<C, B>): Either<C, B> =
  when (this) {
    is Left -> f(this.value)
    is Right -> this
  }

inline fun <A, B> Either<A, B>.handleError(f: (A) -> B): Either<A, B> =
  when (this) {
    is Left -> f(value).right()
    is Right -> this
  }

inline fun <A, B, C> Either<A, B>.redeem(fe: (A) -> C, fa: (B) -> C): Either<A, C> =
  when (this) {
    is Left -> fe(value).right()
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
      is Left -> Left(SGA.run { value.combine(b.value) })
      is Right -> this
    }
    is Right -> when (b) {
      is Left -> b
      is Right -> Either.Right(SGB.run { this@combine.value.combine(b.value) })
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

inline fun <A, B, C, D, E> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  map: (B, C, D) -> E
): Either<A, E> =
  zip(
    c,
    d,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit
  ) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

inline fun <A, B, C, D, E, F> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  map: (B, C, D, E) -> F
): Either<A, F> =
  zip(
    c,
    d,
    e,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit
  ) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

inline fun <A, B, C, D, E, F, G> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  map: (B, C, D, E, F) -> G
): Either<A, G> =
  zip(
    c,
    d,
    e,
    f,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit,
    Right.unit
  ) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

inline fun <A, B, C, D, E, F, G, H> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  g: Either<A, G>,
  map: (B, C, D, E, F, G) -> H
): Either<A, H> =
  zip(c, d, e, f, g, Right.unit, Right.unit, Right.unit, Right.unit) { b, c, d, e, f, g, _, _, _, _ ->
    map(
      b,
      c,
      d,
      e,
      f,
      g
    )
  }

inline fun <A, B, C, D, E, F, G, H, I> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  g: Either<A, G>,
  h: Either<A, H>,
  map: (B, C, D, E, F, G, H) -> I
): Either<A, I> =
  zip(c, d, e, f, g, h, Right.unit, Right.unit, Right.unit) { b, c, d, e, f, g, h, _, _, _ ->
    map(
      b,
      c,
      d,
      e,
      f,
      g,
      h
    )
  }

inline fun <A, B, C, D, E, F, G, H, I, J> Either<A, B>.zip(
  c: Either<A, C>,
  d: Either<A, D>,
  e: Either<A, E>,
  f: Either<A, F>,
  g: Either<A, G>,
  h: Either<A, H>,
  i: Either<A, I>,
  map: (B, C, D, E, F, G, H, I) -> J
): Either<A, J> =
  zip(c, d, e, f, g, h, i, Right.unit, Right.unit) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }

inline fun <A, B, C, D, E, F, G, H, I, J, K> Either<A, B>.zip(
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
  zip(c, d, e, f, g, h, i, j, Right.unit) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Either<A, B>.zip(
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
  flatMap { bb ->
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

fun <A, B> Either<A, B>.replicate(n: Int, MB: Monoid<B>): Either<A, B> =
  if (n <= 0) MB.empty().right()
  else MB.run {
    when (this@replicate) {
      is Left -> this@replicate
      is Right -> List(n) { this@replicate.value }.combineAll().right()
    }
  }

inline fun <A, B> Either<A, B>.ensure(error: () -> A, predicate: (B) -> Boolean): Either<A, B> =
  when (this) {
    is Right -> if (predicate(this.value)) this else error().left()
    is Left -> this
  }

inline fun <A, B, C, D> Either<A, B>.redeemWith(fa: (A) -> Either<C, D>, fb: (B) -> Either<C, D>): Either<C, D> =
  when (this) {
    is Left -> fa(this.value)
    is Right -> fb(this.value)
  }

fun <A, B> Either<A, Iterable<B>>.sequence(): List<Either<A, B>> =
  traverse(::identity)

fun <A, B, C> Either<A, Validated<B, C>>.sequenceValidated(): Validated<B, Either<A, C>> =
  traverseValidated(::identity)

fun <A, B> Either<Iterable<A>, Iterable<B>>.bisequence(): List<Either<A, B>> =
  bitraverse(::identity, ::identity)

fun <A, B, C> Either<Validated<A, B>, Validated<A, C>>.bisequenceValidated(): Validated<A, Either<B, C>> =
  bitraverseValidated(::identity, ::identity)
