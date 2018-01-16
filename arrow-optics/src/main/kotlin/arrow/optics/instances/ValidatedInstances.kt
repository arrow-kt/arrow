package arrow.optics.instances

import arrow.core.*
import arrow.data.*
import arrow.data.Failure
import arrow.data.Success
import arrow.optics.Iso
import arrow.optics.PIso

/**
 * [PIso] that defines equality between [Validated] and [Either]
 */
fun <A1, A2, B1, B2> pValidatedToEither(): PIso<Validated<A1, B1>, Validated<A2, B2>, Either<A1, B1>, Either<A2, B2>> = PIso(
        get = { it.toEither() },
        reverseGet = { Validated.fromEither(it) }
)

/**
 * [Iso] that defines equality between [Validated] and [Either]
 */
fun <A, B> validatedToEither(): Iso<Validated<A, B>, Either<A, B>> = pValidatedToEither()

/**
 * [PIso] that defines equality between [Validated] and [Try]
 */
fun <A, B> pValidatedToTry(): PIso<Validated<Throwable, A>, Validated<Throwable, B>, Try<A>, Try<B>> = PIso(
        get = { it.fold({ Failure(it) }, { Success(it) }) },
        reverseGet = { Validated.fromTry(it) }
)

/**
 * [Iso] that defines equality between [Validated] and [Try]
 */
fun <A> validatedToTry(): Iso<Validated<Throwable, A>, Try<A>> = pValidatedToTry()