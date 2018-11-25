package arrow.effects

import arrow.core.*
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Wraps a [Deferred] with [DeferredK]
 *
 * Note: Using this extension means that the resulting [DeferredK] will use a memoized version of [Deferred].
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.DeferredK
 * import arrow.effects.k
 * import arrow.effects.unsafeRunSync
 * import kotlinx.coroutines.GlobalScope
 * import kotlinx.coroutines.async
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: DeferredK<String> = GlobalScope.async {
 *     // some computation ...
 *     "Done"
 *   }.k()
 *   //sampleEnd
 *   println(result.unsafeRunSync())
 * }
 *  ```
 */
fun <A> Deferred<A>.k(): DeferredK<A> =
  DeferredK.Wrapped(memoized = this)

/**
 * Wrap a suspend function in a [DeferredK] given a context and a start method
 *
 * Note: Using this extension means that the resulting [DeferredK] will rerun f on every await, hence
 *  awaiting a new coroutine every time. This means it won't be memoized and as such can be used for repeated/retried actions and
 *  it will properly re-execute side-effects.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.DeferredK
 * import arrow.effects.asyncK
 * import arrow.effects.unsafeRunSync
 * import kotlinx.coroutines.GlobalScope
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: DeferredK<String> = GlobalScope.asyncK {
 *     // some computation ...
 *     "Done"
 *   }
 *   //sampleEnd
 *   println(result.unsafeRunSync())
 * }
 * ```
 */
fun <A> CoroutineScope.asyncK(ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, f: suspend CoroutineScope.() -> A): DeferredK<A> =
  DeferredK.Generated(ctx, start, this) { f() }

/**
 * Return the wrapped [Deferred] from a [DeferredK]
 *
 * Note: `aDeferredK.value().await()` does not necessarily equal `aDeferredK.await()`. That is because [DeferredK] will attempt to rerun
 *  all computation and if the code executed is not pure it may change on every await. This is important because otherwise impure code would not
 *  rerun, and its side effects never happen. This only applies to [DeferredK]'s created without `Deferred.k()`
 */
fun <A> DeferredKOf<A>.value(): Deferred<A> = this.fix().memoized

/**
 * Returns the [CoroutineScope] the [DeferredK] operates on
 */
fun <A> DeferredKOf<A>.scope(): CoroutineScope = this.fix().scope

@higherkind
@ExperimentalCoroutinesApi
sealed class DeferredK<A>(
  open val scope: CoroutineScope = GlobalScope,
  open val memoized: Deferred<A>
) : DeferredKOf<A>, Deferred<A> by memoized {

  /**
   * Pure wrapper for already constructed [Deferred] instances. Created solely by `Deferred.k()` extension method
   */
  internal data class Wrapped<A>(override val scope: CoroutineScope = GlobalScope, override val memoized: Deferred<A>) : DeferredK<A>(scope = scope, memoized = memoized)

  /**
   * Represents a [DeferredK] that can generate an instance of [Deferred] on every await
   *
   * It does not memoize results and thus can be rerun just as expected from a [MonadDefer]
   * However one can still break this system by ie returning or using a deferred in one of the functions
   *  only when creating all deferred instances inside DeferredK or using DeferredK's methods
   *  one can guarantee not having memoization
   */
  internal data class Generated<A>(
    val ctx: CoroutineContext = Dispatchers.Default,
    val coroutineStart: CoroutineStart = CoroutineStart.LAZY,
    override val scope: CoroutineScope = GlobalScope,
    val generator: suspend () -> A
  ) : DeferredK<A>(scope, scope.async(ctx, coroutineStart) { generator() }) {

    /**
     * Returns either the memoized [Deferred] if it has not been run yet. Or creates a new one.
     */
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
        DeferredK.raiseError<B>(e)
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

    /**
     * [DeferredK] that just contains [Unit]
     *
     * Useful for comprehensions to avoid executing code at the beginning of a binding.
     *
     * This below executes `println("Stuff")` despite the resulting [DeferredK] never being executed itself.
     * To avoid that call [bind] on a [DeferredK] before that. The second binding won't print a thing before run.
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.DeferredK
     * import arrow.effects.deferredk.monad.monad
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   DeferredK.monad().binding {
     *     println("Stuff")
     *     DeferredK { 10 }.bind()
     *   }
     *
     *   DeferredK.monad().binding {
     *     DeferredK.unit().bind()
     *     println("Other Stuff")
     *     DeferredK { 10 }.bind()
     *   }
     *   //sampleEnd
     * }
     * ```
     */
    fun unit(): DeferredK<Unit> = just(Unit)

    fun <A> just(a: A): DeferredK<A> = CompletableDeferred(a).k()

    /**
     * Wraps a function that returns a [DeferredK] in a [DeferredK] together with a specific [CoroutineScope], a [CoroutineContext] and a
     *  [CoroutineStart]. All those parameters (except f) have defaults.
     *
     * Note: Using this method means the resulting [DeferredK] will rerun fa on await, not memoizing its result.
     *  As long as the result of fa is also re-runnable, this [DeferredK] this [DeferredK] will correctly re-run.
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.DeferredK
     * import arrow.effects.unsafeRunSync
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = DeferredK.defer {
     *     println("Calculating solution:")
     *     DeferredK.just(42)
     *   }
     *   //sampleEnd
     *   println(result.unsafeRunSync())
     * }
     * ```
     */
    fun <A> defer(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Unconfined, start: CoroutineStart = CoroutineStart.LAZY, fa: () -> DeferredKOf<A>): DeferredK<A> =
      Generated(ctx, start, scope) { fa().await() }

    /**
     * Wraps a suspend function in a [DeferredK] together with a specific [CoroutineScope], a [CoroutineContext] and a
     *  [CoroutineStart]. All those parameters (except f) have defaults.
     *
     * Note: Using this method means the resulting [DeferredK] will rerun f on await. Making this [DeferredK] re-runnable as long as f itself does not use a non re-runnable
     *  [Deferred] or [DeferredK].
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.DeferredK
     * import arrow.effects.unsafeRunSync
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = DeferredK {
     *     println("Calculating solution:")
     *     42
     *   }
     *   //sampleEnd
     *   println(result.unsafeRunSync())
     * }
     * ```
     */
    operator fun <A> invoke(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, f: suspend () -> A): DeferredK<A> =
      Generated(ctx, start, scope, f)

    fun <A> raiseError(t: Throwable): DeferredK<A> =
      CompletableDeferred<A>().apply { completeExceptionally(t) }.k()

    /**
     * Starts a coroutine that'll run [Proc].
     *
     * Matching the behavior of [async],
     * its [CoroutineContext] is set to [DefaultDispatcher]
     * and its [CoroutineStart] is [CoroutineStart.LAZY].
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
