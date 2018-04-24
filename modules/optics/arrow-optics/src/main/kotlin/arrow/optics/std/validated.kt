package arrow.optics

import arrow.core.*
import arrow.data.Validated
import arrow.data.fix

/**
 * [PIso] that defines equality between [Validated] and [Either]
 */
fun <A1, A2, B1, B2> pValidatedToEither(): PIso<Validated<A1, B1>, Validated<A2, B2>, Either<A1, B1>, Either<A2, B2>> = PIso(
  get = { it.fix().toEither() },
  reverseGet = { Validated.fromEither(it.fix()) }
)

/**
 * [Iso] that defines equality between [Validated] and [Either]
 */
fun <A, B> validatedToEither(): Iso<Validated<A, B>, Either<A, B>> = pValidatedToEither()

/**
 * [PIso] that defines equality between [Validated] and [Try]
 */
fun <A, B> pValidatedToTry(): PIso<Validated<Throwable, A>, Validated<Throwable, B>, Try<A>, Try<B>> = PIso(
  get = { it.fix().fold({ Failure(it) }, { Success(it) }) },
  reverseGet = { Validated.fromTry(it.fix()) }
)

/**
 * [Iso] that defines equality between [Validated] and [Try]
 */
fun <A> validatedToTry(): Iso<Validated<Throwable, A>, Try<A>> = pValidatedToTry()
