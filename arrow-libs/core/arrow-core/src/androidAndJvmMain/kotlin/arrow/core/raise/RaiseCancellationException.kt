package arrow.core.raise

import arrow.core.ControlCancellationException
import arrow.core.InternalArrowApi

@OptIn(InternalArrowApi::class)
@DelicateRaiseApi
public actual sealed class RaiseCancellationException actual constructor(
  internal actual val raised: Any?,
  internal actual val raise: Raise<Any?>
) : ControlCancellationException(RaiseCancellationExceptionCaptured)
