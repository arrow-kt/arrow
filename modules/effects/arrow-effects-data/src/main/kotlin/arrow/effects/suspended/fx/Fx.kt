package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import arrow.data.AndThen
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
        ConnectionSwitchTag -> suspend { FxRunLoop(this) }
        else -> throw Impossible
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
            Fx.Pure(f(this.value), index + 1)
          } catch (e: Throwable) {
            Fx.RaiseError<B>(e.nonFatalOrThrow())
          }
        } else {
          try {
            val b: B = Fx.unsafeRunBlocking(Fx.Single { f(this.value) })
            (this as Fx.Pure<B>).value = b
            this as Fx<B>
          } catch (e: Throwable) {
            Fx.RaiseError<B>(e.nonFatalOrThrow())
          }
        }
      }
      SingleTag -> Fx.Map(this, f)
      ConnectionSwitchTag -> Fx.Map(this, f)
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
        if (index != Platform.maxStackDepthSize) Fx.Map(this, f)
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
      ConnectionSwitchTag -> FxRunLoop(this)
      else -> throw Impossible
    }


  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  inline fun <B> flatMap(noinline f: (A) -> FxOf<B>): Fx<B> =
    when (tag) {
      RaiseErrorTag -> this as Fx<B>
      PureTag -> {
        this as Fx.Pure<A>
//        if(index != Platform.maxStackDepthSize) Fx.Pure(FxRunLoop(f(value).fix()), index + 1)
//        else
          Fx.FlatMap(this, f, 0)
      }
      SingleTag -> Fx.FlatMap(this, f, 0)
      ConnectionSwitchTag -> Fx.FlatMap(this, f, 0)
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
    override fun invoke(value: A): Fx<B> = Fx.Pure(g(value), 0)
    override fun toString(): String = "Fx.Map(...)"
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

  fun <B> bracketCase(release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> =
    FxBracket(this, release, use)

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
    fun <A> just(a: A): Fx<A> = Fx.Pure(a, 0)

    @JvmStatic
    val unit: Fx<Unit> = Fx.Pure(Unit, 0)

    @JvmStatic
    fun <A> raiseError(e: Throwable): Fx<A> = Fx.RaiseError(e)

    @JvmStatic
    fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      Fx.FlatMap(Fx.Pure(Unit, 0), { fa() }, 0)

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

    //TODO
    @JvmStatic fun <A> unsafeRunBlocking(fx: FxOf<A>): A = when (val fa = fx.fix()) {
      is Pure -> fa.value
      else -> {
        var loop = true
        var result: Either<Throwable, A>? = null
        FxRunLoop.start(fx) { r: Either<Throwable, A> ->
          result = r
          loop = false
        }
        while (loop) {
        }
        result!!.fold({ throw it }, ::identity)
      }
    }

    override fun toString(): String = "Fx(...)"

  }

}
