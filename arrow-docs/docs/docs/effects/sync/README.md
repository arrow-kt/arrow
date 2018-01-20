---
layout: docs
title: Sync
permalink: /docs/effects/sync/
---

## Sync

`Sync` is a typeclass representing suspension of execution via functions, allowing for asyncronous and lazy computations.

`Sync` includes all combinators present in [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}).

### Main Combinators

#### invoke

Receives a function returning `A`. The instance is responsible of evaluating the function lazily.

```kotlin
IO.sync().invoke { 1 }
```

As it captures exceptions, `invoke()` is the simplest way of wrapping existing synchronous APIs.

```kotlin
fun <F> getSongUrlAsync(SC: Sync<F> = asycContext()) =
  SC { getSongUrl() }

val songIO: IO<Url> = getSongUrlAsync().ev()
val songDeferred: DeferredKW<Url> = getSongUrlAsync().ev()
```

#### suspend

Receives a function returning `HK<F, A>`. The instance is responsible of creating and running the returned datatype lazily.

```kotlin
IO.sync().suspend { IO.pure(1) }
```

This can be used to wrap synchronous APIs that already return the expected datatype, forcing them to be run lazily.

#### lazy

Suspends a function returning `Unit`.
Useful in cases like [Monad Comprehension]({{ '/docs/patterns/monadcomprehensions' | relative_url }}) where you'd want to defer the start of the comprehension until the datatype is run without needing to use suspend.

```kotlin
val SC = IO.sync()

val result = SC.binding {
  println("Print: now")
  val result = pure(1).bind()
  yields(result + 1)
}

//Print: now

val lazyResult = SC.binding {
  SC.lazy().bind()
  println("Print: lazy")
  val result = eagerIO().bind()
  yields(result + 1)
}

//Nothing here!

lazyResult
  .unsafeRunAsync { }

//Print: lazy
```

### Syntax

#### (() -> A)#defer

Runs the current function in the Async passed as a parameter. It doesn't await for its result.
Use `bind()` on the return, or the alias `bindAsync()`.

All exceptions are wrapped.

```kotlin
{ fibonacci(100) }.defer(ObservableKW.sync())
```

```kotlin
{ fibonacci(100) }.defer(IO.sync())
```

```kotlin
{ throw RuntimeException("Boom") }
  .defer(IO.sync())
  .ev().attempt().unsafeRunAsync { }
```

### Data types

The following datatypes in Arrow provide instances that adhere to the `Sync` typeclass.

- [IO]({{ '/docs/effects/io' | relative_url }})
- [ObservableKW]({{ '/docs/integrations/rx2' | relative_url }})
- [FlowableKW]({{ '/docs/integrations/rx2' | relative_url }})
- [DeferredKW]({{ '/docs/integrations/kotlinxcoroutines/' | relative_url }})
