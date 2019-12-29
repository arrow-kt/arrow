---
layout: docs-core
title: Crosswalk
permalink: /docs/arrow/typeclasses/crosswalk/
redirect_from:
  - /docs/typeclasses/crosswalk/
---

## Crosswalk




The `Crosswalk` typeclass extends the `Functor` and `Foldable` typeclass with the possibility to traverse the
structure through an alignable functor.

### Main Combinators

#### crosswalk()

`fun <F, A, B> crosswalk(ALIGN: Align<F>, fa: (A) -> Kind<F, B>, a: Kind<T, A>): Kind<F, Kind<T, B>>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.crosswalk.crosswalk
import arrow.core.extensions.listk.align.align
import arrow.core.*

ListK.crosswalk().run {
    crosswalk(ListK.align(), listOf("1:2:3:4:5", "6:7:8:9:10", "11:12").k()) {
        it.split(":").k()
    }
}
```

#### sequenceL

`fun <F, A> sequenceL(ALIGN: Align<F>, tfa: Kind<T, Kind<F, A>>): Kind<F, Kind<T, A>>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.crosswalk.crosswalk
import arrow.core.extensions.listk.align.align
import arrow.core.*

ListK.crosswalk().run {
    val lists = listOf(listOf(1, 2, 3, 4, 5).k(),
                       listOf(6, 7, 8, 9, 10).k(),
                       listOf(11, 12).k())

    sequenceL(ListK.align(), lists.k())
}
```

### Laws

Arrow provides [`CrosswalkLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Crosswalk instances.

#### Creating your own `Crosswalk` instances

Arrow already provides Crosswalk instances for common datatypes (e.g. Option, ListK). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`Crosswalk`]({{ '/docs/arrow/typeclasses/crosswalk' | relative_url }}) implement the `Functor` and `Foldable`  typeclass directly.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Crosswalk

TypeClass(Crosswalk::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Crosswalk)

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Crosswalk.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/CrosswalkLaws.kt
