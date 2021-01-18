---
layout: docs-optics
title: Getter
permalink: /optics/getter/
---

## Getter

`Getter` also has some convenience methods to make working with [Reader]({{ '/arrow/mtl/reader/' | relative_url }}) easier.

```kotlin:ank
import arrow.optics.mtl.*
import arrow.mtl.*

val reader: Reader<NonEmptyList<String>, String> = NonEmptyList.head<String>().asGetter().ask()

reader
  .map(String::toUpperCase)
  .runId(NonEmptyList("Hello", "World", "Viewed", "With", "Optics"))
```

```kotlin:ank
NonEmptyList.head<String>().asGetter().asks(String::decapitalize)
  .runId(NonEmptyList("Hello", "World", "Viewed", "With", "Optics"))
```

There are also some convenience methods to make working with [State]({{ '/apidocs/arrow-mtl-data/arrow.mtl/-state.html' | relative_url }}) easier.

```kotlin:ank
import arrow.optics.mtl.*
import arrow.mtl.*

val inspectHealth = healthGetter.extract()
inspectHealth.run(player)
```

```kotlin:ank
val takeMedpack = healthGetter.extractMap { it + 25 }
takeMedpack.run(player)
```
