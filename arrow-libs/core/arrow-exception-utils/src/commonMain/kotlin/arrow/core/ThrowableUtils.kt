package arrow.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException

public fun Throwable?.throwIfNotNull() { if (this != null) throw this }

@OptIn(ExperimentalContracts::class)
public infix fun Throwable?.mergeSuppressed(other: Throwable?): Throwable? {
  contract {
    returns(null) implies (this@mergeSuppressed == null && other == null)
  }
  return when {
    other == null -> this
    this == null -> other
    other is CancellationException -> this.also { addSuppressed(other) }
    else -> this.also { addSuppressed(other.nonFatalOrThrow()) }
  }
}
