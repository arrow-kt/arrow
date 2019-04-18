package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.*
import arrow.data.AndThen
import arrow.effects.*
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.typeclasses.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*

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

const val UnknownTag = -1
const val RaiseErrorTag = 0
const val PureTag = 1
const val SingleTag = 2
const val MapTag = 3
const val FlatMapTag = 4
const val ConnectionSwitchTag = 5
const val AsyncTag = 6
const val ModifyContextTag = 7
const val ContinueOnTag = 9

sealed class FxImpossibleBugs(message: String) : RuntimeException(message) {
  object Fxfa : FxImpossibleBugs("Fx.fa bug, please contact support with this message! https://arrow-kt.io")
  object FxMap: FxImpossibleBugs("FxMap bug, please contact support with this message! https://arrow-kt.io")
  object FxNot: FxImpossibleBugs("FxNot bug, please contact support with this message! https://arrow-kt.io")
  object FxFlatMap: FxImpossibleBugs("FxFlatMap bug, please contact support with this message! https://arrow-kt.io")

  override fun fillInStackTrace(): Throwable = this
}

sealed class Fx<A>(@JvmField var tag: Int = UnknownTag) : FxOf<A> {

  fun <B> followedBy(fa: FxOf<B>): Fx<B> =
    flatMap { fa }

  @Suppress("UNCHECKED_CAST")
  inline val fa: suspend () -> A
    get() =
      when (tag) {
        RaiseErrorTag -> suspend {
          throw (this as Fx.RaiseError<*>).error
        }
        PureTag -> suspend { (this as Fx.Pure<A>).value }
        SingleTag -> (this as Fx.Single<A>).source
        MapTag -> {
          this as Fx.Map<A, *>
          suspend {
            g(!source.fix())
          } as (suspend () -> A)
        }
        FlatMapTag -> {
          this as Fx.FlatMap<A, *>
          suspend { !fb(!source) } as (suspend () -> A)
        }
        ModifyContextTag -> suspend { FxRunLoop(this) }
        ContinueOnTag -> suspend { FxRunLoop(this) }
        ConnectionSwitchTag -> suspend { FxRunLoop(this) }
        AsyncTag -> suspend { FxRunLoop(this) }
        else -> throw FxImpossibleBugs.Fxfa
      }

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> map(noinline f: (A) -> B): Fx<B> =
    when (tag) {
      RaiseErrorTag -> this as Fx<B>
      PureTag -> {
        //Fx.Single { f((this as Fx.Pure<A>).value) }
        this as Fx.Pure<A>
        if (this.index != Platform.maxStackDepthSize) {
          try {
            Pure(f(this.value), index + 1)
          } catch (e: Throwable) {
            RaiseError<B>(e.nonFatalOrThrow())
          }
        } else {
          try {
            val b: B = unsafeRunBlocking(Single { f(this.value) })
            (this as Fx.Pure<B>).value = b
            this as Fx<B>
          } catch (e: Throwable) {
            RaiseError<B>(e.nonFatalOrThrow())
          }
        }
      }
      SingleTag -> Map(this, f)
      ConnectionSwitchTag -> Map(this, f)
      ModifyContextTag -> Map(this, f)
      AsyncTag -> Map(this, f)
      ContinueOnTag -> Map(this, f)
      MapTag -> {
        this as Fx.Map<Any?, Any?>
        val ff = f as (Any?) -> Any?
        //AndThen composes the functions in a stack-safe way by running them in a while loop.
        this.g = AndThen(g).andThen(ff)
        this as Fx<B>
      }
      FlatMapTag -> {
        //If we reach the maxStackDepthSize then we can fold the current FlatMap and return a Map case
        //If we haven't reached the maxStackDepthSize we fuse the map operator within the flatMap stack.
        this as Fx.FlatMap<B, A>
        if (index != Platform.maxStackDepthSize) Map(this, f)
        else FlatMap(source, { a -> Fx { f(!fb(a)) } }, index + 1)
      }
      else -> throw FxImpossibleBugs.FxMap
    }

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  suspend inline operator fun not(): A =
    when (tag) {
      RaiseErrorTag -> throw (this as Fx.RaiseError<*>).error
      PureTag -> (this as Pure<A>).value
      SingleTag -> (this as Single<A>).source()
      MapTag -> FxRunLoop(this)
      FlatMapTag -> FxRunLoop(this)
      ConnectionSwitchTag -> FxRunLoop(this)
      ModifyContextTag -> FxRunLoop(this)
      AsyncTag -> FxRunLoop(this)
      ContinueOnTag -> FxRunLoop(this)
      else -> throw FxImpossibleBugs.FxNot
    }

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> flatMap(noinline f: (A) -> FxOf<B>): Fx<B> =
    when (tag) {
      RaiseErrorTag -> this as Fx<B>
      PureTag -> FlatMap(this, f, 0)
      SingleTag -> FlatMap(this, f, 0)
      ConnectionSwitchTag -> FlatMap(this, f, 0)
      ModifyContextTag -> FlatMap(this, f, 0)
      AsyncTag -> FlatMap(this, f, 0)
      ContinueOnTag -> FlatMap(this, f, 0)
      MapTag -> {
        this as Fx.Map<B, A>
        //We can do fusion between an map-flatMap boundary
        FlatMap(source, { f(g(it)) }, 1)
      }
      FlatMapTag -> {
        this as Fx.FlatMap<B, A>
        if (index != Platform.maxStackDepthSize) FlatMap(this, f, 0)
        else FlatMap(source, { a ->
          Single {
            val b = !fb(a) //Don't we lose the runloop state here?
            !f(b)
          }
        }, index + 1)
      }
      else -> throw FxImpossibleBugs.FxFlatMap
    }

  suspend inline fun bind(): A = !this

  @PublishedApi
  internal class RaiseError<A>(@JvmField val error: Throwable) : Fx<A>(RaiseErrorTag) {
    override fun toString(): String = "Fx.RaiseError(error = $error)"
  }

  @PublishedApi
  internal class Pure<A>(@JvmField var value: A, var index: Int) : Fx<A>(PureTag) {
    override fun toString(): String = "Fx.Pure(value = $value)"
  }

  //Purely wrapped suspend function.
  //Should wrap a singular suspension point otherwise stack safety cannot be guaranteed.
  //This is not an issue if you guarantee stack safety yourself for your suspended program. i.e. using `tailrec`
  @PublishedApi
  internal class Single<A>(@JvmField val source: suspend () -> A) : Fx<A>(SingleTag) {
    override fun toString(): String = "Fx.Single"
  }

  @PublishedApi
  internal class Map<A, B>(
    @JvmField var source: FxOf<A>,
    @JvmField var g: (A) -> B
  ) : Fx<B>(MapTag), (A) -> Fx<B> {
    override fun invoke(value: A): Fx<B> = Pure(g(value), 0)
    override fun toString(): String = "Fx.Map(...)"
  }

  @PublishedApi
  internal class FlatMap<A, B>(
    @JvmField var source: FxOf<A>,
    @JvmField var fb: (A) -> FxOf<B>,
    @JvmField var index: Int
  ) : Fx<B>(FlatMapTag) {
    override fun toString(): String = "Fx.FlatMap(..., index = $index)"
  }

  @PublishedApi
  internal class UpdateContext<A>(
    @JvmField var source: FxOf<A>,
    @JvmField var modify: (CoroutineContext) -> CoroutineContext
  ) : Fx<A>(ModifyContextTag) {
    override fun toString(): String = "Fx.UpdateContext(...)"
  }

  @PublishedApi
  internal class ContinueOn<A>(
    @JvmField var source: FxOf<A>,
    @JvmField var ctx: CoroutineContext
  ) : Fx<A>(ContinueOnTag) {
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
    val restore: ((Any?, Throwable?, FxConnection, FxConnection) -> FxConnection)? = null) : Fx<A>(ConnectionSwitchTag) {

    override fun toString(): String = "Fx.ConnectionSwitch(...)"

    companion object {
      //Internal reusable reference.
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
  internal class Async<A> internal constructor(val ctx: CoroutineContext? = null,
                                               val updateContext: ((CoroutineContext) -> CoroutineContext)? = null,
                                               val proc: FxProc<A>) : Fx<A>(AsyncTag) {

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

  fun handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> =
    FlatMap(this, FxFrame.Companion.ErrorHandler(f), 0)

  fun handleError(f: (Throwable) -> A): Fx<A> = when (this) {
    is RaiseError -> Fx { f(error) }
    is Pure -> this
    else -> Fx {
      try {
        fa()
      } catch (e: Throwable) {
        f(e)
      }
    }
  }

  fun ensure(
    error: () -> Throwable,
    predicate: (A) -> Boolean
  ): Fx<A> = when (this) {
    is RaiseError -> this
    is Pure -> if (!predicate(value)) RaiseError(error()) else this
    else -> Fx {
      val result = !this
      if (!predicate(result)) throw error()
      else result
    }
  }

  fun attempt(): Fx<Either<Throwable, A>> =
    FlatMap(this, FxFrame.attempt(), 0)

  fun updateContext(f: (CoroutineContext) -> CoroutineContext): Fx<A> =
    UpdateContext(this, f)

  fun continueOn(ctx: CoroutineContext): Fx<A> = when (tag) {
    ContinueOnTag -> (this as Fx.ContinueOn<A>).apply {
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
  fun fork(ctx: CoroutineContext): Fx<Fiber<ForFx, A>> = Fx.async { oldConn, cb ->
    val promise = UnsafePromise<A>()
    val conn = FxConnection()
    oldConn.push(conn.cancel())
    conn.push(oldConn.cancel())

    FxRunLoop.startCancelable(this, conn, ctx) { either ->
      either.fold(
        { promise.complete(Either.Left(it)) },
        { promise.complete(Either.Right(it)) }
      )
    }

    cb(Right(FxFiber(promise, conn)))
  }

  /**
   * Makes the source [Fx] uncancelable and switches back to the original cancellation connection after running [this].
   */
  fun uncancelable(): Fx<A> =
    Fx.ConnectionSwitch(this, Fx.ConnectionSwitch.makeUncancelable, { _, _, old, _ -> old })

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
    val lazy: Fx<Unit> = Single(suspendMapUnit)

    @JvmStatic
    val never: Fx<Nothing> = async { _, _ -> Unit }

    @JvmStatic
    fun <A> eval(eval: Eval<A>): Fx<A> = when (eval) {
      is Eval.Now -> Fx.just(eval.value)
      else -> Single { eval.value() }
    }

    @JvmStatic
    fun <A> raiseError(e: Throwable): Fx<A> = RaiseError(e)

    @JvmStatic
    fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      FlatMap(Pure(Unit, 0), { fa() }, 0)

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
    fun <A> unsafeRunBlocking(fx: FxOf<A>): A = when (val fa = fx.fix()) {
      is Pure -> fa.value
      is RaiseError -> throw fa.error
      else -> {
        val loop = AtomicBoolean(true)
        var result: Either<Throwable, A>? = null
        FxRunLoop.start(fx) { r: Either<Throwable, A> ->
          result = r
          loop.compareAndSet(true, false)
        }
        while (loop.get()) {
        }

        result!!.fold({ throw it }, ::identity)
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
      Fx.Async { _ /* The start of this execution is immediate and uncancellable */, cbb ->
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

@Suppress("FunctionName")
internal fun <A> FxFiber(promise: UnsafePromise<A>, conn: FxConnection): Fiber<ForFx, A> {
  val join: Fx<A> = Fx.async { conn2, cb ->
    val cb2: (Either<Throwable, A>) -> Unit = {
      cb(it)
      conn2.pop()
      conn.pop()
    }

    conn2.push(Fx.Single { promise.remove(cb2) })
    conn.push(conn2.cancel())
    promise.get(cb2)
  }
  return Fiber(join, conn.cancel())
}
