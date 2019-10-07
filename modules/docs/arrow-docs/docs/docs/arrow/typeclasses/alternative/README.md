---
layout: docs
title: Alternative
permalink: /docs/arrow/typeclasses/alternative/
redirect_from:
  - /docs/typeclasses/alternative/
---

## Alternative

{:.intermediate}
intermediate

We use [`Option`]({{ '/docs/arrow/core/option' }}) to indicate a computation can fail somehow (that is, it can have either zero results or one result), and we use lists for computations that can have many possible results (ranging from zero to arbitrarily many results). In both of these cases, one useful operation is combining all possible results from multiple computations into a single computation. The `Alternative` type class captures this combination.

`Alternative` is for [`Applicative`]({{ '/docs/arrow/typeclasses/applicative' }}) functors which also have a [`Monoid`]({{ '/docs/arrow/typeclasses/monoid' }}) structure.

### Main Combinators

#### Kind<F, A>.orElse

Is a binary function which represents a choice between alternatives.

`fun <A> Kind<F, A>.orElse(b: Kind<F, A>): Kind<F, A>`

```kotlin:ank
import arrow.core.Option
import arrow.core.extensions.option.monadCombine.monadCombine

Option.monadCombine().run {
    val x: Option<Int> = Option.just(1)
    val y: Option<Int> = Option.empty()
    x.orElse(y)
}
```

```kotlin:ank
import arrow.core.ListK
import arrow.core.extensions.listk.monadCombine.monadCombine
import arrow.core.k

ListK.monadCombine().run {
    val x = listOf(1, 2).k()
    val y = listOf(3, 4).k()
    x.orElse(y)
}
```

#### Kind<F, A>.alt

It is just an infix alias over `orElse`.

```kotlin:ank
import arrow.core.Option
import arrow.core.extensions.option.monadCombine.monadCombine

Option.monadCombine().run {
    val x: Option<Int> = Option.just(1)
    val y: Option<Int> = Option.empty()
    x alt y
}
```

### Laws

Arrow provides `AlternativeLaws` in the form of test cases for internal verifications of lawful instances and third party apps creating their own `Alternative` instances.

## Available Instances:

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Alternative

TypeClass(Alternative::class).dtMarkdownList()
```

Additionally all the instances of [`MonadCombine`]({{ '/docs/arrow/typeclasses/monadcombine' | relative_url }}) implement the `Alternative` directly since it is subtype of `Alternative`.

ank_macro_hierarchy(arrow.typeclasses.Alternative)
