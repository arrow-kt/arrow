---
layout: docs
title: Fix
permalink: /docs/recursion/fix/
---

## Fix

{:.advanced}
advanced

The fix datatype is the simplest way to model birecursion via a direct encoding of
the `project` and `embed` functions.

```kotlin
@higherkind
data class Fix<out A>(val unfix: Kind<A, Eval<FixOf<A>>>) : FixOf<A>
```

`embed` is isomorphic to the `Fix` constructor, and `project` is isomorphic to `unfix`.

### Comparison to Mu and Nu

If Mu is `A` and Nu is `A?`, then fix is `A!`. Fix provides no information about whether
or not a datatype is infinite, and is generally less safe that Mu or Nu. However, due
to its simplicity, both Recursive and Corecursive instances for fix are very fast.

## Avaliable Instances:

- [Recursive]({{ '/docs/recursion/recursive' | relative_url }})
- [Corecursive]({{ '/docs/recursion/recursive' | relative_url }})
- [Birecursive]({{ '/docs/recursion/recursive' | relative_url }})
