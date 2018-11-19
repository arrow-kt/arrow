---
layout: docs
title: Promise
permalink: /docs/effects/promise/
---

## Promise

{:.intermediate}
intermediate

When made, a `Promise` is empty. Until it is fulfilled, which can only happen once.
A `Promise` guarantees (promises) `A` at some point in the future within the context of `F`.

## Constructing a Promise

A promise can easily be made by calling `cancellable`.
Since the allocation of mutable state is not referentially transparent this side-effect is contained within `F`.

```kotlin:ank:silent
import arrow.effects.*
import arrow.effects.instances.io.async.async

val promise: IO<Promise<ForIO, Int>> =
  Promise.uncancelable<ForIO, Int>(IO.async()).fix()
```

In case you want the side-effect to execute immediately and return the `Ref` instance you can use the `unsafe` function.

```kotlin:ank:silent
val unsafePromise: Promise<ForIO, Int> = Promise.unsafeCancellable(IO.async())
```

### Get

Get the promised value, suspending the fiber running the action until the result is available.
This suspending the fiber running the action until the result is available.

```kotlin
import arrow.effects.instances.io.monad.flatMap

promise.flatMap { p ->
  p.get
}.unsafeRunSync() //never ends because `get` keeps waiting for p to be fulfilled.
```

```kotlin:ank
promise.flatMap { p ->
  p.complete(1)
  p.get
}.unsafeRunSync()
```

### Complete

Fulfills the promise with a value. A promise cannot be fulfilled twice, so doing so results in an error.

```kotlin:ank
promise.flatMap { p ->
  p.complete(2)
  p.get
}.unsafeRunSync()
```

```kotlin:ank
promise.flatMap { p ->
  p.complete(1)
  p.complete(2)
}
  .attempt()
  .unsafeRunSync()
```

### Error

Breaks the promise with an exception. A promise cannot be broken twice, so doing so will result in an error.

```kotlin:ank
promise.flatMap { p ->
  p.error(RuntimeException("Break promise"))
}
  .attempt()
  .unsafeRunSync()
```

```kotlin:ank
promise.flatMap { p ->
  p.complete(1)
  p.error(RuntimeException("Break promise"))
}
  .attempt()
  .unsafeRunSync()
```