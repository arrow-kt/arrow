---
layout: docs-core
title: kotlinx.coroutines
permalink: /docs/integrations/kotlinxcoroutines/
---

As of Arrow 0.9.0, we have deprecated support for `Deferred` from `kotlinx.coroutines`. Using `Deferred` as a return type is considered a smell by the library owners, and we were not able to make it work consistently against some of the Laws.

But, we have not given up support for suspend functions! If you would like to use `suspend fun`, you can do so using [`arrow-fx`]({{ '/docs/fx/' | relative_url }}):

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

fun greet(): IO<Nothing, Unit> =
  IO.fx {
    !effect { sayHello() }
    !effect { sayGoodBye() }
  }
fun main() { // The edge of our world
  unsafe { runBlocking { greet() } }
}
//sampleEnd
```
