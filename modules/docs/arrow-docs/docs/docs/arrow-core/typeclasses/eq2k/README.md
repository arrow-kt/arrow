---
layout: docs-core
title: Eq2K
permalink: /docs/arrow/typeclasses/eq2k/
redirect_from:
  - /docs/typeclasses/eq2k/
---

## Eq2K

The `Eq2K` typeclass abstracts the ability to lift the Eq class to binary type constructors.

### Main Combinators

#### Kind2<F, A, B>.eqK(other: Kind2<F, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean

Compares two instances of a type with binary typeconstructor in the context of `F` using the provided `Eq<A>` and `Eq<B>`. Returns true if they're considered equal.

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.either.eq2K.eq2K
import arrow.core.*

// lift String Eq to Either
Either.eq2K().run {
    Either.right("hello").eqK(Either.right("arrow"), String.eq(), String.eq())
}
```

### Laws

Arrow provides `Eq2KLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Eq2K` instances.
See the existing EqK instances implementations and accompanying tests for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Eq2K` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Eq2K

TypeClass(Eq2K::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Eq2K)
