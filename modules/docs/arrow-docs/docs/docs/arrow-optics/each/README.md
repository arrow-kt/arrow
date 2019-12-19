---
layout: docs-optics
title: Each
permalink: /docs/optics/each/
---

## Each


`Each` provides a [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }}) that can focus into a structure `S` to see all its foci `A`.

### Example

`Each` can easily be created given a `Traverse` instance.

```kotlin:ank
import arrow.core.*
import arrow.optics.*
import arrow.optics.typeclasses.*
import arrow.core.extensions.listk.traverse.*

val each: Each<ListKOf<Int>, Int> = Each.fromTraverse(ListK.traverse())

val listTraversal: Traversal<ListKOf<Int>, Int> = each.each()

listTraversal.lastOption(listOf(1, 2, 3).k())
```
```kotlin:ank
listTraversal.lastOption(ListK.empty())
```

#### Creating your own `Each` instances

Arrow provides `Each` instances for some common datatypes in Arrow. You can look them up by calling `Each.each()`.

You may create instances of `Each` for your own datatypes, which you will be able to use as demonstrated in the [example](#example) above.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Each` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.optics.typeclasses.*

TypeClass(Each::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.optics.typeclasses.Each)
