---
layout: docs
title: Dependency Injection
permalink: /docs/patterns/dependency_injection/
---

If you would like to know about using the [`Reader`]({{ '/docs/datatypes/reader' | relative_url }}) datatype instead, visit [this article](https://medium.com/@JorgeCastilloPr/kotlin-dependency-injection-with-the-reader-monad-7d52f94a482e) by [Jorge Castillo](https://github.com/JorgeCastilloPrz).

## Dependency Injection using the `Typeclassless` technique

Arrow allows abstracting polymorphic code that operates over the evidence of having an instance of a [typeclass]({{ '/docs/typeclasses/intro' | relative_url }}) available.
This enables programs that are not coupled to specific datatype implementations.
The technique demonstrated below to write polymorphic code is available for all other typeclasses besides [`Functor`]({{ '/docs/typeclasses/functor' | relative_url }}).

```kotlin
fun <F> multiplyBy2(FT: Functor<F>, fa: Kind<F, Int>): Kind<F, Int> =
  /* ... */

multiplyBy2(Option.functor(), Option(1))
// Some(2)

multiplyBy2(Try.functor(), Try.just(1))
// Success(2)
```

In the example above we've defined a function that can operate over any data type for which a [`Functor`]({{ '/docs/typeclasses/functor' | relative_url }}) instance is available.
And then we applied `multiplyBy2` to two different datatypes for which Functor instances exist.
This technique applied to other Typeclasses allows users to describe entire programs in terms of behaviors typeclasses removing
dependencies to concrete data types and how they operate.

This technique does not enforce inheritance or any kind of subtyping relationship and is frequently known as [`ad-hoc polymorphism`](https://en.wikipedia.org/wiki/Ad_hoc_polymorphism)
and frequently used in programming languages that support [typeclass]({{ '/docs/typeclasses/intro' | relative_url }}) and [typeclass]({{ '/docs/patterns/glossary' | relative_url }}).

Entire libraries and applications can be written without enforcing consumers to use the lib author provided datatypes but letting
users provide their own provided there is typeclass instances for their datatypes.

Now, passing around a typeclass parameter like `Functor` to every function is repetitive, tedious, and error-prone. For that reason, many programming languages provide with a mechanism to describe the dependencies of the function without having to pass them as parameter explicitly. In Kotlin, this is achieved using extension functions.

## Typeclasses as dependencies

As described in the glossary, typeclasses are a grouping of constructors and extension functions on `Kind<F, A>` that form a DSL for the type they're declared to. To access these extension functions you need to be in the scope of a typeclass. That is, the typeclass object has to be bound to `this`. Kotlin provides two ways of achieving this: with standard library functions, and with more extension functions.

#### Standard library functions

The function `multiplyBy2` as defined above receives `FT: Functor<F>` as a parameter. We need to bind `FT` to `this` to be able to access the extension function `fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>` inside. For that we use the standard library function `typeclass.run { }`, which requires a value return.

```kotlin
fun <F> multiplyBy2(FT: Functor<F>, fa: Kind<F, Int>): Kind<F, Int> =
  FT.run { fa.map { it * 2 } }
```

If we have a block that does not return a value or `Unit`, I recommended to use either `with(typeclass) { }` to emphasize the difference.

```kotlin
fun <F> printAllValues(S: Show<Kind<F, Int>>, fa: List<Kind<F, Int>>): Unit {
  with (S) {
    fa.forEach { println(fa.show()) }
  }
}
```

#### Extension functions

An extension function is applied to a type, that becomes bound to `this` and enables calling all its functions without using `this.method()`. If we declare a function to depend on the typeclass, we get automatic access to the extension functions declared inside.

```kotlin
fun <F> Functor<F>.multiplyBy2(fa: Kind<F, Int>): Kind<F, Int> =
  fa.map { it * 2 }
```

And we can call it on the typeclass instances:

```kotlin
Option.functor().multiplyBy2(Try.just(1))
```

```kotlin
Try.functor().multiplyBy2(Try.just(1))
```

The same applies to functions without a return value, so you don't have to remember to use the right function.

```kotlin
fun <F> Show<Kind<F, Int>>.printAllValues(fa: List<Kind<F, Int>>): Unit {
  fa.forEach { println(fa.show()) }
}
```

## Declaring typeclass dependencies on client code

Knowing these two ways of accessing all the extension functions defined on a typeclass, how do we apply them to client code? This is heavily dependent on where Kotlin allows the use of extension functions. Let's go through each case.

### Scoped Functions

Any function that exists inside an object, companion object, or in the root of a file, can be declared as an extension function on its typeclass parameters.
Extension functions declared for the same types can call into each other without passing the type parameter explicitly.

This is the simplest, most direct case, and that's no coincidence. Most FP languages only allow functions scoped to global, a module, or an object that acts as a namespace.
Programs are composed by functions and tested on their typeclass and data parameters that define the functionality.

```kotlin
fun <A> Eq<A>.remove(l: List<A>, a: A): List<A> =
  l.filterNot { a.eqv(it) }
```

```kotlin
object FunctorLaws {
  fun <F> Functor<F>.test(f: (Int) -> Kind<F, Int>): List<Law> =
      listOf(
        Law("Functor Laws: Covariant Identity", { covariantIdentity(f) }),
        Law("Functor Laws: Covariant Composition", { covariantComposition(f) })
      )

  fun <F> Functor<F>.covariantIdentityTest(f: (Int) -> Kind<F, Int>): Unit = ...

  fun <F> Functor<F>.covariantComposition(f: (Int) -> Kind<F, Int>): Unit = ...
}

...

import arrow.test.FunctorLaws.test

Option.functor().test { it.some() }
```

```kotlin
fun <F> MonadError<F, Throwable>.ankMonadErrorInterpreter(): FunctionK<ForAnkOps, F> =
  object : FunctionK<ForAnkOps, F> {
    override fun <A> invoke(fa: Kind<ForAnkOps, A>): Kind<F, A> {
      val op = fa.ev()
      return when (op) {
          is AnkOps.CreateTarget -> catch { createTargetImpl(op.source, op.target) }
          is AnkOps.GetFileCandidates -> catch { getFileCandidatesImpl(op.target) }
          is AnkOps.ReadFile -> catch { readFileImpl(op.source) }
          is AnkOps.ParseMarkdown -> catch { parseMarkDownImpl(op.markdown) }
          is AnkOps.ExtractCode -> catch { extractCodeImpl(op.source, op.tree) }
          is AnkOps.CompileCode -> catch { compileCodeImpl(op.snippets, op.compilerArgs) }
          is AnkOps.ReplaceAnkToLang -> catch { replaceAnkToLangImpl(op.compilationResults) }
          is AnkOps.GenerateFiles -> catch { generateFilesImpl(op.candidates, op.newContents) }
      }
    }
  }
```

### Classes

As typeclasses are defined as interfaces in Kotlin, it's trivial to convert any class you control into an implementer of a typeclass by using delegation.

```kotlin
data class Parser: Monad<ForOption> by Option.monad(), Traverse<ForListK> by ListK.traverse() {
  fun parseInt(s: String): Option<Int> =
    try {
      parseOrThrow(s).just()
    } catch (t: Throwable) {
      none()
    }

  fun parseInts(l: List<Option<String>>): Option<List<Int>> =
    l.k().traverse(this, { it.flatMap { parseInt(it) } }).fix()
}

val parser = Parser()

parser.parseInts(listOf("1".some(), "2".some()))
// Some([1, 2])

parser.parseInts(listOf("bla".some(), "2".some()))
// None
```

```kotlin
class UserFetcher<F>(ME: MonadError<F, Throwable>, val api: ApiService): MonadError<F, Throwable> by ME {

  fun getUserFriends(fid: Kind<F, UserId>): Kind<F, List<User>> =
    bindingCatch {
      val id = fid.bind()
      val user = api.getUser(id).bind()
      user.friendIds.map { api.getUser(it.id) }.bind()
    }.handleError { listOf() }

  fun createId(id: String): Kind<F, UserId> =
    catch { parseIdOrThrow(id) }
      .map { UserId(it) }
}

...

val fetcher = UserFetcher(Either.monadError(), ApiService())

fetcher.getUserFriends(fetcher.createId("122344"))
// Right([User(id="123", friendIds=["122344"]), User(id="44", friendIds=["122344", "1245"]), User(id="1245", friendIds=["122344", "44"])])])
```

### Methods inside a class

This is the case where we find the limitations of the Kotlin compiler. Once you're inside a class, `this` gets bound to the class scope.
This makes that extension functions declared inside a class require using the standard library functions to access them, which is not very idiomatic.

```kotlin
// WRONG
class Parser {
  fun Monad<ForOption>.parseInt(s: String): Option<Int> = ...

  fun ???.parseInts(l: List<Option<String>>): Option<List<Int>> = ...
}

// TEDIOUS AND NOT IDIOMATIC
Parser().run { Option.monad().parseInt("123") }
```

For these cases the most ergonomic option is not to use extension functions, and instead fall back to regular parameter passing.
Once inside the method we can use the standard library functions to access the scope of the typeclass, and treat the method the same as if it was a scoped function.

```kotlin
class Parser {
  fun parseInt(M: Monad<ForOption>, s: String): Option<Int> =
    M.run { ... }

  fun parseInts(M: Monad<ForOption>, l: List<Option<String>>): Option<List<Int>> =
    M.run { ListK.traverse().run { ... } }
}

// JUST MEH
Parser().parseInts(Option.monad(), listOf("1".some(), "2".some()))
```

We are almost back to where we started with parameter passing, at least on this first layer.
Except, there are some benefits once you're inside these public functions, as you can declare private extension methods inside the class and you will be able to seamlessly call them as well as other scoped functions.

## Composing dependencies

In several of the prior examples we have found that we depend on multiple typeclasses that are not part of the same hierarchy.
This is not an uncommon case, and it has to be addressed. How can we express this combination? Luckily, the Kotlin language comes to our aid again.

Kotlin has a keyword `where`, used in method signatures to describe multiple bounds on a generic parameter.
You can define multiple clauses for a single generic parameter that will be the type we declare an extension function on.

Let's see one example of a function that depends on both `Applicative` and `Show`:

```kotlin
fun <F> Applicative<F>.findUserName(S: Show<User>, id: Kind<F, UserId>): Kind<F, Int> = S.run {
  id.map { fetchUser(it) }.map { it.show() }
}
```

Let's do a rewrite using `where` to define multiple typeclass requirements.
We will create a generic parameter `TC` that is bound to both typeclasses. That parameter will be the one we declare an extension on.
We'll also remove running `Show` as it isn't required anymore.

```kotlin
fun <F, TC> TC.findUserName(id: Kind<F, UserId>)
: Kind<F, String> where TC: Applicative<F>, TC: Show<User> =
  id.map { fetchUser(it) }.map { it.show() }
```

Nice! Problem solved! Now, how does one create an `Applicative` + `Show`?

### Creating impossible types

At this point is where type inference gives us an easy way of creating objects that define bounds for multiple typeclasses.
A simple helper function with an inferred return parameter saves us from having to write the type manually:

```kotlin
fun <F, T> createApplicativeShow(AP: Applicative<F>, S: Show<T>) =
  object: Applicative<F> by AP, Show<T> by S

createApplicativeShow(Option.applicative(), Show.any())
  .findUserName(Some("1"))
// Some("Paco")

createApplicativeShow(Either.applicative<Throwable>(), Show.any())
  .findUserName(Some("1"))
// Right("Paco")
```

### Creating real types

Alternatively, you can compose the types in a new interface and give it a new name.
This way you can also skip `where` clauses.
The major downside is that you'll find a combinatory explosion of new interfaces on your codebase,
so use them scarcely when you have a type that repeats itself pervasively through the codebase.

```kotlin
interface ApplicativeShow<F, T>: Applicative<F>, Show<T>

fun <F> ApplicativeShow<F, User>.findUserName(id: Kind<F, UserId>): Kind<F, String> = ...

fun <F, T> createApplicativeShow(AP: Applicative<F>, S: Show<T>): ApplicativeShow<F, T> =
  object: ApplicativeShow<F, T>, Applicative<F> by AP, Show<T> by S
```

There is also another reason to create a new real type, when you have two colliding typeclass instances.

### Disambiguating colliding instances

You may find some cases where two typeclass requirements collide because they're defined for the same type, or include a typeclass on the same hierarchy.
Another way of looking at this kind of collisions is when annotation-based DI frameworks need `javax.inject.Named`.

In the case of Arrow, you will need to disambiguate them manually based off fields.
This has some benefits over the DI framework approach:

* it will only happen at the caller layer, once per collision
* it doesn't require any maintenance changes on all the declaration sites
* the solution is strongly typed
* the solution depends on a function call rather than a string convention, so static analysis and refactor tools are available

This collision happens in an example we saw before. The typeclasses `Monad<F>` and `Traverse<F>` both inherit from `Functor<F>`.
You cannot define a boundary for two different values of `F`. This happened in our Parser class:

```kotlin
// Compiler error: Type parameter <F> for 'Functor' has inconsistent bounds: ForOption, ForListK
data class Parser: Monad<ForOption> by Option.monad(), Traverse<ForListK> by ListK.traverse()
```

Same as with methods inside a class, we have a situation where multiple `this` bindings collide.
The way of disambiguating is by creating a new type that provides both bounds as functions, and has an instance in its companion object:

```kotlin
interface ParserDependencies {
  fun MO(): Monad<ForOption> = Option.monad()

  fun TL(): Traverse<ForListK> = ListK.traverse()

  companion object: ParserDependencies
}
```

And now we can rewrite our parser class avoiding ambiguity:

```kotlin
data class Parser: ParserDependencies by ParserDependencies {
  fun parseInt(s: String): Option<Int> = MO().run {
    try {
      parseOrThrow(s).just()
    } catch (t: Throwable) {
      none()
    }
  }

  fun parseInts(l: List<Option<String>>): Option<List<Int>> = TL().run {
    l.k().traverse(this, { MO().run { it.flatMap { parseInt(it) } } }).fix()
  }
}
```

We have the same benefits and pitfalls we had in methods inside a class.
Once inside the `run` blocks, we're able to call all other scoped extension functions declared for the enclosing typeclass.
This disambiguation only happens in one of the layers, who's responsible for the ambiguity in the first place!

Cool, once we have all the pieces in place, explained and understood, let's mention one last use case: general dependency injection!

## Using DI to inject any object

Now that you know how to define new types to declare multiple dependencies, and add fields to prevent collision between typeclasses in the same hierarchy,
what prevents you from adding arbitrary data to any fields of the type? Well, the answer is nothing :D

Let's grab one of our previous examples and refactor it

```kotlin
class UserFetcher<F>(ME: MonadError<F, Throwable>, val api: ApiService): MonadError<F, Throwable> by ME {

  fun getUserFriends(fid: Kind<F, UserId>): Kind<F, List<User>> =
    bindingCatch {
      val id = fid.bind()
      val user = api.getUser(id).bind()
      user.friendIds.map { api.getUser(it.id) }.bind()
    }.handleError { listOf() }

  fun createId(id: String): Kind<F, UserId> = ...
}
```

On the first step, we'll create a new type enclosing all the dependencies:

```kotlin
interface FetcherDependencies: MonadError<F, Throwable> {

  fun api(): ApiService

  companion object {
    operator fun invoke(ME: MonadError<F, Throwable>, api: ApiService): FetcherDependencies =
      object: FetcherDependencies, MonadError<F, Throwable> by ME {
        override fun api() = api
      }
  }
}
```

With this type we don't need a class anymore, as it already encloses all state.
We can move the functions to an object scope, and make them depend on `FetcherDependencies` and `MonadError<F, Throwable>`.

```kotlin
object Api {
  fun FetcherDependencies.getUserFriends(fid: Kind<F, UserId>): Kind<F, List<User>> =
    bindingCatch {
      val id = fid.bind()
      val user = api().getUser(id).bind()
      user.friendIds.map { api().getUser(it.id) }.bind()
    }.handleError { listOf() }

  fun MonadError<F, Throwable>.createId(id: String): Kind<F, UserId> = ...
}

...

import Api.getUserFriends

val deps = FetcherDependencies(Either.monadError(), ApiService())

deps.getUserFriends(deps.createId("1234"))
// Right([User(id="33", friendIds=["1234", "987"])])
```

What have we gained from this change? We have replaced one concretion, a final class, with an abstraction that's an interface.

* The code is more functional: we have one structural type and many functions that act on it
* A single instance of the object that's used but not retained by the functions, so there's single ownership
* We can still define as many extensions to the functionality as we want
* We can call any function that requires just one or some of the types, encapsulating the rest

And as a consequence to all of this, testability and refactoring possibilities are through the roof!
Many of the promises of OOP, fulfilled with simple functions and interfaces.

```kotlin
import Api.*

with (Option.monadError()) {
  createId("123") shouldBe Some(123)

  createId("-123") shouldBe None

  createId("asfgasf") shouldBe None

  createId("") shouldBe None
}

with (FetcherDependencies(Option.monadError(), MockApiService())) {
  getUserFriends(createId("")) shouldBe listOf()

  getUserFriends(createId("sfgafg")) shouldBe listOf()

  getUserFriends(createId("123")) shouldBe listOf(User("321", listOf("123")))
}
```

### Scoping

Typeclasses are completely stateless collections of functions.
Once you start adding other objects that are stateful you need to take care of the lifecycle of your Dependencies object.

Luckily for us, as there is only one reference to the Syntax object that's passed around across layers through function parameters instead of being retained by classes, it's easy to track its lifecycle and manage it.

Assuming this Dependencies object is completely stateless and it lives at the global scope, a simple root value suffices:

```kotlin
import Api.*

val deps = FetcherDependencies(Either.monadError(), ApiService())

fun main(args: Array<String>) {
  println(deps.getUserFriends(deps.createId("1234")))
}
```

Let's introduce some nuance and assume the scope is now per-screen, as you would with Android activities.
The expected lifecycle of the Dependencies object is the same as the Activity, and gets garbage collected alongside it.

```kotlin
import Api.*

class SettingsActivity: Activity {
  val deps = FetcherDependencies(Either.monadError(), ActivityApiService(this))

  override fun onResume() {
    val id = deps.createId("1234")

    user.text = 
      id.fix().map { it.toString() }.getOrElse { "" }

    friends.text = 
      deps.getUserFriends(id).fix().getOrElse { emptyList() }.joinToString()
  }
}
```

To recap, to scope efficiently means that you have to manually create and store your Dependencies object once, at the origin of the scope, and can now implicitly pass it around across all other layers without explicitly using it as a parameter. Each function is also limited to only use the dependencies that are relevant to itself.
