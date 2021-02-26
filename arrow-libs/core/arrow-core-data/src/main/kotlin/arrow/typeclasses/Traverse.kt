package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.identity
import arrow.core.Option
import arrow.core.Either
import arrow.core.Validated
import arrow.core.Const
import arrow.core.Nel
import arrow.core.SequenceK
import arrow.core.ValidatedNel
import arrow.typeclasses.internal.Id
import arrow.typeclasses.internal.fix
import arrow.typeclasses.internal.idApplicative

@Deprecated(KindDeprecation)
/**
 * In functional programming it is very common to encode "behaviors" as data types - common behaviors include [Option] for possibly missing values, [Either] and [Validated] for possible errors, and [Ref]({{ '/effects/ref/' | relative_url }}) for asynchronous and concurrent access and modification of its content.
 *
 * These behaviors tend to show up in functions working on a single piece of data - for instance parsing a single [String] into an [Int], validating a login, or asynchronously fetching website information for a user.
 *
 * ```kotlin:ank
 * import arrow.core.Either
 * import arrow.core.Right
 *
 * object Profile
 * object NotFound
 * object User
 *
 * fun userInfo(user: User): Either<NotFound, Profile> =
 *   Right(Profile)
 * ```
 *
 * Each function asks only for the data it needs; in the case of `userInfo`, a single `User`. Indeed, we could write one that takes a `List<User>` and fetches profile for all of them, but it would be a bit strange. If we just wanted to fetch the profile of only one user, we would either have to wrap it in a [List] or write a separate function that takes in a single user, nonetheless. More fundamentally, functional programming is about building lots of small, independent pieces and composing them to make larger and larger pieces - does it hold in this case?
 *
 * Given just `(User) -> Either<NotFound, Profile>`, what should we do if we want to fetch profiles for a `List<User>`? We could try familiar combinators like map.
 *
 * ```kotlin:ank
 * fun profilesFor(users: List<User>): List<Either<NotFound, Profile>> =
 *   users.map(::userInfo)
 * ```
 *
 * Note the return type `List<Either<NotFound, Profile>>`. This makes sense given the type signatures, but seems unwieldy. We now have a list of result values, and to work with those values we must then use the combinators on `Either` for every single one. It would be nicer instead if we could get the aggregate result in a single `Either`, say a `Either<NotFound, List<Profile>>`.
 *
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
 * In our above example, `F` is `List`, and `G` is `Option` or `Either`. For the profile example, traverse says given a `List<User>` and a function `(User) -> Either<NotFound, Profile>`, it can give you a `Either<NotFound, List<Profile>>`.
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
 * where AP stands for an `Applicative<G>`, which is in prior snippets `Applicative<ForOption>` or `Applicative<EitherPartialOf<E>>`.
 *
 * ### Sequencing effects
 *
 * Sometimes our effectful functions return a [Unit] value in cases where there is no interesting value to return (e.g. writing to some sort of store).
 *
 *
 * We end up with a `Either<NotFound, List<Unit>>`! A `List<Unit>` is not of any use to us, and communicates the same amount of information as a single [Unit] does.
 *
 * Traversing solely for the sake of the effects (ignoring any values that may be produced, [Unit] or otherwise) is common, so [Foldable] (superclass of [Traverse]) provides [traverse_] and [sequence_] methods that do the same thing as [traverse] and [sequence] but ignore any value produced along the way, returning [Unit] at the end.
 *
 * ### When to use Traverse over Foldable
 *
 * Even though, [Foldable] and [Traverse] are related, because both 'reduce their values to something', it is not obvious why to consider [Traverse] over [Foldable].
 *
 * Here is one example:
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
 * Notice that in the [Either] case, should any string fail to parse the entire [traverse] is considered a failure. Moreover, once it hits its first bad parse, it will not attempt to parse any others down the line (similar behavior would be found with using `Option`). Contrast this with `Validated` where even if one bad parse is hit, it will continue trying to parse the others, accumulating any and all errors as it goes. The behavior of [traverse] is closely tied with the [Applicative] behavior of the data type, where computations are run in isolation.
 *
 * Continuing with the example, we traverse a `List<A>` with its [traverse] and a function `(A) -> Either<NotFound, B>`, we can imagine the traversal as a scatter-gather. Each `A` creates a concurrent computation that will produce a `B` (the scatter), and as the suspended `Either` operations completes they will be gathered back into a `List`.
 *
 * [Traverse] is not limited to [List] or [Nel], it provides an abstraction over 'things that can be traversed over', like a Binary tree, [SequenceK], or a `Stream`, hence the name [Traverse].
 *
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
   * Given a function which returns a [G] effect, thread this effect through the running of this function on all the
   * values in [F], returning an [F]<[B]> in a [G] context.
   */
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>

  /**
   * Thread all the [G] effects through the [F] structure to invert the structure from [F]<[G]<[A]>> to [G]<[F]<[A]>>.
   */
  fun <G, A> Kind<F, Kind<G, A>>.sequence(AG: Applicative<G>): Kind<G, Kind<F, A>> = traverse(AG, ::identity)

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
    traverse(idApplicative) { Id(f(it)) }.fix().value

  fun <G, A, B> Kind<F, A>.flatTraverse(MF: Monad<F>, AG: Applicative<G>, f: (A) -> Kind<G, Kind<F, B>>): Kind<G, Kind<F, B>> =
    AG.run { traverse(this, f).map { MF.run { it.flatten() } } }
}
