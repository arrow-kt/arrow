---
layout: docs-core
title: Traverse
permalink: /arrow/typeclasses/traverse/
---

## Traverse




The `Traverse` typeclass is allowing to commute types from `F<G<A>>` to `G<F<A>>` over sequential execution of code.
The main use of this is traversal over a structure with an effect.
This doc focuses on the methods provided by the typeclass.

### Main Combinators

`Traverse` includes all combinators present in [`Functor`]({{ '/arrow/typeclasses/functor/' | relative_url }})
and [`Foldable`]({{ '/arrow/typeclasses/foldable/' | relative_url }}).

#### Kind<F, A>#traverse

Given a function which returns a `G` effect, thread this effect through the running of this function on all the values
in `F`, returning an `F<B>` in a `G` context.

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.traverse

Some(1).traverse(Id.applicative()) { Id.just(it * 2) }
```

#### Kind<F, Kind<G, A>>#sequence

Thread all the `G` effects through the `F` structure to invert the structure from `F<G<A>>` to `G<F<A>>`.

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.id.applicative.applicative

Const<Int, Nothing>(1).sequence<Nothing, Int, ForId>(Id.applicative())
```

### Laws

Arrow provides [`TraverseLaws`][travers_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own `Traverse` instances.

#### Creating your own `Traverse` instances

Arrow already provides `Traverse` instances for most common datatypes both in Arrow and the Kotlin stdlib.
Oftentimes, you may find the need to provide your own for unsupported datatypes.

See [Deriving and creating custom typeclass]({{ '/patterns/glossary' | relative_url }})

[travers_laws_source]: https://github.com/arrow-kt/arrow-core/blob/master/arrow-core-test/src/main/kotlin/arrow/core/test/laws/TraverseLaws.kt