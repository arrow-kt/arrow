---
layout: docs
title: Sync
permalink: /docs/effects/monadsuspend/
---

## MonadSuspend

`MonadSuspend` is a typeclass representing suspension of execution via functions, allowing for asynchronous and lazy computations.

`MonadSuspend` includes all combinators present in [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}).

### Main Combinators

#### invoke

Receives a function returning `A`. The instance is responsible of evaluating the function lazily.

```kotlin
IO.monadSuspend().invoke { 1 }
```

As it captures exceptions, `invoke()` is the simplest way of wrapping existing synchronous APIs.

```kotlin
fun <F> getSongUrlAsync(SC: MonadSuspend<F> = monadSuspend()) =
  SC { getSongUrl() }

val songIO: IO<Url> = getSongUrlAsync().fix()
val songDeferred: DeferredK<Url> = getSongUrlAsync().fix()
```

#### suspend

Receives a function returning `Kind<F, A>`. The instance is responsible of creating and running the returned datatype lazily.

```kotlin
IO.monadSuspend().suspend { IO.pure(1) }
```

This can be used to wrap synchronous APIs that already return the expected datatype, forcing them to be run lazily.

#### lazy

Suspends a function returning `Unit`.
Useful in cases like [Monad Comprehension]({{ '/docs/patterns/monadcomprehensions' | relative_url }}) where you'd want to defer the start of the comprehension until the datatype is run without needing to use suspend.

```kotlin
val SC = IO.monadSuspend()

val result = SC.binding {
  println("Print: now")
  val result = pure(1).bind()
  result + 1
}

//Print: now

val lazyResult = SC.binding {
  SC.lazy().bind()
  println("Print: lazy")
  val result = eagerIO().bind()
  result + 1
}

//Nothing here!

lazyResult
  .unsafeRunAsync { }

//Print: lazy
```

#### deferUnsafe

Takes as a parameter a function that returns `Either<Throwable, A>`.
The left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution.
This function is assumed to never throw any internal exceptions.

```kotlin
IO.async()
  .deferUnsafe { throw RuntimeException() }
  .unsafeRunSync()
// ERROR!! The program crashes
```

> deferUnsafe() exists for performance purposes when throwing can be avoided.

### Comprehensions

#### bindindCancellable

It starts a [Monad Comprehension]({{ '/docs/patterns/monadcomprehensions' | relative_url }}) that allows for cancellation and suspension in separate threads.

#### bindindCancellable#bindDefer

Binds the function parameter by wrapping the result in `pure()`.

Exceptions are wrapped in `raiseError()`.

#### bindindCancellable#bindDeferIn

Executes the function parameter in a separate `CoroutineContext` and wraps the result in `pure()`.

Exceptions are wrapped in `raiseError()`.

#### bindindCancellable#bindDeferUnsafe

Binds the function parameter by wrapping the result in `pure()`.

While there is no wrapping of exceptions, the left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution.

### Syntax

#### (() -> A)#defer

Wraps the current function in the Sync passed as a parameter. All exceptions are wrapped.

```kotlin
{ fibonacci(100) }.defer(ObservableK.monadSuspend())
```

```kotlin
{ fibonacci(100) }.defer(IO.monadSuspend())
```

```kotlin
{ throw RuntimeException("Boom") }
  .defer(IO.monadSuspend())
  .fix().attempt().unsafeRunAsync { }
```

#### (() -> Either<Throwable, A>)#deferUnsafe

Runs the current function in the `MonadSuspend` passed as a parameter.

While there is no wrapping of exceptions, the left side of the [`Either`]({{ '/docs/datatypes/either' | relative_url }}) represents an error in the execution.

```kotlin
{ fibonacci(100).left() }.deferUnsafe(ObservableK.monadSuspend())
```

```kotlin
{ fibonacci(100).left() }.deferUnsafe(IO.monadSuspend())
```

```kotlin
{ RuntimeException("Boom").right() }
  .deferUnsafe(IO.monadSuspend())
  .fix().attempt().unsafeRunSync()
```

### Laws

Arrow provides [`MonadSuspendLaws`]({{ '/docs/typeclasses/laws#monadsuspendlaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `MonadSuspend` instances.

### Data types

The following datatypes in Arrow provide instances that adhere to the `MonadSuspend` typeclass.

- [IO]({{ '/docs/effects/io' | relative_url }})
- [ObservableK]({{ '/docs/integrations/rx2' | relative_url }})
- [FlowableK]({{ '/docs/integrations/rx2' | relative_url }})
- [DeferredK]({{ '/docs/integrations/kotlinxcoroutines/' | relative_url }})
