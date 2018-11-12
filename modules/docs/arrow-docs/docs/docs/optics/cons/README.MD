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
import arrow.optics.instances.list.cons.uncons

listOf(1, 2, 3).uncons()
```
```kotlin:ank
emptyList<Int>().uncons()
```
```kotlin:ank
import arrow.optics.instances.list.cons.cons

1.cons(listOf(2, 3))
```

### Instances

The following datatypes in Arrow provide instances that adhere to the `Cons` typeclass.

- [ListK]({{ '/docs/datatypes/listk' | relative_url }})
- [String]