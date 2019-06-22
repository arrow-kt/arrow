package arrow.optics

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.identity

/**
 * [PIso] that defines the equality between [Option] and the nullable platform type.
 */
fun <A, B> Option.Companion.toPNullable(): PIso<Option<A>, Option<B>, A?, B?> = PIso(
  get = { it.fold({ null }, ::identity) },
  reverseGet = Option.Companion::fromNullable
)

/**
 * [PIso] that defines the isomorphic relationship between [Option] and the nullable platform type.
 */
fun <A> Option.Companion.toNullable(): Iso<Option<A>, A?> = toPNullable()

/**
 * [PPrism] to focus into an [arrow.core.Some]
 */
fun <A, B> Option.Companion.PSome(): PPrism<Option<A>, Option<B>, A, B> = PPrism(
  getOrModify = { option -> option.fold({ Either.Left(None) }, ::Right) },
  reverseGet = ::Some
)

/**
 * [Prism] to focus into an [arrow.core.Some]
 */
fun <A> Option.Companion.some(): Prism<Option<A>, A> = PSome()

/**
 * [Prism] to focus into an [arrow.core.None]
 */
fun <A> Option.Companion.none(): Prism<Option<A>, Unit> = Prism(
  getOrModify = { option -> option.fold({ Either.Right(Unit) }, { Either.Left(option) }) },
  reverseGet = { _ -> None }
)

/**
 * [Iso] that defines the equality between and [arrow.core.Option] and [arrow.core.Either]
 */
fun <A, B> Option.Companion.toPEither(): PIso<Option<A>, Option<B>, Either<Unit, A>, Either<Unit, B>> = PIso(
  get = { opt -> opt.fold({ Either.Left(Unit) }, ::Right) },
  reverseGet = { either -> either.fold({ None }, ::Some) }
)

/**
 * [Iso] that defines the equality between and [arrow.core.Option] and [arrow.core.Either]
 */
fun <A> Option.Companion.toEither(): Iso<Option<A>, Either<Unit, A>> = toPEither()
