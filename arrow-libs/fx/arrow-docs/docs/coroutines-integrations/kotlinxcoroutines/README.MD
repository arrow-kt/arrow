---
layout: docs-fx
title: kotlinx.coroutines
permalink: /integrations/kotlinxcoroutines/
---

# Kotlin Coroutines and runtime support

Kotlin offers a `suspend` system in the language, and it offers intrinsics in the standard library to build a library on top. These `intrinsic` functions allow you to `startCoroutine`s, `suspendCoroutine`s, build `CoroutineContext`s and so on.

Kotlin's language suspension support can be found in the [kotlin.coroutines](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/index.html) package.

There are currently two libraries that provide a runtime for the language's suspension system.

- [Arrow Fx](https://arrow-kt.io/docs/fx/)
- [KotlinX Coroutines](https://github.com/Kotlin/kotlinx.coroutines)

They can easily interop with each other, and Arrow Fx's integration module offers certain combinators to use Arrow Fx's with KotlinX structured concurrency in frameworks that have chosen to incorporate the KotlinX Coroutines library such as Android and Ktor.

## Integrating Arrow Fx Coroutine with KotlinX Coroutine

If you'd like to introduce Arrow Fx Coroutine in your project, you might want to keep using the KotlinX Coroutines style of cancellation with `CoroutineScope`. This is especially useful on *Android* projects where the Architecture Components [already provide handy scopes for you](https://developer.android.com/topic/libraries/architecture/coroutines#lifecycle-aware).

### unsafeRunScoped & unsafeRunIO

`scope.unsafeRunScoped(f, cb)` runs the specific Arrow Fx Coroutine program with a [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html), so it will be automatically cancelled when the scope does as well.

Similarly, there's `f.unsafeRunIO(scope, cb)`, which works in the same way with different syntax:

```kotlin:ank:playground
import arrow.fx.coroutines.*
import arrow.fx.coroutines.kotlinx.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val scope = CoroutineScope(SupervisorJob())

//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

suspend fun greet(): Unit {
    cancelBoundary()
    sayHello()
    cancelBoundary()
    sayGoodBye()
}

fun main() {
  // This Arrow Fx Coroutine program would stop as soon as the scope is cancelled
  scope.unsafeRunScoped({ greet() }) { }

  // alternatively, you could also do
  suspend { greet() }.unsafeRunIO(scope) { }
}
//sampleEnd
```


## Alternatively, integrating Arrow Fx Coroutines with kotlinx.coroutines

Sometimes you might not want to switch the runtime of your project, and slowly integrate to Arrow Fx Coroutines instead. For this use case, we've added some extensions to work with the KotlinX Coroutines runtime.

*IMPORTANT NOTE*: The way kotlinx.coroutines handle errors is by throwing exceptions after you run your operations. Because of this, it's important to clarify that your operation might crash your app if you're not handling errors or try-catching the execution.

### suspendCancellable

The `suspendCancellable` function will turn an Arrow Fx Coroutine program into a KotlinX Coroutine, allowing you to cancel it within its scope like any other KotlinX Coroutine.

```kotlin:ank:playground
import arrow.fx.coroutines.*
import arrow.fx.coroutines.kotlinx.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob

val scope = CoroutineScope(SupervisorJob())

//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

suspend fun greet(): Unit {
    cancelBoundary()
    sayHello()
    cancelBoundary()
    sayGoodBye()
}

fun main() {
  // This Arrow Fx Coroutine program would stop as soon as the scope is cancelled
  scope.launch {
    suspendCancellable { greet() }
  }
}
//sampleEnd
```

# Handling errors

Let's briefly expand our previous example by adding a function that theoretically fetches (from network/db) the name of a person by their id:

```kotlin:ank
suspend fun fetchNameOrThrow(id: Int): String =
  "fetched name for $id"

suspend fun sayHello(): Unit =
  println("Hello ${fetchNameOrThrow(userId)}")

suspend fun sayGoodBye(): Unit =
  println("Good bye ${fetchNameOrThrow(userId)}!")
```

Because we're using a suspend function, we know that this operation will either give us the name or throw an exception, which could cause our app to crash.

But luckily, we're able to solve this for both combinators presented above using `Either.catch`:

```kotlin:ank:playground
import arrow.core.*
import arrow.fx.coroutines.*
import arrow.fx.coroutines.kotlinx.*
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

fun greet(): Unit = Either.catch {
  cancelBoundary()
  sayHello() // This first call will throw and the exception be captured within this IO.
  cancelBoundary()
  sayGoodBye() // The second op will not be executed because of the above.
}.getOrElse { println("Error printing greeting") }

fun main() {

  //  You can safely run greet() with unsafeRunScoped
  scope.unsafeRunScoped({ greet() }) { }

  // or suspendCancellable + kotlinx.
  suspendCancellable { greet() }
}
//sampleEnd
```
