package arrow.effects.suspended.fx2

import arrow.Kind
import arrow.core.*
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

  internal class Single<A>(override val fa: suspend () -> A, val index: Int) : Fx<A>()

  internal class FlatMap<A, B>(val left: FxOf<A>, val fb: (A) -> FxOf<B>) : Fx<B>() {
    override val fa: suspend () -> B = suspend {
      invokeLoop(this@FlatMap as Fx<Any?>) as B
    }
    override fun toString(): String = "Fx.FlatMap(...)"
  }

  private infix fun <A, B> (suspend () -> A).andThen(g: (A) -> FxOf<B>): suspend () -> B =
    suspend { g(this.invoke()).invoke() }

  private fun <B> andThen(g: (A) -> FxOf<B>): Fx<B> =
    when (this) {
      is Single ->
        if (index != Platform.maxStackDepthSize) Single(fa andThen g, index + 1)
        else andThenF(g)
      is FlatMap<*, *> -> andThenF(g)
    }

  private fun <B> andThenF(right: (A) -> FxOf<B>): Fx<B> =
    Fx.FlatMap(this, right)

  suspend inline operator fun not(): A =
    invokeLoop(this as Fx<Any?>) as A

  tailrec suspend fun invokeLoop(fa: Fx<Any?>): Any? = when (fa) {
    is Single -> fa.fa()
    is FlatMap<*, *> -> {
      val left: Any? = fa.left.fix().invoke()
      val fb: (Any?) -> Fx<Any?> = fa.fb as (Any?) -> Fx<Any?>
      invokeLoop(fb(left))
    }
  }

  suspend inline operator fun invoke(): A =
    !this

  suspend inline operator fun component1(): A =
    !this

  suspend inline fun bind(): A =
    !this

  fun <B> map(f: (A) -> B): Fx<B> =
    flatMap { a -> Fx { f(a) } }

  fun <B> flatMap(f: (A) -> FxOf<B>): Fx<B> =
    andThen(f)

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

    operator fun <A> invoke(fa: suspend () -> A): Fx<A> = Fx.Single(fa, 0)

    fun <A> just(a: A): Fx<A> =
      Fx { a }

    fun unit(): Fx<Unit> =
      Fx { Unit }

    fun <A> raiseError(e: Throwable): Fx<A> = Fx { throw e }

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
