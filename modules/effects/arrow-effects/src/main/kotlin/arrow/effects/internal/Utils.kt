package arrow.effects.internal

import arrow.core.*
import arrow.effects.IO
import arrow.effects.typeclasses.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.AbstractQueuedSynchronizer
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext

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

internal fun <A, B, C> parContinuation(ctx: CoroutineContext, f: (A, B) -> C, cc: Continuation<C>): Continuation<Either<A, B>> =
  object : Continuation<Either<A, B>> {
    override val context: CoroutineContext = ctx

    var intermediate: Either<A, B>? = null

    override fun resume(value: Either<A, B>) =
      synchronized(this) {
        val result = intermediate
        if (null == result) {
          intermediate = value
        } else {
          value.fold({ a ->
            result.fold({
              // Resumed twice on the same side, updating
              intermediate = value
            }, { b ->
              cc.resume(f(a, b))
            })
          }, { b ->
            result.fold({ a ->
              cc.resume(f(a, b))
            }, {
              // Resumed twice on the same side, updating
              intermediate = value
            })
          })
        }
      }

    override fun resumeWithException(exception: Throwable) {
      cc.resumeWithException(exception)
    }
  }

internal fun <A, B, C, D> triContinuation(ctx: CoroutineContext, f: (A, B, C) -> D, cc: Continuation<D>): Continuation<Treither<A, B, C>> =
  object : Continuation<Treither<A, B, C>> {
    override val context: CoroutineContext = ctx

    var intermediate: Tuple3<A?, B?, C?> = Tuple3(null, null, null)

    override fun resume(value: Treither<A, B, C>) =
      synchronized(this) {
        val resA = intermediate.a
        val resB = intermediate.b
        val resC = intermediate.c
        value.fold({ a ->
          if (resB != null && resC != null) {
            cc.resume(f(a, resB, resC))
          } else {
            intermediate = Tuple3(a, resB, resC)
          }
        }, { b ->
          if (resA != null && resC != null) {
            cc.resume(f(resA, b, resC))
          } else {
            intermediate = Tuple3(resA, b, resC)
          }
        }, { c ->
          if (resA != null && resB != null) {
            cc.resume(f(resA, resB, c))
          } else {
            intermediate = Tuple3(resA, resB, c)
          }
        })
      }

    override fun resumeWithException(exception: Throwable) {
      cc.resumeWithException(exception)
    }
  }

internal sealed class Treither<out A, out B, out C> {
  data class Left<out A, out B, out C>(val a: A) : Treither<A, B, C>() {
    override fun <D> fold(fa: (A) -> D, fb: (B) -> D, fc: (C) -> D) =
      fa(a)
  }

  data class Middle<out A, out B, out C>(val b: B) : Treither<A, B, C>() {
    override fun <D> fold(fa: (A) -> D, fb: (B) -> D, fc: (C) -> D) =
      fb(b)
  }

  data class Right<out A, out B, out C>(val c: C) : Treither<A, B, C>() {
    override fun <D> fold(fa: (A) -> D, fb: (B) -> D, fc: (C) -> D) =
      fc(c)
  }

  abstract fun <D> fold(fa: (A) -> D, fb: (B) -> D, fc: (C) -> D): D
}

internal fun <A> asyncIOContinuation(ctx: CoroutineContext, cc: (Either<Throwable, A>) -> Unit): Continuation<A> {
  return object : Continuation<A> {
    override val context: CoroutineContext = ctx

    override fun resume(value: A) {
      cc(value.right())
    }

    override fun resumeWithException(exception: Throwable) {
      cc(exception.left())
    }
  }
}
