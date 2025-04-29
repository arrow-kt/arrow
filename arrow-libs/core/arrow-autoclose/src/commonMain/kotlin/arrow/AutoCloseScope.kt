@file:OptIn(ExperimentalContracts::class)

package arrow

import arrow.atomic.Atomic
import arrow.atomic.update
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
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
 * you need to take into _inspect_ cancellation then you need to use `ResourceScope` from Arrow Fx Coroutines.
 * So both [AutoCloseScope], and `ResourceScope` behave correctly when encountering cancellation, by closing the source,
 * but `ResourceScope` allows inspecting _complete_, _failure_, **and** _cancellation_ in the finalizer.
 *
 * We can write the same code from above as a function by adding the scope as receiver:
 *
 * ```kotlin
 * fun AutoCloseScope.copyFiles(input: String, output: String) {
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
  contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
  val scope = DefaultAutoCloseScope()
  var throwable: Throwable? = null
  return try {
    block(scope)
  } catch (e: Throwable) {
    throwable = e
    throw e
  } finally {
    if (throwable !is CancellationException) throwable?.throwIfFatal()
    scope.close(throwable)
  }
}

public interface AutoCloseScope {
  public fun onClose(release: (Throwable?) -> Unit)

  public fun <A> autoClose(
    acquire: () -> A,
    release: (A, Throwable?) -> Unit
  ): A = acquire().also { a -> onClose { release(a, it) } }

  @OptIn(ExperimentalStdlibApi::class)  // 'AutoCloseable' in stdlib < 2.0
  public fun <A : AutoCloseable> install(autoCloseable: A): A =
    autoCloseable.also { onClose { autoCloseable.close() } }
}

@PublishedApi
internal class DefaultAutoCloseScope : AutoCloseScope {
  private val finalizers = Atomic(emptyList<(Throwable?) -> Unit>())

  override fun onClose(release: (Throwable?) -> Unit) {
    finalizers.update { it + release }
  }

  fun close(error: Throwable?): Nothing? {
    return finalizers.getAndSet(emptyList()).asReversed().fold(error) { acc, finalizer ->
      acc.add(runCatching { finalizer(error) }.exceptionOrNull())
    }?.let { throw it }
  }

  private fun Throwable?.add(other: Throwable?): Throwable? {
    if (other !is CancellationException) other?.throwIfFatal()
    return this?.apply {
      other?.let { addSuppressed(it) }
    } ?: other
  }
}

@PublishedApi
internal expect fun Throwable.throwIfFatal(): Throwable
