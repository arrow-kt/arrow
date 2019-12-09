package arrow.core

/**
 * Extractor of non-fatal Throwables. Will not match fatal errors like `VirtualMachineError`
 * (for example, `OutOfMemoryError` and `StackOverflowError`, subclasses of `VirtualMachineError`), `ThreadDeath`,
 * `LinkageError`, `InterruptedException`.
 *
 * Checks whether the passed [t] Throwable is NonFatal.
 *
 * @param t the Throwable to check
 * @return true if the provided `Throwable` is to be considered non-fatal, or false if it is to be considered fatal
 *
 * ```kotlin:ank:playground
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
 * fun main(args: Array<String>) {
 *   val nonFatal: Either<Throwable, String> =
 *   //sampleStart
 *   try {
 *      Right(unsafeFunction(1))
 *   } catch (t: Throwable) {
 *     if (NonFatal(t)) {
 *         Left(t)
 *     } else {
 *         throw t
 *     }
 *   }
 *   //sampleEnd
 *   println(nonFatal)
 * }
 * ```
 *
 */
fun NonFatal(t: Throwable): Boolean =
  when (t) {
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError -> false
    else -> true
  }

/**
 * Returns the Throwable if NonFatal and throws it otherwise.
 *
 * @throws Throwable the Throwable `this` if Fatal
 * @return the Throwable `this` if NonFatal
 *
 * ```kotlin:ank:playground
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
 * fun main(args: Array<String>) {
 *   val nonFatal: Either<Throwable, String> =
 *   //sampleStart
 *   try {
 *      Right(unsafeFunction(1))
 *   } catch (t: Throwable) {
*       Left(t.nonFatalOrThrow())
 *   }
 *   //sampleEnd
 *   println(nonFatal)
 * }
 * ```
 *
 */
fun Throwable.nonFatalOrThrow(): Throwable =
  if (NonFatal(this)) this else throw this
