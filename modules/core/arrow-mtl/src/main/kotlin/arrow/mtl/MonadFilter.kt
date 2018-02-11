package arrow.mtl

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.core.Option
import arrow.mtl.continuations.BindingFilterContinuation
import arrow.mtl.continuations.MonadFilterContinuation
import arrow.typeclass
import arrow.typeclasses.Monad
import arrow.typeclasses.internal.Platform
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine

@typeclass
interface MonadFilter<F> : Monad<F>, FunctorFilter<F>, TC {

    fun <A> empty(): Kind<F, A>

    override fun <A, B> mapFilter(fa: Kind<F, A>, f: (A) -> Option<B>): Kind<F, B> =
            flatMap(fa, { a -> f(a).fold({ empty<B>() }, { pure(it) }) })

    /**
     * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
     * A coroutine is initiated and inside [MonadContinuation] suspended yielding to [flatMap]. Once all the flatMap binds are completed
     * the underlying monad is returned from the act of executing the coroutine
     */
    fun <B> bindingFilter(cc: CoroutineContext = EmptyCoroutineContext, c: suspend BindingFilterContinuation<F, *>.() -> B): Kind<F, B> {
        val continuation = MonadFilterContinuation<F, B>(this, Platform.awaitableLatch(), cc)
        val coro: suspend () -> Kind<F, B> = { pure(c(continuation)).also { continuation.resolve(Either.Right(it)) } }
        coro.startCoroutine(continuation)
        return continuation.returnedMonad()
    }

}
