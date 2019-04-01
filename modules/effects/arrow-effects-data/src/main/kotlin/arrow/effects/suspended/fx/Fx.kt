package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.*
import arrow.effects.*
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.*
import arrow.typeclasses.Continuation
import arrow.unsafe
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

suspend operator fun <A> FxOf<A>.invoke(): A = fix().invoke()

sealed class Fx<A> : FxOf<A> {

  abstract val fa: suspend () -> A
  abstract fun <B> map(f: (A) -> B): Fx<B>
  abstract fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B>

  internal class RaiseError<A>(val error: Throwable) : Fx<A>() {
    override val fa: suspend () -> A = throw error
    override fun <B> map(f: (A) -> B): Fx<B> = this as Fx<B>
    override fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B> = this as Fx<B>
    override fun toString(): String = "Fx.RaiseError(error = $error)"
  }

  internal class Pure<A>(val value: A) : Fx<A>() {
    override val fa: suspend () -> A = { value }
    override fun <B> map(f: (A) -> B): Fx<B> = Fx.Map(this, f, 0)
    override fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B> = Fx.FlatMap(this, f, 0)
    override fun toString(): String = "Fx.Pure(value = $value)"
  }

  //Purely wrapped suspend function.
  //Should wrap a singular suspension point otherwise stack safety cannot be guaranteed.
  //This is not an issue if you guarantee stack safety yourself for your suspended program. i.e. using `tailrec`
  internal class Single<A>(val source: suspend () -> A) : Fx<A>() {
    override val fa: suspend () -> A = source
    override fun <B> map(f: (A) -> B): Fx<B> = Fx.Map(this, f, 0)
    override fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B> = Fx.FlatMap(this, f, 0)
    override fun toString(): String = "Fx.Single"
  }

  internal class Map<A, B>(val source: FxOf<A>, val g: (A) -> B, val index: Int) : Fx<B>(), (A) -> Fx<B> {
    override fun invoke(value: A): Fx<B> = Fx.Pure(g(value))
    override val fa: suspend () -> B = suspend {
      val a = source.fix().fa.invoke()
      g(a)
    }

    override fun <C> map(f: (B) -> C): Fx<C> =
    // Allowed to do maxStackDepthSize map operations in sequence before
    // starting a new Map fusion in order to avoid stack overflows
      if (index != Platform.maxStackDepthSize) Fx.Map(source, g andThen f, index + 1)
      else Fx.Map(this, f, 0)

    //We can do fusion between an map-flatMap boundary
    override fun <C> flatMap(f: (B) -> FxOf<C>): Fx<C> = Fx.FlatMap(source, g andThen f, 1)

    override fun toString(): String = "Fx.Map(..., index = $index)"
  }

  internal class FlatMap<A, B>(val source: FxOf<A>, val fb: (A) -> FxOf<B>, val index: Int) : Fx<B>() {
    override val fa: suspend () -> B = suspend {
      val a = source.fix().fa()
      fb(a)()
    }

    //If we reach the maxStackDepth then we can fold the current FlatMap and return a Map case
    //If we haven't reached the maxStackDepth we fuse the map operator within the flatMap stack.
    override fun <C> map(f: (B) -> C): Fx<C> =
      if (index != Platform.maxStackDepthSize) Fx.Map(this, f, 0)
      else Fx.FlatMap(source, { a -> Fx { f(fb(a)()) } }, index + 1)

    override fun <C> flatMap(f: (B) -> FxOf<C>): Fx<C> =
      if (index != Platform.maxStackDepthSize) Fx.FlatMap(this, f, 0)
      else Fx.FlatMap(source, { a ->
        Fx.Single {
          val b = fb(a)()
          f(b)()
        }
      }, index + 1)

    override fun toString(): String = "Fx.FlatMap(..., index = $index)"
  }

  suspend inline operator fun not(): A =
    FxRunLoop(this).invoke()

  suspend inline operator fun invoke(): A =
    !this

  suspend inline operator fun component1(): A =
    !this

  suspend inline fun bind(): A =
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
        f(e)()
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
      val result = fa()
      if (!predicate(result)) throw error()
      else result
    }
  }

  fun attempt(): Fx<Either<Throwable, A>> = when (this) {
    is RaiseError -> Fx.Pure(Left(error))
    is Pure -> Fx.Pure(Right(value))
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
    val a = invoke()

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
    release(a, ExitCase.Completed).fix().invoke()
    b
  }

  fun continueOn(ctx: CoroutineContext): Fx<A> =
    unit().map { foldContinuation(ctx) { throw it } }

  companion object {

    operator fun <A> invoke(fa: suspend () -> A): Fx<A> = Fx.Single(fa)

    fun <A> just(a: A): Fx<A> = Fx.Pure(a)

    fun unit(): Fx<Unit> = Fx.Pure(Unit)

    fun <A> raiseError(e: Throwable): Fx<A> = Fx.RaiseError(e)

    fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      Fx.FlatMap(Fx.Pure(Unit), { fa() }, 0)

    fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> =
      Fx.FlatMap(f(a), { result ->
        result.fold({ tailRecM(it, f) }, { just(it) })
      }, 0)

    /** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
    fun <A> async(fa: Proc<A>): Fx<A> = Fx<A> {
      suspendCoroutine { continuation ->
        fa { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }
      }
    }

    fun <A> async(fa: FxProc<A>): Fx<A> = Fx<A> {
      suspendCoroutine { continuation ->
        val conn = continuation.context[CancelToken]?.connection ?: KindConnection.uncancelable(FxAP)
        //Is CancellationException from kotlin in kotlinx package???
        conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
        fa(conn) { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }
      }
    }

    /** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
    internal fun <A> asyncF(fa: ProcF<ForFx, A>): Fx<A> = Fx<A> {
      suspendCoroutine { continuation ->
        fa { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }.fix().foldContinuation(EmptyCoroutineContext, mapUnit)
      }
    }

    fun <A> asyncF(fa: FxProcF<A>): Fx<A> = Fx<A> {
      suspendCoroutine { continuation ->
        val conn = continuation.context[CancelToken]?.connection ?: KindConnection.uncancelable(FxAP)
        //Is CancellationException from kotlin in kotlinx package???
        conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
        fa(conn) { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }.fix().foldContinuation(EmptyCoroutineContext, mapUnit)
      }
    }

    fun <A> unsafe.runBlocking(fa: Fx<A>): A {
      val value = AtomicReference<A>()
      fa.fa.startCoroutine(object : kotlin.coroutines.Continuation<A> {
        override val context: CoroutineContext
          get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<A>) {
          value.set(result.getOrThrow())
        }
      })
      return value.get()
    }

  }

  override fun toString(): String = "Fx(...)"

}

fun helloWorld(): String =
  "Hello World"

suspend fun printHelloWorld(): Unit =
  println(helloWorld())

val program: Fx<Unit> = Fx {
  !Fx { printHelloWorld() }
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
class CancelToken : AbstractCoroutineContextElement(CancelToken) {
  companion object Key : CoroutineContext.Key<CancelToken>

  val connection: KindConnection<ForFx> = KindConnection.invoke(object : MonadDefer<ForFx> {
    override fun <A> defer(fa: () -> FxOf<A>): Fx<A> = Fx.defer(fa)
    override fun <A> raiseError(e: Throwable): Fx<A> = Fx.RaiseError(e)
    override fun <A> FxOf<A>.handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> = fix().handleErrorWith(f)
    override fun <A> just(a: A): Fx<A> = Fx.just(a)
    override fun <A, B> FxOf<A>.flatMap(f: (A) -> FxOf<B>): Fx<B> = fix().flatMap(f)
    override fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> = Fx.tailRecM(a, f)
    override fun <A, B> FxOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> = fix().bracketCase(release, use)

  }) {
    it.fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { })
  }
}