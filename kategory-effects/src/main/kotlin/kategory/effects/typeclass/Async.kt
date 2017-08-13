package kategory

import kotlin.coroutines.experimental.startCoroutine

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation. **/
interface AsyncContext<out F> : Typeclass {
    fun <A> runAsync(fa: Proc<A>): HK<F, A>
}

inline fun <reified F> asyncContext(): AsyncContext<F> = instance(InstanceParametrizedType(AsyncContext::class.java, listOf(F::class.java)))

inline fun <F, A> runAsync(AC: AsyncContext<F>, crossinline f: () -> A): HK<F, A> =
        AC.runAsync { ff: (Either<Throwable, A>) -> Unit ->
            try {
                ff(f().right())
            } catch (e: Throwable) {
                ff(e.left())
            }
        }

suspend inline fun <reified F, A> (() -> A).runAsync(AC: AsyncContext<F> = asyncContext()): HK<F, A> = runAsync(AC, this)

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsync(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> B): B = runAsync(AC, f).bind()

suspend inline fun <reified F, A, B> StackSafeMonadContinuation<F, A>.bindAsync(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> B): B = runAsync(AC, f).bind()

inline fun <F, A> runAsyncUnsafe(AC: AsyncContext<F>, crossinline f: () -> Either<Throwable, A>): HK<F, A> = AC.runAsync { ff: (Either<Throwable, A>) -> Unit -> ff(f()) }

suspend inline fun <reified F, A> (() -> Either<Throwable, A>).runAsyncUnsafe(AC: AsyncContext<F> = asyncContext()): HK<F, A> = runAsyncUnsafe(AC, this)

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsyncUnsafe(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> Either<Throwable, B>): B = runAsyncUnsafe(AC, f).bind()

suspend inline fun <reified F, A, B> StackSafeMonadContinuation<F, A>.bindAsyncUnsafe(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> Either<Throwable, B>): B = runAsyncUnsafe(AC, f).bind()

open class AsyncMonadContinuation<F, A>(M: Monad<F>, val AC: AsyncContext<F>) : MonadContinuation<F, A>(M) {

    internal fun returnedMonad(): HK<F, A> = returnedMonad

    suspend inline fun <B> bindAsync(crossinline f: () -> B): B = runAsync(AC, f).bind()

    suspend inline fun <B> bindAsyncUnsafe(crossinline f: () -> Either<Throwable, B>): B = runAsyncUnsafe(AC, f).bind()
}

fun <F, B> AsyncContext<F>.bindingAsync(M: Monad<F>, c: suspend AsyncMonadContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = AsyncMonadContinuation<F, B>(M, this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}

open class AsyncStackSafeMonadContinuation<F, A>(M: Monad<F>, val AC: AsyncContext<F>) : StackSafeMonadContinuation<F, A>(M) {

    internal fun returnedMonad(): Free<F, A> = returnedMonad

    suspend inline fun <B> bindAsync(crossinline f: () -> B): B = runAsync(AC, f).bind()

    suspend inline fun <B> bindAsyncUnsafe(crossinline f: () -> Either<Throwable, B>): B = runAsyncUnsafe(AC, f).bind()
}

fun <F, B> AsyncContext<F>.bindingStackSafeAsync(M: Monad<F>, c: suspend AsyncStackSafeMonadContinuation<F, *>.() -> Free<F, B>): Free<F, B> {
    val continuation = AsyncStackSafeMonadContinuation<F, B>(M, this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}