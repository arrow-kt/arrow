---
layout: docs-optics
title: Lens
permalink: /optics/lens/
---

## Lens

There are also some convenience methods to make working with [Reader]({{ '/arrow/mtl/reader/' | relative_url }}) easier.

```kotlin:ank
import arrow.optics.mtl.*
import arrow.mtl.*

val reader: Reader<Player, Int> = playerLens.ask()

reader
  .map(Int::inc)
  .runId(Player(50))
```

```kotlin:ank
playerLens.asks(Int::inc)
  .runId(Player(50))
```

There are also some convenience methods to make working with [State]({{ '/apidocs/arrow-mtl-data/arrow.mtl/-state.html' | relative_url }}) easier.
This can make working with nested structures in stateful computations significantly more elegant.

```kotlin:ank
import arrow.mtl.*

val inspectHealth = playerLens.extract()
inspectHealth.run(player)
```

```kotlin:ank
val takeDamage = playerLens.update { it - 15 }
takeDamage.run(player)
```

```kotlin:ank
val restoreHealth = playerLens.assign(100)
restoreHealth.run(player)
```
