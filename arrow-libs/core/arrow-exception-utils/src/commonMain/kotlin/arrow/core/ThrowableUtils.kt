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
    // other completed normally
    other == null -> this
    // this completed normally or with a non-local return
    this == null -> other
    // this completed with a cancellation
    this is CancellationException -> other.also { other.addSuppressed(this) }
    // other completed with a cancellation
    other is CancellationException -> this.also { addSuppressed(other) }
    // both completed exceptionally
    else -> this.also { addSuppressed(other.nonFatalOrThrow()) }
  }
}
