package arrow.mtl.continuations

import arrow.Kind
import arrow.mtl.MonadFilter
import arrow.typeclasses.continuations.BindingContinuation

interface BindingFilterContinuation<F, A> : BindingContinuation<F, A>, MonadFilter<F> {
    /**
     * Binds only if the given predicate matches the inner value otherwise binds into the Monad `empty()` value
     * on `MonadFilter` instances
     */
    suspend fun <B> Kind<F, B>.bindWithFilter(f: (B) -> Boolean): B

    /**
     * Short circuits monadic bind if `predicate == false` return the
     * monad `empty` value.
     */
    fun continueIf(predicate: Boolean): Unit
}
