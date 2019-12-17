---
layout: docs
title: MonadFilter
permalink: /docs/arrow/typeclasses/monadfilter/
redirect_from:
  - /docs/typeclasses/monadfilter/
---

## MonadFilter

{:.advanced}
advanced

`MonadFilter` is a type class that abstracts away the option of interrupting computation if a given predicate is not satisfied.

All instances of `MonadFilter` provide syntax over their respective data types to comprehend monadically over their computation:

## continueIf

Binding over `MonadFilter` instances with `fx.monadFilter` brings into scope the `continueIf` guard that requires a `Boolean` predicate as value. If the predicate is `true` the computation will continue and if the predicate returns `false` the computation is short-circuited returning monad filter instance `empty()` value.

In the example below we demonstrate monadic comprehension over the `MonadFilter` instances for both `Option` and `ListK` since both data types can provide a safe `empty` value.

When `continueIf` is satisfied the computation continues

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.typeclasses.*
import arrow.core.extensions.*
import arrow.core.extensions.option.monadFilter.*

Option.monadFilter().fx.monadFilter {
  val (a) = Option(1)
  val (b) = Option(1)
  val c = a + b
  continueIf(c > 0)
  c
}
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.listk.monadFilter.*

ListK.monadFilter().fx.monadFilter {
  val (a) = listOf(1).k()
  val (b) = listOf(1).k()
  val c = a + b
  continueIf(c > 0)
  c
}
```    

When `continueIf` returns `false` the computation is interrupted and the `empty()` value is returned

```kotlin:ank
Option.monadFilter().fx.monadFilter {
  val (a) = Option(1)
  val (b) = Option(1)
  val c = a + b
  continueIf(c < 0)
  c
}
```

```kotlin:ank
ListK.monadFilter().fx.monadFilter {
  val (a) = listOf(1).k()
  val (b) = listOf(1).k()
  val c = a + b
  continueIf(c < 0)
  c
}
```    

## bindWithFilter

Binding over `MonadFilter` instances with `fx.monadFilter` brings into scope the `bindWithFilter` guard that requires a `Boolean` predicate as value getting matched on the monad capturing inner value. If the predicate is `true` the computation will continue and if the predicate returns `false` the computation is short-circuited returning the monad filter instance `empty()` value.

When `bindWithFilter` is satisfied the computation continues

```kotlin:ank
Option.monadFilter().fx.monadFilter {
  val (a) = Option(1)
  val b = Option(1).bindWithFilter { it == a } //continues
  a + b
}
```

```kotlin:ank
ListK.monadFilter().fx.monadFilter {
  val (a) = listOf(1).k()
  val b = listOf(1).k().bindWithFilter { it == a } //continues
  a + b
}
```

When `bindWithFilter` returns `false` the computation short circuits yielding the monad's empty value

```kotlin:ank
Option.monadFilter().fx.monadFilter {
 val (a) = Option(0)
 val b = Option(1).bindWithFilter { it == a } //short circuits because a is 0
 a + b
}
```   

```kotlin:ank
ListK.monadFilter().fx.monadFilter {
 val (a) = listOf(0).k()
 val b = listOf(1).k().bindWithFilter { it == a } //short circuits because a is 0
 a + b
}
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.MonadFilter

TypeClass(MonadFilter::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.MonadFilter)
