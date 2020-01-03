---
layout: docs-core
title: Unalign
permalink: /docs/arrow/typeclasses/unalign/
redirect_from:
  - /docs/typeclasses/unalign/
---

## Unalign




The `Unlign` typeclass extends the `Semialign` typeclass with an inverse function to align: It splits a union shape
into a tuple representing the component parts.

### Main Combinators

#### unalign(ior: Kind<F, Ior<A, B>>): Tuple2<Kind<F, A>, Kind<F, B>>

splits a union into its component parts.

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.unalign.unalign
import arrow.core.*

ListK.unalign().run {
    unalign(listOf(("A" toT 1).bothIor(), ("B" toT 2).bothIor(), "C".leftIor()).k())
}
```

#### unalignWith(c: Kind<F, C>, fa: (C) -> Ior<A, B>): Tuple2<Kind<F, A>, Kind<F, B>>

after applying the given function, splits the resulting union shaped structure into its components parts

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.unalign.unalign
import arrow.core.*

ListK.unalign().run {
    unalignWith(listOf(1, 2, 3).k()) {
        it.leftIor()
    }
}
```

### Laws

Arrow provides [`UnalignLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Unalign instances.

#### Creating your own `Unalign` instances

Arrow already provides Unalign instances for common datatypes (e.g. Option, ListK, MapK). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`Unalign`]({{ '/docs/arrow/typeclasses/unalign' | relative_url }}) implement the `Semialign` typeclass directly
since they are all subtypes of `Semialign`

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Unalign

TypeClass(Unalign::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Unalign)

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Unalign.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/UnalignLaws.kt
