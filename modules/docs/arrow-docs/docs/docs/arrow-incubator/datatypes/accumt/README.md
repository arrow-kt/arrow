---
layout: docs-incubator
title: AccumT
permalink: /docs/arrow/mtl/accumt/
redirect_from:
  - /docs/datatypes/accumt/
---

## AccumT

`AccumT` is a monad transformer, which adds accumulation capabilities to a given monad.

```kotlin:ank
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.*
import arrow.core.*
import arrow.mtl.*

val accumT1: AccumT<String, ForId, Int> = AccumT {
    s: String -> Id.just("#1" toT 1)
}
val accumT2: AccumT<String, ForId, Int> = AccumT {
    s: String -> Id.just("#2" toT 2)
}

accumT1.flatMap(String.monoid(), Id.monad()) {
    accumT2
}.execAccumT(Id.monad(), "a")
```