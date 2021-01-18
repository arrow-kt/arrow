---
layout: docs-optics
title: Traversal
permalink: /optics/traversal/
---

## Traversal

There are also some convenience methods to make working with [State]({{ '/apidocs/arrow-mtl-data/arrow.mtl/-state.html' | relative_url }}) easier.
This can make working with nested structures in stateful computations significantly more elegant.

```kotlin:ank
import arrow.optics.mtl.*

data class Enemy(val health: Int)
val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()

val dropBomb = ListK.traversal<Enemy>().update { it.copy(health = it.health - 50) }

dropBomb.run(battlefield)
```

```kotlin:ank
val finishingMove = ListK.traversal<Enemy>().assign(Enemy(0))

finishingMove.run(battlefield)
```
