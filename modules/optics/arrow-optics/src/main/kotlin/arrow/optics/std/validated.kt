package arrow.optics

import arrow.core.Either
import arrow.core.Failure
import arrow.core.Success
import arrow.core.Try
import arrow.core.Validated

/**
 * [PIso] that defines equality between [Validated] and [Either]
 */
fun <A1, A2, B1, B2> Validated.Companion.toPEither(): PIso<Validated<A1, B1>, Validated<A2, B2>, Either<A1, B1>, Either<A2, B2>> = PIso(
  get = Validated<A1, B1>::toEither,
  reverseGet = Validated.Companion::fromEither
)

/**
 * [Iso] that defines equality between [Validated] and [Either]
 */
fun <A, B> Validated.Companion.toEither(): Iso<Validated<A, B>, Either<A, B>> = toPEither()

/**
 * [PIso] that defines equality between [Validated] and [Try]
 */
fun <A, B> Validated.Companion.toPTry(): PIso<Validated<Throwable, A>, Validated<Throwable, B>, Try<A>, Try<B>> = PIso(
  get = { it.fold({ Failure(it) }, ::Success) },
  reverseGet = Validated.Companion::fromTry
)

/**
 * [Iso] that defines equality between [Validated] and [Try]
 */
fun <A> Validated.Companion.toTry(): Iso<Validated<Throwable, A>, Try<A>> = toPTry()
