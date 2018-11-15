---
layout: docs
title: Reducible
permalink: /docs/typeclasses/reducible/
---

## Reducible

{:.intermediate}
intermediate

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Reducible

TypeClass(Reducible::class).dtMarkdownList()
```

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
import arrow.reflect.*
import arrow.typeclasses.Reducible

TypeClass(Reducible::class).hierarchyGraph()
```

TODO. Meanwhile you can find a short description in the [intro to typeclasses]({{ '/docs/typeclasses/intro/' | relative_url }}).
