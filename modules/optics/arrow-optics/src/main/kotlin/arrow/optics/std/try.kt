package arrow.optics

import arrow.core.*
import arrow.data.*

/**
 * [PPrism] to focus into an [arrow.Try.Success]
 */
fun <A, B> pTrySuccess(): PPrism<Try<A>, Try<B>, A, B> = PPrism(
  getOrModify = { aTry -> aTry.fix().fold({ Either.Left(Failure<B>(it)) }, { Either.Right(it) }) },
  reverseGet = { b -> Success(b) }
)

/**
 * [Prism] to focus into an [arrow.Option.None]
 */
fun <A> trySuccess(): Prism<Try<A>, A> = pTrySuccess()

/**
 * [Prism] to focus into an [arrow.Try.Failure]
 */
fun <A> tryFailure(): Prism<Try<A>, Throwable> = Prism(
  getOrModify = { aTry -> aTry.fix().fold({ Either.Right(it) }, { Either.Left(Success(it)) }) },
  reverseGet = { throwable -> Failure(throwable) }
)

/**
 * [PIso] that defines the equality between a [Try] and [Either] of [Throwable] and [A]
 */
fun <A, B> pTryToEither(): PIso<Try<A>, Try<B>, Either<Throwable, A>, Either<Throwable, B>> = PIso(
  get = { it.fix().fold({ Either.Left(it) }, { Either.Right(it) }) },
  reverseGet = { it.fix().fold({ Failure(it) }, { Success(it) }) }
)

/**
 * [Iso] that defines the equality between a [Try] and [Either] of [Throwable] and [A]
 */
fun <A> tryToEither(): Iso<Try<A>, Either<Throwable, A>> = pTryToEither()

/**
 * [PIso] that defines the equality between a [Try] and [Validated] of [Throwable] and [A]
 */
fun <A1, A2> pTryToValidated(): PIso<Try<A1>, Try<A2>, Validated<Throwable, A1>, Validated<Throwable, A2>> = PIso(
  get = { it.fix().fold({ Invalid(it) }, { Valid(it) }) },
  reverseGet = { it.fix().fold({ Failure(it) }, { Success(it) }) }
)

/**
 * [Iso] that defines the equality between a [Try] and [Validated] of [Throwable] and [A]
 */
fun <A> tryToValidated(): Iso<Try<A>, Validated<Throwable, A>> = pTryToValidated()
