---
layout: docs
title: Libraries
permalink: /docs/quickstart/libraries/
---

## Core Libraries

Arrow is a modular set of libraries that build on top of each other to provide increasingly higher level features.

One of our design principles is to keep each library as lean as possible to avoid pulling unnecessary dependencies,
specially to support Android development where app size affects performance. You're free to pick and choose only those libraries that your project needs!
Thereby, one only needs to download the toplevel artifacts if it's desired to download the datatypes, typeclasses and their respective extensions.

In this doc we'll describe all the modules that form the core, alongside a list of the most important constructs they include.

### arrow-core

```groovy
dependencies {
    compile "io.arrow-kt:arrow-core:$arrow_version"
}
```

This library include the possible [typeclass extensions]({{ '/docs/patterns/glossary/#instances-and-extensions-interfaces' | relative_url }}) including datatypes and typeclasses from `arrow-core-data` that can be implemented for the datatypes in their respective libraries.

Dependency: `arrow-core-data`

### arrow-core-data

```groovy
dependencies {
    compile "io.arrow-kt:arrow-core-data:$arrow_version"
}
```

The smallest set of [datatypes]({{ '/docs/datatypes/intro/' | relative_url }}) and all basic [typeclasses]({{ '/docs/typeclasses/intro' | relative_url }}) necessary to start in FP, and that other libraries can build upon.
The focus here is on API design and abstracting small code patterns.

Datatypes: [`Either`]({{ '/docs/arrow/core/either/' | relative_url }}), [`Option`]({{ '/docs/arrow/core/option/' | relative_url }}), [`Try`]({{ '/docs/arrow/core/try/' | relative_url }}), [`Eval`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-eval' | relative_url }}), [`Id`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-id/' | relative_url }}), [`Const`]({{ '/docs/typeclasses/const/' | relative_url }}), [`ListK`]({{ '/docs/arrow/data/listk/' | relative_url }}), [`NonEmptyList`]({{ '/docs/arrow/data/nonemptylist/' | relative_url }}), [`SequenceK`]({{ '/docs/arrow/data/sequencek/' | relative_url }}), [`SortedMapK`]({{ '/docs/arrow/data/sortedmapk/' | relative_url }}), [`Ior`]({{ '/docs/arrow/data/ior/' | relative_url }}), [`MapK`]({{ '/docs/arrow/data/mapk/' | relative_url }}), [`Validated`]({{ '/docs/arrow/data/validated/' | relative_url }}), [`SetK`]({{ '/docs/arrow/data/setk/' | relative_url }}), `TupleN`, `Function0`, `Function1`, `FunctionK`

Typeclasses: [`Alternative`]({{ '/docs/arrow/typeclasses/alternative/' | relative_url }}), [`Bimonad`]({{ '/docs/arrow/typeclasses/bimonad/' | relative_url }}), [`Inject`]({{ '/docs/typeclasses/inject/' | relative_url }}), [`Reducible`]({{ '/docs/arrow/typeclasses/reducible/' | relative_url }}), [`Traverse`]({{ '/docs/arrow/typeclasses/traverse/' | relative_url }}), [`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }}), [`Comonad`]({{ '/docs/arrow/typeclasses/comonad/' | relative_url }}), [`Eq`]({{ '/docs/arrow/typeclasses/eq/' | relative_url }}), [`Monad`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }}), [`Monoid`]({{ '/docs/arrow/typeclasses/monoid/' | relative_url }}), [`Semigroup`]({{ '/docs/arrow/typeclasses/semigroup/' | relative_url }}), [`ApplicativeError`]({{ '/docs/arrow/typeclasses/applicativeerror/' | relative_url }}), [`Foldable`]({{ '/docs/arrow/typeclasses/foldable/' | relative_url }}), [`MonoidK`]({{ '/docs/arrow/typeclasses/monoidk/' | relative_url }}), [`SemigroupK`]({{ '/docs/arrow/typeclasses/semigroupk/' | relative_url }}), [`Bifoldable`]({{ '/docs/arrow/typeclasses/bifoldable/' | relative_url }}), [`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }}), [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror/' | relative_url }}), [`Order`]({{ '/docs/arrow/typeclasses/order/' | relative_url }}), [`Show`]({{ '/docs/arrow/typeclasses/show/' | relative_url }}), [`TraverseFilter`]({{ '/docs/arrow/typeclasses/traversefilter/' | relative_url }}), [`FunctorFilter`]({{ '/docs/arrow/typeclasses/functorfilter/' | relative_url }}), [`MonadFilter`]({{ '/docs/arrow/typeclasses/monadfilter/' | relative_url }}), [`MonadCombine`]({{ '/docs/arrow/typeclasses/monadcombine/' | relative_url }}), `Composed`

### arrow-optics

```groovy
dependencies {
    compile "io.arrow-kt:arrow-optics:$arrow_version"
}
```

Optics is the functional way of handling immutable data and collections in a way that's boilerplate free and efficient.

Arrow Optics offers a way of declaring how to focus deeply into immutable structure without boilerplate. It also offers an [Optics DSL]({{ '/docs/optics/dsl/' | relative_url }})  to elegantly describe complex use-cases in an elegant and simple manner without requiring to understand the underlying theory.

For all the new typeclasses it also includes the extensions available for basic types and datatypes in both arrow-core and arrow-extras.

Datatypes: [`Fold`]({{ '/docs/optics/fold/' | relative_url }}), [`Getter`]({{ '/docs/optics/getter/' | relative_url }}), [`Iso`]({{ '/docs/optics/iso/' | relative_url }}), [`Lens`]({{ '/docs/optics/lens/' | relative_url }}), [`Optional`]({{ '/docs/optics/optional/' | relative_url }}), [`Prism`]({{ '/docs/optics/prism/' | relative_url }}), [`Setter`]({{ '/docs/optics/setter/' | relative_url }}), [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }})

Typeclasses: [`At`]({{ '/docs/optics/at/' | relative_url }}), [`Each`]({{ '/docs/optics/each/' | relative_url }}), [`FilterIndex`]({{ '/docs/optics/filterIndex/' | relative_url }}), [`Index`]({{ '/docs/optics/index/' | relative_url }})

### arrow-fx

```groovy
dependencies {
    compile "io.arrow-kt:arrow-fx:$arrow_version"
}
```

The [fx library]({{ '/docs/effects/fx/' | relative_url }}) offers a powerful concurrency DSL with an emphasis on easy concurrency and parallelism with guarantees about concurrent and parallel resource safety. It can be used with Arrow Fx's IO or a set of typeclasses to abstract over concurrency frameworks like `RxJava`, `Reactor`, `Arrow's IO`, etc.

Datatypes: [`IO`]({{ '/docs/effects/io/' | relative_url }})

Typeclasses: [`Fx`]({{ '/docs/effects/fx/' | relative_url }}), [`MonadDefer`]({{ '/docs/effects/monaddefer/' | relative_url }}), [`Async`]({{ '/docs/effects/async/' | relative_url }}), [`Effect`]({{ '/docs/effects/effect/' | relative_url }})

Dependency: `arrow-core`

## Extension libraries

These libraries are hosted inside the arrow repository building on the core, to provide higher level constructs to deal with concepts rather than code abstraction.

### arrow-syntax

```groovy
dependencies {
    compile "io.arrow-kt:arrow-syntax:$arrow_version"
}
```

Multiple extensions functions to work better with function objects and collections.

For function objects the library provides composition, currying, partial application, memoization, pipe operator, complement for predicates, and several more helpers.

For collections, arrow-syntax provides `firstOption`, tail, basic list traversal, and tuple addition.

Dependency: `arrow-core`

### arrow-fx-rx2 & arrow-fx-reactor

```groovy
dependencies {
    compile "io.arrow-kt:arrow-fx-rx2:$arrow_version"
    compile "io.arrow-kt:arrow-fx-reactor:$arrow_version"
}
```

Each of these modules provides wrappers over the datatypes in each of the libraries that implement all the typeclasses provided by arrow-fx

[Rx]({{ 'docs/integrations/rx2/' | relative_url }}): `Observable`, `Flowable`, `Single`

[Reactor]({{ 'docs/integrations/reactor/' | relative_url }}): `Flux`, `Mono`

Dependency: `arrow-fx`

### arrow-mtl

```groovy
dependencies {
    compile "io.arrow-kt:arrow-mtl:$arrow_version"
}
```

Advanced [typeclasses]({{ '/docs/typeclasses/intro' | relative_url }}) to be used in programs using the Tagless-final architecture.

It also includes the extensions available for datatypes in arrow-core 

Dependency: `arrow-mtl-data`

### arrow-mtl-data

```groovy
dependencies {
    compile "io.arrow-kt:arrow-mtl-data:$arrow_version"
}
```

Datatypes: [`Cokleisli`]({{ '/docs/datatypes/cokleisli/' | relative_url }}), [`Coreader`]({{ '/docs/datatypes/coreader/' | relative_url }}),  , [`StateT`]({{ '/docs/arrow/data/statet/' | relative_url }}), [`WriterT`]({{ '/docs/arrow/data/writert/' | relative_url }}), [`EitherT`]({{ '/docs/arrow/data/eithert/' | relative_url }}), [`Kleisli`]({{ '/docs/arrow/data/kleisli/' | relative_url }}), , [`OptionT`]({{ '/docs/arrow/data/optiont/' | relative_url }}), [`Reader`]({{ '/docs/arrow/data/reader/' | relative_url }}),  [`State`]({{ '/docs/arrow/data/state/' | relative_url }}), 

Typeclasses:  [`MonadReader`]({{ '/docs/arrow/mtl/typeclasses/monadreader/' | relative_url }}), [`MonadWriter`]({{ '/docs/arrow/mtl/typeclasses/monadwriter/' | relative_url }}), [`MonadState`]({{ '/docs/arrow/mtl/typeclasses/monadstate' | relative_url }})

Dependency: `arrow-core`

### arrow-optics-mtl

```groovy
dependencies {
    compile "io.arrow-kt:arrow-optics-mtl:$arrow_version"
}
```

Dependencies: `arrow-optics`, `arrow-mtl-data`

### arrow-recursion

```groovy
dependencies {
    compile "io.arrow-kt:arrow-recursion:$arrow_version"
}
```

Recursion schemes is a construct to work with recursive data structures in a way that decouples structure and data, and allows for ergonomy and performance improvements.

Dependency: `arrow-recursion-data`

### arrow-recursion-data

```groovy
dependencies {
    compile "io.arrow-kt:arrow-recursion-data:$arrow_version"
}
```

This library includes the datatypes and typeclasses in Recursion schemes. 

Datatypes: [`Fix`]({{ '/docs/recursion/fix/' | relative_url }}), [`Mu`]({{ '/docs/recursion/mu/' | relative_url }}), [`Nu`]({{ '/docs/recursion/nu/' | relative_url }})

Typeclasses: [`Corecursive`]({{ '/docs/recursion/corecursive/' | relative_url }}), [`Recursive`]({{ '/docs/recursion/recursive/' | relative_url }}), [`Birecursive`]({{ '/docs/recursion/birecursive/' | relative_url }})

Dependency: `arrow-core`

### arrow-integration-retrofit-adapter

```groovy
dependencies {
    compile "io.arrow-kt:arrow-integration-retrofit-adapter:$arrow_version"
}
```

The [adapter]({{ 'docs/integrations/retrofit/' | relative_url }}) is a library that adds integration with Retrofit, providing extensions functions and/or classes to work with Retrofit by encapsulating the responses in the chosen datatypes, through the use of typeclasses.

Dependency: `arrow-fx`

### arrow-free

```groovy
dependencies {
    compile "io.arrow-kt:arrow-free:$arrow_version"
}
```

The [Free datatype]({{ '/docs/free/free/' | relative_url }}) is a way of interpreting domain specific languages from inside your program, including a configurable runner and flexible algebras.
This allows optimization of operations like operator fusion or parallelism, while remaining on your business domain.

Dependency: `arrow-free-data`

### arrow-free-data

```groovy
dependencies {
    compile "io.arrow-kt:arrow-free-data:$arrow_version"
}
```

This library includes the datatypes in Free. 

Datatypes: [`Free`]({{ '/docs/free/free/' | relative_url }}), [`FreeApplicative`]({{ '/docs/free/freeapplicative/' | relative_url }}), [`Cofree`]({{ '/docs/free/cofree/' | relative_url }}), [`Yoneda`]({{ '/docs/free/yoneda/' | relative_url }}), [`Coyoneda`]({{ '/docs/free/coyoneda/' | relative_url }})

Dependency: `arrow-core`

### arrow-aql

```groovy
dependencies {
    compile "io.arrow-kt:arrow-query-language:$arrow_version"
}
```

This [Arrow Query Library]({{ '/docs/aql/intro/' | relative_url }}) focuses on bringing SQL-like syntax to Arrow datatypes.

Dependency: `arrow-core`

## Annotation processors

These libraries focus on meta-programming to generate code that enables other libraries and constructs.

### arrow-meta

```groovy
apply plugin: 'kotlin-kapt' //optional
apply from: rootProject.file('gradle/generated-kotlin-sources.gradle') //only for Android projects

dependencies {
    kapt "io.arrow-kt:arrow-meta:$arrow_version"
}
```

Allows boilerplate generation for [`@extension`](({{ 'docs/patterns/glossary/#instances-and-extensions-interfaces' | relative_url }})) instances and [`@higherkind`]({{ 'https://arrow-kt.io/docs/patterns/glossary/#higher-kinds' | relative_url }}) datatypes.

### arrow-generic

```groovy
apply plugin: 'kotlin-kapt' //optional
apply from: rootProject.file('gradle/generated-kotlin-sources.gradle') //only for Android projects

dependencies {
    kapt "io.arrow-kt:arrow-generic:$arrow_version"
}
```

It allows anonating data classes with [`@product`]({{ '/docs/generic/product/' | relative_url }}) to enable them to be structurally deconstructed in tuples and heterogeneous lists.
