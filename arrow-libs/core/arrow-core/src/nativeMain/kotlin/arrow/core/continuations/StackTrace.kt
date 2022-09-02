package arrow.core.continuations

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal actual fun ShiftCancellationException.stackTrace(): List<String> =
  getStackTrace()
    .drop(1) // drop the first element, which is the shift exception itself
