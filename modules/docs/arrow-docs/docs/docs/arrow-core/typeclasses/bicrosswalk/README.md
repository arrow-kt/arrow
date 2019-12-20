---
layout: docs-core
title: Bicrosswalk
permalink: /docs/arrow/typeclasses/bicrosswalk/
redirect_from:
  - /docs/typeclasses/bicrosswalk/
---

## Bicrosswalk




The `Bicrosswalk` typeclass extends the `Bifunctor` and `Bifoldable` typeclass with the possibility to traverse the
structure through an alignable functor.

### Main Combinators

#### bicrosswalk

`fun <A,B,C,D> bicrosswalk(ALIGN: Align<F>, fa: (A) -> Kind<F, C>, fb: (B) -> Kind<F, D>, tab: Kind2<T, A, B>): Kind<F, Kind2<T, C, D>>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.either.bicrosswalk.bicrosswalk
import arrow.core.extensions.listk.align.align
import arrow.core.*

Either.bicrosswalk().run {
    val either = Either.Right("arrow")
    bicrosswalk(ListK.align(), either, {ListK.just("fa($it)")}) {ListK.just("fb($it)")}
}
```

#### sequenceL

`fun <F, A, B> bisequenceL(ALIGN: Align<F>, tab: Kind2<T, Kind<F, A>, Kind<F, B>>): Kind<F, Kind2<T, A, B>>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.either.bicrosswalk.bicrosswalk
import arrow.core.extensions.listk.align.align
import arrow.core.*

Either.bicrosswalk().run {
    val either: Either<ListK<Int>, ListK<String>> = Either.Right(listOf("hello", "arrow").k())
    bisequenceL(ListK.align(), either)
}
```

### Laws

Arrow provides [`BicrosswalkLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Bicrosswalk instances.

#### Creating your own `Bicrosswalk` instances

Arrow already provides Bicrosswalk instances for common datatypes (e.g. Either, Ior). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`Bicrosswalk`]({{ '/docs/arrow/typeclasses/bicrosswalk' | relative_url }}) implement the `Bifunctor` and `Bifoldable` typeclass directly.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Bicrosswalk

TypeClass(Bicrosswalk::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Bicrosswalk)

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Bicrosswalk.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/BicrosswalkLaws.kt
