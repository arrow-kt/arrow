---
layout: docs
title: Cons
permalink: /docs/optics/cons/
---

## Cons

{:.beginner}
beginner

`Cons` provides a [Prism]({{ '/docs/optics/prism' | relative_url }}) between `S` and its head `A` and tail `S`.

### Example

```kotlin:ank
import arrow.core.*
import arrow.data.*
import arrow.optics.typeclasses.*

val listCons = ListK.cons<Int>()

listCons.run {
  listOf(1.2.3).k().uncons()
}
```

### Instances

The following datatypes in Arrow provide instances that adhere to the `Cons` typeclass.

- [ListK]({{ '/docs/datatypes/listk' | relative_url }})
- [String]