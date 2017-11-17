package kategory.effects

import kategory.*
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

fun <A> Deferred<A>.k(start: CoroutineStart = CoroutineStart.DEFAULT): DeferredKW<A> =
        DeferredKW(this, start)

fun <A> DeferredKWKind<A>.value(): Deferred<A> = this.ev().deferred

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class
)
data class DeferredKW<out A>(val deferred: Deferred<A>, val start: CoroutineStart = CoroutineStart.DEFAULT) : DeferredKWKind<A>, Deferred<A> by deferred {

    fun <B> map(f: (A) -> B): DeferredKW<B> =
            flatMap { a: A -> pure(f(a)) }

    fun <B> ap(fa: DeferredKWKind<(A) -> B>): DeferredKW<B> =
            flatMap { a -> fa.ev().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> DeferredKWKind<B>): DeferredKW<B> =
            async(start = start) {
                f(await()).await()
            }.k()

    companion object {
        fun unit(): DeferredKW<Unit> =
                CompletableDeferred(Unit).k()

        fun <A> pure(a: A): DeferredKW<A> =
                CompletableDeferred(a).k()

        fun <A> suspend(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.DEFAULT, a: () -> A): DeferredKW<A> =
                async(ctx, start) { a() }.k(start)

        operator fun <A> invoke(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.DEFAULT, a: () -> A): DeferredKW<A> =
                suspend(ctx, start, a)

        fun <A> failed(t: Throwable): DeferredKW<A> =
                CompletableDeferred<A>().apply { completeExceptionally(t) }.k()


        fun <A> raiseError(t: Throwable): DeferredKW<A> =
                failed(t)

        /**
         * Starts a coroutine that'll run [Proc].
         */
        fun <A> runAsync(ctx: CoroutineContext, start: CoroutineStart, fa: Proc<A>): DeferredKW<A> =
                async(ctx, start) {
                    CompletableDeferred<A>().apply {
                        fa {
                            it.fold(this::completeExceptionally, this::complete)
                        }
                    }.await()
                }.k(start)

        /**
         * Starts a coroutine that'll run [Proc].
         *
         * Matching the behavior of [async],
         * its [CoroutineContext] is set to [DefaultDispatcher]
         * and its [CoroutineStart] is [CoroutineStart.DEFAULT].
         */
        fun <A> runAsync(fa: Proc<A>): DeferredKW<A> =
                runAsync(DefaultDispatcher, CoroutineStart.DEFAULT, fa)


        fun <A, B> tailRecM(a: A, f: (A) -> DeferredKWKind<Either<A, B>>): DeferredKW<B> =
                f(a).value().let { initial: Deferred<Either<A, B>> ->
                    var current: Deferred<Either<A, B>> = initial
                    async(initial) {
                        val result: B?
                        while (true) {
                            val actual: Either<A, B> = current.await()
                            if (actual is Right) {
                                result = actual.b
                                break
                            } else if (actual is Left) {
                                current = f(actual.a).ev()
                            }
                        }
                        result!!
                    }.k()
                }
    }
}

fun <A> DeferredKWKind<A>.handleErrorWith(f: (Throwable) -> DeferredKW<A>): DeferredKW<A> =
        async(start = this.ev().start) {
            Try { this@handleErrorWith.await() }.fold({ f(it).await() }, ::identity)
        }.k()


fun <A> DeferredKWKind<A>.unsafeAttemptSync(): Try<A> =
        runBlocking {
            Try { this@unsafeAttemptSync.await() }
        }

fun <A> DeferredKWKind<A>.unsafeRunSync(): A =
        unsafeAttemptSync().fold({ throw it }, ::identity)

suspend fun <A> DeferredKWKind<A>.await(): A = this.ev().await()