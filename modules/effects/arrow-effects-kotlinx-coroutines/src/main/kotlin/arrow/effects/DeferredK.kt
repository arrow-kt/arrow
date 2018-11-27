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
 *  For more Information visit the general [DeferredK] documentation.
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
 *  For more Information visit the general [DeferredK] documentation.
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
 *  To be extra safe, one should always use the wrapper directly.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.DeferredK
 * import arrow.effects.value
 * import kotlinx.coroutines.Deferred
 * import kotlinx.coroutines.runBlocking
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: Deferred<String> = DeferredK {
 *     // some computation ...
 *     "Done"
 *   }.value()
 *   //sampleEnd
 *   runBlocking {
 *     println(result.await())
 *   }
 * }
 * ```
 */
fun <A> DeferredKOf<A>.value(): Deferred<A> = this.fix().memoized

/**
 * Returns the [CoroutineScope] the [DeferredK] operates on
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.DeferredK
 * import arrow.effects.scope
 * import kotlinx.coroutines.CoroutineScope
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val scope: CoroutineScope = DeferredK.just(1).scope()
 *   println(scope)
 *   //sampleEnd
 * }
 * ```
 */
fun <A> DeferredKOf<A>.scope(): CoroutineScope = this.fix()._scope

/**
 * A wrapper class for [Deferred] that either memoizes its result or re-runs the computation each time, based on how it is constructed.
 */
@higherkind
@ExperimentalCoroutinesApi
sealed class DeferredK<A>(
  internal val _scope: CoroutineScope = GlobalScope,
  internal val memoized: Deferred<A>
) : DeferredKOf<A>, Deferred<A> by memoized {

  /**
   * Pure wrapper for already constructed [Deferred] instances. Created solely by `Deferred.k()` extension method
   */
  internal class Wrapped<A>(scope: CoroutineScope = GlobalScope, memoized: Deferred<A>) : DeferredK<A>(_scope = scope, memoized = memoized)

  /**
   * Represents a [DeferredK] that can generate an instance of [Deferred] on every await
   *
   * It does not memoize results and thus can be rerun just as expected from a [MonadDefer]
   * However one can still break this system by ie returning or using a deferred in one of the functions
   *  only when creating all deferred instances inside DeferredK or using DeferredK's methods
   *  one can guarantee not having memoization
   */
  internal class Generated<A>(
    val ctx: CoroutineContext = Dispatchers.Default,
    val coroutineStart: CoroutineStart = CoroutineStart.LAZY,
    scope: CoroutineScope = GlobalScope,
    val generator: suspend () -> A
  ) : DeferredK<A>(scope, scope.async(ctx, coroutineStart) { generator() }) {

    /**
     * Returns either the memoized [Deferred] if it has not been run yet. Or creates a new one.
     */
    override suspend fun await(): A =
      if (memoized.isCompleted || memoized.isActive || memoized.isCancelled) {
        _scope.async(ctx, coroutineStart) { generator() }.await()
      } else {
        memoized.await()
      }
  }

  /**
   * Map over the result of the [DeferredK]
   *
   * Note: This function will always rerun when await is called. For more Information visit the general [DeferredK] documentation.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.DeferredK
   * import arrow.effects.unsafeRunSync
   *
   * fun main(args: Array<String>) {
   *    //sampleStart
   *   val result: DeferredK<String> = DeferredK.just(1).map {
   *     it.toString()
   *   }
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   */
  fun <B> map(f: (A) -> B): DeferredK<B> =
    flatMap { a: A -> just(f(a)) }

  /**
   * Apply a function inside a [DeferredK] to the result of this [DeferredK]
   *
   * Note: This function inside will always be rerun when await is called, but the [DeferredK] the function comes from
   *  might not be depending on how it was created.
   *  For more Information visit the general [DeferredK] documentation.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.DeferredK
   * import arrow.effects.unsafeRunSync
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val other: DeferredK<(Int) -> String> = DeferredK {
   *     { i: Int -> "The number is $i" }
   *   }
   *
   *   val result: DeferredK<String> = DeferredK.just(1).ap(other)
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   */
  fun <B> ap(fa: DeferredKOf<(A) -> B>): DeferredK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  /**
   * Maps over the value of the [DeferredK] and flattens the returned [DeferredK]
   *
   * Note: This function will always rerun when await is called. However the [DeferredK] returned from it might not, depending on how it was created.
   *  For more Information visit the general [DeferredK] documentation.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.DeferredK
   * import arrow.effects.unsafeRunSync
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result: DeferredK<Int> = DeferredK.just(1).flatMap {
   *     DeferredK {
   *       // some time consuming task
   *       it * 31
   *     }
   *   }
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   */
  fun <B> flatMap(f: (A) -> DeferredKOf<B>): DeferredK<B> = when (this) {
    is Generated -> Generated(ctx, coroutineStart, _scope) {
      f(
        _scope.async(ctx, coroutineStart) { generator() }.await()
      ).await()
    }
    is Wrapped -> Generated(Dispatchers.Unconfined, CoroutineStart.LAZY, _scope) {
      f(
        memoized.await()
      ).await()
    }
  }

  /**
   * Try-catch-finally in a function way
   *
   * Note: This function will always re-run when await is called. But the [DeferredK] returned by use or release may not be depending on how they were created.
   *  For more Information visit the general [DeferredK] documentation.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.DeferredK
   * import arrow.effects.deferredk.bracket.bracket
   * import arrow.Kind
   * import arrow.effects.typeclasses.Bracket
   * import arrow.effects.unsafeRunSync
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   override fun toString(): String = "This file contains some interesting content!"
   * }
   *
   * class Program<F>(BF: Bracket<F, Throwable>) : Bracket<F, Throwable> by BF {
   *
   *   fun openFile(uri: String): Kind<F, File> = just(File(uri).open())
   *
   *   fun closeFile(file: File): Kind<F, Unit> = just(file.close())
   *
   *   fun fileToString(file: File): Kind<F, String> = just(file.toString())
   * }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val deferredProgram = Program(DeferredK.bracket())
   *
   *   val safeComputation = with (deferredProgram) {
   *   openFile("data.json").bracket(
   *     release = { file -> closeFile(file) },
   *     use = { file -> fileToString(file) })
   *   }
   *   //sampleEnd
   *   println(safeComputation.unsafeRunSync())
   * }
   */
  fun <B> bracketCase(use: (A) -> DeferredK<B>, release: (A, ExitCase<Throwable>) -> DeferredK<Unit>): DeferredK<B> =
    flatMap { a ->
      try {
        use(a).also { release(a, ExitCase.Completed) }
      } catch (e: Exception) {
        release(a, ExitCase.Error(e))
        DeferredK.raiseError<B>(e)
      }
    }

  /**
   * Continue the next computation on a different [CoroutineContext].
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.DeferredK
   * import arrow.effects.unsafeRunSync
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = DeferredK.just(1)
   *     .continueOn(Dispatchers.IO)
   *     .map { println("This is now on IO") }
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   */
  fun continueOn(ctx: CoroutineContext): DeferredK<A> = when (this) {
    is Generated -> Generated(ctx, coroutineStart, _scope) {
      _scope.async(this@DeferredK.ctx, coroutineStart) {
        generator()
      }.await()
    }
    is Wrapped -> _scope.asyncK(ctx, CoroutineStart.LAZY) { memoized.await() }
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
     * Creates a [DeferredK] that just contains [Unit]
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

    /**
     * Lifts a value a into a [DeferredK] of A
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.DeferredK
     * import arrow.effects.unsafeRunSync
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = DeferredK.just(1)
     *   //sampleEnd
     *   println(result.unsafeRunSync())
     * }
     * ```
     */
    fun <A> just(a: A): DeferredK<A> = CompletableDeferred(a).k()

    /**
     * Wraps a function that returns a [DeferredK] in a [DeferredK].
     *
     * Note: Using this method means the resulting [DeferredK] will rerun fa on await, not memoizing its result.
     *  As long as the result of fa is also re-runnable, this [DeferredK] this [DeferredK] will correctly re-run.
     *  For more Information visit the general [DeferredK] documentation.
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
     * Wraps a suspend function in a [DeferredK]
     *
     * Note: Using this method means the resulting [DeferredK] will rerun f on await. Making this [DeferredK] re-runnable as long as f itself does not use a non re-runnable
     *  [Deferred] or [DeferredK].
     *  For more Information visit the general [DeferredK] documentation.
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

    /**
     * Wraps an existing [Deferred] in [DeferredK]
     *
     * Note: Using this method the resulting [DeferredK] will always return a memoized version on await. Side-effects will not be re-run.
     *  For more Information visit the general [DeferredK] documentation.
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.DeferredK
     * import arrow.effects.unsafeRunSync
     * import kotlinx.coroutines.GlobalScope
     * import kotlinx.coroutines.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = DeferredK(
     *     GlobalScope.async { 42 }
     *   )
     *   //sampleEnd
     *   println(result.unsafeRunSync())
     * }
     * ```
     */
    operator fun <A> invoke(fa: Deferred<A>, scope: CoroutineScope = GlobalScope): DeferredK<A> =
      Wrapped(scope, fa)

    /**
     * Creates a failed [DeferredK] with the throwable
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.DeferredK
     * import arrow.effects.unsafeAttemptSync
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result: DeferredK<String> = DeferredK.raiseError<String>(Exception("BOOM"))
     *   //sampleEnd
     *   println(result.unsafeAttemptSync())
     * }
     * ```
     */
    fun <A> raiseError(t: Throwable): DeferredK<A> =
      CompletableDeferred<A>().apply { completeExceptionally(t) }.k()

    /**
     * Starts a coroutine that'll run [Proc].
     *
     * Matching the behavior of [async],
     * its [CoroutineContext] is set to [DefaultDispatcher]
     * and its [CoroutineStart] is [CoroutineStart.LAZY].
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.core.Either
     * import arrow.core.left
     * import arrow.effects.DeferredK
     * import arrow.effects.unsafeAttemptSync
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = DeferredK.async { cb: (Either<Throwable, Int>) -> Unit ->
     *     cb(
     *       Exception("BOOM").left()
     *     )
     *   }
     *   //sampleEnd
     *   println(result.unsafeAttemptSync())
     * }
     * ```
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

/**
 * Handle errors from [MonadThrow]
 *
 * Note: This function will be rerun when awaited multiple times, but the [DeferredK] returned by f might not be depending on how it was created.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.DeferredK
 * import arrow.effects.unsafeAttemptSync
 * import arrow.effects.handleErrorWith
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: DeferredK<String> = DeferredK.raiseError<String>(Exception("BOOM"))
 *     .handleErrorWith { t: Throwable ->
 *       DeferredK.just(t.toString())
 *     }
 *   //sampleEnd
 *   println(result.unsafeAttemptSync())
 * }
 * ```
 */
fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredK<A>): DeferredK<A> =
  DeferredK.Generated(Dispatchers.Unconfined, CoroutineStart.LAZY) {
    Try { await() }.fold({ f(it).await() }, ::identity)
  }

/**
 * Wrap [unsafeRunSync] in [Try] to catch any thrown errors
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.DeferredK
 * import arrow.effects.unsafeAttemptSync
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: DeferredK<String> = DeferredK.raiseError<String>(Exception("BOOM"))
 *   //sampleEnd
 *   println(result.unsafeAttemptSync())
 * }
 * ```
 */
fun <A> DeferredKOf<A>.unsafeAttemptSync(): Try<A> =
  Try { unsafeRunSync() }

/**
 * Runs this [DeferredK] with [runBlocking]. Does not handle errors at all, rethrowing them if they happen.
 * Use [unsafeAttemptSync] if they should be caught automatically.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.DeferredK
 * import arrow.effects.unsafeRunSync
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: DeferredK<String> = DeferredK.raiseError<String>(Exception("BOOM"))
 *   //sampleEnd
 *   println(result.unsafeRunSync())
 * }
 * ```
 */
fun <A> DeferredKOf<A>.unsafeRunSync(): A =
  runBlocking { await() }

/**
 * Runs the [DeferredK] asynchronously and continues with the [DeferredK] returned by cb.
 *
 * Note: This and/or the [DeferredK] will only rerun properly on multiple await calls if both are created properly.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.core.Either
 * import arrow.effects.DeferredK
 * import arrow.effects.unsafeRunSync
 * import arrow.effects.runAsync
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   DeferredK.just(1).runAsync { either: Either<Throwable, Int> ->
 *     either.fold({ t: Throwable ->
 *       DeferredK.raiseError<Unit>(t)
 *     }, { i: Int ->
 *       DeferredK { println("DONE WITH $i") }
 *     })
 *   }
 *   //sampleEnd
 * }
 * ```
 */
fun <A> DeferredKOf<A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
  DeferredK(scope(), Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
    fix().forceExceptionPropagation()
    unsafeRunAsync(cb.andThen { it.unsafeRunAsync { } })
  }

/**
 * Runs the [DeferredK] asynchronously and continues with the [DeferredK] returned by cb.
 * Also provides means to cancel the execution.
 *
 * Note: This and/or the [DeferredK] will only rerun properly on multiple await calls if both are created properly.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.core.Either
 * import arrow.effects.DeferredK
 * import arrow.effects.unsafeAttemptSync
 * import arrow.effects.typeclasses.Disposable
 * import arrow.effects.runAsyncCancellable
 * import kotlinx.coroutines.delay
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result = DeferredK.just(1).runAsyncCancellable { either: Either<Throwable, Int> ->
 *     DeferredK { delay(100) }.map { println("DONE") }
 *   }.map { dispose: Disposable -> dispose() }
 *   //sampleEnd
 *   println(result.unsafeAttemptSync())
 * }
 * ```
 */
fun <A> DeferredKOf<A>.runAsyncCancellable(onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Disposable> =
  DeferredK(scope(), Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
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

/**
 * Runs the [DeferredK] asynchronously and then runs the cb.
 * Catches all errors that may be thrown in await. Errors from cb will still throw as expected.
 *
 * Note: This [DeferredK] will only rerun properly on multiple await calls if created supporting that.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.core.Either
 * import arrow.effects.DeferredK
 * import arrow.effects.unsafeRunAsync
 * import kotlinx.coroutines.delay
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   DeferredK.just(1).unsafeRunAsync { either: Either<Throwable, Int> ->
 *     either.fold({ t: Throwable ->
 *       println(t)
 *     }, { i: Int ->
 *       println("DONE WITH $i")
 *     })
 *   }
 *   //sampleEnd
 * }
 * ```
 */
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
