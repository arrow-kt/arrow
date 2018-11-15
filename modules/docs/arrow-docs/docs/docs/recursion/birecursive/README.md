---
layout: docs
title: Birecursive
permalink: /docs/recursion/birecursive/
---

## Birecursive

{:.advanced}
advanced

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

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
import arrow.reflect.*
import arrow.recursion.typeclasses.*

TypeClass(Birecursive::class).hierarchyGraph()
```
