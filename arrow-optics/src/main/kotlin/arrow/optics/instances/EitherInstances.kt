package arrow.optics

import arrow.core.Either
import arrow.data.Validated
import arrow.syntax.validated.invalid
import arrow.syntax.validated.valid

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
fun <A1, A2, B1, B2> pEitherToValidation(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> = PIso(
        get = { it.fold({ it.invalid() }, { it.valid() }) },
        reverseGet = { it.toEither() }
)

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
fun <A, B> eitherToValidated(): Iso<Either<A, B>, Validated<A, B>> = pEitherToValidation()
