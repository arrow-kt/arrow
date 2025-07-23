package arrow.core

import kotlin.coroutines.cancellation.CancellationException

public actual fun NonFatal(t: Throwable): Boolean =
  when (t) {
    is CancellationException -> false
    else -> true
  }
