@file:Suppress("API_NOT_AVAILABLE")

package arrow

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi @IgnorableReturnValue
internal actual fun Throwable.throwIfFatal(): Throwable =
  when (this) {
    is CancellationException -> throw this
    else -> this
  }
