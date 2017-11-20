---
layout: docs
title: Try
permalink: /docs/datatypes/try/
---

## Try

Kategory has [lots of different types of error handling and reporting](http://kategory.io/docs/patterns/error_handling/), which allows you to choose the best strategy for your situation. 

For example, we have `Option` to model the absence of a value, or `Either` to model the return of a function as a type that may have been successful, or may have failed. 

On the other hand, we have `Try`, which represents a computation that can result in an `A` result (as long as the computation is successful) or in an exception if something has gone wrong. 

That is, there are only two possible implementations of `Try`: a `Try` instance where the operation has been successful, which is represented as `Success<A>`; or a `Try` instance where the computation has failed with a `Throwable`, which is represented as `Failure<A>`.

With just this explanation you might think that we are talking about an `Either<Throwable, A>`, and you are not wrong. `Try` can be implemented in terms of `Either`, but its use cases are very different.

If we know that an operation could result in a failure, for example, because it is code from a library over which we have no control, or better yet, some method from the language itself. We can use `Try` as a substitute for the well-known `try-catch`, allowing us to rise to all its goodness.

The following example represents the typical case when consuming Java code, where domain errors are represented with exceptions.  

```kotlin:ank
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
    println("You don't have connection")
} catch (e: AuthorizationException) {
    println("You don't have permission")
}
```

However, we could use `Try` to retrieve the computation result in a much cleaner way:

```kotlin:ank
import kategory.*

val lotteryTry = Try { getLotteryNumbers() }
lotteryTry
```

By using `getOrElse` we can give a default value to return, when the computation fails, similar to what we can also do with `Option` when there is no value:

```kotlin:ank
lotteryTry.getOrElse { emptyList() }
```

If you want to perform a check on a possible success, you can use `filter` to convert successful computations in failures if conditions aren't met:

```kotlin:ank
lotteryTry.filter {
    it.size < 4
}
```

We can also use `recover` which allow us to recover from a particular error (we receive the error and have to return a new value):

```kotlin:ank
lotteryTry.recover { exception ->
    emptyList()
}
```

Or if you have another different computation that can also fail, you can use `recoverWith` to recover from an error (as you do with `recover`, but in this case, returning a new `Try`):

```kotlin:ank
enum class Source {
    CACHE, NETWORK
}

fun getLotteryNumbers(source: Source): List<String> {
    checkPermissions()

    return getLotteryNumbersFromCloud()
}

Try { getLotteryNumbers(Source.NETWORK) }.recoverWith {
    Try { getLotteryNumbers(Source.CACHE) }
}
```

When you want to handle both cases of the computation you can use `fold`. With `fold` we provide two functions, one for transforming a failure into a new value, the second one to transform the success value into a new one:

```kotlin:ank
lotteryTry.fold(
    { emptyList<String>() },
    { it.filter { it.toIntOrNull() != null } })
```

Or, as we have with `recoverWith`, we can use a version of `fold` which allows us to handle both cases with functions that return a new instance of `Try`, `transform`:

```kotlin:ank
lotteryTry.transform(
    { Try { it.map { it.toInt() } } },
    { Try.pure(emptyList<Int>()) })
```

Lastly, Kategory contains `Try` instances for many useful typeclasses that allows you to use and transform fallibale values:

[`Functor`]({{ '/docs/typeclasses/functor/' | relative_url }})

Transforming the value, if the computation is a success:

```kotlin:ank
Try.functor().map(Try { "3".toInt() }, { it + 1})
```

[`Applicative`]({{ '/docs/typeclasses/applicative/' | relative_url }})

Computing over independent values:

```kotlin:ank
Try.applicative().tupled(Try { "3".toInt() }, Try { "5".toInt() }, Try { "nope".toInt() })
```

[`Monad`]({{ '/docs/_docs/typeclasses/monad/' | relative_url }})

Computing over dependent values ignoring failure:

```kotlin:ank
Try.monad().binding {
    val a = Try { "3".toInt() }.bind()
    val b = Try { "4".toInt() }.bind()
    val c = Try { "5".toInt() }.bind()

    yields(a + b + c)
}
```

```kotlin:ank
Try.monad().binding {
    val a = Try { "none".toInt() }.bind()
    val b = Try { "4".toInt() }.bind()
    val c = Try { "5".toInt() }.bind()

    yields(a + b + c)
}
```

Computing over dependent values that are automatically lifted to the context of `Try`:

```kotlin:ank
val intTry: HK<TryHK, Int> = Try.monadError().bindingE {
    val a = "none".toInt()
    val b = "4".toInt()
    val c = "5".toInt()

    yields(a + b + c)
}

intTry.ev()
```

Available Instances:

```kotlin:ank
import kategory.debug.*

showInstances<TryHK, Unit>()
```

