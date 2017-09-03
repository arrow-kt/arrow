package kategory

import kategory.effects.data.internal.Platform
import kategory.effects.instances.DeferredKWInstances
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

typealias DeferredResult<A> = Either<Throwable, A>

@higherkind data class DeferredKW<out A>(val coroutineContext: CoroutineContext, val thunk: (CoroutineContext) -> Deferred<A>) : DeferredKWKind<A> {

    fun runDeferred(): Deferred<A> =
            thunk(coroutineContext)

    fun <B> map(f: (A) -> B): DeferredKW<B> =
            flatMap { a: A -> pure(coroutineContext, f(a)) }

    fun <B> flatMap(f: (A) -> DeferredKW<B>): DeferredKW<B> =
            DeferredKW(coroutineContext) {
                this@DeferredKW.attempt().fold(
                        { raiseError<B>(coroutineContext, it).runDeferred() },
                        { f(it).runDeferred() })
            }

    companion object {
        inline operator fun <A> invoke(coroutineContext: CoroutineContext, noinline f: suspend CoroutineScope.() -> A): DeferredKW<A> =
                DeferredKW(coroutineContext) { context: CoroutineContext ->
                    Platform.onceOnly<Unit> { Unit }.let {
                        async(context, CoroutineStart.DEFAULT) {
                            f()
                        }
                    }
                }

        inline fun <A> async(coroutineContext: CoroutineContext, noinline fa: Proc<A>): DeferredKW<A> =
                DeferredKW(coroutineContext) { context: CoroutineContext ->
                    Platform.onceOnly<Unit> { Unit }.let {
                        async(context, CoroutineStart.DEFAULT) {
                            IO.async(fa).unsafeRunSync()
                        }
                    }
                }

        inline fun <A> pure(coroutineContext: CoroutineContext, a: A): DeferredKW<A> =
                DeferredKW.invoke(coroutineContext, { a })

        inline fun <A> raiseError(coroutineContext: CoroutineContext, t: Throwable): DeferredKW<A> =
                DeferredKW(coroutineContext) { throw t }

        fun <A, B> tailRecM(coroutineContext: CoroutineContext, a: A, f: (A) -> DeferredKW<Either<A, B>>): DeferredKW<B> {
            tailrec fun go(a: A, f: (A) -> DeferredKW<Either<A, B>>): DeferredKW<B> {
                val result: DeferredResult<Either<A, B>> = f(a).attempt()
                /* FIXME(paco): KT-20075 If you remove return here tailrec stops working. Jetbrains Please. */
                return when (result) {
                    is Either.Left -> raiseError(coroutineContext, result.a)
                    is Either.Right -> {
                        val next: Either<A, B> = result.b
                        when (next) {
                            is Either.Left -> go(next.a, f)
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
            try {
                val deferred: DeferredKW<A> = this@attempt.ev()
                deferred.runDeferred().await().right()
            } catch (throwable: Throwable) {
                throwable.left()
            }
        }

fun <A> DeferredKWKind<A>.unsafeRun(): A =
        attempt().fold({ throw it }, { it })

fun <A> DeferredKWKind<A>.runDeferred(): Deferred<A> =
        this.ev().runDeferred()

inline fun <A> DeferredKW<A>.handleErrorWith(coroutineContext: CoroutineContext, crossinline function: (Throwable) -> DeferredKW<A>): DeferredKW<A> =
        DeferredKW(coroutineContext) { context: CoroutineContext ->
            this@handleErrorWith.attempt().fold(
                    { function(it).runDeferred() },
                    { a: A ->
                        async(coroutineContext, CoroutineStart.DEFAULT) {
                            a
                        }
                    })
        }
