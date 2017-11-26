---
layout: docs
title: Functional Programming Glossary
permalink: /docs/patterns/glossary/
---

## Functional Programming Glossary

TODO: expand terms and usage

### Datatypes

A datatype is a class that encapsulates one reusable coding pattern.
These solutions have a canonical implementation that is generalised for all possible uses.

Some common patterns expressed as datatypes are absence handling with [`Option`]({{ '/docs/datatypes/option' | relative_url }}),
branching in code with [`Either`]({{ '/docs/datatypes/either' | relative_url }}),
catching exceptions with [`Try`]({{ '/docs/datatypes/try' | relative_url }}),
or interacting with the platform the program runs in using [`IO`]({{ '/docs/effects/io' | relative_url }}).

### Typeclasses

A typeclass is an interface representing one behavior associated with a type.
Examples of this behavior are comparison ([`Eq`]({{ '/docs/typeclasses/eq' | relative_url }})),
composability ([`Monoid`]({{ '/docs/typeclasses/monoid' | relative_url }})),
its contents can be mapped from one type to another ([`Functor`]({{ '/docs/typeclasses/functor' | relative_url }})),
or error recovery ([`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }})).

```kotlin
interface Eq<F>: Typeclass {
  fun eqv(a: F, b: F): Boolean
}
```

What differentiates typeclasses from regular interfaces is that they are meant to be implemented outside of their types.
The association is done using generic parametrization rather than the usual subclassing.
This means that they can be implemented for any class, even those not in the current project,
and allows us to make typeclass instances available at a global scope for the single unique type they're associated with.

### Instances

A single implementation of a typeclass for a specific datatype or class.
Because typeclasses require generic parameters each implementation is meant to be unique for that parameter.

```kotlin
@instance
object IntEqInstance: Eq<Int> {
  override fun eqv(a: Int, b: Int): Boolean = a == b
}
```

In KΛTEGORY all typeclass instances can be looked up in a global scope using an inlined reified method with the same name as the typeclass.
Its generic parameter will be used for the lookup, which reinforces the concept that most typeclasses should have a single implementation per type.

All the instances in the library are already registered and available in the global scope.
If you're defining your own instances and would like for them to be discoverable in the global scope 
you can add them by annotating them as `@instance`, and KΛTEGORY's [annotation processor](https://github.com/kategory/kategory#additional-setup) will register them for you.

```kotlin:ank
import kategory.*

eq<Int>()
```

### Type constructors

> NOTE: This approach to type constructors will be simplified if [KEEP-87](https://github.com/Kotlin/KEEP/pull/87) is approved. Go vote!

A type constructor is any class or interface that has at least one generic parameter. For example, 
`ListKW<A>` or `Option<A>`. They're called constructors because they're is similar to a function where the parameter is `A`.
So, after applying the parameter `Int` to the type constructor `ListKW<A>` it returns a `ListKW<Int>`.
This list isn't parametrized in any generic value so it cannot be considered a type constructor anymore.
Like functions, a type constructor with several parameters like `Either<L, R>` can be partially applied for one of them to return another type constructor,
for example `Either<Throwable, A>` or `Either<E, String>`.

Type constructors are useful when matched with typeclasses because they help us represent non-parametrized values.
As type constructors is not a first class feature in Kotlin we use an interface `HK<F, A>` to represent them.
HK stands for Higher Kind, which is the name of the language feature that allows working directly with type constructors.

#### Higher Kinds

In a Higher Kind with the shape `HK<F, A>`, if `A` is the type of the content then `F` has to be the type of the container.
A malformed container would use the whole type constructor, duplicating the type ~~HK\<Option\<A\>, A\>~~.
What KΛTEGORY does instead is define a surrogate type that's not parametrized to represent `F`.
These types are named same as the container and suffixed by HK, as in `OptionHK` or `ListKWHK`.

You can read more about Higher Kinds and type constructors in [KindedJ's README](https://github.com/KindedJ/KindedJ#rationale).

#### Using Higher Kinds with typeclasses

When coupled with typeclasses, we can now define mapability using ([`Functor`]({{ '/docs/typeclasses/functor' | relative_url }})) for any `ListKW`.

```kotlin
interface Functor<F>: Typeclass {
  fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B>
}
```

```kotlin
object ListKWFunctorInstance : Functor<ListKWHK> {
  override fun <A, B> map(fa: HK<ListKWHK, A>, f: (A) -> B): ListKW<B> {
    val list: ListKW<A> = fa.ev()
    return list.map(f)
  }
}
```

You can see a function `ev()` used to access the `map()` function that already exists in `ListKW`.
This is because we need to safely downcast from `HK<ListKWHK, A>` to `ListKW`, and `ev()` is a global function defined to do so.

The function `ev()` is already defined for all datatypes in KΛTEGORY. If you're creating your own datatype that's also a type constructor and would like to create all these helper types and functions,
you can do so simply by annotating it as `@higerkind`, and using KΛTEGORY's [annotation processor](https://github.com/kategory/kategory#additional-setup) will create them for you.

#### Using Higher Kinds and typeclasses with functions

Higher kinds can also be used to represent functions that are parametrized on type constructors.
As long as you have a typeclass that can provide you with the behavior required to use such datatypes, you're good to go!

Let's use the typeclass ([`Applicative`]({{ '/docs/typeclasses/applicative' | relative_url }})), that contains the constructor function `pure()`.

```kotlin
interface Applicative<F>: Functor<F>, Typeclass {
  fun <A> pure(a: A): HK<F, A>
  
  /* ... */
}

object ListKWApplicativeInstance : ListKWFunctorInstance, Applicative<ListKWHK> {
  override fun <A> pure(a: A): HK<F, A> = listOf(a)
  
  /* ... */
}

inline fun <reified F> randomUserStructure(f: (Int) -> User, AP: Applicative<F> = applicative<F>()) =
  AP.pure(f(Math.random()))
```

Remember that all instances already defined in KΛTEGORY can be looked up globally

```kotlin:ank
applicative<ListKWHK>()
```

And now this function `randomUserStructure()` can be used for any datatype that implements ([`Applicative`]({{ '/docs/typeclasses/applicative' | relative_url }})).

```kotlin
val list: ListKW<User> = randomUserStructure(::User).ev()
// [User(342)]

val option: Option<User> = randomUserStructure(::User).ev()
// Some(User(765))

val either: Either<Unit, User> = randomUserStructure(::User).ev()
// Right(User(221))
```
