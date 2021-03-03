---
layout: docs-optics
title: Getter
permalink: /optics/getter/
---

## Getter


A `Getter` is an optic that can focus into a structure and `get` its focus.
It can be seen as a wrapper of a get function `(S) -> A` that can be composed with other optics.

Creating a `Getter` can be done by referencing a property of a data classes or by providing a function.

```kotlin:ank
import arrow.optics.*
import arrow.*

data class Player(val health: Int)

val healthGetter = Getter(Player::health)
val player = Player(75)
healthGetter.get(player)
```
```kotlin:ank
import arrow.core.*

fun <T> nonEmptyListHead() = Getter<NonEmptyList<T>, T> {
    it.head
}

nonEmptyListHead<Int>().get(nonEmptyListOf(1, 2, 3, 4))
```

Or, from any of the optics defined in `arrow-optics` that allow getting its focus safely.

```kotlin:ank:silent
import arrow.core.*
import arrow.optics.*

val headGetter: Getter<NonEmptyList<String>, String> = Lens.nonEmptyListHead<String>()
val tupleGetter: Getter<Pair<String, Int>, String> = PLens.pairFirst<String, Int>()
```

## Composition

Unlike a regular `get` function, a `Getter` composes. Similar to a `Lens`, we can compose `Getter`s to create telescopes and zoom into nested structures.

```kotlin:ank
val firstBar: Getter<NonEmptyList<Player>, Int> = Lens.nonEmptyListHead<Player>() compose healthGetter
firstBar.get(Player(5).nel())
```

`Getter` can be composed with `Getter`, `Iso`, `Lens`, and `Fold`, and the composition results in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Getter | Getter | Getter | X | X | Getter | X | Fold | X |
