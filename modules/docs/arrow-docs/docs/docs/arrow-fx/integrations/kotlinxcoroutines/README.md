---
layout: docs-core
title: kotlinx.coroutines
permalink: /docs/integrations/kotlinxcoroutines/
---

*Note* that as of Arrow 0.9.0, we have deprecated support for `Deferred` from `kotlinx.coroutines`. Using `Deferred` as a return type is considered a smell by the library owners (see [here](https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/composing-suspending-functions.md#async-style-functions) and [here](http://youtrack.jetbrains.com/issue/KT-25620)), and we were not able to make it work consistently against some of the Laws. 

But we have not given up support for suspend functions! If you would like to use `suspend fun`, you can do so using the `arrow-fx` and this module.

# Kotlin Coroutines and runtime support

Due to the current nature of Kotlin Coroutines' implementation we need to make a differentiation between the Coroutines feature in the language and the kotlinx.coroutines runtime, in order to understand their differences and this module altogether:

* Suspend functions and continuations (kotlin.coroutines package) are built in the language, meaning that you don't need extra dependencies to use them.
* Coroutines runtime and cancellation (kotlinx.coroutines package) are built as a separate module and dependency, which is used to actually execute your Coroutines.

Due to this differentiation there are different alternatives consindering each tool and runtime:

| Tool →<br>Runtime ↓ | IO                                          | suspend function    |
|--------------------|----------------------------------------------|---------------------|
| Kotlinx Coroutines | `suspended`<br>`suspendCancellable`*         | `async`<br>`launch` |
| Arrow              | `unsafeRunAsync`<br>`unsafeRunAsyncCancellable`<br>`unsafeRunAsyncScoped`* | Has to be wrapped in `IO.effect` and then run with any of the operations on the left.<br><br>Alternatively it can be wrapped with `Fx.effect` and be executed [polymorphically](/docs/fx/polymorphism/). |

The * marked ops are available within this integration module, offering different alternatives depending on the project's needs. Both options offered in this integration module are described below:

## Integrating Coroutines with IO

If you'd like to introduce `IO` in your project, you might want to keep using the Coroutines style of cancellation with scopes. This is specially useful on *Android* projects where the Architecture Components [already provide handy scopes for you](https://developer.android.com/topic/libraries/architecture/coroutines#lifecycle-aware).

### unsafeRunScoped

Runs the specific `IO` with a [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html), so it will be automatically canceled when the scope does as well:


```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx
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
  greet().unsafeRunScoped(scope)
}
//sampleEnd
```


## Alternatively, integrating IO with kotlinx.coroutines

Sometimes you might want to not switch the runtime of your project and slowly integrate `IO` instead. For this use case we've added some extensions to make `IO` work with the Coroutines runtime.

*IMPORTANT NOTE*: The way kotlinx.coroutines handle errors is by throwing exceptions after you run your operations. Because of this it's important to clarify that your operation might crash your app if you're not handling errors (in case of IO, `handleError`) or try-catching the execution.

### suspendCancellable

The `suspendCancellable` function will turn an `IO` into a cancellable coroutine, allowing you to cancel it within its scope like any other coroutine.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.integrations.kotlinx.suspendCancellable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
import arrow.integrations.kotlinx.suspendCancellable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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