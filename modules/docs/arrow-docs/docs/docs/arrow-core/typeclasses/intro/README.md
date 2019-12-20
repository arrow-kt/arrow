---
layout: docs-core
title: Intro to datatypes
permalink: /docs/typeclasses/intro/
video: 3y9KI7XWXSY
---

## Typeclasses




Typeclasses are interfaces that define a set of extension functions associated to one type. You may see them referred to as "extension interfaces."

The other purpose of these interfaces, like with any other unit of abstraction,
is to have a single shared definition of a common API and behavior shared across many types in different libraries and codebases.

What differentiates FP from OOP is that these interfaces are meant to be implemented *outside* of their types, instead of *by* the types.
Now, the association is done using generic parametrization rather than subclassing by implementing the interface. This has multiple benefits:

* Typeclasses can be implemented for any class, even those not in the current project.
* You can treat typeclass implementations as stateless parameters because they're just a collection of functions.
* You can make the extensions provided by a typeclass for the type they're associated with by using functions like `run` and `with`.

You can read all about how Arrow implements typeclasses in the [glossary]({{ '/docs/patterns/glossary/' | relative_url }}).
If you'd like to use typeclasses effectively in your client code, you can head to the docs entry about [dependency injection]({{ '/docs/patterns/dependency_injection' | relative_url }}).

#### Example

Let's define a typeclass for the behavior of equality between two objects, and we'll call it `Eq`:

```kotlin
interface Eq<T> {
   fun T.eqv(b: T): Boolean

   fun T.neqv(b: T): Boolean =
    !eqv(b)
}
```

For this short example, we will make the scope of the typeclass `Eq` implemented for the type `String` available by using `run`.
This will make all the `Eq` extension functions, such as `eqv` and `neqv`, available inside the `run` block.

```kotlin:ank
import arrow.core.extensions.*

val stringEq = String.eq()

stringEq
```

```kotlin:ank
stringEq.run {
  "1".eqv("2")
    && "2".neqv("1")
}
```

And we can even use it as parametrization in a function call.

```kotlin

fun <F> List<F>.filter(other: F, EQ: Eq<F>) =
  this.filter { EQ.run { it.eqv(other) } }

listOf("1", "2", "3").filter("2", String.eq())
// [2]

listOf(1, 2, 3).filter(3, Eq { one, other -> one < other })
// [1, 2]
```

#### Structure

This section uses concepts explained in the [glossary]({{ '/docs/patterns/glossary/#type-constructors' | relative_url }}) like `Kind`.
Make sure to familiarize yourself with these before jumping into the next section.

A few typeclasses can be defined for values, like `Eq` above, and the rest are defined for type constructors defined by `Kind<F, A>` using a `For-` marker.
All methods inside a typeclass will have one of two shapes:

* Constructor: Create a new `Kind<F, A>` from a value, a function, an error, etc. Some examples are `just`, `raise`, `async`, `defer`, or `binding`.

* Extensions: Add new functionality to a value `A` or a container `Kind<F, A>`, provided by an extension function; for example, `map`, `eqv`, `show`, `traverse`, `sequence`, or `combineAll`.

You can use typeclasses as a DSL to access new extension functions for an existing type,
or treat them as an abstraction placeholder for any one type that can implement the typeclass.
The extension functions are scoped within the typeclass so they do not pollute the global namespace!

To assure that a typeclass has been correctly implemented for a type, Arrow provides a test suite called the "laws" per typeclass.
These test suites are available in the module `arrow-tests`.

### Typeclasses provided by Arrow

We will list all the typeclasses provided in Arrow, grouped by the module they belong to, and a short description of the behavior they abstract.

#### Typeclasses

The package typeclasses contains all the typeclass definitions that are general enough to not be part of a specialized package.
We will list them by their hierarchy.

##### General

- [`Inject`]({{ '/docs/apidocs/arrow-core-data/arrow.typeclasses/-inject/' | relative_url }}) - transformation between datatypes

- [`Alternative`]({{ '/docs/arrow/typeclasses/alternative/' | relative_url }}) - has a structure that contains either of two values

- [`Divide`]({{ '/docs/arrow/typeclasses/divide/' | relative_url }}) - models divide from the divide and conquer pattern

- [`Divisible`]({{ '/docs/arrow/typeclasses/divisible/' | relative_url }}) - extends `Divide` with conquer

- [`Decidable`]({{ '/docs/arrow/typeclasses/decidable/' | relative_url }}) - contravariant version of `Alternative`

##### Show

- [`Show`]({{ '/docs/arrow/typeclasses/show/' | relative_url }}) - literal representation of an object

##### Eq

- [`Eq`]({{ '/docs/arrow/typeclasses/eq/' | relative_url }}) - structural equality between two objects

- [`Order`]({{ '/docs/arrow/typeclasses/order/' | relative_url }}) -  determine whether one object precedes another

- [`Hash`]({{ '/docs/arrow/typeclasses/hash' | relative_url }}) - compute hash of an object

##### Semigroup

- [`Semigroup`]({{ '/docs/arrow/typeclasses/semigroup/' | relative_url }}) - can combine two objects together

- [`SemigroupK`]({{ '/docs/arrow/typeclasses/semigroupk/' | relative_url }}) - can combine two datatypes together

- [`Monoid`]({{ '/docs/arrow/typeclasses/monoid/' | relative_url }}) - combinable objects have an empty value

- [`MonoidK`]({{ '/docs/arrow/typeclasses/monoidk/' | relative_url }}) - combinable datatypes have an empty value

##### Semigroupal

- [`Semigroupal`]({{ '/docs/apidocs/arrow-core-data/arrow.typeclasses/-semigroupal/' | relative_url }}) - abstraction over the cartesian product

- [`Monoidal`]({{ '/docs/apidocs/arrow-core-data/arrow.typeclasses/-monoidal/' | relative_url }}) - adds an identity element to a semigroupal

##### Semiring

- [`Semiring`]({{ '/docs/apidocs/arrow-core-data/arrow.typeclasses/-semiring/' | relative_url }}) - can combine or multiplicatively combine two objects together

##### Functor

- [`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }}) - its contents can be mapped

- [`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }}) - independent execution

- [`ApplicativeError`]({{ '/docs/arrow/typeclasses/applicativeerror/' | relative_url }}) - recover from errors in independent execution

- [`Monad`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }}) - sequential execution

- [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror/' | relative_url }}) - recover from errors in sequential execution

- [`Comonad`]({{ '/docs/arrow/typeclasses/comonad/' | relative_url }}) - can extract values from it

- [`Bimonad`]({{ '/docs/arrow/typeclasses/bimonad/' | relative_url }}) - both monad and comonad

- [`Bifunctor`]({{ '/docs/arrow/typeclasses/bifunctor' | relative_url }}) - same as functor but for two values in the container

- [`Profunctor`]({{ '/docs/arrow/typeclasses/profunctor' | relative_url }}) - function composition inside a context

##### Foldable

- [`Foldable`]({{ '/docs/arrow/typeclasses/foldable/' | relative_url }}) - has a structure from which a value can be computed from visiting each element

- [`Bifoldable`]({{ '/docs/arrow/typeclasses/bifoldable/' | relative_url }}) - same as foldable, but for structures with more than one possible type, like either

- [`Bitraverse`]({{ '/docs/apidocs/arrow-core-data/arrow.typeclasses/-bitraverse/' | relative_url }}) - For those structures that are `Bifoldable` adds the functionality of `Traverse` in each side of the datatype

- [`Reducible`]({{ '/docs/arrow/typeclasses/reducible/' | relative_url }}) - structures that can be combined to a summary value

- [`Traverse`]({{ '/docs/arrow/typeclasses/traverse/' | relative_url }}) - has a structure for which each element can be visited and get applied an effect

#### Effects

Effects provides a hierarchy of typeclasses for lazy and asynchronous execution.

- [`MonadDefer`]({{ '/docs/effects/monaddefer/' | relative_url }}) - can evaluate functions lazily

- [`Async`]({{ '/docs/effects/async/' | relative_url }}) - can be created using an asynchronous callback function

- [`Effect`]({{ '/docs/effects/effect/' | relative_url }}) - can extract a value from an asynchronous function

#### MTL

The Monad Template Library module gives more specialized version of existing typeclasses

- [`FunctorFilter`]({{ '/docs/arrow/typeclasses/functorfilter/' | relative_url }}) - can map values that pass a predicate

- [`MonadFilter`]({{ '/docs/arrow/typeclasses/monadfilter/' | relative_url }}) - can sequentially execute values that pass a predicate

- [`TraverseFilter`]({{ '/docs/arrow/typeclasses/traversefilter/' | relative_url }}) - can traverse values that pass a predicate

- [`MonadCombine`]({{ '/docs/arrow/typeclasses/monadcombine/' | relative_url }}) - has a structure that can be combined and split for several datatypes

- [`MonadReader`]({{ '/docs/arrow/mtl/typeclasses/monadwriter/' | relative_url }}) - can implement the capabilities of the datatype [`Reader`]({{ '/docs/arrow/mtl/reader/' | relative_url }})

- [`MonadWriter`]({{ '/docs/arrow/mtl/typeclasses/monadwriter/' | relative_url }}) - can implement the capabilities of the datatype [`Writer`]({{ '/docs/arrow/mtl/writert/' | relative_url }})

- [`MonadState`]({{ '/docs/arrow/mtl/typeclasses/monadstate' | relative_url }}) - can implement the capabilities of the datatype [`State`]({{ '/docs/datatypes/state/' | relative_url }})

#### Optics

- [`At`]({{ '/docs/optics/at/' | relative_url }}) - provides a [`Lens`]({{ '/docs/optics/lens/' | relative_url }}) for a structure with an indexable focus

- [`FilterIndex`]({{ '/docs/optics/filterindex/' | relative_url }}) - provides a [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }}) for a structure with indexable foci that satisfy a predicate

- [`Index`]({{ '/docs/optics/index/' | relative_url }}) - provides an [`Optional`]({{ '/docs/optics/optional/' | relative_url }}) for a structure with an indexable optional focus

- [`Each`]({{ '/docs/optics/each/' | relative_url }}) - provides a [`Traversal`]({{ '/docs/optics/traversal/' | relative_url }})

#### Recursion

- [`Corecursive`]({{ '/docs/recursion/corecursive/' | relative_url }}) - traverses a structure forwards from the starting case

- [`Recursive`]({{ '/docs/recursion/recursive/' | relative_url }}) - traverses a structure backwards from the base case

- [`Birecursive`]({{ '/docs/recursion/birecursive/' | relative_url }}) - it is both recursive and corecursive
