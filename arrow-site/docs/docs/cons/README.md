---
layout: docs-optics
title: Cons
permalink: /optics/cons/
---

## Cons

`Cons` provides a [Prism]({{ '/optics/prism' | relative_url }}) between a structure `S` and its first element `A` and tail `S`.
It provides a convenient way to attach or detach elements to the beginning side of a structure [S].

It can be constructed by providing the `Prism`.

```kotlin:ank
import arrow.optics.typeclasses.Cons

val listFirst = Cons.list<Int>().cons()
val instance = Cons(listFirst)
instance
```

It defines two functions: `cons` and `uncons`.

`cons` prepends an element `A` to a structure `S`.

```kotlin:ank
import arrow.optics.cons

1 cons listOf(2, 3)
```

`uncons` detaches the first element `A` from a structure `S`.

```kotlin:ank
import arrow.optics.uncons

listOf(1, 2, 3).uncons()
```
```kotlin:ank
emptyList<Int>().uncons()
```
