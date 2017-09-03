package kategory

import kategory.effects.data.internal.Platform
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

typealias DeferredResult<A> = Either<Throwable, A>

@higherkind
@deriving(Monad::class, AsyncContext::class)
data class DeferredKW<out A>(val thunk: (CoroutineContext) -> Deferred<A>) : DeferredKWKind<A> {

    fun <B> map(f: (A) -> B): DeferredKW<B> =
            flatMap { a: A -> pure(f(a)) }

    fun <B> flatMap(f: (A) -> DeferredKWKind<B>): DeferredKW<B> =
            DeferredKW { context: CoroutineContext ->
                this@DeferredKW.attempt(context).fold(
                        { raiseError<B>(it).runDeferred(context) },
                        { f(it).ev().runDeferred(context) })
            }

    companion object {
        inline operator fun <A> invoke(noinline f: suspend CoroutineScope.() -> A): DeferredKW<A> =
                DeferredKW { context: CoroutineContext ->
                    Platform.onceOnly<Unit> { Unit }.let {
                        async(context, CoroutineStart.DEFAULT) {
                            f()
                        }
                    }
                }

        inline fun <A> runAsync(noinline fa: Proc<A>): DeferredKW<A> =
                DeferredKW { context: CoroutineContext ->
                    Platform.onceOnly<Unit> { Unit }.let {
                        async(context, CoroutineStart.DEFAULT) {
                            IO.async(fa).unsafeRunSync()
                        }
                    }
                }

        inline fun <A> pure(a: A): DeferredKW<A> =
                DeferredKW.invoke { a }

        inline fun <A> raiseError(t: Throwable): DeferredKW<A> =
                DeferredKW { throw t }

        fun <A, B> tailRecM(a: A, f: (A) -> DeferredKWKind<Either<A, B>>): DeferredKW<B> {
            tailrec fun go(coroutineContext: CoroutineContext, a: A, f: (A) -> DeferredKWKind<Either<A, B>>): DeferredKW<B> {
                val result: DeferredResult<Either<A, B>> = f(a).attempt(coroutineContext)
                /* FIXME(paco): KT-20075 If you remove return here tailrec stops working. Jetbrains Please. */
                return when (result) {
                    is Either.Left -> raiseError(result.a)
                    is Either.Right -> {
                        val next: Either<A, B> = result.b
                        when (next) {
                            is Either.Left -> go(coroutineContext, next.a, f)
                            is Either.Right -> pure(next.b)
                        }
                    }
                }
            }
            return DeferredKW { context: CoroutineContext ->
                go(context, a, f).thunk(context)
            }
        }

        inline fun functor(): DeferredKWHKMonadInstance = object : DeferredKWHKMonadInstance {}

        inline fun applicative(): DeferredKWHKMonadInstance = object : DeferredKWHKMonadInstance {}

        inline fun monadError(): DeferredKWHKMonadErrorInstance = object : DeferredKWHKMonadErrorInstance {}
    }
}

fun <A> DeferredKWKind<A>.runDeferred(coroutineContext: CoroutineContext): Deferred<A> =
        this.ev().thunk(coroutineContext)

fun <A> DeferredKWKind<A>.attempt(coroutineContext: CoroutineContext): DeferredResult<A> =
        runBlocking {
            try {
                this@attempt.runDeferred(coroutineContext).await().right()
            } catch (throwable: Throwable) {
                throwable.left()
            }
        }

fun <A> DeferredKWKind<A>.unsafeRun(coroutineContext: CoroutineContext): A =
        attempt(coroutineContext).fold({ throw it }, { it })

inline fun <A> DeferredKWKind<A>.handleErrorWith(crossinline function: (Throwable) -> DeferredKWKind<A>): DeferredKW<A> =
        DeferredKW { context: CoroutineContext ->
            this@handleErrorWith.attempt(context).fold(
                    { function(it).runDeferred(context) },
                    { a: A -> DeferredKW.pure(a).runDeferred(context) })
        }
