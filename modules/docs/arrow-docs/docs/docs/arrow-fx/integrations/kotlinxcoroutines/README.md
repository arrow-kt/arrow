---
layout: docs-core
title: kotlinx.coroutines
permalink: /docs/integrations/kotlinxcoroutines/
---

# Kotlin Coroutines and runtime support

Kotlin offers a `suspend` system in the language, and it offers intrinsics in the standard library to build a library on top. These `intrinsic` functions allow you to `startCoroutine`s, `suspendCoroutine`s, build `CoroutineContext`s and so on.

Kotlin's language suspension support can be found in the [kotlin.coroutines](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/index.html) package.

There are currently two libraries that provide a runtime for the language's suspension system.

- [Arrow Fx](https://arrow-kt.io/docs/fx/)
- [KotlinX Coroutines](https://github.com/Kotlin/kotlinx.coroutines)

They can easily interop with each other, and Arrow Fx's integration module offers certain combinators to use Arrow Fx's with KotlinX structured concurrency in frameworks that have chosen to incorporate the KotlinX Coroutines library such as Android and Ktor.

Due to this differentiation, there are different alternatives considering each tool and runtime:

| Tool →<br>Runtime ↓ | IO                                          | suspend function    |
|--------------------|----------------------------------------------|---------------------|
| KotlinX Coroutines | `suspended`<br>`suspendCancellable`*         | `async`<br>`launch` |
| Arrow              | `unsafeRunAsync`<br>`unsafeRunAsyncCancellable`<br>`unsafeRunAsyncScoped`* | Has to be wrapped in `IO.effect` and then run with any of the operations on the left.<br><br>Alternatively, it can be wrapped with `Fx.effect` and be executed [polymorphically](/docs/fx/polymorphism/). |

The * marked ops are available within this integration module, offering different alternatives depending on the project's needs. Both options offered in this integration module are described below:

## Integrating Coroutines with IO

If you'd like to introduce `IO` in your project, you might want to keep using the Coroutines style of cancellation with scopes. This is especially useful on *Android* projects where the Architecture Components [already provide handy scopes for you](https://developer.android.com/topic/libraries/architecture/coroutines#lifecycle-aware).

### unsafeRunScoped & unsafeRunIO

`IO.unsafeRunScoped(scope, cb)` runs the specific `IO` with a [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html), so it will be automatically canceled when the scope does as well.

Similarly, there's `scope.unsafeRunIO(IO, cb)`, which works in the same way with different syntax:

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.fx
import arrow.integrations.kotlinx.unsafeRunScoped
import arrow.integrations.kotlinx.unsafeRunIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val scope = CoroutineScope(SupervisorJob())

//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

fun greet(): IO<Unit> =
  IO.fx {
    !effect { sayHello() }
    !effect { sayGoodBye() }
  }

fun main() {
  // This IO would stop as soon as the scope is cancelled
  greet().unsafeRunScoped(scope) { }

  // alternatively, you could also do
  scope.unsafeRunIO(greet()) { }
}
//sampleEnd
```


## Alternatively, integrating IO with kotlinx.coroutines

Sometimes you might not want to switch the runtime of your project, and slowly integrate `IO` instead. For this use case, we've added some extensions to make `IO` work with the KotlinX Coroutines runtime.

*IMPORTANT NOTE*: The way kotlinx.coroutines handle errors is by throwing exceptions after you run your operations. Because of this, it's important to clarify that your operation might crash your app if you're not handling errors (in case of IO, `handleError`) or try-catching the execution.

### suspendCancellable

The `suspendCancellable` function will turn an `IO` into a cancellable coroutine, allowing you to cancel it within its scope like any other coroutine.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.integrations.kotlinx.suspendCancellable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob

val scope = CoroutineScope(SupervisorJob())

//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

fun greet(): IO<Unit> =
  IO.fx {
    !effect { sayHello() }
    !effect { sayGoodBye() }
  }

fun main() {
  // This IO would stop as soon as the scope is cancelled
  scope.launch {
    greet().suspendCancellable()
  }
}
//sampleEnd
```

# Handling errors

Let's briefly expand our previous example by adding a function that theoretically fetches (from network/db) the name of a person by their id:

```kotlin
suspend fun fetchNameOrThrow(id: Int): String =
  TODO()

suspend fun sayHello(): Unit =
  println("Hello ${fetchNameOrThrow(userId)}")

suspend fun sayGoodBye(): Unit =
  println("Good bye ${fetchNameOrThrow(userId)}!")
```

Because we're using a suspend function, we know that this operation will either give us the name or throw an exception, which could cause our app to crash.

But luckily, we're able to solve this for both combinators presented above using `handleErrorWith`:

```kotlin:ank:playground
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.handleErrorWith
import arrow.integrations.kotlinx.suspendCancellable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob

val scope = CoroutineScope(SupervisorJob())

class NameNotFoundException(val id: Int): Exception("Name not found for id $id")
val userId = 1

//sampleStart
suspend fun fetchNameOrThrow(id: Int): String =
  throw NameNotFoundException(id)

suspend fun sayHello(): Unit =
  println("Hello ${fetchNameOrThrow(userId)}")

suspend fun sayGoodBye(): Unit =
  println("Good bye ${fetchNameOrThrow(userId)}!")

fun greet(): IO<Unit> =
  IO.fx {
    !effect { sayHello() } // This first call will throw and the exception be captured within this IO.
    !effect { sayGoodBye() } // The second op will not be executed because of the above.
  }.handleErrorWith { IO { println("Error printing greeting") } }

fun main() {
  //  You can safely run greet() with unsafeRunScoped
  // or suspendCancellable + kotlinx.
}
//sampleEnd
```

With the power of IO, we can make sure that our program doesn't crash due to recoverable errors, *no matter which runtime we use*!
