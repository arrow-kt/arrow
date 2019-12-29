---
layout: docs-core
title: Align
permalink: /docs/arrow/typeclasses/align/
redirect_from:
  - /docs/typeclasses/align/
---

## Align




The `Align` typeclass extends the `Semialign` typeclass with a value empty(), which acts as a unit in regards to align.

### Main Combinators

#### empty()

returns an empty structure which can be used as either argument for align.

`fun <A> empty(): Kind<F, A>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.align.align
import arrow.core.*

ListK.align().run {
    align(listOf("A", "B").k(), empty<String>())
}
```

### Laws

Arrow provides [`AlignLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Align instances.

#### Creating your own `Align` instances

Arrow already provides Align instances for common datatypes (e.g. Option, ListK, MapK). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`Align`]({{ '/docs/arrow/typeclasses/align' | relative_url }}) implement the `Semialign` typeclass directly
since they are all subtypes of `Semialign`

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Align

TypeClass(Align::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Align)

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Align.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/AlignLaws.kt
