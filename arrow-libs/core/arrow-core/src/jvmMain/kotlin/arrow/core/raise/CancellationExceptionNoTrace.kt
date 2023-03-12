package arrow.core.raise

import kotlin.coroutines.cancellation.CancellationException

/*
 * Inspired by KotlinX Coroutines:
 * https://github.com/Kotlin/kotlinx.coroutines/blob/3788889ddfd2bcfedbff1bbca10ee56039e024a2/kotlinx-coroutines-core/jvm/src/Exceptions.kt#L29
 */
internal actual open class RaiseCancellationException actual constructor(
  internal actual val raised: Any?,
  internal actual val raise: Raise<Any?>,
  internal actual val isTraced: Boolean
) : CancellationException(if (!isTraced) RaiseCancellationExceptionCaptured else "") {
  override fun fillInStackTrace(): Throwable =
    if (isTraced) super.fillInStackTrace()
    else {
    // Prevent Android <= 6.0 bug.
    stackTrace = emptyArray()
    // We don't need stacktrace on shift, it hurts performance.
    this
  }
}
