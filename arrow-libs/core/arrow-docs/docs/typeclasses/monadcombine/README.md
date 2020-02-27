---
layout: docs-core
title: MonadCombine
permalink: /arrow/typeclasses/monadcombine/
---

## MonadCombine




If [`Alternative`]({{ '/arrow/typeclasses/alternative' | relative_url }}) is for [`Applicative` functors]({{ '/arrow/typeclasses/applicative' | relative_url }}) that also have a [`Monoid`]({{ '/arrow/typeclasses/monoid' | relative_url }}) structure, `MonadCombine` is for [`Monads`]({{ '/arrow/typeclasses/monad' | relative_url }}) with monoid capabilities.

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
