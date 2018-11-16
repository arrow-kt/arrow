---
layout: docs
title: MonadState
permalink: /docs/typeclasses/monadstate/
---

## MonadState

{:.advanced}
advanced

TODO. Meanwhile you can find a short description in the [intro to typeclasses]({{ '/docs/typeclasses/intro/' | relative_url }}).


### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.mtl.typeclasses.MonadState

TypeClass(MonadState::class).dtMarkdownList()
```

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
import arrow.reflect.*
import arrow.mtl.typeclasses.MonadState

TypeClass(MonadState::class).hierarchyGraph()
```