package kategory

import kategory.effects.data.internal.Platform.onceOnly
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.CoroutineContext

@higherkind data class JobKW<out A>(val thunk: ((Either<Throwable, A>) -> Unit) -> Job) : HK<JobKWHK, A> {

    fun <B> map(f: (A) -> B): JobKW<B> =
            JobKW { ff: (Either<Throwable, B>) -> Unit ->
                thunk { either: Either<Throwable, A> ->
                    ff(either.map(f))
                }
            }

    fun <B> flatMap(coroutineContext: CoroutineContext, f: (A) -> JobKW<B>): JobKW<B> =
            JobKW { ff: (Either<Throwable, B>) -> Unit ->
                val result = AtomicReference<Either<Throwable, A>>()
                thunk(result::set).apply {
                    invokeOnCompletion { t: Throwable? ->
                        val state: Either<Throwable, A>? = result.get()
                        when {
                            t == null && state != null -> state.fold({ ff(it.left()) }, { f(it).thunk(ff) })
                            t != null -> JobKW.raiseError<B>(coroutineContext, t)
                            else -> throw IllegalStateException("JobKW flatMap completed without success or error")
                        }
                    }
                }
            }

    companion object {
        inline operator fun <A> invoke(coroutineContext: CoroutineContext, noinline f: suspend CoroutineScope.() -> A): JobKW<A> =
                JobKW {
                    onceOnly(it).let { callback: (Either<Throwable, A>) -> Unit ->
                        launch(coroutineContext, CoroutineStart.DEFAULT) {
                            callback(
                                    try {
                                        f().right()
                                    } catch (err: Throwable) {
                                        err.left()
                                    }
                            )
                        }
                    }
                }

        inline fun <A> unsafe(coroutineContext: CoroutineContext, noinline f: suspend CoroutineScope.() -> Either<Throwable, A>): JobKW<A> =
                JobKW {
                    onceOnly(it).let { callback: (Either<Throwable, A>) -> Unit ->
                        launch(coroutineContext, CoroutineStart.DEFAULT) {
                            callback(f())
                        }
                    }
                }

        inline fun <A> async(coroutineContext: CoroutineContext, crossinline fa: Proc<A>): JobKW<A> =
                JobKW {
                    onceOnly(it).let { callback: (Either<Throwable, A>) -> Unit ->
                        launch(coroutineContext, CoroutineStart.DEFAULT) {
                            fa(callback)
                        }
                    }
                }

        inline fun <A> pure(coroutineContext: CoroutineContext, a: A): JobKW<A> =
                JobKW(coroutineContext) { a }

        inline fun <A> raiseError(coroutineContext: CoroutineContext, t: Throwable): JobKW<A> =
                JobKW.unsafe(coroutineContext) { t.left() }

        fun <A, B> tailRecM(coroutineContext: CoroutineContext, a: A, f: (A) -> JobKW<Either<A, B>>): JobKW<B> =
                JobKW.async(coroutineContext) { ff: (Either<Throwable, B>) -> Unit ->
                    f(a).runJob { either: Either<Throwable, Either<A, B>> ->
                        either.fold({ ff(it.left()) }, {
                            when (it) {
                                is Either.Right -> ff(it.b.right())
                                is Either.Left -> tailRecM(coroutineContext, a, f)
                            }
                        })
                    }
                }

        inline fun instances(coroutineContext: CoroutineContext): JobKWInstances =
                object : JobKWInstances {
                    override fun CC(): CoroutineContext = coroutineContext
                }

        inline fun functor(coroutineContext: CoroutineContext): Functor<JobKWHK> = instances(coroutineContext)

        inline fun applicative(coroutineContext: CoroutineContext): Applicative<JobKWHK> = instances(coroutineContext)

        inline fun monad(coroutineContext: CoroutineContext): Monad<JobKWHK> = instances(coroutineContext)

        inline fun monadError(coroutineContext: CoroutineContext): MonadError<JobKWHK, Throwable> = instances(coroutineContext)

        inline fun asyncContext(coroutineContext: CoroutineContext): AsyncContext<JobKWHK> = instances(coroutineContext)
    }
}

fun <A> JobKWKind<A>.runJob(ff: (Either<Throwable, A>) -> Unit): Job =
        this.ev().thunk(ff)

inline fun <A> JobKW<A>.handleErrorWith(crossinline function: (Throwable) -> JobKW<A>): JobKW<A> =
        JobKW { ff: (Either<Throwable, A>) -> Unit ->
            val result = AtomicReference<Either<Throwable, A>>()
            thunk(result::set).apply {
                invokeOnCompletion { t: Throwable? ->
                    val state: Either<Throwable, A>? = result.get()
                    when {
                        t == null && state != null -> state.fold({ function(it).thunk(ff) }, { ff(it.right()) })
                        t != null -> function(t).thunk(ff)
                        else -> throw IllegalStateException("JobKW handleErrorWith completed without success or error")
                    }
                }
            }
        }