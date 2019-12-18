package arrow.fx.internal

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.internal.AtomicBooleanW
import arrow.core.left
import arrow.core.right
import arrow.fx.BIO
import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.IOOf
import arrow.fx.fix
import arrow.fx.typeclasses.Duration
import java.util.concurrent.Executor
import java.util.concurrent.locks.AbstractQueuedSynchronizer
import kotlin.coroutines.CoroutineContext

typealias JavaCancellationException = java.util.concurrent.CancellationException

internal open class ArrowInternalException(
  override val message: String =
    "Arrow-kt internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow/issues/new/choose"
) : RuntimeException(message)

private const val initialIndex: Int = 0
private const val chunkSize: Int = 8

object Platform {

  @Suppress("UNCHECKED_CAST")
  class ArrayStack<A> {

    private val initialArray: Array<Any?> = arrayOfNulls<Any?>(chunkSize)
    private val modulo = chunkSize - 1
    private var array = initialArray
    private var index = initialIndex

    /** Returns `true` if the stack is empty. */
    fun isEmpty(): Boolean =
      index == 0 && (array.getOrNull(0) == null)

    /** Pushes an item on the stack. */
    fun push(a: A) {
      if (index == modulo) {
        val newArray = arrayOfNulls<Any?>(chunkSize)
        newArray[0] = array
        array = newArray
        index = 1
      } else {
        index += 1
      }
      array[index] = a
    }

    /** Pushes an entire iterator on the stack. */
    fun pushAll(cursor: Iterator<A>) {
      while (cursor.hasNext()) push(cursor.next())
    }

    /** Pushes an entire iterable on the stack. */
    fun pushAll(seq: Iterable<A>) {
      pushAll(seq.iterator())
    }

    /** Pushes the contents of another stack on this stack. */
    fun pushAll(stack: ArrayStack<A>) {
      pushAll(stack.iteratorReversed())
    }

    /** Pops an item from the stack (in LIFO order).
     *
     * Returns `null` in case the stack is empty.
     */
    fun pop(): A? {
      if (index == 0) {
        if (array.getOrNull(0) != null) {
          array = array[0] as Array<Any?>
          index = modulo
        } else {
          return null
        }
      }
      val result = array[index] as A
      // GC purposes
      array[index] = null
      index -= 1
      return result
    }

    /** Builds an iterator out of this stack. */
    @Suppress("IteratorNotThrowingNoSuchElementException")
    fun iteratorReversed(): Iterator<A> =
      object : Iterator<A> {
        private var array = this@ArrayStack.array
        private var index = this@ArrayStack.index

        override fun hasNext(): Boolean =
          index > 0 || (array.getOrNull(0) != null)

        override fun next(): A {
          if (index == 0) {
            array = array[0] as Array<Any?>
            index = modulo
          }
          val result = array[index] as A
          index -= 1
          return result
        }
      }

    fun isNotEmpty(): Boolean =
      !isEmpty()
  }

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
    val wasCalled = AtomicBooleanW(false)

    return { a ->
      if (!wasCalled.getAndSet(true)) {
        f(a)
      }
    }
  }

  internal inline fun <A> onceOnly(conn: IOConnection, crossinline f: (A) -> Unit): (A) -> Unit {
    val wasCalled = AtomicBooleanW(false)

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

    return when (val eitherRef = ref) {
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

  inline fun trampoline(crossinline f: () -> Unit): Unit =
    _trampoline.get().execute(Runnable { f() })

  private val underlying = Executor { it.run() }

  @PublishedApi
  internal val _trampoline = object : ThreadLocal<TrampolineExecutor>() {
    override fun initialValue(): TrampolineExecutor =
      TrampolineExecutor(underlying)
  }

  @PublishedApi
  internal class TrampolineExecutor(val underlying: Executor) {
    private var immediateQueue = ArrayStack<Runnable>()
    @Volatile
    private var withinLoop = false

    private fun startLoop(runnable: Runnable) {
      withinLoop = true
      try {
        immediateLoop(runnable)
      } finally {
        withinLoop = false
      }
    }

    fun execute(runnable: Runnable): Unit =
      if (!withinLoop) startLoop(runnable)
      else immediateQueue.push(runnable)

    private fun forkTheRest() {
      class ResumeRun(val head: Runnable, val rest: ArrayStack<Runnable>) : Runnable {
        override fun run() {
          immediateQueue.pushAll(rest)
          immediateLoop(head)
        }
      }

      val head = immediateQueue.pop()
      if (head != null) {
        val rest = immediateQueue
        immediateQueue = ArrayStack()
        underlying.execute(ResumeRun(head, rest))
      }
    }

    @Suppress("SwallowedException") // Should we rewrite with while??
    private tailrec fun immediateLoop(task: Runnable) {
      try {
        task.run()
      } catch (ex: Throwable) {
        forkTheRest()
        // ex.nonFatalOrThrow() //not required???
      }

      val next = immediateQueue.pop()
      return if (next != null) immediateLoop(next)
      else Unit
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

/**
 * [arrow.typeclasses.Continuation] to run coroutine on `ctx` and link result to callback [cc].
 * Use [asyncContinuation] to run suspended functions within a context `ctx` and pass the result to [cc].
 */
internal fun <A> asyncContinuation(ctx: CoroutineContext, cc: (Either<Throwable, A>) -> Unit): arrow.typeclasses.Continuation<A> =
  object : arrow.typeclasses.Continuation<A> {
    override val context: CoroutineContext = ctx

    override fun resume(value: A) {
      cc(value.right())
    }

    override fun resumeWithException(exception: Throwable) {
      cc(exception.left())
    }
  }

/**
 * Utility to makes sure that the original [fa] is gets forked on [ctx].
 * @see IO.fork
 * @see arrow.fx.racePair
 * @see arrow.fx.raceTriple
 *
 * This moves the forking inside the [IO] operation,
 * so it'll share it's [kotlin.coroutines.Continuation] with other potential jumps or [IO.async].
 * @see [arrow.fx.IORunLoop.RestartCallback]
 */
internal fun <A> IOForkedStart(fa: IOOf<A>, ctx: CoroutineContext): IO<A> =
  BIO.Bind(BIO.ContinueOn(IO.unit, ctx)) { fa.fix() }
