package arrow

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.atomic.value
import kotlin.jvm.JvmInline

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
public inline fun <A> autoCloseScope(block: AutoCloseScope.() -> A): A = with(DefaultAutoCloseScope()) {
  try {
    block()
  } catch (e: Throwable) {
    closeAll(e.throwIfFatal())
    error("Unreachable, closeAll should throw the exception passed to it. Please report this bug to the Arrow team.")
  }.also { closeAll(null) }
}

public interface AutoCloseScope {
  public fun onClose(release: (Throwable?) -> Unit)

  public fun <A : AutoCloseable> install(autoCloseable: A): A =
    autoCloseable.also { onClose { autoCloseable.close() } }
}

public inline fun <A> AutoCloseScope.autoClose(
  acquire: () -> A,
  crossinline release: (A, Throwable?) -> Unit
): A = acquire().also { a -> onClose { e -> release(a, e) } }

@JvmInline
@PublishedApi
internal value class DefaultAutoCloseScope(
  private val finalizers: Atomic<List<(Throwable?) -> Unit>> = Atomic(emptyList())
) : AutoCloseScope {
  override fun onClose(release: (Throwable?) -> Unit) =
    finalizers.update { listOf(release) + it }

  fun closeAll(error: Throwable?) {
    finalizers.value.fold(error) { acc, function ->
      acc.add(runCatching { function(error) }.exceptionOrNull())
    }?.let { throw it }
  }
}

private fun Throwable?.add(other: Throwable?): Throwable? =
  this?.apply {
    other?.let { addSuppressed(it) }
  } ?: other

@PublishedApi
internal expect fun Throwable.throwIfFatal(): Throwable
