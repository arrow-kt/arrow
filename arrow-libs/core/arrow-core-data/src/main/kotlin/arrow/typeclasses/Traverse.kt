package arrow.typeclasses

import arrow.Kind
import arrow.core.Id
import arrow.core.identity
import arrow.core.value
import arrow.typeclasses.internal.IdBimonad
import arrow.core.Option
import arrow.core.Either
import arrow.core.Validated
import arrow.core.Const
import arrow.core.Nel
import arrow.core.SequenceK
import arrow.core.ValidatedNel

/**
 * ank_macro_hierarchy(arrow.typeclasses.Traverse)
 *
 * In functional programming it is very common to encode "behaviors" as data types - common behaviors include [Option] for possibly missing values, [Either] and [Validated] for possible errors, and [Ref]({{ '/effects/ref/' | relative_url }}) for asynchronous and concurrent access and modification of its content.
 *
 * These behaviors tend to show up in functions working on a single piece of data - for instance parsing a single [String] into an [Int], validating a login, or asynchronously fetching website information for a user.
 *
 * ```kotlin
 * import arrow.fx.IO
 *
 * interface Profile
 * interface User
 *
 * fun userInfo(u: User): IO<Profile>
 * ```
 *
 * Each function asks only for the data it needs; in the case of `userInfo`, a single `User`. Indeed, we could write one that takes a `List<User>` and fetches profile for all of them, but it would be a bit strange. If we just wanted to fetch the profile of only one user, we would either have to wrap it in a [List] or write a separate function that takes in a single user, nonetheless. More fundamentally, functional programming is about building lots of small, independent pieces and composing them to make larger and larger pieces - does it hold in this case?
 *
 * Given just `(User) -> IO<Profile>`, what should we do if we want to fetch profiles for a `List<User>`? We could try familiar combinators like map.
 *
 * ```kotlin
 * fun profilesFor(users: List<User>): List<IO<Profile>> =
 *   users.map(::userInfo)
 * ```
 *
 * Note the return type `List<IO<Profile>>`. This makes sense given the type signatures, but seems unwieldy. We now have a list of asynchronous values, and to work with those values we must then use the combinators on `IO` for every single one. It would be nicer instead if we could get the aggregate result in a single `IO`, say a `IO<List<Profile>>`.
 *
 * ### Sequencing
 *
 * Similar to the latter, you may find yourself with a collection of data, each of which is already in an data type, for instance a `List<Option<A>>` or `List<IO<Profile>>`. To make this easier to work with, you want a `Option<List<A>>` or `IO<Nothing, List<Profile>>`. Given `Option` and `IO` have an [Applicative] instance, we can [sequence] the list to reverse the types.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.extensions.list.traverse.sequence
 * import arrow.core.extensions.option.applicative.applicative
 * import arrow.core.none
 * import arrow.core.some
 *
 * fun main() {
 *   //sampleStart
 *   val optionList =
 *     listOf(1.some(), 2.some(), 3.some())
 *       .sequence(Option.applicative())
 *
 *   val emptyList =
 *     listOf(1.some(), none(), 3.some())
 *       .sequence(Option.applicative())
 *   //sampleEnd
 *   println("optionList = $optionList")
 *   println("emptyList = $emptyList")
 * }
 * ```
 *
 * [Traverse] provides [sequence] as a convenience method for `traverse(::identity)`.
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.option.applicative.applicative
 * import arrow.core.extensions.option.applicative.map
 * import arrow.core.fix
 * import arrow.core.identity
 * import arrow.core.none
 * import arrow.core.some
 * import arrow.core.Option
 * import arrow.core.extensions.list.traverse.traverse
 *
 * fun main() {
 *   //sampleStart
 *   val optionList: Option<List<Int>> =
 *     listOf(1.some(), 2.some(), 3.some())
 *       .traverse(Option.applicative(), ::identity).fix().map { it.fix() }
 *
 *   val emptyList: Option<List<Int>> =
 *     listOf(1.some(), none(), 3.some())
 *       .traverse(Option.applicative(), ::identity).map { it.fix() }
 *   //sampleEnd
 *   println("optionList = $optionList")
 *   println("emptyList = $emptyList")
 * }
 * ```
 *
 * Interestingly enough, [traverse] is a much more generalized and powerful form of [sequence] that would allow us to parse a `List<String>` or validate credentials for a `List<User>`.
 *
 * Enter [Traverse].
 *
 * ### The Typeclass
 *
 * At center stage of Traverse is the [traverse] method.
 *
 * ```kotlin
 * import arrow.Kind
 * import arrow.typeclasses.Applicative
 * import arrow.typeclasses.Foldable
 * import arrow.typeclasses.Functor
 *
 * interface Traverse<F> : Functor<F>, Foldable<F> {
 *   fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>
 * }
 * ```
 *
 * In our above example, `F` is `List`, and `G` is `Option` or `IO`. For the profile example, traverse says given a `List<User>` and a function `(User) -> IO<Profile>`, it can give you a `IO<List<Profile>>`.
 *
 * Abstracting away the `G` (still imagining `F` to be `List`), [traverse] says given a collection of data, and a function that takes a piece of data and returns an value `B` wrapped in a container `G`, it will traverse the collection, applying the function and aggregating the values (in a `List`) as it goes.
 *
 * In the most general form, `Kind<F, A>` is some sort of context `F` which may contain a value `A` (or several). While `List` tends to be among the most general cases, there also exist [Traverse] instances for [Option], [Either], and [Validated] (among others).
 *
 * In general this holds:
 *
 * ```kotlin
 * map(::f).sequence(AP) == traverse(AP, ::f)
 * ```
 *
 * where AP stands for an `Applicative<G>`, which is in prior snippets `Applicative<ForOption>` or `Applicative<ForIO>`.
 *
 * ### Sequencing effects
 *
 * Sometimes our effectful functions return a [Unit] value in cases where there is no interesting value to return (e.g. writing to some sort of store).
 *
 * ```kotlin:ank
 * import arrow.fx.IO
 *
 * interface Data
 *
 * fun writeToStore(data: Data): IO<Nothing, Unit> = TODO("")
 * ```
 *
 * If we traverse using this, we end up with a funny type.
 *
 * ```kotlin:ank
 * import arrow.core.ListK
 * import arrow.fx.IO
 * import arrow.fx.extensions.io.applicative.applicative
 * import arrow.fx.fix
 *
 * interface Data
 *
 * fun writeToStore(data: Data): IO<Nothing, Unit> = TODO("")
 * //sampleStart
 * fun writeManyToStore(data: ListK<Data>): IO<Nothing, ListK<Unit>> =
 *   data.traverse(IO.applicative()) { writeToStore(it) }.fix()
 * //sampleEnd
 * ```
 *
 * We end up with a `IO<Nothing, ListK<Unit>>`! A `ListK<Unit>` is not of any use to us, and communicates the same amount of information as a single [Unit] does.
 *
 * Traversing solely for the sake of the effects (ignoring any values that may be produced, [Unit] or otherwise) is common, so [Foldable] (superclass of [Traverse]) provides [traverse_] and [sequence_] methods that do the same thing as [traverse] and [sequence] but ignore any value produced along the way, returning [Unit] at the end.
 *
 * ```kotlin:ank
 * import arrow.core.ListK
 * import arrow.core.extensions.list.foldable.traverse_
 * import arrow.fx.IO
 * import arrow.fx.extensions.io.applicative.applicative
 * import arrow.fx.fix
 *
 * interface Data
 *
 * fun writeToStore(data: Data): IO<Nothing, Unit> = TODO("")
 * //sampleStart
 * fun writeManyToStore(data: ListK<Data>): IO<Nothing, Unit> =
 *   data.traverse_(IO.applicative()) { writeToStore(it) }.fix()
 * //sampleEnd
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.extensions.either.applicative.applicative
 * import arrow.core.extensions.list.foldable.sequence_
 * import arrow.core.left
 * import arrow.core.right
 *
 * fun main() {
 *   //sampleStart
 *   val rightUnit = listOf(1.right(), 2.right(), 3.right())
 *     .sequence_(Either.applicative())
 *   val leftString = listOf(1.right(), "String".left(), 3.right())
 *     .sequence_(Either.applicative())
 *   //sampleEnd
 *   println("someUnit= $rightUnit")
 *   println("noneUnit= $leftString")
 * }
 * ```
 *
 * ### When to use Traverse over Foldable
 *
 * Even though, [Foldable] and [Traverse] are related, because both 'reduce their values to something', it is not obvious why to consider [Traverse] over [Foldable].
 *
 * Here is one example:
 *
 * ```kotlin:ank:playground
 * import arrow.core.MapK
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.extensions.option.applicative.applicative
 * import arrow.core.fix
 * import arrow.core.k
 *
 * fun main(){
 *   //sampleStart
 *   val map: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
 *
 *   val optionMapK: Option<MapK<String, String>> = map.traverse(Option.applicative()) { Some("$it") }.fix()
 *
 *   // val optionMapKBoilered = map.foldLeft(Some(emptyMap())) { acc: Option<MapK<String, String>>, i: Int ->
 *   //   acc.fold({ emptyMap() }, { /*Some logic to retrieve the key of a value, transform it and add it to the accumulated Map*/ })
 *   // }
 *   //sampleEnd
 *   println(optionMapK)
 * }
 * ```
 *
 * Both methodologies try to attain the same thing, but what [Foldable] lacks is that it solely drills down to its `A` here `Int` and does not preserve it's shape `F` - `MapK`. Resulting in a tiresome implementation, where we need to come up with an algorithm to resolve a key of a given value in the `Map` - let alone that this algorithm is not universal over any given `Map`.
 *
 * This is where [Traverse] shines, whenever you care about the Output `B` from `(A) -> B` and the existing shape of `F` you may use [traverse].
 *
 * Additionally, you're able to wrap your context `F` within a `G`. That is one reason, may among others, why [Traverse] is strictly more powerful than [Foldable].
 *
 * ### Traversables are Foldable
 *
 * The [Foldable] type class abstracts over “things that can be folded over” similar to how [Traverse] abstracts over “things that can be traversed.” It turns out [Traverse] is strictly more powerful than [Foldable] - that is, [foldLeft] and [foldRight] can be implemented in terms of [traverse] by picking the right [Applicative]. However, arrow's [Traverse] does not implement [foldLeft] and [foldRight] with [traverse] as the actual implementation tends to be inefficient.
 *
 * For brevity and demonstration purposes we’ll implement an isomorphic [foldMap] method in terms of [traverse] by using [Const]. You can then implement [foldRight] in terms of [foldMap], and [foldLeft] can then be implemented in terms of [foldRight], though the resulting implementations may be slow.
 *
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.core.Const
 * import arrow.core.ListK
 * import arrow.core.extensions.const.applicative.applicative
 * import arrow.core.extensions.listk.traverse.traverse
 * import arrow.core.extensions.monoid
 * import arrow.core.fix
 * import arrow.core.identity
 * import arrow.core.k
 * import arrow.typeclasses.Monoid
 * import arrow.typeclasses.Traverse
 *
 * //sampleStart
 * fun <F, B, A> Kind<F, A>.foldMap(f: (A) -> B, M: Monoid<B>, TF: Traverse<F>): B =
 *   TF.run {
 *     M.run {
 *       traverse(Const.applicative(M)) { a: A -> Const<B, Nothing>(f(a)) }.fix().value()
 *     }
 *   }
 *
 * val sing = listOf("Hello", " from ", "the", " other ", "side!").k().foldMap(::identity, String.monoid(), ListK.traverse())
 * //sampleEnd
 * fun main() {
 *   println("Sing=$sing")
 * }
 * ```
 *
 * ### Choose your implementation
 *
 * The type signature of [Traverse] appears highly abstract, although it's easier if you think about it as executing operations over collections - what [traverse] does as it walks the `Kind<F, A>` depending on the context `F` of the function. Let's see some examples where `F` is taken to be `List`.
 *
 * ```kotlin:ank
 * import arrow.core.ValidatedNel
 * import arrow.core.invalidNel
 * import arrow.core.validNel
 * import arrow.core.Either
 *
 * fun parseIntEither(s: String): Either<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(NumberFormatException("$s is not a valid integer."))
 *
 * fun parseIntValidated(s: String): ValidatedNel<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
 *   else NumberFormatException("$s is not a valid integer.").invalidNel()
 * ```
 *
 * We can now [traverse] structures that contain strings and parse them into integers with either with a `fail-fast` strategy using [Either] or accumulating failures with [ValidatedNel].
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.extensions.either.applicative.applicative
 * import arrow.core.k
 * import arrow.core.ValidatedNel
 * import arrow.core.extensions.either.foldable.isEmpty
 * import arrow.core.invalidNel
 * import arrow.core.validNel
 *
 * fun parseIntEither(s: String): Either<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(NumberFormatException("$s is not a valid integer."))
 *
 * fun parseIntValidated(s: String): ValidatedNel<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
 *   else NumberFormatException("$s is not a valid integer.").invalidNel()
 *
 * fun main() {
 *   //sampleStart
 *   val list = listOf("1", "2", "3").k().traverse(Either.applicative(), ::parseIntEither)
 *   val failFastList = listOf("1", "abc", "3", "4s").k().traverse(Either.applicative(), ::parseIntEither)
 *   //sampleEnd
 *   println("list= $list")
 *   println("failFastList= $failFastList")
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.Nel
 * import arrow.core.ValidatedNel
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 * import arrow.core.extensions.validated.applicative.applicative
 * import arrow.core.fix
 * import arrow.core.invalidNel
 * import arrow.core.k
 * import arrow.core.validNel
 *
 * fun parseIntEither(s: String): Either<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
 *   else Either.Left(NumberFormatException("$s is not a valid integer."))
 *
 * fun parseIntValidated(s: String): ValidatedNel<NumberFormatException, Int> =
 *   if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
 *   else NumberFormatException("$s is not a valid integer.").invalidNel()
 *
 * fun main() {
 *   //sampleStart
 *   val validatedList = listOf("1", "22w", "3", "33s").k()
 *     .traverse(ValidatedNel.applicative(Nel.semigroup<NumberFormatException>()), ::parseIntValidated).fix()
 *   //sampleEnd
 *   println(validatedList)
 * }
 * ```
 *
 * Notice that in the [Either] case, should any string fail to parse the entire [traverse] is considered a failure. Moreover, once it hits its first bad parse, it will not attempt to parse any others down the line (similar behavior would be found with using `Option`). Contrast this with `Validated` where even if one bad parse is hit, it will continue trying to parse the others, accumulating any and all errors as it goes. The behavior of [traverse] is closely tied with the [Applicative] behavior of the data type, where computations are run in isolation.
 *
 * Going back to our `IO` example from the beginning with concurrency in mind, we can get an [Applicative] instance for `IO`, that traverses concurrently, by using `parApplicative`. Contrary to `IO.applicative()`, which runs in sequence.
 *
 * It is worth mentioning that `parApplicative` does not implement `lazyAp` in contrast to `IO.applicative()`, which means `parApplicative` creates all IO's upfront and executes them in parallel. More importantly, `parApplicative` breaks monad-applicative-consistency laws by design. The aforementioned law holds in the case of `IO.applicative()`, where an effect only runs , when the previous one is successful - hence the monadic nature.
 *
 * Continuing with the example, we traverse a `List<A>` with its [Traverse] instance `ListK.traverse()` and a function`(A) -> IO<B>`, we can imagine the traversal as a scatter-gather. Each `A` creates a concurrent computation that will produce a `B` (the scatter), and as the `IO` operations completes they will be gathered back into a `List`.
 *
 * Ultimately, we utilize `parTraverse` that calls traverse with `parApplicative` in an concurrent environment [F] - in the following example `IO`:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.IO
 * import arrow.fx.extensions.fx
 * import arrow.fx.extensions.io.concurrent.parTraverse
 * import arrow.fx.unsafeRunSync
 *
 * interface Profile
 * interface User
 * //sampleStart
 * data class DummyUser(val name: String) : User
 * data class DummyProfile(val u: User) : Profile
 *
 * fun userInfo(u: User): IO<Nothing, Profile> =
 *   IO { DummyProfile(u) } // this can be a call to the DB
 *
 * fun List<User>.processLogin(): IO<Nothing, List<Profile>> =
 *   parTraverse { userInfo(it) }
 *
 * fun program(): IO<Nothing, Unit> = IO.fx<Nothing, Unit> {
 *   val list = listOf(DummyUser("Micheal"), DummyUser("Juan"), DummyUser("T'Challa"))
 *     .processLogin().bind()
 *   println(list)
 * }
 * //sampleEnd
 * fun main() {
 *   program().unsafeRunSync()
 * }
 * ```
 *
 * [Traverse] is not limited to [List] or [Nel], it provides an abstraction over 'things that can be traversed over', like a Binary tree, [SequenceK], or a `Stream`, hence the name [Traverse].
 *
 * #### Playing with `Reader`
 *
 * Another interesting data type we can use is `Reader`. Recall that a `Reader<D, A>` is a type alias for `Kleisli<ForId, D, A>` which is a wrapper around `(D) -> Id<A>`.
 *
 * In other words all three aforementioned representations are isomorphic to `D -> A` - (Note: In regards to the category of Kotlin, where the morphisms are functions one might consider this signature `D -> A` rather than wrapper Data types like `Kleisli`)
 *
 * If we fix `D` to be some sort of dependency or configuration and `A` as the return type, we can use the `Reader` applicative in our [traverse].
 *
 * ```kotlin:ank
 * import arrow.mtl.Reader
 *
 * interface Context
 * interface Topic
 * interface Result
 *
 * typealias Job<A> = Reader<Context, A>
 *
 * fun processTopic(t: Topic): Job<Result> = TODO()
 * ```
 *
 * We can imagine we have a data pipeline that processes a bunch of data, each piece of data being categorized by a topic. Given a specific topic, we produce a `Job` that processes that topic. (Note that since a `Job` is just a `Reader`/`Kleisli`, one could write many small `Jobs` and compose them together into one `Job` that is used/returned by `processTopic`.)
 *
 * Corresponding to bunch of topics, a `List<Topic>` if you will. Since `Reader` has an [Applicative] instance, we can [traverse] over this list.
 *
 * ```kotlin:ank
 * import arrow.core.Id
 * import arrow.core.ForId
 * import arrow.core.ListK
 * import arrow.core.extensions.id.applicative.applicative
 * import arrow.mtl.KleisliPartialOf
 * import arrow.mtl.extensions.kleisli.applicative.applicative
 * import arrow.mtl.fix
 *
 * val JobForContext = Job.applicative<Context, ForId>(Id.applicative())
 *
 * fun processTopics(topics: ListK<Topic>): Job<ListK<Result>> =
 *   topics.traverse<KleisliPartialOf<Context, ForId>, Result>(JobForContext) {
 *     processTopic(it)
 *   }.fix()
 * ```
 *
 * Note the nice return type - `Job<List<Result>>`. We now have one aggregate `Job` that, when run, will go through each topic and run the topic-specific job, collecting results as it goes. We say "when run" because a `Job` is some function that requires a Context before producing the value we want.
 *
 * One example of a "context" can be found in the [Spark](http://spark.apache.org/) project or in [Android's Context](https://developer.android.com/reference/android/content/Context). In Spark, information needed to run a Spark job (where the master node is, memory allocated, etc.) resides in a `SparkContext`. Going back to the above example, we can see how one may define topic-specific Spark jobs `(type Job<A> = Reader<SparkContext, A>)` and then run several Spark jobs on a collection of topics via [traverse]. We then get back a `Job<List<Result>>`, which is equivalent to (`SparkContext) -> List<Result>`. When finally passed a `SparkContext`, we can run the job and get our results back.
 *
 * Moreover, the fact that our aggregate job is not tied to any specific `SparkContext` allows us to pass in a `SparkContext` pointing to a production cluster, or (using the exact same job) pass in a test `SparkContext` that just runs locally across threads. This makes testing our large job nice and easy.
 *
 * Finally, this encoding ensures that all the jobs for each topic run on the exact same cluster. At no point do we manually pass in or thread a `SparkContext` through - that is taken care for us by the (applicative) behavior of `Reader` and therefore by [traverse].
 *
 * ### Traversables are Functors
 *
 * As it turns out every [Traverse] is a lawful [Functor]. By carefully picking the `G` to use in [traverse] we can implement `map`.
 *
 * First let's look at the two signatures.
 *
 * ```kotlin:ank
 * import arrow.Kind
 * import arrow.core.Id
 * import arrow.core.extensions.id.applicative.applicative
 * import arrow.core.value
 * import arrow.typeclasses.Applicative
 * import arrow.typeclasses.Foldable
 * import arrow.typeclasses.Functor
 *
 * interface Traverse<F> : Functor<F>, Foldable<F> {
 *   fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>
 *
 *   override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
 *     traverse(Id.applicative()) { Id(f(it)) }.value()
 * }
 * ```
 *
 * Both have a `Kind<F, A>` receiver and a similar `f` parameter. [traverse] expects the return type of `f` to be `Kind<G, B>` whereas `map` just wants `B`. Similarly the return type of [traverse] is `Kind<G, Kind<F, B>>` whereas for `map` it's just `Kind<F, B>`. This suggests we need to pick a `G` such that `Kind<G, A>` communicates exactly as much information as `A`. We can conjure one up by simply wrapping an `A` in `arrow.core.Id`.
 *
 * In order to call [traverse] [Id] needs to be [Applicative] which is straightforward - note that while [Id] just wraps an `A`, it is still a type constructor which matches the shape required by [Applicative].
 *
 * ```kotlin:ank
 * import arrow.core.ForId
 * import arrow.core.Id
 * import arrow.core.IdOf
 * import arrow.core.extensions.id.comonad.extract
 * import arrow.typeclasses.Applicative
 *
 * interface IdApplicative : Applicative<ForId> {
 *   override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): Id<B> =
 *     Id(ff.extract().invoke(extract()))
 *
 *   override fun <A> just(a: A): Id<A> =
 *     Id(a)
 * }
 * ```
 *
 * We can implement [map] by wrapping and unwrapping [Id] as necessary.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Id
 * import arrow.core.MapK
 * import arrow.core.extensions.id.applicative.applicative
 * import arrow.core.fix
 * import arrow.core.k
 * import arrow.core.value
 * //sampleStart
 * val map: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
 *
 * val result = map.traverse(Id.applicative()) { Id("$it") }.fix().value()
 * //sampleEnd
 * fun main() {
 *   println(result)
 * }
 * ```
 *
 * ### Theory Wrap-up
 *
 * [Foldable] and [Traverse] act on multiple elements and reduce them into a single value - in category theory - `Catamorphisms`.
 *
 * In contrast, `homomorphisms` such as [Monoid] and [Applicative] preserve their structure, hence adding two values of type `List<Int>` will yield a `List<Int>` or an [Applicative] example, where `List<Int>.ap(listOf(Int::inc))` results in a `List<Int>`.
 *
 * We can think of catamorphic operations as:
 *
 * - the `if-else` expression in Kotlin, which models a `fold` over `Boolean`
 * - various `fold` methods in Arrow, like in `Option` over `Some` and `None`, `Either` over `Left` and `Right` or `ListK`
 * - `fold` in common ADTs from Computer Science like in a Binary tree
 * - the `reduce` method
 *
 * One among many other usages of `Catamorphisms` are in [Recursion Schemes]({{ '/recursion/intro/' | relative_url }}).
 *
 * ### Data types
 *
 * ```kotlin:ank:replace
 * import arrow.reflect.TypeClass
 * import arrow.reflect.dtMarkdownList
 * import arrow.typeclasses.Traverse
 *
 * TypeClass(Traverse::class).dtMarkdownList()
 * ```
 *
 * ## Futher Reading
 *
 * - [The Essence of the Iterator Pattern](https://www.cs.ox.ac.uk/jeremy.gibbons/publications/iterator.pdf) - Gibbons, Oliveira. JFP, 2009
 * - [Catamorphisms](https://blog.ploeh.dk/2019/04/29/catamorphisms/) - Mark Seemann, 2019
 * - [Catamorphisms in 15 minutes](http://chrislambda.github.io/blog/2014/01/30/catamorphisms-in-15-minutes/) - Chris Jones, 2014
 *
 * ## Credits
 *
 * The content is heavily inspired by [Scala exercise](https://www.scala-exercises.org/cats/traverse), from [examples from the Cats Community](https://typelevel.org/cats/typeclasses/traverse.html) and partially adopted from [Daniel Shin's Blog](https://www.danishin.com/article/Foldable_vs_Traverse_In_Scala).
 */
interface Traverse<F> : Functor<F>, Foldable<F> {

  /**
   * Given a function which returns a G effect, thread this effect through the running of this function on all the
   * values in F, returning an F<B> in a G context.
   */
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>

  /**
   * Thread all the G effects through the F structure to invert the structure from F<G<A>> to G<F<A>>.
   */
  fun <G, A> Kind<F, Kind<G, A>>.sequence(AG: Applicative<G>): Kind<G, Kind<F, A>> = traverse(AG, ::identity)

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
    traverse(IdBimonad) { Id(f(it)) }.value()

  fun <G, A, B> Kind<F, A>.flatTraverse(MF: Monad<F>, AG: Applicative<G>, f: (A) -> Kind<G, Kind<F, B>>): Kind<G, Kind<F, B>> =
    AG.run { traverse(this, f).map { MF.run { it.flatten() } } }
}
