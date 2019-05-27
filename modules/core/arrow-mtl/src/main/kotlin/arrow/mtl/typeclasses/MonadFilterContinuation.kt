package arrow.mtl.typeclasses

import arrow.Kind
import arrow.typeclasses.MonadContinuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

/**
 * marker exception that interrupts the coroutine flow and gets captured
 * to provide the monad empty value
 */
private object PredicateInterrupted : RuntimeException() {
  override fun fillInStackTrace(): Throwable = this
}

@RestrictsSuspension
open class MonadFilterContinuation<F, A>(val MF: MonadFilter<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(MF) {

  override fun resumeWith(result: Result<Kind<F, A>>) {
    result.fold({ super.resumeWith(result) }, {
      when (it) {
        is PredicateInterrupted -> returnedMonad = MF.empty()
        else -> super.resumeWith(result)
      }
    })
  }

  /**
   * Short circuits monadic bind if `predicate == false` return the
   * monad `empty` value.
   */
  fun continueIf(predicate: Boolean) {
    if (!predicate) throw PredicateInterrupted
  }

  /**
   * Binds only if the given predicate matches the inner value otherwise binds into the Monad `empty()` value
   * on `MonadFilter` instances
   */
  suspend fun <B> Kind<F, B>.bindWithFilter(f: (B) -> Boolean): B {
    val b: B = this.bind()
    return if (f(b)) b else MF.empty<B>().bind()
  }
}
