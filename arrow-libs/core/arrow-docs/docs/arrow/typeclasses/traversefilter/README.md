---
layout: docs-core
title: TraverseFilter
permalink: /arrow/typeclasses/traversefilter/
---

## TraverseFilter




`TraverseFilter` is helpful when you want to combine `Traverse` and `FunctorFilter` as one combined operation.
This doc focuses on the methods provided by the typeclass.

### Main Combinators

`TraverseFilter` includes all combinators present in [`Traverse`]({{ '/arrow/typeclasses/traverse/' | relative_url }})
and [`FunctorFilter`]({{ '/arrow/typeclasses/functorfilter/' | relative_url }}).

#### Kind<F, A>#traverseFilter

Returns `F<B>` in `G` context by applying `AP` on a selector function `f`, which returns `Option` of `B` in `G` context.

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.traverseFilter

Some(1).traverseFilter(Id.applicative()) { Id.just(None) }
Some(1).traverseFilter(Id.applicative()) { Id.just((it * 2).some()) }
```

#### Kind<F, A>#filterA

Returns `F<A>` in `G` context by applying `GA` on a selector function `f` in `G` context.

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.option.traverseFilter.filterA

Some(1).filterA({ Id.just(false) }, Id.applicative())
```

#### Kind<F, A>#traverseFilterIsInstance

Filter out instances of a specific type and traverse a context.

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.option.traverseFilter.traverseFilterIsInstance

Some(1).traverseFilterIsInstance(Id.applicative(), Int::class.java)
```

### Laws

Arrow provides [`TraverseFilterLaws`][travers_filter_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own `TraverseFilter` instances.

#### Creating your own `TraverseFilter` instances

Arrow already provides `TraverseFilter` instances for most common datatypes both in Arrow and the Kotlin stdlib.
Oftentimes, you may find the need to provide your own for unsupported datatypes.

See [Deriving and creating custom typeclass]({{ '/patterns/glossary' | relative_url }})

[travers_filter_laws_source]: https://github.com/arrow-kt/arrow-core/blob/master/arrow-core-test/src/main/kotlin/arrow/core/test/laws/TraverseFilterLaws.kt
