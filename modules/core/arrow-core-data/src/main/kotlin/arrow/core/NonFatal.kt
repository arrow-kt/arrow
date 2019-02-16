package arrow.core

/**
 * Extractor of non-fatal Throwables. Will not match fatal errors like `VirtualMachineError`
 * (for example, `OutOfMemoryError` and `StackOverflowError`, subclasses of `VirtualMachineError`), `ThreadDeath`,
 * `LinkageError`, `InterruptedException`.
 */
object NonFatal {
  /**
   * @return true if the provided `Throwable` is to be considered non-fatal, or false if it is to be considered fatal
   */
  operator fun invoke(t: Throwable): Boolean =
    when (t) {
      is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError -> false
      else -> true
    }
}

/**
 * @throw @receiver if it is fatal
 * @return @receiver if it is non-fatal
 */
fun Throwable.nonFatal(): Throwable =
  if (NonFatal(this)) this else throw this
