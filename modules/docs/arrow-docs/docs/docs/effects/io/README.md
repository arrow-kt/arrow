---
layout: docs
title: IO
permalink: /docs/effects/io/
---

## IO

{:.intermediate}
intermediate

`IO` is the most common data type used to represent side-effects in functional languages.
This means `IO` is the data type of choice when interacting with the external environment: databases, network, operative systems, files...

`IO` is used to represent operations that can be executed lazily and are capable of failing, generally with exceptions.
This means that code wrapped inside `IO` will not throw exceptions until it is run, and those exceptions can be captured inside `IO` for the user to check.

The first challenge for someone new to effects with`IO`is evaluating its result. Given that `IO` is used to wrap operations with the environment,
the return value after completion is commonly used in another part of the program.
Coming from an OOP background the simplest way to use the return value of `IO` is to consider it as an asynchronous operation you can register a callback for.

## Running IO

`IO` objects can be constructed passing them functions that will not be immediately called. This is called lazy evaluation.
Running in this context means evaluating the content of an `IO` object, and propagating its result in a synchronous, asynchronous, or deferred way.

Note that `IO` objects can be run multiple times, and depending on how they are constructed they will evaluate its content again every time they're run.

The general good practice is to have a single unsafe run call per program, at the entry point. In backend applications or command line tools this can be at the main. For Android apps, specially those before Android 9.0, this could happen per Activity.

### attempt

Executes and defers the result into a new `IO` that has captured any exceptions inside `Either<Throwable, A>`.
Running this new `IO` will work over its result, rather than on the original `IO` content where `attempt()` was called on.

```kotlin
IO<Int> { throw RuntimeException() }
  .attempt()
```

### runAsync

Takes as a parameter a callback from a result of `Either<Throwable, A>` to a new `IO<Unit>` instance.
All exceptions that would happen on the function parameter are automatically captured and propagated to the `IO<Unit>` return.

It runs the current `IO` asynchronously, calling the callback parameter on completion and returning its result.

The operation will not yield a result immediately; ultimately to start running the suspended computation you have to evaluate that new instance using an unsafe operator like `unsafeRunAsync` or `unsafeRunSync` for `IO`.

```kotlin
IO<Int> { throw RuntimeException("Boom!") }
  .runAsync { result ->
    result.fold({ IO { println("Error") } }, { IO { println(it.toString()) } })
  }
```

### unsafeRunAsync

Takes as a parameter a callback from a result of `Either<Throwable, A>`.
This callback is assumed to never throw any internal exceptions.

It runs the current `IO` asynchronously, calling the callback parameter on completion.

```kotlin
IO<Int> { throw RuntimeException("Boom!") }
  .unsafeRunAsync { result ->
    result.fold({ println("Error") }, { println(it.toString()) })
  }
```

### unsafeRunAsyncCancellable

Same as `unsafeRunAsync` except it returns a cancellation token. Upon invokation the cancellation token stops the current run for the `IO`. 

```
val cancel = myExpensiveIO
  .unsafeRunAsyncCancellable { result ->
    result.fold({ println("Error") }, { println(it.toString()) })
  }
  
cancel()
```

It is important to know that cancelation can only be applied across operator boundaries, i.e. a blocking operation like `Thread.sleep` cannot be cancelled. Use helpers like `IO.sleep` instead!

### unsafeRunTimed

To be used with SEVERE CAUTION, it runs `IO` synchronously and returns an `Option<A>` blocking the current thread. It requires a timeout parameter.
If the any non-blocking operation performed inside `IO` lasts longer than the timeout, `unsafeRunSyncTimed` returns `None`.

If your program has crashed, this function call is a good suspect. To avoid crashing use `attempt()` first.

If your multithreaded program deadlocks, this function call is a good suspect.

If your multithreaded program halts and never completes, this function call is a good suspect.

```kotlin
IO<Int> { throw RuntimeException("Boom!") }
  .attempt()
  .unsafeRunTimed(100.milliseconds)
```

```kotlin
IO.async<Int> { }
  .attempt()
  .unsafeRunTimed(100.milliseconds)
```

### unsafeRunSync

To be used with SEVERE CAUTION, it runs `IO` synchronously and returning its result blocking the current thread.
It generally should be used only for examples & tests.

If your program has crashed, this function call is a good suspect. To avoid crashing use `attempt()` first.

If your multithreaded program deadlocks, this function call is a good suspect.

If your multithreaded program halts and never completes, this function call is a good suspect.

```kotlin
IO<Int> { throw RuntimeException("Boom!") }
  .attempt()
  .unsafeRunSync()
```

```kotlin
IO { 1 }
  .attempt()
  .unsafeRunSync()
```

## Constructors

As we have seen above, the way of constructing an `IO` affects its behavior when run multiple times.
Understanding the constructors is key to mastering `IO`.

### just

Used to wrap single values. It creates an`IO`that returns an existing value.

```kotlin
IO.just(1)
  .unsafeRunSync()
```

### raiseError

Used to notify of errors during execution. It creates an `IO` that returns an existing exception.

```kotlin
IO.raiseError<Int>(RuntimeException("Boom!"))
  .attempt()
  .unsafeRunSync()
```

### invoke

Generally used to wrap existing blocking functions. Creates an `IO` that invokes one lambda function when run.

Note that this function is evaluated every time `IO` is run.

```kotlin
IO { 1 }
  .unsafeRunSync()
```

```kotlin
IO<Int> { throw RuntimeException("Boom!") }
  .attempt()
  .unsafeRunSync()
```

### effect

Similar to `invoke`, accepting `suspend` functions. Creates an `IO` that invokes one suspend lambda function when run.

Note that this function is evaluated every time `IO` is run.

```kotlin
IO.effect { requestSuspend(1) }
  .unsafeRunSync()
```

```kotlin
IO.effect { throw RuntimeException("Boom!") }
  .attempt()
  .unsafeRunSync()
```

### defer

Used to defer the evaluation of an existing `IO`.

```kotlin
IO.defer { IO.just(1) }
  .attempt()
  .unsafeRunSync()
```

### async

Mainly used to integrate with existing frameworks that have asynchronous calls. If the frameworks allow cancelation, use `cancelable` instead.

It requires a function that provides a callback parameter and it expects for the user to start an operation using the other framework.
The callback parameter has to be invoked with an `Either<Throwable, A>` once the other framework has completed its execution.
Note that if the callback is never called IO will run forever and not terminate unless run using `unsafeRunTimed()`.

```kotlin
IO.async<Int> { callback ->
    callback(1.right())
}
  .attempt()
  .unsafeRunSync()
```

```kotlin
IO.async<Int> { callback ->
    callback(RuntimeException("Boom").left())
}
  .attempt()
  .unsafeRunSync()
```

## cancelable

Same as `async`, it's used to integrate with existing frameworks. The unique difference is that the `cancelable` block requires returning an `IO` that'll be executed whe the whole `IO` operation is canceled.

```kotlin
val cancel = IO.cancelable<Int> { callback ->
    val subscription = myObservable.subscribe { callback(it.right()) }
    IO { subscription.cancel() }
}
  .attempt()
  .unsafeRunAsyncCancellable { }
  
cancel() // stops both the local IO and myObservable
```

## sleep

Sleeps for a given duration without blocking a thread.

```kotlin
val result =
  IO.sleep(3.seconds).flatMap {
    IO.effect { println("Hello World!") }
  }.unsafeRunSync()
```

## Effect Comprehensions

`IO` is usually best paired with [comprehensions]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) to get a cleaner syntax.
[Comprehensions]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) also enable cancellation and parallelization of IO effects.

```kotlin
import arrow.typeclasses.*
import arrow.fx.*
import arrow.fx.extensions.fx

IO.fx {
  val (file) = getFile("/tmp/file.txt")
  val (lines) = file.readLines()
  val average =
    if (lines.isEmpty()) {
      0
    } else {
      val count = lines.map { it.length }.foldLeft(0) { acc, lineLength -> acc + lineLength }
      count / lines.length
    }
  average
}.attempt().unsafeRunSync()
```

## Common operators

IO implements all the operators common to all instances of [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror' | relative_url }}). Those include `map`, `flatMap`, and `handleErrorWith`.

### Supported Type Classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.fx.*

DataType(IO::class).tcMarkdownList()
```
