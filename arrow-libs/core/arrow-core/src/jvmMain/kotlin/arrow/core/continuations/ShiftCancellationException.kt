package arrow.core.continuations

import kotlin.coroutines.cancellation.CancellationException

internal actual class ShiftCancellationException actual constructor(
  @JvmField internal actual val shifted: Any?,
  @JvmField internal actual val shift: Shift<Any?>,
  @JvmField internal actual val isTraced: Boolean
) : CancellationException("Shifted Continuation") {
  override fun fillInStackTrace(): Throwable =
    if (isTraced) super.fillInStackTrace()
    else {
      // Prevent Android <= 6.0 bug. https://github.com/Kotlin/kotlinx.coroutines/issues/1866
      stackTrace = emptyArray()
      this
    }
}
