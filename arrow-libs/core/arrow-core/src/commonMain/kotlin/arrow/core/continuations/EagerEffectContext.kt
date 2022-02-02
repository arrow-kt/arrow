package arrow.core.continuations

import arrow.core.EmptyValue
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.jvm.JvmInline

@RestrictsSuspension
public interface EagerEffectContext<R> {

  /** Short-circuit the [Effect] computation with value [R]. */
  public suspend fun <B> shift(r: R): B

  public suspend fun <A> EagerEffect<R, A>.bind(): A {
    var left: Any? = EmptyValue
    var right: Any? = EmptyValue
    fold({ r -> left = r }, { a -> right = a })
    return if (left === EmptyValue) EmptyValue.unbox(right) else shift(EmptyValue.unbox(left))
  }
}

private class Eager(val token: Token, val shifted: Any?, val recover: (Any?) -> Any?) :
  ShiftCancellationException() {
  override fun toString(): String = "ShiftCancellationException($message)"
}

@JvmInline
internal value class EagerEffectDsl<R, A>(
  private val f: suspend EagerEffectContext<R>.() -> A
) : EagerEffect<R, A> {
  override fun <B> fold(recover: (R) -> B, transform: (A) -> B): B {
    val token = Token()
    return try {
      var result: Any? = EmptyValue
      val eagerEffectContext =
        object : EagerEffectContext<R> {
          // Shift away from this Continuation by intercepting it, and completing it with
          // ShiftCancellationException
          // This is needed because this function will never yield a result,
          // so it needs to be cancelled to properly support coroutine cancellation
          override suspend fun <B> shift(r: R): B =
          // Some interesting consequences of how Continuation Cancellation works in Kotlin.
          // We have to throw CancellationException to signal the Continuation was cancelled, and we
          // shifted away.
          // This however also means that the user can try/catch shift and recover from the
          // CancellationException and thus effectively recovering from the cancellation/shift.
          // This means try/catch is also capable of recovering from monadic errors.
            // See: ContSpec - try/catch tests
            throw Eager(token, r, recover as (Any?) -> Any?)
        }

      suspend { transform(f(eagerEffectContext)) }
        .startCoroutineUninterceptedOrReturn(
          Continuation(EmptyCoroutineContext) {
            it.fold({ result = it }) { throwable ->
              if (throwable is Eager && token == throwable.token) {
                throwable.recover(throwable.shifted) as B
              } else throw throwable
            }
          }
        )
      result as B
    } catch (e: Eager) {
      if (token == e.token) e.recover(e.shifted) as B
      else throw e
    }
  }
}
