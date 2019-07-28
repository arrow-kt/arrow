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

In functional programming it is very common to encode "effects" as data types - common effects include `Option` for possibly missing values, `Either` and `Validated` for possible errors, and `Future` for asynchronous computations.

These effects tend to show up in functions working on a single piece of data - for instance parsing a single `String` into an `Int`, validating a login, or asynchronously fetching website information for a user.

```kotlin:ank
import arrow.core.Either
import arrow.core.Option
import java.util.concurrent.Future

interface Profile
interface User

fun parseInt(s: String): Option<Int> = TODO()
data class SecurityError(val message: String)
data class Credentials(val email: String, val password: String)

fun validateLogin(c: Credentials): Either<SecurityError, Unit> = TODO()

fun userInfo(u: User): Future<Profile> = TODO()
```

Each function asks only for the data it actually needs; in the case of `userInfo`, a single `User`. We certainly could write one that takes a `List<User>` and fetch profile for all of them, would be a bit strange. If we just wanted to fetch the profile of just one user, we would either have to wrap it in a `List` or write a separate function that takes in a single user anyways. More fundamentally, functional programming is about building lots of small, independent pieces and composing them to make larger and larger pieces - does this hold true in this case?

Given just `(User) -> Future<Profile>`, what should we do if we want to fetch profiles for a `List<User>`? We could try familiar combinators like map.

```kotlin:ank
import arrow.core.Either
import arrow.core.Option
import java.util.concurrent.Future

interface Profile
interface User

fun parseInt(s: String): Option<Int> = TODO()
data class SecurityError(val message: String)
data class Credentials(val email: String, val password: String)

fun validateLogin(c: Credentials): Either<SecurityError, Unit> = TODO()
fun userInfo(u: User): Future<Profile> = TODO()

fun profilesFor(users:  List<User>): List<Future<Profile>> = users.map(::userInfo)
```

Note the return type `List<Future<Profile>>`. This makes sense given the type signatures, but seems unwieldy. We now have a list of asynchronous values, and to work with those values we must then use the combinators on `Future` for every single one. It would be nicer instead if we could get the aggregate result in a single `Future`, say a `Future<List<Profile>>`.

There exists a much more generalized form that would allow us to parse a `List<String>` or validate credentials for a `List<User>`.

Enter `Traverse`.

### The Typeclass
At center stage of Traverse is the traverse method.

```kotlin
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

interface Traverse<F> : Functor<F>, Foldable<F> {
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>
}
```

In our above example, `F` is `List`, and `G` is `Option`, `Either`, or `Future`. For the profile example, traverse says given a `List<User>` and a function `(User) -> Future<Profile>`, it can give you a `Future<List<Profile>>`.

Abstracting away the `G` (still imagining `F` to be `List`), `traverse` says given a collection of data, and a function that takes a piece of data and returns an effectful value, it will traverse the collection, applying the function and aggregating the effectful values (in a `List`) as it goes.

In the most general form, `Kind<F, A>` is some sort of context which may contain a value (or several). While `List` tends to be among the most general cases, there also exist `Traverse` instances for `Option`, `Either`, and `Validated` (among others).

#### Choose your effect

The type signature of `Traverse` appears highly abstract, and indeed it is - what `traverse` does as it walks the `Kind<F, A>` depends on the effect of the function. Let's see some examples where `F` is taken to be `List`.

```kotlin:ank
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.toT
import arrow.core.validNel
import arrow.core.Either

fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))
    
fun parse(s: String): ValidatedNel<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
  else NumberFormatException("$s is not a valid integer.").invalidNel()
```
We can now `traverse` structures that contain strings parsing them into integers and accumulating failures with `Either`.

```kotlin:ank:playground
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.toT
import arrow.core.validNel
import arrow.core.Either

fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))
    
fun parse(s: String): ValidatedNel<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
  else NumberFormatException("$s is not a valid integer.").invalidNel()

fun main() {
  //sampleStart
  val list = listOf("1", "2", "3").k().traverse(Either.applicative(), ::parseIntEither)
  val isLeft = listOf("1", "abc", "3").k().traverse(Either.applicative(), ::parseIntEither).isEmpty()
  //sampleEnd
  println(list)
  println(isLeft)
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

Notice that in the `Either` case, should any string fail to parse the entire `traversal` is considered a failure. Moreover, once it hits its first bad parse, it will not attempt to parse any others down the line (similar behavior would be found with using `Option` as the effect). Contrast this with `Validated` where even if one bad parse is hit, it will continue trying to parse the others, accumulating any and all errors as it goes. The behavior of traversal is closely tied with the `Applicative` behavior of the data type.

Going back to our `Future` example, we can write an `Applicative` instance for `Future` that runs each `Future` concurrently. Then when we traverse a `List<A>` with an `(A) -> Future<B>`, we can imagine the traversal as a scatter-gather. Each `A` creates a concurrent computation that will produce a `B` (the scatter), and as the `Futures` complete they will be gathered back into a `List`.

Evidently, `Traverse` is not limited to `List` or `Nel`, it provides an abstraction over 'things that can be traversed over', like a Binary tree, hence the name `Traverse`.

#### Playing with `Reader`

Another interesting effect we can use is Reader. Recall that a `Reader<D, A>` is a type alias for `Kleisli<ForId, D, A>` which is a wrapper around `(D) -> A`.

If we fix `D` to be some sort of dependency or configuration, we can use the `Reader` applicative in our `traverse`.

```kotlin:ank
import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.applicative.applicative
import arrow.mtl.Reader
import arrow.mtl.extensions.kleisli.applicative.applicative
import arrow.mtl.fix

interface Context
interface Topic
interface Result

typealias Job<A> = Reader<Context, A>

fun processTopic(t: Topic): Job<Result> = TODO()
```

We can imagine we have a data pipeline that processes a bunch of data, each piece of data being categorized by a topic. Given a specific topic, we produce a `Job` that processes that topic. (Note that since a `Job` is just a `Reader`/`Kleisli`, one could write many small `Jobs` and compose them together into one `Job` that is used/returned by `processTopic`.)

Corresponding to our bunches of data are bunches of topics, a `List<Topic>` if you will. Since Reader has an `Applicative` instance, we can `traverse` over this list with `processTopic`.

```kotlin:ank
fun processTopics(topics: ListK<Topic>): Job<ListK<Result>> =
  topics.traverse(Job.applicative(Id.applicative()), ::processTopic).fix()
```

Note the nice return type - `Job<List<Result>>`. We now have one aggregate `Job` that when run, will go through each topic and run the topic-specific job, collecting results as it goes. We say "when run" because a `Job` is some function that requires a Context before producing the value we want.

One example of a "context" can be found in the [Spark](http://spark.apache.org/) project. In Spark, information needed to run a Spark job (where the master node is, memory allocated, etc.) resides in a `SparkContext`. Going back to the above example, we can see how one may define topic-specific Spark jobs `(type Job<A> = Reader<SparkContext, A>)` and then run several Spark jobs on a collection of topics via `traverse`. We then get back a `Job<List<Result>>`, which is equivalent to (`SparkContext) -> List<Result>`. When finally passed a `SparkContext`, we can run the job and get our results back.

Moreover, the fact that our aggregate job is not tied to any specific `SparkContext` allows us to pass in a `SparkContext` pointing to a production cluster, or (using the exact same job) pass in a test `SparkContext` that just runs locally across threads. This makes testing our large job nice and easy.

Finally, this encoding ensures that all the jobs for each topic run on the exact same cluster. At no point do we manually pass in or thread a `SparkContext` through - that is taken care for us by the (applicative) effect of `Reader` and therefore by `traverse`.

### Sequencing

Sometimes you may find yourself with a collection of data, each of which is already in an effect, for instance a `List<Option<A>>`. To make this easier to work with, you want a `Option<List<A>>`. Given `Option` has an `Applicative` instance, we can traverse over the list with the identity function.

```kotlin:ank:playground
fun main() {
  val optionList: Option<List<Int>> =
  listOf(1.some(), 2.some(), 3.some())
    .traverse(Option.applicative(), ::identity).fix().map { it.fix() }
}
```

### Traversing for effect

Sometimes our effectful functions return a Unit value in cases where there is no interesting value to return (e.g. writing to some sort of store).

### Data types

```kotlin:ank:replace
import arrow.reflect.DataType
import arrow.reflect.tcMarkdownList
import arrow.typeclasses.Traverse

TypeClass(Traverse::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Traverse)

## Credits

The content is heavily inspired by [Scala exercise](https://www.scala-exercises.org/cats/traverse) and partially adopted from [examples from the Cats Community](https://typelevel.org/cats/typeclasses/traverse.html).
