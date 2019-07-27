package arrow.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Eval
import arrow.core.NonFatal
import arrow.core.Option
import arrow.core.Some
import arrow.core.andThen
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.fx.OnCancel.Companion.CancellationException
import arrow.fx.OnCancel.Silent
import arrow.fx.OnCancel.ThrowCancellationException
import arrow.fx.internal.ForwardCancelable
import arrow.fx.internal.IOBracket
import arrow.fx.internal.IOFiber
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform.maxStackDepthSize
import arrow.fx.internal.Platform.onceOnly
import arrow.fx.internal.Platform.unsafeResync
import arrow.fx.internal.UnsafePromise
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.mapUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ForIO private constructor() {
  companion object
}
typealias IOOf<E, A> = Kind<Kind<ForIO, E>, A>

typealias IOPartialOf<E> = Kind<ForIO, E>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <E, A> IOOf<E, A>.fix(): IO<E, A> =
  this as IO<E, A>

typealias IOProc<E, A> = ((Either<E, A>) -> Unit) -> Unit
typealias IOProcF<E, A> = ((Either<E, A>) -> Unit) -> IOOf<E, Unit>

@Suppress("StringLiteralDuplication")
sealed class IO<out E, out A> : IOOf<E, A> {

  companion object : IOParMap2, IOParMap3, IORacePair, IORaceTriple {

    fun <E, A> effect(fe: (Throwable) -> E, f: suspend () -> A): IO<E, A> =
      Effect(null, fe, effect = f)

    // Specialization
    fun <A> effect(f: suspend () -> A): IO<Throwable, A> =
      Effect(null, ::identity, f)

    fun <E, A> effect(ctx: CoroutineContext, fe: (Throwable) -> E, f: suspend () -> A): IO<E, A> =
      Effect(ctx, fe, f)

    // Specialization
    fun <A> effect(ctx: CoroutineContext, f: suspend () -> A): IO<Throwable, A> =
      Effect(ctx, ::identity, f)

    fun <E, A> just(a: A): IO<E, A> = Pure(a)

    fun <E, A> raiseError(e: E): IO<E, A> = RaiseError(e)

    operator fun <E, A> invoke(ctx: CoroutineContext, fe: (Throwable) -> E, f: suspend () -> A): IO<E, A> =
      effect(ctx, fe, f)

    // Specialization
    operator fun <A> invoke(ctx: CoroutineContext, f: suspend () -> A): IO<Throwable, A> =
      Effect(ctx, ::identity, f)

    operator fun <E, A> invoke(fe: (Throwable) -> E, f: suspend () -> A): IO<E, A> =
      Effect(null, fe, f)

    // Specialization
    operator fun <A> invoke(f: suspend () -> A): IO<Throwable, A> =
      Effect(null, ::identity, f)

    fun <E, A> later(fe: (Throwable) -> E, f: () -> A): IO<E, A> =
      defer(fe) { Pure(f()) }

    // Specialization
    fun <A> later(f: () -> A): IO<Throwable, A> =
      defer { Pure(f()) }

    fun <E, A> defer(fe: (Throwable) -> E, f: () -> IOOf<E, A>): IO<E, A> =
      Suspend(fe, f)

    // Specialization
    fun <A> defer(f: () -> IOOf<Throwable, A>): IO<Throwable, A> =
      Suspend(::identity, f)


    fun <E, A> async(fe: (Throwable) -> E, k: IOProc<E, A>): IO<E, A> =
      Async { _: IOConnection, ff: (Either<E, A>) -> Unit ->
        onceOnly(ff).let { callback: (Either<E, A>) -> Unit ->
          try {
            k(callback)
          } catch (throwable: Throwable) {
            callback(Left(fe(throwable.nonFatalOrThrow())))
          }
        }
      }

    // Specialization
    fun <E, A> async(k: IOProc<Throwable, A>): IO<Throwable, A> =
      async(::identity, k)

    fun <E, A> cancelable(fe: (Throwable) -> E, cb: ((Either<E, A>) -> Unit) -> CancelToken<IOPartialOf<E>>): IO<E, A> =
      Async { conn: IOConnection, cbb: (Either<E, A>) -> Unit ->
        onceOnly(conn, cbb).let { cbb2 ->
          val cancelable = ForwardCancelable<E>()
          conn.push(cancelable.cancel())
          if (conn.isNotCanceled()) {
            cancelable.complete(try {
              cb(cbb2)
            } catch (throwable: Throwable) {
              cbb2(Left(fe(throwable.nonFatalOrThrow())))
              unit
            })
          }
        }
      }

    // Specialization
    fun <A> cancelable(cb: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForIO>): IO<Throwable, A> =

    fun <A> cancelableF(cb: ((Either<Throwable, A>) -> Unit) -> IOOf<Throwable, CancelToken<ForIO>>): IO<Throwable, A> =
      Async { conn: IOConnection, cbb: (Either<Throwable, A>) -> Unit ->
        val cancelable = ForwardCancelable()
        val conn2 = IOConnection()
        conn.push(cancelable.cancel())
        conn.push(conn2.cancel())

        onceOnly(conn, cbb).let { cbb2 ->
          val fa: IOOf<Throwable, CancelToken<ForIO>> = try {
            cb(cbb2)
          } catch (throwable: Throwable) {
            cbb2(Left(throwable.nonFatalOrThrow()))
            just(unit)
          }

          IORunLoop.startCancelable(fa, conn2) { result ->
            conn.pop()
            result.fold({ }, cancelable::complete)
          }
        }
      }

    fun <A> asyncF(k: IOProcF<Throwable, A>): IO<Throwable, A> =
      Async { conn: IOConnection, ff: (Either<Throwable, A>) -> Unit ->
        val conn2 = IOConnection()
        conn.push(conn2.cancel())
        onceOnly(conn, ff).let { callback: (Either<Throwable, A>) -> Unit ->
          val fa = try {
            k(callback)
          } catch (t: Throwable) {
            if (NonFatal(t)) {
              IO { callback(Left(t)) }
            } else {
              throw t
            }
          }

          IORunLoop.startCancelable(fa, conn2) { result ->
            result.fold({ e -> callback(Left(e)) }, mapUnit)
          }
        }
      }

    val unit: IO<Throwable, Unit> =
      just(Unit)

    val lazy: IO<Throwable, Unit> =
      invoke { }

    fun <A> eval(eval: Eval<A>): IO<Throwable, A> =
      when (eval) {
        is Eval.Now -> just(eval.value)
        else -> invoke { eval.value() }
      }

    fun <E, A, B> tailRecM(a: A, f: (A) -> IOOf<E, Either<A, B>>): IO<E, B> =
      f(a).fix().flatMap {
        when (it) {
          is Either.Left -> tailRecM(it.a, f)
          is Either.Right -> just(it.b)
        }
      }

    val never: IO<Throwable, Nothing> = async { }
  }

  suspend fun suspended(): A = suspendCoroutine { cont ->
    IORunLoop.start(this) {
      it.fold(cont::resumeWithException, cont::resume)
    }
  }

  open fun <B> map(f: (A) -> B): IO<E, B> =
    Map(this, f, 0)

  open fun <B> flatMap(f: (A) -> IOOf<E, B>): IO<E, B> =
    Bind(this) { f(it).fix() }

  open fun continueOn(ctx: CoroutineContext): IO<E, A> =
    ContinueOn(this, ctx)

  fun <B> ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    flatMap { a -> ff.fix().map { it(a) } }

  /**
   * Create a new [IO] that upon execution starts the receiver [IO] within a [Fiber] on [ctx].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = IO.fx {
   *     val (join, cancel) = !IO.effect {
   *       println("Hello from a fiber on ${Thread.currentThread().name}")
   *     }.startFiber(Dispatchers.Default)
   *   }
   *
   *   //sampleEnd
   *   result.unsafeRunSync()
   * }
   * ```
   *
   * @receiver [IO] to execute on [ctx] within a new suspended [IO].
   * @param ctx [CoroutineContext] to execute the source [IO] on.
   * @return [IO] with suspended execution of source [IO] on context [ctx].
   */
  fun startFiber(ctx: CoroutineContext): IO<Throwable, Fiber<ForIO, A>> = async { cb ->
    val promise = UnsafePromise<A>()
    // A new IOConnection, because its cancellation is now decoupled from our current one.
    val conn = IOConnection()
    IORunLoop.startCancelable(IOForkedStart(this, ctx), conn, promise::complete)
    cb(Either.Right(IOFiber(promise, conn)))
  }

  fun <B> followedBy(fb: IOOf<E, B>) = flatMap { fb }

  fun attempt(): IO<Throwable, Either<Throwable, A>> =
    Bind(this, IOFrame.attempt())

  /**
   * Redeem an [IO] to an [IO] of [B] by resolving the error **or** mapping the value [A] to [B].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.IO
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   IO.raiseError<Int>(RuntimeException("Hello from Error"))
   *     .redeem({ e -> e.message ?: "" }, Int::toString)
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   */
  fun <B> redeem(fe: (Throwable) -> B, fb: (A) -> B): IO<Throwable, B> =
    Bind(this, IOFrame.Companion.Redeem(fe, fb))

  /**
   * Redeem an [IO] to an [IO] of [B] by resolving the error **or** mapping the value [A] to [B] **with** an effect.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.IO
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   IO.just("1")
   *     .redeemWith({ e -> IO.just(-1) }, { str -> IO { str.toInt() } })
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   */
  fun <B> redeemWith(fe: (Throwable) -> IOOf<E, B>, fb: (A) -> IOOf<E, B>): IO<Throwable, B> =
    Bind(this, IOFrame.Companion.RedeemWith(fe, fb))

  fun runAsync(cb: (Either<Throwable, A>) -> IOOf<E, Unit>): IO<Throwable, Unit> =
    IO { unsafeRunAsync(cb.andThen { it.fix().unsafeRunAsync { } }) }

  fun unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
    IORunLoop.start(this, cb)

  fun runAsyncCancellable(onCancel: OnCancel = Silent, cb: (Either<Throwable, A>) -> IOOf<E, Unit>): IO<Throwable, Disposable> =
    async { ccb ->
      val conn = IOConnection()
      val onCancelCb =
        when (onCancel) {
          ThrowCancellationException ->
            cb andThen { it.fix().unsafeRunAsync { } }
          Silent ->
            { either -> either.fold({ if (!conn.isCanceled() || it != CancellationException) cb(either) }, { cb(either) }) }
        }
      ccb(conn.toDisposable().right())
      IORunLoop.startCancelable(this, conn, onCancelCb)
    }

  fun unsafeRunAsyncCancellable(onCancel: OnCancel = Silent, cb: (Either<Throwable, A>) -> Unit): Disposable =
    runAsyncCancellable(onCancel, cb andThen { it.liftIO() }).unsafeRunSync()

  fun unsafeRunSync(): A =
    unsafeRunTimed(Duration.INFINITE)
      .fold({ throw IllegalArgumentException("IO execution should yield a valid result") }, ::identity)

  fun unsafeRunTimed(limit: Duration): Option<A> = IORunLoop.step(this).unsafeRunTimedTotal(limit)

  internal abstract fun unsafeRunTimedTotal(limit: Duration): Option<A>

  /** Makes the source [IO] uncancelable such that a [Fiber.cancel] signal has no effect. */
  fun uncancelable(): IO<Throwable, A> =
    ContextSwitch(this, ContextSwitch.makeUncancelable, ContextSwitch.disableUncancelable)

  fun <B> bracket(release: (A) -> IOOf<E, Unit>, use: (A) -> IOOf<E, B>): IO<Throwable, B> =
    bracketCase({ a, _ -> release(a) }, use)

  fun <B> bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<E, Unit>, use: (A) -> IOOf<E, B>): IO<Throwable, B> =
    IOBracket(this, release, use)

  fun guarantee(finalizer: IOOf<E, Unit>): IO<Throwable, A> = guaranteeCase { finalizer }

  fun guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<E, Unit>): IO<Throwable, A> =
    IOBracket.guaranteeCase(this, finalizer)

  internal data class Pure<out E, out A>(val a: A) : IO<E, A>() {
    // Pure can be replaced by its value
    override fun <B> map(f: (A) -> B): IO<E, B> = Suspend { Pure(f(a)) }

    // Pure can be replaced by its value
    override fun <B> flatMap(f: (A) -> IOOf<E, B>): IO<E, B> = Suspend { f(a).fix() }

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = Some(a)
  }

  internal data class RaiseError<E>(val exception: E) : IO<E, Nothing>() {
    // Errors short-circuit
    override fun <B> map(f: (Nothing) -> B): IO<E, B> = this

    // Errors short-circuit
    override fun <B> flatMap(f: (Nothing) -> IOOf<E, B>): IO<E, B> = this

    override fun unsafeRunTimedTotal(limit: Duration): Option<Nothing> = throw exception
  }

  internal data class Delay<out E, out A>(val handler: (Throwable) -> E, val thunk: () -> A) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class Suspend<out E, out A>(val handler: (Throwable) -> E, val thunk: () -> IOOf<E, A>) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class Async<out E, out A>(val shouldTrampoline: Boolean = false, val k: (IOConnection, (Either<E, A>) -> Unit) -> Unit) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
  }

  internal data class Effect<out E, out A>(val ctx: CoroutineContext? = null, val handler: (Throwable) -> E, val effect: suspend () -> A) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
  }

  internal data class Bind<out E, C, out A>(val cont: IO<E, C>, val g: (C) -> IO<E, A>) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class ContinueOn<out E, A>(val cont: IO<E, A>, val cc: CoroutineContext) : IO<E, A>() {
    // If a ContinueOn follows another ContinueOn, execute only the latest
    override fun continueOn(ctx: CoroutineContext): IO<E, A> = ContinueOn(cont, ctx)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class ContextSwitch<out E, A>(
    val source: IO<E, A>,
    val modify: (IOConnection) -> IOConnection,
    val restore: ((Any?, Throwable?, IOConnection, IOConnection) -> IOConnection)?
  ) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")

    companion object {
      // Internal reusable reference.
      internal val makeUncancelable: (IOConnection) -> IOConnection = { IOConnection.uncancelable }

      internal val disableUncancelable: (Any?, Throwable?, IOConnection, IOConnection) -> IOConnection =
        { _, _, old, _ -> old }
    }
  }

  internal data class Map<out E, C, out A>(val source: IOOf<E, C>, val g: (C) -> A, val index: Int) : IO<E, A>(), (C) -> IO<E, A> {
    override fun invoke(value: C): IO<E, A> = just(g(value))

    override fun <B> map(f: (A) -> B): IO<E, B> =
    // Allowed to do maxStackDepthSize map operations in sequence before
      // starting a new Map fusion in order to avoid stack overflows
      if (index != maxStackDepthSize) Map(source, g.andThen(f), index + 1)
      else Map(this, f, 0)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }
}

fun <E, A> IOOf<E, A>.handleErrorWith(f: (E) -> IOOf<E, A>): IO<E, A> =
  IO.Bind(fix(), IOFrame.Companion.ErrorHandler(f))

fun <E, A> IOOf<E, A>.handleError(f: (Throwable) -> A): IO<E, A> =
  handleErrorWith { e -> IO.Pure(f(e)) }

fun <E, A> A.liftIO(): IO<E, A> = IO.just(this)
