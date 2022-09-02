package arrow.core.continuations

import kotlin.coroutines.cancellation.CancellationException

internal actual class ShiftCancellationException actual constructor(
  internal actual val shifted: Any?,
  internal actual val shift: Shift<Any?>,
  internal actual val isTraced: Boolean
) : CancellationException("Shifted Continuation")
