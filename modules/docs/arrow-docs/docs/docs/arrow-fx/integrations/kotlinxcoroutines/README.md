---
layout: docs-core
title: kotlinx.coroutines
permalink: /docs/integrations/kotlinxcoroutines/
---

## Kotlin Coroutines vs runtime

Due to the current nature of Kotlin Coroutines' implementation we need to make a differentiation between the Coroutines feature in the language and the runtime, in order to understand their differences and this module altogether:

* Suspend functions and implicit continuations are built in the language, meaning that you don't need extra dependencies to use those.
* Coroutines runtime and cancellation are built as a separate module: KotlinX Coroutines. Which is used to actually execute your Coroutines.

Due to this differentiation there are 2 possible integration options: to use suspend functions with Arrow Fx or use Arrow (specifically IO) with the Coroutines runtime. 

## Integrating Coroutines in Fx

As of Arrow 0.9.0, we have deprecated support for `Deferred` from `kotlinx.coroutines`. Using `Deferred` as a return type is considered a smell by the library owners, and we were not able to make it work consistently against some of the Laws. 

But, we have not given up support for suspend functions! If you would like to use `suspend fun`, you can do so using [`arrow-fx`]({{ '/docs/fx/' | relative_url }}). Additionally you can now scope IO with a [CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html), so it'll run until the scope is cancelled like any coroutine:

### unsafeRunScoped



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
  // or any other scope. This IO would stop as soon as the scope is cancelled
  greet().unsafeRunScoped(GlobalScope)
}
//sampleEnd
```


## Integrating IO with Coroutines

Sometimes you might want to not switch the runtime of your project and slowly integrate `IO` instead. For this use case we've added some extensions to make `IO` work with the coroutines runtime:

### suspendCancellable

The `suspendCancellable` function will turn an IO into a cancellable coroutine, allowing you to cancel it within its scope like any other coroutine.

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
  // or any other scope. This IO would stop as soon as the scope is cancelled
  GlobalScope.launch {
    greet().suspendCancellable()
  }
}
//sampleEnd
```

