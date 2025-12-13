package arrow.core

import kotlin.coroutines.cancellation.CancellationException

/**
 * Extractor of non-fatal Throwable. Will not match fatal errors like `VirtualMachineError`
 * (for example, `OutOfMemoryError` and `StackOverflowError`, subclasses of `VirtualMachineError`), `ThreadDeath`,
 * `LinkageError`, `InterruptedException`.
 * This will also not match [CancellationException] since that's a fatal exception in Kotlin for cancellation purposes.
 *
 * Checks whether the passed [t] Throwable is NonFatal.
 *
 * @param t the Throwable to check
 * @return true if the provided `Throwable` is to be considered non-fatal, or false if it is to be considered fatal
 *
 * ```kotlin
 * import arrow.*
 * import arrow.core.*
 *
 * fun unsafeFunction(i: Int): String =
 *    when (i) {
 *         1 -> throw IllegalArgumentException("Non-Fatal")
 *         2 -> throw OutOfMemoryError("Fatal")
 *         else -> "Hello"
 *    }
 *
 * fun main() {
 *   val nonFatal: Either<Throwable, String> =
 *   //sampleStart
 *   try {
 *      Either.Right(unsafeFunction(1))
 *   } catch (t: Throwable) {
 *     if (NonFatal(t)) {
 *         Either.Left(t)
 *     } else {
 *         throw t
 *     }
 *   }
 *   //sampleEnd
 *   println(nonFatal)
 * }
 * ```
 * <!--- KNIT example-nonfatal-01.kt -->
 *
 */
public expect fun NonFatal(t: Throwable): Boolean
