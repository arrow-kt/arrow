---
layout: docs-core
title: Intro to datatypes
permalink: /datatypes/intro/
---

## Datatypes

A datatype is an abstraction that encapsulates one reusable coding pattern.
These solutions have a canonical implementation that is generalized for all possible uses.

A datatype is implemented by a data class, or a sealed hierarchy of data classes and objects.
These datatypes are generalized by having one or several generic parameters.

#### Example

`Option<A>` is a datatype that represents absence.
It has one generic parameter `A`, representing the type of the values that `Option` may contain.
`Option` can be specialized for any type `A` because this type does not affect its behavior.
`Option` behaves the same for `Int`, `String`, or `DomainUserClass`.

The implementation of `Option<A>` is a sealed class with two subtypes: An object `None` and a data class `Some<A>`.
`Some<A>` represents presence of the value and thus it has one field containing it, and `None` represents absence.

All operations over `Option` have to take into account absence or presence,
so there is a function `fold()` that takes a continuation function per case, `() -> B` and `(A) -> B`.
The implementation of `fold()` is a simple `when` that checks whether `this` is a `None` or a `Some<A>`, and it applies the appropriate continuation function.

All other functions provided by `Option` are implemented by using `fold()`, making for idiomatic helper functions like `getOrNull`, `getOrElse`, or `map`. These functions work for any value of `A` and `B`. This way, what `Option` does for each individual case of `String`, `Int`, or absence is up to the functions passed by the user.

Feel free to explore the [implementation of `Option`](https://github.com/arrow-kt/arrow/blob/main/arrow-libs/core/arrow-core/src/main/kotlin/arrow/core/extensions/option.kt) and [other datatypes](https://github.com/arrow-kt/arrow-core/tree/main/arrow-core-data/src/main/kotlin/arrow/core) to discover their behavior!

### Datatypes in Arrow

We will list all the datatypes available in Arrow by the module they belong to, and a short description of the coding pattern they abstract.

#### Core

Core contains the datatypes that are also used by the public API of several [typeclasses]({{ '/patterns/glossary/' | relative_url }}),
so they are always required.

- [`Option`]({{ '/apidocs/arrow-core/arrow.core/-option/' | relative_url }}) - absence of a value, or failure to construct a correct value

- [`Either`]({{ '/apidocs/arrow-core/arrow.core/-either/' | relative_url }}) - an if/else branch in execution

- [`Eval`]({{ '/apidocs/arrow-core/arrow.core/-eval' | relative_url }}) - lazy evaluation of functions with stack safety and memoization

- `TupleN` - a heterogeneous grouping of 4-9 values without creating a named class

#### Data

Data contains the bulk of the datatypes provided by Arrow. We can separate them into several categories.

##### General use

- [`NonEmptyList`]({{ '/apidocs/arrow-core/arrow.core/-non-empty-list/' | relative_url }}) - a homogeneous list that has at least 1 value

- [`Ior`]({{ '/apidocs/arrow-core/arrow.core/-ior/' | relative_url }}) - a branch in execution for three possible paths: One, two, or both

- [`Const`]({{ '/arrow/typeclasses/const/' | relative_url }}) - tags a value with a "phantom generic" that's never instantiated, and it can be used for example to represents units or state

##### Error handling

- [`Validated`]({{ '/apidocs/arrow-core/arrow.core/-validated/' | relative_url }}) - returns the result of aggregating multiple calculations that can fail, and it also aggregates the errors

- [`Either`]({{ '/apidocs/arrow-core/arrow.core/-either/' | relative_url }}) - an if/else branch in execution

#### Effects

All effects are different implementations of the same abstraction: Lazy execution of code that can move to other threads and cause exceptions.
They are more general than the other datatypes as they combine the abstractions of several of them.

For an overview on Arrow's Effect system see [Arrow Fx Coroutines](https://arrow-kt.io/docs/fx/) for support of KotlinX Coroutines primitives and Flow based streams.


##### Codata

- [`Store`]({{ '/apidocs/arrow-ui-data/arrow.ui/-store/' | relative_url }}) - a datatype that holds an initial state and a function for extracting a representation of it.

- [`Moore`]({{ '/apidocs/arrow-ui-data/arrow.ui/-moore/' | relative_url }}) - a datatype that holds an initial state and can move to new states only when an event of a specific type is dispatched.

- [`Sum`]({{ '/apidocs/arrow-ui-data/arrow.ui/-sum/' | relative_url }}) - a datatype that holds two comonads and a flag for indicating which one is active. Both sides evolve at the same time.

- [`Day`]({{ '/apidocs/arrow-ui-data/arrow.ui/-day/' | relative_url }}) - a datatype that holds two comonads which evolve independently.
