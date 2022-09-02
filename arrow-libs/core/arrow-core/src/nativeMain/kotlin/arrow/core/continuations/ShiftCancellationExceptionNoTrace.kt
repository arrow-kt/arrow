package arrow.core.continuations

internal actual class ShiftCancellationExceptionNoTrace actual constructor(
  shifted: Any?,
  shift: Shift<Any?>,
) : ShiftCancellationException(shifted, shift)
