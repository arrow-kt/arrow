package arrow

import arrow.atomic.Atomic
import arrow.atomic.update
import kotlin.coroutines.cancellation.CancellationException

/**
 * The [AutoCloseScope] DSL allows for elegantly working with close-ables,
 * without having to worry about intermediate errors, composing them,
 * or keeping track of different close-ables and when they need to be closed.
 *
 * Often when working with multiple [AutoCloseable],
 * `Closeable` from Java we need to use multiple nested [use] blocks
 * and this can become tedious. An example below for copying files in Java:
 * <!--- INCLUDE
 * public fun main() {
 * -->
 * <!--- SUFFIX
 * }
 * -->
 * ```kotlin
 * Scanner("testRead.txt")
 *   .use { scanner ->
 *     Printer("testWrite.txt")
 *       .use { printer ->
 *         for(line in scanner) {
 *           printer.print(line)
 *         }
 *       }
 *   }
 * ```
 * We can also write this code as follows:
 * ```kotlin
 * autoCloseScope {
 *   val scanner = install(Scanner("testRead.txt"))
 *   val printer = install(Printer("testWrite.txt"))
 *   for(line in scanner) {
 *     printer.print(line)
 *   }
 * }
 * ```
 * <!--- KNIT example-autocloseable-01.kt -->
 * In the snippet above, `Scanner`, and `File` just like the nested [use] blocks
 *
 * This also works with `suspend`, since [autoCloseScope] is `inline` however if
 * you need to take into account cancellation then you need to use `ResourceScope` from Arrow Fx Coroutines.
 *
 * This DSL works very well with Kotlin's experimental feature context receivers, soon called context parameters.
 * We can write the same code from above as a function:
 * ```kotlin
 * context(AutoCloseScope)
 * fun copyFiles(input: String, output: String) {
 *   val scanner = install(Scanner(input))
 *   val printer = install(Printer(output))
 *   for(line in scanner) {
 *     printer.print(line)
 *   }
 * }
 * ```
 * <!--- KNIT example-autocloseable-02.kt -->
 */
public inline fun <A> autoCloseScope(block: AutoCloseScope.() -> A): A {
  val scope = DefaultAutoCloseScope()
  return try {
    block(scope)
      .also { scope.close(null) }
  } catch (e: CancellationException) {
    scope.close(e) ?: throw e
  } catch (e: Throwable) {
    scope.close(e.throwIfFatal()) ?: throw e
  }
}

public interface AutoCloseScope {
  public fun <A> autoClose(
    acquire: () -> A,
    release: (A, Throwable?) -> Unit
  ): A

  @ExperimentalStdlibApi
  public fun <A : AutoCloseable> install(autoCloseable: A): A =
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

@PublishedApi
internal expect fun Throwable.throwIfFatal(): Throwable
