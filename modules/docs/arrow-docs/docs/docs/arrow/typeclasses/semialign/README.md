---
layout: docs
title: Semialign
permalink: /docs/arrow/typeclasses/semialign/
redirect_from:
  - /docs/typeclasses/semialign/
---

## Semialign

{:.beginner}
beginner

The `Semialign` typeclass lets us combine two structures of type `Kind<F, A>` and `Kind<F, B>` 
into a single type `Kind<F, Ior<A,B>>`. 
The type `Ior<A,B>` thats used to hold the elements allows either side to be absent. This allows to combine the two structures
without truncating to the size of the smaller input.

### Main Combinators

#### align

Combines two structures by taking the union of their shapes and using Ior to hold the elements.

`fun <A, B> align(left: Kind<F, A>, right: Kind<F, B>): Kind<F, Ior<A, B>>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.semialign.semialign
import arrow.core.*

ListK.semialign().run {
    align(listOf("A", "B").k(), listOf(1, 2, 3).k())
}
```

#### alignWith

combines two structures by taking the union of their shapes and combining the elements with the given function.

`fun <A, B, C> alignWith(a: Kind<F, A>, b: Kind<F, B>, fa: (Ior<A, B>) -> C): Kind<F, C>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.semialign.semialign
import arrow.core.*

ListK.semialign().run {
    alignWith(listOf("A", "B").k(), listOf(1, 2, 3).k()) {
        "$it"
    }
}
```

### Laws

Arrow provides [`SemialignLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Semialign instances.

#### Creating your own `Semialign` instances

Arrow already provides Semialign instances for common datatypes (e.g. Option, ListK, MapK). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`Semialign`]({{ '/docs/arrow/typeclasses/semialign' | relative_url }}) implement the `Functor` typeclass directly
since they are all subtypes of `Functor`

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Semialign

TypeClass(Semialign::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Semialign)

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Semialign.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/SemialignLaws.kt
