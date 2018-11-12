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

```kotlin:ank
import arrow.effects.*
import arrow.effects.deferredk.monad.monad
import arrow.effects.typeclasses.Fiber

fun <A, B, C> parallelMap(first: DeferredK<A>,
                     second: DeferredK<B>,
                     f: (A, B) -> C): DeferredK<C> =
  DeferredK.monad().binding {
    val fiberOne: Fiber<ForDeferredK, A> = first.startF().bind()
    val fiberTwo: Fiber<ForDeferredK, B> = second.startF().bind()
    f(fiberOne.join.bind(), fiberTwo.join.bind())
  }.fix()

val first = DeferredK.sleep(5000).map {
  println("Hi, I am first")
  1
}

val second = DeferredK.sleep(3000).map {
  println("Hi, I am second")
  2
}
```

```kotlin
parMap(first, second, Int::plus).await()

//Hi, I am second
//Hi, I am first
//3
```

