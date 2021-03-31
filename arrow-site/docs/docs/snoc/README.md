---
layout: docs-optics
title: Snoc
permalink: /optics/snoc/
---

## Snoc

`Snoc` provides a [Prism]({{ '/optics/prism' | relative_url }}) between `S` and its init `A` and last element `S`.
`Snoc` can be seen as the reverse of [Cons]({{ '/optics/cons' | relative_url }}); it provides a way to attach or detach elements on the end side of a structure.

It can be constructed by providing the `Prism`.

```kotlin:ank
import arrow.optics.typeclasses.Snoc

val listLast = Snoc.list<Int>().snoc()
val instance = Snoc(listLast)
instance
```

It defines two functions `snoc` and `unsnoc`.

`snoc` appends an element `A` to a structure `S`.

```kotlin:ank
import arrow.optics.snoc

listOf(1, 2) snoc 3
```

`unsnoc` detaches the last element `A` from a structure `S`.

```kotlin:ank
import arrow.optics.unsnoc

listOf(1, 2, 3).unsnoc()
```
```kotlin:ank
emptyList<Int>().unsnoc()
```
