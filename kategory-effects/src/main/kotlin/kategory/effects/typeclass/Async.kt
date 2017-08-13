package kategory

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation. **/
interface AsyncContext<out F> : Typeclass {
    fun <A> runAsync(fa: Proc<A>): HK<F, A>
}

inline fun <reified F> asyncContext(): AsyncContext<F> = instance(InstanceParametrizedType(AsyncContext::class.java, listOf(F::class.java)))

inline fun <reified F, A> runAsync(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> A): HK<F, A> =
        AC.runAsync { ff: (Either<Throwable, A>) -> Unit ->
            try {
                ff(f().right())
            } catch (e: Throwable) {
                ff(e.left())
            }
        }

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsync(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> B): B =
        runAsync(AC, f).bind()

suspend inline fun <reified F, A, B> StackSafeMonadContinuation<F, A>.bindAsync(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> B): B =
        runAsync(AC, f).bind()

inline fun <reified F, A> runAsyncUnsafe(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> Either<Throwable, A>): HK<F, A> = AC.runAsync { ff: (Either<Throwable, A>) -> Unit -> ff(f()) }

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsyncUnsafe(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> Either<Throwable, B>): B =
        runAsyncUnsafe(AC, f).bind()

suspend inline fun <reified F, A, B> StackSafeMonadContinuation<F, A>.bindAsyncUnsafe(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> Either<Throwable, B>): B =
        runAsyncUnsafe(AC, f).bind()
