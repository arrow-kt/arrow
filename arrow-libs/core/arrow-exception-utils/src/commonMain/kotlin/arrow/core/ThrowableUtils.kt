package arrow.core

import kotlin.coroutines.cancellation.CancellationException

public fun Throwable?.throwIfNotNull() { if (this != null) throw this }

public infix fun Throwable?.mergeSuppressed(other: Throwable?): Throwable? = when {
  other == null -> this
  this == null -> other
  other is CancellationException -> {
    this.addSuppressed(other)
    this
  }
  else -> {
    this.addSuppressed(other.nonFatalOrThrow())
    this
  }
}
