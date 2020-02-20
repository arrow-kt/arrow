package arrow.optics

import arrow.core.Either
import arrow.core.Failure
import arrow.core.Invalid
import arrow.core.Left
import arrow.core.Right
import arrow.core.Success
import arrow.core.Try
import arrow.core.Valid
import arrow.core.Validated

fun <A, B> Try.Companion.pSuccess(): PPrism<Try<A>, Try<B>, A, B> = PPrism(
  getOrModify = { aTry -> aTry.fold({ Either.Left(Failure(it)) }, ::Right) },
  reverseGet = ::Success
)

/**
 * [Prism] to focus into an [arrow.core.Try.Success]
 */
fun <A> Try.Companion.success(): Prism<Try<A>, A> = pSuccess()

/**
 * [Prism] to focus into an [arrow.core.Try.Failure]
 */
fun <A> Try.Companion.failure(): Prism<Try<A>, Throwable> = Prism(
  getOrModify = { aTry -> aTry.fold(::Right) { Either.Left(aTry) } },
  reverseGet = ::Failure
)

/**
 * [PIso] that defines the equality between a [Try] and [Either] of [Throwable] and [A]
 */
fun <A, B> Try.Companion.toPEither(): PIso<Try<A>, Try<B>, Either<Throwable, A>, Either<Throwable, B>> = PIso(
  get = { it.fold(::Left, ::Right) },
  reverseGet = { it.fold({ Failure(it) }, ::Success) }
)

/**
 * [Iso] that defines the equality between a [Try] and [Either] of [Throwable] and [A]
 */
fun <A> Try.Companion.toEither(): Iso<Try<A>, Either<Throwable, A>> = toPEither()

/**
 * [PIso] that defines the equality between a [Try] and [Validated] of [Throwable] and [A]
 */
fun <A1, A2> Try.Companion.toPValidated(): PIso<Try<A1>, Try<A2>, Validated<Throwable, A1>, Validated<Throwable, A2>> = PIso(
  get = { it.fold(::Invalid, ::Valid) },
  reverseGet = { it.fold({ Failure(it) }, ::Success) }
)

/**
 * [Iso] that defines the equality between a [Try] and [Validated] of [Throwable] and [A]
 */
fun <A> Try.Companion.toValidated(): Iso<Try<A>, Validated<Throwable, A>> = toPValidated()
