---
layout: docs-optics
title: Cons
permalink: /docs/optics/cons/
---

## Cons




`Cons` provides a [Prism]({{ '/docs/optics/prism' | relative_url }}) between a structure `S` and its first element `A` and tail `S`.
It provides a convenient way to attach or detach elements to the beginning side of a structure [S].

It can be constructed by providing the `Prism`.

```kotlin:ank
import arrow.core.ListK
import arrow.optics.extensions.listk.cons.cons
import arrow.optics.typeclasses.Cons

val listFirst = ListK.cons<Int>().cons()
val instance = Cons(listFirst)
instance
```

It defines two functions: `cons` and `uncons`.

`cons` prepends an element `A` to a structure `S`.

```kotlin:ank
import arrow.optics.extensions.list.cons.cons

1.cons(listOf(2, 3))
```

`uncons` detaches the first element `A` from a structure `S`.

```kotlin:ank
import arrow.optics.extensions.list.cons.uncons

listOf(1, 2, 3).uncons()
```
```kotlin:ank
emptyList<Int>().uncons()
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.optics.typeclasses.*

TypeClass(Cons::class).dtMarkdownList()
```