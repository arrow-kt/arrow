package kategory

import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
open class MonadFilterContinuation<F, A>(val MF: MonadFilter<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadContinuation<F, A>(MF) {

    object PredicateInterrupted: RuntimeException()

    override fun resumeWithException(exception: Throwable) {
        when (exception) {
            is PredicateInterrupted -> returnedMonad = MF.empty()
            else -> super.resumeWithException(exception)
        }
    }

    /**
     * Short circuits monadic bind if `predicate == false` return the
     * monad `empty` value.
     */
    fun continueIf(predicate: Boolean): Unit {
        if (!predicate) throw PredicateInterrupted
    }

}