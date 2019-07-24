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

#### Table of Content
* [arrow-core](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-core)
* [arrow-core-data](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-core-data)
* [arrow-optics](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-optics)
* [arrow-fx](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-fx)
* [arrow-syntax](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-syntax)
* [arrow-fx-rx2 & arrow-fx-reactor](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-fx-rx2&arrow-fx-reactor)
* [arrow-mtl](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-mtl)
* [arrow-mtl-data](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-mtl-data)
* [arrow-optics-mtl](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-optics-mtl)
* [arrow-recursion](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-recrusion)
* [arrow-recursion-data](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-recrusion-data)
* [arrow-integration-retrofit-adapter](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-integration-retrofit-adapter)
* [arrow-free](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-free)
* [arrow-free-data](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-free-data)
* [arrow-aql](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-aql)
* [arrow-meta](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-meta)
* [arrow-generic](modules/docs/arrow-docs/docs/docs/quickstart/libraries/README.md#arrow-generic)

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

### arrow-optics

```groovy
dependencies {
    compile "io.arrow-kt:arrow-optics:$arrow_version"
}
```

Optics is the functional way of handling immutable data and collections in a way that's boilerplate free and efficient.

Arrow Optics offers a way of declaring how to focus deeply into immutable structure without boilerplate. It also offers an [Optics DSL]({{ '/docs/optics/dsl/' | relative_url }})  to elegantly describe complex use-cases in an elegant and simple manner without requiring to understand the underlying theory.

For all the new typeclasses it also includes the extensions available for basic types and datatypes in both arrow-core and arrow-extras.

Dependency: `arrow-core`

### arrow-fx

```groovy
dependencies {
    compile "io.arrow-kt:arrow-fx:$arrow_version"
}
```

The [fx library]({{ '/docs/effects/fx/' | relative_url }}) offers a powerful concurrency DSL with an emphasis on easy concurrency and parallelism with guarantees about concurrent and parallel resource safety. It can be used with Arrow Fx's IO or a set of typeclasses to abstract over concurrency frameworks like `RxJava`, `Reactor`, `Arrow's IO`, etc.

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

[Rx]({{ 'docs/integrations/rx2/' | relative_url }})

[Reactor]({{ 'docs/integrations/reactor/' | relative_url }})

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
