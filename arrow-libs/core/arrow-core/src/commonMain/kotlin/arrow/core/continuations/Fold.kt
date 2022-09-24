@file:JvmMultifileClass
@file:JvmName("Effect")
@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.continuations

import arrow.core.nonFatalOrThrow
import kotlin.coroutines.cancellation.CancellationException
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * `invoke` the [Effect] and [fold] the result:
 *  - _success_ [transform] result of [A] to a value of [B].
 *  - _shift_ [recover] from `shifted` value of [R] to a value of [B].
 *  - _exception_ [error] from [Throwable] by transforming value into [B].
 *
 * This method should never be wrapped in `try`/`catch` as it will not throw any unexpected errors,
 * it will only result in [CancellationException], or fatal exceptions such as `OutOfMemoryError`.
 */
public suspend fun <R, A, B> Effect<R, A>.fold(
  error: suspend (error: Throwable) -> B,
  recover: suspend (shifted: R) -> B,
  transform: suspend (value: A) -> B,
): B = fold({ invoke() }, { error(it) }, { recover(it) }, { transform(it) })

public suspend fun <R, A, B> Effect<R, A>.fold(
  recover: suspend (shifted: R) -> B,
  transform: suspend (value: A) -> B,
): B = fold({ throw it }, recover, transform)

public fun <R, A, B> EagerEffect<R, A>.fold(recover: (R) -> B, transform: (A) -> B): B =
  fold({ throw it }, recover, transform)

public inline fun <R, A, B> EagerEffect<R, A>.fold(
  error: (error: Throwable) -> B,
  recover: (shifted: R) -> B,
  transform: (value: A) -> B,
): B = fold({ invoke(this) }, error, recover, transform)

@JvmName("_foldOrThrow")
public inline fun <R, A, B> fold(
  @BuilderInference program: Raise<R>.() -> A,
  recover: (shifted: R) -> B,
  transform: (value: A) -> B,
): B = fold(program, { throw it }, recover, transform)

@JvmName("_fold")
public inline fun <R, A, B> fold(
  @BuilderInference program: Raise<R>.() -> A,
  error: (error: Throwable) -> B,
  recover: (shifted: R) -> B,
  transform: (value: A) -> B,
): B {
  val shift = DefaultRaise()
  return try {
    transform(program(shift))
  } catch (e: CancellationException) {
    recover(e.shiftedOrRethrow(shift))
  } catch (e: Throwable) {
    error(e.nonFatalOrThrow())
  }
}

/** Returns the shifted value, rethrows the CancellationException if not our scope */
@PublishedApi
internal fun <R> CancellationException.shiftedOrRethrow(shift: DefaultRaise): R =
  if (this is ShiftCancellationException && this.raise === shift) _shifted as R
  else throw this

/** Serves as both purposes of a scope-reference token, and a default implementation for Shift. */
@PublishedApi
internal class DefaultRaise : Raise<Any?> {
  override fun <B> raise(r: Any?): B = throw ShiftCancellationException(r, this)
}

/** CancellationException is required to cancel coroutines when shifting from within them. */
private class ShiftCancellationException(val _shifted: Any?, val raise: Raise<Any?>) :
  CancellationException("Shifted Continuation")
