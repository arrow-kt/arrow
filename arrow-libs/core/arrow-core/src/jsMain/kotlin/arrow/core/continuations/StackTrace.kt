package arrow.core.continuations

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal actual fun ShiftCancellationException.stackTrace(): List<String> =
  stackTraceToString().split("\n").drop(1)
