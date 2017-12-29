package arrow.optics

import arrow.*

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
fun <A1, A2, B1, B2> pEitherToValidation(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> = PIso(
        get = { it.fold({ Validated.Invalid(it) }, { Validated.Valid(it) }) },
        reverseGet = { it.toEither() }
)

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
fun <A, B> eitherToValidated(): Iso<Either<A, B>, Validated<A, B>> = pEitherToValidation()
