---
layout: docs-core
title: Intro to datatypes
permalink: /typeclasses/intro/
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

You can read all about how Arrow implements typeclasses in the [glossary]({{ '/patterns/glossary/' | relative_url }}).
If you'd like to use typeclasses effectively in your client code, you can head to the docs entry about [dependency injection]({{ '/patterns/dependency_injection' | relative_url }}).

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

This section uses concepts explained in the [glossary]({{ '/patterns/glossary/#type-constructors' | relative_url }}) like `Kind`.
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

- [`Inject`]({{ '/apidocs/arrow-core-data/arrow.typeclasses/-inject/' | relative_url }}) - transformation between datatypes

- [`Alternative`]({{ '/arrow/typeclasses/alternative/' | relative_url }}) - has a structure that contains either of two values

- [`Divide`]({{ '/arrow/typeclasses/divide/' | relative_url }}) - models divide from the divide and conquer pattern

- [`Divisible`]({{ '/arrow/typeclasses/divisible/' | relative_url }}) - extends `Divide` with conquer

- [`Decidable`]({{ '/arrow/typeclasses/decidable/' | relative_url }}) - contravariant version of `Alternative`

##### Show

- [`Show`]({{ '/arrow/typeclasses/show/' | relative_url }}) - literal representation of an object

##### Eq

- [`Eq`]({{ '/arrow/typeclasses/eq/' | relative_url }}) - structural equality between two objects

- [`Order`]({{ '/arrow/typeclasses/order/' | relative_url }}) -  determine whether one object precedes another

- [`Hash`]({{ '/arrow/typeclasses/hash' | relative_url }}) - compute hash of an object

##### Semigroup

- [`Semigroup`]({{ '/arrow/typeclasses/semigroup/' | relative_url }}) - can combine two objects together

- [`SemigroupK`]({{ '/arrow/typeclasses/semigroupk/' | relative_url }}) - can combine two datatypes together

- [`Monoid`]({{ '/arrow/typeclasses/monoid/' | relative_url }}) - combinable objects have an empty value

- [`MonoidK`]({{ '/arrow/typeclasses/monoidk/' | relative_url }}) - combinable datatypes have an empty value

##### Semigroupal

- [`Semigroupal`]({{ '/apidocs/arrow-core-data/arrow.typeclasses/-semigroupal/' | relative_url }}) - abstraction over the cartesian product

- [`Monoidal`]({{ '/apidocs/arrow-core-data/arrow.typeclasses/-monoidal/' | relative_url }}) - adds an identity element to a semigroupal

##### Semiring

- [`Semiring`]({{ '/apidocs/arrow-core-data/arrow.typeclasses/-semiring/' | relative_url }}) - can combine or multiplicatively combine two objects together

##### Functor

- [`Functor`]({{ '/arrow/typeclasses/functor/' | relative_url }}) - its contents can be mapped

- [`Applicative`]({{ '/arrow/typeclasses/applicative/' | relative_url }}) - independent execution

- [`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror/' | relative_url }}) - recover from errors in independent execution

- [`Monad`]({{ '/arrow/typeclasses/monad/' | relative_url }}) - sequential execution

- [`MonadError`]({{ '/arrow/typeclasses/monaderror/' | relative_url }}) - recover from errors in sequential execution

- [`Comonad`]({{ '/arrow/typeclasses/comonad/' | relative_url }}) - can extract values from it

- [`Bimonad`]({{ '/arrow/typeclasses/bimonad/' | relative_url }}) - both monad and comonad

- [`Bifunctor`]({{ '/arrow/typeclasses/bifunctor' | relative_url }}) - same as functor but for two values in the container

- [`Profunctor`]({{ '/arrow/typeclasses/profunctor' | relative_url }}) - function composition inside a context

##### Foldable

- [`Foldable`]({{ '/arrow/typeclasses/foldable/' | relative_url }}) - has a structure from which a value can be computed from visiting each element

- [`Bifoldable`]({{ '/arrow/typeclasses/bifoldable/' | relative_url }}) - same as foldable, but for structures with more than one possible type, like either

- [`Bitraverse`]({{ '/apidocs/arrow-core-data/arrow.typeclasses/-bitraverse/' | relative_url }}) - For those structures that are `Bifoldable` adds the functionality of `Traverse` in each side of the datatype

- [`Reducible`]({{ '/arrow/typeclasses/reducible/' | relative_url }}) - structures that can be combined to a summary value

- [`Traverse`]({{ '/apidocs/arrow-core-data/arrow.typeclasses/-traverse/' | relative_url }}) - has a structure for which each element can be visited and get applied an effect

#### Effects

Effects provides a hierarchy of typeclasses for lazy and asynchronous execution.

- [`MonadDefer`]({{ '/effects/monaddefer/' | relative_url }}) - can evaluate functions lazily

- [`Async`]({{ '/effects/async/' | relative_url }}) - can be created using an asynchronous callback function

- [`Effect`]({{ '/effects/effect/' | relative_url }}) - can extract a value from an asynchronous function

#### MTL

The Monad Template Library module gives more specialized version of existing typeclasses

- [`FunctorFilter`]({{ '/arrow/typeclasses/functorfilter/' | relative_url }}) - can map values that pass a predicate

- [`MonadFilter`]({{ '/arrow/typeclasses/monadfilter/' | relative_url }}) - can sequentially execute values that pass a predicate

- [`TraverseFilter`]({{ '/arrow/typeclasses/traversefilter/' | relative_url }}) - can traverse values that pass a predicate

- [`MonadCombine`]({{ '/arrow/typeclasses/monadcombine/' | relative_url }}) - has a structure that can be combined and split for several datatypes

- [`MonadReader`]({{ '/arrow/mtl/typeclasses/monadwriter/' | relative_url }}) - can implement the capabilities of the datatype [`Reader`]({{ '/arrow/mtl/reader/' | relative_url }})

- [`MonadWriter`]({{ '/arrow/mtl/typeclasses/monadwriter/' | relative_url }}) - can implement the capabilities of the datatype [`Writer`]({{ '/arrow/mtl/writert/' | relative_url }})

- [`MonadState`]({{ '/arrow/mtl/typeclasses/monadstate' | relative_url }}) - can implement the capabilities of the datatype [`State`]({{ '/arrow/mtl/state/' | relative_url }})

#### Optics

- [`At`]({{ '/optics/at/' | relative_url }}) - provides a [`Lens`]({{ '/optics/lens/' | relative_url }}) for a structure with an indexable focus

- [`FilterIndex`]({{ '/optics/filterindex/' | relative_url }}) - provides a [`Traversal`]({{ '/optics/traversal/' | relative_url }}) for a structure with indexable foci that satisfy a predicate

- [`Index`]({{ '/optics/index/' | relative_url }}) - provides an [`Optional`]({{ '/optics/optional/' | relative_url }}) for a structure with an indexable optional focus

- [`Each`]({{ '/optics/each/' | relative_url }}) - provides a [`Traversal`]({{ '/optics/traversal/' | relative_url }})

#### Recursion

- [`Corecursive`]({{ '/recursion/corecursive/' | relative_url }}) - traverses a structure forwards from the starting case

- [`Recursive`]({{ '/recursion/recursive/' | relative_url }}) - traverses a structure backwards from the base case

- [`Birecursive`]({{ '/recursion/birecursive/' | relative_url }}) - it is both recursive and corecursive
