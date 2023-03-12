package arrow.core.raise

import kotlin.coroutines.cancellation.CancellationException

internal actual open class RaiseCancellationException actual constructor(
  internal actual val raised: Any?,
  internal actual val raise: Raise<Any?>,
  internal actual val isTraced: Boolean
) : CancellationException(if (!isTraced) RaiseCancellationExceptionCaptured else "")
