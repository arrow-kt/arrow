---
layout: docs
title: Traverse
permalink: /docs/arrow/typeclasses/traverse/
redirect_from:
  - /docs/typeclasses/traverse/
---

## Traverse

{:.intermediate}
intermediate

In functional programming it is very common to encode "behaviors" as data types - common behaviors include `Option` for possibly missing values, `Either` and `Validated` for possible errors, and [`Promise`]({{ '/docs/effects/promise/' | relative_url }}) for asynchronous computations, which has the same purpose as a `Future`, only pure and lazy.

These behaviors tend to show up in functions working on a single piece of data - for instance parsing a single `String` into an `Int`, validating a login, or asynchronously fetching website information for a user.

```kotlin:ank
import arrow.core.Either
import arrow.core.Option
import arrow.fx.ForIO
import arrow.fx.Promise

interface Profile
interface User

fun userInfo(u: User): Promise<ForIO, Profile> = TODO()
```

Each function asks only for the data it actually needs; in the case of `userInfo`, a single `User`. Certainly, we could write one that takes a `List<User>` and fetches profile for all of them,but it would be a bit strange. If we just wanted to fetch the profile of just one user, we would either have to wrap it in a `List` or write a separate function that takes in a single user anyways. More fundamentally, functional programming is about building lots of small, independent pieces and composing them to make larger and larger pieces - does this hold true in this case?

Given just `(User) -> Promise<ForIO, Profile>`, what should we do if we want to fetch profiles for a `List<User>`? We could try familiar combinators like map.

```kotlin:ank
fun profilesFor(users: List<User>): List<Promise<ForIO, Profile>> = users.map(::userInfo)
```

Note the return type `List<Promise<ForIO, Profile>>`. This makes sense given the type signatures, but seems unwieldy. We now have a list of asynchronous values, and to work with those values we must then use the combinators on `Promise` for every single one. It would be nicer instead if we could get the aggregate result in a single `Promise`, say a `Promise<ForIO, List<Profile>>`.

There exists a much more generalized form that would allow us to parse a `List<String>` or validate credentials for a `List<User>`.

Enter `Traverse`.

### The Typeclass

At center stage of Traverse is the `traverse` method.

```kotlin
import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

interface Traverse<F> : Functor<F>, Foldable<F> {
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>
}
```

In our above example, `F` is `List`, and `G` is `Option`, `Either`, or `Promise`. For the profile example, traverse says given a `List<User>` and a function `(User) -> Promise<ForIO, Profile>`, it can give you a `Promise<ForIO, List<Profile>>`.

Abstracting away the `G` (still imagining `F` to be `List`), `traverse` says given a collection of data, and a function that takes a piece of data and returns a value `B` wrapped in a container `G`, it will traverse the collection, applying the function and aggregating the values (in a `List`) as it goes.

In the most general form, `Kind<F, A>` is some sort of context `F` which may contain a value `A` (or several). While `List` tends to be among the most general cases, there also exist `Traverse` instances for `Option`, `Either`, and `Validated` (among others).

Essentially, one uses `Traverse`, when there is a function `(A) -> Kind<G, B>` and `G` is an `Applicative` instance, thus `G` provides `just` and `map` functions e.g.: just as `Promise<ForIO, B>` provides and you want to `lift` the previous function to work on `Kind<F, A>` where `F` is a `Traverse` instance - and you still care about `A` (the `Profiles`) `(List<User>)` and return `Kind<G, Kind<F, B>>` e.g.: `Promise<ForIO, List<Profile>>`

### To fold Or not to fold

Even though, `Foldable` and `Traverse` are related, because both 'reduce their values to something', it is not obvious why to consider `Traverse` over `Foldable`.

Here is a small example:

```kotlin:ank:playground
import arrow.core.MapK
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import arrow.core.k

fun main(){
  //sampleStart
  val map: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()

  val optionMapK: Option<MapK<String, String>> = map.traverse(Option.applicative()) { Some("$it") }.fix()
  
  // val optionMapKBoilered = map.foldLeft(Some(emptyMap())) { acc: Option<MapK<String, String>>, i: Int ->
  //   acc.fold({ emptyMap() }, { /*Some logic to retrieve the key of a value, transform it and add it to the accumulated Map*/ })
  // }
  //sampleEnd
  println(optionMapK)
}
```

Both values try to attain the same thing, but what `Foldable` lacks is that it solely drills down to it's `A` here `Int` and does not preserve its shape `F` - `MapK`. This is where `Traverse` shines, whenever you care about the Output `B` from `(A) -> B` and the existing shape of `F` you would use `traverse`.

Additionally, you're able to wrap your context `F` within a `G`. That is, may among others, the reason why `Traverse` is strictly more powerful than `Foldable`.

You can also misuse it's powers - where a `map` is more considerable: 

```kotlin:ank
import arrow.core.Id
import arrow.core.MapK
import arrow.core.extensions.id.applicative.applicative
import arrow.core.fix
import arrow.core.k
import arrow.core.value

val map: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()

map.traverse(Id.applicative()) { Id("$it") }.fix().value()
```
```kotlin:ank
map.map { "$it" }
```

### Traversables are Foldable

The `Foldable` type class abstracts over “things that can be folded over” similar to how `Traverse` abstracts over “things that can be traversed.” It turns out `Traverse` is strictly more powerful than `Foldable` - that is, `foldLeft` and `foldRight` can be implemented in terms of `traverse` by picking the right `Applicative`. However, arrow's `Traverse` does not implement `foldLeft` and `foldRight` as the actual implementation tends to be inefficient.

For brevity and demonstration purposes we’ll implement an isomorphic `foldMap` method in terms of `traverse` by using `arrow.typeclasses.Const`. You can then implement `foldRight` in terms of `foldMap`, and `foldLeft` can then be implemented in terms of `foldRight`, though the resulting implementations may be slow.

```kotlin:ank
import arrow.Kind
import arrow.core.extensions.const.applicative.applicative
import arrow.typeclasses.Const
import arrow.typeclasses.Monoid
import arrow.typeclasses.Traverse
import arrow.typeclasses.fix

fun <F, B, A> Kind<F, A>.foldMap(M: Monoid<B>, TF: Traverse<F>, b: B, f: (A) -> B): B = TF.run {
  M.run {
    traverse(Const.applicative(M)) { a: A -> Const<B, B>(f(a)) }.fix().value()
  }
}
```

### Choose your implementation

The type signature of `Traverse` appears highly abstract, although it's easier if you think about it as executing operations over collections - what `traverse` does as it walks the `Kind<F, A>` depending on the context `F` of the function. Let's see some examples where `F` is taken to be `List`.

```kotlin:ank
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import arrow.core.Either

fun parseIntEither(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

fun parseIntValidated(s: String): ValidatedNel<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
  else NumberFormatException("$s is not a valid integer.").invalidNel()
```
We can now `traverse` structures that contain strings parsing them into integers and accumulating failures with `ValidatedNel`.

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.k
import arrow.core.ValidatedNel
import arrow.core.extensions.either.foldable.isEmpty
import arrow.core.invalidNel
import arrow.core.validNel

fun parseIntEither(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

fun parseIntValidated(s: String): ValidatedNel<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
  else NumberFormatException("$s is not a valid integer.").invalidNel()

fun main() {
  //sampleStart
  val list = listOf("1", "2", "3").k().traverse(Either.applicative(), ::parseIntEither)
  val isLeft = listOf("1", "abc", "3").k().traverse(Either.applicative(), ::parseIntEither).isEmpty()
  //sampleEnd
  println("list= $list")
  println("isLeft= $isLeft")
}
```

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.Nel
import arrow.core.ValidatedNel
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.fix
import arrow.core.invalidNel
import arrow.core.k
import arrow.core.validNel

fun parseIntEither(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))

fun parseIntValidated(s: String): ValidatedNel<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
  else NumberFormatException("$s is not a valid integer.").invalidNel()

fun main() {
  //sampleStart
  val validatedList = listOf("1", "22w", "3", "33s").k()
    .traverse(ValidatedNel.applicative(Nel.semigroup<NumberFormatException>()), ::parseIntValidated).fix()
  //sampleEnd
  println(validatedList)
}
```

Notice that in the `Either` case, should any string fail to parse the entire `traverse` is considered a failure. Moreover, once it hits its first bad parse, it will not attempt to parse any others down the line (similar behavior would be found with using `Option`). Contrast this with `Validated` where even if one bad parse is hit, it will continue trying to parse the others, accumulating any and all errors as it goes. The behavior of `traverse` is closely tied with the `Applicative` behavior of the data type.

Going back to our `Promise` example, we can get an `Applicative` instance for `IO`, by converting `Promise` to `IO` that runs each `Promise` concurrently. Then when we traverse a `List<A>` with an `(A) -> Promise<ForIO, B>`, we can imagine the traversal as a scatter-gather. Each `A` creates a concurrent computation that will produce a `B` (the scatter), and as the `Promise`s complete they will be gathered back into a `List`.

```kotlin:ank:playground
import arrow.core.ListK
import arrow.core.k
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Promise
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.fix
import arrow.unsafe

interface Profile
interface User

data class DummyUser(val name: String) : User
data class DummyProfile(val u: User) : Profile

fun main() {
  //sampleStart
  val promise: IO<Promise<ForIO, Profile>> =
    Promise.uncancelable<ForIO, Profile>(IO.async()).fix()

  fun userInfo(u: User): IO<Profile> =
    promise.flatMap { p -> p.complete(DummyProfile(u)).flatMap { p.get() } }

  fun ListK<User>.processLogin() =
    traverse(IO.applicative()) { userInfo(it) }

  fun program(): IO<Unit> = IO.fx {
    val (list) = listOf(DummyUser("Micheal"), DummyUser("Juan"), DummyUser("T'Challa")).k()
      .processLogin()
    println(list)
  }

  unsafe { runBlocking { program() } }
  //sampleEnd
}
```

Evidently, `Traverse` is not limited to `List` or `Nel`, it provides an abstraction over 'things that can be traversed over', like a Binary tree, a Sequence, or a Stream, hence the name `Traverse`.

### Playing with `Reader`

Another interesting data type we can use is Reader. Recall that a `Reader<D, A>` is a type alias for `Kleisli<ForId, D, A>` which is a wrapper around `(D) -> A`.

If we fix `D` to be some sort of dependency or configuration and `A` as the return Type, we can use the `Reader` applicative in our `traverse`.

```kotlin:ank
import arrow.mtl.Reader

interface Context
interface Topic
interface Result

typealias Job<A> = Reader<Context, A>

fun processTopic(t: Topic): Job<Result> = TODO()
```

We can imagine we have a data pipeline that processes a bunch of data, each piece of data being categorized by a topic. Given a specific topic, we produce a `Job` that processes that topic. (Note that since a `Job` is just a `Reader`/`Kleisli`, one could write many small `Jobs` and compose them together into one `Job` that is used/returned by `processTopic`.)

Corresponding to bunch of topics, a `List<Topic>` if you will. Since Reader has an `Applicative` instance, we can `traverse` over this list with `processTopic`.

```kotlin:ank
import arrow.core.Id
import arrow.core.ForId
import arrow.core.ListK
import arrow.core.extensions.id.applicative.applicative
import arrow.mtl.KleisliPartialOf
import arrow.mtl.extensions.kleisli.applicative.applicative
import arrow.mtl.fix

val JobForContext = Job.applicative<ForId, Context>(Id.applicative())

fun processTopics(topics: ListK<Topic>): Job<ListK<Result>> =
  topics.traverse<KleisliPartialOf<ForId, Context>, Result>(JobForContext) {
    processTopic(it)
  }.fix()
```

Note the nice return type - `Job<List<Result>>`. We now have one aggregate `Job` that when run, will go through each topic and run the topic-specific job, collecting results as it goes. We say "when run" because a `Job` is some function that requires a Context before producing the value we want.

One example of a "context" can be found in the [Spark](http://spark.apache.org/) project. In Spark, information needed to run a Spark job (where the master node is, memory allocated, etc.) resides in a `SparkContext`. Going back to the above example, we can see how one may define topic-specific Spark jobs `(type Job<A> = Reader<SparkContext, A>)` and then run several Spark jobs on a collection of topics via `traverse`. We then get back a `Job<List<Result>>`, which is equivalent to (`SparkContext) -> List<Result>`. When finally passed a `SparkContext`, we can run the job and get our results back.

Moreover, the fact that our aggregate job is not tied to any specific `SparkContext` allows us to pass in a `SparkContext` pointing to a production cluster, or (using the exact same job) pass in a test `SparkContext` that just runs locally across threads. This makes testing our large job nice and easy.

Finally, this encoding ensures that all the jobs for each topic run on the exact same cluster. At no point do we manually pass in or thread a `SparkContext` through - that is taken care for us by the (applicative) behavior of `Reader` and therefore by `traverse`.

### Sequencing

Sometimes you may find yourself with a collection of data, each of which is already in an data type, for instance a `List<Option<A>>`. To make this easier to work with, you want a `Option<List<A>>`. Given `Option` has an `Applicative` instance, we can traverse over the list with the identity function.

```kotlin:ank:playground
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.applicative.map
import arrow.core.fix
import arrow.core.identity
import arrow.core.none
import arrow.core.some
import arrow.core.Option
import arrow.core.extensions.list.traverse.traverse

fun main() {
  //sampleStart
  val optionList: Option<List<Int>> =
    listOf(1.some(), 2.some(), 3.some())
      .traverse(Option.applicative(), ::identity).fix().map { it.fix() }

  val emptyList: Option<List<Int>> =
    listOf(1.some(), none(), 3.some())
      .traverse(Option.applicative(), ::identity).map { it.fix() }
  //sampleEnd
  println("optionList = $optionList")
  println("emptyList = $emptyList")
}
```

`Traverse` provides a convenience method `sequence` that does exactly this.

```kotlin:ank:playground
import arrow.core.Option
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.option.applicative.applicative
import arrow.core.none
import arrow.core.some

fun main() {
  //sampleStart
  val optionList =
    listOf(1.some(), 2.some(), 3.some())
      .sequence(Option.applicative())

  val emptyList =
    listOf(1.some(), none(), 3.some())
      .sequence(Option.applicative())
  //sampleEnd
  println("optionList = $optionList")
  println("emptyList = $emptyList")
}
```

In general this holds:

```kotlin
map(::f).sequence(AP) == traverse(AP, ::f)
```

where AP stands for an `Applicative<G>`, which is in the prior snippet `Applicative<ForOption>`.

### Traversables are Functors

As it turns out every `Traverse` is a lawful `Functor`. By carefully picking the `G` to use in `traverse` we can implement `map`.

First let's look at the two signatures.

```kotlin:ank
import arrow.Kind
import arrow.core.Id
import arrow.core.extensions.id.applicative.applicative
import arrow.core.value
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

interface Traverse<F> : Functor<F>, Foldable<F> {
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
    traverse(Id.applicative()) { Id(f(it)) }.value()
}
```

Both have an `Kind<F, A>` receiver and a similar `f` parameter. `traverse` expects the return type of `f` to be `Kind<G, B>` whereas `map` just wants `B`. Similarly the return type of `traverse` is `Kind<G, Kind<F, B>>` whereas for `map` it's just `Kind<F, B>`. This suggests we need to pick a `G` such that `Kind<G, A>` communicates exactly as much information as `A`. We can conjure one up by simply wrapping an `A` in `arrow.core.Id`.

In order to call `traverse` `Id` needs to be `Applicative` which is straightforward - note that while `Id` just wraps an `A`, it is still a type constructor which matches the shape required by `Applicative`.

```kotlin:ank
import arrow.core.ForId
import arrow.core.Id
import arrow.core.IdOf
import arrow.core.fix
import arrow.typeclasses.Applicative

object IdApplicative : Applicative<ForId> {
  override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): Id<B> =
    fix().ap(ff)

  override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <A> just(a: A): Id<A> =
    Id.just(a)
}
```

We can implement `Traverse#map` by wrapping and unwrapping `Id` as necessary.

### Traversing for effects

Sometimes our effectful functions return a `Unit` value in cases where there is no interesting value to return (e.g. writing to some sort of store).

```kotlin:ank
import arrow.fx.ForIO
import arrow.fx.Promise

interface Data

fun writeToStore(data:Data): Promise<ForIO, Unit> = TODO()
```

If we traverse using this, we end up with a funny type.

```kotlin:ank
import arrow.core.ListK
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix

fun writeManyToStore(data: ListK<Data>): IO<ListK<Unit>> =
  data.traverse(IO.applicative()) { writeToStore(it).get().fix() }.fix()
```

We end up with a `IO<ListK<Unit>>`! A `ListK<Unit>` is not of any use to us, and communicates the same amount of information as a single `Unit` does.

Traversing solely for the sake of the effects (ignoring any values that may be produced, `Unit` or otherwise) is common, so `Foldable` (superclass of `Traverse`) provides `traverse_` and `sequence_` methods that do the same thing as `traverse` and `sequence` but ignores any value produced along the way, returning `Unit` at the end.

```kotlin:ank
import arrow.core.extensions.listk.foldable.traverse_

fun writeManyToStore(data: ListK<Data>): IO<Unit> =
  data.traverse_(IO.applicative()) { writeToStore(it).get() }.fix()
```
```kotlin:ank:playground
import arrow.core.Option
import arrow.core.extensions.list.foldable.sequence_
import arrow.core.extensions.option.applicative.applicative
import arrow.core.none
import arrow.core.some

fun main() {
  //sampleStart
  val someUnit = listOf(1.some(), 2.some(), 3.some())
    .sequence_(Option.applicative())
  val noneUnit = listOf(1.some(), none(), 3.some())
    .sequence_(Option.applicative())
  //sampleEnd
  println("someUnit= $someUnit")
  println("noneUnit= $noneUnit")
}
```

### Theory Wrap-up

Unsurprisingly, `Foldable` and `Traverse` act on multiple elements and reduce them into a single value - in cat theory - `Catamorphisms`.

In contrast, `homomorphisms` such as `Monoid` and `Applicative` preserve their structure, hence `List<Int>` `+` `List<Int>` will yield a `List<Int>` or an `Applicative` instance `List<Int>.map(::toString)` result in a `List<String>`.

We can think of catamorphic operations as :

- the `if-else` expression in Kotlin, which models a `fold` over `Boolean`
- various `fold` methods in Arrow, like in `Option` over `Some` and `None`, `Either` over `Left` and `Right` or `ListK`
- `fold` in common ADTs from Computer Science like in a Binary tree 
- the `reduce` method 

One among many other usages of `Catamorphisms` are in [Recursion Schemes]({{ '/docs/recursion/intro/' | relative_url }}).

### Data types

```kotlin:ank:replace
import arrow.reflect.TypeClass
import arrow.reflect.dtMarkdownList
import arrow.typeclasses.Traverse

TypeClass(Traverse::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Traverse)

## Futher Reading

- [The Essence of the Iterator Pattern](https://www.cs.ox.ac.uk/jeremy.gibbons/publications/iterator.pdf) - Gibbons, Oliveira. JFP, 2009
- [Catamorphisms](https://blog.ploeh.dk/2019/04/29/catamorphisms/) - Mark Seemann, 2019

## Credits

The content is heavily inspired by [Scala exercise](https://www.scala-exercises.org/cats/traverse), from [examples from the Cats Community](https://typelevel.org/cats/typeclasses/traverse.html) and partially adopted from [Daniel Shin's Blog](https://www.danishin.com/article/Foldable_vs_Traverse_In_Scala).
