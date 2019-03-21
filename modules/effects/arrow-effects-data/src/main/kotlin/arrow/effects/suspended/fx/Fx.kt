package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.*
import arrow.effects.internal.Platform
import arrow.effects.typeclasses.ConnectedProc
import arrow.effects.typeclasses.ConnectedProcF
import arrow.effects.typeclasses.ExitCase
import arrow.typeclasses.Continuation
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
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

suspend operator fun <A> FxOf<A>.invoke(): A = fix().fa.invoke()

inline class Fx<A>(val fa: suspend () -> A) : FxOf<A> {
  companion object
}

fun <A> fx(f: suspend () -> A): suspend () -> A =
  f

suspend inline operator fun <A> (suspend () -> A).not(): A =
  this()

suspend inline fun <A> (suspend () -> A).bind(): A =
  this()

suspend inline operator fun <A> (suspend () -> A).component1(): A =
  this()

/**
 * Avoid recalculating stack traces on rethrows
 */
class RaisedError(val exception: Throwable) : Throwable() {
  override fun fillInStackTrace(): Throwable =
    this

  override val cause: Throwable?
    get() = exception

  override fun equals(other: Any?): Boolean =
    when (other) {
      is RaisedError -> exception.message == other.exception.message
      is Throwable -> exception.message == other.message
      else -> exception == other
    }
}

inline fun <A> Throwable.raiseError(): suspend () -> A =
  { throw RaisedError(this) }

suspend fun <A, B> (suspend () -> A).map(f: (A) -> B): suspend () -> B =
  { f(this()) }

fun <A> just(a: A): suspend () -> A =
  { a }

val <A> A.just: suspend () -> A
  get() = { this }

suspend fun <A, B> (suspend () -> A).ap(ff: suspend () -> (A) -> B): suspend () -> B =
  map(ff())

suspend fun <A, B> (suspend () -> A).flatMap(f: (A) -> suspend () -> B): suspend () -> B =
  {
    try {
      !f(this())
    } catch (e: Throwable) {
      if (NonFatal(e)) {
        !raiseError<B>(e)
      } else throw e
    }
  }

suspend inline fun <A> (suspend () -> A).attempt(unit: Unit = Unit): suspend () -> Either<Throwable, A> =
  attempt(this)

suspend inline fun <A> attempt(crossinline f: suspend () -> A): suspend () -> Either<Throwable, A> =
  {
    try {
      f().right()
    } catch (e: Throwable) {
      e.left()
    }
  }

val unit: suspend () -> Unit = { Unit }

fun <A> raiseError(e: Throwable, unit: Unit = Unit): suspend () -> A =
  { if (NonFatal(e)) throw RaisedError(e) else throw e }

inline fun <A> (suspend () -> A).handleErrorWith(crossinline f: (Throwable) -> suspend () -> A): suspend () -> A =
  {
    try {
      this()
    } catch (r: RaisedError) {
      !f(r.exception)
    } catch (e: Throwable) {
      !f(e.nonFatalOrThrow())
    }
  }

inline fun <A> (suspend () -> A).handleError(crossinline f: (Throwable) -> A): suspend () -> A =
  {
    try {
      this()
    } catch (r: RaisedError) {
      f(r.exception)
    } catch (e: Throwable) {
      if (NonFatal(e)) f(e)
      else throw e
    }
  }

suspend inline fun <A> defer(noinline fa: suspend () -> A): suspend () -> A =
  unit.flatMap { fa }

inline fun <A> (suspend () -> A).ensure(
  crossinline error: () -> Throwable,
  crossinline predicate: (A) -> Boolean
): suspend () -> A =
  {
    val result = this()
    if (!predicate(result)) throw error()
    else result
  }

suspend fun <A, B> (suspend () -> A).bracketCase(
  release: (A, ExitCase<Throwable>) -> suspend () -> Unit,
  use: (A) -> suspend () -> B
): suspend () -> B = {
  val a = invoke()

  val fxB = try {
    use(a)
  } catch (e: Throwable) {
    release(a, ExitCase.Error(e)).foldContinuation { e2 ->
      throw Platform.composeErrors(e, e2)
    }
    throw e
  }

  val b = fxB.foldContinuation { e ->
    when (e) {
      is CancellationException -> release(a, ExitCase.Canceled).foldContinuation { e2 ->
        throw Platform.composeErrors(e, e2)
      }
      else -> release(a, ExitCase.Error(e)).foldContinuation { e2 ->
        throw Platform.composeErrors(e, e2)
      }
    }
    throw e
  }
  release(a, ExitCase.Completed).invoke()
  b
}

fun <A> (suspend () -> A).foldContinuation(
  context: CoroutineContext = EmptyCoroutineContext,
  onError: (Throwable) -> A
): A {
  val result: AtomicReference<A> = AtomicReference()
  startCoroutine(object : Continuation<A> {
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
