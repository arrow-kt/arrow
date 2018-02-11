package arrow.typeclasses.continuations

import arrow.Kind
import arrow.typeclasses.Awaitable
import arrow.typeclasses.MonadError
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
open class MonadErrorBlockingContinuation<F, E, A>(val ME: MonadError<F, E>, latch: Awaitable<Kind<F, A>>, override val context: CoroutineContext, private val convertError: (Throwable) -> E) :
        MonadBlockingContinuation<F, A>(ME, latch, context), MonadError<F, E> by ME {

    override fun returnedMonad(): Kind<F, A> =
            awaitBlocking().fold({ raiseError(convertError(it)) }, { result -> flatMap(returnedMonad, { result }) })
}