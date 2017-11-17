package kategory.effects

import kategory.*
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

fun <A> Deferred<A>.k(): DeferredKW<A> = DeferredKW(this)

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
            async {
                f(await()).await()
            }.k()

    companion object {
        fun unit(): DeferredKW<Unit> =
                DeferredKW(CompletableDeferred(Unit))

        fun <A> pure(a: A): DeferredKW<A> =
                DeferredKW(CompletableDeferred(a))

        fun <A> async(a: () -> A, ctx: CoroutineContext = CommonPool): DeferredKW<A> =
                DeferredKW(async(ctx) { a() })

        fun <A> failed(t: Throwable): DeferredKW<A> =
                CompletableDeferred<A>().apply { completeExceptionally(t) }.k()


        fun <A> raiseError(t: Throwable): DeferredKW<A> =
                failed(t)

        fun <A> runAsync(fa: Proc<A>): DeferredKW<A> =
                CompletableDeferred<A>().apply {
                    fa {
                        it.fold(this::completeExceptionally, this::complete)
                    }
                }.k()

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
        async {
            Try { this@handleErrorWith.await() }.fold({ f(it).await() }, ::identity)
        }.k()


fun <A> DeferredKWKind<A>.unsafeAttemptSync(): Try<A> =
        runBlocking {
            Try { this@unsafeAttemptSync.await() }
        }

fun <A> DeferredKWKind<A>.unsafeRunSync(): A =
        unsafeAttemptSync().fold({ throw it }, ::identity)

suspend fun <A> DeferredKWKind<A>.await(): A = this.ev().await()