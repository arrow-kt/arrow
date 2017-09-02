package kategory

import kategory.effects.data.internal.Platform
import kategory.effects.instances.DeferredKWInstances
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

typealias DeferredResult<A> = Either<Throwable, A>

@higherkind data class DeferredKW<out A>(val thunk: () -> Deferred<DeferredResult<A>>) : DeferredKWKind<A> {

    fun <B> map(coroutineContext: CoroutineContext, f: (A) -> B): DeferredKW<B> =
            flatMap(coroutineContext, { a: A -> pure(coroutineContext, f(a)) })

    fun <B> flatMap(coroutineContext: CoroutineContext, f: (A) -> DeferredKW<B>): DeferredKW<B> =
            DeferredKW {
                this@DeferredKW.attempt().fold(
                        { raiseError<B>(coroutineContext, it).thunk() },
                        { f(it).thunk() })
            }

    companion object {
        inline operator fun <A> invoke(coroutineContext: CoroutineContext, noinline f: suspend CoroutineScope.() -> A): DeferredKW<A> =
                DeferredKW {
                    Platform.onceOnly<Unit> { Unit }.let {
                        async(coroutineContext, CoroutineStart.DEFAULT) {
                            try {
                                f().right()
                            } catch (throwable: Throwable) {
                                throwable.left()
                            }
                        }
                    }
                }

        inline fun <A> unsafe(coroutineContext: CoroutineContext, noinline f: suspend CoroutineScope.() -> DeferredResult<A>): DeferredKW<A> =
                DeferredKW {
                    Platform.onceOnly<Unit> { Unit }.let {
                        async(coroutineContext, CoroutineStart.DEFAULT) {
                            f()
                        }
                    }
                }

        inline fun <A> async(coroutineContext: CoroutineContext, noinline fa: Proc<A>): DeferredKW<A> =
                DeferredKW {
                    Platform.onceOnly<Unit> { Unit }.let {
                        async(coroutineContext, CoroutineStart.DEFAULT) {
                            IO.async(fa).attempt().unsafeRunSync()
                        }
                    }
                }

        inline fun <A> pure(coroutineContext: CoroutineContext, a: A): DeferredKW<A> =
                DeferredKW(coroutineContext) { a }

        inline fun <A> raiseError(coroutineContext: CoroutineContext, t: Throwable): DeferredKW<A> =
                DeferredKW.unsafe(coroutineContext) { t.left() }

        fun <A, B> tailRecM(coroutineContext: CoroutineContext, a: A, f: (A) -> DeferredKW<Either<A, B>>): DeferredKW<B> {
            tailrec fun go(a: A, f: (A) -> DeferredKW<Either<A, B>>): DeferredKW<B> =
                    f(a).attempt().let { result ->
                        /* FIXME(paco): KT-20075 If you remove return here tailrec stops working. Jetbrains Please. */
                        when (result) {
                            is Either.Left -> raiseError(coroutineContext, result.a)
                            is Either.Right -> {
                                val next: Either<A, B> = result.b
                                when (next) {
                                    is Either.Left -> return go(next.a, f)
                                    is Either.Right -> pure(coroutineContext, next.b)
                                }
                            }
                        }
                    }
            return go(a, f)
        }

        inline fun instances(coroutineContext: CoroutineContext): DeferredKWInstances =
                object : DeferredKWInstances {
                    override fun CC(): CoroutineContext = coroutineContext
                }

        inline fun functor(coroutineContext: CoroutineContext): Functor<DeferredKWHK> = instances(coroutineContext)

        inline fun applicative(coroutineContext: CoroutineContext): Applicative<DeferredKWHK> = instances(coroutineContext)

        inline fun monad(coroutineContext: CoroutineContext): Monad<DeferredKWHK> = instances(coroutineContext)

        inline fun monadError(coroutineContext: CoroutineContext): MonadError<DeferredKWHK, Throwable> = instances(coroutineContext)

        inline fun asyncContext(coroutineContext: CoroutineContext): AsyncContext<DeferredKWHK> = instances(coroutineContext)
    }
}

fun <A> DeferredKWKind<A>.attempt(): DeferredResult<A> =
        runBlocking {
            this@attempt.ev().thunk().await()
        }

fun <A> DeferredKWKind<A>.unsafeRun(): A =
        attempt().fold({ throw it }, { it })

fun <A> DeferredKWKind<A>.runDeferred(): Deferred<DeferredResult<A>> =
        this.ev().thunk()

inline fun <A> DeferredKW<A>.handleErrorWith(coroutineContext: CoroutineContext, crossinline function: (Throwable) -> DeferredKW<A>): DeferredKW<A> =
        DeferredKW {
            this@handleErrorWith.attempt().fold(
                    { function(it).thunk() },
                    { a: A ->
                        async(coroutineContext, CoroutineStart.DEFAULT) {
                            a.right()
                        }
                    })
        }
