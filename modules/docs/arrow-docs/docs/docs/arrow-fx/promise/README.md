---
layout: docs-fx
title: Promise
permalink: /docs/effects/promise/
---

## Promise




When made, a `Promise` is empty. That is, until it is fulfilled, which can only happen once.
A `Promise` guarantees (promises) `A` at some point in the future within the context of `F`.

## Constructing a Promise

A promise can easily be made by calling `uncancelable`.
Since the allocation of mutable state is not referentially transparent, this side-effect is contained within `F`.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.io.async.async

fun main(args: Array<String>) {
//sampleStart
val promise: IO<Nothing, Promise<ForIO, Int>> =
  Promise.uncancelable<ForIO, Int>(IO.async()).fix()
//sampleEnd
println(promise)
}
```

In case you want the side-effect to execute immediately and return the `Promise` instance, you can use the `unsafeUncancelable` function.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.io.async.async

fun main(args: Array<String>) {
//sampleStart
val unsafePromise: Promise<ForIO, Int> = Promise.unsafeUncancelable(IO.async())
//sampleEnd
println(unsafePromise)
}
```

### Get

Get the promised value, suspending the fiber running the action until the result is available.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.flatMap

fun main(args: Array<String>) {
//sampleStart
Promise.uncancelable<ForIO, Int>(IO.async()).flatMap { p ->
  p.get()
} //never ends because `get` keeps waiting for p to be fulfilled.
//sampleEnd
}
```

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.flatMap

fun main(args: Array<String>) {
//sampleStart
val result = Promise.uncancelable<ForIO, Int>(IO.async()).flatMap { p ->
  p.complete(1).flatMap {
    p.get()
  }
}.unsafeRunSync()
//sampleEnd
println(result)
}
```

### Complete

Fulfills the promise with a value. A promise cannot be fulfilled twice, so doing so results in an error.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.flatMap

fun main(args: Array<String>) {
//sampleStart
val result = Promise.uncancelable<ForIO, Int>(IO.async()).flatMap { p ->
  p.complete(2).flatMap {
    p.get()
  }
}.unsafeRunSync()
//sampleEnd
println(result)
}
```

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.flatMap

fun main(args: Array<String>) {
//sampleStart
val result = Promise.uncancelable<ForIO, Int>(IO.async()).flatMap { p ->
  p.complete(1).flatMap {
    p.complete(2)
  }
}
  .attempt()
  .unsafeRunSync()
//sampleEnd
println(result)
}
```

### Error

Breaks the promise with an exception. A promise cannot be broken twice, so doing so will result in an error.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.flatMap

fun main(args: Array<String>) {
//sampleStart
val result = Promise.uncancelable<ForIO, Int>(IO.async()).flatMap { p ->
  p.error(RuntimeException("Break promise"))
}
  .attempt()
  .unsafeRunSync()
//sampleEnd
println(result)
}
```

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.flatMap

fun main(args: Array<String>) {
//sampleStart
val result = Promise.uncancelable<ForIO, Int>(IO.async()).flatMap { p ->
  p.complete(1).flatMap {
    p.error(RuntimeException("Break promise"))
  }
}
  .attempt()
  .unsafeRunSync()
//sampleEnd
println(result)
}
```
