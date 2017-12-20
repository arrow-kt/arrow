package arrow

import kotlin.coroutines.experimental.startCoroutine

interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F>, Typeclass {

    fun <A> ensure(fa: HK<F, A>, error: () -> E, predicate: (A) -> Boolean): HK<F, A> =
            flatMap(fa, {
                if (predicate(it)) pure(it)
                else raiseError(error())
            })

}

inline fun <reified F, A, reified E> HK<F, A>.ensure(
        FT: MonadError<F, E> = monadError(),
        noinline error: () -> E,
        noinline predicate: (A) -> Boolean): HK<F, A> = FT.ensure(this, error, predicate)

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside [MonadErrorContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 */
fun <F, B> MonadError<F, Throwable>.bindingE(c: suspend MonadErrorContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadErrorContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}

inline fun <reified F, reified E> monadError(): MonadError<F, E> =
        instance(InstanceParametrizedType(MonadError::class.java, listOf(typeLiteral<F>(), typeLiteral<E>())))
