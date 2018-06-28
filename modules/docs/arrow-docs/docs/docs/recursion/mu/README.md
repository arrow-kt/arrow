---
layout: docs
title: Mu
permalink: /docs/recursion/mu/
---

## Mu

{:.advanced}
advanced

The Mu datatype is the simplest way to model recursion via a direct encoding of the
cata function.

```kotlin
@higherkind
abstract class Mu<out F> : MuOf<F> {
  abstract fun <A> unMu(fa: Algebra<F, Eval<A>>): Eval<A>
}
```

`unMu` is isomorphic to cata.

### Comparison to Fix and Nu

Mu guarantees that a datatype will be finite, and should be used for any algorithms
that will only work on finite data (for example `sum`). Due to it being a direct encoding
of cata, Mu also has a very fast Recursive instance.

## Avaliable Instances:

- [Recursive]({{ '/docs/recursion/recursive' | relative_url }})
- [Corecursive]({{ '/docs/recursion/recursive' | relative_url }})
- [Birecursive]({{ '/docs/recursion/recursive' | relative_url }})
