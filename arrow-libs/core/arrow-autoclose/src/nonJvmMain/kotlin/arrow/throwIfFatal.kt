package arrow

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal actual fun Throwable.throwIfFatal() {
  when (this) {
    is CancellationException -> throw this
  }
}
