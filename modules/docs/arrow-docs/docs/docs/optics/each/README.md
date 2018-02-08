---
layout: docs
title: Each
permalink: /docs/optics/each/
---

## Each

`Each` provides a [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }}) that can focus into a structure `S` to see all its foci `A`.

### Example

`Each` can easily be created given a `Traverse` instance.

```kotlin:ank
import arrow.data.*
import arrow.optics.*
import arrow.optics.typeclasses.*

val each: Each<ListKWKind<Int>, Int> = Each.fromTraverse(ListKW.traverse())

val listTraversal: Traversal<ListKWKind<Int>, Int> = each.each()

listTraversal.lastOption(listOf(1, 2, 3).k())
```
```kotlin:ank
listTraversal.lastOption(ListKW.empty())
```

#### Creating your own `Each` instances

Arrow provides `Each` instances for some common datatypes in Arrow. You can look them up by calling `Each.each()`.

You may create instances of `Each` for your own datatypes which you will be able to use as demonstrated in the [example](#example) above.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Each` instances for custom datatypes.

### Instances

The following datatypes in Arrow provide instances that adhere to the `Each` typeclass.

- [MapKW]({{ '/docs/datatypes/mapkw' | relative_url }})
- [ListKW]({{ '/docs/datatypes/listkw' | relative_url }})
- [Option]({{ '/docs/datatypes/option' | relative_url }})
- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Either]({{ '/docs/datatypes/either' | relative_url }})
