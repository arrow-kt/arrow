package arrow.mtl.continuations

import arrow.Kind
import arrow.mtl.MonadFilter
import arrow.typeclasses.Awaitable
import arrow.typeclasses.continuations.MonadBlockingContinuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
open class MonadFilterContinuation<F, A>(MF: MonadFilter<F>, latch: Awaitable<Kind<F, A>>, override val context: CoroutineContext) :
        MonadBlockingContinuation<F, A>(MF, latch, context), MonadFilter<F> by MF, BindingFilterContinuation<F, A> {
    /**
     * marker exception that interrupts the coroutine flow and gets captured
     * to provide the monad empty value
     */
    private object PredicateInterrupted : RuntimeException()

    override fun resumeWithException(exception: Throwable) {
        when (exception) {
            PredicateInterrupted -> returnedMonad = empty()
            else -> super.resumeWithException(exception)
        }
    }

    override fun continueIf(predicate: Boolean) {
        if (!predicate) throw MonadFilterContinuation.PredicateInterrupted
    }

    override suspend fun <B> Kind<F, B>.bindWithFilter(f: (B) -> Boolean): B {
        val b: B = bind { this }
        return if (f(b)) b else bind { empty<B>() }
    }

}