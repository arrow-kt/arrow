---
layout: docs-incubator
title: MonadTrans
permalink: /docs/arrow/mtl/typeclasses/monadtrans/
redirect_from:
  - /docs/typeclasses/monadtrans/
---

## MonadTrans

MonadTrans is a typeclass that abstracts lifting arbitray monadic computations in another context.

```kotlin:ank
import arrow.mtl.extensions.optiont.monadTrans.monadTrans
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.*
import arrow.core.*
import arrow.mtl.*

OptionT.monadTrans().run {
  Id.just("hello").liftT(Id.monad())
}
```

### Laws

Arrow provides [`MonadTransLaws`][laws_source]{:target="_blank"} in the form of test cases for internal 
verification of lawful instances and third party apps creating their own `MonadTrans` instances.

#### Creating your own `MonadTrans` instances

Arrow already provides MonadTrans instances for OptionT. See the implementation
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.mtl.typeclasses.MonadTrans

TypeClass(MonadTrans::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.mtl.typeclasses.MonadTrans)

[laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/MonadTransLaws.kt
