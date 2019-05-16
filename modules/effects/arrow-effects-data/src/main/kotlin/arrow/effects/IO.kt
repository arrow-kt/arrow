package arrow.effects

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
import arrow.effects.internal.BracketStart
import arrow.effects.internal.ForwardCancelable
import arrow.effects.internal.GuaranteeReleaseFrame
import arrow.effects.internal.IOFiber
import arrow.effects.internal.IOForkedStart
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.IOFrame
import arrow.effects.internal.IORacePair
import arrow.effects.internal.IORaceTriple
import arrow.effects.internal.IORunLoop
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ConnectedProc
import arrow.effects.typeclasses.ConnectedProcF
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.Duration
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.Proc
import arrow.effects.typeclasses.ProcF
import arrow.effects.typeclasses.mapUnit
import arrow.higherkind
import kotlin.coroutines.CoroutineContext

typealias IOProc<A> = ConnectedProc<ForIO, A>
typealias IOProcF<A> = ConnectedProcF<ForIO, A>

fun <A> Proc<A>.toIOProc(): IOProc<A> = { _: IOConnection, proc -> this(proc) }
fun <A> ProcF<ForIO, A>.toIOProcF(): IOProcF<A> = { _: IOConnection, proc -> this(proc) }

suspend inline operator fun <A> IOOf<A>.not(): A = fix().invoke()

fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
  IO.FlatMap(this, IOFrame.Companion.ErrorHandler(f))

fun <A> IOOf<A>.handleError(f: (Throwable) -> A): IO<A> = when (this) {
  is IO.RaiseError -> IO { f(error) }
  is IO.Pure -> this
  else -> IO.FlatMap(this, IOFrame.Companion.ErrorHandler(f.andThen { IO.Pure<A>(it) }))
}

fun <A> A.liftIO(): IO<A> = IO.just(this)

@higherkind
sealed class IO<out A> : IOOf<A> {

  /**
   * The [suspended] form of this [IO].
   * `IO<A> -> (suspend () -> A)`
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   *
   *fun main(args: Array<String>) = kotlinx.coroutines.runBlocking {
   *  //sampleStart
   *  val f: suspend () -> String = IO.just("Hello World!").suspended
   *  //sampleEnd
   *  println(f())
   * }
   * ```
   */
  inline val suspended: suspend () -> A
    get() = when (this) {
      is RaiseError -> suspend { throw error }
      is Pure -> suspend { value }
      is Single -> suspend { source() }
      is Lazy -> suspend { source(Unit) }
      is Defer -> suspend { IORunLoop(thunk()) }
      is Map<*, A> -> suspend { IORunLoop(this) }
      is FlatMap<*, A> -> suspend { IORunLoop(this) }
      is UpdateContext -> suspend { IORunLoop(this) }
      is ContinueOn -> suspend { IORunLoop(this) }
      is ConnectionSwitch -> suspend { IORunLoop(this) }
      is Async -> suspend { IORunLoop(this) }
      is AsyncContinueOn -> suspend { IORunLoop(this) }
      is AsyncUpdateContext -> suspend { IORunLoop(this) }
    }

  /**
   * Transform the [IO] wrapped value of [A] into [B] preserving the [IO] structure.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   IO.just("Hello").map { "$it World" }
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(result))
   * }
   * ```
   */
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> map(noinline f: (A) -> B): IO<B> = when (this) {
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
   * Transform the [IO] value of [A] by sequencing an effect [IO] that results in [B].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   IO.just("Hello").flatMap { IO { "$it World" } }
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(result))
   * }
   * ```
   */
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> flatMap(noinline f: (A) -> IOOf<B>): IO<B> = when (this) {
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
        val fbb = fb as (Any?) -> IOOf<A>
        when (val fx: IO<A> = fbb(a).fix()) {
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
   * Transform the [IO] by sequencing an effect [IO] that results in [B] while ignoring the original value of [A].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   IO.just("Hello").followedBy(IO { "World" })
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun <B> followedBy(fa: IOOf<B>): IO<B> =
    flatMap { fa }

  /**
   * Redeem an [IO] to an [IO] of [B] by resolving the error **or** mapping the value [A] to [B].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   IO.raiseError<Int>(RuntimeException("Hello from Error"))
   *     .redeem({ e -> e.message!! }, Int::toString)
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun <B> redeem(fe: (Throwable) -> B, fs: (A) -> B): IO<B> =
    FlatMap(this, IOFrame.Companion.Redeem(fe, fs))

  /**
   * Redeem an [IO] to an [IO] of [B] by resolving the error **or** mapping the value [A] to [B] **with** an effect.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   IO.just("1")
   *     .redeemWith({ e -> IO { -1 } }, { str -> IO { str.toInt() } })
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun <B> redeemWith(fe: (Throwable) -> IOOf<B>, fs: (A) -> IOOf<B>): IO<B> =
    FlatMap(this, IOFrame.Companion.RedeemWith(fe, fs))

  /**
   * Discards the [A] value inside [IO] signaling this container may be pointing to a no-op
   * or an effect whose return value is deliberately ignored. The singleton value [Unit] serves as signal.
   *
   * `IO<A> -> IO<Unit>
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   IO.just("Hello World").unit()
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun unit(): IO<Unit> = map(mapUnit)

  /**
   * A way to safely acquire a resource and release in the face of errors and cancellation.
   * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
   *
   * [Bracket] exists out of a three stages:
   *   1. acquisition
   *   2. consumption
   *   3. releasing
   *
   * 1. Resource acquisition is **NON CANCELABLE**.
   *   If resource acquisition fails, meaning no resource was actually successfully acquired then we short-circuit the effect.
   *   Reason being, we cannot [release] what we did not `acquire` first. Same reason we cannot call [use].
   *   If it is successful we pass the result to stage 2 [use].
   *
   * 2. Resource consumption is like any other [IO] effect. The key difference here is that it's wired in such a way that
   *   [release] **will always** be called either on [ExitCase.Canceled], [ExitCase.Error] or [ExitCase.Completed].
   *   If it failed than the resulting [IO] from [bracketCase] will be `IO.raiseError(e)`, otherwise the result of [use].
   *
   * 3. Resource releasing is **NON CANCELABLE**, otherwise it could result in leaks.
   *   In the case it throws the resulting [IO] will be either the error or a composed error if one occurred in the [use] stage.
   *
   * @param use is the action to consume the resource and produce an [IO] with the result.
   * Once the resulting [IO] terminates, either successfully, error or disposed,
   * the [release] function will run to clean up the resources.
   *
   * @param release the allocated resource after the resulting [IO] of [use] is terminates.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   fun content(): IO<String> =
   *     IO.just("This file contains some interesting content!")
   * }
   *
   * fun openFile(uri: String): IO<File> = IO { File(uri).open() }
   * fun closeFile(file: File): IO<Unit> = IO { file.close() }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val safeComputation = openFile("data.json").bracketCase(
   *     release = { file, exitCase ->
   *       when (exitCase) {
   *         is ExitCase.Completed -> { /* do something */ }
   *         is ExitCase.Canceled -> { /* do something */ }
   *         is ExitCase.Error -> { /* do something */ }
   *       }
   *       closeFile(file)
   *     },
   *     use = { file -> file.content() }
   *   )
   *   //sampleEnd
   *   println(safeComputation)
   * }
   *  ```
   */
  fun <B> bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> = async { conn, cb ->
    val forwardCancel = ForwardCancelable()
    conn.push(forwardCancel.cancel()) // Connect ForwardCancelable to existing connection.

    if (conn.isNotCanceled()) IORunLoop.start(fix(), cb = BracketStart(use, release, conn, forwardCancel, cb))
    else forwardCancel.complete(unit)
  }

  /**
   * Meant for specifying tasks with safe resource acquisition and release in the face of errors and interruption.
   * It would be the the equivalent of `try/catch/finally` statements in mainstream imperative languages for resource
   * acquisition and release.
   *
   * @param release is the action that's supposed to release the allocated resource after `use` is done, irregardless
   * of its exit condition.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   override fun toString(): String = "This file contains some interesting content!"
   * }
   *
   * fun openFile(uri: String): IO<File> = IO { File(uri).open() }
   * fun closeFile(file: File): IO<Unit> = IO { file.close() }
   * fun fileToString(file: File): IO<String> = IO { file.toString() }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val safeComputation = openFile("data.json").bracket({ file: File -> closeFile(file) }, { file -> fileToString(file) })
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(safeComputation))
   * }
   * ```
   */
  fun <B> bracket(release: (A) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
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
  fun guaranteeCase(release: (ExitCase<Throwable>) -> IOOf<Unit>): IO<A> = async { conn, cb ->
    Platform.trampoline {
      val frame = GuaranteeReleaseFrame<A>(release)
      val onNext = FlatMap(this, frame)
      // Registering our cancelable token ensures that in case cancellation is detected, `release` gets called
      conn.push(frame.cancel)

      // Race condition check, avoiding starting `source` in case the connection was already cancelled — n.b. we don't need
      // to trigger `release` otherwise, because it already happened
      if (conn.isNotCanceled()) IORunLoop.startCancelable(onNext, conn, cb = cb)
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
  fun guarantee(finalizer: IOOf<Unit>): IO<A> =
    guaranteeCase { finalizer }

  fun <B> ap(ff: IOOf<(A) -> B>): IO<B> =
    ff.fix().flatMap { map(it) }

  fun ensure(
    error: () -> Throwable,
    predicate: (A) -> Boolean
  ): IO<A> = when (this) {
    is RaiseError -> this
    is Pure -> if (!predicate(value)) RaiseError(error()) else this
    else -> flatMap { result ->
      if (!predicate(result)) RaiseError(error())
      else Pure<A>(result)
    }
  }

  fun attempt(): IO<Either<Throwable, A>> =
    FlatMap(this, IOFrame.attempt())

  fun updateContext(f: (CoroutineContext) -> CoroutineContext): IO<A> =
    UpdateContext(this, f)

  /**
   * Continue the evaluation on provided [CoroutineContext]
   *
   * @param ctx [CoroutineContext] to run evaluation on
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = IO.unit.continueOn(Dispatchers.Default).flatMap {
   *     IO { Thread.currentThread().name }
   *   }
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(result))
   * }
   * ```
   */
  fun continueOn(ctx: CoroutineContext): IO<A> = when (this) {
    is ContinueOn -> this.apply {
      this.ctx = ctx
    }
    else -> ContinueOn(this, ctx)
  }

  /**
   * Create a new [IO] that upon execution starts the source [IO] within a [Fiber] on [ctx].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val program = IO { "Running inside a fiber" }
   *     .fork(Dispatchers.Default).flatMap { (join, cancel) ->
   *       join
   *     }
   *   //sampleEnd
   *   println(IO.unsafeRunBlocking(program))
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [IO] on.
   * @return [IO] with suspended execution of source [IO] on context [ctx].
   */
  fun fork(ctx: CoroutineContext): IO<Fiber<ForIO, A>> = async { _, cb ->
    val promise = UnsafePromise<A>()
    val conn = IOConnection()
    IORunLoop.startCancelable(IOForkedStart(this, ctx), conn, cb = promise::complete)
    cb(Right(IOFiber(promise, conn)))
  }

  /**
   * Make [IO] uncancelable and switches back to the original cancellation connection after running.
   */
  fun uncancelable(): IO<A> =
    ConnectionSwitch(this, ConnectionSwitch.makeUncancelable, { _, _, old, _ -> old })

  companion object : IORacePair, IORaceTriple {

    /**
     * Wraps a suspend function into [IO].
     *
     * @param fa suspended function to wrap into [IO].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = IO { throw RuntimeException("NUKES MISFIRED...BOOOOOM!!") }
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    operator fun <A> invoke(fa: suspend () -> A): IO<A> = Single(fa)

    @JvmStatic
    fun <A> effect(fa: suspend () -> A): IO<A> = Single(fa)

    /**
     * Delay a computation on provided [CoroutineContext].
     *
     * @param ctx [CoroutineContext] to run evaluation on.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     * import kotlinx.coroutines.Dispatchers
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = IO(Dispatchers.Default) { Thread.currentThread().name }
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    operator fun <A> invoke(ctx: CoroutineContext, f: () -> A): IO<A> =
      Map(UpdateContext(unit) { ctx }) { f() }

    /**
     * Wrap a pure value [A] into [IO].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = IO.just("Hello World")
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> just(a: A): IO<A> = Pure(a)

    /**
     * A pure [IO] value of [Unit].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = IO.unit
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    val unit: IO<Unit> = Pure(Unit)

    /**
     * A lazy [IO] value of [Unit].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = IO.lazy
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    val lazy: IO<Unit> = Lazy { Unit }

    /**
     * Wraps a pure function in a lazy manner
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   fun longCalculation(): Int = 9999
     *   //sampleStart
     *   val result = IO.lazy { longCalculation() }
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> lazy(f: (Unit) -> A): IO<A> = Lazy(f)

    /**
     * Task that never finishes evaluating.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val fint: IO<Int> = IO.never
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(fint))
     * }
     * ```
     */
    @JvmStatic
    val never: IO<Nothing> = async { _, _ -> Unit }

    /**
     * Evaluates an [Eval] instance within a safe [IO] context.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     * import arrow.core.Eval
     *
     * fun main(args: Array<String>) {
     *   fun longCalculation(): Int = 9999
     *   //sampleStart
     *   val result = IO.eval(Eval.later { longCalculation() })
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> eval(eval: Eval<A>): IO<A> = when (eval) {
      is Eval.Now -> just(eval.value)
      else -> Lazy { eval.value() }
    }

    /**
     * Raise an error in a pure way without actually throwing.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result: IO<Int> = IO.raiseError<Int>(RuntimeException("Boom"))
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> raiseError(e: Throwable): IO<A> = RaiseError(e)

    /**
     * Defer a computation that results in an [IO] value.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = IO.defer { IO { Thread.currentThread().name } }
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> defer(fa: () -> IOOf<A>): IO<A> =
      Defer(fa)

    /**
     * Defer a computation on provided [CoroutineContext].
     *
     * @param ctx [CoroutineContext] to run evaluation on.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     * import kotlinx.coroutines.Dispatchers
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = IO.defer(Dispatchers.Default) { IO { Thread.currentThread().name } }
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A> defer(ctx: CoroutineContext, f: () -> IOOf<A>): IO<A> =
      FlatMap(ContinueOn(unit, ctx), { f() })

    /**
     * Perform a recursive operation in a stack-safe way.
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     * import arrow.effects.IO
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = IO.tailRecM(0) { i ->
     *     IO.just(
     *      if(i == 5000) Right(i)
     *      else Left(i + 1)
     *     )
     *   }
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     */
    @JvmStatic
    fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> =
      FlatMap(f(a), { result ->
        result.fold({ tailRecM(it, f) }, { just(it) })
      }, 0)

    /**
     * Creates a cancelable instance of [IO] that executes an asynchronous process on evaluation.
     * This combinator can be used to wrap callbacks or other similar impure code that requires cancellation code.
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     * import arrow.effects.*
     * import java.lang.RuntimeException
     *
     * typealias Callback = (List<String>?, Throwable?) -> Unit
     *
     * class GithubId
     * object GithubService {
     *   private val listeners: MutableMap<GithubId, Callback> = mutableMapOf()
     *   fun getUsernames(callback: (List<String>?, Throwable?) -> Unit): GithubId {
     *     val id = GithubId()
     *     listeners[id] = callback
     *     //execute operation and call callback at some point in future
     *     return id
     *   }
     *
     *   fun unregisterCallback(id: GithubId): Unit {
     *     listeners.remove(id)
     *   }
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   fun getUsernames(): IO<List<String>> =
     *     IO.async { conn: IOConnection, cb: (Either<Throwable, List<String>>) -> Unit ->
     *       val id = GithubService.getUsernames { names, throwable ->
     *         when {
     *           names != null -> cb(Right(names))
     *           throwable != null -> cb(Left(throwable))
     *           else -> cb(Left(RuntimeException("Null result and no exception")))
     *         }
     *       }
     *
     *       conn.push(IO { GithubService.unregisterCallback(id) })
     *       conn.push(IO { println("Everything we push to the cancellation stack will execute on cancellation") })
     *     }
     *
     *   val result = getUsernames()
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     *
     * @param fa an asynchronous computation that might fail typed as [ConnectedProc].
     * @see asyncF for a version that can suspend side effects in the registration function.
     */
    @JvmStatic
    fun <A> async(fa: IOProc<A>): IO<A> = Async(fa)

    /**
     * Creates a cancelable instance of [IO] that executes an asynchronous process on evaluation.
     * This combinator can be used to wrap callbacks or other similar impure code that requires cancellation code.
     *
     * ```kotlin:ank:playground
     * import arrow.core.*
     * import arrow.effects.*
     * import java.lang.RuntimeException
     *
     * typealias Callback = (List<String>?, Throwable?) -> Unit
     *
     * class GithubId
     * object GithubService {
     *   private val listeners: MutableMap<GithubId, Callback> = mutableMapOf()
     *   fun getUsernames(callback: (List<String>?, Throwable?) -> Unit): GithubId {
     *     val id = GithubId()
     *     listeners[id] = callback
     *     //execute operation and call callback at some point in future
     *     return id
     *   }
     *
     *   fun unregisterCallback(id: GithubId): Unit {
     *     listeners.remove(id)
     *   }
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   fun getUsernames(): IO<List<String>> =
     *     IO.asyncF { conn: IOConnection, cb: (Either<Throwable, List<String>>) -> Unit ->
     *       IO {
     *         val id = GithubService.getUsernames { names, throwable ->
     *           when {
     *             names != null -> cb(Right(names))
     *             throwable != null -> cb(Left(throwable))
     *             else -> cb(Left(RuntimeException("Null result and no exception")))
     *           }
     *         }
     *
     *         conn.push(IO { GithubService.unregisterCallback(id) })
     *         conn.push(IO { println("Everything we push to the cancellation stack will execute on cancellation") })
     *       }
     *     }
     *
     *   val result = getUsernames()
     *   //sampleEnd
     *   println(IO.unsafeRunBlocking(result))
     * }
     * ```
     *
     * @param fa a deferred asynchronous computation that might fail typed as [ConnectedProcF].
     * @see async for a version that can suspend side effects in the registration function.
     */
    @JvmStatic
    fun <A> asyncF(fa: IOProcF<A>): IO<A> = Async.invokeF(fa)

    /**
     * [unsafeRunBlocking] allows you to run any [IO] to its wrapped value [A].
     *
     * It's called unsafe because it immediately runs the effects wrapped in [IO],
     * and thus is **not** referentially transparent.
     *
     * **NOTE** this function is intended for testing, it should never appear in your mainline production code!
     *
     * @param fx the [IO] to run
     * @return the resulting value
     * @see [unsafeRunBlocking] or [unsafeRunNonBlockingCancellable] that run the value as [Either].
     * @see [runNonBlocking] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> unsafeRunBlocking(fx: IOOf<A>): A =
      unsafeRunTimed(fx.fix(), Duration.INFINITE)
        .fold({ throw IllegalArgumentException("IO execution should yield a valid result") }, ::identity)

    /**
     * Run [fx] with a limitation on how long to await *individual* async results.
     * It's possible that this methods runs forever i.e. for an infinite recursive [IO].
     *
     * **NOTE** this function is intended for testing, it should never appear in your mainline production code!
     *
     * @see unsafeRunBlocking
     */
    @JvmStatic
    fun <A> unsafeRunTimed(fx: IO<A>, limit: Duration): Option<A> = when (fx) {
      is RaiseError -> throw fx.error
      is Pure -> Some(fx.value)
      is Lazy -> Some(fx.source(Unit))
      else -> when (val result = IORunLoop.step(fx)) {
        is Pure<A> -> Some(result.value)
        is RaiseError -> throw result.error
        is Lazy<A> -> Some(result.source(Unit))
        else -> Platform.unsafeResync(result, limit)
      }
    }

    /**
     * [unsafeRunNonBlocking] allows you to run any [IO] to its wrapped value [A].
     *
     * It receives the values in a callback [cb] and thus **has** the ability to run `NonBlocking` but that depends on the implementation.
     * When the underlying effects/program runs blocking on the callers thread this method will run blocking.
     *
     * To start this on `NonBlocking` use `IO.unsafeRunNonBlocking(NonBlocking.shift().followedBy(fx))`.
     *
     * @param fx the [IO] to run
     * @param cb the callback that is called with the computations result represented as an [Either].
     * @see [unsafeRunNonBlockingCancellable] to run in a cancellable manner.
     * @see [runNonBlocking] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> unsafeRunNonBlocking(fx: IOOf<A>, cb: (Either<Throwable, A>) -> Unit): Unit =
      IORunLoop.start(fx, cb = cb)

    /**
     * [runNonBlocking] allows you to run any [IO] to its wrapped value [A] in a referential transparent manner.
     *
     * Reason it can happen in a referential transparent manner is because nothing is actually running when this method is invoked.
     * The combinator can be used to define how several programs have to run in a safe manner.
     *
     * ```
     * val programOne = IO { 1 }
     * val programTwo = IO { 2 }
     * val programThree = IO { 3 }
     *
     * IO.runNonBlocking(programOne, cb)
     * ```
     */
    @JvmStatic
    fun <A> runNonBlocking(fx: IOOf<A>, cb: (Either<Throwable, A>) -> IO<Unit>): IO<Unit> = IO {
      IORunLoop.start(fx, cb = cb.andThen { unsafeRunBlocking(it) })
    }

    /**
     * [unsafeRunNonBlockingCancellable] allows you to run any [IO] to its wrapped value [A] in a cancellable manner.
     *
     * It receives the values in a callback [cb] and thus **has** the ability to run `NonBlocking` but that depends on the implementation.
     * When the underlying effects/program runs blocking on the callers thread this method will run blocking.
     *
     * @param fx the [IO] to run
     * @param cb the callback that is called with the computations result represented as an [Either].
     * @return a [Disposable] that can be used to cancel the computation.
     * @see [unsafeRunNonBlocking] to run in a non-cancellable manner.
     * @see [runNonBlockingCancellable] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> unsafeRunNonBlockingCancellable(fx: IOOf<A>, onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> Unit): Disposable =
      unsafeRunBlocking(runNonBlockingCancellable(fx, onCancel, cb.andThen { unit }))

    /**
     * A pure version of [unsafeRunNonBlockingCancellable], which defines how an [IO] is ran in a cancelable manner but it doesn't run yet.
     *
     * It receives the values in a callback [cb] and thus **has** the ability to run `NonBlocking` but that depends on the implementation.
     * When the underlying effects/program runs blocking on the callers thread this method will run blocking.
     *
     * @param fx the [IO] to run
     * @param cb the callback that is called with the computations result represented as an [Either].
     * @return a [Disposable] that can be used to cancel the computation.
     * @see [unsafeRunNonBlocking] to run in a non-cancellable manner.
     * @see [unsafeRunNonBlockingCancellable] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> runNonBlockingCancellable(fx: IOOf<A>, onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Disposable> =
      async { _ /* The start of this execution is immediate and uncancelable */, cbb ->
        val conn = IOConnection()
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
        IORunLoop.startCancelable(fx, conn, cb = onCancelCb)
      }
  }

  override fun toString(): String = "IO(...)"

  // Only internals from this point.

  @PublishedApi
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  internal inline fun <B> unsafeRecast(): IO<B> = this as IO<B>

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  @PublishedApi
  internal suspend inline operator fun invoke(): A = when (this) {
    is RaiseError -> throw error
    is Pure -> value
    is Single -> source()
    is Lazy -> source(Unit)
    is Defer -> IORunLoop(this)
    is Map<*, *> -> IORunLoop(this)
    is FlatMap<*, *> -> IORunLoop(this)
    is UpdateContext -> IORunLoop(this)
    is ContinueOn -> IORunLoop(this)
    is ConnectionSwitch -> IORunLoop(this)
    is Async -> IORunLoop(this)
    is AsyncContinueOn -> IORunLoop(this)
    is AsyncUpdateContext -> IORunLoop(this)
  }

  @PublishedApi
  internal class RaiseError(@JvmField val error: Throwable) : IO<Nothing>() {
    override fun toString(): String = "IO.RaiseError(error = $error)"
  }

  @PublishedApi
  internal class Pure<A>(@JvmField var internalValue: Any?) : IO<A>() {
    @Suppress("UNCHECKED_CAST")
    inline val value: A
      get() = internalValue as A

    override fun toString(): String = "IO.Pure(value = $value)"
    override fun equals(other: Any?): Boolean =
      if (other is Pure<*>) value == other.value else false
  }

  // Purely wrapped suspend function.
  // Should wrap a singular suspension point otherwise stack safety cannot be guaranteed.
  // This is not an issue if you guarantee stack safety yourself for your suspended program. i.e. using `tailrec`
  @PublishedApi
  internal class Single<A>(@JvmField val source: suspend () -> A) : IO<A>() {
    override fun toString(): String = "IO.Single"
  }

  /**
   * Internal only effect declaration to bypass suspension overhead
   */
  @PublishedApi
  internal class Lazy<A>(@JvmField var source: (Unit) -> A) : IO<A>() {
    override fun toString(): String = "IO.Lazy"
  }

  @PublishedApi
  internal class Defer<A>(@JvmField var thunk: () -> IOOf<A>) : IO<A>() {
    override fun toString(): String = "IO.Defer"
  }

  @PublishedApi
  internal class Map<A, B>(
    @JvmField var source: IOOf<A>,
    @JvmField var g: (A) -> B
  ) : IO<B>(), (A) -> IO<B> {
    override fun invoke(value: A): IO<B> = Pure(g(value))
    override fun toString(): String = "IO.Map(...)"
  }

  @PublishedApi
  internal class FlatMap<A, B>(
    @JvmField var source: IOOf<A>,
    @JvmField var fb: (A) -> IOOf<B>,
    @JvmField var index: Int = 0
  ) : IO<B>() {
    override fun toString(): String = "IO.FlatMap(..., index = $index)"
  }

  @PublishedApi
  internal class UpdateContext<A>(
    @JvmField var source: IOOf<A>,
    @JvmField var modify: (CoroutineContext) -> CoroutineContext
  ) : IO<A>() {
    override fun toString(): String = "IO.UpdateContext(...)"
  }

  @PublishedApi
  internal class ContinueOn<A>(
    @JvmField var source: IOOf<A>,
    @JvmField var ctx: CoroutineContext
  ) : IO<A>() {
    override fun toString(): String = "IO.ContinueOn(...)"
  }

  /**
   * [ConnectionSwitch] is used to temporally switch the [KindConnection] attached to the computation.
   * i.e. Switch from a cancellable [KindConnection.DefaultKindConnection] to [KindConnection.uncancelable] and later switch back to the original connection.
   * This is what [bracketCase] uses to make `acquire` and `release` uncancelable and disconnect it from the cancel stack.
   *
   * This node nor its combinators are useful outside of IO's internals and is instead used to write features like [bracketCase] and [uncancelable].
   */
  @PublishedApi
  internal class ConnectionSwitch<A>(
    val source: IOOf<A>,
    val modify: (IOConnection) -> IOConnection,
    val restore: ((Any?, Throwable?, IOConnection, IOConnection) -> IOConnection)? = null
  ) : IO<A>() {

    override fun toString(): String = "IO.ConnectionSwitch(...)"

    companion object {
      // Internal reusable reference.
      val makeUncancelable: (IOConnection) -> IOConnection = { IONonCancelable }
      val makeCancelable: (IOConnection) -> IOConnection = {
        when (it) {
          is KindConnection.Uncancelable -> IOConnection()
          is KindConnection.DefaultKindConnection -> it
        }
      }
      val revertToOld: (Any?, Throwable?, IOConnection, IOConnection) -> IOConnection = { _, _, old, _ ->
        old
      }
      val disableUncancelableAndPop: (Any?, Throwable?, IOConnection, IOConnection) -> IOConnection = { _, _, old, _ ->
        old.pop()
        old
      }
    }
  }

  @PublishedApi
  internal class AsyncContinueOn<A>(val source: IO<A>, val ctx: CoroutineContext) : IO<A>() {
    override fun toString(): String = "IO.AsyncContinueOn(..)"
  }

  @PublishedApi
  internal class AsyncUpdateContext<A>(val source: IO<A>, val f: (CoroutineContext) -> CoroutineContext) : IO<A>() {
    override fun toString(): String = "IO.AsyncUpdateContext(..)"
  }

  @PublishedApi
  internal class Async<A> internal constructor(val proc: IOProc<A>) : IO<A>() {

    companion object {

      @JvmStatic
      operator fun <A> invoke(proc: IOProc<A>): IO<A> = Async { conn: IOConnection, ff: (Either<Throwable, A>) -> Unit ->
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
      fun <A> invokeF(procF: IOProcF<A>): IO<A> = Async { conn: IOConnection, ff: (Either<Throwable, A>) -> Unit ->
        val conn2 = IOConnection()
        conn.push(conn2.cancel())
        Platform.onceOnly(conn, ff).let { callback: (Either<Throwable, A>) -> Unit ->
          val fx = try {
            procF(conn2, callback)
          } catch (t: Throwable) {
            if (NonFatal(t)) IO { callback(Either.Left(t)) }
            else throw t
          }

          IORunLoop.startCancelable(fx, conn2) { result ->
            // DEV: If suspended cancels conn2 like so `conn.cancel().map { cb(Right(Unit)) }`
            // It doesn't run the stack of conn2, instead the result is seen in the cb of startCancelable.
            val resultCancelled = result.fold({ e -> e == OnCancel.CancellationException }, { false })
            if (resultCancelled && conn.isNotCanceled()) IORunLoop.start(conn.cancel(), cb = mapUnit)
            else Unit
          }
        }
      }
    }

    override fun toString(): String = "IO.Async(..)"
  }

  fun unsafeRunSync() = IO.unsafeRunBlocking(this)
}
