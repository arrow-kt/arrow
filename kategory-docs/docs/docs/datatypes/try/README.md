---
layout: docs
title: Try
permalink: /docs/datatypes/try/
---

## Try

Kategory has a lots of different types of error handling and reporting, which can make it difficult to decide which one is best for your situation. 

For example, we have `Option` to model the absence of a value, or `Either` to model the return of a function as a type that may have been successful, or may have failed. 

On the other hand, we have `Try`, which represents a computation that can result in an `A` result (as long as the computation is successful) or in an exception if something has gone wrong. 

That is, there are only two possible implementations of `Try`: a `Try` instance where the operation has been successful, which is represented as `Success<A>`; or a `Try` instance where the computation has failed with a `Throwable`, which is represented as `Failure<A>`.

With just this explanation you might think that we are talking about an `Either<Throwable, A>`, and you are not wrong. `Try` can be implemented in terms of `Either`, but its use cases are very different.

If we know that an operation could result in a failure, for example, because it is code from a library over which we have no control, or better yet, some method from the language itself. We can use `Try` as a substitute for the well-known `try-catch`, allowing us to rise to all its goodness.

For example, if we try to parse a `String` to `Int` using the `String` method, the computation would fail with a `NumberFormatException` if the string is not a valid number:

```kotlin:ank
"3".toInt()
"nope".toInt()
```

The traditional way to control this would be to use a `try-catch` block, as we have said before:

```kotlin:ank
try {
    "nope".toInt()
} catch (exception: NumberFormatException) {
    // string is not a number
}
```

However, we could use `Try` to retrieve the computation result in a much cleaner way:

```kotlin:ank
import kategory.Try

val intTry: Try<Int> = Try { "nope".toInt() }
intTry
```

By using `getOrElse` we can give a default value to return, when the computation fails, similar to what we can also do with `Option` when there is no value:

```kotlin:ank
intTry.getOrElse { 0 }
```

We can also use `recover` and `recoverWith` which allow us to recover from a particular error (we receive the error and have to return a new value or a new `Try`, respectively):

```kotlin:ank
intTry.recover {
    when(it) {
        is NumberFormatException -> 0
        else -> 42
    }
}

intTry.recoverWith {
    Try { "42".toInt() }
}
```

On the other hand, you can use `Try` with `when` expressions to disambiguate the type:

```kotlin:ank
val failure: Try<Int> = Try { "nope".toInt() }

val value = when(failure) {
    is Try.Success -> failure.value
    is Try.Failure -> 0
}
value
```

```kotlin:ank
val success: Try<Int> = Try { "3".toInt() }

val value = when(success) {
    is Try.Success -> success.value
    is Try.Failure -> 0
}
value
```

Kategory contains `Try` instances for many useful typeclasses that allows you to use and transform optional values

[`Functor`]({{ '/docs/typeclasses/functor/' | relative_url }})

Transforming the value, if the computation is a success

```kotlin:ank
Try.functor().map(Try { "3".toInt() }, { it + 1})
```

[`Applicative`]({{ '/docs/typeclasses/applicative/' | relative_url }})

Computing over independent values

```kotlin:ank
Try.applicative().tupled(Try { "3".toInt() }, Try { "5".toInt() }, Try { "nope".toInt() })
```

[`Monad`]({{ '/docs/_docs/typeclasses/monad/' | relative_url }})

Computing over dependent values ignoring failure

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

Available Instances:

```kotlin:ank
import kategory.debug.*

showInstances<TryHK, Unit>()
```

