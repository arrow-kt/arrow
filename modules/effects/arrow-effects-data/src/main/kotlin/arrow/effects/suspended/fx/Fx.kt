package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.Eval
import arrow.core.NonFatal
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.andThen
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import arrow.data.AndThen
import arrow.effects.KindConnection
import arrow.effects.OnCancel
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.ConnectedProc
import arrow.effects.typeclasses.ConnectedProcF
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.Duration
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.mapUnit
import arrow.higherkind
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

typealias FxProc<A> = ConnectedProc<ForFx, A>
typealias FxProcF<A> = ConnectedProcF<ForFx, A>

suspend inline operator fun <A> FxOf<A>.not(): A = fix().invoke()

fun <A> FxOf<A>.handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> =
  Fx.FlatMap(this, FxFrame.Companion.ErrorHandler(f))

fun <A> FxOf<A>.handleError(f: (Throwable) -> A): Fx<A> = when (this) {
  is Fx.RaiseError -> Fx { f(error) }
  is Fx.Pure -> this
  else -> Fx.FlatMap(this, FxFrame.Companion.ErrorHandler(f.andThen { Fx.Pure<A>(it) }))
}

@higherkind
sealed class Fx<out A> : FxOf<A> {

  /**
   * The _suspended_ form of this [Fx].
   * `Fx<A> -> (suspend () -> A)`
   *
   * ```kotlin:ank:playground
   * import arrow.effects.suspended.fx
   *
   * fun main(args: Array<String>) = runBlocking<Unit> {
   *   val result =
   *   //sampleStart
   *   val f: suspend () -> String = Fx.just("Hello World!").suspended
   *   //sampleEnd
   *   println(f())
   * }
   * ```
   */
  inline val suspended: suspend () -> A
    get() = when (this) {
      is RaiseError -> suspend { throw error }
      is Pure -> suspend { value }
      is Single -> suspend { source() }
      is Lazy -> suspend { source(Unit) }
      is Defer -> suspend { FxRunLoop(thunk()) }
      is Map<*, A> -> suspend { FxRunLoop(this) }
      is FlatMap<*, A> -> suspend { FxRunLoop(this) }
      is UpdateContext -> suspend { FxRunLoop(this) }
      is ContinueOn -> suspend { FxRunLoop(this) }
      is ConnectionSwitch -> suspend { FxRunLoop(this) }
      is Async -> suspend { FxRunLoop(this) }
      is AsyncContinueOn -> suspend { FxRunLoop(this) }
      is AsyncUpdateContext -> suspend { FxRunLoop(this) }
    }

  /**
   * Transform the [Fx] wrapped value of [A] into [B] preserving the [Fx] structure.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.suspended.fx.Fx
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Fx.just("Hello").map { "$it World" }
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(result))
   * }
   * ```
   */
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> map(noinline f: (A) -> B): Fx<B> = when (this) {
    is RaiseError -> this
    is Pure -> Lazy { f(value) }
    is Single -> Map(this, f)
    is Lazy -> {
      this as Lazy<Any?>
      this.source = AndThen.invoke(source).andThen(f as (Any?) -> B)
      unsafeRecast()
    }
    is Defer -> Map(this, f)
    is Map<*, *> -> {
      this as Map<A, Any?>
      this.g = AndThen(g).andThen(f as (Any?) -> B)
      unsafeRecast()
    }
    is FlatMap<*, *> -> {
      // If we reach the maxStackDepthSize then we can fold the current FlatMap and return a Map case
      // If we haven't reached the maxStackDepthSize we fuse the map operator within the flatMap stack.
      (this as FlatMap<Any?, Any?>)
      if (index != Platform.maxStackDepthSize) Map(this, f)
      else {
        this.fb = { a ->
          when (val fa = fb(a).fix()) {
            is Pure -> {
              (fa).internalValue = f(fa.value as A)
              fa.unsafeRecast<B>()
            }
            is RaiseError -> fa
            is Lazy -> {
              try {
                fa.source = { f(fa.source(Unit) as A) }
                fa
              } catch (e: Throwable) {
                RaiseError(e.nonFatalOrThrow())
              }
            }
            else -> Map(fb(a), f as (Any?) -> B)
          }
        }
        unsafeRecast()
      }
    }
    is UpdateContext -> Map(this, f)
    is ContinueOn -> Map(this, f)
    is ConnectionSwitch -> Map(this, f)
    is Async -> Map(this, f)
    is AsyncContinueOn -> Map(this, f)
    is AsyncUpdateContext -> Map(this, f)
  }

  /**
   * Transform the [Fx] value of [A] by sequencing an effect [Fx] that results in [B].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.suspended.fx.Fx
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Fx.just("Hello").flatMap { Fx { "$it World" } }
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(result))
   * }
   * ```
   */
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> flatMap(noinline f: (A) -> FxOf<B>): Fx<B> = when (this) {
    is RaiseError -> this
    is Pure -> Defer { f(value) }
    is Single -> FlatMap(this, f)
    is Lazy -> Defer { f(source(Unit)) }
    is Defer -> FlatMap(this, f)
    is Map<*, *> -> {
      val gg = this.g as (Any?) -> A
      FlatMap(source, { f(gg(it)) }, 1)
    }
    is FlatMap<*, *> -> {

      if (index != Platform.maxStackDepthSize) FlatMap(this, f)
      else FlatMap(source, { a ->
        val fbb = fb as (Any?) -> FxOf<A>
        when (val fx: Fx<A> = fbb(a).fix()) {
          is Pure -> f(fx.value)
          is RaiseError -> fx
          is Lazy -> {
            try {
              f(fx.source(Unit))
            } catch (e: Throwable) {
              RaiseError(e.nonFatalOrThrow())
            }
          }
          else -> FlatMap(fx, f)
        }
      }, index + 1)
    }
    is UpdateContext -> FlatMap(this, f)
    is ContinueOn -> FlatMap(this, f)
    is ConnectionSwitch -> FlatMap(this, f)
    is Async -> FlatMap(this, f)
    is AsyncContinueOn -> FlatMap(this, f)
    is AsyncUpdateContext -> FlatMap(this, f)
  }

  /**
   * Transform the [Fx] by sequencing an effect [Fx] that results in [B] while ignoring the original value of [A].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.suspended.fx.Fx
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Fx.just("Hello").followedBy(Fx { "World" })
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun <B> followedBy(fa: FxOf<B>): Fx<B> =
    flatMap { fa }

  /**
   * Redeem an [Fx] to an [Fx] of [B] by resolving the error **or** mapping the value [A] to [B].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.suspended.fx.Fx
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Fx.raiseError<Int>(RuntimeException("Hello from Error"))
   *     .redeem({ e -> e.message!! }, Int::toString)
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun <B> redeem(fe: (Throwable) -> B, fs: (A) -> B): Fx<B> =
    FlatMap(this, FxFrame.Companion.Redeem(fe, fs))

  /**
   * Redeem an [Fx] to an [Fx] of [B] by resolving the error **or** mapping the value [A] to [B] **with** an effect.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.suspended.fx.Fx
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Fx.just("1")
   *     .redeemWith({ e -> Fx { -1 } }, { str -> Fx { str.toInt() } })
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun <B> redeemWith(fe: (Throwable) -> FxOf<B>, fs: (A) -> FxOf<B>): Fx<B> =
    FlatMap(this, FxFrame.Companion.RedeemWith(fe, fs))

  /**
   * Discards the [A] value inside [Fx] signaling this container may be pointing to a no-op
   * or an effect whose return value is deliberately ignored. The singleton value [Unit] serves as signal.
   *
   * `Fx<A> -> Fx<Unit>
   *
   * ```kotlin:ank:playground:extension
   * import arrow.effects.suspended.fx.Fx
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Fx.just("Hello World").unit()
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun unit(): Fx<Unit> = map(mapUnit)

  /**
   * Meant for specifying tasks with safe resource acquisition and release in the face of errors and interruption.
   * It would be the the equivalent of `try/catch/finally` statements in mainstream imperative languages for resource
   * acquisition and release.
   *
   * @param release is the action that's supposed to release the allocated resource after `use` is done, irregardless
   * of its exit condition.
   *
   * ```kotlin:ank:playground:extension
   * import arrow.effects.suspended.fx.Fx
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   override fun toString(): String = "This file contains some interesting content!"
   * }
   *
   * fun openFile(uri: String): Fx<File> = Fx { File(uri).open() }
   * fun closeFile(file: File): Fx<Unit> = Fx { file.close() }
   * fun fileToString(file: File): Fx<String> = Fx { file.toString() }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val safeComputation = openFile("data.json").bracket({ file: File -> closeFile(file) }, { file -> fileToString(file) })
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(safeComputation))
   * }
   * ```
   */
  fun <B> bracket(release: (A) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> =
    bracketCase({ a, _ -> release(a) }, use)

  /**
   * Executes the given `finalizer` when the source is finished, either in success or in error, or if canceled, allowing
   * for differentiating between exit conditions. That's thanks to the [ExitCase] argument of the finalizer.
   *
   * As best practice, it's not a good idea to release resources via `guaranteeCase` in polymorphic code.
   * Prefer [bracketCase] for the acquisition and release of resources.
   *
   * @see [guarantee] for the simpler version
   * @see [bracketCase] for the more general operation
   *
   */
  fun guaranteeCase(release: (ExitCase<Throwable>) -> FxOf<Unit>): Fx<A> = async { conn, cb ->
    Platform.trampoline {
      val frame = GuaranteeReleaseFrame<A>(release)
      val onNext = FlatMap(this, frame)
      // Registering our cancelable token ensures that in case cancellation is detected, `release` gets called
      conn.push(frame.cancel)

      // Race condition check, avoiding starting `source` in case the connection was already cancelled — n.b. we don't need
      // to trigger `release` otherwise, because it already happened
      if (conn.isNotCanceled()) FxRunLoop.startCancelable(onNext, conn, cb = cb)
      else Unit
    }
  }

  /**
   * Executes the given [finalizer] when the source is finished, either in success or in error, or if canceled.
   *
   * As best practice, prefer [bracket] for the acquisition and release of resources.
   *
   * @see [guaranteeCase] for the version that can discriminate between termination conditions
   * @see [bracket] for the more general operation
   */
  fun guarantee(finalizer: FxOf<Unit>): Fx<A> =
    guaranteeCase { finalizer }

  fun <B> ap(ff: FxOf<(A) -> B>): Fx<B> =
    ff.fix().flatMap { map(it) }

  fun ensure(
    error: () -> Throwable,
    predicate: (A) -> Boolean
  ): Fx<A> = when (this) {
    is RaiseError -> this
    is Pure -> if (!predicate(value)) RaiseError(error()) else this
    else -> flatMap { result ->
      if (!predicate(result)) RaiseError(error())
      else Pure<A>(result)
    }
  }

  fun attempt(): Fx<Either<Throwable, A>> =
    FlatMap(this, FxFrame.attempt())

  fun updateContext(f: (CoroutineContext) -> CoroutineContext): Fx<A> =
    UpdateContext(this, f)

  /**
   * Continue the evaluation on provided [CoroutineContext]
   *
   * @param ctx [CoroutineContext] to run evaluation on
   *
   * ```kotlin:ank:playground:extension
   * import arrow.effects.suspended.fx.Fx
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = Fx.unit.continueOn(Dispatchers.Default).flatMap {
   *     Fx { Thread.currentThread().name }
   *   }
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun continueOn(ctx: CoroutineContext): Fx<A> = when (this) {
    is ContinueOn -> this.apply {
      this.ctx = ctx
    }
    else -> ContinueOn(this, ctx)
  }

  /**
   * Create a new [Fx] that upon execution starts the source [Fx] within a [Fiber] on [ctx].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.suspended.fx.Fx
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val program = Fx { "Running inside a fiber" }
   *     .fork(Dispatchers.Default).flatMap { (join, cancel) ->
   *       join
   *     }
   *   //sampleEnd
   *   println(Fx.unsafeRunBlocking(program))
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [Fx] on.
   * @return [Fx] with suspended execution of source [Fx] on context [ctx].
   */
  fun fork(ctx: CoroutineContext): Fx<Fiber<ForFx, A>> = async { oldConn, cb ->
    val promise = UnsafePromise<A>()
    val conn = FxConnection()
    oldConn.push(conn.cancel())
    conn.push(oldConn.cancel())

    suspend {
      suspendCoroutine { ca: Continuation<A> ->
        FxRunLoop.startCancelable(this, conn, ctx) { either: Either<Throwable, A> ->
          either.fold({ error ->
            ca.resumeWith(Result.failure(error))
          }, { a ->
            ca.resumeWith(Result.success(a))
          })
        }
      }
    }.startCoroutine(asyncContinuation(ctx) { either ->
      promise.complete(either)
    })

    cb(Right(FxFiber(promise, conn)))
  }

  /**
   * Make [Fx] uncancelable and switches back to the original cancellation connection after running.
   */
  fun uncancelable(): Fx<A> =
    ConnectionSwitch(this, ConnectionSwitch.makeUncancelable, { _, _, old, _ -> old })

  companion object {

    /**
     * Wraps a suspend function into [Fx].
     *
     * @param fa suspended function to wrap into [Fx].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Fx { throw RuntimeException("NUKES MISFIRED...BOOOOOM!!") }
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    operator fun <A> invoke(fa: suspend () -> A): Fx<A> = Single(fa)

    /**
     * Delay a computation on provided [CoroutineContext].
     *
     * @param ctx [CoroutineContext] to run evaluation on.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     * import kotlinx.coroutines.Dispatchers
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Fx(Dispatchers.Default) { Thread.currentThread().name }
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    operator fun <A> invoke(ctx: CoroutineContext, f: () -> A): Fx<A> =
      Map(UpdateContext(unit) { ctx }) { f() }

    /**
     * Wrap a pure value [A] into [Fx].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Fx.just("Hello World")
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> just(a: A): Fx<A> = Pure(a)

    /**
     * A pure [Fx] value of [Unit].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Fx.unit
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    val unit: Fx<Unit> = Pure(Unit)

    /**
     * A lazy [Fx] value of [Unit].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Fx.lazy
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    val lazy: Fx<Unit> = Lazy { Unit }

    /**
     * Wraps a pure function in a lazy manner
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   fun longCalculation(): Int = 9999
     *   //sampleStart
     *   val result = Fx.lazy { longCalculation() }
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> lazy(f: (Unit) -> A): Fx<A> = Lazy(f)

    /**
     * Task that never finishes evaluating.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val fint: Fx<Int> = Fx.never
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(fint))
     * }
     * ```
     */
    @JvmStatic
    val never: Fx<Nothing> = async { _, _ -> Unit }

    /**
     * Evaluates an [Eval] instance within a safe [Fx] context.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   fun longCalculation(): Int = 9999
     *   //sampleStart
     *   val result = Fx.eval(Eval.later { longCalculation() })
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> eval(eval: Eval<A>): Fx<A> = when (eval) {
      is Eval.Now -> just(eval.value)
      else -> Lazy { eval.value() }
    }

    /**
     * Raise an error in a pure way without actually throwing.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result: Fx<Int> = Fx.raiseError<Int>(RuntimeException("Boom"))
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> raiseError(e: Throwable): Fx<A> = RaiseError(e)

    /**
     * Defer a computation that results in an [Fx] value.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Fx.defer { Fx { Thread.currentThread().name } }
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      Defer(fa)

    /**
     * Defer a computation on provided [CoroutineContext].
     *
     * @param ctx [CoroutineContext] to run evaluation on.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     * import kotlinx.coroutines.Dispatchers
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Fx.defer(Dispatchers.Default) { Fx { Thread.currentThread().name } }
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> defer(ctx: CoroutineContext, f: () -> FxOf<A>): Fx<A> =
      FlatMap(ContinueOn(unit, ctx), { f() })

    /**
     * Perform a recursive operation in a stack-safe way.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.suspended.fx.Fx
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Fx.tailRecM(0) { i ->
     *     Fx.just(
     *      if(i == 5000) Right(i)
     *      else Left(i + 1)
     *     )
     *   }
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> =
      FlatMap(f(a), { result ->
        result.fold({ tailRecM(it, f) }, { just(it) })
      }, 0)

    /**
     * Creates a cancelable instance of [Fx] that executes an asynchronous process on evaluation.
     * This combinator can be used to wrap callbacks or other similar impure code that requires cancellation code.
     *
     * ```kotlin:ank:playground:extension
     * import arrow.effects.suspended.fx.Fx
     * import java.lang.RuntimeException
     *
     * typealias Callback = (List<String>?, Throwable?) -> Unit
     *
     * class Id
     * object GithubService {
     *   private val listeners: MutableMap<Id, Callback> = mutableMapOf()
     *   fun getUsernames(callback: (List<String>?, Throwable?) -> Unit): Id {
     *     val id = Id()
     *     listeners[id] = callback
     *     //execute operation and call callback at some point in future
     *     return id
     *   }
     *
     *   fun unregisterCallback(id: Id): Unit {
     *     listeners.remove(id)
     *   }
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   fun getUsernames(): Fx<List<String>> =
     *     Fx.async { conn: FxConnection, cb: (Either<Throwable, List<String>>) -> Unit ->
     *       val id = GithubService.getUsernames { names, throwable ->
     *         when {
     *           names != null -> cb(Right(names))
     *           throwable != null -> cb(Left(throwable))
     *           else -> cb(Left(RuntimeException("Null result and no exception")))
     *         }
     *       }
     *
     *       conn.push(Fx { GithubService.unregisterCallback(id) })
     *       conn.push(Fx { println("Everything we push to the cancellation stack will execute on cancellation") })
     *     }
     *
     *   val result = getUsernames()
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     *
     * @param fa an asynchronous computation that might fail typed as [ConnectedProc].
     * @see asyncF for a version that can suspend side effects in the registration function.
     */
    @JvmStatic
    fun <A> async(fa: FxProc<A>): Fx<A> = Async(fa)

    /**
     * Creates a cancelable instance of [Fx] that executes an asynchronous process on evaluation.
     * This combinator can be used to wrap callbacks or other similar impure code that requires cancellation code.
     *
     * ```kotlin:ank:playground:extension
     * import arrow.effects.suspended.fx.Fx
     * import java.lang.RuntimeException
     *
     * typealias Callback = (List<String>?, Throwable?) -> Unit
     *
     * class Id
     * object GithubService {
     *   private val listeners: MutableMap<Id, Callback> = mutableMapOf()
     *   fun getUsernames(callback: (List<String>?, Throwable?) -> Unit): Id {
     *     val id = Id()
     *     listeners[id] = callback
     *     //execute operation and call callback at some point in future
     *     return id
     *   }
     *
     *   fun unregisterCallback(id: Id): Unit {
     *     listeners.remove(id)
     *   }
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   fun getUsernames(): Fx<List<String>> =
     *     Fx.asyncF { conn: FxConnection, cb: (Either<Throwable, List<String>>) -> Unit ->
     *       Fx {
     *         val id = GithubService.getUsernames { names, throwable ->
     *           when {
     *             names != null -> cb(Right(names))
     *             throwable != null -> cb(Left(throwable))
     *             else -> cb(Left(RuntimeException("Null result and no exception")))
     *           }
     *         }
     *
     *         conn.push(Fx { GithubService.unregisterCallback(id) })
     *         conn.push(Fx { println("Everything we push to the cancellation stack will execute on cancellation") })
     *       }
     *     }
     *
     *   val result = getUsernames()
     *   //sampleEnd
     *   println(Fx.unsafeRunBlocking(result))
     * }
     * ```
     *
     * @param fa a deferred asynchronous computation that might fail typed as [ConnectedProcF].
     * @see async for a version that can suspend side effects in the registration function.
     */
    @JvmStatic
    fun <A> asyncF(fa: FxProcF<A>): Fx<A> = Async.invokeF(fa)

    /**
     * [unsafeRunBlocking] allows you to run any [Fx] to its wrapped value [A].
     *
     * It's called unsafe because it immediately runs the effects wrapped in [Fx],
     * and thus is **not** referentially transparent.
     *
     * **NOTE** this function is intended for testing, it should never appear in your mainline production code!
     *
     * @param fx the [Fx] to run
     * @return the resulting value
     * @see [unsafeRunBlocking] or [unsafeRunNonBlockingCancellable] that run the value as [Either].
     * @see [runNonBlocking] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> unsafeRunBlocking(fx: FxOf<A>): A = when (val current = fx.fix()) {
      is Single -> UnsafePromise<A>().run {
        current.source.startCoroutine(asyncContinuation(EmptyCoroutineContext) {
          complete(it)
        })
        await()
      }
      else -> unsafeRunTimed(fx, Duration.INFINITE)
        .fold({ throw IllegalArgumentException("IO execution should yield a valid result") }, ::identity)
    }

    /**
     * Run [fx] with a limitation on how long to await *individual* async results.
     * It's possible that this methods runs forever i.e. for an infinite recursive [Fx].
     *
     * **NOTE** this function is intended for testing, it should never appear in your mainline production code!
     *
     * @see unsafeRunBlocking
     */
    @JvmStatic
    fun <A> unsafeRunTimed(fx: FxOf<A>, limit: Duration): Option<A> = when (val current = fx.fix()) {
      is RaiseError -> throw current.error
      is Pure -> Some(current.value)
      is Lazy -> Some(current.source(Unit))
      else -> when (val result = FxRunLoop.step(fx)) {
        is Pure -> Some(result.value)
        is RaiseError -> throw result.error
        is Lazy -> Some(result.source(Unit))
        else -> Platform.unsafeResync(fx.fix(), limit)
      }
    }

    /**
     * [unsafeRunNonBlocking] allows you to run any [Fx] to its wrapped value [A].
     *
     * It receives the values in a callback [cb] and thus **has** the ability to run `NonBlocking` but that depends on the implementation.
     * When the underlying effects/program runs blocking on the callers thread this method will run blocking.
     *
     * To start this on `NonBlocking` use `Fx.unsafeRunNonBlocking(NonBlocking.shift().followedBy(fx))`.
     *
     * @param fx the [Fx] to run
     * @param cb the callback that is called with the computations result represented as an [Either].
     * @see [unsafeRunNonBlockingCancellable] to run in a cancellable manner.
     * @see [runNonBlocking] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> unsafeRunNonBlocking(fx: FxOf<A>, cb: (Either<Throwable, A>) -> Unit): Unit =
      FxRunLoop.start(fx, cb = cb)

    /**
     * [runNonBlocking] allows you to run any [Fx] to its wrapped value [A] in a referential transparent manner.
     *
     * Reason it can happen in a referential transparent manner is because nothing is actually running when this method is invoked.
     * The combinator can be used to define how several programs have to run in a safe manner.
     *
     * ```
     * val programOne = Fx { 1 }
     * val programTwo = Fx { 2 }
     * val programThree = Fx { 3 }
     *
     * Fx.runNonBlocking(programOne, cb)
     * ```
     */
    @JvmStatic
    fun <A> runNonBlocking(fx: FxOf<A>, cb: (Either<Throwable, A>) -> Fx<Unit>): Fx<Unit> = Fx {
      FxRunLoop.start(fx, cb = cb.andThen { unsafeRunBlocking(it) })
    }

    /**
     * [unsafeRunNonBlockingCancellable] allows you to run any [Fx] to its wrapped value [A] in a cancellable manner.
     *
     * It receives the values in a callback [cb] and thus **has** the ability to run `NonBlocking` but that depends on the implementation.
     * When the underlying effects/program runs blocking on the callers thread this method will run blocking.
     *
     * @param fx the [Fx] to run
     * @param cb the callback that is called with the computations result represented as an [Either].
     * @return a [Disposable] that can be used to cancel the computation.
     * @see [unsafeRunNonBlocking] to run in a non-cancellable manner.
     * @see [runNonBlockingCancellable] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> unsafeRunNonBlockingCancellable(fx: FxOf<A>, onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> Unit): Disposable =
      unsafeRunBlocking(runNonBlockingCancellable(fx, onCancel, cb.andThen { unit }))

    /**
     * A pure version of [unsafeRunNonBlockingCancellable], which defines how an [Fx] is ran in a cancelable manner but it doesn't run yet.
     *
     * It receives the values in a callback [cb] and thus **has** the ability to run `NonBlocking` but that depends on the implementation.
     * When the underlying effects/program runs blocking on the callers thread this method will run blocking.
     *
     * @param fx the [Fx] to run
     * @param cb the callback that is called with the computations result represented as an [Either].
     * @return a [Disposable] that can be used to cancel the computation.
     * @see [unsafeRunNonBlocking] to run in a non-cancellable manner.
     * @see [unsafeRunNonBlockingCancellable] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> runNonBlockingCancellable(fx: FxOf<A>, onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> Fx<Unit>): Fx<Disposable> =
      async { _ /* The start of this execution is immediate and uncancelable */, cbb ->
        val conn = FxConnection()
        val onCancelCb =
          when (onCancel) {
            OnCancel.ThrowCancellationException -> cb.andThen { unsafeRunNonBlocking(it, mapUnit) }
            OnCancel.Silent -> { either ->
              either.fold(
                { if (conn.isNotCanceled() || it != OnCancel.CancellationException) unsafeRunNonBlocking(cb(either), mapUnit) },
                { unsafeRunNonBlocking(cb(either), mapUnit) })
            }
          }

        cbb(Right(conn.toDisposable()))
        FxRunLoop.startCancelable(fx, conn, cb = onCancelCb)
      }
  }

  override fun toString(): String = "Fx(...)"

  @PublishedApi
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  internal inline fun <B> unsafeRecast(): Fx<B> = this as Fx<B>

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  @PublishedApi
  internal suspend inline operator fun invoke(): A = when (this) {
    is RaiseError -> throw error
    is Pure -> value
    is Single -> source()
    is Lazy -> source(Unit)
    is Defer -> FxRunLoop(this)
    is Map<*, *> -> FxRunLoop(this)
    is FlatMap<*, *> -> FxRunLoop(this)
    is UpdateContext -> FxRunLoop(this)
    is ContinueOn -> FxRunLoop(this)
    is ConnectionSwitch -> FxRunLoop(this)
    is Async -> FxRunLoop(this)
    is AsyncContinueOn -> FxRunLoop(this)
    is AsyncUpdateContext -> FxRunLoop(this)
  }

  @PublishedApi
  internal class RaiseError(@JvmField val error: Throwable) : Fx<Nothing>() {
    override fun toString(): String = "Fx.RaiseError(error = $error)"
  }

  @PublishedApi
  internal class Pure<A>(@JvmField var internalValue: Any?) : Fx<A>() {
    @Suppress("UNCHECKED_CAST")
    inline val value: A
      get() = internalValue as A

    override fun toString(): String = "Fx.Pure(value = $value)"
  }

  // Purely wrapped suspend function.
  // Should wrap a singular suspension point otherwise stack safety cannot be guaranteed.
  // This is not an issue if you guarantee stack safety yourself for your suspended program. i.e. using `tailrec`
  @PublishedApi
  internal class Single<A>(@JvmField val source: suspend () -> A) : Fx<A>() {
    override fun toString(): String = "Fx.Single"
  }

  /**
   * Internal only effect declaration to bypass suspension overhead
   */
  @PublishedApi
  internal class Lazy<A>(@JvmField var source: (Unit) -> A) : Fx<A>() {
    override fun toString(): String = "Fx.Lazy"
  }

  @PublishedApi
  internal class Defer<A>(@JvmField var thunk: () -> FxOf<A>) : Fx<A>() {
    override fun toString(): String = "Fx.Defer"
  }

  @PublishedApi
  internal class Map<A, B>(
    @JvmField var source: FxOf<A>,
    @JvmField var g: (A) -> B
  ) : Fx<B>(), (A) -> Fx<B> {
    override fun invoke(value: A): Fx<B> = Pure(g(value))
    override fun toString(): String = "Fx.Map(...)"
  }

  @PublishedApi
  internal class FlatMap<A, B>(
    @JvmField var source: FxOf<A>,
    @JvmField var fb: (A) -> FxOf<B>,
    @JvmField var index: Int = 0
  ) : Fx<B>() {
    override fun toString(): String = "Fx.FlatMap(..., index = $index)"
  }

  @PublishedApi
  internal class UpdateContext<A>(
    @JvmField var source: FxOf<A>,
    @JvmField var modify: (CoroutineContext) -> CoroutineContext
  ) : Fx<A>() {
    override fun toString(): String = "Fx.UpdateContext(...)"
  }

  @PublishedApi
  internal class ContinueOn<A>(
    @JvmField var source: FxOf<A>,
    @JvmField var ctx: CoroutineContext
  ) : Fx<A>() {
    override fun toString(): String = "Fx.ContinueOn(...)"
  }

  /**
   * [ConnectionSwitch] is used to temporally switch the [KindConnection] attached to the computation.
   * i.e. Switch from a cancellable [KindConnection.DefaultKindConnection] to [KindConnection.uncancelable] and later switch back to the original connection.
   * This is what [bracketCase] uses to make `acquire` and `release` uncancelable and disconnect it from the cancel stack.
   *
   * This node nor its combinators are useful outside of Fx's internals and is instead used to write features like [bracketCase] and [uncancelable].
   */
  @PublishedApi
  internal class ConnectionSwitch<A>(
    val source: FxOf<A>,
    val modify: (FxConnection) -> FxConnection,
    val restore: ((Any?, Throwable?, FxConnection, FxConnection) -> FxConnection)? = null
  ) : Fx<A>() {

    override fun toString(): String = "Fx.ConnectionSwitch(...)"

    companion object {
      // Internal reusable reference.
      val makeUncancelable: (FxConnection) -> FxConnection = { FxNonCancelable }
      val makeCancelable: (FxConnection) -> FxConnection = {
        when (it) {
          is KindConnection.Uncancelable -> FxConnection()
          is KindConnection.DefaultKindConnection -> it
        }
      }
      val revertToOld: (Any?, Throwable?, FxConnection, FxConnection) -> FxConnection = { _, _, old, _ ->
        old
      }
      val disableUncancelableAndPop: (Any?, Throwable?, FxConnection, FxConnection) -> FxConnection = { _, _, old, _ ->
        old.pop()
        old
      }
    }
  }

  @PublishedApi
  internal class AsyncContinueOn<A>(val source: Fx<A>, val ctx: CoroutineContext) : Fx<A>() {
    override fun toString(): String = "Fx.AsyncContinueOn(..)"
  }

  @PublishedApi
  internal class AsyncUpdateContext<A>(val source: Fx<A>, val f: (CoroutineContext) -> CoroutineContext) : Fx<A>() {
    override fun toString(): String = "Fx.AsyncUpdateContext(..)"
  }

  @PublishedApi
  internal class Async<A> internal constructor(
    val ctx: CoroutineContext? = null,
    val proc: FxProc<A>
  ) : Fx<A>() {

    companion object {

      @JvmStatic
      operator fun <A> invoke(proc: FxProc<A>): Fx<A> = Async { conn: FxConnection, ff: (Either<Throwable, A>) -> Unit ->
        Platform.onceOnly(ff).let { callback: (Either<Throwable, A>) -> Unit ->
          try {
            proc(conn, callback)
          } catch (throwable: Throwable) {
            if (NonFatal(throwable)) callback(Either.Left(throwable))
            else throw throwable
          }
        }
      }

      @JvmStatic
      fun <A> invokeF(procF: FxProcF<A>): Fx<A> = Async { conn: FxConnection, ff: (Either<Throwable, A>) -> Unit ->
        val conn2 = FxConnection()
        conn.push(conn2.cancel())
        Platform.onceOnly(conn, ff).let { callback: (Either<Throwable, A>) -> Unit ->
          val fx = try {
            procF(conn2, callback)
          } catch (t: Throwable) {
            if (NonFatal(t)) Fx { callback(Either.Left(t)) }
            else throw t
          }

          FxRunLoop.startCancelable(fx, conn2) { result ->
            // DEV: If suspended cancels conn2 like so `conn.cancel().map { cb(Right(Unit)) }`
            // It doesn't run the stack of conn2, instead the result is seen in the cb of startCancelable.
            val resultCancelled = result.fold({ e -> e == OnCancel.CancellationException }, { false })
            if (resultCancelled && conn.isNotCanceled()) FxRunLoop.start(conn.cancel(), cb = mapUnit)
            else Unit
          }
        }
      }
    }

    override fun toString(): String = "Fx.Async(..)"
  }
}

@Suppress("FunctionName")
internal fun <A> FxFiber(promise: UnsafePromise<A>, conn: FxConnection): Fiber<ForFx, A> {
  val join: Fx<A> = Fx.async { conn2, cb ->
    val cb2: (Either<Throwable, A>) -> Unit = {
      cb(it)
      conn2.pop()
      conn.pop()
    }

    conn2.push(Fx.Lazy { promise.remove(cb2) })
    conn.push(conn2.cancel())
    promise.get(cb2)
  }
  return Fiber(join, conn.cancel())
}
