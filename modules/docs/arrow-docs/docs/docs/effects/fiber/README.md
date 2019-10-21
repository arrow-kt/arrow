---
layout: docs
title: Fiber
permalink: /docs/effects/fiber/
---

## Fiber

{:.advanced}
advanced

A `Fiber` is a concurrency primitive for describing parallel operations or multi-tasking.
Concurrently started tasks can either be joined or canceled and this are the only two operators available on `Fiber`.

Using `Fiber` we can verily easily describe parallel operations such as `parallelMap`.
**Note** the operation written below does not support proper cancellation,
when the resulting `IO` is canceled it does not propagate this cancellation back to the underlying `IO`.

```kotlin:ank
import arrow.fx.*
import kotlinx.coroutines.Dispatchers.Default
import arrow.fx.extensions.fx
import arrow.fx.typeclasses.Fiber
import arrow.fx.IO

fun <A, B, C> parallelMap(first: IO<A>,
                     second: IO<B>,
                     f: (A, B) -> C): IO<C> =
  IO.fx {
    val (fiberOne: Fiber<ForIO, A>) = first.fork(Default)
    val (fiberTwo: Fiber<ForIO, B>) = second.fork(Default)
    f(!fiberOne.join(), !fiberTwo.join())
  }

val first = IO<Unit> { Thread.sleep(5000) }.map {
  println("Hi, I am first")
  1
}

val second = IO<Unit> { Thread.sleep(5000) }.map {
  println("Hi, I am second")
  2
}
```

```kotlin
parallelMap(first, second, Int::plus).await()

//Hi, I am second
//Hi, I am first
//3
```

We could fix this snippet to support proper cancellation by using `bracket` instead of `flatMap`,
which allows us to register an operation to run on cancellation, error or completion.

```kotlin:ank
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.extensions.io.monad.map

fun <A, B, C> parallelMap2(first: IO<A>,
                          second: IO<B>,
                          f: (A, B) -> C): IO<C> =
      first.fork(Default).bracket(use = { (joinA, _) ->
          second.fork(Default).bracket(use = { (joinB, _) ->
            joinA.flatMap { a ->
              joinB.map { b -> f(a, b) }
            }
          }, release = { (_, cancelB) -> cancelB })
        }, release = { (_, cancelA) -> cancelA })
```


