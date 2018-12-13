---
layout: docs
title: Libraries
permalink: /docs/quickstart/libraries/
---

## Core Libraries

Arrow is a modular set of libraries that build on top of each other to provide increasingly higher level features.

One of our design principles is to keep each library as lean as possible to avoid pulling unnecessary dependencies,
specially to support Android development where app size affects performance. You're free to pick and choose only those libraries that your project needs!

In this doc we'll describe all the modules that form the core, alongside a list of the most important constructs they include.

### arrow-core

{:.beginner}
beginner

The smallest set of [datatypes]({{ '/docs/datatypes/intro/' | relative_url }}) necessary to start in FP, and that other libraries can build upon.
The focus here is on API design and abstracting small code patterns.

Datatypes: [`Either`]({{ '/docs/arrow/core/either/' | relative_url }}), [`Option`]({{ '/docs/arrow/core/option/' | relative_url }}), [`Try`]({{ '/docs/arrow/core/try/' | relative_url }}), [`Eval`]({{ '/docs/arrow/core/eval/' | relative_url }}), [`Id`]({{ '/docs/arrow/core/id/' | relative_url }}), `TupleN`, `Function0`, `Function1`, `FunctionK`

### arrow-syntax

{:.beginner}
beginner

Multiple extensions functions to work better with function objects and collections.

Dependencies: arrow-core

For function objects the library provides composition, currying, partial application, memoization, pipe operator, complement for predicates, and several more helpers.

For collections, arrow-syntax provides `firstOption`, tail, basic list traversal, and tuple addition.

### arrow-typeclasses

{:.intermediate}
intermediate

All the basic [typeclasses]({{ '/docs/typeclasses/intro' | relative_url }}) that can compose into a simple program.

Dependencies: arrow-core

Datatypes: [`Const`]({{ '/docs/typeclasses/const/' | relative_url }})

Typeclasses: [`Alternative`]({{ '/docs/arrow/typeclasses/alternative/' | relative_url }}), [`Bimonad`]({{ '/docs/arrow/typeclasses/bimonad/' | relative_url }}), [`Inject`]({{ '/docs/typeclasses/inject/' | relative_url }}), [`Reducible`]({{ '/docs/arrow/typeclasses/reducible/' | relative_url }}), [`Traverse`]({{ '/docs/arrow/typeclasses/traverse/' | relative_url }}), [`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }}), [`Comonad`]({{ '/docs/arrow/typeclasses/comonad/' | relative_url }}), [`Eq`]({{ '/docs/arrow/typeclasses/eq/' | relative_url }}), [`Monad`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }}), [`Monoid`]({{ '/docs/arrow/typeclasses/monoid/' | relative_url }}), [`Semigroup`]({{ '/docs/arrow/typeclasses/semigroup/' | relative_url }}), [`ApplicativeError`]({{ '/docs/arrow/typeclasses/applicativeerror/' | relative_url }}), [`Foldable`]({{ '/docs/arrow/typeclasses/foldable/' | relative_url }}), [`MonoidK`]({{ '/docs/arrow/typeclasses/monoidk/' | relative_url }}), [`SemigroupK`]({{ '/docs/arrow/typeclasses/semigroupk/' | relative_url }}), [`Bifoldable`]({{ '/docs/arrow/typeclasses/bifoldable/' | relative_url }}), [`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }}), [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror/' | relative_url }}), [`Order`]({{ '/docs/arrow/typeclasses/order/' | relative_url }}), [`Show`]({{ '/docs/arrow/typeclasses/show/' | relative_url }}), `Composed`

### arrow-data

{:.beginner}
beginner

This library focuses on expanding the helpers provided by typeclasses to existing constructs, like the system collections.
You can also find more advanced constructs for pure functional programming like the `RWS` datatypes, or transformers.

Dependencies: arrow-typeclasses

Datatypes: [`Cokleisli`]({{ '/docs/datatypes/cokleisli/' | relative_url }}), [`Coreader`]({{ '/docs/datatypes/coreader/' | relative_url }}), [`Ior`]({{ '/docs/arrow/data/ior/' | relative_url }}), [`ListK`]({{ '/docs/arrow/data/listk/' | relative_url }}), [`NonEmptyList`]({{ '/docs/arrow/data/nonemptylist/' | relative_url }}), [`SequenceK`]({{ '/docs/arrow/data/sequencek/' | relative_url }}), [`SortedMapK`]({{ '/docs/arrow/data/sortedmapk/' | relative_url }}), [`StateT`]({{ '/docs/arrow/data/statet/' | relative_url }}), [`WriterT`]({{ '/docs/arrow/data/writert/' | relative_url }}), [`Coproduct`]({{ '/docs/arrow/data/coproduct/' | relative_url }}), [`EitherT`]({{ '/docs/arrow/data/eithert/' | relative_url }}), [`Kleisli`]({{ '/docs/arrow/data/kleisli/' | relative_url }}), [`MapK`]({{ '/docs/arrow/data/mapk/' | relative_url }}), [`OptionT`]({{ '/docs/arrow/data/optiont/' | relative_url }}), [`Reader`]({{ '/docs/arrow/data/reader/' | relative_url }}), [`SetK`]({{ '/docs/arrow/data/setk/' | relative_url }}), [`State`]({{ '/docs/arrow/data/state/' | relative_url }}), [`Validated`]({{ '/docs/arrow/data/validated/' | relative_url }})

### arrow-instances-(core, data)

{:.intermediate}
intermediate

These two libraries include the possible [typeclass instances]({{ '/docs/patterns/glossary/#instances' | relative_url }}) that can be implemented for the datatypes in arrow-core and arrow-data, and some basic types.

Dependencies: arrow-typeclasses, and either arrow-core or arrow-data

### arrow-mtl

{:.advanced}
advanced

Advanced [typeclasses]({{ '/docs/typeclasses/intro' | relative_url }}) to be used in programs using the Tagless-final architecture.

It also includes the instances available for datatypes in both arrow-core and arrow-data

Dependencies: arrow-instances-data

Typeclasses: [`FunctorFilter`]({{ '/docs/arrow/mtl/typeclasses/functorfilter/' | relative_url }}), [`MonadFilter`]({{ '/docs/arrow/mtl/typeclasses/monadfilter/' | relative_url }}), [`MonadReader`]({{ '/docs/arrow/mtl/typeclasses/monadreader/' | relative_url }}), [`MonadWriter`]({{ '/docs/arrow/mtl/typeclasses/monadwriter/' | relative_url }}), [`MonadCombine`]({{ '/docs/arrow/mtl/typeclasses/monadcombine/' | relative_url }}), [`MonadState`]({{ '/docs/arrow/mtl/typeclasses/monadstate' | relative_url }}), [`TraverseFilter`]({{ '/docs/arrow/mtl/typeclasses/traversefilter/' | relative_url }})

## Extension libraries

These libraries are hosted inside the arrow repository building on the core, to provide higher level constructs to deal with concepts rather than code abstraction.

### arrow-optics

{:.beginner}
beginner

Optics is the functional way of handling immutable data and collections in a way that's boilerplate free and efficient.

It can be used alongside annotation processing to generate [simple DSLs]({{ '/docs/optics/dsl/' | relative_url }}) that read like imperative code.

For all the new typeclasses it also includes the instances available for basic types and datatypes in both arrow-core and arrow-data.

Datatypes: [`Fold`]({{ '/docs/optics/fold/' | relative_url }}), [`Getter`]({{ '/docs/optics/getter/' | relative_url }}), [`Iso`]({{ '/docs/optics/iso/' | relative_url }}), [`Lens`]({{ '/docs/optics/lens/' | relative_url }}), [`Optional`]({{ '/docs/optics/optional/' | relative_url }}), [`Prism`]({{ '/docs/optics/prism/' | relative_url }}), [`Setter`]({{ '/docs/optics/setter/' | relative_url }}), [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }})

Typeclasses: [`At`]({{ '/docs/optics/at/' | relative_url }}), [`Each`]({{ '/docs/optics/each/' | relative_url }}), [`FilterIndex`]({{ '/docs/optics/filterIndex/' | relative_url }}), [`Index`]({{ '/docs/optics/index/' | relative_url }})

### arrow-effects

{:.intermediate}
intermediate

The effects library abstracts over concurrency frameworks using typeclasses. Additionally it provides its own concurrency primitive, called IO.

Datatypes: [`IO`]({{ '/docs/effects/io/' | relative_url }})

Typeclasses: [`MonadDefer`]({{ '/docs/effects/monaddefer/' | relative_url }}), [`Async`]({{ '/docs/effects/async/' | relative_url }}), [`Effect`]({{ '/docs/effects/effect/' | relative_url }})


### arrow-effects-(rx2, reactor, kotlinx-coroutines)

{:.intermediate}
intermediate

Each of these modules provides wrappers over the datatypes in each of the libraries that implement all the typeclasses provided by arrow-effects

[Rx]({{ 'docs/integrations/rx2/' | relative_url }}): `Observable`, `Flowable`, `Single`

[Reactor]({{ 'docs/integrations/reactor/' | relative_url }}): `Flux`, `Mono`

[kotlinx.coroutines]({{ 'docs/integrations/kotlinxcoroutines/' | relative_url }}): `Deferred`

### arrow-recursion

{:.advanced}
advanced

Recursion schemes is a construct to work with recursive data structures in a way that decuples structure and data, and allows for ergonomy and performance improvements.

Datatypes: [`Fix`]({{ '/docs/recursion/fix/' | relative_url }}), [`Mu`]({{ '/docs/recursion/mu/' | relative_url }}), [`Nu`]({{ '/docs/recursion/nu/' | relative_url }})

Typeclasses: [`Corecursive`]({{ '/docs/recursion/corecursive/' | relative_url }}), [`Recursive`]({{ '/docs/recursion/recursive/' | relative_url }}), [`Birecursive`]({{ '/docs/recursion/birecursive/' | relative_url }})

### arrow-integration-retrofit-adapter

{:.advanced}
advanced

The [adapter]({{ 'docs/integrations/retrofit/' | relative_url }}) is a library that adds integration with Retrofit, providing extensions functions and/or classes to work with Retrofit by encapsulating the responses in the chosen datatypes, through the use of typeclasses.

### arrow-free

{:.advanced}
advanced

The [Free datatype]({{ '/docs/free/free/' | relative_url }}) is a way of interpreting domain specific languages from inside your program, including a configurable runner and flexible algebras.
This allows optimization of operations like operator fusion or parallelism, while remaining on your business domain.

Datatypes: [`Free`]({{ '/docs/free/free/' | relative_url }}), [`FreeApplicative`]({{ '/docs/free/freeapplicative/' | relative_url }}), [`Cofree`]({{ '/docs/free/cofree/' | relative_url }}), [`Yoneda`]({{ '/docs/free/yoneda/' | relative_url }}), [`Coyoneda`]({{ '/docs/free/coyoneda/' | relative_url }})

## Annotation processors

These libraries focus on meta-programming to generate code that enables other libraries and constructs.

### arrow-generic

{:.advanced}
advanced

It allows anonating data classes with [`@product`]({{ '/docs/generic/product/' | relative_url }}) to enable them to be structurally deconstructed in tuples and heterogeneous lists.
