package katz.typeclasses

import java.io.Serializable
import kotlin.coroutines.experimental.RestrictsSuspension

interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F> {
    fun <A> ensure(fa: HK<F, A>, error: () -> E, predicate: (A) -> Boolean): HK<F, A> =
            flatMap(fa, {
                if (predicate(it)) pure(it)
                else raiseError(error())
            })

}

@RestrictsSuspension
class MonadErrorContinuation<F, A>(val ME : MonadError<F, Throwable>) : Serializable, MonadContinuation<F, A>(ME) {
    override fun resumeWithException(exception: Throwable) {
        returnedMonad = ME.raiseError(exception)
    }
}