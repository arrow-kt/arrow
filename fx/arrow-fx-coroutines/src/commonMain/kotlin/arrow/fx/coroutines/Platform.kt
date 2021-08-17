package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlin.jvm.JvmName

internal const val ArrowExceptionMessage =
  "Arrow-kt internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow/issues/new/choose"

internal class ArrowInternalException(override val message: String = ArrowExceptionMessage) : RuntimeException(message)

public object Platform {

  public fun composeErrors(first: Throwable, res: Result<Any?>): Throwable {
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
  public fun composeErrors(first: Throwable, vararg rest: Throwable): Throwable {
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
  public fun composeErrors(first: Throwable, rest: List<Throwable>): Throwable {
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
  public fun composeErrors(first: Throwable?, other: Throwable?): Throwable? =
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
  public fun composeErrors(first: Throwable, other: Throwable?): Throwable =
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
  public fun composeErrors(all: List<Throwable>): Throwable? =
    all.firstOrNull()?.let { first ->
      composeErrors(first, all.drop(1))
    }
}

public class AtomicRefW<A>(a: A) {
  private val atomicRef = atomic(a)

  public var value: A
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  public fun getAndSet(a: A): A = atomicRef.getAndSet(a)

  public fun updateAndGet(function: (A) -> A): A = atomicRef.updateAndGet(function)

  public fun compareAndSet(expect: A, update: A): Boolean = atomicRef.compareAndSet(expect, update)

  public fun lazySet(a: A): Unit = atomicRef.lazySet(a)

  override fun toString(): String = value.toString()
}

public class AtomicBooleanW(a: Boolean) {
  private val ref = atomic(a)

  public var value: Boolean
    set(a) {
      ref.value = a
    }
    get() = ref.value

  public fun getAndSet(a: Boolean): Boolean = ref.getAndSet(a)

  public fun updateAndGet(function: (Boolean) -> Boolean): Boolean = ref.updateAndGet(function)

  public fun compareAndSet(expect: Boolean, update: Boolean): Boolean = ref.compareAndSet(expect, update)

  public fun lazySet(a: Boolean): Unit = ref.lazySet(a)

  override fun toString(): String = value.toString()
}

public class AtomicIntW(a: Int) {
  private val atomicRef = atomic(a)

  public var value: Int
    set(a) {
      atomicRef.value = a
    }
    get() = atomicRef.value

  public fun getAndSet(a: Int): Int = atomicRef.getAndSet(a)

  public fun getAndAdd(delta: Int): Int = atomicRef.getAndAdd(delta)

  public fun addAndGet(delta: Int): Int = atomicRef.addAndGet(delta)

  public fun getAndIncrement(): Int = atomicRef.getAndIncrement()

  public fun getAndDecrement(): Int = atomicRef.getAndDecrement()

  public fun incrementAndGet(): Int = atomicRef.incrementAndGet()

  public fun decrementAndGet(): Int = atomicRef.decrementAndGet()

  public fun updateAndGet(function: (Int) -> Int): Int = atomicRef.updateAndGet(function)

  public fun compareAndSet(expect: Int, update: Int): Boolean = atomicRef.compareAndSet(expect, update)

  public fun lazySet(a: Int): Unit = atomicRef.lazySet(a)

  override fun toString(): String = value.toString()
}
