---
layout: docs
title: Try
permalink: /docs/arrow/core/try/
redirect_from:
  - /docs/datatypes/try/
video: XavztYVMUqI
---

## Try

{:.beginner}
beginner

[Перевод на русский](/docs/arrow/core/try/ru)

Arrow has [lots of different types of error handling and reporting](http://arrow-kt.io/docs/patterns/error_handling/), which allows you to choose the best strategy for your situation.

For example, we have `Option` to model the absence of a value, or `Either` to model the return of a function as a type that may have been successful, or may have failed.

On the other hand, we have `Try`, which represents a computation that can result in an `A` result (as long as the computation is successful) or in an exception if something has gone wrong.

That is, there are only two possible implementations of `Try`: a `Try` instance where the operation has been successful, which is represented as `Success<A>`; or a `Try` instance where the computation has failed with a `Throwable`, which is represented as `Failure`.

With just this explanation you might think that we are talking about an `Either<Throwable, A>`, and you are not wrong. `Try` can be implemented in terms of `Either`, but its use cases are very different.

If we know that an operation could result in a failure, for example, because it is code from a library over which we have no control, or better yet, some method from the language itself. We can use `Try` as a substitute for the well-known `try-catch`, allowing us to rise to all its goodness.

The following example represents the typical case when consuming Java code, where domain errors are represented with exceptions.  

```kotlin:ank:silent
open class GeneralException: Exception()

class NoConnectionException: GeneralException()

class AuthorizationException: GeneralException()

fun checkPermissions() {
    throw AuthorizationException()
}

fun getLotteryNumbersFromCloud(): List<String> {
    throw NoConnectionException()
}

fun getLotteryNumbers(): List<String> {
    checkPermissions()

    return getLotteryNumbersFromCloud()
}
```

The traditional way to control this would be to use a `try-catch` block, as we have said before:

```kotlin:ank
try {
    getLotteryNumbers()
} catch (e: NoConnectionException) {
    //...
} catch (e: AuthorizationException) {
    //...
}
```

However, we could use `Try` to retrieve the computation result in a much cleaner way:

```kotlin:ank
import arrow.*
import arrow.core.*

val lotteryTry = Try { getLotteryNumbers() }
lotteryTry
```

By using `getOrDefault` we can give a default value to return, when the computation fails, similar to what we can also do with `Option` when there is no value:

```kotlin:ank
lotteryTry.getOrDefault { emptyList() }
```

If the underlying failure is useful to determine the default value, `getOrElse` can be used:

```kotlin:ank
lotteryTry.getOrElse { ex: Throwable -> emptyList() }
```

`getOrElse` can generally be used anywhere `getOrDefault` is used, ignoring the exception if it's not needed:

```kotlin:ank
lotteryTry.getOrElse { emptyList() }
```

If you want to perform a check on a possible success, you can use `filter` to convert successful computations in failures if conditions aren't met:

```kotlin:ank
lotteryTry.filter {
    it.size < 4
}
```

We can also use `handleError` which allow us to recover from a particular error (we receive the error and have to return a new value):

```kotlin:ank
lotteryTry.handleError { exception ->
    emptyList()
}
```

Or if you have another different computation that can also fail, you can use `handleErrorWith` to recover from an error (as you do with `handleError`, but in this case, returning a new `Try`):

```kotlin:ank
enum class Source {
    CACHE, NETWORK
}

fun getLotteryNumbers(source: Source): List<String> {
    checkPermissions()

    return getLotteryNumbersFromCloud()
}

Try { getLotteryNumbers(Source.NETWORK) }.handleErrorWith {
    Try { getLotteryNumbers(Source.CACHE) }
}
```

When you want to handle both cases of the computation you can use `fold`. With `fold` we provide two functions, one for transforming a failure into a new value, the second one to transform the success value into a new one:

```kotlin:ank
lotteryTry.fold(
    { emptyList<String>() },
    { it.filter { it.toIntOrNull() != null } })
```

When using Try, it is a common scenario to convert the returned `Try<Throwable, DomainObject>` instance to `Either<DomainError, DomainObject>`. One can use `toEither`, and than call `mapLeft` to achieve this goal:

```kotlin
sealed class DomainError(val message: String, val cause: Throwable) {
    class GeneralError(message: String, cause: Throwable) : DomainError(message, cause)
    class NoConnectionError(message: String, cause: Throwable) : DomainError(message, cause)
    class AuthorizationError(message: String, cause: Throwable) : DomainError(message, cause)
}

Try {
    getLotteryNumbersFromCloud()
}.toEither()
    .mapLeft {
        DomainError.NoConnectionError("Failed to fetch lottery numbers from cloud", it)
    }
// Left(a=DomainError$NoConnectionError@3ada9e37)
```

As the codebase grows, it is easy to recognize, that this pattern reoccurs everywhere when `Try` to `Either` conversion is being used.

To help this problem, `Try` has a convenient `toEither` implementation, which takes an `onLeft: (Throwable) -> B` parameter. If the result of the conversion from `Try` to `Either` fails, the supplied `onLeft` argument is called to supply domain specific value for the left (error) branch. Using this version, the code can be simplified to the one below:

```kotlin
Try {
    getLotteryNumbersFromCloud()
}.toEither {
    DomainError.NoConnectionError("Failed to fetch lottery numbers from cloud", it)
}
// Left(a=DomainError$NoConnectionError@574caa3f)
```

Lastly, Arrow contains `Try` instances for many useful typeclasses that allows you to use and transform fallibale values:

[`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }})

Transforming the value, if the computation is a success:

```kotlin:ank
import arrow.typeclasses.*
import arrow.core.extensions.*
import arrow.core.extensions.`try`.functor.*

Try { "3".toInt() }.map { it + 1 }
```

[`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }})

Computing over independent values:

```kotlin:ank
import arrow.core.extensions.`try`.apply.tupled
  
tupled(Try { "3".toInt() }, Try { "5".toInt() }, Try { "nope".toInt() })
```

[`Monad`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }})

Computing over dependent values ignoring failure:

```kotlin:ank
import arrow.core.extensions.fx

Try.fx {
  val (a) = Try { "3".toInt() }
  val (b) = Try { "4".toInt() }
  val (c) = Try { "5".toInt() }
  a + b + c
}
```

```kotlin:ank
Try.fx {
  val (a) = Try { "none".toInt() }
  val (b) = Try { "4".toInt() }
  val (c) = Try { "5".toInt() }
  a + b + c
}
```

Computing over dependent values that are automatically lifted to the context of `Try`:

```kotlin:ank
Try.fx {
  val a = "none".toInt()
  val b = "4".toInt()
  val c = "5".toInt()
  a + b + c
}
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.core.*

DataType(Try::class).tcMarkdownList()
```
