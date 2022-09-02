package arrow.core.continuations

@PublishedApi
internal actual fun ShiftCancellationException.stackTrace(): List<String> =
  stackTrace
    .drop(1) // drop the first element, which is the shift exception itself
    .map { it.toString() }
