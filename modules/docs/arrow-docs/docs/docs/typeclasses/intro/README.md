---
layout: docs
title: Intro to datatypes
permalink: /docs/typeclasses/intro/
---

## Typeclasses

A typeclass is a specification for one behavior associated with a single type.
This behavior is checked by a test suite called the "laws" for that typeclass.
These test suites are available in the package arrow-tests.

What differentiates typeclasses from regular OOP inheritance is that typeclasses are meant to be implemented outside of their types.
The association is done using generic parametrization rather than the usual subclassing by implementing the interface.
This means that they can be implemented for any class, even those not in the current project,
and allows us to make available at the global scope any one implementation of a typeclasses for the single unique type they're associated with.

#### Example

You can read all about how Arrow does typeclasses in the [glossary]({{ '/docs/patterns/glossary/' | relative_url }}).

### Typeclasses in Arrow

We will list all the typeclasses available in arrow by the module they belong to, and a short description of the behavior they abstract.

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

##### Functor

- [`Functor`]({{ '/docs/typeclasses/functor/' | relative_url }}) - its contents can be mapped

- [`Applicative`]({{ '/docs/typeclasses/applicative/' | relative_url }}) - independent execution

- [`ApplicativeError`]({{ '/docs/typeclasses/applicativeerror/' | relative_url }}) - recover from errors in independent execution

- [`Monad`]({{ '/docs/typeclasses/monad/' | relative_url }}) - sequential execution

- [`MonadError`]({{ '/docs/typeclasses/monaderror/' | relative_url }}) - recover from errors in sequential execution

- [`Comonad`]({{ '/docs/typeclasses/comonad/' | relative_url }}) - can extract values from it

- [`Bimonad`]({{ '/docs/typeclasses/bimonad/' | relative_url }}) - both monad and comonad

##### Semigroup

- [`Semigroup`]({{ '/docs/typeclasses/semigroup/' | relative_url }}) - can combine two objects together

- [`SemigroupK`]({{ '/docs/typeclasses/semigroupk/' | relative_url }}) - can combine two datatypes together

- [`Monoid`]({{ '/docs/typeclasses/monoid/' | relative_url }}) - combinable objects have an empty value

- [`MonoidK`]({{ '/docs/typeclasses/monoidk/' | relative_url }}) - datatypes have an empty value

##### Foldable

- [`Foldable`]({{ '/docs/typeclasses/foldable/' | relative_url }}) - has a structure from which a value can be computed from visiting each element

- [`Bifoldable`]({{ '/docs/typeclasses/bifoldable/' | relative_url }}) - same as foldable, but for structures with more than one possible type, like either

- [`Reducible`]({{ '/docs/typeclasses/reducible/' | relative_url }}) - structures that can be combined to a summary value

- [`Traverse`]({{ '/docs/typeclasses/traverse/' | relative_url }}) - has a structure for which each element can be visited and get applied an effect

#### Effects

Effects provides a hierarchy of typeclasses for lazy and asynchronous execution.

- [`MonadSuspend`]({{ '/docs/effects/monadsuspend/' | relative_url }}) - can evaluate functions lazily

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

- [`At`]({{ '/docs/typeclasses/at/' | relative_url }}) - provides a [`Lens`]({{ '/docs/optics/lens/' | relative_url }}) for a structure with an indexable focus.

- [`FilterIndex`]({{ '/docs/typeclasses/filterindex/' | relative_url }}) - provides a [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }}) for a structure with indexable foci that satisfy a predicate.

- [`Index`]({{ '/docs/typeclasses/index/' | relative_url }}) - provides an [`Optional`]({{ '/docs/optics/optional/' | relative_url }}) for a structure with an indexable optional focus.

- [`Each`]({{ '/docs/typeclasses/each/' | relative_url }}) - provides a [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }}).

#### Recursion

- [`Corecursive`]({{ '/docs/typeclasses/corecursive/' | relative_url }}) - traverses a structure forwards from the starting case

- [`Recursive`]({{ '/docs/typeclasses/recursive/' | relative_url }}) - traverses a structure backwards from the base case

- [`Birecursive`]({{ '/docs/typeclasses/birecursive/' | relative_url }}) - it is both recursive and corecursive
