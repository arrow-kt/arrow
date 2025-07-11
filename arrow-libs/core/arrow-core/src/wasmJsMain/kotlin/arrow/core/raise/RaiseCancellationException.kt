package arrow.core.raise

import kotlin.coroutines.cancellation.CancellationException

@DelicateRaiseApi
public actual sealed class RaiseCancellationException actual constructor(
  internal actual val raised: Any?,
  internal actual val raise: Raise<Any?>
) : CancellationException(RaiseCancellationExceptionCaptured)
