package arrow.effects

import arrow.*
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation. **/
@typeclass
interface AsyncContext<out F> : Typeclass {
    fun <A> runAsync(fa: Proc<A>): HK<F, A>
}

inline fun <F, A> runAsync(AC: AsyncContext<F>, crossinline f: () -> A): HK<F, A> =
        AC.runAsync { ff: (Either<Throwable, A>) -> Unit ->
            try {
                ff(Right(f()))
            } catch (e: Throwable) {
                ff(Left(e))
            }
        }

suspend inline fun <reified F, A> (() -> A).runAsync(AC: AsyncContext<F> = asyncContext()): HK<F, A> = runAsync(AC, this)

inline fun <F, A> runAsyncUnsafe(AC: AsyncContext<F>, crossinline f: () -> Either<Throwable, A>): HK<F, A> =
        AC.runAsync { ff: (Either<Throwable, A>) -> Unit -> ff(f()) }

suspend inline fun <reified F, A> (() -> Either<Throwable, A>).runAsyncUnsafe(AC: AsyncContext<F> = asyncContext()): HK<F, A> =
        runAsyncUnsafe(AC, this)
