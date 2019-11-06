---
layout: docs-core
title: Intro to datatypes
permalink: /docs/datatypes/intro/
---

## Datatypes

{:.beginner}
beginner

A datatype is a an abstraction that encapsulates one reusable coding pattern.
These solutions have a canonical implementation that is generalized for all possible uses.

A datatype is implemented by a data class, or a sealed hierarchy of data classes and objects.
These datatypes are generalized by having one or several generic parameters,
and, to become a [type constructor]({{ '/docs/patterns/glossary/' | relative_url }}), they implement the interface [`Kind`]({{ '/docs/patterns/glossary/' | relative_url }}) for these generic parameters.
Datatypes work over themselves, never directly over the values defined by its generic parameters.

#### Example

`Option<A>` is a datatype that represents absence.
It has one generic parameter `A`, representing the type of the values that `Option` may contain.
`Option` can be specialized for any type `A` because this type does not affect its behavior.
`Option` behaves the same for `Int`, `String`, or `DomainUserClass`.
To indicate that `Option` is a [type constructor]({{ '/docs/patterns/glossary/' | relative_url }}) for all values of `A`, it implements `OptionOf<A>`, which is a typealias of `Kind<ForOption, A>`.

The implementation of `Option<A>` is a sealed class with two subtypes: An object `None` and a data class `Some<A>`.
`Some<A>` represents presence of the value and thus it has one field containing it, and `None` represents absence.

All operations over `Option` have to take into account absence or presence,
so there is a function `fold()` that takes a continuation function per case, `() -> B` and `(A) -> B`.
The implementation of `fold()` is a simple `when` that checks whether `this` is a `None` or a `Some<A>`, and it applies the appropriate continuation function.

All other functions provided by `Option` are implemented by using `fold()`, making for idiomatic helper functions like `getOrNull`, `getOrElse`, or `map`. These functions work for any value of `A` and `B`. This way, what `Option` does for each individual case of `String`, `Int`, or absence is up to the functions passed by the user.

Feel free to explore the [implementation of `Option`](https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-core/src/main/kotlin/arrow/core/Option.kt) and [other datatypes](https://github.com/arrow-kt/arrow/tree/master/modules/core/arrow-core-data/src/main/kotlin/arrow/core) to discover their behavior!

### Datatypes in Arrow

We will list all the datatypes available in Arrow by the module they belong to, and a short description of the coding pattern they abstract.

#### Core

Core contains the datatypes that are also used by the public API of several [typeclasses]({{ '/docs/patterns/glossary/' | relative_url }}),
so they are always required.

- [`Id`]({{ '/docs/arrow/core/id/' | relative_url }}) - a simple wrapper without any behavior, used mostly for testing

- [`Option`]({{ '/docs/arrow/core/option/' | relative_url }}) - absence of a value, or failure to construct a correct value

- [`Either`]({{ '/docs/arrow/core/either/' | relative_url }}) - an if/else branch in execution

- [`Eval`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-eval' | relative_url }}) - lazy evaluation of functions with stack safety and memoization

- `TupleN` - a heterogeneous grouping of 2-9 values without creating a named class

#### Data

Data contains the bulk of the datatypes provided by Arrow. We can separate them into several categories.

##### General use

- [`NonEmptyList`]({{ '/docs/arrow/data/nonemptylist/' | relative_url }}) - a homogeneous list that has at least 1 value

- [`Ior`]({{ '/docs/arrow/data/ior/' | relative_url }}) - a branch in execution for three possible paths: One, two, or both

- [`Const`]({{ '/docs/arrow/typeclasses/const/' | relative_url }}) - tags a value with a "phantom generic" that's never instantiated, and it can be used for example to represents units or state

##### Error handling

- [`Try`]({{ '/docs/arrow/core/try/' | relative_url }}) - returns the result of executing a block of code that can fail and throw exceptions

- [`Validated`]({{ '/docs/arrow/data/validated/' | relative_url }}) - returns the result of aggregating multiple calculations that can fail, and it also aggregates the errors

##### Reader/Writer/State

- [`Kleisli`]({{ '/docs/arrow/data/kleisli/' | relative_url }}) - similar to Dependency Injection and Inversion of Control, it represents a calculation with a dependency on an external context

- [`Reader`]({{ '/docs/arrow/data/reader/' | relative_url }}) - same as kleisli but operating over the `Id` datatype

- [`Writer`]({{ '/docs/arrow/data/writert/' | relative_url }}) - represents calculations that carry over one extra aggregated value, generally a logger or reporter

- [`State`]({{ '/docs/arrow/data/state/' | relative_url }}) - represents a stateful calculation with a carried value that can be read from or modified, like a combination of reader and writer

##### Wrappers

These types wrap over some of Kotlin's collections and functions to give them capabilities related to [typeclasses]({{ '/docs/typeclasses/intro/' | relative_url }}) provided by Arrow.

- [`ListK`]({{ '/docs/arrow/data/listk/' | relative_url }})

- [`SequenceK`]({{ '/docs/arrow/data/sequencek/' | relative_url }})

- [`SetK`]({{ '/docs/arrow/data/setk/' | relative_url }})

- [`MapK`]({{ '/docs/arrow/data/mapk/' | relative_url }})

- [`SortedMapK`]({{ '/docs/arrow/data/sortedmapk/' | relative_url }})

- [`Function0`]({{ '/docs/arrow/core/function0/' | relative_url }})

- [`Function1`]({{ '/docs/arrow/core/function1/' | relative_url }})

##### Transformers

A transformer is a special kind of datatype that allows combining two datatypes to give one of them the abstractions of another

- [`OptionT`]({{ '/docs/arrow/data/optiont/' | relative_url }}) - gives the datatype wrapped the properties of `Option`

- [`EitherT`]({{ '/docs/arrow/data/eithert/' | relative_url }}) - gives the datatype wrapped the properties of `Either`

- [`ReaderT`]({{ '/docs/arrow/data/kleisli/' | relative_url }}) - gives the datatype wrapped the properties of `Reader`

- [`WriterT`]({{ '/docs/arrow/data/writert/' | relative_url }}) - gives the datatype wrapped the properties of `Writer`

- [`StateT`]({{ '/docs/arrow/data/statet/' | relative_url }}) - gives the datatype wrapped the properties of `State`


##### Codata

TODO

- [`Cokleisli`]

- [`Coreader`]

- [`Store`]({{ '/docs/arrow/data/store/' | relative_url }}) - a datatype that holds an initial state and a function for extracting a representation of it.

- [`Moore`]({{ '/docs/arrow/data/moore/' | relative_url }}) - a datatype that holds an initial state and can move to new states only when an event of a specific type is dispatched.

- [`Sum`]({{ '/docs/arrow/data/moore/' | relative_url }}) - a datatype that holds two comonads and a flag for indicating which one is active. Both sides evolve at the same time.

- [`Day`]({{ '/docs/arrow/data/day/' | relative_url }}) - a datatype that holds two comonads which evolve independently.

#### Effects

All effects are different implementations of the same abstraction: Lazy execution of code that can move to other threads and cause exceptions.
They are more general than the other datatypes as they combine the abstractions of several of them.

- [`IO`]({{ '/docs/effects/io/' | relative_url }})

- [`Deferred`]({{ '/docs/integrations/kotlinxcoroutines/' | relative_url }})

- [`Observable`]({{ '/docs/integrations/rx2/' | relative_url }})

- [`Promise`]({{ '/docs/effects/promise/' | relative_url }})

- [`Ref`]({{ '/docs/effects/ref/' | relative_url }})

#### Free

[Free]({{ '/docs/patterns/free_algebras/' | relative_url }}) is a general abstraction to represent [Domain Specific Languages]({{ '/docs/patterns/free_algebras/' | relative_url }}) that can be interpreted using Effects.

- [`Free`]({{ '/docs/free/free/' | relative_url }})

- [`FreeApplicative`]({{ '/docs/free/freeapplicative/' | relative_url }})

- [`Cofree`]({{ '/docs/free/cofree/' | relative_url }})

- [`Yoneda`]({{ '/docs/free/yoneda/' | relative_url }})

- [`Coyoneda`]({{ '/docs/free/coyoneda/' | relative_url }})

#### Recursion schemes

Recursion schemes are an abstraction for structured recursion that ensure runtime safety and provide powerful abstractions for recursive datatypes.

- [`Fix`]({{ 'docs/recursion/fix' | relative_url }}) - Models birecursion

- [`Mu`]({{ 'docs/recursion/mu' | relative_url }}) - Models recursion

- [`Nu`]({{ 'docs/recursion/nu' | relative_url }}) - Models corecursion
