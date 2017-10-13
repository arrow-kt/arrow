---
layout: docs
title: Getter
permalink: /docs/optics/getter/
---

## Getter

A `Getter` is an optic that can focus into a structure and `get` its focus.
It can be seen as a wrapper of a get function `(S) -> A` that can be composed with other optics.

Creating a `Getter` can be done by referencing a property of a data classes or by providing a function.

```kotlin:ank
import kategory.optics.*
import kategory.*

data class Foo(val bar: Int)

val barGetter = Getter(Foo::bar)

barGetter.get(Foo(5))
```
```kotlin:ank
fun <T> nonEmptyListHead() = Getter<NonEmptyList<T>, T> {
    it.head
}

nonEmptyListHead<Int>().get(NonEmptyList.of(1, 2, 3, 4))
```

Or from any of the optics defined in `kategory-optics` that allow to safely getting its focus.

```kotlin:ank:silent
val headGetter: Getter<NonEmptyList<String>, String> = nelHead<String>().asGetter()
val tupleGetter: Getter<Tuple2<String, Int>, String> = firstTuple2<String, Int>().asGetter()
``` 

## Composition

Unlike a regular `get` function a `Getter` composes. Similar to a `Lens` we can compose `Getter`s to create telescopes and zoom into nested structures.

```kotlin:ank
val firstBar: Getter<NonEmptyList<Foo>, Int> = (nelHead<Foo>() compose barGetter)
firstBar.get(Foo(5).nel())
```

`Getter` can be composed with `Getter`, `Iso`, `Lens` and `Fold` and the composition results in the following optics.

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Getter | Getter | Getter | X | X | Getter | X | Fold | X |