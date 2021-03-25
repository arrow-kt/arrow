package arrow.optics

import arrow.core.Either
import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated

/**
 * [PIso] that defines the equality between [Either] and [Validated]
 */
@Deprecated(
  "Use the pValidated function exposed in the Iso' companion object",
  ReplaceWith(
    "Iso.eitherToPValidated()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <A1, A2, B1, B2> Either.Companion.toPValidated(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> =
  PIso(
    get = { it.fold(::Invalid, ::Valid) },
    reverseGet = Validated<A2, B2>::toEither
  )

/**
 * [Iso] that defines the equality between [Either] and [Validated]
 */
@Deprecated(
  "Use the validated function exposed in the Iso' companion object",
  ReplaceWith(
    "Iso.validated()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Either.Companion.toValidated(): Iso<Either<A, B>, Validated<A, B>> =
  toPValidated()
