---
layout: docs
title: SortedMapK
permalink: /docs/arrow/data/sortedmapk/
redirect_from:
  - /docs/datatypes/sortedmapk/
---

## SortedMapK

{:.beginner}
beginner

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.core.*

DataType(SortedMapK::class).tcMarkdownList()
```

`SortedMapK` is pretty much the same as [`MapK`]({{ '/docs/arrow/data/mapk' | relative_url }}) except that it is sorted.
