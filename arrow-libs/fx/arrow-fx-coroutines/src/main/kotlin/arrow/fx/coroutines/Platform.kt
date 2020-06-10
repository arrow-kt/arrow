package arrow.fx.coroutines

import arrow.core.Either
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import java.util.concurrent.locks.AbstractQueuedSynchronizer
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

internal open class ArrowInternalException(
  override val message: String =
    "Arrow-kt internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow/issues/new/choose"
) : RuntimeException(message)

private const val initialIndex: Int = 0
private const val chunkSize: Int = 8

object Platform {

  @Suppress("UNCHECKED_CAST")
  internal class ArrayStack<A> {

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

  inline fun <A> onceOnly(crossinline f: (A) -> Unit): (A) -> Unit {
    val wasCalled = AtomicBooleanW(false)

    return { a ->
      if (!wasCalled.getAndSet(true)) {
        f(a)
      }
    }
  }

  internal inline fun <A> onceOnly(conn: SuspendConnection, crossinline f: (A) -> Unit): (A) -> Unit {
    val wasCalled = AtomicBooleanW(false)

    return { a ->
      if (!wasCalled.getAndSet(true)) {
        conn.pop()
        f(a)
      }
    }
  }

  internal fun <A> unsafeRunSync(f: suspend () -> A): A {
    val latch = OneShotLatch()
    var ref: Either<Throwable, A>? = null
    f.startCoroutine(Continuation(EmptyCoroutineContext) { a ->
      ref = a.fold({ aa -> Either.Right(aa) }, { t -> Either.Left(t) })
      latch.releaseShared(1)
    })

    latch.acquireSharedInterruptibly(1)

    return when (val either = ref) {
      is Either.Left -> throw either.a
      is Either.Right -> either.b
      null -> throw ArrowInternalException("Suspend execution should yield a valid result")
    }
  }

  internal fun <A> unsafeRunSync(startOn: CoroutineContext, f: suspend () -> A): A {
    val latch = OneShotLatch()
    var ref: Either<Throwable, A>? = null
    f.startCoroutine(Continuation(startOn) { a ->
      ref = a.fold({ aa -> Either.Right(aa) }, { t -> Either.Left(t) })
      latch.releaseShared(1)
    })

    latch.acquireSharedInterruptibly(1)

    return when (val either = ref) {
      is Either.Left -> throw either.a
      is Either.Right -> either.b
      null -> throw ArrowInternalException("Suspend execution should yield a valid result")
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

class AtomicRefW<A>(a: A) {
  private val atomicRef = atomic(a)

  var value: A
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  fun getAndSet(a: A) = atomicRef.getAndSet(a)

  fun updateAndGet(function: (A) -> A) = atomicRef.updateAndGet(function)

  fun compareAndSet(expect: A, update: A) = atomicRef.compareAndSet(expect, update)

  fun lazySet(a: A) = atomicRef.lazySet(a)

  override fun toString(): String = value.toString()
}

class AtomicBooleanW(a: Boolean) {
  private val ref = atomic(a)

  var value: Boolean
    set(a) {
      ref.value = a
    }
    get() = ref.value

  fun getAndSet(a: Boolean) = ref.getAndSet(a)

  fun updateAndGet(function: (Boolean) -> Boolean) = ref.updateAndGet(function)

  fun compareAndSet(expect: Boolean, update: Boolean) = ref.compareAndSet(expect, update)

  fun lazySet(a: Boolean) = ref.lazySet(a)

  override fun toString(): String = value.toString()
}

class AtomicIntW(a: Int) {
  private val atomicRef = atomic(a)

  var value: Int
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  fun getAndSet(a: Int) = atomicRef.getAndSet(a)

  fun getAndAdd(delta: Int) = atomicRef.getAndAdd(delta)

  fun addAndGet(delta: Int) = atomicRef.addAndGet(delta)

  fun getAndIncrement() = atomicRef.getAndIncrement()

  fun getAndDecrement() = atomicRef.getAndDecrement()

  fun incrementAndGet() = atomicRef.incrementAndGet()

  fun decrementAndGet() = atomicRef.decrementAndGet()

  fun updateAndGet(function: (Int) -> Int) = atomicRef.updateAndGet(function)

  fun compareAndSet(expect: Int, update: Int) = atomicRef.compareAndSet(expect, update)

  fun lazySet(a: Int) = atomicRef.lazySet(a)

  override fun toString(): String = value.toString()
}

fun Throwable.nonFatalOrThrow(): Throwable =
  when (this) {
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError -> throw this
    else -> this
  }
