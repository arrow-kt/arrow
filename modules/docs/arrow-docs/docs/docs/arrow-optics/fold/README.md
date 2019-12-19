---
layout: docs-optics
title: Fold
permalink: /docs/optics/fold/
---

## Fold


Note: Don't confuse this with the collection aggregate operation [`fold`](https://kotlinlang.org/docs/reference/collection-aggregate.html#fold-and-reduce).

A `Fold` is an optic that can see into a structure and get 0 to N foci.
It is a generalization of an instance of [`Foldable`](/docs/arrow/typeclasses/foldable).

Creating a `Fold` can be done by manually defining `foldMap`.

```kotlin:ank
import arrow.core.*
import arrow.optics.*
import arrow.typeclasses.*
import arrow.core.extensions.*

fun <T> nullableFold(): Fold<T?, T> = object : Fold<T?, T> {
    override fun <R> foldMap(M: Monoid<R>, s: T?, f: (T) -> R): R =
        s?.let(f) ?: M.empty()
}
```

Or you can get a `Fold` from any existing `Foldable`.

```kotlin:ank:silent
import arrow.core.extensions.nonemptylist.foldable.*

val nonEmptyIntFold: Fold<NonEmptyListOf<Int>, Int> = Fold.fromFoldable(NonEmptyList.foldable())
```

`Fold` has an API similar to `Foldable`, but because it's defined in terms of `foldMap`, there are no associative fold functions available.

```kotlin:ank
nullableFold<Int>().isEmpty(null)
```
```kotlin:ank
nonEmptyIntFold.combineAll(Int.monoid(), NonEmptyList.of(1, 2, 3))
```
```kotlin:ank
nullableFold<Int>().headOption(null)
```
```kotlin:ank
nonEmptyIntFold.headOption(NonEmptyList.of(1, 2, 3, 4))
```

## Composition

Composing `Fold` can be used for accessing foci in nested structures.

```kotlin:ank
val nestedNelFold: Fold<NonEmptyListOf<NonEmptyListOf<Int>>, NonEmptyListOf<Int>> = Fold.fromFoldable(NonEmptyList.foldable())

val nestedNel = NonEmptyList.of(1, 2, 3, 4).map {
    NonEmptyList.of(it, it)
}

(nestedNelFold compose nonEmptyIntFold).getAll(nestedNel)
```

`Fold` can be composed with all optics except `Setter`, and results in the following optics.

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Fold | Fold | Fold | Fold | Fold | Fold | X | Fold | Fold |
