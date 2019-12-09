package arrow.optics

import arrow.core.Either
import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
fun <A1, A2, B1, B2> Either.Companion.toPValidated(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> = PIso(
  get = { it.fold(::Invalid, ::Valid) },
  reverseGet = Validated<A2, B2>::toEither
)

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
fun <A, B> Either.Companion.toValidated(): Iso<Either<A, B>, Validated<A, B>> = toPValidated()
