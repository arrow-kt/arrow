import arrow.core.Either
import arrow.core.Validated
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resumeWithException

public fun <R, A> cont(f: suspend ContEffect<R>.() -> A): Cont<R, A> =
  Continuation(f)

/**
 * A Continuation of type [R] and [A] will
 *  - "Successfully" complete with a value of [A].
 *  - Short-circuit with a value of [R].
 */
public interface Cont<R, A> {
  suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B
}

interface ContEffect<R> {
  public suspend fun <B> shift(r: R): B

  public suspend fun <B> Cont<R, B>.bind(): B =
    fold(this@ContEffect::shift, ::identity)

  public suspend fun <B> Either<R, B>.bind(): B =
    when (this) {
      is Either.Left -> shift(value)
      is Either.Right -> value
    }

  public suspend fun <B> Validated<R, B>.bind(): B =
    when (this) {
      is Validated.Valid -> value
      is Validated.Invalid -> shift(value)
    }

  public suspend fun <B> Result<B>.bind(transform: (Throwable) -> R): B =
    fold(::identity) { throwable ->
      shift(transform(throwable))
    }

  public suspend fun ensure(value: Boolean, shift: () -> R): Unit =
    if (value) Unit else shift(shift())
}

@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <R, B : Any> ContEffect<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}

// We create a `Token` for every scope, so we can properly differentiate between nested scopes
private class ShiftCancellationException(val token: Token) : CancellationException("Shifted Continuation")

private class Token {
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}

private class Continuation<R, A>(private val f: suspend ContEffect<R>.() -> A) : Cont<R, A> {
  override suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B =
    suspendCoroutineUninterceptedOrReturn { cont ->
      var token: Token? = null
      var shifted: B? = null
      val effect = object : ContEffect<R> {
        // Shift away from this Continuation by intercepting it, and completing it.
        // The Continuation we grab here, is the same one from line 25 which is constructed on line 40
        // So this is a single continuation, completing it with it's final value means the remainder of the
        // continuation program is short-circuited
        override suspend fun <B> shift(r: R): B {
          shifted = f(r)
          token = Token()
          return suspendCoroutineUninterceptedOrReturn { contB ->
            contB.resumeWithException(ShiftCancellationException(token!!))
            COROUTINE_SUSPENDED
          }
        }
      }

      suspend {
        val a = f(effect)
        g(a)
      }.startCoroutineUninterceptedOrReturn(Continuation(cont.context) { res ->
        res.fold(cont::resume) { throwable ->
          if (throwable is ShiftCancellationException && token == throwable.token) cont.resume(shifted!!)
          else cont.resumeWith(res)
        }
      })
    }
}
