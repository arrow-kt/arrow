package arrow.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException

public fun Throwable?.throwIfNotNull() { if (this != null) throw this }

/**
 * Merges two nullable [Throwable] values by adding one as suppressed to the other.
 *
 * Returns the non-null throwable when only one is present, or `null` when both are `null`.
 * [ControlCancellationException]s are deprioritized in the presence of other exceptions.
 *
 * @param other Another throwable to merge with this one.
 * @return The merged throwable, or `null` if both are `null`.
 */
@OptIn(ExperimentalContracts::class, InternalArrowApi::class)
public infix fun Throwable?.mergeSuppressed(other: Throwable?): Throwable? {
  contract {
    returns(null) implies (this@mergeSuppressed == null && other == null)
  }
  return when {
    // other completed normally
    other == null -> this
    // this completed normally or with a non-local return
    this == null -> other
    // this completed with raise
    this is ControlCancellationException -> other.also { other.addSuppressed(this) }
    // other completed with cancellation or raise
    other is CancellationException -> this.also { addSuppressed(other) }
    // both completed exceptionally or this completed with cancellation
    else -> this.also { addSuppressed(other.nonFatalOrThrow()) }
  }
}
