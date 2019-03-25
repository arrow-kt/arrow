package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.*
import arrow.effects.internal.Platform
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.ConnectedProc
import arrow.effects.typeclasses.ConnectedProcF
import arrow.effects.typeclasses.ExitCase
import arrow.typeclasses.Continuation
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.getOrSet
import kotlin.coroutines.*

class ForFx private constructor() {
  companion object
}
typealias
  FxOf<A> = Kind<ForFx, A>
typealias FxProc<A> = ConnectedProc<ForFx, A>
typealias FxProcF<A> = ConnectedProcF<ForFx, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> FxOf<A>.fix(): Fx<A> =
  this as Fx<A>

suspend operator fun <A> FxOf<A>.invoke(): A = fix().fa.invoke()

class Fx<A>(val fa: suspend () -> A) : FxOf<A> {
  companion object
}

fun <A> fx(f: suspend () -> A): Fx<A> =
  Fx(f)

fun <A> effect(f: suspend () -> A): suspend () -> A =
  f

val threadInvocations: ThreadLocal<Int> = ThreadLocal.withInitial { 0 }

suspend inline operator fun <A> (suspend () -> A).not(): A {
  val currentIters = threadInvocations.get()
  return if (currentIters < 127) {
    threadInvocations.set(currentIters + 1)
    this()
  } else {
    threadInvocations.set(0)
    trampoline(this)()
  }
}

suspend inline fun <A> (suspend () -> A).bind(): A =
  !this

suspend inline operator fun <A> (suspend () -> A).component1(): A =
  !this

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

object Trampoline : TrampolinePoolElement(Executor { it.run() }), CoroutineContext

suspend fun <A> trampoline(f: suspend () -> A): suspend () -> A =
  suspendCoroutine { c ->
    f.startCoroutine(asyncContinuation(Trampoline) {
      it.fold(c::resumeWithException) { c.resume { it } }
    })
  }


suspend fun <A, B> (suspend () -> A).map(f: (A) -> B): suspend () -> B =
  { f(!this@map) }

fun <A> just(a: A): suspend () -> A =
  { a }

val <A> A.just: suspend () -> A
  get() = { this }

suspend fun <A, B> (suspend () -> A).ap(ff: suspend () -> (A) -> B): suspend () -> B =
  map(!ff)

suspend fun <A, B> (suspend () -> A).flatMap(f: (A) -> suspend () -> B): suspend () -> B =
  {
    try {
      !f(!this)
    } catch (e: Throwable) {
      if (NonFatal(e)) {
        !raiseError<B>(e)
      } else throw e
    }
  }

suspend fun <A> (suspend () -> A).attempt(unit: Unit = Unit): suspend () -> Either<Throwable, A> =
  attempt(this)

suspend fun <A> attempt(f: suspend () -> A): suspend () -> Either<Throwable, A> =
  {
    try {
      (!f).right()
    } catch (e: Throwable) {
      e.left()
    }
  }

val unit: suspend () -> Unit = { Unit }

fun <A> raiseError(e: Throwable, unit: Unit = Unit): suspend () -> A =
  { if (NonFatal(e)) throw RaisedError(e) else throw e }

fun <A> (suspend () -> A).handleErrorWith(f: (Throwable) -> suspend () -> A): suspend () -> A =
  {
    try {
      !this
    } catch (r: RaisedError) {
      !f(r.exception)
    } catch (e: Throwable) {
      !f(e.nonFatalOrThrow())
    }
  }

fun <A> (suspend () -> A).handleError(f: (Throwable) -> A): suspend () -> A =
  {
    try {
      !this
    } catch (r: RaisedError) {
      f(r.exception)
    } catch (e: Throwable) {
      if (NonFatal(e)) f(e)
      else throw e
    }
  }

fun <A> (suspend () -> A).ensure(
  error: () -> Throwable,
  predicate: (A) -> Boolean
): suspend () -> A =
  {
    val result = !this
    if (!predicate(result)) throw error()
    else result
  }

suspend fun <A, B> (suspend () -> A).bracketCase(
  release: (A, ExitCase<Throwable>) -> suspend () -> Unit,
  use: (A) -> suspend () -> B
): suspend () -> B = {
  val a = !this

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

fun TrampolinePool(executorService: Executor): CoroutineContext =
  TrampolinePoolElement(executorService)

private val iterations: ThreadLocal<Int> = ThreadLocal.withInitial { 0 }
private val threadTrampoline = ThreadLocal<TrampolineExecutor>()

open class TrampolinePoolElement(
  val executionService: Executor,
  val asyncBoundaryAfter: Int = 127
) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {

  override fun <T> interceptContinuation(continuation: kotlin.coroutines.Continuation<T>): kotlin.coroutines.Continuation<T> =
    TrampolinedContinuation(executionService, continuation.context.fold(continuation) { cont, element ->
      if (element != this@TrampolinePoolElement && element is ContinuationInterceptor)
        element.interceptContinuation(cont)
      else cont
    }, asyncBoundaryAfter)
}

private class TrampolinedContinuation<T>(
  val executionService: Executor,
  val cont: kotlin.coroutines.Continuation<T>,
  val asyncBoundaryAfter: Int
) : kotlin.coroutines.Continuation<T> {
  override val context: CoroutineContext = cont.context

  override fun resumeWith(result: Result<T>) {
    val currentIterations = iterations.get()
    if (currentIterations > asyncBoundaryAfter) {
      iterations.set(0)
      threadTrampoline
        .getOrSet { TrampolineExecutor(executionService) }
        .execute(Runnable {
          cont.resumeWith(result)
        })
    } else {
      //println("Blocking: currentIterations: $currentIterations, cont.context: $context")
      iterations.set(currentIterations + 1)
      cont.resumeWith(result)
    }
  }
}

/**
 * Trampoline implementation, meant to be stored in a `ThreadLocal`.
 * See `TrampolineEC`.
 *
 * INTERNAL API.
 */
internal class TrampolineExecutor(val underlying: Executor) {
  private var immediateQueue = Platform.ArrayStack<Runnable>()
  private var withinLoop = false

  fun startLoop(runnable: Runnable): Unit {
    withinLoop = true
    try {
      immediateLoop(runnable)
    } finally {
      withinLoop = false
    }
  }

  fun execute(runnable: Runnable): Unit {
    if (!withinLoop) {
      startLoop(runnable)
    } else {
      immediateQueue.push(runnable)
    }
  }

  private fun forkTheRest(): Unit {
    class ResumeRun(val head: Runnable, val rest: Platform.ArrayStack<Runnable>) : Runnable {
      override fun run(): Unit {
        immediateQueue.addAll(rest)
        immediateLoop(head)
      }
    }

    val head = immediateQueue.firstOrNull()
    if (head != null) {
      immediateQueue.pop()
      val rest = immediateQueue
      immediateQueue = Platform.ArrayStack<Runnable>()
      underlying.execute(ResumeRun(head, rest))
    }
  }

  private tailrec fun immediateLoop(task: Runnable): Unit {
    try {
      task.run()
    } catch (ex: Throwable) {
      forkTheRest()
      ex.nonFatalOrThrow()
    }

    val next = immediateQueue.firstOrNull()
    if (next != null) {
      immediateQueue.pop()
      immediateLoop(next)
    }
  }
}