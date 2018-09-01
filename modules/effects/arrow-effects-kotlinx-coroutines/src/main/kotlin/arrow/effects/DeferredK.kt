package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import arrow.typeclasses.Traverse
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun <A> Deferred<A>.k(): DeferredK<A> =
  DeferredK(this)

fun <A> DeferredKOf<A>.value(): Deferred<A> = this.fix().deferred

@higherkind
data class DeferredK<out A>(val deferred: Deferred<A>) : DeferredKOf<A>, Deferred<A> by deferred {

  fun <B> map(f: (A) -> B): DeferredK<B> =
    flatMap { a: A -> just(f(a)) }

  fun <B> ap(fa: DeferredKOf<(A) -> B>): DeferredK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> DeferredKOf<B>): DeferredK<B> =
    kotlinx.coroutines.async(Unconfined, CoroutineStart.LAZY) {
      f(await()).await()
    }.k()

  fun continueOn(ctx: CoroutineContext): DeferredK<A> =
    kotlinx.coroutines.async(ctx, CoroutineStart.LAZY) {
      deferred.await()
    }.k()

  companion object {
    fun unit(): DeferredK<Unit> =
      CompletableDeferred(Unit).k()

    fun <A> just(a: A): DeferredK<A> =
      CompletableDeferred(a).k()

    fun <A> defer(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.LAZY, f: suspend () -> A): DeferredK<A> =
      kotlinx.coroutines.async(ctx, start) { f() }.k()

    fun <A> defer(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.LAZY, fa: () -> DeferredKOf<A>): DeferredK<A> =
      kotlinx.coroutines.async(ctx, start) { fa().await() }.k()

    operator fun <A> invoke(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.DEFAULT, f: () -> A): DeferredK<A> =
      kotlinx.coroutines.async(ctx, start) { f() }.k()

    fun <A> failed(t: Throwable): DeferredK<A> =
      CompletableDeferred<A>().apply { completeExceptionally(t) }.k()

    fun <A> raiseError(t: Throwable): DeferredK<A> =
      failed(t)

    /**
     * Starts a coroutine that'll run [Proc].
     *
     * Matching the behavior of [async],
     * its [CoroutineContext] is set to [DefaultDispatcher]
     * and its [CoroutineStart] is [CoroutineStart.DEFAULT].
     */
    fun <A> async(ctx: CoroutineContext = DefaultDispatcher, start: CoroutineStart = CoroutineStart.DEFAULT, fa: Proc<A>): DeferredK<A> =
      kotlinx.coroutines.async(ctx, start) {
        CompletableDeferred<A>().apply {
          fa {
            it.fold(this::completeExceptionally, this::complete)
          }
        }.await()
      }.k()

    fun <A, B> tailRecM(a: A, f: (A) -> DeferredKOf<Either<A, B>>): DeferredK<B> =
      f(a).value().let { initial: Deferred<Either<A, B>> ->
        var current: Deferred<Either<A, B>> = initial
        kotlinx.coroutines.async(Unconfined, CoroutineStart.LAZY) {
          val result: B
          while (true) {
            val actual: Either<A, B> = current.await()
            if (actual is Either.Right) {
              result = actual.b
              break
            } else if (actual is Either.Left) {
              current = f(actual.a).fix()
            }
          }
          result
        }.k()
      }
  }
}

fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredK<A>): DeferredK<A> =
  async(Unconfined, CoroutineStart.LAZY) {
    Try { await() }.fold({ f(it).await() }, ::identity)
  }.k()

fun <A> DeferredKOf<A>.unsafeAttemptSync(): Try<A> =
  Try { unsafeRunSync() }

fun <A> DeferredKOf<A>.unsafeRunSync(): A =
  runBlocking { await() }

fun <A> DeferredKOf<A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
  DeferredK(Unconfined, CoroutineStart.DEFAULT) {
    unsafeRunAsync(cb.andThen { })
  }

fun <A> DeferredKOf<A>.unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
  async(Unconfined, CoroutineStart.DEFAULT) {
    Try { await() }.fold({ cb(Left(it)) }, { cb(Right(it)) })
  }.let {
    // Deferred swallows all exceptions. How about no.
    it.invokeOnCompletion { a: Throwable? ->
      if (a != null) throw a
    }
  }

suspend fun <A> DeferredKOf<A>.await(): A = this.fix().await()

suspend fun <F, A> Kind<F, DeferredKOf<A>>.awaitAll(T: Traverse<F>): Kind<F, A> = T.run {
    this@awaitAll.sequence(DeferredK.applicative()).await()
}
