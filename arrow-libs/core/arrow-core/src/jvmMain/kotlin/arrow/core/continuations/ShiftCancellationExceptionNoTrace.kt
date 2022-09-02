package arrow.core.continuations

internal actual class ShiftCancellationExceptionNoTrace actual constructor(
  shifted: Any?,
  shift: Shift<Any?>,
) : ShiftCancellationException(shifted, shift) {
  override fun fillInStackTrace(): Throwable {
    // Prevent Android <= 6.0 bug. https://github.com/Kotlin/kotlinx.coroutines/issues/1866
    stackTrace = emptyArray()
    return this
  }
}
