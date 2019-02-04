package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.fx.dispatchers.dispatchers
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.Continuation
import arrow.unsafe
import java.util.concurrent.CancellationException
import java.util.concurrent.ForkJoinPool
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

class Fx<A>(internal val fa: suspend () -> A) : FxOf<A> {

  suspend operator fun not(): A =
    fa()

  suspend operator fun invoke(): A =
    fa()

  suspend operator fun component1(): A =
    fa()

  suspend fun bind(): A =
    fa()

  fun <B> map(f: (A) -> B): Fx<B> =
    Fx { f(fa()) }

  fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B> =
    Fx { f(fa()).fix()() }

  fun <B> ap(ff: Fx<(A) -> B>): Fx<B> =
    ff.flatMap { map(it) }

  fun handleErrorWith(f: (Throwable) -> Fx<A>): Fx<A> =
    Fx {
      try {
        fa()
      } catch (e: Throwable) {
        f(e)()
      }
    }

  fun handleError(f: (Throwable) -> A): Fx<A> =
    Fx {
      try {
        fa()
      } catch (e: Throwable) {
        f(e)
      }
    }

  fun ensure(
    error: () -> Throwable,
    predicate: (A) -> Boolean
  ): Fx<A> =
    Fx {
      val result = fa()
      if (!predicate(result)) throw error()
      else result
    }

  fun attempt(): Fx<Either<Throwable, A>> =
    Fx {
      try {
        fa().right()
      } catch (e: Throwable) {
        e.left()
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

  @RestrictsSuspension
  companion object {

    operator fun <A> invoke(fa: suspend () -> A): Fx<A> =
      Fx(fa)

    fun <A> just(a: A): Fx<A> =
      Fx { a }

    fun unit(): Fx<Unit> =
      Fx { Unit }

    fun <A> raiseError(e: Throwable): Fx<A> =
      Fx { throw e }

    fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      Fx { Unit }.flatMap { fa() }

    fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> =
      f(a).fix().flatMap { result ->
        result.fold({ tailRecM(it, f) }, { just(it) })
      }

    /** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
    internal fun <A> async(fa: Proc<A>): Fx<A> = Fx<A> {
      suspendCoroutine { continuation ->
        fa { either ->
          continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
        }
      }
    }

    fun <A> async(fa: FxProc<A>): Fx<A> = Fx<A> {
      suspendCoroutine { continuation ->
        val conn = FxConnection()
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
        val conn = FxConnection()
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

}

fun helloWorld(): String =
  "Hello World"

suspend fun printHelloWorld(): Unit =
  println(helloWorld())

val program: Fx<Unit> = Fx {
  !Fx { printHelloWorld() }
}

@extension
interface FxFunctor : Functor<ForFx> {
  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)
}

@extension
interface FxApplicative : Applicative<ForFx>, FxFunctor {
  override fun <A> just(a: A): Fx<A> =
    Fx.just(a)

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    fix().ap(ff.fix())

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)
}

@extension
interface FxApplicativeError : ApplicativeError<ForFx, Throwable>, FxApplicative {
  override fun <A> raiseError(e: Throwable): Fx<A> =
    Fx.raiseError(e)

  override fun <A> FxOf<A>.handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FxMonad : Monad<ForFx>, FxApplicative {

  override fun <A, B> FxOf<A>.flatMap(f: (A) -> FxOf<B>): Fx<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFx, Either<A, B>>): FxOf<B> =
    Fx.tailRecM(a, f)

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    fix().ap(ff.fix())

}

@extension
interface FxMonadError : MonadError<ForFx, Throwable>, FxApplicativeError, FxMonad

@extension
interface FxMonadThrow : MonadThrow<ForFx>, FxMonadError

@extension
interface FxBracket : Bracket<ForFx, Throwable>, FxMonadThrow {
  override fun <A, B> FxOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> =
    fix().bracketCase(release, use)
}

@extension
interface FxMonadDefer : MonadDefer<ForFx>, FxBracket {
  override fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
    unit().flatMap { fa() }
}

@extension
interface FxAsync : Async<ForFx>, FxMonadDefer {

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx.async(fa)

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx.asyncF(k)

  override fun <A> FxOf<A>.continueOn(ctx: CoroutineContext): Fx<A> =
    fix().continueOn(ctx)
}

private class Pool(val pool: ForkJoinPool) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
  override fun <T> interceptContinuation(continuation: kotlin.coroutines.Continuation<T>): kotlin.coroutines.Continuation<T> =
    PoolContinuation(pool, continuation.context.fold(continuation) { cont, element ->
      if (element != this@Pool && element is ContinuationInterceptor)
        element.interceptContinuation(cont) else cont
    })
}

private class PoolContinuation<T>(
  val pool: ForkJoinPool,
  val cont: kotlin.coroutines.Continuation<T>
) : kotlin.coroutines.Continuation<T> {
  override val context: CoroutineContext = cont.context

  override fun resumeWith(result: Result<T>) {
    pool.execute { cont.resumeWith(result) }
  }
}

val NonBlocking: CoroutineContext = EmptyCoroutineContext + Pool(ForkJoinPool())

@extension
interface FxDispatchers : Dispatchers<ForFx> {
  override fun default(): CoroutineContext =
    NonBlocking
}

@extension
interface FxConcurrent : Concurrent<ForFx>, FxAsync {
  override fun dispatchers(): Dispatchers<ForFx> = Fx.dispatchers()

  override fun <A> async(fa: FxProc<A>): Fx<A> =
    Fx.async(fa)

  override fun <A> asyncF(fa: FxProcF<A>): Fx<A> =
    Fx.asyncF(fa)

  override fun <A> CoroutineContext.startFiber(fa: FxOf<A>): Fx<Fiber<ForFx, A>> =
    Fx {
      val promise = UnsafePromise<A>()
      val conn = FxConnection()
      fa.fix().fa.startCoroutine(asyncContinuation(this@startFiber) { either ->
        either.fold(
          { promise.complete(it.left()) },
          { promise.complete(it.right()) }
        )
      })

      FxFiber(promise, conn)
    }

  override fun <A, B> CoroutineContext.racePair(fa: FxOf<A>, fb: FxOf<B>): Fx<RacePair<ForFx, A, B>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: FxOf<A>, fb: FxOf<B>, fc: FxOf<C>): Fx<RaceTriple<ForFx, A, B, C>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx.asyncF(k)

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx.async(fa)
}

@extension
interface FxUnsafeRun : UnsafeRun<ForFx> {
  override suspend fun <A> unsafe.runBlocking(fa: () -> FxOf<A>): A =
    fa().fix().foldContinuation { throw it }

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> FxOf<A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().fa.startCoroutine(asyncContinuation(NonBlocking, cb))
}

private fun <A> Fx<A>.foldContinuation(
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

fun FxConnection(): KindConnection<ForFx> =
  KindConnection(object : FxMonadDefer {}) { it.fix().foldContinuation { e -> throw e } }

internal fun <A> FxFiber(promise: UnsafePromise<A>, conn: KindConnection<ForFx>): Fiber<ForFx, A> {
  val join: Fx<A> = Fx.async { conn2, cb ->
    conn2.push(Fx { promise.remove(cb) })
    conn.push(conn2.cancel())
    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }
  return Fiber(join, conn.cancel())
}

//fun main() {
//  unsafe { Fx.runBlocking(program) }
//}