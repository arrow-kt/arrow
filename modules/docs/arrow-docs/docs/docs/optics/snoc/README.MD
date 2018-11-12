---
layout: docs
title: Snoc
permalink: /docs/optics/snoc/
---

## Snoc

{:.beginner}
beginner

`Snoc` provides a [Prism]({{ '/docs/optics/prism' | relative_url }}) between `S` and its init `A` and last element `S`.

### Example

```kotlin:ank
import arrow.optics.instances.list.snoc.unsnoc

listOf(1, 2, 3).unsnoc()
```
```kotlin:ank
emptyList<Int>().unsnoc()
```
```kotlin:ank
import arrow.optics.instances.list.snoc.snoc

listOf(1, 2).snoc(3)
```
### Instances

The following datatypes in Arrow provide instances that adhere to the `Snoc` typeclass.

- [ListK]({{ '/docs/datatypes/listk' | relative_url }})
- [String]