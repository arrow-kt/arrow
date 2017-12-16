package kategory.effects

import kategory.*
import kategory.effects.data.internal.BindingCancellationException
import kotlin.coroutines.experimental.startCoroutine

interface MonadSuspend<F, E> : MonadError<F, E>, Typeclass {
    fun <A> unsafeRunSync(fa: HK<F, A>): A

    fun <A> runAsync(fa: HK<F, A>, cb: (Either<E, A>) -> HK<F, Unit>): HK<F, Unit>

    fun <A> unsafeRunAsync(fa: HK<F, A>, cb: (Either<E, A>) -> Unit): Unit

    fun <A> suspend(f: () -> HK<F, A>): HK<F, A>

    operator fun <A> invoke(f: () -> A): HK<F, A> =
            suspend { pure(f()) }

    fun lazy(): HK<F, Unit> = invoke { }
}

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside [MonadSuspendContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over [MonadSuspend] instances that can support [Throwable] in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 *
 * This operation is cancellable by calling invoke on the [Fiber].
 * If [invoke] is called the binding result will become a lifted [BindingCancellationException].
 *
 */
fun <F, B> MonadSuspend<F, Throwable>.bindingFiber(AC: AsyncContext<F>, c: suspend MonadSuspendContinuation<F, *>.() -> HK<F, B>): Fiber<F, B> {
    val continuation = MonadSuspendContinuation<F, B>(this, AC)
    c.startCoroutine(continuation, continuation)
    return Fiber(continuation.returnedMonad(), continuation.disposable())
}

inline fun <reified F, reified E> monadSuspend(): MonadSuspend<F, E> =
        instance(InstanceParametrizedType(MonadSuspend::class.java, listOf(typeLiteral<F>(), typeLiteral<E>())))