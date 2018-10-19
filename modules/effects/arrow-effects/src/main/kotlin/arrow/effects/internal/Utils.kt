package arrow.effects.internal

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.effects.IO
import arrow.effects.typeclasses.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.AbstractQueuedSynchronizer

typealias JavaCancellationException = java.util.concurrent.CancellationException

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

  inline fun onceOnly(crossinline f: () -> Unit): () -> Unit {
    val wasCalled = AtomicBoolean(false)

    return {
      if (!wasCalled.getAndSet(true)) {
        f()
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
