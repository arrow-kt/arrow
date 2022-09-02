@file:OptIn(ExperimentalTypeInference::class)
package arrow.core.continuations

import kotlin.experimental.ExperimentalTypeInference

public fun <R, A> Effect<R, A>.traced(recover: (traces: List<String>, R) -> Unit): Effect<R, A> =
  effect { traced({ bind() }, recover) }

public fun <R, A> EagerEffect<R, A>.traced(recover: (traces: List<String>, R) -> Unit): EagerEffect<R, A> =
  eagerEffect { traced({ bind() }, recover) }

public inline fun <R, A> Shift<R>.traced(
  @BuilderInference program: Shift<R>.() -> A,
  recover: (traces: List<String>, R) -> Unit
): A {
  val nested = DefaultShift(true)
  return try {
    program(nested)
  } catch (e: ShiftCancellationException) {
    val r: R = e.shiftedOrRethrow(nested)
    recover(e.stackTrace(), r)
    shift(r)
  }
}

@PublishedApi
internal expect fun ShiftCancellationException.stackTrace(): List<String>
