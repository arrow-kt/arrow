package kategory

import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.CoroutineContext

@higherkind data class JobW<out A>(val thunk: ((Either<Throwable, A>) -> Unit) -> Job) : HK<JobWHK, A> {

    fun <B> map(f: (A) -> B): JobW<B> =
            JobW { ff: (Either<Throwable, B>) -> Unit ->
                thunk { either: Either<Throwable, A> ->
                    ff(either.map(f))
                }
            }

    fun <B> flatMap(f: (A) -> JobW<B>): JobW<B> =
            JobW { ff: (Either<Throwable, B>) -> Unit ->
                val state = AtomicReference<A?>()
                thunk { either: Either<Throwable, A> ->
                    either.fold({}, { state.set(it) })
                }.apply {
                    this.invokeOnCompletion { t: Throwable? ->
                        val a: A? = state.get()
                        if (t == null && a != null) {
                            f(a).thunk { either: Either<Throwable, B> ->
                                ff(either)
                            }
                        }
                    }
                }
            }

    companion object {
        fun <A> pure(coroutineContext: CoroutineContext, a: A): JobW<A> =
                JobW { ff: (Either<Throwable, A>) -> Unit ->
                    launch(coroutineContext, CoroutineStart.DEFAULT) {
                        ff(a.right())
                    }
                }

        fun <A> raiseError(coroutineContext: CoroutineContext, t: Throwable): JobW<A> =
                JobW { ff: (Either<Throwable, A>) -> Unit ->
                    launch(coroutineContext, CoroutineStart.DEFAULT) {
                        ff(t.left())
                    }
                }

        fun <A, B> tailRecM(a: A, f: (A) -> JobW<Either<A, B>>, coroutineContext: CoroutineContext): JobW<B> =
                JobW.runAsync(coroutineContext) { ff: (Either<Throwable, B>) -> Unit ->
                    f(a).runJob { either: Either<Throwable, Either<A, B>> ->
                        either.fold({ ff(it.left()) }, {
                            when (it) {
                                is Either.Right -> ff(it.b.right())
                                is Either.Left -> tailRecM(a, f, coroutineContext)
                            }
                        })
                    }
                }

        fun <A> runAsync(coroutineContext: CoroutineContext, fa: Proc<A>): JobW<A> =
                JobW { ff: (Either<Throwable, A>) -> Unit ->
                    launch(coroutineContext, CoroutineStart.DEFAULT) {
                        fa(ff)
                    }
                }

        inline fun instances(coroutineContext: CoroutineContext): JobWInstances =
                object : JobWInstances {
                    override fun CC(): CoroutineContext = coroutineContext
                }

        fun functor(coroutineContext: CoroutineContext): Functor<JobWHK> = instances(coroutineContext)

        fun applicative(coroutineContext: CoroutineContext): Applicative<JobWHK> = instances(coroutineContext)

        fun monad(coroutineContext: CoroutineContext): Monad<JobWHK> = instances(coroutineContext)

        fun monadError(coroutineContext: CoroutineContext): MonadError<JobWHK, Throwable> = instances(coroutineContext)

        fun asyncContext(coroutineContext: CoroutineContext): AsyncContext<JobWHK> = instances(coroutineContext)
    }
}

fun <A> JobWKind<A>.runJob(ff: (Either<Throwable, A>) -> Unit): Job =
        this.ev().thunk(ff)

fun <A> JobW<A>.handleErrorWith(function: (Throwable) -> JobW<A>): JobW<A> =
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