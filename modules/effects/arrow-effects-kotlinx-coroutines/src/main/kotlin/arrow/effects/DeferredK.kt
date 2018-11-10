package arrow.effects

import arrow.core.*
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.Proc
import arrow.higherkind
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

  fun <B> flatMap(scope: CoroutineScope = GlobalScope, f: (A) -> DeferredKOf<B>): DeferredK<B> =
    scope.async(Dispatchers.Unconfined, CoroutineStart.LAZY) {
      f(await()).await()
    }.k()

  fun continueOn(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext): DeferredK<A> =
    scope.async(ctx, CoroutineStart.LAZY) {
      deferred.await()
    }.k()

  companion object {
    fun unit(): DeferredK<Unit> =
      CompletableDeferred(Unit).k()

    fun <A> just(a: A): DeferredK<A> =
      CompletableDeferred(a).k()

    fun <A> defer(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, f: suspend () -> A): DeferredK<A> =
      scope.async(ctx, start) { f() }.k()

    fun <A> defer(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, fa: () -> DeferredKOf<A>): DeferredK<A> =
      scope.async(ctx, start) { fa().await() }.k()

    operator fun <A> invoke(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.DEFAULT, f: () -> A): DeferredK<A> =
      scope.async(ctx, start) { f() }.k()

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
    fun <A> async(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.DEFAULT, fa: Proc<A>): DeferredK<A> =
      scope.async(ctx, start) {
        CompletableDeferred<A>().apply {
          fa {
            it.fold(this::completeExceptionally, this::complete)
          }
        }.await()
      }.k()

    fun <A, B> tailRecM(a: A, f: (A) -> DeferredKOf<Either<A, B>>): DeferredK<B> =
      f(a).value().let { initial: Deferred<Either<A, B>> ->
        var current: Deferred<Either<A, B>> = initial
        GlobalScope.async(Dispatchers.Unconfined, CoroutineStart.LAZY) {
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

fun <A> DeferredKOf<A>.handleErrorWith(scope: CoroutineScope = GlobalScope, f: (Throwable) -> DeferredK<A>): DeferredK<A> =
  scope.async(Dispatchers.Unconfined, CoroutineStart.LAZY) {
    Try { await() }.fold({ f(it).await() }, ::identity)
  }.k()

fun <A> DeferredKOf<A>.unsafeAttemptSync(): Try<A> =
  Try { unsafeRunSync() }

fun <A> DeferredKOf<A>.unsafeRunSync(): A =
  runBlocking { await() }

fun <A> DeferredKOf<A>.runAsync(scope: CoroutineScope = GlobalScope, cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
  scope.async(Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
    forceExceptionPropagation()
    unsafeRunAsync(this, cb.andThen { it.unsafeRunAsync(this) { } })
  }.k()

fun <A> DeferredKOf<A>.runAsyncCancellable(scope: CoroutineScope = GlobalScope, onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Disposable> =
  scope.async(Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
    forceExceptionPropagation()
    val call = CompletableDeferred<Unit>(parent = runAsync(scope, cb))
    val disposable: Disposable = {
      when (onCancel) {
        OnCancel.ThrowCancellationException -> call.completeExceptionally(OnCancel.CancellationException)
        OnCancel.Silent -> call.cancel()
      }
    }
    disposable
  }.k()

fun <A> DeferredKOf<A>.unsafeRunAsync(scope: CoroutineScope = GlobalScope, cb: (Either<Throwable, A>) -> Unit): Unit =
  scope.async(Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
    Try { await() }.fold({ cb(Left(it)) }, { cb(Right(it)) })
  }.forceExceptionPropagation()

private fun DeferredKOf<*>.forceExceptionPropagation(): Unit =
  fix().deferred.forceExceptionPropagation()

private fun Deferred<*>.forceExceptionPropagation(): Unit =
// Deferred swallows all exceptions. How about no.
  invokeOnCompletion { a: Throwable? ->
    if (a != null) throw a
  }.let { }

suspend fun <A> DeferredKOf<A>.await(): A = this.fix().await()