---
layout: docs-fx
title: Arrow Fx - Asynchronous & Concurrent Programming
permalink: /docs/fx/async/
---

# Asynchronous & Concurrent Programming

Arrow Fx benefits from the `!effect` application and direct syntax for asynchronous programming by yielding extremely succinct programs without callbacks. This allows us to use direct style syntax with asynchronous and concurrent operations while preserving effect control in the types and runtime, and bind their results to the left-hand side. The resulting expressions enjoy the same syntax that most OOP and Java programmers are already accustomed to—direct blocking imperative style.

## Dispatchers and Contexts

Performing effects while switching execution contexts a la carte is trivial.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx
import kotlinx.coroutines.newSingleThreadContext

//sampleStart
val contextA = newSingleThreadContext("A")

suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

val program = IO.fx {
  continueOn(contextA)
  !effect { printThreadName() }
  continueOn(dispatchers().default())
  !effect { printThreadName() }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

In addition to `continueOn`, Arrow Fx allows users to override the executions context in all functions that require one.

## Fibers

A [Fiber](/docs/effects/fiber) represents the pure result of a [Concurrent] data type starting concurrently that can be either `join`ed or `cancel`ed.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

val program = IO.fx {
  val fiberA = !effect { threadName() }.fork(dispatchers().default())
  val fiberB = !effect { threadName() }.fork(dispatchers().default())
  val threadA = !fiberA.join()
  val threadB = !fiberB.join()
  !effect { println(threadA) }
  !effect { println(threadB) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

When we spawn fibers, we can obtain their deferred non-blocking result using `join()` and destructuring the effect.

`dispatchers().default()` is an execution context that's available to all concurrent data types, such as IO, that you can use directly on `fx` blocks.

Note that, because we are using `Fiber` and a Dispatcher that may not create new threads in all cases here, there is no guarantee that the printed thread names will be different.

This is part of the greatness of Fibers. They run as scheduled, based on the policies provided by the Dispatcher's Context.

## Parallelization & Concurrency

Arrow Fx comes with built-in versions of `parMapN`, `parTraverse`, and `parSequence`, allowing users to dispatch effects in parallel and receive non-blocking results and direct syntax without wrappers.

### `parMapN`

`parMapN` allows *N#* effects to run in parallel non-blocking waiting for all results to complete, and then delegates to a user-provided function that applies a final transformation over the results.
Once the function specifies a valid return, we can observe how the returned non-blocking value is bound on the left-hand side.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

data class ThreadInfo(
  val threadA: String,
  val threadB: String
)

val program = IO.fx {
  val (threadA: String, threadB: String) =
    !dispatchers().default().parMapN(
      effect { threadName() },
      effect { threadName() },
      ::ThreadInfo
    )
  !effect { println(threadA) }
  !effect { println(threadB) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

### `parTraverse`

`parTraverse` allows any `Iterable<suspend () -> A>` to iterate over its contained effects in parallel as we apply a user-provided function over each effect result, and then gather all the transformed results in a `List<B>`.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx

//sampleStart
suspend fun threadName(i: Int): String =
  "$i on ${Thread.currentThread().name}"

val program = IO.fx {
  val result: List<String> = !
  listOf(1, 2, 3).parTraverse { i ->
    effect { threadName(i) }
  }
  !effect { println(result) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

### `parSequence`

`parSequence` applies all effects in `Iterable<suspend () -> A>` in non-blocking in parallel, then gathers all the transformed results and returns them in a `List<B>`.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

val program = IO.fx {
  val result: List<String> = !listOf(
    effect { threadName() },
    effect { threadName() },
    effect { threadName() }
  ).parSequence()

  !effect { println(result) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

## Arrow Fx vs KotlinX Coroutines

Arrow Fx can be seen as a companion to the KotlinX Coroutines library in the same way that Arrow serves as a companion to the Kotlin standard library in providing the abstractions and runtime to implement Typed FP in Kotlin.

Arrow Fx adds an extra layer of security and effect control where we can easily model side effects and how they interact with pure computations.

In contrast with the coroutines library, where `Deferred` computations are eager by default and fire immediately when instantiated, in Arrow Fx, all bindings and compositions are lazy and suspended, ensuring execution is explicit and always deferred until the last second.

Deferring execution and being able to suspend side effects is essential for programs built with Arrow because we can ensure that effects run in a controlled environment and preserve the properties of purity and referential transparency, allowing us to apply equational reasoning over the different parts that conform our programs.

Since Arrow Fx uses this lazy behavior by default, we don't have to resort to special configuration arguments when creating deferred computations.

The value `program` below is pure and referentially transparent because `fx` returns a lazy computation:

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx
//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

val program = IO.fx {
  !effect { printThreadName() }
}

fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
//sampleEnd
```

Using the same with the default `async` constructor from the coroutines library will yield an impure function because effects are not controlled and they fire immediately upon function invocation:

```kotlin:ank:playground
import kotlinx.coroutines.*
import kotlin.system.*

//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

suspend fun program() =
  GlobalScope.async { printThreadName() }

fun main() {
  runBlocking<Unit> { program().await() }
}
//sampleEnd
```

In the previous program, `printThreadName()` may be invoked before we call `await`.
If we want a pure lazy version of this operation, we need to hint to the `async` constructor that our policy is not to start right away.

```kotlin:ank:playground
import kotlinx.coroutines.*
import kotlin.system.*

//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

suspend fun program() =
  GlobalScope.async(start = CoroutineStart.LAZY) { printThreadName() }

fun main() {
  runBlocking<Unit> { program().await() }
}
//sampleEnd
```

If an `async` computation fires immediately, it does not give us a chance to suspend side effects. This implies that all functions that immediately produce their effects when invoked are impure and non-referentially transparent. This is the default in the KotlinX Coroutines Library.

Arrow Fx is not opinionated as to whether eagerly firing is a more or less appropriate technique. We, the authors, understand this style gathers a different audience where purity and referential transparency may not be goals or optimization techniques are in play, and that's just fine.

Life goes on.

Arrow Fx offers, in contrast, a different approach that is in line with Arrow's primary concern——helping you, as a user, create well-typed, safe, and pure programs in Kotlin.

On top of complementing the KotlinX Coroutines API, Arrow Fx provides interoperability with its runtime, allowing you to run polymorphic programs over the KotlinX Coroutines, Rx2, Reactor, and even custom runtimes.

## Integrating with third-party libraries

Arrow Fx integrates with the Arrow Effects IO runtime, Rx2, Reactor framework, and any library that models effectful async/concurrent computations and can provide a `@extension` to the `ConcurrentEffect<F>` type class defined in the `arrow-effects` module out of the box.

If you are interested in providing your own runtime as a backend to the Arrow Fx library, please contact us in the main [Arrow Gitter](https://gitter.im/arrow-kt/Lobby) or #Arrow channel on the official [Kotlin Lang Slack](https://kotlinlang.slack.com/) with any questions and we'll help you along the way.
