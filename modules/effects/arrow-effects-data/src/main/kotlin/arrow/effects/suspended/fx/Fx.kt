package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.NonFatal
import arrow.core.Right
import arrow.core.andThen
import arrow.core.nonFatalOrThrow
import arrow.data.AndThen
import arrow.effects.KindConnection
import arrow.effects.OnCancel
import arrow.effects.fork
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.ConnectedProc
import arrow.effects.typeclasses.ConnectedProcF
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.mapUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine

class ForFx private constructor() {
  companion object
}
typealias FxOf<A> = Kind<ForFx, A>
typealias FxProc<A> = ConnectedProc<ForFx, A>
typealias FxProcF<A> = ConnectedProcF<ForFx, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> FxOf<A>.fix(): Fx<A> =
  this as Fx<A>

suspend inline operator fun <A> FxOf<A>.not(): A = !fix()

sealed class Fx<out A> : FxOf<A> {

  @PublishedApi
  internal inline fun <B> unsafeRecast(): Fx<B> = this as Fx<B>

  fun <B> followedBy(fa: FxOf<B>): Fx<B> =
    flatMap { fa }

  inline val fa: suspend () -> A
    get() = when (this) {
      is RaiseError -> suspend { throw error }
      is Pure -> suspend { value }
      is Single -> suspend { source() }
      is Lazy -> suspend { source(Unit) }
      is Defer -> suspend { FxRunLoop(thunk()) }
      is Map<*, A> -> suspend { FxRunLoop(this) } //g(FxRunLoop(source)) in Any? land
      is FlatMap<*, A> -> suspend { FxRunLoop(this) }
      is UpdateContext -> suspend { FxRunLoop(this) }
      is ContinueOn -> suspend { FxRunLoop(this) }
      is ConnectionSwitch -> suspend { FxRunLoop(this) }
      is Async -> suspend { FxRunLoop(this) }
    }

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> map(noinline f: (A) -> B): Fx<B> = when (this) {
    is RaiseError -> unsafeRecast()
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
            is RaiseError -> fa.unsafeRecast()
            is Lazy -> {
              try {
                fa.source = { f(fa.source(Unit) as A) }
                fa
              } catch (e: Throwable) {
                RaiseError<B>(e.nonFatalOrThrow())
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
  }

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  suspend inline operator fun not(): A = when (this) {
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
  }

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> flatMap(noinline f: (A) -> FxOf<B>): Fx<B> = when (this) {
    is RaiseError -> unsafeRecast()
    is Pure -> Defer { f(value) }
    is Single -> FlatMap(this, f, 0)
    is Lazy -> Defer { f(source(Unit)) }
    is Defer -> FlatMap(this, f, 0)
    is Map<*, *> -> {
      val gg = this.g as (Any?) -> A
      FlatMap(source, { f(gg(it)) }, 1)
    }
    is FlatMap<*, *> -> {

      if (index != Platform.maxStackDepthSize) FlatMap(this, f, 0)
      else FlatMap(source, { a ->
        val fbb = fb as (Any?) -> FxOf<A>
        when (val fx: Fx<A> = fbb(a).fix()) {
          is Pure -> f(fx.value)
          is RaiseError -> fx.unsafeRecast()
          is Lazy -> {
            try {
              f(fx.source(Unit))
            } catch (e: Throwable) {
              RaiseError<B>(e.nonFatalOrThrow())
            }
          }
          else -> FlatMap(fx, f, 0)
        }
      }, index + 1)
    }
    is UpdateContext -> FlatMap(this, f, 0)
    is ContinueOn -> FlatMap(this, f, 0)
    is ConnectionSwitch -> FlatMap(this, f, 0)
    is Async -> FlatMap(this, f, 0)
  }

  suspend inline fun bind(): A = !this

  @PublishedApi
  internal class RaiseError<A>(@JvmField val error: Throwable) : Fx<A>() {
    override fun toString(): String = "Fx.RaiseError(error = $error)"
  }

  @PublishedApi
  internal class Pure<A>(@JvmField var internalValue: Any?, var index: Int) : Fx<A>() {
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
    override fun invoke(value: A): Fx<B> = Pure(g(value), 0)
    override fun toString(): String = "Fx.Map(...)"
  }

  @PublishedApi
  internal class FlatMap<A, B>(
    @JvmField var source: FxOf<A>,
    @JvmField var fb: (A) -> FxOf<B>,
    @JvmField var index: Int
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
  internal class Async<A> internal constructor(
    val ctx: CoroutineContext? = null,
    val updateContext: ((CoroutineContext) -> CoroutineContext)? = null,
    val proc: FxProc<A>
  ) : Fx<A>() {

    companion object {

      @JvmStatic
      operator fun <A> invoke(proc: FxProc<A>): Fx<A> = Async { conn: FxConnection, ff: (Either<Throwable, A>) -> Unit ->
        Platform.onceOnly(ff).let { callback: (Either<Throwable, A>) -> Unit ->
          try {
            proc(conn, callback)
          } catch (throwable: Throwable) {
            if (NonFatal(throwable)) {
              callback(Either.Left(throwable))
            } else {
              throw throwable
            }
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
            // DEV: If fa cancels conn2 like so `conn.cancel().map { cb(Right(Unit)) }`
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

  suspend inline operator fun invoke(): A =
    !this

//  suspend inline operator fun component1(): A =
//    !this

  fun <B> ap(ff: FxOf<(A) -> B>): Fx<B> =
    ff.fix().flatMap { map(it) }

  fun ensure(
    error: () -> Throwable,
    predicate: (A) -> Boolean
  ): Fx<A> = when (this) {
    is RaiseError -> this
    is Pure -> if (!predicate(value)) RaiseError(error()) else this
    else -> flatMap { result ->
      if (!predicate(result)) RaiseError<A>(error())
      else Pure<A>(result, 0)
    }
  }

  fun attempt(): Fx<Either<Throwable, A>> =
    FlatMap(this, FxFrame.attempt(), 0)

  fun updateContext(f: (CoroutineContext) -> CoroutineContext): Fx<A> =
    UpdateContext(this, f)

  fun continueOn(ctx: CoroutineContext): Fx<A> = when (this) {
    is ContinueOn -> this.apply {
      this.ctx = ctx
    }
    else -> ContinueOn(this, ctx)
  }

  /**
   * [fork] runs this [Fx] value concurrently in a [Fiber] within a safe [Fx] environment.
   * A [Fiber] is a function pair that you can use to [Fiber.join] or [Fiber.cancel] a concurrently running [Fx].
   *
   * @see Fx.Companion.racePair for another combinator using [Fiber]
   * @see Concurrent a tagless version of this operators derived and build from [fork].
   */
  fun fork(ctx: CoroutineContext): Fx<Fiber<ForFx, A>> = async { oldConn, cb ->
    val promise = UnsafePromise<A>()
    val conn = FxConnection()
    oldConn.push(conn.cancel())
    conn.push(oldConn.cancel())

    FxRunLoop.startCancelable(this, conn, ctx) {
      promise.complete(it)
    }

    cb(Right(FxFiber(promise, conn)))
  }

  /**
   * Makes the source [Fx] uncancelable and switches back to the original cancellation connection after running [this].
   */
  fun uncancelable(): Fx<A> =
    ConnectionSwitch(this, ConnectionSwitch.makeUncancelable, { _, _, old, _ -> old })

  @RestrictsSuspension
  companion object {

    @JvmStatic
    operator fun <A> invoke(fa: suspend () -> A): Fx<A> = Single(fa)

    @JvmStatic
    operator fun <A> invoke(ctx: CoroutineContext, f: () -> A): Fx<A> =
      Map(UpdateContext(unit) { ctx }) { f() }

    @JvmStatic
    fun <A> just(a: A): Fx<A> = Pure(a, 0)

    @JvmStatic
    val unit: Fx<Unit> = Pure(Unit, 0)

    @JvmStatic
    val lazy: Fx<Unit> = Lazy { Unit }

    @JvmStatic
    fun <A> lazy(f: (Unit) -> A): Fx<A> = Lazy(f)

    @JvmStatic
    val never: Fx<Nothing> = async { _, _ -> Unit }

    @JvmStatic
    fun <A> eval(eval: Eval<A>): Fx<A> = when (eval) {
      is Eval.Now -> just(eval.value)
      else -> Lazy { eval.value() }
    }

    @JvmStatic
    fun <A> raiseError(e: Throwable): Fx<A> = RaiseError(e)

    @JvmStatic
    fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      Defer(fa)

    @JvmStatic
    fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> =
      FlatMap(f(a), { result ->
        result.fold({ tailRecM(it, f) }, { just(it) })
      }, 0)

    @JvmStatic
    fun <A> async(fa: FxProc<A>): Fx<A> = Async(fa)

    @JvmStatic
    fun <A> asyncF(fa: FxProcF<A>): Fx<A> = Async.invokeF(fa)

    /**
     * [unsafeRunBlocking] allows you to run any [Fx] to its wrapped value [A].
     *
     * It's called unsafe because it immediately runs the effects wrapped in [Fx],
     * and thus is **not** referentially transparent.
     *
     * **NOTE:** this function throws whenever an error occurs during computation.
     *
     * @param fx the [Fx] to run
     * @return the resulting value
     * @see [unsafeRunBlocking] or [unsafeRunNonBlockingCancellable] that run the value as [Either].
     * @see [runNonBlocking] to run in a referential transparent manner.
     */
    @JvmStatic
    fun <A> unsafeRunBlocking(fx: FxOf<A>): A = when (val current = fx.fix()) {
      is RaiseError -> throw current.error
      is Pure -> current.value
      is Lazy -> current.source(Unit)
      is Single -> UnsafePromise<A>().run {
        current.source.startCoroutine(asyncContinuation(EmptyCoroutineContext) {
          complete(it)
        })
        await()
      }
      else -> {
        when (val result = FxRunLoop.step(fx.fix())) {
          is Pure -> result.value
          is RaiseError -> throw result.error
          is Lazy -> result.source(Unit)
          is Single -> UnsafePromise<A>().run {
            result.source.startCoroutine(asyncContinuation(EmptyCoroutineContext) {
              complete(it)
            })
            await()
          }
          else -> UnsafePromise<A>().run {
            FxRunLoop.start(fx) { complete(it) }
            await()
          }
        }
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
      unsafeRunBlocking(runNonBlockingCancellable(fx, onCancel, cb.andThen { Fx.unit }))

    @JvmStatic
    fun <A> runNonBlockingCancellable(fx: FxOf<A>, onCancel: OnCancel = OnCancel.Silent, cb: (Either<Throwable, A>) -> Fx<Unit>): Fx<Disposable> =
      async { _ /* The start of this execution is immediate and uncancellable */, cbb ->
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
}

fun <A, B> FxOf<A>.redeem(fe: (Throwable) -> B, fs: (A) -> B): Fx<B> =
  Fx.FlatMap(this, FxFrame.Companion.Redeem(fe, fs), 0)

fun <A, B> FxOf<A>.redeemWith(fe: (Throwable) -> FxOf<B>, fs: (A) -> FxOf<B>): Fx<B> =
  Fx.FlatMap(this, FxFrame.Companion.RedeemWith(fe, fs), 0)

fun <A> FxOf<A>.handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> =
  Fx.FlatMap(this, FxFrame.Companion.ErrorHandler(f), 0)

fun <A> FxOf<A>.handleError(f: (Throwable) -> A): Fx<A> = when (this) {
  is Fx.RaiseError -> Fx { f(error) }
  is Fx.Pure -> this
  else -> Fx.FlatMap(this, FxFrame.Companion.ErrorHandler(f.andThen { Fx.Pure<A>(it, 0) }), 0)
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
