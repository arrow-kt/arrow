---
layout: docs-optics
title: Fold
permalink: /optics/fold/
---

## Fold

A `Fold` is an optic that can see into a structure and get 0 to N foci.
It is a generalization of [`fold`](https://kotlinlang.org/docs/reference/collection-aggregate.html#fold-and-reduce), and implements all operators that can be derived from it.

A structure `S` that has a focus `A` to which we can apply a function `(A) -> R` with `Monoid<R>` to `S` and get `R`.
For example, `S == List<Int>` to which we apply `(Int) -> String` with `Monoid<String>` and we get `R == String`

Creating a `Fold` can be done by manually defining `foldMap`.

```kotlin:ank
import arrow.core.*
import arrow.optics.*
import arrow.typeclasses.*

fun <T> nullableFold(): Fold<T?, T> = object : Fold<T?, T> {
    override fun <R> foldMap(M: Monoid<R>, s: T?, f: (T) -> R): R =
        s?.let(f) ?: M.empty()
}
```

`Fold` has an API similar to `kotlin.collections`, but because it's defined in terms of `foldMap`, there are no associative fold functions available.

```kotlin:ank
nullableFold<Int>().isEmpty(null)
```
```kotlin:ank
Fold.nonEmptyList<Int>().combineAll(Monoid.int(), nonEmptyListOf(1, 2, 3))
```
```kotlin:ank
nullableFold<Int>().firstOrNull(null)
```
```kotlin:ank
Fold.nonEmptyList<Int>().firstOrNull(nonEmptyListOf(1, 2, 3, 4))
```

## Composition

Composing `Fold` can be used for accessing foci in nested structures.

```kotlin:ank
val nestedNelFold: Fold<NonEmptyList<NonEmptyList<Int>>, NonEmptyList<Int>> = Fold.nonEmptyList()

val nestedNel = nonEmptyListOf(1, 2, 3, 4).map {
    nonEmptyListOf(it, it)
}

(nestedNelFold compose Fold.nonEmptyList()).getAll(nestedNel)
```

`Fold` can be composed with all optics except `Setter`, and results in the following optics.

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Fold | Fold | Fold | Fold | Fold | Fold | X | Fold | Fold |
