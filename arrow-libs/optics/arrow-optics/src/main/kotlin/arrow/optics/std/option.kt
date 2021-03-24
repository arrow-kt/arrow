package arrow.optics

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.extensions.option.traverse.traverse
import arrow.core.identity
import arrow.typeclasses.Applicative

/**
 * [PIso] that defines the equality between [Option] and the nullable platform type.
 */
@Deprecated(
  "Use the optionToPNullable function exposed in the Iso' companion object",
  ReplaceWith(
    "Iso.optionToPNullable()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Option.Companion.toPNullable(): PIso<Option<A>, Option<B>, A?, B?> = PIso(
  get = { it.fold({ null }, ::identity) },
  reverseGet = Option.Companion::fromNullable
)

/**
 * [PIso] that defines the isomorphic relationship between [Option] and the nullable platform type.
 */
@Deprecated(
  "Use the optionToNullable function exposed in the Iso' companion object",
  ReplaceWith(
    "Iso.optionToNullable()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option.Companion.toNullable(): Iso<Option<A>, A?> = toPNullable()

/**
 * [PPrism] to focus into an [arrow.core.Some]
 */
@Deprecated(
  "Use the pSome function exposed in the Prism' companion object",
  ReplaceWith(
    "Prism.pSome()",
    "arrow.optics.Prism"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Option.Companion.PSome(): PPrism<Option<A>, Option<B>, A, B> = PPrism(
  getOrModify = { option -> option.fold({ Either.Left(None) }, ::Right) },
  reverseGet = ::Some
)

/**
 * [Prism] to focus into an [arrow.core.Some]
 */
@Deprecated(
  "Use the some function exposed in the Prism' companion object",
  ReplaceWith(
    "Prism.some()",
    "arrow.optics.Prism"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option.Companion.some(): Prism<Option<A>, A> = PSome()

/**
 * [Prism] to focus into an [arrow.core.None]
 */
@Deprecated(
  "Use the none function exposed in the Prism' companion object",
  ReplaceWith(
    "Prism.none()",
    "arrow.optics.Prism"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option.Companion.none(): Prism<Option<A>, Unit> = Prism(
  getOrModify = { option -> option.fold({ Either.Right(Unit) }, { Either.Left(option) }) },
  reverseGet = { _ -> None }
)

/**
 * [Iso] that defines the equality between and [arrow.core.Option] and [arrow.core.Either]
 */
@Deprecated(
  "Use the optionToPEither function exposed in the Iso' companion object",
  ReplaceWith(
    "Iso.optionToPEither()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Option.Companion.toPEither(): PIso<Option<A>, Option<B>, Either<Unit, A>, Either<Unit, B>> = PIso(
  get = { opt -> opt.fold({ Either.Left(Unit) }, ::Right) },
  reverseGet = { either -> either.fold({ None }, ::Some) }
)

/**
 * [Iso] that defines the equality between and [arrow.core.Option] and [arrow.core.Either]
 */
@Deprecated(
  "Use the optionToEither function exposed in the Iso' companion object",
  ReplaceWith(
    "Iso.optionToEither()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option.Companion.toEither(): Iso<Option<A>, Either<Unit, A>> = toPEither()
