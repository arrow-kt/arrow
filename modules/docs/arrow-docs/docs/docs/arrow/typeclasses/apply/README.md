---
layout: docs
title: Apply
permalink: /docs/arrow/typeclasses/apply/
redirect_from:
  - /docs/typeclasses/apply/
---

## Apply

`Apply` extends the `Functor` type class (which features the familiar `map` function) with a new function `ap`. The `ap` function is similar to `map` in that we are transforming a value in a context (a context being the `F` in `Kind<F, A>`; 
a context can be `Option`, `List` or `Promise` for example).
However, the difference between `ap` and `map` is that for `ap` the function that takes care of the transformation is of type `Kind<F, (A) -> B>`, whereas for `map` it is `(A) -> B`.

Here are the implementations of `Apply` for the `Option` and `List` types:

```kotlin:ank:playground
@extension
interface OptionApply : Apply<ForOption> {
  override fun <A, B> OptionOf<A>.ap(ff: OptionOf<(A) -> B>): Option<B> =
    ff.fix().flatMap { this.fix().map(it) }

  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    fix().map(f)
}

@extension
interface ListKApply : Apply<ForListK> {
  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    ff.fix().flatMap { f -> map(f) }

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)
}
```
