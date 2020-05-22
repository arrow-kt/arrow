---
layout: docs-core
title: FunctorFilter
permalink: /arrow/typeclasses/functorfilter/
---

## FunctorFilter




The `FunctorFilter` typeclass is useful when you need to `map` and filter out elements simultaneously.
This doc focuses on the methods provided by the typeclass.

### Main Combinators

`FunctorFilter` includes all combinators present in [`Functor`]({{ '/arrow/typeclasses/functor/' | relative_url }}).

####  Kind<F, A>#filterMap

A combined map and filter. Filtering is handled via `Option` instead of `Boolean` such that the output type `B` can be different than the input type `A`.

```kotlin:ank
import arrow.core.*

Some(1).filterMap { None }
```

#### Kind<F, Option<A>>#flattenOption

**Flatten** out a structure by collapsing Options.

```kotlin:ank
import arrow.core.extensions.option.functorFilter.flattenOption

Some(1).map { None }.flattenOption()
```

#### Kind<F, A>#filter

Apply a filter to a structure such that the output structure contains all `A` elements in the input structure that satisfy the predicate `f` but none that don't.

```kotlin:ank
Some(1).filter { false }
```

### Laws

Arrow provides [`FunctorFilterLaws`][functor_filter_law_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own `FunctorFilter` instances.

#### Creating your own `FunctorFilter` instances

Arrow already provides `FunctorFilter` instances for most common datatypes both in Arrow and the Kotlin stdlib.

See [Deriving and creating custom typeclass]({{ '/patterns/glossary' | relative_url }}) to provide your own `FunctorFilter` instances for custom datatypes.


### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.FunctorFilter

TypeClass(FunctorFilter::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.FunctorFilter)

[functor_filter_law_source]: https://github.com/arrow-kt/arrow-core/blob/master/arrow-core-test/src/main/kotlin/arrow/core/test/laws/FunctorFilterLaws.kt

