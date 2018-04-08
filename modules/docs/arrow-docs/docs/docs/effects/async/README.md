---
layout: docs
title: Async
permalink: /docs/effects/async/
---

## Async

Being able to run code in a different context of execution (i.e. thread) than the current one implies that, even if it's part of a sequence, the code will have to be asynchronous.
Running asynchronous code always requires a callback after completion on error capable of returning to the current thread.

The same way the typeclass [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) represents a sequence of events, and [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) a sequence that can fail, the typeclass `Async` represents asynchronous code with a callback.
Examples of that can run code asynchronously are typically datatypes that can suspend effects, and delay evaluation.

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.effects.*

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
The function is responsible of calling the callback once it obtains a result.
The callback accepts `Either<Throwable, A>` as the return, where the left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution and the right side is the completion value of the operation.

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

#### never

Creates an object using `async()` whose callback is never called.

Depending on how the datatype is implemented this may cause unexpected errors like awaiting infinitely for a result.

Use with *SEVERE CAUTION*.

```kotlin
IO.async()
  .never()
  .unsafeRunSync()
// ERROR!! The program blocks the current thread forever.
```

> never() exists to test datatypes that can handle non-termination.
For example, IO has unsafeRunTimed that runs never() safely.

### Syntax available inside Monad Comprehensions

All the syntax functions are geared towards using `Async` inside [Monad Comprehension]({{ '/docs/patterns/monadcomprehensions' | relative_url }})
to create blocks of code to be run asynchronously.

#### binding#bindAsync

Runs a function parameter in the Async passed as a parameter,
and then awaits for the result before continuing the execution.

Note that there is no automatic error handling or wrapping of exceptions.

```kotlin
IO.monad().binding {
  val a = bindAsync(IO.async()) { fibonacci(100) }
  a + 1
}.fix().unsafeRunSync()
```

#### binding#bindAsyncUnsafe

Runs a function parameter in the Async passed as a parameter,
and then awaits for the result before continuing the execution.

While there is no wrapping of exceptions, the left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution.

```kotlin
IO.monad().binding {
  val a = bindAsync(IO.async()) { fibonacci(100).left() }
  a + 1
}.fix().unsafeRunSync()
```

```kotlin
IO.monad().binding {
  val a = bindAsync(IO.async()) { RuntimeException("Boom").right() }
  a + 1
}.fix().unsafeRunSync()
```

### Laws

Arrow provides [`AsyncLaws`]({{ '/docs/typeclasses/laws#asynclaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `Async` instances.

### Data Types

The following data types in Arrow provide instances that adhere to the `Async` type class.

- [IO]({{ '/docs/effects/io' | relative_url }})
- [ObservableK]({{ '/docs/integrations/rx2' | relative_url }})
- [FlowableK]({{ '/docs/integrations/rx2' | relative_url }})
- [DeferredK]({{ '/docs/integrations/kotlinxcoroutines/' | relative_url }})
