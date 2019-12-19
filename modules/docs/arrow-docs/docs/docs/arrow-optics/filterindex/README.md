---
layout: docs-optics
title: FilterIndex
permalink: /docs/optics/filterindex/
---

## FilterIndex


`FilterIndex` provides a [Traversal]({{ '/docs/optics/traversal' | relative_url }}) that can focus into a structure `S` and get, set, or modify 0 to N foci whose index `I` satisfies a predicate.

If the foci `A` for a structure `S` can be indexed by `I`, then a `Traversal` can be created by `FilterIndex` that is filtered by a predicate on `I`.

`FilterIndex` can easily be created, given a `Traverse` instance and an indexing function.

```kotlin:ank
import arrow.core.*
import arrow.optics.*
import arrow.optics.typeclasses.*
import arrow.core.extensions.listk.traverse.*

val filterIndexStringByIndex = FilterIndex.fromTraverse<ForListK, String>({ list ->
    list.fix().map {
        it toT it.length
    }
}, ListK.traverse())
```

Given a `FilterIndex` instance, we can create a `Traversal` that filters out the foci that do not match the predicate.

```kotlin:ank
val filter: Traversal<ListKOf<String>, String> = filterIndexStringByIndex.filter { length -> length > 3 }

filter.getAll(listOf("H", "He", "Hel", "Hell", "Hello").k())
```

Arrow provides `FilterIndex` instances for some common datatypes in both Arrow and the Kotlin stdlib that can be filtered by index, like `ListK`, and `MapK`. You can look them up by calling `FilterIndex.filterIndex()`.

```kotlin:ank
import arrow.optics.extensions.listk.filterIndex.*
ListK.filterIndex<Int>().filter { index -> index % 2 == 0 }
            .getAll(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).k())
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.optics.typeclasses.*

TypeClass(FilterIndex::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.optics.typeclasses.FilterIndex)
