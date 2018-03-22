package arrow.optics

import arrow.core.*
import arrow.data.*

/**
 * [PPrism] to focus into an [arrow.Try.Success]
 */
fun <A, B> pTrySuccess(): PPrism<TryOf<A>, TryOf<B>, A, B> = PPrism(
        getOrModify = { aTry -> aTry.fix().fold({ Either.Left(Failure<B>(it)) }, { Either.Right(it) }) },
        reverseGet = { b -> Success(b) }
)

/**
 * [Prism] to focus into an [arrow.Option.None]
 */
fun <A> trySuccess(): Prism<TryOf<A>, A> = pTrySuccess()

/**
 * [Prism] to focus into an [arrow.Try.Failure]
 */
fun <A> tryFailure(): Prism<TryOf<A>, Throwable> = Prism(
        getOrModify = { aTry -> aTry.fix().fold({ Either.Right(it) }, { Either.Left(Success(it)) }) },
        reverseGet = { throwable -> Failure(throwable) }
)

/**
 * [PIso] that defines the equality between a [Try] and [Either] of [Throwable] and [A]
 */
fun <A, B> pTryToEither(): PIso<TryOf<A>, TryOf<B>, EitherOf<Throwable, A>, EitherOf<Throwable, B>> = PIso(
        get = { it.fix().fold({ Either.Left(it) }, { Either.Right(it) }) },
        reverseGet = { it.fix().fold({ Failure(it) }, { Success(it) }) }
)

/**
 * [Iso] that defines the equality between a [Try] and [Either] of [Throwable] and [A]
 */
fun <A> tryToEither(): Iso<TryOf<A>, EitherOf<Throwable, A>> = pTryToEither()

/**
 * [PIso] that defines the equality between a [Try] and [Validated] of [Throwable] and [A]
 */
fun <A1, A2> pTryToValidated(): PIso<TryOf<A1>, TryOf<A2>, ValidatedOf<Throwable, A1>, ValidatedOf<Throwable, A2>> = PIso(
        get = { it.fix().fold({ Invalid(it) }, { Valid(it) }) },
        reverseGet = { it.fix().fold({ Failure(it) }, { Success(it) }) }
)

/**
 * [Iso] that defines the equality between a [Try] and [Validated] of [Throwable] and [A]
 */
fun <A> tryToValidated(): Iso<TryOf<A>, ValidatedOf<Throwable, A>> = pTryToValidated()
