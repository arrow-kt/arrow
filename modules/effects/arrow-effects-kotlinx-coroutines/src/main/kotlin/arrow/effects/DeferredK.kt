package arrow.effects

import arrow.core.*
import arrow.effects.DeferredK.Generated.ConnectedGenerated
import arrow.effects.DeferredK.Generated.DefaultGenerated
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.Proc
import arrow.effects.typeclasses.ProcF
import arrow.effects.internal.unsafe
import arrow.effects.typeclasses.*
import arrow.higherkind
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.SelectClause0
import kotlinx.coroutines.selects.SelectClause1
import kotlin.coroutines.CoroutineContext

/**
 * Wraps a [Deferred] with [DeferredK]
 *
 * Note: Using this extension means that the resulting [DeferredK] will use a memoized version of [Deferred].
 *  For more Information visit the general [DeferredK] documentation.
 *
 * ```kotlin:ank:playground
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
 * ```kotlin:ank:playground
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
fun <A> CoroutineScope.asyncK(ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, f: suspend () -> A): DeferredK<A> =
  DefaultGenerated(ctx, start, this, f)

/**
 * Return the wrapped [Deferred] from a [DeferredK]
 *
 * Note: `DeferredK.value().await()` does not necessarily equal `DeferredK.await()`. That is because [DeferredK] will attempt to rerun
 *  all computation and if the code executed is not pure it may change on every await. This is important because otherwise impure code would not
 *  rerun, and its side effects never happen. This only applies to [DeferredK]'s created without `Deferred.k()`
 *  To be extra safe, one should always use the wrapper directly.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * ```kotlin:ank:playground
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
fun <A> DeferredKOf<A>.value(): Deferred<A> = fix().value()

/**
 * Awaits the value of a [DeferredK].
 *
 * Note: `DeferredK.await()` does not necessarily equal `DeferredK.await()`. That is because [DeferredK] will attempt to rerun
 *  all computation and if the code executed is not pure it may change on every await. This is important because otherwise impure code would not
 *  rerun, and its side effects never happen. This only applies to [DeferredK]'s created with `Deferred.k()`
 *  To be extra safe, one should always use the wrapper directly i.e. using [DeferredK.just], [DeferredK.raiseError], [asyncK], [DeferredK.async], etc.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * ```kotlin:ank:playground
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
suspend fun <A> DeferredKOf<A>.await(): A = value().await()

/**
 * Returns the [CoroutineScope] the [DeferredK] operates on
 *
 * ```kotlin:ank:playground
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
fun <A> DeferredKOf<A>.scope(): CoroutineScope = fix().scope

// FIXME (0.8.1)
// The calls to `Unconfined` ask for @ExperimentalCoroutinesApi, which makes using DeferredK light up
// with warnings like a Xmas tree. Unconfined or an alternative will always exist.
//
// Please avoid adding the annotation for now.
/**
 * A wrapper class for [Deferred] that either memoizes its result or re-runs the computation each time, based on how it is constructed.
 */
@higherkind
sealed class DeferredK<A>(
  internal open val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined),
  protected open var memoized: Deferred<A>
) : DeferredKOf<A>, Deferred<A> by memoized {

  abstract fun value(): Deferred<A>

  /**
   * Pure wrapper for already constructed [Deferred] instances. Created solely by `Deferred.k()` extension method
   */
  internal class Wrapped<A>(scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined), memoized: Deferred<A>) : DeferredK<A>(scope, memoized) {
    override fun value(): Deferred<A> = memoized
  }

  /**
   * Represents a [DeferredK] that can generate an instance of [Deferred] on every await
   *
   * It does not memoize results and thus can be rerun just as expected from a [MonadDefer]
   * **However** one can still break this system by ie returning or using a deferred in one of the functions
   *  only when creating all deferred instances inside DeferredK or using DeferredK's methods
   *  one can guarantee not having memoization
   */
  internal sealed class Generated<A>(
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined),
    override var memoized: Deferred<A>
  ) : DeferredK<A>(scope, memoized) {

    internal class DefaultGenerated<A>(val ctx: CoroutineContext = Dispatchers.Default,
                                       val coroutineStart: CoroutineStart = CoroutineStart.LAZY,
                                       scope: CoroutineScope = CoroutineScope(ctx),
                                       val generator: suspend () -> A) : Generated<A>(scope, scope.async(ctx, coroutineStart) { generator() }) {
      /**
       * Awaits either the memoized [Deferred] if it has not been run yet. Or creates a new one.
       *
       * @see [Deferred.await]
       */
      override suspend fun await(): A = value().await()

      /**
       * Starts either the memoized [Deferred] if it has not been run yet. Or creates a new one.
       *
       * @see [Deferred.start]
       */
      override fun start(): Boolean = value().start()

      /**
       * Returns either the memoized [Deferred] if it has not been run yet. Or creates a new one.
       */
      override fun value(): Deferred<A> {
        if (isCompleted || isActive || isCancelled) {
          memoized = scope.async(ctx, coroutineStart) {
            generator()
          }
        }

        return memoized
      }

      override fun cancel() = scope.coroutineContext.cancel()
    }

    /**
     * Represents a [DeferredK] that can generate a connection aware instance of [Deferred] on every await.
     *
     * It does not memoize results and thus can be rerun just as expected from a [MonadDefer]
     * **However** one can still break this system by ie returning or using a deferred in one of the functions
     *  only when creating all deferred instances inside DeferredK or using DeferredK's methods
     *  one can guarantee not having memoization
     *
     * @see [DeferredK.async]
     */
    // DEV DOCS: Connection aware version of `Generated`, this is necessary because the generator needs to know how to
    // internally create a new connection for a new generated `Deferred` value.
    internal class ConnectedGenerated<A>(
      val ctx: CoroutineContext = Dispatchers.Default,
      val coroutineStart: CoroutineStart = CoroutineStart.LAZY,
      scope: CoroutineScope = CoroutineScope(ctx),
      val generator: suspend CoroutineScope.(DeferredKConnection) -> A
    ) : Generated<A>(scope, wireConnection(ctx, coroutineStart, scope, generator)) {

      companion object {
        private fun <A> wireConnection(
          ctx: CoroutineContext = Dispatchers.Default,
          coroutineStart: CoroutineStart = CoroutineStart.LAZY,
          scope: CoroutineScope = CoroutineScope(ctx),
          generator: suspend CoroutineScope.(DeferredKConnection) -> A
        ): Deferred<A> {
          val conn = DeferredKConnection()
          val newScope = if (scope.coroutineContext[Job] == null) scope + Job() else scope
          val job = newScope.coroutineContext[Job]!!
          val d = newScope.async(ctx, coroutineStart) {
            generator(conn)
          }
          conn.push(DeferredK { newScope.coroutineContext.cancel() })
          job.invokeOnCompletion { e -> if (e is CancellationException) conn.cancel().unsafeRunSync() }
          return d
        }
      }

      /**
       * Awaits either the memoized [Deferred] if it has not been run yet. Or creates a new one.
       *
       * @see [Deferred.await]
       */
      override suspend fun await(): A = value().await()

      /**
       * Starts either the memoized [Deferred] if it has not been run yet. Or creates a new one.
       *
       * @see [Deferred.start]
       */
      override fun start(): Boolean = value().start()

      /**
       * Returns either the memoized [Deferred] if it has not been run yet. Or creates a new one.
       */
      override fun value(): Deferred<A> {
        if (isCompleted || isActive || isCancelled) {
          memoized = wireConnection(ctx, coroutineStart, scope, generator)
        }
        return memoized
      }

      override fun cancel() = scope.coroutineContext.cancel()

    }
  }

  /**
   * Represents the failure case of [DeferredK].
   */
  // DEV DOCS: Required because with the desired scope `CoroutineScope(Dispatchers.Unconfined)` the original implementation
  // `CompletableDeferred<A>().apply { completeExceptionally(t) }.k()` evaluates eager and thus blowing the stack of the caller of `raiseError`.
  internal class Error<A>(val error: Throwable, scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)) :
    DeferredK<A>(scope, memoized = scope.async(scope.coroutineContext, CoroutineStart.LAZY) { throw error }) {

    override fun value(): Deferred<A> =
      scope.async(scope.coroutineContext, CoroutineStart.LAZY) { throw error }
  }

  /**
   * Map over the result of the [DeferredK]
   *
   * Note: This function will always rerun when await is called. For more Information visit the general [DeferredK] documentation.
   *
   * ```kotlin:ank:playground
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
   * ```kotlin:ank:playground
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
   * ```kotlin:ank:playground
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
    is DefaultGenerated -> DefaultGenerated(ctx, coroutineStart, scope) {
      f(value().await()).await()
    }
    is ConnectedGenerated -> ConnectedGenerated(ctx, coroutineStart, scope) {
      f(value().await()).await()
    }
    is Wrapped -> DefaultGenerated(Dispatchers.Unconfined, CoroutineStart.LAZY, scope) {
      f(memoized.await()).await()
    }
    is Error -> this as DeferredK<B>
  }

  /**
   * Try-catch-finally in a function way
   *
   * Note: This function will always re-run when await is called. But the [DeferredK] returned by use or release may not be depending on how they were created.
   *  For more Information visit the general [DeferredK] documentation.
   *
   * ```kotlin:ank:playground
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
  fun <B> bracketCase(use: (A) -> DeferredKOf<B>, release: (A, ExitCase<Throwable>) -> DeferredKOf<Unit>): DeferredK<B> =
    when (this) {
      is DefaultGenerated -> DefaultGenerated(ctx, coroutineStart, scope) {
        value().bracketCase(use, release)
      }
      is Wrapped -> DefaultGenerated(Dispatchers.Unconfined, CoroutineStart.LAZY, scope) {
        value().bracketCase(use, release)
      }
      is ConnectedGenerated -> ConnectedGenerated(ctx, coroutineStart, scope) {
        value().bracketCase(use, release)
      }
      is Error -> this as DeferredK<B>
    }

  private suspend inline fun <B> Deferred<A>.bracketCase(use: (A) -> DeferredKOf<B>, release: (A, ExitCase<Throwable>) -> DeferredKOf<Unit>): B {
    val a = await()
    val b = try {
      use(a).await()
    } catch (e: Throwable) {
      try {
        if (e is CancellationException) release(a, ExitCase.Cancelled).await()
        else release(a, ExitCase.Error(e)).await()
      } catch (e2: Throwable) {
        throw Platform.composeErrors(e, e2)
      }
      throw e
    }

    release(a, ExitCase.Completed).await()
    return b
  }

  /**
   * Continue the next computation on a different [CoroutineContext].
   *
   * ```kotlin:ank:playground
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
    is DefaultGenerated -> DefaultGenerated(ctx, coroutineStart, scope) {
      scope.async(this@DeferredK.ctx, coroutineStart) {
        generator()
      }.await()
    }
    is Wrapped -> scope.asyncK(ctx, CoroutineStart.LAZY) { memoized.await() }
    is ConnectedGenerated -> ConnectedGenerated(ctx, coroutineStart, scope) {
      scope.async(this@DeferredK.ctx, coroutineStart) {
        generator(it)
      }.await()
    }
    is Error -> this
  }

  fun startF(ctx: CoroutineContext): DeferredK<Fiber<ForDeferredK, A>> = DeferredK {
    val promise = Promise.unsafe<Either<Throwable, A>>()
    val d = continueOn(ctx)
    d.start()
    d.invokeOnCompletion { e -> e?.let { promise.complete(Left(it)) } ?: promise.complete(Right(d.getCompleted())) }
    Fiber(DeferredK { promise.get.value().fold({ throw it }, ::identity) }, DeferredK { d.cancel() })
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
     * Lifts a value a into a [DeferredK] of A
     *
     * ```kotlin:ank:playground
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
     * ```kotlin:ank:playground
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
    fun <A> defer(ctx: CoroutineContext = Dispatchers.Unconfined, scope: CoroutineScope = CoroutineScope(ctx), start: CoroutineStart = CoroutineStart.LAZY, fa: () -> DeferredKOf<A>): DeferredK<A> =
      DefaultGenerated(ctx, start, scope) {
        val t = fa().fix()
        when (t) {
          is Wrapped -> Wrapped(scope, t.memoized)
          is DefaultGenerated -> DefaultGenerated(t.ctx, t.coroutineStart, scope + t.ctx, t.generator)
          is ConnectedGenerated -> ConnectedGenerated(t.ctx, t.coroutineStart, scope + t.ctx, t.generator)
          is Error -> t
        }.await()
      }

    /**
     * Wraps a suspend function in a [DeferredK]
     *
     * Note: Using this method means the resulting [DeferredK] will rerun f on await. Making this [DeferredK] re-runnable as long as f itself does not use a non re-runnable
     *  [Deferred] or [DeferredK].
     *  For more Information visit the general [DeferredK] documentation.
     *
     * ```kotlin:ank:playground
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
    operator fun <A> invoke(ctx: CoroutineContext = Dispatchers.Default, scope: CoroutineScope = CoroutineScope(ctx), start: CoroutineStart = CoroutineStart.LAZY, f: suspend () -> A): DeferredK<A> =
      DefaultGenerated(ctx, start, scope, f)

    /**
     * Wraps an existing [Deferred] in [DeferredK]
     *
     * Note: Using this method the resulting [DeferredK] will always return a memoized version on await. Side-effects will not be re-run.
     *  For more Information visit the general [DeferredK] documentation.
     *
     * ```kotlin:ank:playground
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
    operator fun <A> invoke(fa: Deferred<A>, scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)): DeferredK<A> =
      Wrapped(scope, fa)

    /**
     * Creates a failed [DeferredK] with the throwable
     *
     * ```kotlin:ank:playground
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
      Error(t)

    /**
     * Starts a coroutine that'll run [DeferredKProc].
     *
     * Matching the behavior of [asyncK],
     * its [CoroutineContext] is set to [DefaultDispatcher]
     * and its [CoroutineStart] is [CoroutineStart.LAZY].
     *
     * ```kotlin:ank:playground
     * import arrow.core.Either
     * import arrow.core.right
     * import arrow.effects.DeferredK
     * import arrow.effects.DeferredKConnection
     * import arrow.effects.unsafeAttemptSync
     *
     * class Resource {
     *   fun asyncRead(f: (String) -> Unit): Unit = f("Some value of a resource")
     *   fun close(): Unit = Unit
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = DeferredK.async { conn: DeferredKConnection, cb: (Either<Throwable, String>) -> Unit ->
     *     val resource = Resource()
     *     conn.push(DeferredK { resource.close() })
     *     resource.asyncRead { value -> cb(value.right()) }
     *   }
     *   //sampleEnd
     *   println(result.unsafeAttemptSync())
     * }
     * ```
     */
    fun <A> async(ctx: CoroutineContext = Dispatchers.Default, scope: CoroutineScope = CoroutineScope(ctx), start: CoroutineStart = CoroutineStart.LAZY, fa: DeferredKProc<A>): DeferredK<A> {
      val newScope = if (scope.coroutineContext[Job] == null) scope + Job() else scope
      val parent = newScope.coroutineContext[Job]!!
      return ConnectedGenerated(ctx, start, newScope) { conn ->
        CompletableDeferred<A>(parent).apply {
          fa(conn) { it.fold(this::completeExceptionally, this::complete) }
        }.await()
      }
    }

    fun <A> asyncF(scope: CoroutineScope = GlobalScope, ctx: CoroutineContext = Dispatchers.Default, start: CoroutineStart = CoroutineStart.LAZY, fa: DeferredKProcF<A>): DeferredK<A> {
      val newScope = if (scope.coroutineContext[Job] == null) scope + Job() else scope
      val parent = newScope.coroutineContext[Job]!!
      return ConnectedGenerated(ctx, start, scope) { conn ->
        CompletableDeferred<A>(parent).apply {
          fa(conn) { it.fold(this::completeExceptionally, this::complete) }.await()
        }.await()
      }
    }

    fun <A, B> tailRecM(a: A, f: (A) -> DeferredKOf<Either<A, B>>): DeferredK<B> =
      f(a).value().let { initial: Deferred<Either<A, B>> ->
        var current: Deferred<Either<A, B>> = initial
        val scope = CoroutineScope(Dispatchers.Unconfined)
        DefaultGenerated(scope.coroutineContext, CoroutineStart.LAZY, scope) {
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

    fun <A, B> racePair(ctx: CoroutineContext,
                        lh: DeferredKOf<A>,
                        rh: DeferredKOf<B>): DeferredK<Either<Tuple2<A, Fiber<ForDeferredK, B>>, Tuple2<Fiber<ForDeferredK, A>, B>>> =
      lh.startF(ctx).flatMap { fiberA ->
        rh.startF(ctx).flatMap { fiberB ->
          DeferredK.async<Either<Tuple2<A, Fiber<ForDeferredK, B>>, Tuple2<Fiber<ForDeferredK, A>, B>>> { conn, cb ->
            fiberA.join().fix().invokeOnCompletion { error ->
              error?.let { cb(it.left()) } ?: cb((fiberA.join().fix().getCompleted() toT fiberB).left().right())
            }
            fiberB.join().unsafeRunAsync { eith ->
              cb(eith.map { (fiberA toT it).right() })
            }
          }
        }
      }
  }

  override val children: Sequence<Job>
    get() = memoized.children
  override val isActive: Boolean
    get() = memoized.isActive
  override val isCancelled: Boolean
    get() = memoized.isCancelled
  override val isCompleted: Boolean
    get() = memoized.isCompleted
  override val key: CoroutineContext.Key<*>
    get() = memoized.key
  override val onAwait: SelectClause1<A>
    get() = memoized.onAwait
  override val onJoin: SelectClause0
    get() = memoized.onJoin

  override suspend fun await(): A =
    memoized.await()

  override fun cancel() =
    memoized.cancel()

  @ObsoleteCoroutinesApi override fun cancel(cause: Throwable?): Boolean =
    memoized.cancel(cause)

  @InternalCoroutinesApi override fun getCancellationException(): CancellationException =
    memoized.getCancellationException()

  @ExperimentalCoroutinesApi override fun getCompleted(): A =
    memoized.getCompleted()

  @ExperimentalCoroutinesApi override fun getCompletionExceptionOrNull(): Throwable? =
    memoized.getCompletionExceptionOrNull()

  @InternalCoroutinesApi
  override fun invokeOnCompletion(onCancelling: Boolean, invokeImmediately: Boolean, handler: CompletionHandler): DisposableHandle =
    memoized.invokeOnCompletion(onCancelling, invokeImmediately, handler)

  override fun invokeOnCompletion(handler: CompletionHandler): DisposableHandle =
    memoized.invokeOnCompletion(handler)

  override suspend fun join() =
    memoized.join()

  override fun start(): Boolean =
    memoized.start()

}

/**
 * Handle errors from [MonadThrow]
 *
 * Note: This function will be rerun when awaited multiple times, but the [DeferredK] returned by f might not be depending on how it was created.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * ```kotlin:ank:playground
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
fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredKOf<A>): DeferredK<A> = when (val d = this.fix()) {
  is DeferredK.Error -> f(d.error).fix()
  else -> DeferredK(Dispatchers.Unconfined, scope(), CoroutineStart.LAZY) {
    try {
      await()
    } catch (e: Throwable) {
      f(e).await()
    }
  }
}

fun <A> DeferredKOf<A>.startF(ctx: CoroutineContext): DeferredK<Fiber<ForDeferredK, A>> {
  val join = scope().asyncK(ctx = ctx, start = CoroutineStart.DEFAULT) { await() }
  val cancel = DeferredK(start = CoroutineStart.LAZY) { join.cancel() }
  return DeferredK.just(Fiber(join, cancel))
}

/**
 * Wrap [unsafeRunSync] in [Try] to catch any thrown errors
 *
 * ```kotlin:ank:playground
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
 * ```kotlin:ank:playground
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
 * ```kotlin:ank:playground
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
  DeferredK(Dispatchers.Unconfined, scope(), CoroutineStart.LAZY) {
    val result: A? = try {
      await()
    } catch (t: Throwable) {
      cb(Left(t)).await()
      null
    }

    result?.let { cb(Right(it)) }?.await() ?: Unit
  }

/**
 * Runs the [DeferredK] asynchronously and continues with the [DeferredK] returned by cb.
 * Also provides means to cancel the execution.
 *
 * Note: This and/or the [DeferredK] will only rerun properly on multiple await calls if both are created properly.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * ```kotlin:ank:playground
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
  DeferredK(Dispatchers.Unconfined, scope(), CoroutineStart.LAZY) {
    fix().forceExceptionPropagation()
    val self = this@runAsyncCancellable.fix()
    self.start()
    self.invokeOnCompletion {
      if (self.isCompleted && !self.isCancelled) {
        val exception = self.getCompletionExceptionOrNull()
        if (exception == null) cb(Right(self.getCompleted())).unsafeRunAsync { }
        else cb(Left(exception)).unsafeRunAsync { }
      }
    }

    val job = scope().coroutineContext[Job] ?: self
    val disposable: Disposable = {
      when (onCancel) {
        OnCancel.ThrowCancellationException -> job.cancel()
        OnCancel.Silent -> job.cancel()
      }
    }
    disposable
  }

fun <A> DeferredKOf<A>.unsafeRunAsyncCancellable(onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> Unit): Disposable =
  runAsyncCancellable(onCancel) { r -> DeferredK { cb(r) } }.unsafeRunSync()

/**
 * Runs the [DeferredK] asynchronously and then runs the cb.
 * Catches all errors that may be thrown in await. Errors from cb will still throw as expected.
 *
 * Note: This [DeferredK] will only rerun properly on multiple await calls if created supporting that.
 *  For more Information visit the general [DeferredK] documentation.
 *
 * ```kotlin:ank:playground
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
fun <A> DeferredKOf<A>.unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit = when (val d = this.fix()) {
  is DeferredK.Error<A> -> cb(Left(d.error))
  else -> scope().async(Dispatchers.Unconfined, CoroutineStart.DEFAULT) {
    val a = try {
      await()
    } catch (e: Throwable) {
      cb(Left(e))
      null
    }
    a?.let { cb(Right(it)) }
  }.forceExceptionPropagation()
}

// Deferred swallows all exceptions. How about no.
internal fun Deferred<*>.forceExceptionPropagation(): Unit =
  invokeOnCompletion { a: Throwable? ->
    if (a != null && a !is CancellationException) throw a
  }.let { }
