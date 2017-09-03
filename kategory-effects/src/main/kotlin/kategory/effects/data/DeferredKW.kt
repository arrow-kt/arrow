package kategory

import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext

@higherkind
@deriving(Monad::class, AsyncContext::class)
class DeferredKW<out A>(val ctx: CoroutineContext, val deferred: Deferred<A>) : DeferredKWKind<A> {

    fun <B> map(f: (A) -> B): DeferredKW<B> =
      flatMap { a: A -> pure(f(a)) }

    fun <B> flatMap(f: (A) -> DeferredKWKind<B>): DeferredKW<B> =
        if (!deferred.isActive && deferred.isCompleted && !deferred.isCompletedExceptionally) {
            f(deferred.getCompleted()).ev()
        } else {
            val promise = CompletableDeferred<B>()
            deferred.invokeOnCompletion { t ->
                if (deferred.isCompletedExceptionally && t != null) promise.completeExceptionally(t)
                else {
                    val db = f(deferred.getCompleted())
                    val continuation = db.ev().deferred
                    continuation.invokeOnCompletion { t ->
                        if (continuation.isCompletedExceptionally && t != null) promise.completeExceptionally(t)
                        else promise.complete(continuation.getCompleted())
                    }
                }
            }
            DeferredKW(ctx, promise)
        }

    companion object {

        fun unit(): DeferredKW<Unit> =
                DeferredKW(CommonPool, CompletableDeferred(Unit))

        fun <A> pure(a: A): DeferredKW<A> =
                DeferredKW(CommonPool, CompletableDeferred(a))

        fun <A> async(a: () -> A, ctx: CoroutineContext = CommonPool): DeferredKW<A> =
                DeferredKW(ctx, kotlinx.coroutines.experimental.async(ctx) { a() })

        fun <A> failed(t: Throwable): DeferredKW<A> {
            val promise = CompletableDeferred<A>()
            promise.completeExceptionally(t)
            return DeferredKW(CommonPool, promise)
        }


        fun <A> raiseError(t: Throwable): DeferredKW<A> =
                failed(t)

        fun <A> runAsync(fa: Proc<A>, ctx: CoroutineContext = CommonPool): DeferredKW<A> {
            val promise = CompletableDeferred<A>()
            fa {
                it.fold({ promise.completeExceptionally(it) }, { promise.complete(it) })
            }
            return DeferredKW(ctx, promise)
        }

        fun <A, B> tailRecM(a: A, f: (A) -> DeferredKWKind<Either<A, B>>): DeferredKW<B> {
            fun loop(witness: CompletableDeferred<B>, a: A, f: (A) -> DeferredKWKind<Either<A, B>>): Unit {
                val d = f(a).ev().deferred
                d.invokeOnCompletion { t ->
                    if (d.isCompletedExceptionally && t != null) witness.completeExceptionally(t)
                    else {
                        val eitherResult = d.getCompleted()
                        when (eitherResult) {
                            is Either.Left ->
                                kotlinx.coroutines.experimental.async(CommonPool) {
                                    loop(witness, eitherResult.a, f)
                                }
                            is Either.Right -> witness.complete(eitherResult.b)
                        }
                    }
                }
            }
            val promise = CompletableDeferred<B>()
            kotlinx.coroutines.experimental.async(CommonPool) { loop(promise, a, f) }
            return DeferredKW(CommonPool, promise)
        }

        fun functor(): DeferredKWHKMonadInstance = object : DeferredKWHKMonadInstance {}

        fun applicative(): DeferredKWHKMonadInstance = object : DeferredKWHKMonadInstance {}

        fun monadError(): DeferredKWHKMonadErrorInstance = object : DeferredKWHKMonadErrorInstance, GlobalInstance<MonadError<DeferredKWHK, Throwable>>() {}

    }
}

fun <A> DeferredKWKind<A>.handleErrorWith(f: (Throwable) -> DeferredKWKind<A>): DeferredKW<A> {
    val dthis = this.ev()
    val promise = CompletableDeferred<A>()
    dthis.deferred.invokeOnCompletion { t ->
        if (dthis.deferred.isCompletedExceptionally && t != null) {
            val nested = f(t).ev().deferred
            nested.invokeOnCompletion { x ->
                if (nested.isCompletedExceptionally && x != null) {
                    promise.completeExceptionally(x)
                } else {
                    promise.complete(nested.getCompleted())
                }
            }
        }
        else promise.complete(dthis.deferred.getCompleted())
    }
    return DeferredKW(dthis.ctx, promise)
}

fun <A> DeferredKWKind<A>.unsafeAttemptSync(): Try<A> =
        runBlocking {
            Try { this@unsafeAttemptSync.ev().deferred.await() }
        }

fun <A> DeferredKWKind<A>.unsafeRunSync(): A =
        unsafeAttemptSync().fold({ throw it }, { it })