package arrow.optics

import arrow.core.*

fun <A, B> Option.Companion.toPNullable(): PIso<Option<A>, Option<B>, A?, B?> = PIso(
  get = { it.fold({ null }, ::identity) },
  reverseGet = Option.Companion::fromNullable
)

fun <A> Option.Companion.toNullable(): Iso<Option<A>, A?> = toPNullable()

/**
 * [PPrism] to focus into an [arrow.Some]
 */
fun <A, B> Option.Companion.asPSome(): PPrism<Option<A>, Option<B>, A, B> = PPrism(
  getOrModify = { option -> option.fix().fold({ Either.Left(None) }, { a -> Either.Right(a) }) },
  reverseGet = { b -> Some(b) }
)

/**
 * [Prism] to focus into an [arrow.Some]
 */
fun <A> Option.Companion.asSome(): Prism<Option<A>, A> = asPSome()

/**
 * [Prism] to focus into an [arrow.None]
 */
fun <A> Option.Companion.asNone(): Prism<Option<A>, Unit> = Prism(
  getOrModify = { option -> option.fix().fold({ Either.Right(Unit) }, { Either.Left(Some(it)) }) },
  reverseGet = { _ -> None }
)

/**
 * [Iso] that defines the equality between and [arrow.Option] and [arrow.Either]
 */
fun <A, B> Option.Companion.toPEither(): PIso<Option<A>, Option<B>, Either<Unit, A>, Either<Unit, B>> = PIso(
  get = { opt -> opt.fix().fold({ Either.Left(Unit) }, { a -> Either.Right(a) }) },
  reverseGet = { either -> either.fix().fold({ None }, { b -> Some(b) }) }
)

/**
 * [Iso] that defines the equality between and [arrow.Option] and [arrow.Either]
 */
fun <A> Option.Companion.toEither(): Iso<Option<A>, Either<Unit, A>> = toPEither()
