package arrow.fx.coroutines

import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.recover
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Gets current system time in milliseconds since certain moment in the past,
 * only delta between two subsequent calls makes sense.
 *
 * For the JVM target this delegates to `java.lang.System.currentTimeMillis()`
 * For the native targets this delegates to `kotlin.system.getTimeMillis`
 * For Javascript it relies on `new Date().getTime()`
 */
public expect fun timeInMillis(): Long

@OptIn(ExperimentalContracts::class)
@PublishedApi
internal object FailureValue {
  private class Failure(val error: Any?)
  fun failureValue(error: Any?): Any? = Failure(error)

  @Suppress("UNCHECKED_CAST")
  fun <E, T> RaiseAccumulate<E>.bindNel(value: Any?): T =
    withNel {
      if (value is Failure) raise(value.error as NonEmptyList<E>) else value as T
    }

  inline fun <E, T> mightFail(block: Raise<E>.() -> T): Any? {
    contract {
      callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return recover(block, ::failureValue)
  }
}
