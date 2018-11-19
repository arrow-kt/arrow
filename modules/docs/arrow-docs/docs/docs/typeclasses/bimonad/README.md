---
layout: docs
title: Bimonad
permalink: /docs/typeclasses/bimonad/
---

## Bimonad

{:.intermediate}
intermediate

A datatype that's both a Monad and a Comonad.

TODO. Meanwhile you can find a short description in the [intro to typeclasses]({{ '/docs/typeclasses/intro/' | relative_url }}).

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Bimonad

TypeClass(Bimonad::class).dtMarkdownList()
```

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
import arrow.reflect.*
import arrow.typeclasses.Bimonad

TypeClass(Bimonad::class).hierarchyGraph()
```