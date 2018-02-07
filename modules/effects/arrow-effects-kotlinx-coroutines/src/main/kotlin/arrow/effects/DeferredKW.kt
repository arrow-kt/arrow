package arrow.effects

import arrow.core.*
import arrow.data.Try
import arrow.deriving
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

fun <A> Deferred<A>.k(): DeferredKW<A> =
        DeferredKW(this)

fun <A> DeferredKWOf<A>.value(): Deferred<A> = this.reify().deferred

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class
)
data class DeferredKW<out A>(val deferred: Deferred<A>) : DeferredKWOf<A>, Deferred<A> by deferred {

    fun <B> map(f: (A) -> B): DeferredKW<B> =
            flatMap { a: A -> pure(f(a)) }

    fun <B> ap(fa: DeferredKWOf<(A) -> B>): DeferredKW<B> =
            flatMap { a -> fa.reify().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> DeferredKWOf<B>): DeferredKW<B> =
            kotlinx.coroutines.experimental.async(Unconfined, CoroutineStart.LAZY) {
                f(await()).await()
            }.k()

    companion object {
        fun unit(): DeferredKW<Unit> =
                CompletableDeferred(Unit).k()

        fun <A> pure(a: A): DeferredKW<A> =
                CompletableDeferred(a).k()

        fun <A> suspend(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.LAZY, f: suspend () -> A): DeferredKW<A> =
                kotlinx.coroutines.experimental.async(ctx, start) { f() }.k()

        fun <A> suspend(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.LAZY, fa: () -> DeferredKWOf<A>): DeferredKW<A> =
                kotlinx.coroutines.experimental.async(ctx, start) { fa().await() }.k()

        operator fun <A> invoke(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.DEFAULT, f: () -> A): DeferredKW<A> =
                kotlinx.coroutines.experimental.async(ctx, start) { f() }.k()

        fun <A> failed(t: Throwable): DeferredKW<A> =
                CompletableDeferred<A>().apply { completeExceptionally(t) }.k()

        fun <A> raiseError(t: Throwable): DeferredKW<A> =
                failed(t)

        /**
         * Starts a coroutine that'll run [Proc].
         *
         * Matching the behavior of [async],
         * its [CoroutineContext] is set to [DefaultDispatcher]
         * and its [CoroutineStart] is [CoroutineStart.DEFAULT].
         */
        fun <A> async(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.DEFAULT, fa: Proc<A>): DeferredKW<A> =
                kotlinx.coroutines.experimental.async(ctx, start) {
                    CompletableDeferred<A>().apply {
                        fa {
                            it.fold(this::completeExceptionally, this::complete)
                        }
                    }.await()
                }.k()

        fun <A, B> tailRecM(a: A, f: (A) -> DeferredKWOf<Either<A, B>>): DeferredKW<B> =
                f(a).value().let { initial: Deferred<Either<A, B>> ->
                    var current: Deferred<Either<A, B>> = initial
                    kotlinx.coroutines.experimental.async(Unconfined, CoroutineStart.LAZY) {
                        val result: B
                        while (true) {
                            val actual: Either<A, B> = current.await()
                            if (actual is Either.Right) {
                                result = actual.b
                                break
                            } else if (actual is Either.Left) {
                                current = f(actual.a).reify()
                            }
                        }
                        result
                    }.k()
                }
    }
}

fun <A> DeferredKWOf<A>.handleErrorWith(f: (Throwable) -> DeferredKW<A>): DeferredKW<A> =
        async(Unconfined, CoroutineStart.LAZY) {
            Try { await() }.fold({ f(it).await() }, ::identity)
        }.k()

fun <A> DeferredKWOf<A>.unsafeAttemptSync(): Try<A> =
        Try { unsafeRunSync() }

fun <A> DeferredKWOf<A>.unsafeRunSync(): A =
        runBlocking { await() }

fun <A> DeferredKWOf<A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKWOf<Unit>): DeferredKW<Unit> =
        DeferredKW(Unconfined, CoroutineStart.DEFAULT) {
            unsafeRunAsync(cb.andThen { })
        }

fun <A> DeferredKWOf<A>.unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
        async(Unconfined, CoroutineStart.DEFAULT) {
            Try { await() }.fold({ cb(Left(it)) }, { cb(Right(it)) })
        }.let {
            // Deferred swallows all exceptions. How about no.
            it.invokeOnCompletion { a: Throwable? ->
                if (a != null) throw a
            }
        }

suspend fun <A> DeferredKWOf<A>.await(): A = this.reify().await()
