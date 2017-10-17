---
layout: docs
title: Fold
permalink: /docs/optics/fold/
---

## Fold

`Fold` is an optic that allows to focus into structure and get multiple results.
It is a generalisation of something `Foldable`.

Creating a `Fold` can be done by manually defining `foldMap`.

```kotlin:ank
import kategory.*
import kategory.optics.*

fun <T> nullableFold(): Fold<T?, T> = object : Fold<T?, T> {
    override fun <R> foldMap(M: Monoid<R>, s: T?, f: (T) -> R): R =
        s?.let(f) ?: M.empty()
}
```

Or you can get a `Fold` from any existing `Foldable`.

```kotlin:ank:silent
val nonEmptyIntFold: Fold<NonEmptyListKind<Int>, Int> = Fold.fromFoldable(NonEmptyList.foldable())
```

`Fold` has a similar API as `Foldable` but because it's defined in terms of `foldMap` there are no associative fold functions available.

```kotlin:ank
nullableFold<Int>().isEmpty(null)
```
```kotlin:ank
nonEmptyIntFold.combineAll(NonEmptyList.of(1, 2, 3))
```
```kotlin:ank
nullableFold<Int>().headOption(null)
```
```kotlin:ank
nonEmptyIntFold.headOption(NonEmptyList.of(1, 2, 3, 4))
```

## Composition

Composing `Fold` can be used for accessing nested structures to be able to see foci.

```kotlin:ank
val nestedNelFold: Fold<NonEmptyListKind<NonEmptyListKind<Int>>, NonEmptyListKind<Int>> = Fold.fromFoldable()

val nestedNel = NonEmptyList.of(1, 2, 3, 4).map {
    NonEmptyList.of(it, it)
}

(nestedNelFold compose nonEmptyIntFold).getAll(nestedNel)
```

`Fold` can be composed with all optics but `Setter` and result in the following optics.

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Fold | Fold | Fold | Fold | Fold | Fold | X | Fold | Fold |