package arrow.fx

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Some
import arrow.core.andThen
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.fx.IO.Bind
import arrow.fx.IO.Companion.async
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
import arrow.fx.typeclasses.ProcE
import arrow.fx.typeclasses.ProcEF
import arrow.fx.typeclasses.mapToUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ForIO private constructor() {
  companion object
}
typealias IOOf<E, A> = Kind2<ForIO, E, A>

typealias IOPartialOf<E> = Kind<ForIO, E>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <E, A> IOOf<E, A>.fix(): IO<E, A> =
  this as IO<E, A>

@Suppress("StringLiteralDuplication")
sealed class IO<out E, out A> : IOOf<E, A> {

  companion object : IOParMap2, IOParMap3, IORacePair, IORaceTriple {

    fun <A> just(a: A): IO<Nothing, A> = Pure(a)

    fun <E> raiseError(e: E): IO<E, Nothing> = RaiseError(e)

    fun <E, A> defer(f: () -> IOOf<E, A>): IO<E, A> =
      Suspend(f)

    fun <E, A> effect(fe: (Throwable) -> E, f: suspend () -> A): IO<E, A> =
      Effect(null, f).mapError(fe)

    // Specialization
    fun <A> effect(f: suspend () -> A): IO<Throwable, A> =
      Effect(null, f)

    fun <E, A> effect(ctx: CoroutineContext, fe: (Throwable) -> E, f: suspend () -> A): IO<E, A> =
      Effect(ctx, f).mapError(fe)

    // Specialization
    fun <A> effect(ctx: CoroutineContext? = null, f: suspend () -> A): IO<Throwable, A> =
      Effect(ctx, f)

    operator fun <E, A> invoke(fe: (Throwable) -> E, f: suspend () -> A): IO<E, A> =
      invoke(f).mapError(fe)

    // Specialization
    operator fun <A> invoke(f: suspend () -> A): IO<Throwable, A> =
      Effect(null, f)

    operator fun <E, A> invoke(ctx: CoroutineContext, fe: (Throwable) -> E, f: suspend () -> A): IO<E, A> =
      effect(ctx, f).mapError(fe)

    // Specialization
    operator fun <A> invoke(ctx: CoroutineContext, f: suspend () -> A): IO<Throwable, A> =
      Effect(ctx, f)

    fun <E, A> later(f: () -> A): IO<E, A> =
      defer { Pure<E, A>(f()) }

    fun <E, A> async(k: ProcE<E, A>): IO<E, A> =
      Async(false) { _: IOConnection, ff: (Either<E, A>) -> Unit ->
        k(onceOnly(ff))
      }

    fun <E, A> asyncF(k: ProcEF<IOPartialOf<E>, E, A>): IO<E, A> =
      Async(false) { conn: IOConnection, ff: (Either<E, A>) -> Unit ->
        val conn2 = IOConnection()
        conn.push(conn2.cancel())
        onceOnly(conn, ff).let { callback: (Either<E, A>) -> Unit ->
          val fa: IOOf<E, Unit> = k(callback)

          IORunLoop.startCancelable(fa, conn2) { result ->
            result.fold({ e -> callback(Left(e)) }, mapToUnit)
          }
        }
      }

    fun <E, A> cancelable(fe: (Throwable) -> E, cb: ((Either<E, A>) -> Unit) -> CancelToken<IOPartialOf<Throwable>>): IO<E, A> =
      Async(false) { conn: IOConnection, cbb: (Either<E, A>) -> Unit ->
        onceOnly(conn, cbb).let { cbb2 ->
          val cancelable = ForwardCancelable()
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

    fun <E, A> cancelableF(fe: (Throwable) -> E, cb: ((Either<E, A>) -> Unit) -> IOOf<E, CancelToken<IOPartialOf<Throwable>>>): IO<E, A> =
      Async(false) { conn: IOConnection, cbb: (Either<E, A>) -> Unit ->
        val cancelable = ForwardCancelable()
        val conn2 = IOConnection()
        conn.push(cancelable.cancel())
        conn.push(conn2.cancel())

        onceOnly(conn, cbb).let { cbb2 ->
          val fa: IOOf<E, CancelToken<IOPartialOf<Throwable>>> = try {
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
          is Left -> tailRecM(it.a, f)
          is Right -> just(it.b)
        }
      }

    val never: IO<Nothing, Nothing> =
      async { }
  }

  suspend fun suspended(): A = suspendCoroutine { cont ->
    IORunLoop.start(this) {
      it.fold({ cont.resumeWithException(UncaughtError(it).asException()) }, cont::resume)
    }
  }

  open fun <B> map(f: (A) -> B): IO<E, B> =
    Map(this, f, 0)

  open fun <B> mapError(f: (E) -> B): IO<B, A> =
    MapError(this, f, 0)

  open fun continueOn(ctx: CoroutineContext): IO<E, A> =
    ContinueOn(this, ctx)

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
    override fun <B> map(f: (A) -> B): IO<E, B> = Suspend { Pure<E, B>(f(a)) }

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

  internal data class Suspend<E, out A>(val thunk: () -> IOOf<E, A>) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class Async<E, out A>(val shouldTrampoline: Boolean = false, val k: (IOConnection, (Either<E, A>) -> Unit) -> Unit) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
  }

  internal data class Effect<out A>(val ctx: CoroutineContext? = null, val effect: suspend () -> A) : IO<Throwable, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
  }

  // Unsafe state
  internal data class Delay<out A>(val thunk: () -> A) : IO<Throwable, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  // Unsafe state
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
    val restore: ((Any?, Any?, IOConnection, IOConnection) -> IOConnection)?
  ) : IO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")

    companion object {
      // Internal reusable reference.
      internal val makeUncancelable: (IOConnection) -> IOConnection = { IOConnection.uncancelable }

      internal val disableUncancelable: (Any?, Any?, IOConnection, IOConnection) -> IOConnection =
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

data class UncaughtError<E>(val e: E) {
  fun asException(): Exception =
    UncaughtErrorException("There was an unhandled error $e")

  class UncaughtErrorException(s: String) : Exception(s)
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
    is IO.Pure -> Suspend { f(io.a).fix() }
    // Errors short-circuit
    is IO.RaiseError -> io
    else -> Bind(io) { f(it).fix() }
  }

fun <E, A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
  flatMap { a -> ff.fix().map { it(a) } }

fun <E, A> IOOf<E, A>.runAsyncCancellable(onCancel: OnCancel = Silent, cb: (Either<E, A>) -> IOOf<E, Unit>): IO<E, Disposable> =
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
    IORunLoop.startCancelable(fix(), conn, onCancelCb)
  }

fun <A, B> IOOf<Throwable, A>.bracket(release: (A) -> IOOf<Throwable, Unit>, use: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
  bracketCase({ a, _ -> release(a) }, use)

fun <A, B> IOOf<Throwable, A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Throwable, Unit>, use: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
  IOBracket(this, release, use)

fun <A> IOOf<Throwable, A>.guarantee(finalizer: IOOf<Throwable, Unit>): IO<Throwable, A> = guaranteeCase { finalizer }

fun <A> IOOf<Throwable, A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<Throwable, Unit>): IO<Throwable, A> =
  IOBracket.guaranteeCase(fix(), finalizer)

fun <B, E : B, A> IOOf<E, A>.widenError(): IO<B, A> =
  fix()

fun <A> A.liftIO(): IO<Nothing, A> = IO.just(this)

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
fun <A> IOOf<Throwable, A>.startFiber(ctx: CoroutineContext): IO<Throwable, Fiber<IOPartialOf<Throwable>, A>> = async { cb ->
  val promise = UnsafePromise<Throwable, A>()
  // A new IOConnection, because its cancellation is now decoupled from our current one.
  val conn = IOConnection()
  IORunLoop.startCancelable(IOForkedStart(this, ctx), conn, promise::complete)
  cb(Either.Right(IOFiber(promise, conn)))
}

// fun <E, A> IOOf<E, A>.startFiber(ctx: CoroutineContext, fe: (Throwable) -> E): IO<E, Fiber<IOPartialOf<E>, A>> =
//  TODO()
