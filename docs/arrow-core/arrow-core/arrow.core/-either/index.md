//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)

# Either

[common]\
sealed class [Either](index.md)&lt;out [A](index.md), out [B](index.md)&gt;

In day-to-day programming, it is fairly common to find ourselves writing functions that can fail. For instance, querying a service may result in a connection issue, or some unexpected JSON response.

To communicate these errors, it has become common practice to throw exceptions; however, exceptions are not tracked in any way, shape, or form by the compiler. To see what kind of exceptions (if any) a function may throw, we have to dig through the source code. Then, to handle these exceptions, we have to make sure we catch them at the call site. This all becomes even more unwieldy when we try to compose exception-throwing procedures.

import arrow.core.andThen\
\
//sampleStart\
val throwsSomeStuff: (Int) -&gt; Double = {x -&gt; x.toDouble()}\
val throwsOtherThings: (Double) -&gt; String = {x -&gt; x.toString()}\
val moreThrowing: (String) -&gt; List&lt;String&gt; = {x -&gt; listOf(x)}\
val magic = throwsSomeStuff.andThen(throwsOtherThings).andThen(moreThrowing)\
//sampleEnd\
fun main() {\
 println ("magic = $magic")\
}<!--- KNIT example-either-01.kt -->

Assume we happily throw exceptions in our code. Looking at the types of the functions above, any could throw a number of exceptions -- we do not know. When we compose, exceptions from any of the constituent functions can be thrown. Moreover, they may throw the same kind of exception (e.g., IllegalArgumentException) and, thus, it gets tricky tracking exactly where an exception came from.

How then do we communicate an error? By making it explicit in the data type we return.

##  Either vs Validated

In general, Validated is used to accumulate errors, while Either is used to short-circuit a computation upon the first error. For more information, see the Validated vs Either section of the Validated documentation.

By convention, the right side of an Either is used to hold successful values.

import arrow.core.Either\
\
val right: Either&lt;String, Int&gt; =\
//sampleStart\
 Either.Right(5)\
//sampleEnd\
fun main() {\
 println(right)\
}<!--- KNIT example-either-02.kt -->import arrow.core.Either\
\
val left: Either&lt;String, Int&gt; =\
//sampleStart\
 Either.Left("Something went wrong")\
//sampleEnd\
fun main() {\
 println(left)\
}<!--- KNIT example-either-03.kt -->

Because Either is right-biased, it is possible to define a Monad instance for it.

Since we only ever want the computation to continue in the case of [Right](-right/index.md) (as captured by the right-bias nature), we fix the left type parameter and leave the right one free.

So, the map and flatMap methods are right-biased:

import arrow.core.Either\
import arrow.core.flatMap\
\
//sampleStart\
val right: Either&lt;String, Int&gt; = Either.Right(5)\
val value = right.flatMap{ Either.Right(it + 1) }\
//sampleEnd\
fun main() {\
 println("value = $value")\
}<!--- KNIT example-either-04.kt -->import arrow.core.Either\
import arrow.core.flatMap\
\
//sampleStart\
val left: Either&lt;String, Int&gt; = Either.Left("Something went wrong")\
val value = left.flatMap{ Either.Right(it + 1) }\
//sampleEnd\
fun main() {\
 println("value = $value")\
}<!--- KNIT example-either-05.kt -->

##  Using Either instead of exceptions

As a running example, we will have a series of functions that will:

<ul><li>Parse a string into an integer</li><li>Calculate the reciprocal</li><li>Convert the reciprocal into a string</li></ul>

Using exception-throwing code, we could write something like this:

import arrow.core.Either\
import arrow.core.flatMap\
\
//sampleStart\
fun parse(s: String): Int =\
  if (s.matches(Regex("-?[0-9]+"))) s.toInt()\
  else throw NumberFormatException("$s is not a valid integer.")\
\
fun reciprocal(i: Int): Double =\
  if (i == 0) throw IllegalArgumentException("Cannot take reciprocal of 0.")\
  else 1.0 / i\
\
fun stringify(d: Double): String = d.toString()\
//sampleEnd<!--- KNIT example-either-06.kt -->

Instead, let's make the fact that some of our functions can fail explicit in the return type.

import arrow.core.Either\
import arrow.core.flatMap\
import arrow.core.left\
import arrow.core.right\
\
//sampleStart\
// Either Style\
fun parse(s: String): Either&lt;NumberFormatException, Int&gt; =\
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())\
  else Either.Left(NumberFormatException("$s is not a valid integer."))\
\
fun reciprocal(i: Int): Either&lt;IllegalArgumentException, Double&gt; =\
  if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))\
  else Either.Right(1.0 / i)\
\
fun stringify(d: Double): String = d.toString()\
\
fun magic(s: String): Either&lt;Exception, String&gt; =\
  parse(s).flatMap { reciprocal(it) }.map { stringify(it) }\
//sampleEnd<!--- KNIT example-either-07.kt -->

These calls to parse return a [Left](-left/index.md) and [Right](-right/index.md) value

import arrow.core.Either\
\
fun parse(s: String): Either&lt;NumberFormatException, Int&gt; =\
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())\
  else Either.Left(NumberFormatException("$s is not a valid integer."))\
\
//sampleStart\
val notANumber = parse("Not a number")\
val number2 = parse("2")\
//sampleEnd\
fun main() {\
 println("notANumber = $notANumber")\
 println("number2 = $number2")\
}<!--- KNIT example-either-08.kt -->

Now, using combinators like flatMap and map, we can compose our functions together.

import arrow.core.Either\
import arrow.core.flatMap\
\
fun parse(s: String): Either&lt;NumberFormatException, Int&gt; =\
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())\
  else Either.Left(NumberFormatException("$s is not a valid integer."))\
\
fun reciprocal(i: Int): Either&lt;IllegalArgumentException, Double&gt; =\
  if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))\
  else Either.Right(1.0 / i)\
\
fun stringify(d: Double): String = d.toString()\
\
fun magic(s: String): Either&lt;Exception, String&gt; =\
  parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }\
\
//sampleStart\
val magic0 = magic("0")\
val magic1 = magic("1")\
val magicNotANumber = magic("Not a number")\
//sampleEnd\
fun main() {\
 println("magic0 = $magic0")\
 println("magic1 = $magic1")\
 println("magicNotANumber = $magicNotANumber")\
}<!--- KNIT example-either-09.kt -->

In the following exercise, we pattern-match on every case in which the Either returned by magic can be in. Note the when clause in the [Left](-left/index.md) - the compiler will complain if we leave that out because it knows that, given the type Either[Exception, String], there can be inhabitants of [Left](-left/index.md) that are not NumberFormatException or IllegalArgumentException. You should also notice that we are using [SmartCast](https://kotlinlang.org/docs/reference/typecasts.html#smart-casts) for accessing [Left](-left/index.md) and [Right](-right/index.md) values.

import arrow.core.Either\
import arrow.core.flatMap\
\
fun parse(s: String): Either&lt;NumberFormatException, Int&gt; =\
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())\
  else Either.Left(NumberFormatException("$s is not a valid integer."))\
\
fun reciprocal(i: Int): Either&lt;IllegalArgumentException, Double&gt; =\
  if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))\
  else Either.Right(1.0 / i)\
\
fun stringify(d: Double): String = d.toString()\
\
fun magic(s: String): Either&lt;Exception, String&gt; =\
  parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }\
\
//sampleStart\
val x = magic("2")\
val value = when(x) {\
  is Either.Left -&gt; when (x.value) {\
    is NumberFormatException -&gt; "Not a number!"\
    is IllegalArgumentException -&gt; "Can't take reciprocal of 0!"\
    else -&gt; "Unknown error"\
  }\
  is Either.Right -&gt; "Got reciprocal: ${x.value}"\
}\
//sampleEnd\
fun main() {\
 println("value = $value")\
}<!--- KNIT example-either-10.kt -->

Instead of using exceptions as our error value, let's instead enumerate explicitly the things that can go wrong in our program.

import arrow.core.Either\
import arrow.core.flatMap\
//sampleStart\
// Either with ADT Style\
\
sealed class Error {\
  object NotANumber : Error()\
  object NoZeroReciprocal : Error()\
}\
\
fun parse(s: String): Either&lt;Error, Int&gt; =\
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())\
  else Either.Left(Error.NotANumber)\
\
fun reciprocal(i: Int): Either&lt;Error, Double&gt; =\
  if (i == 0) Either.Left(Error.NoZeroReciprocal)\
  else Either.Right(1.0 / i)\
\
fun stringify(d: Double): String = d.toString()\
\
fun magic(s: String): Either&lt;Error, String&gt; =\
  parse(s).flatMap{reciprocal(it)}.map{ stringify(it) }\
//sampleEnd<!--- KNIT example-either-11.kt -->

For our little module, we enumerate any and all errors that can occur. Then, instead of using exception classes as error values, we use one of the enumerated cases. Now, when we pattern match, we are able to comphrensively handle failure without resulting in an else branch; moreover, since Error is sealed, no outside code can add additional subtypes that we might fail to handle.

import arrow.core.Either\
import arrow.core.flatMap\
\
sealed class Error {\
 object NotANumber : Error()\
 object NoZeroReciprocal : Error()\
}\
\
fun parse(s: String): Either&lt;Error, Int&gt; =\
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())\
  else Either.Left(Error.NotANumber)\
\
fun reciprocal(i: Int): Either&lt;Error, Double&gt; =\
  if (i == 0) Either.Left(Error.NoZeroReciprocal)\
  else Either.Right(1.0 / i)\
\
fun stringify(d: Double): String = d.toString()\
\
fun magic(s: String): Either&lt;Error, String&gt; =\
  parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }\
\
//sampleStart\
val x = magic("2")\
val value = when(x) {\
  is Either.Left -&gt; when (x.value) {\
    is Error.NotANumber -&gt; "Not a number!"\
    is Error.NoZeroReciprocal -&gt; "Can't take reciprocal of 0!"\
  }\
  is Either.Right -&gt; "Got reciprocal: ${x.value}"\
}\
//sampleEnd\
fun main() {\
 println("value = $value")\
}<!--- KNIT example-either-12.kt -->

##  Either.catch exceptions

Sometimes you do need to interact with code that can potentially throw exceptions. In such cases, you should mitigate the possibility that an exception can be thrown. You can do so by using the catch function.

Example:

import arrow.core.Either\
\
//sampleStart\
fun potentialThrowingCode(): String = throw RuntimeException("Blow up!")\
\
suspend fun makeSureYourLogicDoesNotHaveSideEffects(): Either&lt;Error, String&gt; =\
  Either.catch { potentialThrowingCode() }.mapLeft { Error.SpecificError }\
//sampleEnd\
suspend fun main() {\
  println("makeSureYourLogicDoesNotHaveSideEffects().isLeft() = ${makeSureYourLogicDoesNotHaveSideEffects().isLeft()}")\
}\
\
sealed class Error {\
  object SpecificError : Error()\
}<!--- KNIT example-either-13.kt -->

##  Resolve Either into one type of value

In some cases you can not use Either as a value. For instance, when you need to respond to an HTTP request. To resolve Either into one type of value, you can use the resolve function. In the case of an HTTP endpoint you most often need to return some (framework specific) response object which holds the result of the request. The result can be expected and positive, this is the success flow. Or the result can be expected but negative, this is the error flow. Or the result can be unexpected and negative, in this case an unhandled exception was thrown. In all three cases, you want to use the same kind of response object. But probably you want to respond slightly different in each case. This can be achieved by providing specific functions for the success, error and throwable cases.

Example:

import arrow.core.Either\
import arrow.core.flatMap\
import arrow.core.left\
import arrow.core.right\
\
//sampleStart\
suspend fun httpEndpoint(request: String = "Hello?") =\
  Either.resolve(\
    f = {\
      if (request == "Hello?") "HELLO WORLD!".right()\
      else Error.SpecificError.left()\
    },\
    success = { a -&gt; handleSuccess({ a: Any -&gt; log(Level.INFO, "This is a: $a") }, a) },\
    error = { e -&gt; handleError({ e: Any -&gt; log(Level.WARN, "This is e: $e") }, e) },\
    throwable = { throwable -&gt; handleThrowable({ throwable: Throwable -&gt; log(Level.ERROR, "Log the throwable: $throwable.") }, throwable) },\
    unrecoverableState = { _ -&gt; Unit.right() }\
  )\
//sampleEnd\
suspend fun main() {\
 println("httpEndpoint().status = ${httpEndpoint().status}")\
}\
\
@Suppress("UNUSED_PARAMETER")\
suspend fun &lt;A&gt; handleSuccess(log: suspend (a: A) -&gt; Either&lt;Throwable, Unit&gt;, a: A): Either&lt;Throwable, Response&gt; =\
  Either.catch {\
    Response.Builder(HttpStatus.OK)\
      .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)\
      .body(a)\
      .build()\
  }\
\
@Suppress("UNUSED_PARAMETER")\
suspend fun &lt;E&gt; handleError(log: suspend (e: E) -&gt; Either&lt;Throwable, Unit&gt;, e: E): Either&lt;Throwable, Response&gt; =\
  createErrorResponse(HttpStatus.NOT_FOUND, ErrorResponse("$ERROR_MESSAGE_PREFIX $e"))\
\
suspend fun handleThrowable(log: suspend (throwable: Throwable) -&gt; Either&lt;Throwable, Unit&gt;, throwable: Throwable): Either&lt;Throwable, Response&gt; =\
  log(throwable)\
    .flatMap { createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponse("$THROWABLE_MESSAGE_PREFIX $throwable")) }\
\
suspend fun createErrorResponse(httpStatus: HttpStatus, errorResponse: ErrorResponse): Either&lt;Throwable, Response&gt; =\
  Either.catch {\
    Response.Builder(httpStatus)\
      .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)\
      .body(errorResponse)\
      .build()\
  }\
\
suspend fun log(level: Level, message: String): Either&lt;Throwable, Unit&gt; =\
  Unit.right() // Should implement logging.\
\
enum class HttpStatus(val value: Int) { OK(200), NOT_FOUND(404), INTERNAL_SERVER_ERROR(500) }\
\
class Response private constructor(\
  val status: HttpStatus,\
  val headers: Map&lt;String, String&gt;,\
  val body: Any?\
) {\
\
  data class Builder(\
    val status: HttpStatus,\
    var headers: Map&lt;String, String&gt; = emptyMap(),\
    var body: Any? = null\
  ) {\
    fun header(key: String, value: String) = apply { this.headers = this.headers + mapOf&lt;String, String&gt;(key to value) }\
    fun body(body: Any?) = apply { this.body = body }\
    fun build() = Response(status, headers, body)\
  }\
}\
\
val CONTENT_TYPE = "Content-Type"\
val CONTENT_TYPE_APPLICATION_JSON = "application/json"\
val ERROR_MESSAGE_PREFIX = "An error has occurred. The error is:"\
val THROWABLE_MESSAGE_PREFIX = "An exception was thrown. The exception is:"\
sealed class Error {\
  object SpecificError : Error()\
}\
data class ErrorResponse(val errorMessage: String)\
enum class Level { INFO, WARN, ERROR }<!--- KNIT example-either-14.kt -->

There are far more use cases for the resolve function, the HTTP endpoint example is just one of them.

##  Syntax

Either can also map over the [Left](-left/index.md) value with mapLeft, which is similar to map, but applies on left instances.

import arrow.core.Either\
\
//sampleStart\
val r : Either&lt;Int, Int&gt; = Either.Right(7)\
val rightMapLeft = r.mapLeft {it + 1}\
val l: Either&lt;Int, Int&gt; = Either.Left(7)\
val leftMapLeft = l.mapLeft {it + 1}\
//sampleEnd\
fun main() {\
 println("rightMapLeft = $rightMapLeft")\
 println("leftMapLeft = $leftMapLeft")\
}<!--- KNIT example-either-15.kt -->

Either&lt;A, B&gt; can be transformed to Either&lt;B,A&gt; using the swap() method.

import arrow.core.Either.Left\
import arrow.core.Either\
\
//sampleStart\
val r: Either&lt;String, Int&gt; = Either.Right(7)\
val swapped = r.swap()\
//sampleEnd\
fun main() {\
 println("swapped = $swapped")\
}<!--- KNIT example-either-16.kt -->

For using Either's syntax on arbitrary data types. This will make possible to use the left(), right(), contains(), getOrElse() and getOrHandle() methods:

import arrow.core.right\
\
val right7 =\
//sampleStart\
  7.right()\
//sampleEnd\
fun main() {\
 println(right7)\
}<!--- KNIT example-either-17.kt -->import arrow.core.left\
\
 val leftHello =\
//sampleStart\
 "hello".left()\
//sampleEnd\
fun main() {\
 println(leftHello)\
}<!--- KNIT example-either-18.kt -->import arrow.core.right\
import arrow.core.contains\
\
//sampleStart\
val x = 7.right()\
val contains7 = x.contains(7)\
//sampleEnd\
fun main() {\
 println("contains7 = $contains7")\
}<!--- KNIT example-either-19.kt -->import arrow.core.left\
import arrow.core.getOrElse\
\
//sampleStart\
val x = "hello".left()\
val getOr7 = x.getOrElse { 7 }\
//sampleEnd\
fun main() {\
 println("getOr7 = $getOr7")\
}<!--- KNIT example-either-20.kt -->import arrow.core.left\
import arrow.core.getOrHandle\
\
//sampleStart\
val x = "hello".left()\
val value = x.getOrHandle { "$it world!" }\
//sampleEnd\
fun main() {\
 println("value = $value")\
}<!--- KNIT example-either-21.kt -->

For creating Either instance based on a predicate, use Either.conditionally() method. It will evaluate an expression passed as first parameter, in case the expression evaluates to false it will give an Either.Left&lt;L&gt; build from the second parameter. If the expression evaluates to a true it will take the third parameter and give an Either.Right&lt;R&gt;:

import arrow.core.Either\
\
val value =\
//sampleStart\
 Either.conditionally(true, { "Error" }, { 42 })\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-22.kt -->import arrow.core.Either\
\
val value =\
//sampleStart\
 Either.conditionally(false, { "Error" }, { 42 })\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-23.kt -->

Another operation is fold. This operation will extract the value from the Either, or provide a default if the value is [Left](-left/index.md)

import arrow.core.Either\
import arrow.core.right\
\
//sampleStart\
val x : Either&lt;Int, Int&gt; = 7.right()\
val fold = x.fold({ 1 }, { it + 3 })\
//sampleEnd\
fun main() {\
 println("fold = $fold")\
}<!--- KNIT example-either-24.kt -->import arrow.core.Either\
import arrow.core.left\
\
//sampleStart\
val y : Either&lt;Int, Int&gt; = 7.left()\
val fold = y.fold({ 1 }, { it + 3 })\
//sampleEnd\
fun main() {\
 println("fold = $fold")\
}<!--- KNIT example-either-25.kt -->

The getOrHandle() operation allows the transformation of an Either.Left value to a Either.Right using the value of [Left](-left/index.md). This can be useful when mapping to a single result type is required like fold(), but without the need to handle Either.Right case.

As an example, we want to map an Either&lt;Throwable, Int&gt; to a proper HTTP status code:

import arrow.core.Either\
import arrow.core.getOrHandle\
\
//sampleStart\
val r: Either&lt;Throwable, Int&gt; = Either.Left(NumberFormatException())\
val httpStatusCode = r.getOrHandle {\
  when(it) {\
    is NumberFormatException -&gt; 400\
    else -&gt; 500\
  }\
}\
//sampleEnd\
fun main() {\
 println("httpStatusCode = $httpStatusCode")\
}<!--- KNIT example-either-26.kt -->

The ``leftIfNull`` operation transforms a null Either.Right value to the specified ``Either.Left`` value. If the value is non-null, the value wrapped into a non-nullable ``Either.Right`` is returned (very useful to skip null-check further down the call chain). If the operation is called on an ``Either.Left``, the same ``Either.Left`` is returned.

See the examples below:

import arrow.core.Either.Right\
import arrow.core.leftIfNull\
\
val value =\
//sampleStart\
 Right(12).leftIfNull({ -1 })\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-27.kt -->import arrow.core.Either.Right\
import arrow.core.leftIfNull\
\
val value =\
//sampleStart\
 Right(null).leftIfNull({ -1 })\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-28.kt -->import arrow.core.Either.Left\
import arrow.core.leftIfNull\
\
val value =\
//sampleStart\
 Left(12).leftIfNull({ -1 })\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-29.kt -->

Another useful operation when working with null is rightIfNotNull. If the value is null, it will be transformed to the specified Either.Left and, if it's not null, the type will be wrapped to Either.Right.

Example:

import arrow.core.rightIfNotNull\
\
val value =\
//sampleStart\
 "value".rightIfNotNull { "left" }\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-30.kt -->import arrow.core.rightIfNotNull\
\
val value =\
//sampleStart\
 null.rightIfNotNull { "left" }\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-31.kt -->

The inverse of rightIfNotNull, rightIfNull. If the value is null it will be transformed to the specified Either.right and the type will be Nothing?. If the value is not null than it will be transformed to the specified Either.Left.

Example:

import arrow.core.rightIfNull\
\
val value =\
//sampleStart\
 "value".rightIfNull { "left" }\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-32.kt -->import arrow.core.rightIfNull\
\
val value =\
//sampleStart\
 null.rightIfNull { "left" }\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-either-33.kt -->

Arrow contains Either instances for many useful typeclasses that allows you to use and transform right values. Option does not require a type parameter with the following functions, but it is specifically used for Either.Left

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
| [Left](-left/index.md) | [common]<br>data class [Left](-left/index.md)&lt;out [A](-left/index.md)&gt;(value: [A](-left/index.md)) : [Either](index.md)&lt;[A](-left/index.md), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)&gt; <br>The left side of the disjoint union, as opposed to the [Right](-right/index.md) side. |
| [Right](-right/index.md) | [common]<br>data class [Right](-right/index.md)&lt;out [B](-right/index.md)&gt;(value: [B](-right/index.md)) : [Either](index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [B](-right/index.md)&gt; <br>The right side of the disjoint union, as opposed to the [Left](-left/index.md) side. |

## Functions

| Name | Summary |
|---|---|
| [all](all.md) | [common]<br>inline fun [all](all.md)(predicate: ([B](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [bifoldLeft](bifold-left.md) | [common]<br>inline fun &lt;[C](bifold-left.md)&gt; [bifoldLeft](bifold-left.md)(c: [C](bifold-left.md), f: ([C](bifold-left.md), [A](index.md)) -&gt; [C](bifold-left.md), g: ([C](bifold-left.md), [B](index.md)) -&gt; [C](bifold-left.md)): [C](bifold-left.md) |
| [bifoldMap](bifold-map.md) | [common]<br>inline fun &lt;[C](bifold-map.md)&gt; [bifoldMap](bifold-map.md)(MN: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[C](bifold-map.md)&gt;, f: ([A](index.md)) -&gt; [C](bifold-map.md), g: ([B](index.md)) -&gt; [C](bifold-map.md)): [C](bifold-map.md) |
| [bimap](bimap.md) | [common]<br>inline fun &lt;[C](bimap.md), [D](bimap.md)&gt; [bimap](bimap.md)(leftOperation: ([A](index.md)) -&gt; [C](bimap.md), rightOperation: ([B](index.md)) -&gt; [D](bimap.md)): [Either](index.md)&lt;[C](bimap.md), [D](bimap.md)&gt;<br>Map over Left and Right of this Either |
| [bitraverse](bitraverse.md) | [common]<br>inline fun &lt;[AA](bitraverse.md), [C](bitraverse.md)&gt; [bitraverse](bitraverse.md)(fe: ([A](index.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[AA](bitraverse.md)&gt;, fa: ([B](index.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[C](bitraverse.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Either](index.md)&lt;[AA](bitraverse.md), [C](bitraverse.md)&gt;&gt; |
| [bitraverseNullable](bitraverse-nullable.md) | [common]<br>inline fun &lt;[AA](bitraverse-nullable.md), [C](bitraverse-nullable.md)&gt; [bitraverseNullable](bitraverse-nullable.md)(fl: ([A](index.md)) -&gt; [AA](bitraverse-nullable.md)?, fr: ([B](index.md)) -&gt; [C](bitraverse-nullable.md)?): [Either](index.md)&lt;[AA](bitraverse-nullable.md), [C](bitraverse-nullable.md)&gt;? |
| [bitraverseOption](bitraverse-option.md) | [common]<br>inline fun &lt;[AA](bitraverse-option.md), [C](bitraverse-option.md)&gt; [bitraverseOption](bitraverse-option.md)(fl: ([A](index.md)) -&gt; [Option](../-option/index.md)&lt;[AA](bitraverse-option.md)&gt;, fr: ([B](index.md)) -&gt; [Option](../-option/index.md)&lt;[C](bitraverse-option.md)&gt;): [Option](../-option/index.md)&lt;[Either](index.md)&lt;[AA](bitraverse-option.md), [C](bitraverse-option.md)&gt;&gt; |
| [bitraverseValidated](bitraverse-validated.md) | [common]<br>inline fun &lt;[AA](bitraverse-validated.md), [C](bitraverse-validated.md), [D](bitraverse-validated.md)&gt; [bitraverseValidated](bitraverse-validated.md)(fe: ([A](index.md)) -&gt; [Validated](../-validated/index.md)&lt;[AA](bitraverse-validated.md), [C](bitraverse-validated.md)&gt;, fa: ([B](index.md)) -&gt; [Validated](../-validated/index.md)&lt;[AA](bitraverse-validated.md), [D](bitraverse-validated.md)&gt;): [Validated](../-validated/index.md)&lt;[AA](bitraverse-validated.md), [Either](index.md)&lt;[C](bitraverse-validated.md), [D](bitraverse-validated.md)&gt;&gt; |
| [exists](exists.md) | [common]<br>inline fun [exists](exists.md)(predicate: ([B](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns false if [Left](-left/index.md) or returns the result of the application of the given predicate to the [Right](-right/index.md) value. |
| [findOrNull](find-or-null.md) | [common]<br>inline fun [findOrNull](find-or-null.md)(predicate: ([B](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [B](index.md)? |
| [fold](fold.md) | [common]<br>inline fun &lt;[C](fold.md)&gt; [fold](fold.md)(ifLeft: ([A](index.md)) -&gt; [C](fold.md), ifRight: ([B](index.md)) -&gt; [C](fold.md)): [C](fold.md)<br>Applies ifLeft if this is a [Left](-left/index.md) or ifRight if this is a [Right](-right/index.md). |
| [foldLeft](fold-left.md) | [common]<br>inline fun &lt;[C](fold-left.md)&gt; [foldLeft](fold-left.md)(initial: [C](fold-left.md), rightOperation: ([C](fold-left.md), [B](index.md)) -&gt; [C](fold-left.md)): [C](fold-left.md) |
| [foldMap](fold-map.md) | [common]<br>fun &lt;[C](fold-map.md)&gt; [foldMap](fold-map.md)(MN: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[C](fold-map.md)&gt;, f: ([B](index.md)) -&gt; [C](fold-map.md)): [C](fold-map.md) |
| [isEmpty](is-empty.md) | [common]<br>fun [isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if [Left](-left/index.md) |
| [isLeft](is-left.md) | [common]<br>fun [isLeft](is-left.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isNotEmpty](is-not-empty.md) | [common]<br>fun [isNotEmpty](is-not-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if [Right](-right/index.md) |
| [isRight](is-right.md) | [common]<br>fun [isRight](is-right.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [map](map.md) | [common]<br>inline fun &lt;[C](map.md)&gt; [map](map.md)(f: ([B](index.md)) -&gt; [C](map.md)): [Either](index.md)&lt;[A](index.md), [C](map.md)&gt;<br>The given function is applied if this is a [Right](-right/index.md). |
| [mapLeft](map-left.md) | [common]<br>inline fun &lt;[C](map-left.md)&gt; [mapLeft](map-left.md)(f: ([A](index.md)) -&gt; [C](map-left.md)): [Either](index.md)&lt;[C](map-left.md), [B](index.md)&gt;<br>The given function is applied if this is a [Left](-left/index.md). |
| [orNone](or-none.md) | [common]<br>fun [orNone](or-none.md)(): [Option](../-option/index.md)&lt;[B](index.md)&gt; |
| [orNull](or-null.md) | [common]<br>fun [orNull](or-null.md)(): [B](index.md)?<br>Returns the right value if it exists, otherwise null |
| [replicate](replicate.md) | [common]<br>fun [replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Either](index.md)&lt;[A](index.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](index.md)&gt;&gt; |
| [swap](swap.md) | [common]<br>fun [swap](swap.md)(): [Either](index.md)&lt;[B](index.md), [A](index.md)&gt;<br>If this is a [Left](-left/index.md), then return the left value in [Right](-right/index.md) or vice versa. |
| [tap](tap.md) | [common]<br>inline fun [tap](tap.md)(f: ([B](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Either](index.md)&lt;[A](index.md), [B](index.md)&gt;<br>The given function is applied as a fire and forget effect if this is a [Right](-right/index.md). When applied the result is ignored and the original Either value is returned |
| [tapLeft](tap-left.md) | [common]<br>inline fun [tapLeft](tap-left.md)(f: ([A](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Either](index.md)&lt;[A](index.md), [B](index.md)&gt;<br>The given function is applied as a fire and forget effect if this is a [Left](-left/index.md). When applied the result is ignored and the original Either value is returned |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [toValidated](to-validated.md) | [common]<br>fun [toValidated](to-validated.md)(): [Validated](../-validated/index.md)&lt;[A](index.md), [B](index.md)&gt; |
| [toValidatedNel](to-validated-nel.md) | [common]<br>fun [toValidatedNel](to-validated-nel.md)(): [ValidatedNel](../index.md#682410975%2FClasslikes%2F-1961959459)&lt;[A](index.md), [B](index.md)&gt; |
| [traverse](traverse.md) | [common]<br>inline fun &lt;[C](traverse.md)&gt; [traverse](traverse.md)(fa: ([B](index.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[C](traverse.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Either](index.md)&lt;[A](index.md), [C](traverse.md)&gt;&gt; |
| [traverseNullable](traverse-nullable.md) | [common]<br>inline fun &lt;[C](traverse-nullable.md)&gt; [traverseNullable](traverse-nullable.md)(fa: ([B](index.md)) -&gt; [C](traverse-nullable.md)?): [Either](index.md)&lt;[A](index.md), [C](traverse-nullable.md)&gt;? |
| [traverseOption](traverse-option.md) | [common]<br>inline fun &lt;[C](traverse-option.md)&gt; [traverseOption](traverse-option.md)(fa: ([B](index.md)) -&gt; [Option](../-option/index.md)&lt;[C](traverse-option.md)&gt;): [Option](../-option/index.md)&lt;[Either](index.md)&lt;[A](index.md), [C](traverse-option.md)&gt;&gt; |
| [traverseValidated](traverse-validated.md) | [common]<br>inline fun &lt;[AA](traverse-validated.md), [C](traverse-validated.md)&gt; [traverseValidated](traverse-validated.md)(fa: ([B](index.md)) -&gt; [Validated](../-validated/index.md)&lt;[AA](traverse-validated.md), [C](traverse-validated.md)&gt;): [Validated](../-validated/index.md)&lt;[AA](traverse-validated.md), [Either](index.md)&lt;[A](index.md), [C](traverse-validated.md)&gt;&gt; |
| [void](void.md) | [common]<br>fun [void](void.md)(): [Either](index.md)&lt;[A](index.md), [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt; |

## Inheritors

| Name |
|---|
| [Either](-left/index.md) |
| [Either](-right/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [bind](../../arrow.core.computations/-result-effect/bind.md) | [common]<br>fun &lt;[A](../../arrow.core.computations/-result-effect/bind.md)&gt; [Either](index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [A](../../arrow.core.computations/-result-effect/bind.md)&gt;.[bind](../../arrow.core.computations/-result-effect/bind.md)(): [A](../../arrow.core.computations/-result-effect/bind.md) |
| [bisequence](../bisequence.md) | [common]<br>fun &lt;[A](../bisequence.md), [B](../bisequence.md)&gt; [Either](index.md)&lt;[Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](../bisequence.md)&gt;, [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](../bisequence.md)&gt;&gt;.[bisequence](../bisequence.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Either](index.md)&lt;[A](../bisequence.md), [B](../bisequence.md)&gt;&gt; |
| [bisequenceNullable](../bisequence-nullable.md) | [common]<br>fun &lt;[A](../bisequence-nullable.md), [B](../bisequence-nullable.md)&gt; [Either](index.md)&lt;[A](../bisequence-nullable.md)?, [B](../bisequence-nullable.md)?&gt;.[bisequenceNullable](../bisequence-nullable.md)(): [Either](index.md)&lt;[A](../bisequence-nullable.md), [B](../bisequence-nullable.md)&gt;? |
| [bisequenceOption](../bisequence-option.md) | [common]<br>fun &lt;[A](../bisequence-option.md), [B](../bisequence-option.md)&gt; [Either](index.md)&lt;[Option](../-option/index.md)&lt;[A](../bisequence-option.md)&gt;, [Option](../-option/index.md)&lt;[B](../bisequence-option.md)&gt;&gt;.[bisequenceOption](../bisequence-option.md)(): [Option](../-option/index.md)&lt;[Either](index.md)&lt;[A](../bisequence-option.md), [B](../bisequence-option.md)&gt;&gt; |
| [bisequenceValidated](../bisequence-validated.md) | [common]<br>fun &lt;[A](../bisequence-validated.md), [B](../bisequence-validated.md), [C](../bisequence-validated.md)&gt; [Either](index.md)&lt;[Validated](../-validated/index.md)&lt;[A](../bisequence-validated.md), [B](../bisequence-validated.md)&gt;, [Validated](../-validated/index.md)&lt;[A](../bisequence-validated.md), [C](../bisequence-validated.md)&gt;&gt;.[bisequenceValidated](../bisequence-validated.md)(): [Validated](../-validated/index.md)&lt;[A](../bisequence-validated.md), [Either](index.md)&lt;[B](../bisequence-validated.md), [C](../bisequence-validated.md)&gt;&gt; |
| [combine](../combine.md) | [common]<br>fun &lt;[A](../combine.md), [B](../combine.md)&gt; [Either](index.md)&lt;[A](../combine.md), [B](../combine.md)&gt;.[combine](../combine.md)(SGA: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[A](../combine.md)&gt;, SGB: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[B](../combine.md)&gt;, b: [Either](index.md)&lt;[A](../combine.md), [B](../combine.md)&gt;): [Either](index.md)&lt;[A](../combine.md), [B](../combine.md)&gt; |
| [combineK](../combine-k.md) | [common]<br>fun &lt;[A](../combine-k.md), [B](../combine-k.md)&gt; [Either](index.md)&lt;[A](../combine-k.md), [B](../combine-k.md)&gt;.[combineK](../combine-k.md)(y: [Either](index.md)&lt;[A](../combine-k.md), [B](../combine-k.md)&gt;): [Either](index.md)&lt;[A](../combine-k.md), [B](../combine-k.md)&gt; |
| [compareTo](../compare-to.md) | [common]<br>operator fun &lt;[A](../compare-to.md) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[A](../compare-to.md)&gt;, [B](../compare-to.md) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[B](../compare-to.md)&gt;&gt; [Either](index.md)&lt;[A](../compare-to.md), [B](../compare-to.md)&gt;.[compareTo](../compare-to.md)(other: [Either](index.md)&lt;[A](../compare-to.md), [B](../compare-to.md)&gt;): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [contains](../contains.md) | [common]<br>fun &lt;[A](../contains.md), [B](../contains.md)&gt; [Either](index.md)&lt;[A](../contains.md), [B](../contains.md)&gt;.[contains](../contains.md)(elem: [B](../contains.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if this is a [Right](-right/index.md) and its value is equal to elem (as determined by ==), returns false otherwise. |
| [ensure](../ensure.md) | [common]<br>inline fun &lt;[A](../ensure.md), [B](../ensure.md)&gt; [Either](index.md)&lt;[A](../ensure.md), [B](../ensure.md)&gt;.[ensure](../ensure.md)(error: () -&gt; [A](../ensure.md), predicate: ([B](../ensure.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Either](index.md)&lt;[A](../ensure.md), [B](../ensure.md)&gt; |
| [filterOrElse](../filter-or-else.md) | [common]<br>inline fun &lt;[A](../filter-or-else.md), [B](../filter-or-else.md)&gt; [Either](index.md)&lt;[A](../filter-or-else.md), [B](../filter-or-else.md)&gt;.[filterOrElse](../filter-or-else.md)(predicate: ([B](../filter-or-else.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), default: () -&gt; [A](../filter-or-else.md)): [Either](index.md)&lt;[A](../filter-or-else.md), [B](../filter-or-else.md)&gt;<br>Returns [Right](-right/index.md) with the existing value of [Right](-right/index.md) if this is a [Right](-right/index.md) and the given predicate holds for the right value.<br> |
| [filterOrOther](../filter-or-other.md) | [common]<br>inline fun &lt;[A](../filter-or-other.md), [B](../filter-or-other.md)&gt; [Either](index.md)&lt;[A](../filter-or-other.md), [B](../filter-or-other.md)&gt;.[filterOrOther](../filter-or-other.md)(predicate: ([B](../filter-or-other.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), default: ([B](../filter-or-other.md)) -&gt; [A](../filter-or-other.md)): [Either](index.md)&lt;[A](../filter-or-other.md), [B](../filter-or-other.md)&gt;<br>Returns [Right](-right/index.md) with the existing value of [Right](-right/index.md) if this is a [Right](-right/index.md) and the given predicate holds for the right value.<br> |
| [flatMap](../flat-map.md) | [common]<br>inline fun &lt;[A](../flat-map.md), [B](../flat-map.md), [C](../flat-map.md)&gt; [Either](index.md)&lt;[A](../flat-map.md), [B](../flat-map.md)&gt;.[flatMap](../flat-map.md)(f: ([B](../flat-map.md)) -&gt; [Either](index.md)&lt;[A](../flat-map.md), [C](../flat-map.md)&gt;): [Either](index.md)&lt;[A](../flat-map.md), [C](../flat-map.md)&gt;<br>Binds the given function across [Right](-right/index.md). |
| [flatten](../flatten.md) | [common]<br>fun &lt;[A](../flatten.md), [B](../flatten.md)&gt; [Either](index.md)&lt;[A](../flatten.md), [Either](index.md)&lt;[A](../flatten.md), [B](../flatten.md)&gt;&gt;.[flatten](../flatten.md)(): [Either](index.md)&lt;[A](../flatten.md), [B](../flatten.md)&gt; |
| [getOrElse](../get-or-else.md) | [common]<br>inline fun &lt;[B](../get-or-else.md)&gt; [Either](index.md)&lt;*, [B](../get-or-else.md)&gt;.[getOrElse](../get-or-else.md)(default: () -&gt; [B](../get-or-else.md)): [B](../get-or-else.md)<br>Returns the value from this [Right](-right/index.md) or the given argument if this is a [Left](-left/index.md). |
| [getOrHandle](../get-or-handle.md) | [common]<br>inline fun &lt;[A](../get-or-handle.md), [B](../get-or-handle.md)&gt; [Either](index.md)&lt;[A](../get-or-handle.md), [B](../get-or-handle.md)&gt;.[getOrHandle](../get-or-handle.md)(default: ([A](../get-or-handle.md)) -&gt; [B](../get-or-handle.md)): [B](../get-or-handle.md)<br>Returns the value from this [Right](-right/index.md) or allows clients to transform [Left](-left/index.md) to [Right](-right/index.md) while providing access to the value of [Left](-left/index.md). |
| [handleError](../handle-error.md) | [common]<br>inline fun &lt;[A](../handle-error.md), [B](../handle-error.md)&gt; [Either](index.md)&lt;[A](../handle-error.md), [B](../handle-error.md)&gt;.[handleError](../handle-error.md)(f: ([A](../handle-error.md)) -&gt; [B](../handle-error.md)): [Either](index.md)&lt;[A](../handle-error.md), [B](../handle-error.md)&gt; |
| [handleErrorWith](../handle-error-with.md) | [common]<br>inline fun &lt;[A](../handle-error-with.md), [B](../handle-error-with.md), [C](../handle-error-with.md)&gt; [Either](index.md)&lt;[A](../handle-error-with.md), [B](../handle-error-with.md)&gt;.[handleErrorWith](../handle-error-with.md)(f: ([A](../handle-error-with.md)) -&gt; [Either](index.md)&lt;[C](../handle-error-with.md), [B](../handle-error-with.md)&gt;): [Either](index.md)&lt;[C](../handle-error-with.md), [B](../handle-error-with.md)&gt;<br>Applies the given function f if this is a [Left](-left/index.md), otherwise returns this if this is a [Right](-right/index.md). This is like flatMap for the exception. |
| [leftIfNull](../left-if-null.md) | [common]<br>inline fun &lt;[A](../left-if-null.md), [B](../left-if-null.md)&gt; [Either](index.md)&lt;[A](../left-if-null.md), [B](../left-if-null.md)?&gt;.[leftIfNull](../left-if-null.md)(default: () -&gt; [A](../left-if-null.md)): [Either](index.md)&lt;[A](../left-if-null.md), [B](../left-if-null.md)&gt;<br>Returns [Right](-right/index.md) with the existing value of [Right](-right/index.md) if this is an [Right](-right/index.md) with a non-null value. The returned Either.Right type is not nullable. |
| [leftWiden](../left-widen.md) | [common]<br>fun &lt;[AA](../left-widen.md), [A](../left-widen.md) : [AA](../left-widen.md), [B](../left-widen.md)&gt; [Either](index.md)&lt;[A](../left-widen.md), [B](../left-widen.md)&gt;.[leftWiden](../left-widen.md)(): [Either](index.md)&lt;[AA](../left-widen.md), [B](../left-widen.md)&gt; |
| [merge](../merge.md) | [common]<br>inline fun &lt;[A](../merge.md)&gt; [Either](index.md)&lt;[A](../merge.md), [A](../merge.md)&gt;.[merge](../merge.md)(): [A](../merge.md)<br>Returns the value from this [Right](-right/index.md) or [Left](-left/index.md). |
| [orNull](../or-null.md) | [common]<br>fun &lt;[B](../or-null.md)&gt; [Either](index.md)&lt;*, [B](../or-null.md)&gt;.[orNull](../or-null.md)(): [B](../or-null.md)?<br>Returns the value from this [Right](-right/index.md) or null if this is a [Left](-left/index.md). |
| [redeem](../redeem.md) | [common]<br>inline fun &lt;[A](../redeem.md), [B](../redeem.md), [C](../redeem.md)&gt; [Either](index.md)&lt;[A](../redeem.md), [B](../redeem.md)&gt;.[redeem](../redeem.md)(fe: ([A](../redeem.md)) -&gt; [C](../redeem.md), fa: ([B](../redeem.md)) -&gt; [C](../redeem.md)): [Either](index.md)&lt;[A](../redeem.md), [C](../redeem.md)&gt; |
| [redeemWith](../redeem-with.md) | [common]<br>inline fun &lt;[A](../redeem-with.md), [B](../redeem-with.md), [C](../redeem-with.md), [D](../redeem-with.md)&gt; [Either](index.md)&lt;[A](../redeem-with.md), [B](../redeem-with.md)&gt;.[redeemWith](../redeem-with.md)(fa: ([A](../redeem-with.md)) -&gt; [Either](index.md)&lt;[C](../redeem-with.md), [D](../redeem-with.md)&gt;, fb: ([B](../redeem-with.md)) -&gt; [Either](index.md)&lt;[C](../redeem-with.md), [D](../redeem-with.md)&gt;): [Either](index.md)&lt;[C](../redeem-with.md), [D](../redeem-with.md)&gt; |
| [replicate](../replicate.md) | [common]<br>fun &lt;[A](../replicate.md), [B](../replicate.md)&gt; [Either](index.md)&lt;[A](../replicate.md), [B](../replicate.md)&gt;.[replicate](../replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MB: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[B](../replicate.md)&gt;): [Either](index.md)&lt;[A](../replicate.md), [B](../replicate.md)&gt; |
| [sequence](../sequence.md) | [common]<br>fun &lt;[A](../sequence.md), [B](../sequence.md)&gt; [Either](index.md)&lt;[A](../sequence.md), [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](../sequence.md)&gt;&gt;.[sequence](../sequence.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Either](index.md)&lt;[A](../sequence.md), [B](../sequence.md)&gt;&gt; |
| [sequenceNullable](../sequence-nullable.md) | [common]<br>fun &lt;[A](../sequence-nullable.md), [B](../sequence-nullable.md)&gt; [Either](index.md)&lt;[A](../sequence-nullable.md), [B](../sequence-nullable.md)?&gt;.[sequenceNullable](../sequence-nullable.md)(): [Either](index.md)&lt;[A](../sequence-nullable.md), [B](../sequence-nullable.md)&gt;? |
| [sequenceOption](../sequence-option.md) | [common]<br>fun &lt;[A](../sequence-option.md), [B](../sequence-option.md)&gt; [Either](index.md)&lt;[A](../sequence-option.md), [Option](../-option/index.md)&lt;[B](../sequence-option.md)&gt;&gt;.[sequenceOption](../sequence-option.md)(): [Option](../-option/index.md)&lt;[Either](index.md)&lt;[A](../sequence-option.md), [B](../sequence-option.md)&gt;&gt; |
| [sequenceValidated](../sequence-validated.md) | [common]<br>fun &lt;[A](../sequence-validated.md), [B](../sequence-validated.md), [C](../sequence-validated.md)&gt; [Either](index.md)&lt;[A](../sequence-validated.md), [Validated](../-validated/index.md)&lt;[B](../sequence-validated.md), [C](../sequence-validated.md)&gt;&gt;.[sequenceValidated](../sequence-validated.md)(): [Validated](../-validated/index.md)&lt;[B](../sequence-validated.md), [Either](index.md)&lt;[A](../sequence-validated.md), [C](../sequence-validated.md)&gt;&gt; |
| [widen](../widen.md) | [common]<br>fun &lt;[A](../widen.md), [C](../widen.md), [B](../widen.md) : [C](../widen.md)&gt; [Either](index.md)&lt;[A](../widen.md), [B](../widen.md)&gt;.[widen](../widen.md)(): [Either](index.md)&lt;[A](../widen.md), [C](../widen.md)&gt;<br>Given [B](../widen.md) is a sub type of [C](../widen.md), re-type this value from Either to Either |
| [zip](../zip.md) | [common]<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(fb: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, f: ([B](../zip.md), [C](../zip.md)) -&gt; [D](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(fb: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;): [Either](index.md)&lt;[A](../zip.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[B](../zip.md), [C](../zip.md)&gt;&gt;<br>inline fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(c: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, d: [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;, map: ([B](../zip.md), [C](../zip.md), [D](../zip.md)) -&gt; [E](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [E](../zip.md)&gt;<br>inline fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(c: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, d: [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;, e: [Either](index.md)&lt;[A](../zip.md), [E](../zip.md)&gt;, map: ([B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md)) -&gt; [F](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [F](../zip.md)&gt;<br>inline fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(c: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, d: [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;, e: [Either](index.md)&lt;[A](../zip.md), [E](../zip.md)&gt;, f: [Either](index.md)&lt;[A](../zip.md), [F](../zip.md)&gt;, map: ([B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md)) -&gt; [G](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [G](../zip.md)&gt;<br>inline fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(c: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, d: [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;, e: [Either](index.md)&lt;[A](../zip.md), [E](../zip.md)&gt;, f: [Either](index.md)&lt;[A](../zip.md), [F](../zip.md)&gt;, g: [Either](index.md)&lt;[A](../zip.md), [G](../zip.md)&gt;, map: ([B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md)) -&gt; [H](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [H](../zip.md)&gt;<br>inline fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(c: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, d: [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;, e: [Either](index.md)&lt;[A](../zip.md), [E](../zip.md)&gt;, f: [Either](index.md)&lt;[A](../zip.md), [F](../zip.md)&gt;, g: [Either](index.md)&lt;[A](../zip.md), [G](../zip.md)&gt;, h: [Either](index.md)&lt;[A](../zip.md), [H](../zip.md)&gt;, map: ([B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md)) -&gt; [I](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [I](../zip.md)&gt;<br>inline fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(c: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, d: [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;, e: [Either](index.md)&lt;[A](../zip.md), [E](../zip.md)&gt;, f: [Either](index.md)&lt;[A](../zip.md), [F](../zip.md)&gt;, g: [Either](index.md)&lt;[A](../zip.md), [G](../zip.md)&gt;, h: [Either](index.md)&lt;[A](../zip.md), [H](../zip.md)&gt;, i: [Either](index.md)&lt;[A](../zip.md), [I](../zip.md)&gt;, map: ([B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md)) -&gt; [J](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [J](../zip.md)&gt;<br>inline fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md), [K](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(c: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, d: [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;, e: [Either](index.md)&lt;[A](../zip.md), [E](../zip.md)&gt;, f: [Either](index.md)&lt;[A](../zip.md), [F](../zip.md)&gt;, g: [Either](index.md)&lt;[A](../zip.md), [G](../zip.md)&gt;, h: [Either](index.md)&lt;[A](../zip.md), [H](../zip.md)&gt;, i: [Either](index.md)&lt;[A](../zip.md), [I](../zip.md)&gt;, j: [Either](index.md)&lt;[A](../zip.md), [J](../zip.md)&gt;, map: ([B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md)) -&gt; [K](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [K](../zip.md)&gt;<br>inline fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md), [K](../zip.md), [L](../zip.md)&gt; [Either](index.md)&lt;[A](../zip.md), [B](../zip.md)&gt;.[zip](../zip.md)(c: [Either](index.md)&lt;[A](../zip.md), [C](../zip.md)&gt;, d: [Either](index.md)&lt;[A](../zip.md), [D](../zip.md)&gt;, e: [Either](index.md)&lt;[A](../zip.md), [E](../zip.md)&gt;, f: [Either](index.md)&lt;[A](../zip.md), [F](../zip.md)&gt;, g: [Either](index.md)&lt;[A](../zip.md), [G](../zip.md)&gt;, h: [Either](index.md)&lt;[A](../zip.md), [H](../zip.md)&gt;, i: [Either](index.md)&lt;[A](../zip.md), [I](../zip.md)&gt;, j: [Either](index.md)&lt;[A](../zip.md), [J](../zip.md)&gt;, k: [Either](index.md)&lt;[A](../zip.md), [K](../zip.md)&gt;, map: ([B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md), [K](../zip.md)) -&gt; [L](../zip.md)): [Either](index.md)&lt;[A](../zip.md), [L](../zip.md)&gt; |
