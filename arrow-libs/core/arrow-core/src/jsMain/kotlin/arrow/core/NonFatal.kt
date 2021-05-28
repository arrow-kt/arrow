package arrow.core

import arrow.continuations.generic.ControlThrowable
import kotlin.coroutines.cancellation.CancellationException

actual fun NonFatal(t: Throwable): Boolean =
  when (t) {
    is ControlThrowable, is CancellationException -> false
    else -> true
  }
