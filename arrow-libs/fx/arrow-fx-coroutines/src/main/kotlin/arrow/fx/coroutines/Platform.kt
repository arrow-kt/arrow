package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.coroutines.runBlocking

internal const val ArrowExceptionMessage =
  "Arrow-kt internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow/issues/new/choose"

internal class ArrowInternalException(override val message: String = ArrowExceptionMessage) : RuntimeException(message)

object Platform {

  internal fun <A> unsafeRunSync(f: suspend () -> A): A =
    runBlocking { f() }

  fun composeErrors(first: Throwable, res: Result<Any?>): Throwable {
    res.fold({ first }, { e -> first.addSuppressed(e) })
    return first
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

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  @JvmName("composeErrorsNullable")
  fun composeErrors(first: Throwable?, other: Throwable?): Throwable? =
    first?.let { a ->
      other?.let { b ->
        a.apply { addSuppressed(b) }
      } ?: a
    } ?: other

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  fun composeErrors(first: Throwable, other: Throwable?): Throwable =
    other?.let { a ->
      a.apply { addSuppressed(first) }
    } ?: first

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  fun composeErrors(all: List<Throwable>): Throwable? =
    all.firstOrNull()?.let { first ->
      composeErrors(first, all.drop(1))
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
