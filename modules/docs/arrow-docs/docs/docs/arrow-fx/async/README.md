---
layout: docs-fx
title: Async
permalink: /docs/effects/async/
---

## Async




Being able to run code in a different context of execution (i.e., thread) than the current one implies that, even if it's part of a sequence, the code will have to be asynchronous.
Running asynchronous code always requires a callback after completion on error capable of returning to the current thread.

The same way the typeclass [`Monad`]({{ '/docs/arrow/typeclasses/monad' | relative_url }}) represents a sequence of events, and [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror' | relative_url }}) a sequence that can fail, the typeclass `Async` represents asynchronous code with a callback.
Examples that can run code asynchronously are typically datatypes that can suspend effects and delay evaluation.

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.fx.*
import arrow.fx.extensions.io.async.*

IO.async()
  .async { callback: (Either<Throwable, Int>) -> Unit ->
    callback(1.right())
  }.fix().attempt().unsafeRunSync()
```

```kotlin:ank
IO.async()
  .async { callback: (Either<Throwable, Int>) -> Unit ->
    callback(RuntimeException().left())
  }.fix().attempt().unsafeRunSync()
```

`Async` includes all combinators present in [`MonadDefer`]({{ '/docs/effects/monaddefer/' | relative_url }}).

### Main Combinators

#### async

Receives a function returning `Unit` with a callback as a parameter.
The function is responsible for calling the callback once it obtains a result.
The callback accepts `Either<Throwable, A>` as the return, where the left side of the [`Either`]({{ '/docs/arrow/core/either' | relative_url }}) represents an error in the execution, and the right side is the completion value of the operation.

```kotlin
IO.async()
  .async { callback: (Either<Throwable, Int>) -> Unit ->
    userFetcherWithCallback("1").startAsync({ user: User ->
      callback(user.left())
    }, { error: Exception ->
      callback(error.right())
    })
  }
```

```kotlin
IO.async()
  .async { callback: (Either<Throwable, Int>) -> Unit ->
    userFromDatabaseObservable().subscribe({ user: User ->
      callback(user.left())
    }, { error: Exception ->
      callback(error.right())
    })
  }
```

#### continueOn

It makes the rest of the operator chain execute on a separate `CoroutineContext`, effectively jumping threads if necessary.

```kotlin
IO.async().run {
  // In current thread
  just(createUserFromId(123))
    .continueOn(CommonPool)
    // In CommonPool
    .flatMap { request(it) }
    .continueOn(Ui)
    // In Ui
    .flatMap { showResult(it) }
}
```

Behind the scenes, `continueOn()` starts a new coroutine and passes the rest of the chain as the block to execute.

The function `continueOn()` is also available inside [`Monad Comprehensions`]({{ '/docs/patterns/monad_comprehensions' | relative_url }}).

#### effect

Similar to `MonadDefer`'s `later`, this constructor takes a single suspended function and, optionally, the `CoroutineContext` it has to be run on.

```kotlin
IO.async().run {
  // In current thread
  effect(CommonPool) {
    // In CommonPool
    requestSuspend(createUserFromId(123))
  }
}
```

#### invoke with CoroutineContext

Similar to `MonadDefer`'s `later`, this constructor takes a single generator function and the `CoroutineContext` it has to be run on.

```kotlin
IO.async().run {
  // In current thread
  invoke(CommonPool) {
    // In CommonPool
    requestSync(createUserFromId(123))
  }
}
```

#### defer with CoroutineContext

Similar to `MonadDefer`'s `defer`, this constructor takes a single function returning a `Kind<F, A>` and the `CoroutineContext` it has to be run on.

```kotlin
IO.async().run {
  // In current thread
  defer(CommonPool) {
    // In CommonPool
    async { cb ->
      requestAsync(createUserFromId(123), cb)
    }
  }
}
```

#### never

Creates an object using `async()` whose callback is never called.

Depending on how the datatype is implemented, this may cause unexpected errors like awaiting forever for a result.

Use with *SEVERE CAUTION*.

```kotlin
IO.async()
  .never()
  .unsafeRunSync()
// ERROR!! The program blocks the current thread forever.
```

> never() exists to test datatypes that can handle non-termination.
For example, IO has unsafeRunTimed that runs never() safely.

### Laws

Arrow provides `AsyncLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Async` instances.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.fx.typeclasses.*

TypeClass(Async::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.fx.typeclasses.Async)
