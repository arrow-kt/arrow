---
layout: docs-core
title: Alternative
permalink: /docs/arrow/typeclasses/alternative/
redirect_from:
  - /docs/typeclasses/alternative/
---

## Alternative




We use [`Option`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-option/' }}) to indicate a computation can fail somehow (that is, it can have either zero results or one result), and we use lists for computations that can have many possible results (ranging from zero to arbitrarily many results). In both of these cases, one useful operation is combining all possible results from multiple computations into a single computation. The `Alternative` type class captures this combination.

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

#### Kind<F, A>.some

`fun <A> Kind<F, A>.some(): Kind<F, SequenceK<A>>`

Repeats the current computation, lazily collecting its results into a sequence, until it fails. It is required that the computation succeeds at least once.

```kotlin:ank
import arrow.core.Option
import arrow.core.extensions.option.monadCombine.monadCombine

Option.monadCombine().run {
  val x = Option.just(1)
  x.some().map { it.take(5).toList() }
}
```

```kotlin:ank
import arrow.core.Option
import arrow.core.extensions.option.monadCombine.monadCombine

Option.monadCombine().run {
  val x = Option.empty<Int>()
  x.some().map { it.take(5).toList() }
}
```

#### Kind<F, A>.many

`fun <A> Kind<F, A>.many(): Kind<F, SequenceK<A>>`

Same function as some, but it does not require the computation to succeed.

```kotlin:ank
import arrow.core.Option
import arrow.core.extensions.option.monadCombine.monadCombine

Option.monadCombine().run {
  val x = Option.empty<Int>()
  x.many().map { it.take(5).toList() }
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
