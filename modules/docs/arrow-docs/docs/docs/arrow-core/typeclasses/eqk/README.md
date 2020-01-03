---
layout: docs-core
title: EqK
permalink: /docs/arrow/typeclasses/eqk/
redirect_from:
  - /docs/typeclasses/eqk/
---

## EqK




The `EqK` typeclass abstracts the ability to lift the Eq class to unary type constructors.

### Main Combinators

#### Kind<F, A>.eqK(other: Kind<F, A>, EQ: Eq<A>): Boolean

Compares two instances of `A` in the context of `F` using the provided `Eq<A>`. Returns true if they're considered equal.

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.option.eqK.eqK
import arrow.core.*

// lift String Eq to Option
Option.eqK().run {
    Option.just("hello").eqK(Option.just("arrow"), String.eq())
}
```

### Laws

Arrow provides `EqKLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `EqK` instances.
See the existing EqK instances implementations and accompanying tests for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `EqK` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.EqK

TypeClass(EqK::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.EqK)
