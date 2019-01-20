package arrow.effects

import arrow.core.*
import arrow.core.Either.Left
import arrow.effects.OnCancel.Companion.CancellationException
import arrow.effects.OnCancel.Silent
import arrow.effects.OnCancel.ThrowCancellationException
import arrow.effects.internal.IOBracket
import arrow.effects.internal.Platform
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

typealias IO<A> = BIO<Throwable, A>
typealias ForIO = ForBIO
typealias IOOf<A> = BIOOf<Throwable, A>

@higherkind
@Suppress("StringLiteralDuplication")
sealed class BIO<out E, out A> : BIOOf<E, A> {

  companion object {

    fun <A> just(a: A): BIO<Nothing, A> =
      Pure(a)

    fun <E, A> raiseError(e: E): BIO<E, A> =
      RaiseError(e)

    operator fun <E, A> invoke(recovery: (Throwable) -> E, f: () -> A): BIO<E, A> =
      defer(recovery) { Pure<E, A>(f()) }

    fun <E, A> defer(recovery: (Throwable) -> E, f: () -> BIOOf<E, A>): BIO<E, A> =
      Suspend(recovery, f)

    fun <E, A> async(k: IOProc<A>): BIO<E, A> =
      Async { conn: IOConnection, ff: (Either<Throwable, A>) -> Unit ->
        onceOnly(ff).let { callback: (Either<Throwable, A>) -> Unit ->
          try {
            k(conn, callback)
          } catch (throwable: Throwable) {
            callback(Left(throwable))
          }
        }
      }

    fun <E, A> asyncF(k: IOProcF<A>): BIO<E, A> =
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

    operator fun <E, A> invoke(ctx: CoroutineContext, recovery: (Throwable) -> E, f: () -> A): BIO<E, A> =
      BIO.lazy.continueOn(ctx).flatMap { invoke(recovery, f) }

    private val unit: BIO<Nothing, Unit> =
      just(Unit)

    val lazy: BIO<Nothing, Unit> =
      Delay.safe { }

    fun <E, A> eval(eval: Eval<A>, recovery: (Throwable) -> E): BIO<E, A> =
      when (eval) {
        is Eval.Now -> just(eval.value)
        else -> invoke(recovery) { eval.value() }
      }

    fun <E, A, B> tailRecM(a: A, f: (A) -> BIOOf<Nothing, Either<A, B>>): BIO<E, B> =
      f(a).fix().flatMap {
        when (it) {
          is Either.Left -> tailRecM(it.a, f)
          is Either.Right -> BIO.just(it.b)
        }
      }

    val never: BIO<Nothing, Nothing> = async { _, _ -> Unit }

    /* For parMap, look into IOParallel */
  }

  open fun <B> map(f: (A) -> B): BIO<E, B> =
    Map(this, f, 0)

  fun <X> leftMap(f: (E) -> X): BIO<X, A> =
    attempt<E, X, A>().flatMap {
      it.fold({ raiseError<X, A>(f(it)) }, { just(it) })
    }

  fun <X, B> bimap(f: (E) -> X, g: (A) -> B): BIO<X, B> =
    attempt<E, X, A>().flatMap {
      it.fold({ raiseError<X, B>(f(it)) }, { just(g(it)) })
    }

  open fun continueOn(ctx: CoroutineContext): BIO<E, A> =
    ContinueOn(this, ctx)

  fun runAsync(cb: (Either<Throwable, A>) -> BIOOf<Nothing, Unit>): BIO<Nothing, Unit> =
    Delay.safe { unsafeRunAsync(cb.andThen { it.fix().unsafeRunAsync { } }) }

  fun unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
    IORunLoop.start(this, cb)

  fun runAsyncCancellable(onCancel: OnCancel = Silent, cb: (Either<Throwable, A>) -> BIOOf<Nothing, Unit>): BIO<E, Disposable> =
    BIO.async { _ /* The start of this execution is immediate and uncancellable */, ccb ->
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
  fun uncancelable(): BIO<E, A> =
    BIO.ContextSwitch(this, ContextSwitch.makeUncancelable, ContextSwitch.disableUncancelable())

  fun <B> bracket(release: (A) -> BIOOf<Nothing, Unit>, use: (A) -> BIOOf<Nothing, B>): BIO<E, B> =
    bracketCase({ a, _ -> release(a) }, use)

  fun <B> bracketCase(release: (A, ExitCase<Throwable>) -> BIOOf<Nothing, Unit>, use: (A) -> BIOOf<Nothing, B>): BIO<E, B> =
    IOBracket(this, release, use)

  fun guarantee(finalizer: BIOOf<Nothing, Unit>): BIO<E, A> = guaranteeCase { finalizer }

  fun guaranteeCase(finalizer: (ExitCase<Throwable>) -> BIOOf<Nothing, Unit>): BIO<E, A> =
    IOBracket.guaranteeCase(this, finalizer)

  internal data class Pure<E, out A>(val a: A) : BIO<E, A>() {
    // Pure can be replaced by its value
    override fun <B> map(f: (A) -> B): BIO<E, B> = Delay.safe { f(a) }

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = Some(a)
  }

  internal data class RaiseError<E>(val exception: E) : BIO<E, Nothing>() {
    // Errors short-circuit
    override fun <B> map(f: (Nothing) -> B): BIO<E, B> = this

    override fun unsafeRunTimedTotal(limit: Duration): Option<Nothing> =
      throw Platform.CustomException(exception)
  }

  internal data class Delay<E, out A>(val recovery: (Throwable) -> E, val thunk: () -> A) : BIO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")

    companion object {
      internal fun <E, A> safe(f: () -> A): BIO<E, A> =
        invoke({ e: Throwable -> throw e /* never happens */ }, f)
    }
  }

  internal data class Suspend<E, out A>(val recovery: (Throwable) -> E, val thunk: () -> BIOOf<E, A>) : BIO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class Async<E, out A>(val k: IOProc<A>) : BIO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
  }

  internal data class Bind<E, I, out A>(val cont: BIO<E, I>, val g: (I) -> BIO<E, A>) : BIO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class ContinueOn<E, A>(val cont: BIO<E, A>, val cc: CoroutineContext) : BIO<E, A>() {
    // If a ContinueOn follows another ContinueOn, execute only the latest
    override fun continueOn(ctx: CoroutineContext): BIO<E, A> = ContinueOn(cont, ctx)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }

  internal data class ContextSwitch<E, A>(
    val source: BIO<E, A>,
    val modify: (IOConnection) -> IOConnection,
    val restore: ((Any?, Throwable?, IOConnection, IOConnection) -> IOConnection)?) : BIO<E, A>() {
    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")

    companion object {
      //Internal reusable reference.
      internal val makeUncancelable: (IOConnection) -> IOConnection = { IOConnection.uncancelable }

      internal fun <A> disableUncancelable(): (A, Throwable?, IOConnection, IOConnection) -> IOConnection =
        { _, _, old, _ -> old }
    }
  }

  internal data class Map<E, I, out A>(val source: BIOOf<E, I>, val g: (I) -> A, val index: Int) : BIO<E, A>(), (I) -> BIO<Nothing, A> {
    override fun invoke(value: I): BIO<Nothing, A> = just(g(value))

    override fun <B> map(f: (A) -> B): BIO<E, B> =
    // Allowed to do maxStackDepthSize map operations in sequence before
    // starting a new Map fusion in order to avoid stack overflows
      if (index != maxStackDepthSize) Map(source, g.andThen(f), index + 1)
      else Map(this, f, 0)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
  }
}

// Victims of variance

fun <E, A, B> BIO<E, A>.ap(ff: BIOOf<E, (A) -> B>): BIO<E, B> =
  fix().flatMap { a -> ff.fix().map { it(a) } }

fun <E : X, X, A, B> BIO<E, A>.flatMap(f: (A) -> BIO<X, B>): BIO<X, B> =
  BIO.Bind(this) { f(it).fix() }

fun <E, X, A> BIO<E, A>.attempt(): BIO<X, Either<E, A>> =
  BIO.Bind(this, IOFrame.any())

fun <E, A> BIO<E, A>.handleErrorWith(f: (Throwable) -> BIOOf<E, A>): BIO<E, A> =
  BIO.Bind(fix(), IOFrame.errorHandler(f))

fun <A> A.liftIO(): BIO<Nothing, A> = BIO.just(this)

// Specializations for IO

fun <A> BIO.Companion.defer(f: () -> IO<A>): IO<A> =
  BIO.Suspend(::identity, f)

operator fun <A> BIO.Companion.invoke(f: () -> A): IO<A> =
  defer(::identity) { BIO.just(f()) }

operator fun <A> BIO.Companion.invoke(ctx: CoroutineContext, f: () -> A): IO<A> =
  BIO.lazy.continueOn(ctx).flatMap { invoke(f) }

fun <A> eval(eval: Eval<A>): IO<A> =
  BIO.eval(eval, ::identity)
