package kategory.effects

import kategory.*

open class AsyncMonadContinuation<F, A>(M: Monad<F>, val AC: AsyncContext<F>) : MonadContinuation<F, A>(M) {

    internal fun returnedMonad(): HK<F, A> = returnedMonad

    suspend inline fun <B> bindAsync(crossinline f: () -> B): B = runAsync(AC, f).bind()

    suspend inline fun <B> bindAsyncUnsafe(crossinline f: () -> Either<Throwable, B>): B = runAsyncUnsafe(AC, f).bind()
}

open class AsyncStackSafeMonadContinuation<F, A>(M: Monad<F>, val AC: AsyncContext<F>) : StackSafeMonadContinuation<F, A>(M) {

    internal fun returnedMonad(): Free<F, A> = returnedMonad

    suspend inline fun <B> bindAsync(crossinline f: () -> B): B = runAsync(AC, f).bind()

    suspend inline fun <B> bindAsyncUnsafe(crossinline f: () -> Either<Throwable, B>): B = runAsyncUnsafe(AC, f).bind()
}

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsync(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> B): B =
        runAsync(AC, f).bind()

suspend inline fun <reified F, A, B> StackSafeMonadContinuation<F, A>.bindAsync(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> B): B =
        runAsync(AC, f).bind()

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsyncUnsafe(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> Either<Throwable, B>):
        B = runAsyncUnsafe(AC, f).bind()

suspend inline fun <reified F, A, B> StackSafeMonadContinuation<F, A>.bindAsyncUnsafe(AC: AsyncContext<F> = asyncContext(),
                                                                                      crossinline f: () -> Either<Throwable, B>): B =
        runAsyncUnsafe(AC, f).bind()