package arrow.optics

import arrow.core.Either
import arrow.core.fix
import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import arrow.data.fix

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
fun <A1, A2, B1, B2> pEitherToValidation(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> = PIso(
  get = { it.fix().fold({ Invalid(it) }, { Valid(it) }) },
  reverseGet = { it.fix().toEither() }
)

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
fun <A, B> eitherToValidated(): Iso<Either<A, B>, Validated<A, B>> = pEitherToValidation()
