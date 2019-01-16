package arrow.effects

import arrow.core.*
import arrow.core.Either.Left
import arrow.effects.OnCancel.Companion.CancellationException
import arrow.effects.OnCancel.Silent
import arrow.effects.OnCancel.ThrowCancellationException
import arrow.effects.internal.IOBracket
import arrow.effects.internal.Platform.maxStackDepthSize
import arrow.effects.internal.Platform.onceOnly
import arrow.effects.internal.Platform.unsafeResync
import arrow.effects.typeclasses.*
import arrow.higherkind
import kotlin.coroutines.CoroutineContext

typealias IOProc<A> = (IOConnection, (Either<Throwable, A>) -> Unit) -> Unit
typealias IOProcF<A> = (IOConnection, (Either<Throwable, A>) -> Unit) -> IOOf<Unit>

fun <A> Proc<A>.toIOProc(): IOProc<A> = { _: IOConnection, proc -> this(proc) }
fun <A> ProcF<ForIO, A>.toIOProcF(): IOProcF<A> = { _: IOConnection, proc -> this(proc) }

@higherkind
@Suppress("StringLiteralDuplication")
sealed class IO<out A> : IOOf<A> {

  companion object {

    fun <A> just(a: A): IO<A> = Pure(a)

    fun <A> raiseError(e: Throwable): IO<A> = RaiseError(e)

    operator fun <A> invoke(f: () -> A): IO<A> = defer { Pure(f()) }

    fun <A> defer(f: () -> IOOf<A>): IO<A> = Suspend(f)

    fun <A> async(k: IOProc<A>): IO<A> =
      Async { conn: IOConnection, ff: (Either<Throwable, A>) -> Unit ->
        onceOnly(ff).let { callback: (Either<Throwable, A>) -> Unit ->
          try {
            k(conn, callback)
          } catch (throwable: Throwable) {
            callback(Left(throwable))
          }
        }
      }

    fun <A> asyncF(k: IOProcF<A>): IO<A> =
      Async { conn: IOConnection, ff: (Either<Throwable, A>) -> Unit ->
        val conn2 = IOConnection()
        conn.push(conn2.cancel())
        onceOnly(conn, ff).let { callback: (Either<Throwable, A>) -> Unit ->
          val fa = try {
            k(conn2, callback)
          } catch (t: Throwable) {
            IO { callback(Left(t)) }
          }

          IORunLoop.startCancelable(fa, conn2) { result ->
            // DEV: If fa cancels conn2 like so `conn.cancel().map { cb(Right(Unit)) }`
            // It doesn't run the stack of conn2, instead the result is seen in the cb of startCancelable.
            val resultCancelled = result.fold({ e -> e == OnCancel.CancellationException }, { false })
            if (resultCancelled && conn.isNotCanceled()) IORunLoop.start(conn.cancel(), mapUnit)
            else Unit
          }
        }
      }

    operator fun <A> invoke(ctx: CoroutineContext, f: () -> A): IO<A> =
      IO.unit.continueOn(ctx).flatMap { invoke(f) }

    val unit: IO<Unit> =
      just(Unit)

    val lazy: IO<Unit> =
      invoke { }

    fun <A> eval(eval: Eval<A>): IO<A> =
      when (eval) {
        is Eval.Now -> just(eval.value)
        else -> invoke { eval.value() }
      }

    fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> =
      f(a).fix().flatMap {
        when (it) {
          is Either.Left -> tailRecM(it.a, f)
          is Either.Right -> IO.just(it.b)
        }
      }

    val never: IO<Nothing> = async { _, _ -> Unit }

    /* For parMap, look into IOParallel */
  }

  open fun <B> map(f: (A) -> B): IO<B> =
    Map(this, f, 0)

  open fun <B> flatMap(f: (A) -> IOOf<B>): IO<B> =
    Bind(this) { f(it).fix() }

  open fun continueOn(ctx: CoroutineContext): IO<A> =
    ContinueOn(this, ctx)

  fun attempt(): IO<Either<Throwable, A>> =
    Bind(this, IOFrame.any())

  fun runAsync(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Unit> =
    IO { unsafeRunAsync(cb.andThen { it.fix().unsafeRunAsync { } }) }

  fun unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
    IORunLoop.start(this, cb)

  fun runAsyncCancellable(onCancel: OnCancel = Silent, cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Disposable> =
    IO.async { _ /* The start of this execution is immediate and uncancellable */, ccb ->
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
  fun uncancelable(): IO<A> =
    IO.ContextSwitch(this, ContextSwitch.makeUncancelable, ContextSwitch.disableUncancelable())

  fun <B> bracket(release: (A) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    bracketCase({ a, _ -> release(a) }, use)

  fun <B> bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    IOBracket(this, release, use)

  fun guarantee(finalizer: IOOf<Unit>): IO<A> = guaranteeCase { finalizer }

  fun guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<Unit>): IO<A> =
    IOBracket.guaranteeCase(this, finalizer)

  internal data class Pure<out A>(val a: A) : IO<A>() {
    // Pure can be replaced by its value
    override fun <B> map(f: (A) -> B): IO<B> = Suspend { Pure(f(a)) }

    // Pure can be replaced by its value
    override fun <B> flatMap(f: (A) -> IOOf<B>): IO<B> = Suspend { f(a).fix() }

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = Some(a)
  }

  internal data class RaiseError(val exception: Throwable) : IO<Nothing>() {
    // Errors short-circuit
    override fun <B> map(f: (Nothing) -> B): IO<B> = this

    // Errors short-circuit
    override fun <B> flatMap(f: (Nothing) -> IOOf<B>): IO<B> = this

    override fun unsafeRunTimedTotal(limit: Duration): Option<Nothing> = throw exception
  }

  internal data class Delay<out A>(val thunk: () -> A) : IO<A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class Suspend<out A>(val thunk: () -> IOOf<A>) : IO<A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class Async<out A>(val k: IOProc<A>) : IO<A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
  }

  internal data class Bind<E, out A>(val cont: IO<E>, val g: (E) -> IO<A>) : IO<A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class ContinueOn<A>(val cont: IO<A>, val cc: CoroutineContext) : IO<A>() {
    // If a ContinueOn follows another ContinueOn, execute only the latest
    override fun continueOn(ctx: CoroutineContext): IO<A> = ContinueOn(cont, ctx)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class ContextSwitch<A>(
    val source: IO<A>,
    val modify: (IOConnection) -> IOConnection,
    val restore: ((Any?, Throwable?, IOConnection, IOConnection) -> IOConnection)?) : IO<A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")

    companion object {
      //Internal reusable reference.
      internal val makeUncancelable: (IOConnection) -> IOConnection = { IOConnection.uncancelable }

      internal fun <A> disableUncancelable(): (A, Throwable?, IOConnection, IOConnection) -> IOConnection =
        { _, _, old, _ -> old }
    }
  }

  internal data class Map<E, out A>(val source: IOOf<E>, val g: (E) -> A, val index: Int) : IO<A>(), (E) -> IO<A> {
    override fun invoke(value: E): IO<A> = just(g(value))

    override fun <B> map(f: (A) -> B): IO<B> =
    // Allowed to do maxStackDepthSize map operations in sequence before
    // starting a new Map fusion in order to avoid stack overflows
      if (index != maxStackDepthSize) Map(source, g.andThen(f), index + 1)
      else Map(this, f, 0)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }
}

fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
  fix().flatMap { a -> ff.fix().map { it(a) } }

fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
  IO.Bind(fix(), IOFrame.errorHandler(f))

fun <A> A.liftIO(): IO<A> = IO.just(this)
