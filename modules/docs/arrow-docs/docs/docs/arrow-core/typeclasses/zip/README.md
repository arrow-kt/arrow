---
layout: docs-core
title: Zip
permalink: /docs/arrow/typeclasses/zip/
redirect_from:
  - /docs/typeclasses/zip/
---

## Zip




The `Zip` typeclass extends the `Semialign` typeclass with a function that takes the intersection of non-uniform shapes.

### Main Combinators

#### zip

Combines two structures by taking the intersection of their shapes
and using `Tuple2` to hold the elements.

`fun <A, B> Kind<F, A>.zip(other: Kind<F, B>): Kind<F, Tuple2<A, B>>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.zip.zip
import arrow.core.*

ListK.zip().run {
    listOf("A", "B").k().zip(listOf(1, 2, 3).k())
}
```

#### zipWith

Combines two structures by taking the intersection of their shapes
and then combines the elements with the given function.

`fun <A, B, C> Kind<F, A>.zipWith(other: Kind<F, B>, f: (A, B) -> C): Kind<F, C>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.zip.zip
import arrow.core.*

ListK.zip().run {
    listOf("A", "B").k().zipWith(listOf(1, 2, 3).k()) {
        a, b -> "$a # $b"
    }
}
```

### Laws

Arrow provides [`ZipLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Zip instances.

#### Creating your own `Zip` instances

Arrow already provides Zip instances for common datatypes (e.g. Option, ListK, MapK). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`Zip`]({{ '/docs/arrow/typeclasses/zip' | relative_url }}) implement the `Semialign` typeclass directly
since they are all subtypes of `Semialign`

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Zip

TypeClass(Zip::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Zip)

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Zip.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/ZipLaws.kt
