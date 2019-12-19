---
layout: docs-optics
title: Snoc
permalink: /docs/optics/snoc/
---

## Snoc


`Snoc` provides a [Prism]({{ '/docs/optics/prism' | relative_url }}) between `S` and its init `A` and last element `S`.
`Snoc` can be seen as the reverse of [Cons](({{ '/docs/optics/cons' | relative_url }})); it provides a way to attach or detach elements on the end side of a structure.

It can be constructed by providing the `Prism`.

```kotlin:ank
import arrow.core.ListK
import arrow.optics.extensions.listk.snoc.snoc
import arrow.optics.typeclasses.Snoc

val listLast = ListK.snoc<Int>().snoc()
val instance = Snoc(listLast)
instance
```

It defines two functions `snoc` and `unsnoc`.

`snoc` appends an element `A` to a structure `S`.

```kotlin:ank
import arrow.optics.extensions.list.snoc.snoc

listOf(1, 2).snoc(3)
```

`unsnoc` detaches the last element `A` from a structure `S`.

```kotlin:ank
import arrow.optics.extensions.list.snoc.unsnoc

listOf(1, 2, 3).unsnoc()
```
```kotlin:ank
emptyList<Int>().unsnoc()
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.optics.typeclasses.*

TypeClass(Snoc::class).dtMarkdownList()
```
