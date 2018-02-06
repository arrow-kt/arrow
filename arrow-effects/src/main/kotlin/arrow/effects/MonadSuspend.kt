package arrow.effects

import arrow.HK
import arrow.TC
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.effects.data.internal.BindingCancellationException
import arrow.typeclass
import arrow.typeclasses.MonadError
import kotlin.coroutines.experimental.startCoroutine

/** The context required to defer evaluating a safe computation. **/
@typeclass
interface MonadSuspend<F> : MonadError<F, Throwable>, TC {
    fun <A> suspend(fa: () -> HK<F, A>): HK<F, A>

    operator fun <A> invoke(fa: () -> A): HK<F, A> =
            suspend {
                try {
                    pure(fa())
                } catch (t: Throwable) {
                    raiseError<A>(t)
                }
            }

    fun lazy(): HK<F, Unit> = invoke { }

    fun <A> deferUnsafe(f: () -> Either<Throwable, A>): HK<F, A> =
            suspend { f().fold({ raiseError<A>(it) }, { pure(it) }) }
}

inline fun <reified F, A> (() -> A).defer(SC: MonadSuspend<F> = monadSuspend()): HK<F, A> = SC(this)

inline fun <reified F, A> (() -> Either<Throwable, A>).deferUnsafe(SC: MonadSuspend<F> = monadSuspend()): HK<F, A> =
        SC.deferUnsafe(this)

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside [MonadSuspendCancellableContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 *
 * This operation is cancellable by calling invoke on the [Disposable] return.
 * If [Disposable.invoke] is called the binding result will become a lifted [BindingCancellationException].
 */
fun <F, B> MonadSuspend<F>.bindingCancellable(c: suspend MonadSuspendCancellableContinuation<F, *>.() -> B): Tuple2<HK<F, B>, Disposable> {
    val continuation = MonadSuspendCancellableContinuation<F, B>(this)
    val wrapReturn: suspend MonadSuspendCancellableContinuation<F, *>.() -> HK<F, B> = { pure(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad() toT continuation.disposable()
}
