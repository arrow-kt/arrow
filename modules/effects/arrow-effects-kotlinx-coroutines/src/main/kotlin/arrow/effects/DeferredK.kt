package arrow.effects

import arrow.core.*
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun <A> Deferred<A>.k(): DeferredK<A> =
  DeferredK.Wrapped(memoized = this)

fun <A> CoroutineScope.asyncK(ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, f: suspend CoroutineScope.() -> A): DeferredK<A> =
  DeferredK.Generated(ctx, start, this) { f() }

fun <A> DeferredKOf<A>.value(): Deferred<A> = this.fix().memoized

fun <A> DeferredKOf<A>.scope(): CoroutineScope = this.fix().scope

@higherkind
@ExperimentalCoroutinesApi
sealed class DeferredK<A>(
  val scope: CoroutineScope = GlobalScope,
  val memoized: Deferred<A>
) : DeferredKOf<A>, Deferred<A> by memoized {

  /**
   * Pure wrapper for already constructed deferred instances, does nothing special really
   */
  class Wrapped<A>(scope: CoroutineScope = GlobalScope, memoized: Deferred<A>) : DeferredK<A>(scope = scope, memoized = memoized)

  /**
   * Represents a DeferredK that can generate an instance of deferred on every await
   *
   * It does not memoize results and thus can be rerun just as expected from a MonadDefer
   * However one can still break this system by ie returning or using a deferred in one of the functions
   *  only when creating all deferred instances inside DeferredK or using DeferredK's methods
   *  one can guarantee not having memoization
   */
  class Generated<A>(
    val ctx: CoroutineContext = Dispatchers.Default,
    val coroutineStart: CoroutineStart = CoroutineStart.LAZY,
    scope: CoroutineScope = GlobalScope,
    val generator: suspend () -> A
  ) : DeferredK<A>(scope, scope.async(ctx, coroutineStart) { generator() }) {

    override suspend fun await(): A =
      if (memoized.isCompleted || memoized.isActive || memoized.isCancelled) {
        scope.async(ctx, coroutineStart) { generator() }.await()
      } else {
        memoized.await()
      }
  }

  fun <B> map(f: (A) -> B): DeferredK<B> =
    flatMap { a: A -> just(f(a)) }

  fun <B> ap(fa: DeferredKOf<(A) -> B>): DeferredK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> DeferredKOf<B>): DeferredK<B> = when (this) {
    is Generated -> Generated(ctx, coroutineStart, scope) {
      f(
        scope.async(ctx, coroutineStart) { generator() }.await()
      ).await()
    }
    is Wrapped -> Generated(Dispatchers.Unconfined, CoroutineStart.LAZY, scope) {
      f(
        memoized.await()
      ).await()
    }
  }

  fun <B> bracketCase(use: (A) -> DeferredK<B>, release: (A, ExitCase<Throwable>) -> DeferredK<Unit>): DeferredK<B> =
    flatMap { a ->
      try {
        use(a).also { release(a, ExitCase.Completed) }
      } catch (e: Exception) {
        release(a, ExitCase.Error(e))
        DeferredK.failed<B>(e)
      }
    }

  fun continueOn(ctx: CoroutineContext): DeferredK<A> = when (this) {
    is Generated -> Generated(ctx, coroutineStart, scope) {
      scope.async(this@DeferredK.ctx, coroutineStart) {
        generator()
      }.await()
    }
    is Wrapped -> scope.asyncK(ctx, CoroutineStart.LAZY) { memoized.await() }
  }

  override fun equals(other: Any?): Boolean =
    when (other) {
      is DeferredK<*> -> this.memoized == other.memoized
      is Deferred<*> -> this.memoized == other
      else -> false
    }

  override fun hashCode(): Int = memoized.hashCode()

  companion object {
    fun unit(): DeferredK<Unit> = just(Unit)

    fun <A> just(a: A): DeferredK<A> = CompletableDeferred(a).k()

    fun <A> defer(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, fa: () -> DeferredKOf<A>): DeferredK<A> =
      Generated(ctx, start, scope) { fa().await() }

    operator fun <A> invoke(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, f: suspend () -> A): DeferredK<A> =
      Generated(ctx, start, scope, f)

    fun <A> raiseError(t: Throwable): DeferredK<A> =
      failed(t)

    fun <A> failed(t: Throwable): DeferredK<A> =
      CompletableDeferred<A>().apply { completeExceptionally(t) }.k()

    /**
     * Starts a coroutine that'll run [Proc].
     *
     * Matching the behavior of [async],
     * its [CoroutineContext] is set to [DefaultDispatcher]
     * and its [CoroutineStart] is [CoroutineStart.DEFAULT].
     */
    fun <A> async(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, fa: Proc<A>): DeferredK<A> =
      Generated(ctx, start, scope) {
        CompletableDeferred<A>().apply {
          fa {
            it.fold(this::completeExceptionally, this::complete)
          }
        }.await()
      }

    fun <A, B> tailRecM(a: A, f: (A) -> DeferredKOf<Either<A, B>>): DeferredK<B> =
      f(a).value().let { initial: Deferred<Either<A, B>> ->
        var current: Deferred<Either<A, B>> = initial
        Generated(Dispatchers.Unconfined, CoroutineStart.LAZY, GlobalScope) {
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
        }
      }
  }
}

fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredK<A>): DeferredK<A> =
  DeferredK.Generated(Dispatchers.Unconfined, CoroutineStart.LAZY) {
    Try { await() }.fold({ f(it).await() }, ::identity)
  }

fun <A> DeferredKOf<A>.unsafeAttemptSync(): Try<A> =
  Try { unsafeRunSync() }

fun <A> DeferredKOf<A>.unsafeRunSync(): A =
  runBlocking { await() }

fun <A> DeferredKOf<A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
  DeferredK.invoke(scope(), Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
    fix().forceExceptionPropagation()
    unsafeRunAsync(cb.andThen { it.unsafeRunAsync { } })
  }

fun <A> DeferredKOf<A>.runAsyncCancellable(onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Disposable> =
  DeferredK.invoke(scope(), Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
    fix().forceExceptionPropagation()
    val call = CompletableDeferred<Unit>(parent = runAsync(cb))
    val disposable: Disposable = {
      when (onCancel) {
        OnCancel.ThrowCancellationException -> call.completeExceptionally(OnCancel.CancellationException)
        OnCancel.Silent -> call.cancel()
      }
    }
    disposable
  }

fun <A> DeferredKOf<A>.unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
  scope().async(Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
    Try { await() }.fold({ cb(Left(it)) }, { cb(Right(it)) })
  }.forceExceptionPropagation()

private fun Deferred<*>.forceExceptionPropagation(): Unit =
// Deferred swallows all exceptions. How about no.
  invokeOnCompletion { a: Throwable? ->
    if (a != null) throw a
  }.let { }

suspend fun <A> DeferredKOf<A>.await(): A = this.fix().await()
