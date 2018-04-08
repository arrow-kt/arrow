---
layout: docs
title: Intro to datatypes
permalink: /docs/typeclasses/intro/
video: 3y9KI7XWXSY
---

## Typeclasses

Typeclasses define a set of functions associated to one generic type.
All methods inside a typeclass will have one of two shapes:

* Constructor: create a new `Kind<F, A>` from a value, a function, an error... Some examples are `just`, `raise`, `async`, `defer`, or `binding`.

* Extensions: add new functionality to a value `A` or a container `Kind<F, A>`, provided by an extension function. For example, `map`, `eqv`, `show`, `traverse`, `sequence`, or `combineAll`.

You can use typeclasses as a DSL to access new free functionality for an existing type,
or treat them as an abstraction placeholder for any one type that can implement the typeclass.
The extension functions are scoped within the typeclass so they do not pollute the global namespace!

What differentiates typeclasses from regular OOP inheritance is that typeclasses are meant to be implemented *outside* of their types.
The association is done using generic parametrization rather than subclassing by implementing the interface. This has multiple benefits:

* You can treat typeclass implementations as stateless parameters because they're just a collection of functions
* Typeclasses can be implemented for any class, even those not in the current project
* You can make available any one implementation of a typeclasses at any scope for the generic type they're associated with by using functions like `run` and `with`

To assure that a typeclass has been correctly implemented for a type, Arrow provides a test suite called the "laws" per typeclass.
These test suites are available in the module `arrow-tests`.

#### Example

You can read all about how Arrow implements typeclasses in the [glossary]({{ '/docs/patterns/glossary/' | relative_url }}).

For this short example we will make available the scope of the typeclass `Eq` implemented for the type `String`, by using `run`.
This will make all the `Eq` extension functions, such as `eqv` and `neqv`, available inside the `run` block.

```kotlin:ank
import arrow.instances.*

val stringEq = String.eq()

stringEq
```

```kotlin:ank
stringEq.run {
  "1".eqv("2")
    && "2".neqv("1")
}
```

### Typeclasses provided by Arrow

We will list all the typeclasses provided in Arrow grouped by the module they belong to, and a short description of the behavior they abstract.

#### Typeclasses

The package typeclasses contains all the typeclass definitions that are general enough not to be part of a specialized package.
We will list them by their hierarchy.

##### General

- [`Inject`]({{ '/docs/typeclasses/inject/' | relative_url }}) - transformation between datatypes

- [`Alternative`]({{ '/docs/typeclasses/alternative/' | relative_url }}) - has an structure that contains either of two values

##### Show

- [`Show`]({{ '/docs/typeclasses/show/' | relative_url }}) - literal representation of an object

##### Eq

- [`Eq`]({{ '/docs/typeclasses/eq/' | relative_url }}) - structural equality between two objects

- [`Order`]({{ '/docs/typeclasses/order/' | relative_url }}) -  determine whether one object precedes another

##### Semigroup

- [`Semigroup`]({{ '/docs/typeclasses/semigroup/' | relative_url }}) - can combine two objects together

- [`SemigroupK`]({{ '/docs/typeclasses/semigroupk/' | relative_url }}) - can combine two datatypes together

- [`Monoid`]({{ '/docs/typeclasses/monoid/' | relative_url }}) - combinable objects have an empty value

- [`MonoidK`]({{ '/docs/typeclasses/monoidk/' | relative_url }}) - combinable datatypes have an empty value

##### Functor

- [`Functor`]({{ '/docs/typeclasses/functor/' | relative_url }}) - its contents can be mapped

- [`Applicative`]({{ '/docs/typeclasses/applicative/' | relative_url }}) - independent execution

- [`ApplicativeError`]({{ '/docs/typeclasses/applicativeerror/' | relative_url }}) - recover from errors in independent execution

- [`Monad`]({{ '/docs/typeclasses/monad/' | relative_url }}) - sequential execution

- [`MonadError`]({{ '/docs/typeclasses/monaderror/' | relative_url }}) - recover from errors in sequential execution

- [`Comonad`]({{ '/docs/typeclasses/comonad/' | relative_url }}) - can extract values from it

- [`Bimonad`]({{ '/docs/typeclasses/bimonad/' | relative_url }}) - both monad and comonad

##### Foldable

- [`Foldable`]({{ '/docs/typeclasses/foldable/' | relative_url }}) - has a structure from which a value can be computed from visiting each element

- [`Bifoldable`]({{ '/docs/typeclasses/bifoldable/' | relative_url }}) - same as foldable, but for structures with more than one possible type, like either

- [`Reducible`]({{ '/docs/typeclasses/reducible/' | relative_url }}) - structures that can be combined to a summary value

- [`Traverse`]({{ '/docs/typeclasses/traverse/' | relative_url }}) - has a structure for which each element can be visited and get applied an effect

#### Effects

Effects provides a hierarchy of typeclasses for lazy and asynchronous execution.

- [`MonadDefer`]({{ '/docs/effects/monaddefer/' | relative_url }}) - can evaluate functions lazily

- [`Async`]({{ '/docs/effects/async/' | relative_url }}) - can be created using an asynchronous callback function

- [`Effect`]({{ '/docs/effects/effect/' | relative_url }}) - can extract a value from an asynchronous function

#### MTL

The Monad Template Library module gives more specialized version of existing typeclasses

- [`FunctorFilter`]({{ '/docs/typeclasses/functorfilter/' | relative_url }}) - can map values that pass a predicate

- [`MonadFilter`]({{ '/docs/typeclasses/monadfilter/' | relative_url }}) - can sequentially execute values that pass a predicate

- [`TraverseFilter`]({{ '/docs/typeclasses/traversefilter/' | relative_url }}) - can traverse values that pass a predicate

- [`MonadCombine`]({{ '/docs/typeclasses/monadcombine/' | relative_url }}) - has a structure that can be combined and split for several datatypes

- [`MonadReader`]({{ '/docs/typeclasses/monadwriter/' | relative_url }}) - can implement the capabilities of the datatype [`Reader`]({{ '/docs/datatypes/reader/' | relative_url }})

- [`MonadWriter`]({{ '/docs/typeclasses/monadwriter/' | relative_url }}) - can implement the capabilities of the datatype [`Writer`]({{ '/docs/datatypes/writert/' | relative_url }})

- [`MonadState`]({{ '/docs/typeclasses/monadstate/' | relative_url }}) - can implement the capabilities of the datatype [`State`]({{ '/docs/datatypes/state/' | relative_url }})

#### Optics

- [`At`]({{ '/docs/optics/at/' | relative_url }}) - provides a [`Lens`]({{ '/docs/optics/lens/' | relative_url }}) for a structure with an indexable focus

- [`FilterIndex`]({{ '/docs/optics/filterindex/' | relative_url }}) - provides a [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }}) for a structure with indexable foci that satisfy a predicate

- [`Index`]({{ '/docs/optics/index/' | relative_url }}) - provides an [`Optional`]({{ '/docs/optics/optional/' | relative_url }}) for a structure with an indexable optional focus

- [`Each`]({{ '/docs/optics/each/' | relative_url }}) - provides a [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }})

#### Recursion

- [`Corecursive`]({{ '/docs/recursion/corecursive/' | relative_url }}) - traverses a structure forwards from the starting case

- [`Recursive`]({{ '/docs/recursion/recursive/' | relative_url }}) - traverses a structure backwards from the base case

- [`Birecursive`]({{ '/docs/recursion/birecursive/' | relative_url }}) - it is both recursive and corecursive
