@file:JvmMultifileClass
@file:JvmName("Effect")

package arrow.core.continuations

import arrow.core.nonFatalOrThrow
import kotlin.coroutines.cancellation.CancellationException
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public suspend fun <R, A, B> Effect<R, A>.fold(
  recover: suspend (shifted: R) -> B,
  transform: suspend (value: A) -> B,
): B = fold({ throw it }, recover, transform)

public suspend fun <R, A, B> Effect<R, A>.fold(
  error: suspend (error: Throwable) -> B,
  recover: suspend (shifted: R) -> B,
  transform: suspend (value: A) -> B,
): B = fold(
  { bind() },
  { error(it) },
  { recover(it) },
  { transform(it) }
)

public fun <R, A, B> EagerEffect<R, A>.fold(recover: (R) -> B, transform: (A) -> B): B =
  fold({ throw it }, recover, transform)

public inline fun <R, A, B> EagerEffect<R, A>.fold(
  error: (error: Throwable) -> B,
  recover: (shifted: R) -> B,
  transform: (value: A) -> B,
): B = fold({ invoke(this) }, error, recover, transform)

@OptIn(ExperimentalTypeInference::class)
@JvmName("_fold")
public inline fun <R, A, B> fold(
  @BuilderInference program: Shift<R>.() -> A,
  error: (error: Throwable) -> B,
  recover: (shifted: R) -> B,
  transform: (value: A) -> B,
): B {
  val shift = DefaultShift<R>()
  return try {
    transform(program(shift))
  } catch (e: ShiftCancellationException) {
    if (shift === e.shift) recover(e.shifted as R) else throw e
  } catch (e: Throwable) {
    error(e.nonFatalOrThrow())
  }
}

@PublishedApi
internal class ShiftCancellationException(
  @PublishedApi
  internal val shifted: Any?,
  @PublishedApi
  internal val shift: Shift<Any?>,
) : CancellationException("Shifted Continuation")

@PublishedApi
internal class DefaultShift<R> : Shift<R> {
  override fun <B> shift(r: R): B =
    throw ShiftCancellationException(r, this as DefaultShift<Any?>)
}
