package arrow.mtl

import arrow.Kind
import arrow.TC
import arrow.core.Option
import arrow.typeclass
import arrow.typeclasses.Monad
import kotlin.coroutines.experimental.startCoroutine

@typeclass
interface MonadFilter<F> : Monad<F>, FunctorFilter<F>, TC {

    fun <A> empty(): Kind<F, A>

    override fun <A, B> mapFilter(fa: Kind<F, A>, f: (A) -> Option<B>): Kind<F, B> =
            flatMap(fa, { a -> f(a).fold({ empty<B>() }, { pure(it) }) })
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside [MonadContinuation] suspended yielding to [flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F, B> MonadFilter<F>.bindingFilter(c: suspend MonadFilterContinuation<F, *>.() -> B): Kind<F, B> {
    val continuation = MonadFilterContinuation<F, B>(this)
    val wrapReturn: suspend MonadFilterContinuation<F, *>.() -> Kind<F, B> = { pure(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}
