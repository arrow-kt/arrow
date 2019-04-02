package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.effects.IO
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.*
import arrow.typeclasses.Continuation
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicReference
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

object Impossible : RuntimeException("Fx bug, please contact support! https://arrow-kt.io") {
  override fun fillInStackTrace(): Throwable = this
}

sealed class Fx<A>(@JvmField var tag: Int = UnknownTag) : FxOf<A> {

  @Suppress("UNCHECKED_CAST")
  inline val fa: suspend () -> A
    get() =
      when (tag) {
        RaiseErrorTag -> {
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
        else -> throw Impossible
      }


  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> map(noinline f: (A) -> B): Fx<B> =
    when (tag) {
      RaiseErrorTag -> this as Fx<B>
      PureTag -> {
        this as Fx.Pure<A>
        Fx.Single { f(value) }
      }
      SingleTag -> Fx.Map(this, f, 0)
      MapTag -> {
        // Allowed to do maxStackDepthSize map operations in sequence before
        // starting a new Map fusion in order to avoid stack overflows
        this as Fx.Map<Any?, Any?>
        if (index != Platform.maxStackDepthSize) {
          val ff = f as (Any?) -> Any?
          this.g = { ff(g(it)) }
          this.index += 1
          this as Fx<B>
        } else Fx.Map(this, f, 0)


        //if (index != Platform.maxStackDepthSize) Fx.Map(source, { f(g(it)) }, index + 1)
        //else Fx.Map(this, f, 0)
      }
      FlatMapTag -> {
        //If we reach the maxStackDepthSize then we can fold the current FlatMap and return a Map case
        //If we haven't reached the maxStackDepthSize we fuse the map operator within the flatMap stack.
        this as Fx.FlatMap<B, A>
        if (index != Platform.maxStackDepthSize) Fx.Map(this, f, 0)
        else Fx.FlatMap(source, { a -> Fx { f(!fb(a)) } }, index + 1)
      }
      else -> throw Impossible
    }

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  suspend inline operator fun not(): A =
    when (tag) {
      RaiseErrorTag -> throw (this as Fx.RaiseError<*>).error
      PureTag -> (this as Fx.Pure<A>).value
      SingleTag -> (this as Fx.Single<A>).source()
      MapTag -> FxRunLoop(this)
      FlatMapTag -> FxRunLoop(this)
      else -> throw Impossible
    }

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> flatMap(noinline f: (A) -> FxOf<B>): Fx<B> =
    when (tag) {
      RaiseErrorTag -> this as Fx<B>
      PureTag -> {
        this as Fx.Pure<A>
        f(value).fix()
      }
      SingleTag -> Fx.FlatMap(this, f, 0)
      MapTag -> {
        this as Fx.Map<B, A>
        //We can do fusion between an map-flatMap boundary
        Fx.FlatMap(source, { f(g(it)) }, 1)
      }
      FlatMapTag -> {
        this as Fx.FlatMap<B, A>
        if (index != Platform.maxStackDepthSize) Fx.FlatMap(this, f, 0)
        else Fx.FlatMap(source, { a ->
          Fx.Single {
            val b = !fb(a)
            !f(b)
          }
        }, index + 1)
      }
      else -> throw Impossible
    }

  suspend inline fun bind(): A = !this

  @PublishedApi
  internal class RaiseError<A>(@JvmField val error: Throwable) : Fx<A>(RaiseErrorTag) {
    override fun toString(): String = "Fx.RaiseError(error = $error)"
  }

  @PublishedApi
  internal class Pure<A>(@JvmField val value: A) : Fx<A>(PureTag) {
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
    @JvmField var g: (A) -> B,
    @JvmField var index: Int
  ) : Fx<B>(MapTag), (A) -> Fx<B> {
    override fun invoke(value: A): Fx<B> = Fx.Pure(g(value))
    override fun toString(): String = "Fx.Map(..., index = $index)"
  }

  @PublishedApi
  internal class FlatMap<A, B>(
    @JvmField val source: FxOf<A>,
    @JvmField val fb: (A) -> FxOf<B>,
    @JvmField val index: Int
  ) : Fx<B>(FlatMapTag) {
    override fun toString(): String = "Fx.FlatMap(..., index = $index)"
  }

  /**
   * [ConnectionSwitch] is used to temporally switch the `KindConnection` attached to the computation.
   * i.e. Switch to `KindConnection` to `KindConnection.uncancelable` and later switch back to the original connection.
   * This is what [bracketCase] uses to make `acquire` and `release` uncancelable and disconnect it from the cancel stack.
   *
   * This node nor its combinators are useful outside of Fx's internals.
   * Instead we expose features explicitly like [bracketCase] and [uncancelable].
   */
  @PublishedApi
  internal class ConnectionSwitch<A>(
    val source: FxOf<A>,
    val modify: (FxConnection) -> FxConnection,
    val restore: ((Any?, Throwable?, FxConnection, FxConnection) -> FxConnection)?) : Fx<A>(ConnectionSwitchTag) {

    companion object {
      //Internal reusable reference.
      val makeUncancelable: (FxConnection) -> FxConnection = { NonCancelable.connection }
      val disableUncancelableAndPop: (Any?, Throwable?, FxConnection, FxConnection) -> FxConnection = { _, _, old, _ ->
        old.pop()
        old
      }
    }
  }

  suspend inline operator fun invoke(): A =
    !this

  suspend inline operator fun component1(): A =
    !this

  fun <B> ap(ff: FxOf<(A) -> B>): Fx<B> =
    ff.fix().flatMap { map(it) }

  fun handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> =
    Fx.FlatMap(this, FxFrame.errorHandler(f), 0)

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
    is Pure -> if (!predicate(value)) Fx.RaiseError(error()) else this
    else -> Fx {
      val result = !this
      if (!predicate(result)) throw error()
      else result
    }
  }

  fun attempt(): Fx<Either<Throwable, A>> =
    Fx.FlatMap(this, FxFrame.any(), 0)

  fun <B> bracketCase(
    release: (A, ExitCase<Throwable>) -> FxOf<Unit>,
    use: (A) -> FxOf<B>
  ): Fx<B> = Fx {
    val a = !this

    val fxB: Fx<B> = try {
      use(a).fix()
    } catch (e: Throwable) {
      release(a, ExitCase.Error(e)).fix().foldContinuation { e2 ->
        throw Platform.composeErrors(e, e2)
      }
      throw e
    }

    val b = fxB.foldContinuation { e ->
      when (e) {
        is CancellationException -> release(a, ExitCase.Canceled).fix().foldContinuation { e2 ->
          throw Platform.composeErrors(e, e2)
        }
        else -> release(a, ExitCase.Error(e)).fix().foldContinuation { e2 ->
          throw Platform.composeErrors(e, e2)
        }
      }
      throw e
    }
    !release(a, ExitCase.Completed).fix()
    b
  }

  fun continueOn(ctx: CoroutineContext): Fx<A> =
    unit.map { foldContinuation(ctx) { throw it } }

  /** Makes the source [IO] uncancelable such that a [Fiber.cancel] signal has no effect. */
  fun uncancelable(): Fx<A> =
    Fx.ConnectionSwitch(this, Fx.ConnectionSwitch.makeUncancelable, { _, _, old, _ -> old })

  inline fun foldContinuation(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onError: (Throwable) -> A
  ): A {
    val result: AtomicReference<A> = AtomicReference()
    fa.startCoroutine(object : Continuation<A> {
      override fun resume(value: A) {
        result.set(value)
      }

      override fun resumeWithException(exception: Throwable) {
        result.set(onError(exception))
      }

      override val context: CoroutineContext
        get() = context
    })
    return result.get()
  }

  @RestrictsSuspension
  companion object {

    @JvmStatic
    operator fun <A> invoke(fa: suspend () -> A): Fx<A> = Fx.Single(fa)

    @JvmStatic
    fun <A> just(a: A): Fx<A> = Fx.Pure(a)

    @JvmStatic
    val unit: Fx<Unit> = Fx.Pure(Unit)

    @JvmStatic
    fun <A> raiseError(e: Throwable): Fx<A> = Fx.RaiseError(e)

    @JvmStatic
    fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      Fx.FlatMap(Fx.Pure(Unit), { fa() }, 0)

    @JvmStatic
    fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> =
      Fx.FlatMap(f(a), { result ->
        result.fold({ tailRecM(it, f) }, { just(it) })
      }, 0)

    /** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
    @JvmStatic fun <A> async(fa: Proc<A>): Fx<A> = Fx {
      suspendCoroutine<A> { continuation ->
        fa { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }
      }
    }

    @JvmStatic fun <A> async(fa: FxProc<A>): Fx<A> = Fx {
      suspendCoroutine<A> { continuation ->
        val conn = (continuation.context[CancelContext] ?: NonCancelable).connection
        //Is CancellationException from kotlin in kotlinx package???
        conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
        fa(conn) { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }
      }
    }

    /** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
    @JvmStatic fun <A> asyncF(fa: ProcF<ForFx, A>): Fx<A> = Fx {
      suspendCoroutine<A> { continuation ->
        fa { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }.fix().foldContinuation(EmptyCoroutineContext, mapUnit)
      }
    }

    @JvmStatic fun <A> asyncF(fa: FxProcF<A>): Fx<A> = Fx {
      suspendCoroutine<A> { continuation ->
        val conn = (continuation.context[CancelContext] ?: NonCancelable).connection
        //Is CancellationException from kotlin in kotlinx package???
        conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
        fa(conn) { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }.fix().foldContinuation(EmptyCoroutineContext, mapUnit)
      }
    }

    @JvmStatic
    fun <A> unsafeRunBlocking(fa: Fx<A>): A {
      var loop = true
      var result: Either<Throwable, A>? = null
      FxRunLoop.start(fa) { r: Either<Throwable, A> ->
        result = r
        loop = false
      }
      while (loop) {
      }
      return result!!.fold({ throw it }, ::identity)
    }

  }

  override fun toString(): String = "Fx(...)"

}
