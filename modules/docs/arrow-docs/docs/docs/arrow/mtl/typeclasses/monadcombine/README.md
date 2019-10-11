---
layout: docs
title: MonadCombine
permalink: /docs/arrow/typeclasses/monadcombine/
redirect_from:
  - /docs/typeclasses/monadcombine/
---

## MonadCombine

{:.advanced}
advanced

If [`Alternative`]({{ '/docs/arrow/typeclasses/alternative' }}) is for [`Applicative` functors]({{ '/docs/arrow/typeclasses/applicative' }}) that also have a [`Monoid`]({{ '/docs/arrow/typeclasses/monoid' }}) structure, `MonadCombine` is for [`Monads`]({{ '/docs/arrow/typeclasses/monad' }}) with monoid capabilities.

`MonadCombine` is just a claim indicating something is both an `Alternative` and a `Monad`.

### Main Combinators

#### Kind<F, Kind<G, A>>.unite()

`fun <G, A> Kind<F, Kind<G, A>>.unite(FG: Foldable<G>): Kind<F, A>`

This function takes "interesting" values from the inner G context and combines them into a flat F context.

```kotlin:ank
import arrow.core.ListK
import arrow.core.Option
import arrow.core.extensions.listk.monadCombine.monadCombine
import arrow.core.extensions.option.applicative.just
import arrow.core.extensions.option.foldable.foldable
import arrow.core.k

val f = listOf(1.just(), Option.empty(), 2.just()).k()
ListK.monadCombine().run {
    f.unite(Option.foldable())
}
```

#### Kind<F, Kind2<G, A, B>>.separate()

`fun <G, A, B> Kind<F, Kind2<G, A, B>>.separate(BG: Bifoldable<G>): Tuple2<Kind<F, A>, Kind<F, B>>`

This function takes A and B values from inner Bifoldable G and returns them separated into two flat F contexts.

```kotlin:ank
import arrow.core.Either
import arrow.core.ListK
import arrow.core.extensions.either.bifoldable.bifoldable
import arrow.core.extensions.listk.monadCombine.monadCombine
import arrow.core.k
import arrow.core.left
import arrow.core.right

val f = listOf(1.right(), 2.left(), 3.right(), 4.left()).k()
ListK.monadCombine().run {
    f.separate(Either.bifoldable())
}
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.MonadCombine

TypeClass(MonadCombine::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.MonadCombine)
