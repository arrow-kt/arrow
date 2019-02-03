package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.effects.CancelToken
import arrow.effects.KindConnection
import arrow.effects.OnCancel
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.ForwardCancelable.Companion.State.Active
import arrow.effects.typeclasses.suspended.ForwardCancelable.Companion.State.Empty
import arrow.extension
import arrow.typeclasses.*
import arrow.unsafe
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine

class ForFx private constructor() {
  companion object
}
typealias FxOf<A> = Kind<ForFx, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> FxOf<A>.fix(): Fx<A> =
  this as Fx<A>

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
    val b = use(a).fix().foldContinuation { e ->
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


  @RestrictsSuspension
  companion object {

    operator fun <A> invoke(fa: suspend () -> A): Fx<A> =
      Fx(fa)

    fun <A> just(a: A): Fx<A> =
      Fx { a }

    fun <A> raiseError(e: Throwable): Fx<A> =
      Fx { throw e }

    fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
      Fx { Unit }.flatMap { fa() }

    fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> =
      f(a).fix().flatMap { result ->
        result.fold({ tailRecM(it, f) }, { just(it) })
      }

    fun <A> asyncF(fa: ProcF<ForFx, A>): Fx<A> =
      Fx<A> {

        val x =
        fa(x)
        val conn = FxConnection()
        //On disposing of the upstream stream this will be called by `setCancellable` so check if upstream is already disposed or not because
        //on disposing the stream will already be in a terminated state at this point so calling onError, in a terminated state, will blow everything up.
        conn.push(FluxK { if (!sink.isCancelled) sink.error(OnCancel.CancellationException) })
        sink.onCancel { conn.cancel().value().subscribe() }

        fa(conn) { callback: Either<Throwable, A> ->
          callback.fold({
            sink.error(it)
          }, {
            sink.next(it)
            sink.complete()
          })
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
    Fx { Unit }.flatMap { fa() }
}

@extension
interface FxAsync : Async<ForFx>, FxMonadDefer {
  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx.asyncF(k)

  override fun <A> Kind<ForFx, A>.continueOn(ctx: CoroutineContext): Kind<ForFx, A> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

private fun <A> Fx<A>.foldContinuation(onError: (Throwable) -> A): A {
  val result: AtomicReference<A> = AtomicReference()
  fa.startCoroutine(object : Continuation<A> {
    override fun resume(value: A) {
      result.set(value)
    }

    override fun resumeWithException(exception: Throwable) {
      result.set(onError(exception))
    }

    override val context: CoroutineContext
      get() = EmptyCoroutineContext
  })
  return result.get()
}

fun FxConnection(): KindConnection<ForFx> =
  KindConnection(object : FxMonadDefer {}) { it.fix().fa.invoke() }


fun main() {
  unsafe { Fx.runBlocking(program) }
}