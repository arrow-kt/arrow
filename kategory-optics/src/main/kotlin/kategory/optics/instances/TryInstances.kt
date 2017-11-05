package kategory.optics

import kategory.Either
import kategory.Try
import kategory.Validated
import kategory.invalid
import kategory.left
import kategory.right
import kategory.valid

/**
 * [PPrism] to focus into an [kategory.Try.Success]
 */
fun <A, B> pTrySuccess(): PPrism<Try<A>, Try<B>, A, B> = PPrism(
        getOrModify = { aTry -> aTry.fold({ Try.Failure<B>(it).left() }, { it.right() }) },
        reverseGet = { b -> Try.Success(b) }
)

/**
 * [Prism] to focus into an [kategory.Option.None]
 */
fun <A> trySuccess(): Prism<Try<A>, A> = pTrySuccess()

/**
 * [Prism] to focus into an [kategory.Try.Failure]
 */
fun <A> tryFailure(): Prism<Try<A>, Throwable> = Prism(
        getOrModify = { aTry -> aTry.fold({ it.right() }, { Try.Success(it).left() }) },
        reverseGet = { throwable -> Try.Failure(throwable) }
)

/**
 * [PIso] that defines the equality between a [Try] and [Either] of [Throwable] and [A]
 */
fun <A, B> pTryToEither(): PIso<Try<A>, Try<B>, Either<Throwable, A>, Either<Throwable, B>> = PIso(
        get = { it.fold({ it.left() }, { it.right() }) },
        reverseGet = { it.fold({ Try.Failure(it) }, { Try.Success(it) }) }
)

/**
 * [Iso] that defines the equality between a [Try] and [Either] of [Throwable] and [A]
 */
fun <A> tryToEither(): Iso<Try<A>, Either<Throwable, A>> = pTryToEither()

/**
 * [PIso] that defines the equality between a [Try] and [Validated] of [Throwable] and [A]
 */
fun <A1, A2> pTryToValidated(): PIso<Try<A1>, Try<A2>, Validated<Throwable, A1>, Validated<Throwable, A2>> = PIso(
        get = { it.fold({ it.invalid() }, { it.valid() }) },
        reverseGet = { it.fold({ Try.Failure(it) }, { Try.Success(it) }) }
)

/**
 * [Iso] that defines the equality between a [Try] and [Validated] of [Throwable] and [A]
 */
fun <A> tryToValidated(): Iso<Try<A>, Validated<Throwable, A>> = pTryToValidated()