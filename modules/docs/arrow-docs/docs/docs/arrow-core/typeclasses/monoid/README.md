---
layout: docs-core
title: Monoid
permalink: /docs/arrow/typeclasses/monoid/
redirect_from:
  - /docs/typeclasses/monoid/
---

## Monoid




`Monoid` extends the `Semigroup` type class, adding an `empty` method to semigroup's `combine`. The empty method must return a value that, when combined with any other instance of that type, returns the other instance, i.e.,

```kotlin
(combine(x, empty) == combine(empty, x) == x)
```

For example, if we have a `Monoid<String>` with `combine` defined as string concatenation, then `empty = ""`.

Having an empty defined allows us to combine all the elements of some potentially empty collection of `T` for which a `Monoid<T>` is defined and return a `T`, rather than an `Option<T>` as we have a sensible default to fall back to.

And let's see the instance of Monoid<String> in action.

```kotlin:ank
import arrow.*
import arrow.core.extensions.*
import arrow.typeclasses.*

String.monoid().run { empty() }
```

```kotlin:ank
String.monoid().run {
  listOf<String>("Λ", "R", "R", "O", "W").combineAll()
}
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.option.monoid.*

Option.monoid(Int.monoid()).run { listOf<Option<Int>>(Some(1), Some(1)).combineAll() }
```

The advantage of using these type class provided methods, rather than the specific ones for each type, is that we can compose monoids to allow us to operate on more complex types, for example.

This is also true if we define our own instances. As an example, let's use `Foldable`'s `foldMap`, which maps over values accumulating the results, using the available `Monoid` for the type mapped onto.

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.list.foldable.foldMap

listOf(1, 2, 3, 4, 5).k().foldMap(Int.monoid(), ::identity)
```

```kotlin:ank
listOf(1, 2, 3, 4, 5).k().foldMap(String.monoid(), { it.toString() })
```

To use this with a function that produces a tuple, we can define a Monoid for a tuple that will be valid for any tuple where the types it contains also have a Monoid available.

```kotlin:ank:silent
fun <A, B> monoidTuple(MA: Monoid<A>, MB: Monoid<B>): Monoid<Tuple2<A, B>> =
  object: Monoid<Tuple2<A, B>> {

    override fun Tuple2<A, B>.combine(y: Tuple2<A, B>): Tuple2<A, B> {
      val (xa, xb) = this
      val (ya, yb) = y
      return Tuple2(MA.run { xa.combine(ya) }, MB.run { xb.combine(yb) })
    }

    override fun empty(): Tuple2<A, B> = Tuple2(MA.empty(), MB.empty())
  }
```

This way, we are able to combine both values in one pass, hurrah!

```kotlin:ank
val M = monoidTuple(Int.monoid(), String.monoid())
val list = listOf(1, 1).k()

list.foldMap(M) { n: Int ->
  Tuple2(n, n.toString())
}
```


### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Monoid

TypeClass(Monoid::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Monoid)
