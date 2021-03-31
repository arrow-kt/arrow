---
layout: docs-optics
title: FilterIndex
permalink: /optics/filterindex/
---

## FilterIndex


`FilterIndex` provides a [Traversal]({{ '/optics/traversal' | relative_url }}) that can focus into a structure `S` and get, set, or modify 0 to N foci whose index `I` satisfies a predicate.

If the foci `A` for a structure `S` can be indexed by `I`, then a `Traversal` can be created by `FilterIndex` that is filtered by a predicate on `I`.

`FilterIndex` can easily be created, given an `Every` instance that filters a certain index.

```kotlin:ank
import arrow.core.*
import arrow.typeclasses.*
import arrow.optics.*
import arrow.optics.typeclasses.*

val filterIndexStringByIndex : FilterIndex<List<String>, Int, String> = FilterIndex { p ->
  object : Every<List<String>, String> {
    override fun <R> foldMap(M: Monoid<R>, s: List<String>, map: (String) -> R): R = M.run {
      s.foldIndexed(empty()) { index, acc, a -> if (p(index)) acc.combine(map(a)) else acc }
    }
  
    override fun modify(s: List<String>, map: (focus: String) -> String): List<String> =
      s.mapIndexed { index, a -> if (p(index)) map(a) else a }
  }
}
```

Given a `FilterIndex` instance, we can create a `Traversal` that filters out the foci that do not match the predicate.

```kotlin:ank
val filter: Every<List<String>, String> = filterIndexStringByIndex.filter { index -> index > 3 }

filter.getAll(listOf("H", "He", "Hel", "Hell", "Hello"))
```

Arrow provides `FilterIndex` instances for some common datatypes in both Arrow and the Kotlin stdlib that can be filtered by index, like `ListK`, and `MapK`. You can look them up by calling `FilterIndex.filterIndex()`.

```kotlin:ank
import arrow.optics.typeclasses.FilterIndex

FilterIndex.list<Int>().filter { index -> index % 2 == 0 }
            .getAll(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
```
