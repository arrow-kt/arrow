package arrow.core.continuations


internal actual class ShiftCancellationExceptionNoTrace actual constructor(
  shifted: Any?,
  raise: Raise<Any?>,
) : ShiftCancellationException(shifted, raise)
