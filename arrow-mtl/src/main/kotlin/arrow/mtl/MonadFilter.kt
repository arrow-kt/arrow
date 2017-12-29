package arrow.mtl

import arrow.*
import arrow.core.Option
import kotlin.coroutines.experimental.startCoroutine

interface MonadFilter<F> : Monad<F>, FunctorFilter<F>, Typeclass {

    fun <A> empty(): HK<F, A>

    override fun <A, B> mapFilter(fa: HK<F, A>, f: (A) -> Option<B>): HK<F, B> =
            flatMap(fa, { a -> f(a).fold({ empty<B>() }, { pure(it) }) })
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside [MonadContinuation] suspended yielding to [flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F, B> MonadFilter<F>.bindingFilter(c: suspend MonadFilterContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadFilterContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}

inline fun <reified F> monadFilter(): MonadFilter<F> = instance(InstanceParametrizedType(MonadFilter::class.java, listOf(typeLiteral<F>())))
