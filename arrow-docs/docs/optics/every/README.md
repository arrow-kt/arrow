---
layout: docs-optics
title: Every
permalink: /optics/every/
---

## Every

`Every` combines the powers of [`Traversal`]({{ '/optics/traversal/' | relative_url }}) and [`Fold`]({{ '/optics/fold/' | relative_url }}).
It can focus into a structure `S` to see all its foci `A`.

### Example

`Every` can easily be created given a `Traversal` and `Fold` instance.

```kotlin:ank
import arrow.optics.*
import arrow.optics.typeclasses.*

val every: Every<List<Int>, Int> = Every.from(Traversal.list<Int>(), Fold.list<Int>())

every.lastOrNull(listOf(1, 2, 3))
```
```kotlin:ank
every.lastOrNull(emptyList())
```

#### Creating your own `Every` instances

Arrow provides `Every` instances for some common datatypes in Arrow and Kotlin Std. You can find them on the companion object of `Every`.

You may create instances of `Every` for your own datatypes, which you will be able to use as demonstrated in the [example](#example) above.
This can be done by implementing both `foldMap` and `modify` yourself.

```kotlin:ank:silent
import arrow.typeclasses.Monoid

fun <A> PEvery.Companion.list(): Every<List<A>, A> = object : Every<List<A>, A> {
  override fun <R> foldMap(M: Monoid<R>, s: List<A>, map: (A) -> R): R =
    M.run { s.fold(empty()) { acc, a -> acc.combine(map(a)) } }

  override fun modify(s: List<A>, map: (focus: A) -> A): List<A> =
    s.map(map)
}
```

