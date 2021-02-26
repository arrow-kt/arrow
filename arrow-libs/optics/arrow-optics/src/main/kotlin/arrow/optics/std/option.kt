package arrow.optics

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.identity
import arrow.typeclasses.Monoid

/**
 * [PIso] that defines the equality between the nullable platform type and [Option].
 */
fun <A, B> PIso.Companion.pNullableToOption(): PIso<A?, B?, Option<A>, Option<B>> =
  PIso(
    get = Option.Companion::fromNullable,
    reverseGet = { it.fold({ null }, ::identity) }
  )

fun <A, B> PIso.Companion.nullableToOption(): PIso<A?, B?, Option<A>, Option<B>> =
  pNullableToOption()

/**
 * [PIso] that defines the equality between [Option] and the nullable platform type.
 */
fun <A, B> PIso.Companion.pOptionToNullable(): PIso<Option<A>, Option<B>, A?, B?> =
  PIso(
    get = { it.fold({ null }, ::identity) },
    reverseGet = Option.Companion::fromNullable
  )

/**
 * [PIso] that defines the isomorphic relationship between [Option] and the nullable platform type.
 */
fun <A> PIso.Companion.optionToNullable(): Iso<Option<A>, A?> =
  pOptionToNullable()

/**
 * [PPrism] to focus into an [arrow.core.Some]
 */
fun <A, B> PPrism.Companion.pOption(): PPrism<Option<A>, Option<B>, A, B> =
  PPrism(
    getOrModify = { option -> option.fold({ Either.Left(None) }, ::Right) },
    reverseGet = ::Some
  )

/**
 * [Prism] to focus into an [arrow.core.Some]
 */
fun <A> PPrism.Companion.option(): Prism<Option<A>, A> =
  pOption()

/**
 * [Prism] to focus into an [arrow.core.None]
 */
fun <A> PPrism.Companion.none(): Prism<Option<A>, Unit> =
  Prism(
    getOrModify = { option -> option.fold({ Either.Right(Unit) }, { Either.Left(option) }) },
    reverseGet = { _ -> None }
  )

/**
 * [Iso] that defines the equality between and [arrow.core.Option] and [arrow.core.Either]
 */
fun <A, B> PIso.Companion.pOptionToEither(): PIso<Option<A>, Option<B>, Either<Unit, A>, Either<Unit, B>> =
  PIso(
    get = { opt -> opt.fold({ Either.Left(Unit) }, ::Right) },
    reverseGet = { either -> either.fold({ None }, ::Some) }
  )

/**
 * [Iso] that defines the equality between and [arrow.core.Option] and [arrow.core.Either]
 */
fun <A> PIso.Companion.optionToEither(): Iso<Option<A>, Either<Unit, A>> =
  pOptionToEither()

/**
 * [Traversal] for [Option] that has focus in each [arrow.core.Some].
 *
 * @receiver [PTraversal.Companion] to make it statically available.
 * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
 */
fun <A> PTraversal.Companion.option(): Traversal<Option<A>, A> =
  Traversal { s, f -> s.map(f) }

fun <A> Fold.Companion.option(): Fold<Option<A>, A> =
  object : Fold<Option<A>, A> {
    override fun <R> foldMap(M: Monoid<R>, s: Option<A>, map: (A) -> R): R =
      M.run { s.foldLeft(empty()) { b, a -> b.combine(map(a)) } }
  }

fun <A> PEvery.Companion.option(): Every<Option<A>, A> =
  object : Every<Option<A>, A> {
    override fun <R> foldMap(M: Monoid<R>, s: Option<A>, map: (A) -> R): R =
      M.run { s.foldLeft(empty()) { b, a -> b.combine(map(a)) } }

    override fun modify(s: Option<A>, map: (focus: A) -> A): Option<A> = s.map(map)
  }
