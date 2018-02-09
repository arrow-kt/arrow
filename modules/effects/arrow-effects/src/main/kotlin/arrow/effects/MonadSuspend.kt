package arrow.effects

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.effects.data.internal.BindingCancellationException
import arrow.typeclass
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadErrorSyntax
import kotlin.coroutines.experimental.startCoroutine

/** The context required to defer evaluating a safe computation. **/
@typeclass(syntax = false)
interface MonadSuspend<F> : MonadError<F, Throwable>, TC {
    fun <A> suspend(fa: () -> Kind<F, A>): Kind<F, A>

    operator fun <A> invoke(fa: () -> A): Kind<F, A> =
            suspend {
                try {
                    pure(fa())
                } catch (t: Throwable) {
                    raiseError<A>(t)
                }
            }

    fun lazy(): Kind<F, Unit> = invoke { }

    fun <A> deferUnsafe(f: () -> Either<Throwable, A>): Kind<F, A> =
            suspend { f().fold({ raiseError<A>(it) }, { pure(it) }) }
}

interface MonadSuspendSyntax<F> : MonadErrorSyntax<F, Throwable> {

    fun monadSuspend(): MonadSuspend<F>

    override fun monadError() : MonadError <F, Throwable> = monadSuspend()

    fun <A> kotlin.Function0<arrow.core.Either<kotlin.Throwable, A>>.`deferUnsafe`(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().`deferUnsafe`(this)

    fun <A> kotlin.Function0<A>.`invoke`(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().`invoke`(this)

    fun `lazy`(): arrow.Kind<F, kotlin.Unit> =
            this@MonadSuspendSyntax.monadSuspend().`lazy`()

    fun <A> kotlin.Function0<arrow.Kind<F, A>>.`suspend`(dummy: Unit = Unit): arrow.Kind<F, A> =
            this@MonadSuspendSyntax.monadSuspend().`suspend`(this)
}

inline fun <reified F, A> (() -> A).defer(SC: MonadSuspend<F> = monadSuspend()): Kind<F, A> = SC(this)

inline fun <reified F, A> (() -> Either<Throwable, A>).deferUnsafe(SC: MonadSuspend<F> = monadSuspend()): Kind<F, A> =
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
fun <F, B> MonadSuspend<F>.bindingCancellable(c: suspend MonadSuspendCancellableContinuation<F, *>.() -> B): Tuple2<Kind<F, B>, Disposable> {
    val continuation = MonadSuspendCancellableContinuation<F, B>(this)
    val wrapReturn: suspend MonadSuspendCancellableContinuation<F, *>.() -> Kind<F, B> = { pure(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad() toT continuation.disposable()
}
