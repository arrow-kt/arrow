package kategory.effects

import kategory.*

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation. **/
interface AsyncContext<out F> : Typeclass {
    fun <A> runAsync(fa: Proc<A>): HK<F, A>
}

inline fun <reified F> asyncContext(): AsyncContext<F> = instance(InstanceParametrizedType(AsyncContext::class.java, listOf(typeLiteral<F>())))

inline fun <F, A> runAsync(AC: AsyncContext<F>, crossinline f: () -> A): HK<F, A> =
        AC.runAsync { ff: (Either<Throwable, A>) -> Unit ->
            try {
                ff(f().right())
            } catch (e: Throwable) {
                ff(e.left())
            }
        }

suspend inline fun <reified F, A> (() -> A).runAsync(AC: AsyncContext<F> = asyncContext()): HK<F, A> = runAsync(AC, this)

inline fun <F, A> runAsyncUnsafe(AC: AsyncContext<F>, crossinline f: () -> Either<Throwable, A>): HK<F, A> =
        AC.runAsync { ff: (Either<Throwable, A>) -> Unit -> ff(f()) }

suspend inline fun <reified F, A> (() -> Either<Throwable, A>).runAsyncUnsafe(AC: AsyncContext<F> = asyncContext()): HK<F, A> =
        runAsyncUnsafe(AC, this)
