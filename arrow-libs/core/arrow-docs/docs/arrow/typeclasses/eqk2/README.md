---
layout: docs-core
title: EqK2
permalink: /arrow/typeclasses/eqk2/
---

## EqK2

The `EqK2` typeclass abstracts the ability to lift the Eq class to binary type constructors.

### Main Combinators

#### Kind2<F, A, B>.eqK(other: Kind2<F, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean

Compares two instances of a type `F` with a binary type constructor using the provided `Eq<A>` and `Eq<B>`. Returns true if they're considered equal.

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.either.eqK2.eqK2
import arrow.core.*

// lift String Eq to Either
Either.eqK2().run {
    Either.right("hello").eqK(Either.right("arrow"), String.eq(), String.eq())
}
```

### Laws

Arrow provides `EqK2Laws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `EqK2` instances.
See the existing EqK2 instances implementations and accompanying tests for reference.

See [Deriving and creating custom typeclass]({{ '/patterns/glossary' | relative_url }}) to provide your own `EqK2` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.EqK2

TypeClass(EqK2::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.EqK2)
