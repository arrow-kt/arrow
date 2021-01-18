---
layout: docs-optics
title: Setter
permalink: /optics/setter/
---

## Setter

There are also some convenience methods to make working with [State]({{ '/arrow/mtl/state/' | relative_url }}) easier.
This can make working with nested structures in stateful computations significantly more elegant.

```kotlin:ank
import arrow.optics.mtl.*

val takeDamage = playerSetter.update_ { it - 15 }
takeDamage.run(Player(75))
```

```kotlin:ank
val restoreHealth = playerSetter.assign_(100)
restoreHealth.run(Player(75))
```
