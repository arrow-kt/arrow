package kategory

import kategory.effects.data.internal.Platform.onceOnly
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext

@higherkind data class JobW<out A>(val thunk: ((Either<Throwable, A>) -> Unit) -> Job) : HK<JobWHK, A> {

    fun <B> map(f: (A) -> B): JobW<B> =
            JobW { ff: (Either<Throwable, B>) -> Unit ->
                thunk { either: Either<Throwable, A> ->
                    ff(either.map(f))
                }
            }

    fun <B> flatMap(f: (A) -> JobW<B>): JobW<B> =
            JobW { ff: (Either<Throwable, B>) -> Unit ->
                val state = AtomicReference<Either<Throwable, A>>()
                thunk { either: Either<Throwable, A> ->
                    state.set(either)
                }.apply {
                    this.invokeOnCompletion { t: Throwable? ->
                        val a: Either<Throwable, A>? = state.get()
                        if (t == null && a != null) {
                            a.fold(
                                    { ff(it.left()) },
                                    {
                                        f(it).thunk { either: Either<Throwable, B> ->
                                            ff(either)
                                        }
                                    })

                        } else if (t != null) {
                            JobW.raiseError<B>(EmptyCoroutineContext, t)
                        } else {
                            throw IllegalStateException("JobW flatMap completed without success or error")
                        }
                    }
                }
            }

    companion object {
        inline operator fun <A> invoke(coroutineContext: CoroutineContext, noinline f: suspend CoroutineScope.() -> A): JobW<A> =
                JobW {
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

        inline fun <A> unsafe(coroutineContext: CoroutineContext, noinline f: suspend CoroutineScope.() -> Either<Throwable, A>): JobW<A> =
                JobW {
                    onceOnly(it).let { callback: (Either<Throwable, A>) -> Unit ->
                        launch(coroutineContext, CoroutineStart.DEFAULT) {
                            callback(f())
                        }
                    }
                }

        inline fun <A> async(coroutineContext: CoroutineContext, crossinline fa: Proc<A>): JobW<A> =
                JobW {
                    onceOnly(it).let { callback: (Either<Throwable, A>) -> Unit ->
                        launch(coroutineContext, CoroutineStart.DEFAULT) {
                            fa(callback)
                        }
                    }
                }

        inline fun <A> pure(coroutineContext: CoroutineContext, a: A): JobW<A> =
                JobW(coroutineContext) { a }

        inline fun <A> raiseError(coroutineContext: CoroutineContext, t: Throwable): JobW<A> =
                JobW.unsafe(coroutineContext) { t.left() }

        fun <A, B> tailRecM(coroutineContext: CoroutineContext, a: A, f: (A) -> JobW<Either<A, B>>): JobW<B> =
                JobW.async(coroutineContext) { ff: (Either<Throwable, B>) -> Unit ->
                    f(a).runJob { either: Either<Throwable, Either<A, B>> ->
                        either.fold({ ff(it.left()) }, {
                            when (it) {
                                is Either.Right -> ff(it.b.right())
                                is Either.Left -> tailRecM(coroutineContext, a, f)
                            }
                        })
                    }
                }

        inline fun instances(coroutineContext: CoroutineContext): JobWInstances =
                object : JobWInstances {
                    override fun CC(): CoroutineContext = coroutineContext
                }

        inline fun functor(coroutineContext: CoroutineContext): Functor<JobWHK> = instances(coroutineContext)

        inline fun applicative(coroutineContext: CoroutineContext): Applicative<JobWHK> = instances(coroutineContext)

        inline fun monad(coroutineContext: CoroutineContext): Monad<JobWHK> = instances(coroutineContext)

        inline fun monadError(coroutineContext: CoroutineContext): MonadError<JobWHK, Throwable> = instances(coroutineContext)

        inline fun asyncContext(coroutineContext: CoroutineContext): AsyncContext<JobWHK> = instances(coroutineContext)
    }
}

fun <A> JobWKind<A>.runJob(ff: (Either<Throwable, A>) -> Unit): Job =
        this.ev().thunk(ff)

inline fun <A> JobW<A>.handleErrorWith(crossinline function: (Throwable) -> JobW<A>): JobW<A> =
        JobW { ff: (Either<Throwable, A>) -> Unit ->
            val runCallback: (Either<Throwable, A>) -> Unit = { either: Either<Throwable, A> ->
                ff(either)
            }
            this.thunk(runCallback).apply {
                invokeOnCompletion { t: Throwable? ->
                    if (t != null) {
                        function(t).thunk(runCallback)
                    }
                }
            }
        }