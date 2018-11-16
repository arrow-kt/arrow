---
layout: docs
title: MonadReader
permalink: /docs/typeclasses/monadreader/
---

## MonadReader

{:.advanced}
advanced

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.mtl.typeclasses.MonadReader

TypeClass(MonadReader::class).dtMarkdownList()
```

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
import arrow.reflect.*
import arrow.mtl.typeclasses.MonadReader

TypeClass(MonadReader::class).hierarchyGraph()
```

TODO. Meanwhile you can find a short description in the [intro to typeclasses]({{ '/docs/typeclasses/intro/' | relative_url }}).
