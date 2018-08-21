---
layout: docs
title: Sync
permalink: /docs/effects/monaddefer/
---

## MonadDefer

{:.intermediate}
intermediate

`MonadDefer` is a typeclass to abstract over computations that cause side effects. This means that the computations are defered until they're are asked to be performed *synchronously*. Without effect suspension the effects would otherwise run immediately.

```kotlin
val now = IO.applicative().just(println("eager side effect"))
// Print: "eager side effect"

now.unsafeRunAsync { }
// Nothing, the effect has run already

val later = IO.monadDefer().invoke { println("lazy side effect") }
// Nothing, the effect is deferred until executed

later.unsafeRunAsync { }
// Print: "lazy side effect"
```

### Main Combinators

All the new combinators added by `MonadDefer` are constructors. `MonadDefer` also includes all combinators present in [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}).

#### invoke

Receives a function returning `A`. The instance is responsible of evaluating the function lazily.

```kotlin
IO.monadDefer().invoke { 1 }
```

As it captures exceptions, `invoke()` is the simplest way of wrapping existing synchronous APIs.

```kotlin
fun <F> getSongUrlAsync(SC: MonadDefer<F>) =
  SC { getSongUrl() }

val songIO: IOOf<Url> = getSongUrlAsync(IO.monadDefer())
val songDeferred: DeferredKOf<Url> = getSongUrlAsync(DeferredK.monadDefer())
```

#### defer

Receives a function returning `Kind<F, A>`. The instance is responsible of creating and running the returned datatype lazily.

```kotlin
IO.monadDefer().defer { IO.just(1) }
```

This can be used to wrap synchronous APIs that already return the expected datatype, forcing them to be run lazily.

#### lazy

Suspends a function returning `Unit`.
Useful in cases like [Monad Comprehension]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) where you'd want to defer the start of the comprehension until the datatype is run without needing to use suspend.

```kotlin
val SC = IO.monadDefer()

val result = SC.binding {
  println("Print: now")
  val result = just(1).bind()
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

### Laws

Arrow provides `MonadDeferLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `MonadDefer` instances.

### Data Types

The following data types in Arrow provide instances that adhere to the `MonadDefer` type class.

- [IO]({{ '/docs/effects/io' | relative_url }})
- [ObservableK]({{ '/docs/integrations/rx2' | relative_url }})
- [FlowableK]({{ '/docs/integrations/rx2' | relative_url }})
- [DeferredK]({{ '/docs/integrations/kotlinxcoroutines/' | relative_url }})
- [FluxK]({{ '/docs/integrations/reactor' | relative_url }})
- [MonoK]({{ '/docs/integrations/reactor' | relative_url }})

