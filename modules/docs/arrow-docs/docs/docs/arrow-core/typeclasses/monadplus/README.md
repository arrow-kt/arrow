---
layout: docs-core
title: MonadPlus
permalink: /docs/arrow/typeclasses/monadplus/
redirect_from:
  - /docs/typeclasses/monadplus/
---

## MonadPlus

The `MonadPlus` typeclass lets us combine two structures of type `Kind<F, A>` with a associative function `plusM`
into a `Kind<F, A>`. It also provides the function `zeroM` which acts as a neutral argument to the `plusM` function. 

### Main Combinators

#### Kind<F, A>.plusM(other: Kind<F, A>): Kind<F, A>

The `plusM` operations allows us to combine two structures into a single one:

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.monadPlus.monadPlus
import arrow.core.*

ListK.monadPlus().run {
    listOf(1, 2).k().plusM(listOf(3, 4, 5).k())
}
```

#### zeroM(): Kind<F, A>

The value returned by `zeroM` acts as a neutral argument
for the `plusM` operation

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.listk.monadPlus.monadPlus
import arrow.core.*

ListK.monadPlus().run {
    listOf(1, 2).k().plusM(zeroM())
}
```

### Laws

Arrow provides [`MonadPlusLaws`][tc_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own MonadPlus instances.

#### Creating your own `MonadPlus` instances

Arrow already provides MonadPlus instances for common datatypes (e.g. Option, ListK). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`MonadPlus`]({{ '/docs/arrow/typeclasses/monadplus' | relative_url }}) implement the `Monad` typeclass directly
since they are all subtypes of `Monad`

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.MonadPlus

TypeClass(MonadPlus::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.MonadPlus)

[tc_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/MonadPlus.kt
[tc_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/MonadPlusLaws.kt
