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
import arrow.fx.IO.Bind
import arrow.fx.IO.Suspend
import arrow.fx.IOFrame.Companion.RedeemWith
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

    fun <A> just(a: A): IO<Nothing, A> = Pure(a)

    fun <E> raiseError(e: E): IO<E, Nothing> = RaiseError(e)

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
    fun <A> cancelable(cb: ((Either<Throwable, A>) -> Unit) -> CancelToken<IOPartialOf<Throwable>>): IO<Throwable, A> =
      cancelable(::identity, cb)

    fun <E, A> cancelableF(fe: (Throwable) -> E, cb: ((Either<E, A>) -> Unit) -> IOOf<E, CancelToken<IOPartialOf<E>>>): IO<E, A> =
      Async { conn: IOConnection, cbb: (Either<E, A>) -> Unit ->
        val cancelable = ForwardCancelable<E>()
        val conn2 = IOConnection()
        conn.push(cancelable.cancel())
        conn.push(conn2.cancel())

        onceOnly(conn, cbb).let { cbb2 ->
          val fa: IOOf<E, CancelToken<IOPartialOf<E>>> = try {
            cb(cbb2)
          } catch (throwable: Throwable) {
            cbb2(Left(fe(throwable.nonFatalOrThrow())))
            just(unit)
          }

          IORunLoop.startCancelable(fa, conn2) { result ->
            conn.pop()
            result.fold({ }, cancelable::complete)
          }
        }
      }

    // Specialization
    fun <A> cancelableF(cb: ((Either<Throwable, A>) -> Unit) -> IOOf<Throwable, CancelToken<IOPartialOf<Throwable>>>): IO<Throwable, A> =
      cancelableF(::identity, cb)

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

    val rethrow: (Throwable) -> Nothing =
      { t -> throw t }

    val unit: IO<Nothing, Unit> =
      just(Unit)

    val lazy: IO<Nothing, Unit> =
      invoke(rethrow) { }

    fun <A> eval(eval: Eval<A>): IO<Nothing, A> =
      when (eval) {
        is Eval.Now -> just(eval.value)
        else -> invoke(rethrow) { eval.value() }
      }

    fun <E, A, B> tailRecM(a: A, f: (A) -> IOOf<E, Either<A, B>>): IO<E, B> =
      f(a).fix().flatMap {
        when (it) {
          is Either.Left -> tailRecM(it.a, f)
          is Either.Right -> just(it.b)
        }
      }

    val never: IO<Nothing, Nothing> =
      async(rethrow) { }
  }

  suspend fun suspended(): A = suspendCoroutine { cont ->
    IORunLoop.start(this) {
      it.fold(cont::resumeWithException, cont::resume)
    }
  }

  open fun <B> map(f: (A) -> B): IO<E, B> =
    Map(this, f, 0)

  open fun <B> mapError(f: (E) -> B): IO<B, A> =
    MapError(this, f, 0)

  open fun continueOn(ctx: CoroutineContext): IO<E, A> =
    ContinueOn(this, ctx)

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
  fun startFiber(ctx: CoroutineContext): IO<Throwable, Fiber<IOPartialOf<Throwable>, A>> = async { cb ->
    val promise = UnsafePromise<A>()
    // A new IOConnection, because its cancellation is now decoupled from our current one.
    val conn = IOConnection()
    IORunLoop.startCancelable(IOForkedStart(this, ctx), conn, promise::complete)
    cb(Either.Right(IOFiber(promise, conn)))
  }

  fun attempt(): IO<E, Either<E, A>> =
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
  fun <B> redeem(fe: (E) -> B, fb: (A) -> B): IO<E, B> =
    Bind(this, IOFrame.Companion.Redeem(fe, fb))

  fun unsafeRunAsync(cb: (Either<E, A>) -> Unit): Unit =
    IORunLoop.start(this, cb)

  fun unsafeRunAsyncCancellable(onCancel: OnCancel = Silent, cb: (Either<E, A>) -> Unit): Disposable =
    runAsyncCancellable(onCancel, cb andThen { it.liftIO() }).unsafeRunSync()

  fun unsafeRunSync(): A =
    unsafeRunTimed(Duration.INFINITE)
      .fold({ throw IllegalArgumentException("IO execution should yield a valid result") }, ::identity)

  fun unsafeRunTimed(limit: Duration): Option<A> = IORunLoop.step(this).unsafeRunTimedTotal(limit)

  internal abstract fun unsafeRunTimedTotal(limit: Duration): Option<A>

  /** Makes the source [IO] uncancelable such that a [Fiber.cancel] signal has no effect. */
  fun uncancelable(): IO<E, A> =
    ContextSwitch(this, ContextSwitch.makeUncancelable, ContextSwitch.disableUncancelable)

  internal data class Pure<E, out A>(val a: A) : IO<E, A>() {
    // Pure can be replaced by its value
    override fun <B> map(f: (A) -> B): IO<E, B> = Suspend(rethrow) { Pure(f(a)) }

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = Some(a)
  }

  internal data class RaiseError<E>(val exception: E) : IO<E, Nothing>() {
    // Errors short-circuit
    override fun <B> map(f: (Nothing) -> B): IO<E, B> = this

    // RaiseError can be replaced by its value
    override fun <B> mapError(f: (E) -> B): IO<B, Nothing> =
      RaiseError(f(exception))

    override fun unsafeRunTimedTotal(limit: Duration): Option<Nothing> {
      if (exception is Throwable) {
        throw exception
      } else {
        throw AssertionError("Unreachable $exception")
      }
    }
  }

  internal data class Delay<E, out A>(val handler: (Throwable) -> E, val thunk: () -> A) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class Suspend<E, out A>(val handler: (Throwable) -> E, val thunk: () -> IOOf<E, A>) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class Async<E, out A>(val shouldTrampoline: Boolean = false, val k: (IOConnection, (Either<E, A>) -> Unit) -> Unit) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
  }

  internal data class Effect<E, out A>(val ctx: CoroutineContext? = null, val handler: (Throwable) -> E, val effect: suspend () -> A) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
  }

  internal data class Bind<E, C, out A>(val cont: IO<E, C>, val g: (C) -> IO<E, A>) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class ContinueOn<E, A>(val cont: IO<E, A>, val cc: CoroutineContext) : IO<E, A>() {
    // If a ContinueOn follows another ContinueOn, execute only the latest
    override fun continueOn(ctx: CoroutineContext): IO<E, A> = ContinueOn(cont, ctx)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class ContextSwitch<E, A>(
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

  internal data class Map<E, C, out A>(val source: IOOf<E, C>, val g: (C) -> A, val index: Int) : IO<E, A>(), (C) -> IO<E, A> {
    override fun invoke(value: C): IO<E, A> = just(g(value))

    override fun <B> map(f: (A) -> B): IO<E, B> =
    // Allowed to do maxStackDepthSize map operations in sequence before
      // starting a new Map fusion in order to avoid stack overflows
      if (index != maxStackDepthSize) Map(source, g.andThen(f), index + 1)
      else Map(this, f, 0)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class MapError<E, C, out A>(val source: IOOf<E, A>, val g: (E) -> C, val index: Int) : IO<C, A>(), (E) -> IO<C, A> {
    override fun invoke(value: E): IO<C, A> = raiseError(g(value))

    override fun <B> mapError(f: (C) -> B): IO<B, A> =
    // Allowed to do maxStackDepthSize map operations in sequence before
      // starting a new Map fusion in order to avoid stack overflows
      if (index != maxStackDepthSize) MapError(source, g.andThen(f), index + 1)
      else MapError(this, f, 0)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }
}

// These have to go here to work around lack of supertype on generics

fun <E, A> IOOf<E, A>.handleErrorWith(f: (E) -> IOOf<E, A>): IO<E, A> =
  Bind(fix(), IOFrame.Companion.ErrorHandler(f))

fun <E, A> IOOf<E, A>.handleError(f: (E) -> A): IO<E, A> =
  handleErrorWith { e -> IO.Pure(f(e)) }

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
fun <E, A, B> IOOf<E, A>.redeemWith(fe: (E) -> IOOf<E, B>, fb: (A) -> IOOf<E, B>): IO<E, B> =
  Bind(fix(), RedeemWith(fe, fb))

fun <E, A> IOOf<E, A>.runAsync(cb: (Either<E, A>) -> IOOf<E, Unit>): IO<E, Unit> =
  IO(IO.rethrow) { fix().unsafeRunAsync(cb.andThen { it.fix().unsafeRunAsync { } }) }

fun <E, A, B> IOOf<E, A>.followedBy(fb: IOOf<E, B>) = flatMap { fb }

fun <E, A, B> IOOf<E, A>.flatMap(f: (A) -> IOOf<E, B>): IO<E, B> =
  when (val io = fix()) {
    // Pure can be replaced by its value
    is IO.Pure -> Suspend(IO.rethrow) { f(io.a).fix() }
    // Errors short-circuit
    is IO.RaiseError -> io
    else -> Bind(io) { f(it).fix() }
  }

fun <E, A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
  flatMap { a -> ff.fix().map { it(a) } }

fun <E, A> IOOf<E, A>.runAsyncCancellable(onCancel: OnCancel = Silent, cb: (Either<E, A>) -> IOOf<E, Unit>): IO<E, Disposable> =
  IO.async(IO.rethrow) { ccb ->
    val conn = IOConnection()
    val onCancelCb =
      when (onCancel) {
        ThrowCancellationException ->
          cb andThen { it.fix().unsafeRunAsync { } }
        Silent ->
          { either -> either.fold({ if (!conn.isCanceled() || it != CancellationException) cb(either) }, { cb(either) }) }
      }
    ccb(conn.toDisposable().right())
    IORunLoop.startCancelable(fix(), conn, onCancelCb)
  }

fun <E, A, B> IOOf<E, A>.bracket(release: (A) -> IOOf<E, Unit>, use: (A) -> IOOf<E, B>): IO<E, B> =
  bracketCase({ a, _ -> release(a) }, use)

fun <E, A, B> IOOf<E, A>.bracketCase(release: (A, ExitCase<E>) -> IOOf<E, Unit>, use: (A) -> IOOf<E, B>): IO<E, B> =
  IOBracket(this, release, use)

fun <E, A> IOOf<E, A>.guarantee(finalizer: IOOf<E, Unit>): IO<E, A> = guaranteeCase { finalizer }

fun <E, A> IOOf<E, A>.guaranteeCase(finalizer: (ExitCase<E>) -> IOOf<E, Unit>): IO<E, A> =
  IOBracket.guaranteeCase(fix(), finalizer)

fun <A> A.liftIO(): IO<Nothing, A> = IO.just(this)
