package arrow.optics

import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.fix
import arrow.data.*

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
fun <A1, A2, B1, B2> pEitherToValidation(): PIso<EitherOf<A1, B1>, EitherOf<A2, B2>, ValidatedOf<A1, B1>, ValidatedOf<A2, B2>> = PIso(
        get = { it.fix().fold({ Invalid(it) }, { Valid(it) }) },
        reverseGet = { it.fix().toEither() }
)

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
fun <A, B> eitherToValidated(): Iso<EitherOf<A, B>, ValidatedOf<A, B>> = pEitherToValidation()
