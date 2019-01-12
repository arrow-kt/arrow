package arrow.effects.internal

import arrow.core.*
import arrow.effects.IO
import arrow.effects.KindConnection
import arrow.effects.typeclasses.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.AbstractQueuedSynchronizer
import kotlin.coroutines.CoroutineContext

typealias JavaCancellationException = java.util.concurrent.CancellationException

class ArrowInternalException(override val message: String =
    "Arrow-kt internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow/issues/new/choose"
) : RuntimeException(message)

object Platform {

  class ArrayStack<A> : ArrayDeque<A>()

  /**
   * Establishes the maximum stack depth for `IO#map` operations.
   *
   * The default is `128`, from which we substract one as an
   * optimization. This default has been reached like this:
   *
   *  - according to official docs, the default stack size on 32-bits
   *    Windows and Linux was 320 KB, whereas for 64-bits it is 1024 KB
   *  - according to measurements chaining `Function1` references uses
   *    approximately 32 bytes of stack space on a 64 bits system;
   *    this could be lower if "compressed oops" is activated
   *  - therefore a "map fusion" that goes 128 in stack depth can use
   *    about 4 KB of stack space
   */
  const val maxStackDepthSize = 127

  inline fun <A> onceOnly(crossinline f: (A) -> Unit): (A) -> Unit {
    val wasCalled = AtomicBoolean(false)

    return { a ->
      if (!wasCalled.getAndSet(true)) {
        f(a)
      }
    }
  }

  inline fun <F, A> onceOnly(conn: KindConnection<F>, crossinline f: (A) -> Unit): (A) -> Unit {
    val wasCalled = AtomicBoolean(false)

    return { a ->
      if (!wasCalled.getAndSet(true)) {
        conn.pop()
        f(a)
      }
    }
  }

  fun <A> unsafeResync(ioa: IO<A>, limit: Duration): Option<A> {
    val latch = OneShotLatch()
    var ref: Either<Throwable, A>? = null
    ioa.unsafeRunAsync { a ->
      ref = a
      latch.releaseShared(1)
    }

    if (limit == Duration.INFINITE) {
      latch.acquireSharedInterruptibly(1)
    } else {
      latch.tryAcquireSharedNanos(1, limit.nanoseconds)
    }

    val eitherRef = ref

    return when (eitherRef) {
      null -> None
      is Either.Left -> throw eitherRef.a
      is Either.Right -> Some(eitherRef.b)
    }
  }

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  fun composeErrors(first: Throwable, vararg rest: Throwable): Throwable {
    rest.forEach { if (it != first) first.addSuppressed(it) }
    return first
  }

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  fun composeErrors(first: Throwable, rest: List<Throwable>): Throwable {
    rest.forEach { if (it != first) first.addSuppressed(it) }
    return first
  }

}

private class OneShotLatch : AbstractQueuedSynchronizer() {
  override fun tryAcquireShared(ignored: Int): Int =
    if (state != 0) {
      1
    } else {
      -1
    }

  override fun tryReleaseShared(ignore: Int): Boolean {
    state = 1
    return true
  }
}


/**
 * [arrow.core.Continuation] to run coroutine on `ctx` and link result to callback [cc].
 * Use [asyncContinuation] to run suspended functions within a context `ctx` and pass the result to [cc].
 */
internal fun <A> asyncContinuation(ctx: CoroutineContext, cc: (Either<Throwable, A>) -> Unit): arrow.core.Continuation<A> =
  object : arrow.core.Continuation<A> {
    override val context: CoroutineContext = ctx

    override fun resume(value: A) {
      cc(value.right())
    }

    override fun resumeWithException(exception: Throwable) {
      cc(exception.left())
    }

  }
