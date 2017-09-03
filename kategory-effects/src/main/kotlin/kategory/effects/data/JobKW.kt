package kategory

import kategory.effects.data.internal.Platform.onceOnly
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.CoroutineContext

typealias JobResult<T> = Either<Throwable, T>

@higherkind
@deriving(Monad::class, AsyncContext::class)
data class JobKW<out A>(val thunk: (CoroutineContext, (JobResult<A>) -> Unit) -> Job) : JobKWKind<A> {

    fun <B> map(f: (A) -> B): JobKW<B> =
            JobKW { context: CoroutineContext, ff: (JobResult<B>) -> Unit ->
                thunk(context) { either: JobResult<A> ->
                    ff(either.map(f))
                }
            }

    fun <B> flatMap(f: (A) -> JobKWKind<B>): JobKW<B> =
            JobKW { context: CoroutineContext, ff: (JobResult<B>) -> Unit ->
                val result = AtomicReference<JobResult<A>>()
                thunk(context, result::set).apply {
                    invokeOnCompletion { t: Throwable? ->
                        val state: Either<Throwable, A>? = result.get()
                        when {
                            t == null && state != null -> state.fold({ ff(it.left()) }, { f(it).ev().thunk(context, ff) })
                            t != null -> JobKW.raiseError<B>(t)
                            else -> throw IllegalStateException("JobKW flatMap completed without success or error")
                        }
                    }
                }
            }

    companion object {
        inline operator fun <A> invoke(noinline f: suspend CoroutineScope.() -> A): JobKW<A> =
                JobKW { context: CoroutineContext, ff: (JobResult<A>) -> Unit ->
                    onceOnly(ff).let { callback: (JobResult<A>) -> Unit ->
                        launch(context, CoroutineStart.DEFAULT) {
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

        inline fun <A> unsafe(noinline f: suspend CoroutineScope.() -> JobResult<A>): JobKW<A> =
                JobKW { context: CoroutineContext, ff: (JobResult<A>) -> Unit ->
                    onceOnly(ff).let { callback: (JobResult<A>) -> Unit ->
                        launch(context, CoroutineStart.DEFAULT) {
                            callback(f())
                        }
                    }
                }

        inline fun <A> runAsync(crossinline fa: Proc<A>): JobKW<A> =
                JobKW { context: CoroutineContext, ff: (JobResult<A>) -> Unit ->
                    onceOnly(ff).let { callback: (JobResult<A>) -> Unit ->
                        launch(context, CoroutineStart.DEFAULT) {
                            fa(callback)
                        }
                    }
                }

        inline fun <A> pure(a: A): JobKW<A> =
                JobKW.invoke { a }

        inline fun <A> raiseError(t: Throwable): JobKW<A> =
                JobKW.unsafe { t.left() }

        fun <A, B> tailRecM(a: A, f: (A) -> JobKWKind<Either<A, B>>): JobKW<B> =
                JobKW { context: CoroutineContext, ff: (JobResult<B>) -> Unit ->
                    f(a).runJob(context) { either: JobResult<Either<A, B>> ->
                        either.fold({ ff(it.left()) }, {
                            when (it) {
                                is Either.Right -> ff(it.b.right())
                                is Either.Left -> tailRecM(it.a, f)
                            }
                        })
                    }
                }

        inline fun functor(): JobKWHKMonadErrorInstance = object : JobKWHKMonadErrorInstance {}

        inline fun applicative(): JobKWHKMonadErrorInstance = object : JobKWHKMonadErrorInstance {}

        inline fun monadError(): JobKWHKMonadErrorInstance = object : JobKWHKMonadErrorInstance {}
    }
}

fun <A> JobKWKind<A>.runJob(context: CoroutineContext, ff: (JobResult<A>) -> Unit): Job =
        this.ev().thunk(context, ff)

inline fun <A> JobKW<A>.handleErrorWith(crossinline function: (Throwable) -> JobKWKind<A>): JobKW<A> =
        JobKW { context: CoroutineContext, ff: (JobResult<A>) -> Unit ->
            val result = AtomicReference<JobResult<A>>()
            this@handleErrorWith.runJob(context, result::set).apply {
                invokeOnCompletion { t: Throwable? ->
                    val state: Either<Throwable, A>? = result.get()
                    when {
                        t == null && state != null -> state.fold({ function(it).runJob(context, ff) }, { ff(it.right()) })
                        t != null -> function(t).runJob(context, ff)
                        else -> throw IllegalStateException("JobKW handleErrorWith completed without success or error")
                    }
                }
            }
        }
