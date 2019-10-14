---
layout: docs
title: Either
permalink: /docs/arrow/core/either/
redirect_from:
  - /docs/datatypes/either/
video: q6HpChSq-xc
---

## Either

{:.beginner}
beginner

In day-to-day programming, it is fairly common to find ourselves writing functions that can fail.
For instance, querying a service may result in a connection issue, or some unexpected JSON response.

To communicate these errors it has become common practice to throw exceptions; however,
exceptions are not tracked in any way, shape, or form by the compiler. To see what
kind of exceptions (if any) a function may throw, we have to dig through the source code.
Then to handle these exceptions, we have to make sure we catch them at the call site. This
all becomes even more unwieldy when we try to compose exception-throwing procedures.

```kotlin:ank:playground
import arrow.core.andThen

//sampleStart
val throwsSomeStuff: (Int) -> Double = {x -> x.toDouble()}
val throwsOtherThings: (Double) -> String = {x -> x.toString()}
val moreThrowing: (String) -> List<String> = {x -> listOf(x)}
val magic = throwsSomeStuff.andThen(throwsOtherThings).andThen(moreThrowing)
//sampleEnd
fun main() {
 println ("magic = $magic")
}
```

Assume we happily throw exceptions in our code. Looking at the types of the above functions, any of them could throw any number of exceptions -- we do not know. When we compose, exceptions from any of the constituent
functions can be thrown. Moreover, they may throw the same kind of exception
(e.g. `IllegalArgumentException`) and thus it gets tricky tracking exactly where an exception came from.

How then do we communicate an error? By making it explicit in the data type we return.

## Either vs Validated

In general, `Validated` is used to accumulate errors, while `Either` is used to short-circuit a computation
upon the first error. For more information, see the `Validated` vs `Either` section of the `Validated` documentation.

By convention the right hand side of an `Either` is used to hold successful values.

```kotlin:ank:playground
import arrow.core.Either

val right: Either<String, Int> =
//sampleStart
 Either.Right(5)
//sampleEnd
fun main() {
 println(right)
}
```

```kotlin:ank:playground
import arrow.core.Either

val left: Either<String, Int> =
//sampleStart
 Either.Left("Something went wrong")
//sampleEnd
fun main() {
 println(left)
}
```
Because `Either` is right-biased, it is possible to define a Monad instance for it.

Since we only ever want the computation to continue in the case of `Right` (as captured by the right-bias nature),
we fix the left type parameter and leave the right one free.

So the map and flatMap methods are right-biased:

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.flatMap

//sampleStart
val right: Either<String, Int> = Either.Right(5)
val value = right.flatMap{ Either.Right(it + 1) }
//sampleEnd
fun main() {
 println("value = $value")
}
```

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.flatMap

//sampleStart
val left: Either<String, Int> = Either.Left("Something went wrong")
val value = left.flatMap{ Either.Right(it + 1) }
//sampleEnd
fun main() {
 println("value = $value")
}
```

## Using Either instead of exceptions

As a running example, we will have a series of functions that will:

* Parse a string into an integer
* Calculate the reciprocal
* Convert the reciprocal into a string

Using exception-throwing code, we could write something like this:

```kotlin:ank
import arrow.core.Either
import arrow.core.flatMap

//sampleStart
fun parse(s: String): Int =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt()
  else throw NumberFormatException("$s is not a valid integer.")

fun reciprocal(i: Int): Double =
  if (i == 0) throw IllegalArgumentException("Cannot take reciprocal of 0.")
  else 1.0 / i

fun stringify(d: Double): String = d.toString()
//sampleEnd
```

Instead, let's make the fact that some of our functions can fail explicit in the return type.

```kotlin:ank
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right

//sampleStart
// Either Style
fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
  if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
  else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Exception, String> =
  parse(s).flatMap { reciprocal(it) }.map { stringify(it) }
//sampleEnd
```

These calls to `parse` returns a `Left` and `Right` value

```kotlin:ank:playground
import arrow.core.Either

fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

//sampleStart
val notANumber = parse("Not a number")
val number2 = parse("2")
//sampleEnd
fun main() {
 println("notANumber = $notANumber")
 println("number2 = $number2")
}
```

Now, using combinators like `flatMap` and `map`, we can compose our functions together.

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.flatMap

fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
  if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
  else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Exception, String> =
  parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }

//sampleStart
val magic0 = magic("0")
val magic1 = magic("1")
val magicNotANumber = magic("Not a number")
//sampleEnd
fun main() {
 println("magic0 = $magic0")
 println("magic1 = $magic1")
 println("magicNotANumber = $magicNotANumber")
}
```

In the following exercise we pattern-match on every case the `Either` returned by `magic` can be in.
Note the `when` clause in the `Left` - the compiler will complain if we leave that out because it knows that
given the type `Either[Exception, String]`, there can be inhabitants of `Left` that are not
`NumberFormatException` or `IllegalArgumentException`. You should also notice that we are using
[SmartCast](https://kotlinlang.org/docs/reference/typecasts.html#smart-casts) for accessing to `Left` and `Right`
value.

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.flatMap

fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
  if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
  else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Exception, String> =
  parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }

//sampleStart
val x = magic("2")
val value = when(x) {
  is Either.Left -> when (x.a){
    is NumberFormatException -> "Not a number!"
    is IllegalArgumentException -> "Can't take reciprocal of 0!"
    else -> "Unknown error"
  }
  is Either.Right -> "Got reciprocal: ${x.b}"
}
//sampleEnd
fun main() {
 println("value = $value")
}
```

Instead of using exceptions as our error value, let's instead enumerate explicitly the things that
can go wrong in our program.

```kotlin:ank
import arrow.core.Either
import arrow.core.flatMap
//sampleStart
// Either with ADT Style

sealed class Error {
  object NotANumber : Error()
  object NoZeroReciprocal : Error()
}

fun parse(s: String): Either<Error, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(Error.NotANumber)

fun reciprocal(i: Int): Either<Error, Double> =
  if (i == 0) Either.Left(Error.NoZeroReciprocal)
  else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Error, String> =
  parse(s).flatMap{reciprocal(it)}.map{ stringify(it) }
//sampleEnd
```

For our little module, we enumerate any and all errors that can occur. Then, instead of using
exception classes as error values, we use one of the enumerated cases. Now when we pattern match,
we are able to comphrensively handle failure without resulting to an `else` branch; moreover
since Error is sealed, no outside code can add additional subtypes which we might fail to handle.

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.flatMap

sealed class Error {
 object NotANumber : Error()
 object NoZeroReciprocal : Error()
}

fun parse(s: String): Either<Error, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(Error.NotANumber)

fun reciprocal(i: Int): Either<Error, Double> =
  if (i == 0) Either.Left(Error.NoZeroReciprocal)
  else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Error, String> =
  parse(s).flatMap{ reciprocal(it) }.map{ stringify(it) }

//sampleStart
val x = magic("2")
val value = when(x) {
  is Either.Left -> when (x.a){
    is Error.NotANumber -> "Not a number!"
    is Error.NoZeroReciprocal -> "Can't take reciprocal of 0!"
  }
  is Either.Right -> "Got reciprocal: ${x.b}"
}
//sampleEnd
fun main() {
 println("value = $value")
}
```

## Syntax

Either can also map over the `left` value with `mapLeft` which is similar to map but applies on left instances.

```kotlin:ank:playground
import arrow.core.Either

//sampleStart
val r : Either<Int, Int> = Either.Right(7)
val rightMapLeft = r.mapLeft {it + 1}
val l: Either<Int, Int> = Either.Left(7)
val leftMapLeft = l.mapLeft {it + 1}
//sampleEnd
fun main() {
 println("rightMapLeft = $rightMapLeft")
 println("leftMapLeft = $leftMapLeft")
}
```

`Either<A, B>` can be transformed to `Either<B,A>` using the `swap()` method.

```kotlin:ank:playground
import arrow.core.Right
import arrow.core.Either

//sampleStart
val r: Either<String, Int> = Either.Right(7)
val swapped = r.swap()
//sampleEnd
fun main() {
 println("swapped = $swapped")
}
```

For using Either's syntax on arbitrary data types.
This will make possible to use the `left()`, `right()`, `contains()`, `getOrElse()` and `getOrHandle()` methods:

```kotlin:ank:playground
import arrow.core.right

val right7 =
//sampleStart
  7.right()
//sampleEnd
fun main() {
 println(right7)
}
```

```kotlin:ank:playground
import arrow.core.left

 val leftHello =
//sampleStart
 "hello".left()
//sampleEnd
fun main() {
 println(leftHello)
}
```

```kotlin:ank:playground
import arrow.core.right
import arrow.core.contains

//sampleStart
val x = 7.right()
val contains7 = x.contains(7)
//sampleEnd
fun main() {
 println("contains7 = $contains7")
}
```

```kotlin:ank:playground
import arrow.core.left
import arrow.core.getOrElse

//sampleStart
val x = "hello".left()
val getOr7 = x.getOrElse { 7 }
//sampleEnd
fun main() {
 println("getOr7 = $getOr7")
}
```

```kotlin:ank:playground
import arrow.core.left
import arrow.core.getOrHandle

//sampleStart
val x = "hello".left()
val value = x.getOrHandle { "$it world!" }
//sampleEnd
fun main() {
 println("value = $value")
}
```

For creating Either instance based on a predicate, use `Either.cond()` method :

```kotlin:ank:playground
import arrow.core.Either

val value =
//sampleStart
 Either.cond(true, { 42 }, { "Error" })
//sampleEnd
fun main() {
 println(value)
}
```

```kotlin:ank:playground
import arrow.core.Either

val value =
//sampleStart
 Either.cond(false, { 42 }, { "Error" })
//sampleEnd
fun main() {
 println(value)
}
```

Another operation is `fold`. This operation will extract the value from the Either, or provide a default if the value is `Left`

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.right

//sampleStart
val x : Either<Int, Int> = 7.right()
val fold = x.fold({ 1 }, { it + 3 })
//sampleEnd
fun main() {
 println("fold = $fold")
}
```

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.left

//sampleStart
val y : Either<Int, Int> = 7.left()
val fold = y.fold({ 1 }, { it + 3 })
//sampleEnd
fun main() {
 println("fold = $fold")
}
```

The `getOrHandle()` operation allows the transformation of an `Either.Left` value to a `Either.Right` using
the value of `Left`. This can be useful when a mapping to a single result type is required like `fold()` but without
the need to handle `Either.Right` case.

As an example we want to map an `Either<Throwable, Int>` to a proper HTTP status code:

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.getOrHandle

//sampleStart
val r: Either<Throwable, Int> = Either.Left(NumberFormatException())
val httpStatusCode = r.getOrHandle {
  when(it) {
    is NumberFormatException -> 400
    else -> 500
  }
}
//sampleEnd
fun main() {
 println("httpStatusCode = $httpStatusCode")
}
```

The ```leftIfNull``` operation transforms a null `Either.Right` value to the specified ```Either.Left``` value.
If the value is non-null, the value wrapped into a non-nullable ```Either.Right``` is returned (very useful to
skip null-check further down the call chain).
If the operation is called on an ```Either.Left```, the same ```Either.Left``` is returned.

See the examples below:

```kotlin:ank:playground
import arrow.core.Right
import arrow.core.leftIfNull

val value =
//sampleStart
 Right(12).leftIfNull({ -1 })
//sampleEnd
fun main() {
 println(value)
}
```

```kotlin:ank:playground
import arrow.core.Right
import arrow.core.leftIfNull

val value =
//sampleStart
 Right(null).leftIfNull({ -1 })
//sampleEnd
fun main() {
 println(value)
}
```

```kotlin:ank:playground
import arrow.core.Left
import arrow.core.leftIfNull

val value =
//sampleStart
 Left(12).leftIfNull({ -1 })
//sampleEnd
fun main() {
 println(value)
}
```

Another useful operation when working with null is `rightIfNotNull`.
If the value is null it will be transformed to the specified `Either.Left` and if its not null the type will
be wrapped to `Either.Right`.

Example:

```kotlin:ank:playground
import arrow.core.rightIfNotNull

val value =
//sampleStart
 "value".rightIfNotNull { "left" }
//sampleEnd
fun main() {
 println(value)
}
```

```kotlin:ank:playground
import arrow.core.rightIfNotNull

val value =
//sampleStart
 null.rightIfNotNull { "left" }
//sampleEnd
fun main() {
 println(value)
}
```

The inverse of `rightIfNotNull`, `rightIfNull`.
If the value is null it will be transformed to the specified `Either.right` and the type will be `Nothing?`.
If the value is not null than it will be transformed to the specified `Either.Left`.

Example:

```kotlin:ank:playground
import arrow.core.rightIfNull

val value =
//sampleStart
 "value".rightIfNull { "left" }
//sampleEnd
fun main() {
 println(value)
}
```

```kotlin:ank:playground
import arrow.core.rightIfNull

val value =
//sampleStart
 null.rightIfNull { "left" }
//sampleEnd
fun main() {
 println(value)
}
```

Arrow contains `Either` instances for many useful typeclasses that allows you to use and transform right values.
Both Option and Try don't require a type parameter with the following functions, but it is specifically used for Either.Left

[`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }})

Transforming the inner contents

```kotlin:ank:playground
import arrow.core.Right

val value =
//sampleStart
 Right(1).map{ it + 1 }
//sampleEnd
fun main() {
 println(value)
}
```

[`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }})

Computing over independent values

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.extensions.either.apply.tupled

val value =
//sampleStart
 tupled(Either.Right(1), Either.Right("a"), Either.Right(2.0))
//sampleEnd
fun main() {
 println(value)
}
```

[`Monad`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }})

Computing over dependent values ignoring absence


```kotlin:ank:playground
import arrow.core.extensions.fx
import arrow.core.Either

val value =
//sampleStart
 Either.fx<Int, Int> {
  val (a) = Either.Right(1)
  val (b) = Either.Right(1 + a)
  val (c) = Either.Right(1 + b)
  a + b + c
 }
//sampleEnd
fun main() {
 println(value)
}
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.DataType
import arrow.reflect.tcMarkdownList
import arrow.core.Either

DataType(Either::class).tcMarkdownList()
```
