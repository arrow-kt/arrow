---
layout: docs-incubator
title: Birecursive
permalink: /docs/recursion/birecursive/
---

## Birecursive




A datatype that's both `Recursive` and `Corecursive`, which enables applying both `fold` and `unfold`
operations to it.

### Data types

Arrow provides three datatypes that are instances of `Birecursive`, each modeling a
different way of defining birecursion.

```kotlin:ank:replace
import arrow.reflect.*
import arrow.recursion.typeclasses.*

TypeClass(Birecursive::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.recursion.typeclasses.Birecursive)
