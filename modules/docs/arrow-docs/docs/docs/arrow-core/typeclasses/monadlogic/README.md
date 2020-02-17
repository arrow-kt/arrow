---
layout: docs-core
title: MonadLogic
permalink: /docs/arrow/typeclasses/monadlogic/
redirect_from:
  - /docs/typeclasses/monadlogic/
---

## MonadLogic

The `MonadLogic` typeclass provides some functions to control when computations should be performed.

### Main Combinators

#### Kind<F, A>.splitM(): Kind<F, Option<Tuple2<Kind<F, A>, A>>>

Splits a computations into its first result and another computation that computes the rest.

#### <F, A>.interleave(other: Kind<F, A>): Kind<F, A>

Some computations can have an infinite number of potential results. Such computations can cause problems in 
some circumstances as combined computations may never terminate. `Interleave` allows to combine computations
in a way that fair consideration of branches is ensured.

#### Kind<F, A>.unweave(ffa: (A) -> Kind<F, B>): Kind<F, B>

#### Kind<F, A>.ifThen(fb: Kind<F, B>, ffa: (A) -> Kind<F, B>): Kind<F, B>

Logical conditional. The equivalent of Prolog's soft-cut. If its first argument succeeds at all, 
then the results will be fed into the success branch. Otherwise, the failure branch is taken.

#### Kind<F, A>.once(): Kind<F, A>

Pruning. Selects one result out of many. Useful for when multiple results of a computation will be equivalent, or should be treated as such.

#### Kind<F, A>.voidIfValue(): Kind<F, Unit>

Inverts a logic computation. If F succeeds with at least one value, voidIfValue m fails. If F fails, then voidIfValue F succeeds the value Unit.

#### Kind<ForOption, Tuple2<Kind<F, A>, A>>.reflect(ML: MonadLogic<F>): Kind<F, A>

The inverse of splitM.

### Laws

Arrow provides [`MonadLogicLaws`][tc_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own MonadLogic instances.

#### Creating your own `MonadLogic` instances

Arrow already provides MonadLogic instances for common datatypes (e.g. ListK, SequenceK). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`MonadLogic`]({{ '/docs/arrow/typeclasses/monadlogic' | relative_url }}) implement the `MonadPlus` typeclass directly
since they are all subtypes of `MonadPlus`

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.MonadLogic

TypeClass(MonadLogic::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.MonadLogic)

[tc_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/MonadLogic.kt
[tc_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/MonadLogicLaws.kt
