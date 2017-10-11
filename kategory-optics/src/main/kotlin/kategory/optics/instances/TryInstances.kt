package kategory.optics

import kategory.Try
import kategory.left
import kategory.right

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