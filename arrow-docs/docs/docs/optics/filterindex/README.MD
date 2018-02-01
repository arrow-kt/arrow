---
layout: docs
title: FilterIndex
permalink: /docs/optics/filterindex/
---

## FilterIndex

`FilterIndex` provides a [Traversal]({{ '/docs/optics/traversal' | relative_url }}) that can focus into a structure `S` and get, set or modify 0 to N foci whose index `I` satisfies a predicate.

If for a structure `S` the foci `A` can be indexed by `I` then a `Traversal` can be created by `FilterIndex` that is filtered by a predicate on `I`.

`FilterIndex` can easily be created given a `Traverse` instance and an indexing function.

```kotlin:ank
import arrow.core.*
import arrow.data.*
import arrow.optics.*
import arrow.optics.typeclasses.*

val filterIndexStringByIndex = FilterIndex.fromTraverse<ListKWHK, String>({ list ->
    list.ev().map {
        it toT it.length
    }
}, ListKW.traverse())
```

Given a `FilterIndex` instance we can create a `Traversal` that filters out the foci that do not match the predicate.

```kotlin:ank
val filter: Traversal<ListKWKind<String>, String> = filterIndexStringByIndex.filter { length -> length > 3 }

filter.getAll(listOf("H", "He", "Hel", "Hell", "Hello").k())
```

Arrow provides `FilterIndex` instances for some common datatypes both in Arrow and the Kotlin stdlib that can be filtered by index, like `ListKW` and `MapKW`. You can look them up by calling `FilterIndex.filterIndex()`.

```kotlin:ank
FilterIndex.filterIndex<ListKWKind<Int>, Int, Int> { index -> index % 2 == 0 }
            .getAll(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).k())
```

### Instances

The following datatypes in Arrow provide instances that adhere to the `FilterIndex` typeclass.

- [String]
- [NonEmptyList]({{ '/docs/datatypes/nonemptylist' | relative_url }})
- [ListKW]({{ '/docs/datatypes/listkw' | relative_url }})
- [SequenceKW]({{ '/docs/datatypes/sequencekw' | relative_url }})
- [MapKW]({{ '/docs/datatypes/mapkw' | relative_url }})
