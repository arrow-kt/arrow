---
layout: docs
title: Arrow Fx : Asynchronous & Concurrent Programming
permalink: /docs/effects/fx/async/
---

- [Asynchronous & Concurrent Programming](#asynchronous---concurrent-programming)
  * [Dispatchers and Contexts](#dispatchers-and-contexts)
  * [Fibers](#fibers)
  * [Parallelization & Concurrency](#parallelization---concurrency)
    + [`parMapN`](#-parmapn-)
    + [`parTraverse`](#-partraverse-)
    + [`parSequence`](#-parsequence-)
  * [Cancellation](#cancellation)
  * [Arrow Fx vs KotlinX Coroutines](#arrow-fx-vs-kotlinx-coroutines)
  * [Integrating with third party libraries](#integrating-with-third-party-libraries)

# Asynchronous & Concurrent Programming

Arrow Fx benefits from `!effect` application and direct syntax for asynchronous programming yielding extremely succinct programs without callbacks. This allow us to use direct style syntax with asynchronous and concurrent operations while preserving effect control in the types and runtime and bind their results to the left-hand side. 
 The resulting expressions enjoy the same syntax that most OOP and Java programmers are already accustomed to, direct blocking imperative style.

## Dispatchers and Contexts

Performing effects while switching execution contexts a la carte is trivial.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx
import kotlinx.coroutines.newSingleThreadContext

//sampleStart
val contextA = newSingleThreadContext("A")

suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

val program = fx {
  continueOn(contextA)
  !effect { printThreadName() }
  continueOn(NonBlocking)
  !effect { printThreadName() }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

In addition to `continueOn`, Arrow Fx allows users to override the executions context in all functions that require one.

## Fibers

A [Fiber](/docs/effects/fiber) represents the pure result of a [Concurrent] data type being started concurrently and that can be either `join`ed or `cancel`ed.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

val program = fx {
  val fiberA = !NonBlocking.startFiber(effect { threadName() })
  val fiberB = !NonBlocking.startFiber(effect { threadName() })
  val threadA = !fiberA.join()
  val threadB = !fiberA.join()
  !effect { println(threadA) }
  !effect { println(threadB) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

When we spawn fibers we can obtain their deferred non-blocking result using `join()` and destructuring the effect.

`NonBlocking` is an execution context available to all concurrent data types such as IO that you can use directly on `fx` blocks.

Note that because we are using `Fiber` here and a Dispatcher that may not create new threads in all cases we are not guaranteed that the thread names printed would be different.

This is part of the greatness of Fibers. They run as scheduled based on the policies provided by the Dispatcher's Context.

## Parallelization & Concurrency

Arrow Fx comes with built in versions of `parMapN`, `parTraverse` and `parSequence` that allows users to dispatch effects in parallel and receive results non-blocking and direct syntax without wrappers. 

### `parMapN`

`parMapN` allows *N#* effects to run in parallel non-blocking waiting for all results to complete and then it delegates to a user provided function that applies a final transformation over the results.
Once the function specifies a valid return we can observe how the returned non-blocking value is bound in the left hand side. 

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name
  
data class ThreadInfo(
  val threadA : String, 
  val threadB: String
)

val program = fx {
  val (threadA: String, threadB: String) = 
    !NonBlocking.parMapN(
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

`parTraverse` allows any `Iterable<suspend () -> A>` to iterate over its contained effects in parallel as we apply a user provided function over each effect result and then gather all the transformed results in a `List<B>` 

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

val program = fx {
  val result: List<String> = !NonBlocking.parTraverse(
    listOf(
        effect { threadName() },
        effect { threadName() },
        effect { threadName() }
    )
  ) {
      "running on: $it" 
    }
  !effect { println(result) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

### `parSequence`

`parSequence` applies all effects in `Iterable<suspend () -> A>` in non-blocking in parallel and then gathers all the transformed results and returns them in a `List<B>` 

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

val program = fx {
  val result: List<String> = !NonBlocking.parSequence(
    listOf(
      effect { threadName() },
      effect { threadName() },
      effect { threadName() }
    )
  )
  
  !effect { println(result) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

## Cancellation

All concurrent `fx` continuations are cancellable. Users may use the `fxCancellable` function to run `fx` blocks that beside returning a value it returns a disposable handler that can interrupt the operation.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fxCancellable
//sampleStart
val (_, disposable) = fxCancellable {
  !effect { println("BOOM!") }
}
//sampleEnd
fun main() { // The edge of our world
  println(disposable)
}
```

## Arrow Fx vs KotlinX Coroutines

In the same way Arrow is a companion to the Kotlin standard library providing the abstractions and runtime to implement Typed FP in Kotlin, Arrow Fx can be seen as a companion to the KotlinX Coroutines library.

Arrow Fx adds an extra layer of security and effect control where we can easily model side effects and how they interact with pure computations.

In contrast with the couroutines library where `Deferred` computations are eager by default and fire immediately when instantiated, in Arrow Fx, all bindings and compositions are lazy and suspended ensuring execution is explicit and always deferred until the last second.

Deferring execution and being able to suspend side effects is important for programs built with Arrow because we can ensure that effects run in a controlled environment and preserve the properties of purity and referential transparency that allows us to apply equational reasoning over the different parts that conform our programs.

Since Arrow Fx uses this lazy behavior by default we don't have to resort to special configuration arguments when creating deferred computations.

The value `program` below is pure and referentially transparent because `fx` returns a lazy computation. 

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx
//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

val program = fx {
  !effect { printThreadName() }
}

fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
//sampleEnd
```

The same with the default `async` constructor from the coroutines library will yield an impure function because effects are not controlled and they fire immediately upon function invocation:

```kotlin:ank:playground
import kotlinx.coroutines.*
import kotlin.system.*

//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

suspend fun program() = 
  async { printThreadName() }

fun main() { 
  runBlocking<Unit> { program().await() }
}
//sampleEnd
```

In the previous program `printThreadName()` may get invoked before we call `await`.
If we wanted a pure lazy version of this operation we need to hint the `async` constructor that our policy is to not start right away. 

```kotlin:ank:playground
import kotlinx.coroutines.*
import kotlin.system.*

//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

suspend fun program() = 
  async(start = CoroutineStart.LAZY) { printThreadName() }

fun main() { 
  runBlocking<Unit> { program().await() }
}
//sampleEnd
```

If an `async` computations fires immediately it does not give us a chance to suspend side effects. This implies that all functions that produce their effects immediately when invoked are impure and non-referentially transparent. This is the default in the KotlinX Coroutines Lib.

Arrow Fx is not opinionated as to whether firing eagerly is a more or less appropriate technique. We, the authors, understand this style gathers a different audience where purity and referential transparency may be non goals or optimizations techniques are in play and that is just fine. 

Life goes on.

Arrow Fx offers in contrast a different approach that is inline with Arrow's main concern which is helping you as a user create well-typed safe and pure programs in Kotlin.

On top of complementing the KolinX Coroutines api, Arrow Fx provides interoperability with its runtime allowing you to run polymorphic programs over the KotlinX Coroutines, Rx2, Reactor and even custom runtimes.

## Integrating with third party libraries

Arrow Fx integrates out of the box with the Arrow Effects IO runtime, Rx2, Reactor framework and any library that models effectful async/concurrent computations and can provide a `@extension` to the `ConcurrentEffect<F>` type class defined in the `arrow-effects` module.

If you are interested in providing your own runtime as backend to the Arrow Fx library please contact us in the arrow main gitter or slack channels with any questions and we'll help you along the way.