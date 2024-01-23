package arrow

import arrow.atomic.Atomic
import arrow.atomic.update

/**
 * The [AutoCloseScope] DSL allows for elegantly working with close-ables,
 * without having to worry about intermediate errors, composing them,
 * or keeping track of different close-ables and when they need to be closed.
 *
 * Similar to C#'s or Scala's Using.
 *
 * ```kotlin
 * fun main() = autoClose {
 *   val scanner = install(Scanner(File("testRead.txt"))
 *   val printer = install(PrintWriter(File("testWrite.txt")))
 *   for(line in scanner) {
 *     printer.print(line)
 *   }
 * }
 * ```
 */
public inline fun <A> autoCloseScope(block: AutoCloseScope.() -> A): A {
  val scope = DefaultAutoCloseScope()
  return try {
    block(scope).also {
      scope.close(null)
    }
  } catch (e: Throwable) {
    scope.close(e) ?: throw e
  }
}

/**
 * The AutoCloseScope interface exposes all functionality for the [autoCloseScope],
 * and can conveniently be combined with context parameters, or extension functions.
 *
 * ```kotlin
 * context(_ : AutoCloseScope)
 * fun copyFiles() {
 *   val scanner = install(Scanner(File("testRead.txt")))
 *   val printer = install(PrintWriter(File("testWrite.txt")))
 *   for(line in scanner) {
 *     printer.print(line)
 *   }
 * }
 * ```
 */
public interface AutoCloseScope {
  public fun <A> autoClose(
    acquire: () -> A,
    release: (A, Throwable?) -> Unit
  ): A

  @ExperimentalStdlibApi
  public fun <A : AutoCloseable> autoClose(autoCloseable: A): A =
    autoClose({ autoCloseable }) { a, _ -> a.close() }
}

@PublishedApi
internal class DefaultAutoCloseScope : AutoCloseScope {
  private val finalizers = Atomic(emptyList<(Throwable?) -> Unit>())

  override fun <A> autoClose(acquire: () -> A, release: (A, Throwable?) -> Unit): A =
    try {
      acquire().also { a ->
        finalizers.update { it + { e -> release(a, e) } }
      }
    } catch (e: Throwable) {
      throw e
    }

  fun close(error: Throwable?): Nothing? {
    return finalizers.get().fold(error) { acc, function ->
      acc.add(runCatching { function.invoke(error) }.exceptionOrNull())
    }?.let { throw it }
  }

  private fun Throwable?.add(other: Throwable?): Throwable? =
    this?.apply {
      other?.let { addSuppressed(it) }
    } ?: other
}
