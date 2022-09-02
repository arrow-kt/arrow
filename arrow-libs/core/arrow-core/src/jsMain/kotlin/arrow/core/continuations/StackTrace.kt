package arrow.core.continuations

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal actual fun CancellationException.stackTrace(): List<String> =
  stackTraceToString().split("\n").drop(1)
