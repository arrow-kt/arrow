---
layout: docs-incubator
title: Accum
permalink: /docs/arrow/mtl/accum/
redirect_from:
  - /docs/datatypes/accum/
---

## Accum

`Accum` is a structure similiar to `State` that provides a functional approach to handling application state. In contrast to `State` it provides append-only accumulation of state during the computation.

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.*
import arrow.mtl.*

val a1 = accum { s: String ->
    "#1" toT 1
}

val a2 = accum { s: String ->
    "#2" toT 1
}

val a3 = a1.flatMap(String.monoid()) {
    a2
}

a3.execAccum(".")
```