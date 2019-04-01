package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.identity
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.*
import arrow.typeclasses.Applicative
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

sealed class Fx<A> : FxOf<A> {

  abstract val tag: Int
  abstract val fa: suspend () -> A
  abstract fun <B> map(f: (A) -> B): Fx<B>
  abstract fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B>
  abstract suspend operator fun not(): A
  suspend inline fun bind(): A = !this

  @PublishedApi
  @Suppress("UNCHECKED_CAST")
  internal class RaiseError<A>(@JvmField internal val error: Throwable) : Fx<A>() {
    override val tag: Int = RaiseErrorTag
    override suspend operator fun not(): A = throw error
    override val fa: suspend () -> A = throw error
    override fun <B> map(f: (A) -> B): Fx<B> = this as Fx<B>
    override fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B> = this as Fx<B>
    override fun toString(): String = "Fx.RaiseError(error = $error)"
  }

  @PublishedApi
  internal class Pure<A>(@JvmField internal val value: A) : Fx<A>() {
    override val tag: Int = PureTag
    override suspend operator fun not(): A = value
    override val fa: suspend () -> A = { value }
    override fun <B> map(f: (A) -> B): Fx<B> = Fx.Map(this, f, 0)
    override fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B> = Fx.FlatMap(this, f, 0)
    override fun toString(): String = "Fx.Pure(value = $value)"
  }

  //Purely wrapped suspend function.
  //Should wrap a singular suspension point otherwise stack safety cannot be guaranteed.
  //This is not an issue if you guarantee stack safety yourself for your suspended program. i.e. using `tailrec`
  @PublishedApi
  internal class Single<A>(@JvmField internal val source: suspend () -> A) : Fx<A>() {
    override val tag: Int = SingleTag
    override suspend operator fun not(): A = source()
    override val fa: suspend () -> A = source
    override fun <B> map(f: (A) -> B): Fx<B> = Fx.Map(this, f, 0)
    override fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B> = Fx.FlatMap(this, f, 0)
    override fun toString(): String = "Fx.Single"
  }

  @PublishedApi
  internal class Map<A, B>(
    @JvmField internal val source: FxOf<A>,
    @JvmField private val g: (A) -> B,
    @JvmField private val index: Int
  ) : Fx<B>(), (A) -> Fx<B> {
    override val tag: Int = MapTag
    override fun invoke(value: A): Fx<B> = Fx.Pure(g(value))
    override suspend operator fun not(): B = FxRunLoop(this)
    override val fa: suspend () -> B = suspend {
      val a = !source
      g(a)
    }

    override fun <C> map(f: (B) -> C): Fx<C> =
    // Allowed to do maxStackDepthSize map operations in sequence before
    // starting a new Map fusion in order to avoid stack overflows
      if (index != Platform.maxStackDepthSize) Fx.Map(source, { f(g(it)) }, index + 1)
      else Fx.Map(this, f, 0)

    //We can do fusion between an map-flatMap boundary
    override fun <C> flatMap(f: (B) -> FxOf<C>): Fx<C> = Fx.FlatMap(source, { f(g(it)) }, 1)

    override fun toString(): String = "Fx.Map(..., index = $index)"
  }

  @PublishedApi
  internal class FlatMap<A, B>(
    @JvmField internal val source: FxOf<A>,
    @JvmField internal val fb: (A) -> FxOf<B>,
    @JvmField private val index: Int
  ) : Fx<B>() {
    override val tag: Int = FlatMapTag
    override suspend operator fun not(): B = FxRunLoop(this)
    override val fa: suspend () -> B = suspend { !fb(!source) }

    //If we reach the maxStackDepth then we can fold the current FlatMap and return a Map case
    //If we haven't reached the maxStackDepth we fuse the map operator within the flatMap stack.
    override fun <C> map(f: (B) -> C): Fx<C> =
      if (index != Platform.maxStackDepthSize) Fx.Map(this, f, 0)
      else Fx.FlatMap(source, { a -> Fx { f(!fb(a)) } }, index + 1)

    override fun <C> flatMap(f: (B) -> FxOf<C>): Fx<C> =
      if (index != Platform.maxStackDepthSize) Fx.FlatMap(this, f, 0)
      else Fx.FlatMap(source, { a ->
        Fx.Single {
          val b = !fb(a)
          !f(b)
        }
      }, index + 1)

    override fun toString(): String = "Fx.FlatMap(..., index = index)"
  }

  suspend inline operator fun invoke(): A =
    !this

  suspend inline operator fun component1(): A =
    !this

  fun <B> ap(ff: FxOf<(A) -> B>): Fx<B> =
    ff.fix().flatMap { map(it) }

  fun handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> = when (this) {
    is RaiseError -> f(error).fix()
    is Pure -> this
    else -> Fx {
      try {
        fa()
      } catch (e: Throwable) {
        !f(e).fix()
      }
    }
  }

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

  fun attempt(): Fx<Either<Throwable, A>> = when (tag) {
    RaiseErrorTag -> Fx.Pure(Left((this as Fx.RaiseError<A>).error))
    PureTag -> Fx.Pure(Right((this as Fx.Pure<A>).value))
    else -> Fx {
      try {
        Right(fa())
      } catch (e: Throwable) {
        Left(e)
      }
    }
  }

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
    unit().map { foldContinuation(ctx) { throw it } }

  companion object {

    @JvmStatic operator fun <A> invoke(fa: suspend () -> A): Fx<A> = Fx.Single(fa)

    @JvmStatic fun <A> just(a: A): Fx<A> = Fx.Pure(a)

    @JvmStatic fun unit(): Fx<Unit> = Fx.Pure(Unit)

    @JvmStatic fun <A> raiseError(e: Throwable): Fx<A> = Fx.RaiseError(e)

    @JvmStatic fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      Fx.FlatMap(Fx.Pure(Unit), { fa() }, 0)

    @JvmStatic fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> =
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
        val conn = (continuation.context[CancelToken] ?: NonCancelable).connection
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
        val conn = (continuation.context[CancelToken] ?: NonCancelable).connection
        //Is CancellationException from kotlin in kotlinx package???
        conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
        fa(conn) { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }.fix().foldContinuation(EmptyCoroutineContext, mapUnit)
      }
    }

    @JvmStatic fun <A> unsafeRunBlocking(fa: Fx<A>): A {
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

fun <A> Fx<A>.foldContinuation(
  context: CoroutineContext = EmptyCoroutineContext,
  onError: (Throwable) -> A
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

//Can we somehow share this across arrow-effects-data and arrow-effects-io-extensions..
class CancelToken(val connection: KindConnection<ForFx>) : AbstractCoroutineContextElement(CancelToken) {
  companion object Key : CoroutineContext.Key<CancelToken>
}

//Considering reorganizing this and creating this into a typeclass.
val NonCancelable: CancelToken = CancelToken(KindConnection.uncancelable(object : Applicative<ForFx> {
  override fun <A> just(a: A): Fx<A> = Fx.just(a)
  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> = fix().ap(ff)
}))