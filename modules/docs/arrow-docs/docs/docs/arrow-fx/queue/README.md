---
layout: docs-fx
title: Queue
permalink: /docs/effects/queue/
---

## Queue

{:.intermediate}
intermediate

A `Queue` is a lightweight, asynchronous first-in-first-out queue for holding arbitrary values within a `Concurrent`
context. There are two primary means of working with an instance of a `Queue`: placing items via `offer` and removing
items via `take`.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent

fun main(args: Array<String>) {
  val result =
  //sampleStart
    IO.fx {
      val q = !Queue.bounded<ForIO, Int>(10, IO.concurrent())
      !q.offer(42)
      !q.take()
    }
  //sampleEnd
  println(result.unsafeRunSync())
}
```

Attempting to take a value from an empty `Queue` will cause the calling fiber to be suspended and will resume upon a new
value being placed on the `Queue`.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent

fun main(args: Array<String>) {
  val result =
  //sampleStart
    IO.fx {
      val q = !Queue.bounded<ForIO, Int>(10, IO.concurrent())
      val waiting = !q.take().fork()
      !q.offer(42)
      !waiting.join()
    }
  //sampleEnd
  println(result.unsafeRunSync())
}
```

### Construction and capacity strategies

When constructing a new `Queue` there are four different options to choose from which behave differently with respect to
overflowing the configured capacity of the `Queue`:

 * **Bounded**: Offering to a `bounded` queue at capacity will cause the fiber making the call to be suspended until the
 queue has space to receive the offer value.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent

fun main(args: Array<String>) {
  val result =
  //sampleStart
    IO.fx {
      val capacity = 2
      val q = !Queue.bounded<ForIO, Int>(capacity, IO.concurrent())
      !q.offer(42)
      !q.offer(43)
      !q.offer(44).fork() // <-- This `offer` exceeds the capacity and will be suspended
      val fortyTwo   = !q.take()
      val fortyThree = !q.take()
      val fortyFour  = !q.take()
      listOf(fortyTwo, fortyThree, fortyFour)
    }
  //sampleEnd
  println(result.unsafeRunSync())
}
```

 * **Dropping**: Offering to a `dropping` queue at capacity will cause the offered value to be discarded.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent

fun main(args: Array<String>) {
  val result =
  //sampleStart
    IO.fx {
      val capacity = 2
      val q = !Queue.dropping<ForIO, Int>(capacity, IO.concurrent())
      !q.offer(42)
      !q.offer(43)
      !q.offer(44) // <-- This `offer` exceeds the capacity and will be dropped immediately
      val fortyTwo   = !q.take()
      val fortyThree = !q.take()
      !q.offer(45)
      val fortyFive  = !q.take()
      listOf(fortyTwo, fortyThree, fortyFive)
    }
  //sampleEnd
  println(result.unsafeRunSync())
}
```

 * **Sliding**: Offering to a `sliding` queue at capacity will cause the oldest value at the front of the queue to be
discarded, making room for the offered value. n.b. A `sliding` queue must have a capacity of at least 1.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent

fun main(args: Array<String>) {
  val result =
  //sampleStart
    IO.fx {
      val capacity = 2
      val q = !Queue.sliding<ForIO, Int>(capacity, IO.concurrent())
      !q.offer(42)
      !q.offer(43)
      !q.offer(44) // <-- This `offer` exceeds the capacity, causing the oldest value to be removed
      val fortyThree = !q.take()
      val fortyFour  = !q.take()
      !q.offer(45)
      val fortyFive  = !q.take()
      listOf(fortyThree, fortyFour, fortyFive)
    }
  //sampleEnd
  println(result.unsafeRunSync())
}
```

 * **Unbounded**: An `unbounded` queue has no notion of capacity and is bound only by exhausting the memory limits of
the runtime.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent

fun main(args: Array<String>) {
  val result =
  //sampleStart
    IO.fx {
      val q = !Queue.unbounded<ForIO, Int>(IO.concurrent())
      !q.offer(42)
      // ...
      !q.offer(42000000)
      !q.take()
    }
  //sampleEnd
  println(result.unsafeRunSync())
}
```

### Shutting down

A `Queue` also has the ability to be `shutdown`, interrupting any future calls to `take` or `offer` with a
`QueueShutdown` error and cancelling any suspended fibers waiting to `take` or `offer`.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent

fun main(args: Array<String>) {
  val result =
  //sampleStart
    IO.fx {
      val q = !Queue.bounded<ForIO, Int>(10, IO.concurrent())
      val t = !q.take().fork()
      !q.shutdown()
      !t.join() // Attempting to `join` after `shutdown` results in a `QueueShutdown` error
    }
  //sampleEnd
  println(result.attempt().unsafeRunSync())
}
```

Consumers of the `Queue` can also track the event of a shutdown by calling `awaitShutdown` to receive a suspended
`Concurrent<F>` that will resume once the `Queue` has been shutdown.

```kotlin:ank:playground
import arrow.fx.*
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent

fun main(args: Array<String>) {
  val result =
  //sampleStart
    IO.fx {
      val q = !Queue.bounded<ForIO, Int>(10, IO.concurrent())
      val onShutdown = !q.awaitShutdown().fork()
      !q.offer(42)
      val fortyTwo = !q.take()
      !q.shutdown()
      !onShutdown.join()
      fortyTwo
    }
  //sampleEnd
  println(result.unsafeRunSync())
}
```
