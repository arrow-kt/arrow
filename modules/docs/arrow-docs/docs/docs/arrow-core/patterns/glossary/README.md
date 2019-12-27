---
layout: docs-core
title: Glossary
permalink: /docs/patterns/glossary/
---

## Functional Programming Glossary




Note: This section keeps on growing! Keep an eye on it from time to time.

This document is meant to be an introduction to Functional Programming for people from all backgrounds.
We'll go through some of the key concepts, and then dive into their implementation in real world cases.

Some similar documents focused on explaining general concepts, rather than Arrow's versions,
can be found for examples [in JavaScript](https://github.com/hemanth/functional-programming-jargon) and [in Scala](https://gist.github.com/jdegoes/97459c0045f373f4eaf126998d8f65dc).

### Datatypes

A datatype is a class that encapsulates one reusable coding pattern.
These solutions have a canonical implementation that is generalized for all possible uses.

Some common patterns expressed as datatypes are absence handling with [`Option`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-option/' | relative_url }}),
branching in code with [`Either`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-either/' | relative_url }}),
catching exceptions with [`Try`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-try/' | relative_url }}),
or interacting with the platform the program runs in using [`IO`]({{ '/docs/effects/io' | relative_url }}).

Some of these patterns are implemented using a mix of `sealed` classes, where each inheritor is a `data` class.
For example, the internal representation of an `Option` is a `sealed` class with two `data` classes: `Some<A>(val a: A)`, and `None`.
And `Ior` is a `sealed` class with three `data` class inheritors: `Left(val a: A)`, `Right(val b: B)`, and `Both(val a: A, val b: B)`.

Datatypes that express patterns like deferred evaluation can do it by nesting themselves with every operation they chain. One example is `IO`.

```kotlin:ank
import arrow.fx.*

IO { 0 }
 .flatMap { IO { it * 2 } }
 .map { it + 1 }
```

You can read more about all the [datatypes]({{ '/docs/datatypes/intro' | relative_url }}) that Arrow provides in its [section of the docs]({{ '/docs/datatypes/intro' | relative_url }}).

### Typeclasses

Typeclasses are interface abstractions that define a set of extension functions associated to one type.
These extension functions are canonical and consistent across languages and libraries.
And they have inherent mathematical properties that are testable, such as commutativity or associativity.

Examples of behaviors abstracted by typeclasses are: Comparability ([`Eq`]({{ '/docs/arrow/typeclasses/eq' | relative_url }})),
composability ([`Monoid`]({{ '/docs/arrow/typeclasses/monoid' | relative_url }})),
its contents can be mapped from one type to another ([`Functor`]({{ '/docs/arrow/typeclasses/functor' | relative_url }})),
or error recovery ([`MonadError`]({{ '/docs/arrow/typeclasses/monaderror' | relative_url }})).

Typeclasses have two main uses:

* Add new functionality to types. For example, if I know how to compare two objects, I can add a new extension function to check for inequality.
Or, if I know how to aggregate objects together, I can add an extension function for `List` that aggregates all of its elements.
The number of extra extension functions that you get per typeclass can be from one in `Eq` to 17 (!) in `Foldable`.

* Abstracting over behavior. Like any other interface, you can use them in your functions and classes as a way of talking about the capabilities of the implementation,
without exposing the details. This way, you can create APIs that work the same for `Option`, `Try`, or `Observable`.

You can read more about all the [typeclasses]({{ '/docs/typeclasses/intro' | relative_url }}) that Arrow provides in its [section of the docs]({{ '/docs/typeclasses/intro' | relative_url }}).

Let's dive into one example. The typeclass `Eq` parametrized to `F` defines equality between two objects of type `F`:

```kotlin:ank:silent
interface Eq<F> {
  fun F.eqv(b: F): Boolean

  fun F.neqv(b: F): Boolean =
    !eqv(b)
}
```

### Instances and Extensions Interfaces

A single implementation of a typeclass for a specific datatype or class.
Because typeclasses require generic parameters, each implementation is meant to be unique for that parameter.

For example, given a class like this:

```kotlin:ank:silent
data class User(val id: Int) {
  companion object
}
```

We can declare that instances of this class can be equated based on their `id` property, and, therefore, that `User` itself is an instance of the `Eq` typeclass:

```kotlin:ank:silent
import arrow.extension

@extension
interface UserEq: Eq<User> {
  override fun User.eqv(b: User): Boolean = id == b.id
}
```

Note that classes must have companion objects for this to work. All typeclass instances provided by Arrow can be found in the companion object of the type they're defined for, including platform types like String or Int.

```kotlin:ank:silent
import arrow.*
import arrow.core.*
import arrow.core.extensions.*
import arrow.typeclasses.*
import arrow.core.extensions.option.functor.*
import arrow.core.extensions.either.monadError.*
import arrow.core.extensions.listk.traverse.*
```

```kotlin:ank:silent
String.eq()
```

```kotlin:ank:silent
Option.functor()
```

```kotlin:ank:silent
import arrow.core.extensions.mapk.semigroup.*

MapK.semigroup<String, Int>(Int.semigroup())
```

```kotlin:ank:silent
Either.monadError<Throwable>()
```

```kotlin:ank:silent
ListK.traverse()
```

If you're defining your own instances and would like for them to be discoverable in their corresponding datatypes' companion object, you can generate it by annotating them as `@extension`, and Arrow's [annotation processor](https://github.com/arrow-kt/arrow#additional-setup) will create the extension functions for you.

NOTE: If you'd like to use `@extension` for transitive typeclasses, like a `Show<List<A>>` that requires a function returning a `Show<A>`, you'll need the function providing the transitive typeclass to have 0 parameters. This will make the transitive typeclass a parameter of the extension function.


### Syntax

When creating an instance with the `@extension` annotation, the processor generates extension functions for the types that are available simply by importing them. See, for example, the coroutine function `binding`, the constructor `map`, and the method `sequence` defined in the instances of `Monad`, `Applicative`, and `Traverse`:

```kotlin:ank:silent
import arrow.core.Option
import arrow.core.extensions.fx

Option.fx {
  val (a) = Option(1)
  val (b) = Option(a + 1)
  a + b
}
```

```kotlin:ank
import arrow.core.extensions.option.apply.map

map(Option(1), Option(2), Option(3)) { (one, two, three) ->
  one + two + three
}
```

```kotlin:ank:silent
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.option.applicative.applicative

listOf(Option(1), Option(2), Option(3)).sequence(Option.applicative())
```

```kotlin:ank
import arrow.core.extensions.fx

Try.fx {
  val (a) = Try { 1 }
  val (b) = Try { a + 1 }
  a + b
}
```

```kotlin:ank:silent
import arrow.core.extensions.`try`.apply.map

map(Try { 1 }, Try { 2 }, Try { 3 }) { (one, two, three) ->
  one + two + three
}
```

```kotlin:ank:silent
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.either.applicative.applicative

listOf(Right(1), Right(2), Right(3)).sequence(Either.applicative<Throwable>())
```

### Type constructors

> NOTE: This approach to type constructors will be simplified if [KEEP-87](https://github.com/Kotlin/KEEP/pull/87) is approved. Go vote!

A type constructor is any class or interface that has at least one generic parameter. For example,
[`ListK<A>`]({{ '/docs/arrow/core/listk' | relative_url }}) or [`Option<A>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-option/' | relative_url }}).
They're called constructors because they're similar to a factory function where the parameter is `A`, except type constructors only work for types.
So, we could say that, after applying the parameter `Int` to the type constructor `ListK<A>`, it returns a `ListK<Int>`.
As `ListK<Int>` isn't parametrized in any generic value, it is not considered a type constructor anymore, just a regular type.

As with functions, a type constructor with several parameters like [`Either<L, R>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-either/' | relative_url }}) can be partially applied for one of them to return another type constructor with one fewer parameter.
For example, applying `Throwable` to the left side yields `Either<Throwable, A>`, or applying `String` to the right side results in `Either<E, String>`.

Type constructors are useful when matched with typeclasses because they help us represent instances of parametrized classes — the containers — that work for all generic parameters — the content.
As type constructors is not a first class feature in Kotlin, Λrrow uses an interface `Kind<F, A>` to represent them.
Kind stands for Higher Kind, which is the name of the language feature that allows working directly with type constructors.

#### Higher Kinds

In a Higher Kind with the shape `Kind<F, A>`, if `A` is the type of the content, then `F` has to be the type of the container.

A malformed Higher Kind would use the whole type constructor to define the container, duplicating the type of the content ~~`Kind<Option<A>, A>`~~.
This incorrect representation has a large number of issues when working with partially applied types and nested types.

What Λrrow does instead is define a surrogate type that's not parametrized to represent `F`.
These types are named the same as the container and prefixed by For-, as in `ForOption` or `ForListK`.
You have seen these types used in the Syntax section above!

```kotlin:ank:silent
class ForOption private constructor() { companion object {} }

sealed class Option<A>: Kind<ForOption, A>
```

```kotlin:ank:silent
class ForListK private constructor() { companion object {} }

data class ListK<A>(val list: List<A>): Kind<ForListK, A>
```

As `ListK<A>` is the only existing implementation of `Kind<ForListK, A>`, we can define an extension function on `Kind<ForListK, A>` to do the downcasting safely for us.
This function by convention is called `fix()`, as in fixing a type from something generic into something concrete.

```kotlin:ank:silent
fun <A> Kind<ForListK, A>.fix() = this as ListK<A>
```

This way, we can convert from `ListK<A>` to `Kind<ForListK, A>` via simple subclassing, and from `Kind<ForListK, A>` to `ListK<A>` using the function `fix()`.
Being able to define extension functions that work for partially applied generics is a feature from Kotlin that's not available in Java.
You can define `fun Kind<ForOption, A>.fix()` and `fun Kind<ForListK, A>.fix()`, and the compiler can smartly decide which one you're trying to use.
If it can't, it means there's an ambiguity you should fix!

The function `fix()` is already defined for all datatypes in Λrrow, alongside a typealias for its `Kind<F, A>` specialization done by suffixing the type with Of, as in `ListKOf<A>` or `OptionOf<A>`. If you're creating your own datatype that's also a type constructor and would like to create all these helper types and functions,
you can do so by simply annotating it as `@higherkind`, and the Λrrow's [annotation processor](https://github.com/arrow-kt/arrow#additional-setup) will create them for you.

```kotlin:ank:silent
@higherkind data class ListK<A>(val list: List<A>): ListKOf<A>

// Generates the following code:
//
// class ForListK private constructor() { companion object {} }
// typealias ListKOf<A> = Kind<ForListK, A>
// fun ListKOf<A>.fix() = this as ListK<A>
```

Note that the annotation `@higherkind` will also generate the integration typealiases required by [KindedJ]({{ '/docs/integrations/kindedj' | relative_url }}) as long as the datatype is invariant. You can read more about sharing Higher Kinds and type constructors across JVM libraries in [KindedJ's README](https://github.com/KindedJ/KindedJ#rationale).

#### Using Higher Kinds with typeclasses

Now that we have a way of representing generic constructors for any type, we can write typeclasses that are parametrised for containers.

Let's use as an example a typeclass that specifies how to map the contents of any container `F`. This typeclass that comes from computer science is called a [`Functor`]({{ '/docs/arrow/typeclasses/functor' | relative_url }}).

```kotlin:ank:silent
interface Functor<F> {
  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
}
```

See how the class is parametrized on the container `F`, and the function is parametrized to the content `A`. This way, we can have a single representation that works for all mappings from `A` to `B`.

Let's define an instance of `Functor` for the datatype `ListK`, our own wrapper for lists.

```kotlin
@extension
interface ListKFunctor : Functor<ForListK> {
  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): Kind<ForListK, B> {
    return this.fix().map(f)
  }
}
```

This interface extends `Functor` for the value `F` of `ListK`. We use an annotation processor `@extension` to generate an object out of an interface with all the default methods already defined, and to add an extension function to get it into the companion object of the datatype.
The `@extension` processor also projects all type class declared functions into the data type that it's extending as extensions functions.
These extensions functions may be imported a la carte when working with concrete data types.

```kotlin:ank:silent
@extension
interface ListKFunctor : Functor<ForListK>
```

```kotlin
// Somewhere else in the codebase
ListK.functor()
```

The signature of `map` takes a parameter `Kind<ForListK, A>`, which is the receiver and a mapping function from `A` to `B`, once the types have been replaced. This means that map will work for all instances of `ListK<A>` for whatever the value of `A` can be.

```kotlin
override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B>
```

The implementation is short. On the first line, we downcast `Kind<ForListK, A>` to `ListK<A>` using `fix()`. Once the value has been downcasted, the implementation of map inside the `ListK<A>` we have obtained already implements the expected behavior of map.

```kotlin
val list: ListK<A> = this.fix()
return list.map(f)
```

#### Using Higher Kinds and typeclasses with functions

Higher kinds are also used to model functions that require a datatype to implement a typeclass. This way, you can create functions that abstract behavior (defined by a typeclass) and allow callers to define which datatype they'd like to apply it to.

Let's use the typeclass [`Applicative`]({{ '/docs/arrow/typeclasses/applicative' | relative_url }}) that contains the constructor function `just()`.

```kotlin:ank:silent
interface Applicative<F>: Functor<F> {

  // Constructs the current datatype with a value of type A inside
  fun <A> just(a: A): Kind<F, A>

}
```

Once we have this typeclass behavior defined, we can now write a function that's parametrized for any `F` that has one instance of `Applicative`. The function uses the constructor `just` to create a value of type `Kind<F, User>`, effectively generifying the return on any container `F`.

```kotlin:ank:silent
fun <F> Applicative<F>.randomUserStructure(f: (Int) -> User): Kind<F, User> =
  this.just(f(Math.random().toInt()))
```

Now let's create a simple example instance of `Applicative`, where our `F` is `ListK`. This implementation of a `just` constructor is trivial for lists, as it just requires wrapping the value.

```kotlin
@extension
interface ListKApplicative : Applicative<ForListK> {
  override fun <A> just(a: A): Kind<ForListK, A> = ListK(listOf(a))
}
```

And now we can show how this function `randomUserStructure()` can be used for any datatype that implements [`Applicative`]({{ '/docs/arrow/typeclasses/applicative' | relative_url }}). As the function returns a value `Kind<F, User>`, the caller is responsible for calling `fix()` to downcast it to the expected value.

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

Passing the instance in every function call seems like a burden. So, because `randomUserStructure` is an extension function for [`Applicative`]({{ '/docs/arrow/typeclasses/applicative' | relative_url }}), we can omit the implicit parameter as long as we are within the scope of an Applicative instance. You can use the standard functions `with` and `run` for this.

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

It is also possible to use a form of [`Dependency Injection`]({{ '/docs/patterns/dependency_injection' | relative_url }}) to make the typeclass scope available to a whole class. For example, using simple delegation:

```kotlin
class UserFetcher<F>(AP: Applicative<F>): Applicative<F> by AP {
    fun genUser() = randomUserStructure(::User)
}

UserFetcher(Option.applicative()).genUser().fix()
// Some(value = User(943))
```

To learn more about this `Typeclassless` technique, you should head to the [`Dependency Injection`]({{ '/docs/patterns/dependency_injection' | relative_url }}) documentation.

### Side-effects and Effects

A side-effect is a statement that changes something in the running environment. Generally, this means setting a variable, displaying a value on screen, writing to a file or a database, logging, start a new thread . . .

When talking about side-effects, we generally see functions that have the signature `(...) -> Unit`, meaning that, unless the function doesn't do anything, there's at least one side-effect. Side-effects can also happen in the middle of another function, which is an undesirable behavior in Functional Programming.

Side-effects are too general to be unit tested for because they depend on the environment. They also have poor composability. Overall, they're considered to be outside the Functional Programming paradigm, and are often referred to as "impure" functions.

Because side-effects are unavoidable in any program, FP provides several datatypes for dealing with them! One way is by abstracting their behavior. The simplest examples of this are the `Writer`datatype, which allows you to write to an information sink like a log or a file buffer; or `State` datatype, which simulates scoped mutable state for the duration of an operation.

For more complicated side-effects that can throw or jump threads, we need more advanced datatypes, called Effects, that wrap over impure operations. Some of these datatypes may already be familiar to you, like [`rx.Observable`]({{ '/docs/integrations/rx2/' | relative_url }}), [`kotlinx.coroutines.Deferred`]({{ '/docs/integrations/kotlinxcoroutines/' | relative_url }}), or Arrow's [`IO`]({{ '/docs/effects/io/' | relative_url }}). These Effects compose, catch exceptions, control asynchrony, and, most importantly, can be run lazily. This gets rid of the issues with side-effects.

Although one can also write the whole program in an imperative way inside a single Effect wrapper, that wouldn't be very efficient, as you don't get any of its benefits. :D
