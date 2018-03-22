package arrow.optics

import arrow.core.*
import arrow.data.Validated
import arrow.data.ValidatedOf
import arrow.data.fix

/**
 * [PIso] that defines equality between [Validated] and [Either]
 */
fun <A1, A2, B1, B2> pValidatedToEither(): PIso<ValidatedOf<A1, B1>, ValidatedOf<A2, B2>, EitherOf<A1, B1>, EitherOf<A2, B2>> = PIso(
        get = { it.fix().toEither() },
        reverseGet = { Validated.fromEither(it.fix()) }
)

/**
 * [Iso] that defines equality between [Validated] and [Either]
 */
fun <A, B> validatedToEither(): Iso<ValidatedOf<A, B>, EitherOf<A, B>> = pValidatedToEither()

/**
 * [PIso] that defines equality between [Validated] and [Try]
 */
fun <A, B> pValidatedToTry(): PIso<ValidatedOf<Throwable, A>, ValidatedOf<Throwable, B>, TryOf<A>, TryOf<B>> = PIso(
        get = { it.fix().fold({ Failure(it) }, { Success(it) }) },
        reverseGet = { Validated.fromTry(it.fix()) }
)

/**
 * [Iso] that defines equality between [Validated] and [Try]
 */
fun <A> validatedToTry(): Iso<ValidatedOf<Throwable, A>, TryOf<A>> = pValidatedToTry()
