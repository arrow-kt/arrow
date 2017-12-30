package arrow.effects

import arrow.*
import arrow.core.*
import arrow.data.Try
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

fun <A> Deferred<A>.k(): DeferredKW<A> =
        DeferredKW(this)

fun <A> DeferredKWKind<A>.value(): Deferred<A> = this.ev().deferred

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class
)
data class DeferredKW<out A>(val deferred: Deferred<A>) : DeferredKWKind<A>, Deferred<A> by deferred {

    fun <B> map(f: (A) -> B): DeferredKW<B> =
            flatMap { a: A -> pure(f(a)) }

    fun <B> ap(fa: DeferredKWKind<(A) -> B>): DeferredKW<B> =
            flatMap { a -> fa.ev().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> DeferredKWKind<B>): DeferredKW<B> =
            async(Unconfined, CoroutineStart.LAZY) {
                f(await()).await()
            }.k()

    companion object {
        fun unit(): DeferredKW<Unit> =
                CompletableDeferred(Unit).k()

        fun <A> pure(a: A): DeferredKW<A> =
                CompletableDeferred(a).k()

        fun <A> suspend(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.LAZY, a: suspend () -> A): DeferredKW<A> =
                async(ctx, start) { a() }.k()

        fun <A> suspend(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.LAZY, a: () -> DeferredKW<A>): DeferredKW<A> =
                async(ctx, start) { a().await() }.k()

        operator fun <A> invoke(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.DEFAULT, a: () -> A): DeferredKW<A> =
                async(ctx, start) { a() }.k()

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
        fun <A> runAsync(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.DEFAULT, fa: Proc<A>): DeferredKW<A> =
                async(ctx, start) {
                    CompletableDeferred<A>().apply {
                        fa {
                            it.fold(this::completeExceptionally, this::complete)
                        }
                    }.await()
                }.k()

        fun <A, B> tailRecM(a: A, f: (A) -> DeferredKWKind<Either<A, B>>): DeferredKW<B> =
                f(a).value().let { initial: Deferred<Either<A, B>> ->
                    var current: Deferred<Either<A, B>> = initial
                    async(Unconfined, CoroutineStart.LAZY) {
                        val result: B
                        while (true) {
                            val actual: Either<A, B> = current.await()
                            if (actual is Either.Right) {
                                result = actual.b
                                break
                            } else if (actual is Either.Left) {
                                current = f(actual.a).ev()
                            }
                        }
                        result
                    }.k()
                }
    }
}

fun <A> DeferredKWKind<A>.handleErrorWith(f: (Throwable) -> DeferredKW<A>): DeferredKW<A> =
        async(Unconfined, CoroutineStart.LAZY) {
            Try { await() }.fold({ f(it).await() }, ::identity)
        }.k()

fun <A> DeferredKWKind<A>.unsafeAttemptSync(): Try<A> =
        Try { unsafeRunSync() }

fun <A> DeferredKWKind<A>.unsafeRunSync(): A =
        runBlocking { await() }

fun <A> DeferredKWKind<A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKW<Unit>): DeferredKW<Unit> =
        DeferredKW(Unconfined, CoroutineStart.DEFAULT) {
            unsafeRunAsync(cb.andThen { })
        }

fun <A> DeferredKWKind<A>.unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
        async(Unconfined, CoroutineStart.DEFAULT) {
            Try { await() }.fold({ cb(Left(it)) }, { cb(Right(it)) })
        }.let {
            // Deferred swallows all exceptions. How about no.
            it.invokeOnCompletion { a: Throwable? ->
                if (a != null) throw a
            }
        }

suspend fun <A> DeferredKWKind<A>.await(): A = this.ev().await()
