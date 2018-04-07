---
layout: docs
title: Glossary
permalink: /docs/patterns/glossary/
---

## Functional Programming Glossary

Note: This section keeps on growing! Keep an eye on it from time to time.

This document is meant to be an introduction to Functional Programming for people from all backgrounds.
We'll go through some of the key concepts, and then dive on their implementation and use in real world cases.

### Datatypes

A datatype is a class that encapsulates one reusable coding pattern.
These solutions have a canonical implementation that is generalised for all possible uses.

Some common patterns expressed as datatypes are absence handling with [`Option`]({{ '/docs/datatypes/option' | relative_url }}),
branching in code with [`Either`]({{ '/docs/datatypes/either' | relative_url }}),
catching exceptions with [`Try`]({{ '/docs/datatypes/try' | relative_url }}),
or interacting with the platform the program runs in using [`IO`]({{ '/docs/effects/io' | relative_url }}).

You can read more about all the [datatypes]({{ '/docs/datatypes/intro' | relative_url }}) that Arrow provides in its [section of the docs]({{ '/docs/datatypes/intro' | relative_url }}).

### Typeclasses

Typeclasses define a set of functions associated to one type.
This behavior is checked by a test suite called the "laws" for that typeclass.

You can use typeclasses to add new free functionality to an existing type,
or treat them as an abstraction placeholder for any one type that can implement the typeclass.

Examples of these behaviors are comparability ([`Eq`]({{ '/docs/typeclasses/eq' | relative_url }})),
composability ([`Monoid`]({{ '/docs/typeclasses/monoid' | relative_url }})),
its contents can be mapped from one type to another ([`Functor`]({{ '/docs/typeclasses/functor' | relative_url }})),
or error recovery ([`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }})).

You can read more about all the [typeclasses]({{ '/docs/typeclasses/intro' | relative_url }}) that Arrow provides in its [section of the docs]({{ '/docs/typeclasses/intro' | relative_url }}).

One example, the typeclass `Eq` parametrized to `F` defines equality between two objects of type `F`:

```kotlin
interface Eq<F> {
  fun eqv(a: F, b: F): Boolean
}
```

### Instances

A single implementation of a typeclass for a specific datatype or class.
Because typeclasses require generic parameters each implementation is meant to be unique for that parameter.

```kotlin
@instance(User::class)
interface UserEqInstance: Eq<User> {
  override fun User.eqv(b: User): Boolean = id == b.id
}
```

All typeclass instances provided Arrow can be found in the companion object of the type they're defined for, including platform types like String or Int.

```kotlin:ank:silent
import arrow.core.*
import arrow.data.*
import arrow.instances.*
```

```kotlin:ank
String.eq()
```

```kotlin:ank
Option.functor()
```

```kotlin:ank
MapK.semigroup<String, Int>(Int.semigroup())
```

```kotlin:ank
Either.monadError<Throwable>()
```

```kotlin:ank
ListK.traverse()
```

If you're defining your own instances and would like for them to be discoverable in their corresponding datatypes
you can generate it by annotating them as `@instance`, and Arrow's [annotation processor](https://github.com/arrow-kt/arrow#additional-setup) will create the extension functions for you.

### Type constructors

> NOTE: This approach to type constructors will be simplified if [KEEP-87](https://github.com/Kotlin/KEEP/pull/87) is approved. Go vote!

A type constructor is any class or interface that has at least one generic parameter. For example, 
[`ListK<A>`]({{ '/docs/datatypes/listK' | relative_url }}) or [`Option<A>`]({{ '/docs/datatypes/option' | relative_url }}).
They're called constructors because they're similar to a factory function where the parameter is `A`, except type constructors work only for types.
So, we could say that after applying the parameter `Int` to the type constructor `ListK<A>` it returns a `ListK<Int>`.
As `ListK<Int>` isn't parametrized in any generic value it is not considered a type constructor anymore, just a regular type.

Like functions, a type constructor with several parameters like [`Either<L, R>`]({{ '/docs/datatypes/either' | relative_url }}) can be partially applied for one of them to return another type constructor with one fewer parameter,
for example applying `Throwable` to the left side yields `Either<Throwable, A>`, or applying `String` to the right side results in `Either<E, String>`.

Type constructors are useful when matched with typeclasses because they help us represent instances of parametrized classes -the containers- that work for all generic parameters -the content-.
As type constructors is not a first class feature in Kotlin, Λrrow uses an interface `Kind<F, A>` to represent them.
Kind stands for Higher Kind, which is the name of the language feature that allows working directly with type constructors.

#### Higher Kinds

In a Higher Kind with the shape `Kind<F, A>`, if `A` is the type of the content then `F` has to be the type of the container.

A malformed Higher Kind would use the whole type constructor to define the container, duplicating the type of the content ~~`Kind<Option<A>, A>`~~.
This incorrect representation has large a number of issues when working with partially applied types and nested types.

What Λrrow does instead is define a surrogate type that's not parametrized to represent `F`.
These types are named same as the container and prefixed by For, as in `ForOption` or `ForListK`.

```kotlin
class ForOption private constructor()

sealed class Option<A>: Kind<ForOption, A>
```

```kotlin
class ForListK private constructor()

data class ListK<A>(val list: List<A>): Kind<ForListK, A>
```

As `ListK<A>` is the only existing implementation of `Kind<ForListK, A>`, we can define an extension function on `Kind<ForListK, A>` to do the downcasting safely for us.
This function by convention is called `fix()`, as in, fixing a type from something generic into concrete.

```kotlin
fun Kind<ForListK, A>.fix() = this as ListK<A>
```

This way we have can to convert from `ListK<A>` to `Kind<ForListK, A>` via simple subclassing and from `Kind<ForListK, A>` to `ListK<A>` using the function `fix()`.
Being able to define extension functions that work for partially applied generics is a feature from Kotlin that's not available in Java.
You can define `fun Kind<ForOption, A>.fix()` and `fun Kind<ForListK, A>.fix()` and the compiler can smartly decide which one you're trying to use.
If it can't it means there's an ambiguity you should fix!

The function `fix()` is already defined for all datatypes in Λrrow, alongside a typealias for its `Kind<F, A>` specialization done by suffixing the type with Of, as in `ListKOf<A>` or `OptionOf<A>`. If you're creating your own datatype that's also a type constructor and would like to create all these helper types and functions,
you can do so simply by annotating it as `@higerkind` and the Λrrow's [annotation processor](https://github.com/arrow-kt/arrow#additional-setup) will create them for you.

```kotlin
@higherkind
data class ListK<A>(val list: List<A>): ListKOf<A>

// Generates the following code:
//
// class ForListK private constructor()
// typealias ListKOf<A> = Kind<ForListK, A>
// fun ListKOf<A>.fix() = this as ListK<A>
```

Note that the annotation `@higerkind` will also generate the integration typealiases required by [KindedJ]({{ '/docs/integrations/kindedj' | relative_url }}) as long as the datatype is invariant. You can read more about sharing Higher Kinds and type constructors across JVM libraries in [KindedJ's README](https://github.com/KindedJ/KindedJ#rationale).

#### Using Higher Kinds with typeclasses

Now that we have a way of representing generic constructors for any type, we can write typeclasses that are parametrised for containers.

Let's take as an example a typeclass that specifies how to map the contents of any container `F`. This typeclass that comes from computer science is called a [`Functor`]({{ '/docs/typeclasses/functor' | relative_url }}).

```kotlin
interface Functor<F> {
  fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B>
}
```

See how the class is parametrized on the container `F`, and the function is parametrized to the content `A`. This way we can have a single representation that works for all mappings from `A` to `B`.

Let's define an instance of `Functor` for the datatype `ListK`, our own wrapper for lists.

```kotlin
@instance
interface ListKFunctorInstance : Functor<ForListK> {
  override fun <A, B> map(fa: Kind<ForListK, A>, f: (A) -> B): ListK<B> {
    val list: ListK<A> = fa.fix()
    return list.map(f)
  }
}
```

This interface extends `Functor` for the value `F` of `ListK`. We use an annotation processor `@instance` to generate an object out of an interface with all the default methods already defined, and to add that method to the global typeclass instance lookup. See that we respect the naming convention of datatype + typeclass + the word `Instance`.

```kotlin
@instance
interface ListKFunctorInstance : Functor<ForListK>
```

The signature of `map` once the types have been replaced takes a parameter `Kind<ForListK, A>`, which is the receiver, and a mapping function from `A` to `B`. This means that map will work for all instances of `ListK<A>` for whatever the value of `A` can be.

```kotlin
override fun <A, B> map(fa: Kind<ForListK, A>, f: (A) -> B): ListK<B>
```

The implementation is short. On the first line we downcast `Kind<ForListK, A>` to `ListK<A>` using `fix()`. Once the value has been downcasted, the implementation of map inside the `ListK<A>` we have obtained already implements the expected behavior of map.

```kotlin
val list: ListK<A> = fa.fix()
return list.map(f)
```

#### Using Higher Kinds and typeclasses with functions

Higher kinds are also used to model functions that require for a datatype to implement a typeclass. This way you can create functions that abstract behavior (defined by a typeclass) and allow callers to define which datatype they'd like to apply it to.

Let's use the typeclass [`Applicative`]({{ '/docs/typeclasses/applicative' | relative_url }}), that contains the constructor function `just()`.

```kotlin
interface Applicative<F>: Functor<F> {

  // Constructs the current datatype with a value of type A inside
  fun <A> just(a: A): Kind<F, A>
  
  /* ... */
}
```

Once we have this typeclass behavior define we can now write a function that's parametrized for any `F` that has one instance of `Applicative`. The function uses the constructor `just` to create a value of type `Kind<F, User>`, effectively generifying the return on any container `F`.

```kotlin
fun <F> Applicative<F>.randomUserStructure(f: (Int) -> User): Kind<F, User> =
  AP.just(f(Math.random()))
```

Now lets create a simple example instance of `Applicative` where our `F` is `ListK`. This implementation of a `just` constructor is trivial for lists, as it just requires wrapping the value.

```kotlin
@instance
interface ListKApplicativeInstance : Applicative<ForListK> {
  override fun <A> just(a: A): Kind<ForListK, A> = ListK(listOf(a))
  
  /* ... */
}
```

And now we can show how this function `randomUserStructure()` can be used for any datatype that implements [`Applicative`]({{ '/docs/typeclasses/applicative' | relative_url }}). As the function returns a value `Kind<F, User>` the caller is responsible of calling `fix()` to downcast it to the expected value.

```kotlin
val list = ListK.applicative().randomUserStructure(::User).fix()
//[User(342)]
```

```kotlin
val option = Option.applicative().randomUserStructure(::User).fix()
//Some(User(765))
```

```kotlin
val either = Either.applicative<Unit>().randomUserStructure(::User).fix()
//Right(User(221))
```

Passing the instance in every function call seems like a burden. So, because `randomUserStructure` is an extension function for [`Applicative`]({{ '/docs/typeclasses/applicative' | relative_url }}) we can omit the implicit parameter as long as we are within the scope of an Applicative instance. You can use the standard functions `with` and `run` for this.

```kotlin
with (ListK.applicative()) {
    // Lots of Kotlin here

    // Multiple calls

    randomUserStructure(::User).fix()
}
// [User(342)]
```

```kotlin
Option.applicative().run { 
    tupled(randomUserStructure(::User), randomUserStructure(::User))
}
// Some(value = Tuple2(a = User(765), b = User(127)))
```

To make the typeclass scope available to a whole class you can use simple delegation, like this:

```kotlin
class UserFetcher<F>(AP: Applicative<F>): Applicative<F> by AP {

    fun genUser() = randomUserStructure(::User)
}

UserFetcher(Option.applicative()).genUser().fix()
// Some(value = User(943))
```

This technique of [Dependency Injection]({{ '/docs/patterns/dependency_injection' | relative_url }}) that expresses dependencies as the implicit parameter of an extension function is called `Typeclassless`. You can read more about it in this [blog series](http://www.pacoworks.com/2018/02/25/simple-dependency-injection-in-kotlin-part-1/).
