---
layout: docs-core
title: Monoid
permalink: /arrow/typeclasses/monoid/
---

## Monoid

`Monoid` extends the `Semigroup` type class, adding an `empty` function to semigroup's `combine`. The empty method must return a value that, when combined with any other instance of that type, returns the other instance, i.e.,

```kotlin
(a.combine(empty()) == empty().combine(a) == a)
```

For example, if we have a `Monoid<String>` with `combine` defined as string concatenation, then `empty() = ""`.

Having `empty` defined allows us to combine all the elements of some potentially empty collection of `T` for which a `Monoid<T>` is defined and return a `T`, rather than an `Option<T>` as we have a sensible default to fall back to.

Let's see the instance of Monoid<String> in action:

```kotlin:ank
import arrow.typeclasses.Monoid

Monoid.string().run { empty() }
```

```kotlin:ank
Monoid.string().run {
  listOf("Λ", "R", "R", "O", "W").combineAll()
}
```

```kotlin:ank
import arrow.core.Option
import arrow.core.Some
import arrow.typeclasses.Monoid

Monoid.option(Monoid.int()).run { listOf<Option<Int>>(Some(1), Some(1)).combineAll() }
```

The advantage of using these type class provided methods, rather than the specific ones for each type, is that we can compose monoids to allow us to operate on more complex types, for example.

This is also true if we define our own instances. As an example, let's use `Foldable`'s `foldMap`, which maps over values accumulating the results, using the available `Monoid` for the type mapped onto.

```kotlin:ank
import arrow.core.foldMap
import arrow.core.identity
import arrow.typeclasses.Monoid

listOf(1, 2, 3, 4, 5).foldMap(Monoid.int(), ::identity)
```

```kotlin:ank
import arrow.core.foldMap
import arrow.typeclasses.Monoid

listOf(1, 2, 3, 4, 5).foldMap(Monoid.string()) { it.toString() }
```

To use this with a function that produces a pair, we can define a Monoid for a pair that will be valid for any pair where the types it contains also have a Monoid available.

```kotlin:ank:silent
import arrow.typeclasses.Monoid

fun <A, B> monoidPair(MA: Monoid<A>, MB: Monoid<B>): Monoid<Pair<A, B>> =
  object : Monoid<Pair<A, B>> {

    override fun Pair<A, B>.combine(other: Pair<A, B>): Pair<A, B> {
      val (thisA, thisB) = this
      val (otherA, otherB) = other
      return Pair(MA.run { thisA.combine(otherA) }, MB.run { thisB.combine(otherB) })
    }
    
    override fun empty(): Pair<A, B> = Pair(MA.empty(), MB.empty())
}
```

This way, we are able to combine both values in one pass, hurrah!

```kotlin:ank
import arrow.core.foldMap
import arrow.typeclasses.Monoid

val M = monoidPair(Monoid.int(), Monoid.string())
val list = listOf(1, 1)

list.foldMap(M) { n: Int ->
  Pair(n, n.toString())
}
```
