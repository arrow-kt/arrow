package arrow.core.continuations

import arrow.core.Either
import arrow.core.EmptyValue
import arrow.core.Validated
import arrow.core.identity
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

public interface EagerEffect<R, A> {
  public fun <B> fold(recover: (R) -> B, transform: (A) -> B): B

  public fun toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

  public fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  public fun <B> map(f: (A) -> B): EagerEffect<R, B> = flatMap { a -> eagerEffect { f(a) } }

  public fun <B> flatMap(f: (A) -> EagerEffect<R, B>): EagerEffect<R, B> = eagerEffect {
    f(bind()).bind()
  }

  public fun attempt(): EagerEffect<R, Result<A>> = eagerEffect {
    kotlin.runCatching { bind() }
  }

  public fun handleError(f: (R) -> A): EagerEffect<Nothing, A> = eagerEffect {
    fold(f, ::identity)
  }

  public fun <R2> handleErrorWith(f: (R) -> EagerEffect<R2, A>): EagerEffect<R2, A> =
    eagerEffect {
      toEither().fold({ r -> f(r).bind() }, ::identity)
    }

  public fun <B> redeem(f: (R) -> B, g: (A) -> B): EagerEffect<Nothing, B> = eagerEffect {
    fold(f, g)
  }

  public fun <R2, B> redeemWith(
    f: (R) -> EagerEffect<R2, B>,
    g: (A) -> EagerEffect<R2, B>
  ): EagerEffect<R2, B> = eagerEffect { fold(f, g).bind() }
}

/**
 * `RestrictsSuspension` version of `Effect<R, A>`. This version runs eagerly, can can be used in
 * non-suspending code.
 */
public fun <R, A> eagerEffect(f: suspend EagerEffectContext<R>.() -> A): EagerEffect<R, A> =
  EagerEffectDsl(f)


private class Eager(val token: Token, val shifted: Any?, val recover: (Any?) -> Any?) :
  ShiftCancellationException() {
  override fun toString(): String = "ShiftCancellationException($message)"
}

// change to inline value class after fix in 1.6.20
internal data class EagerEffectDsl<R, A>(
  private val f: suspend EagerEffectContext<R>.() -> A
) : EagerEffect<R, A> {
  override fun <B> fold(recover: (R) -> B, transform: (A) -> B): B {
    val token = Token()
    return try {
      var result: Any? = EmptyValue
      suspend {
        transform(
          f(
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
          )
        )
      }.startCoroutineUninterceptedOrReturn(
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
