package arrow.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.jvm.JvmInline

public fun <R, A> cont(f: suspend ContEffect<R>.() -> A): Cont<R, A> =
  Continuation(f)

/**
 * [Cont] represents a suspending computation that runs will either
 *  - Complete with a value of [A].
 *  - Short-circuit with a value of [R].
 *
 * So [Cont] is defined by [fold], to map both values of [R] and [A] to a value of `B`.
 */
public interface Cont<R, A> {
  public suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B

  public suspend fun toEither(): Either<R, A> =
    fold({ Either.Left(it) }) { Either.Right(it) }

  public suspend fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  public fun attempt(): Cont<R, Result<A>> =
    cont { runCatching { bind() } }

  public fun <B> map(f: suspend (A) -> B): Cont<R, B> =
    cont { fold(this::shift, f) }

  public fun <B> flatMap(f: suspend (A) -> Cont<R, B>): Cont<R, B> =
    cont { fold(this::shift, f).bind() }

  public fun handleError(f: suspend (R) -> A): Cont<Nothing, A> =
    cont { fold(f, ::identity) }

  public fun <R2> handleErrorWith(f: suspend (R) -> Cont<R2, A>): Cont<R2, A> =
    cont { fold({ f(it).bind() }, ::identity) }

  public fun <B> redeem(f: suspend (R) -> B, g: suspend (A) -> B): Cont<Nothing, B> =
    cont { fold(f, g) }

  public fun <R2, B> redeemWith(f: suspend (R) -> Cont<R2, B>, g: suspend (A) -> Cont<R2, B>): Cont<R2, B> =
    cont { fold(f, g).bind() }
}

/** Context of the [Cont] DSL. */
public interface ContEffect<R> {
  /**
   * Short-circuit the [Cont] computation with value [R].
   */
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

  public suspend fun <B> Option<B>.bind(shift: () -> R): B =
    when (this) {
      None -> shift(shift())
      is Some -> value
    }

  /**
   * Ensure check if the [value] is `true`.
   * if it is true it will allow the continuation to continue running,
   * if it is `false`, then it cancel the continuation and return the provided [R].
   * Monadic version of kotlin.require
   *
   * ```kotlin:ank:playground
   * import arrow.core.cont
   *
   * //sampleStart
   * suspend fun main() {
   *   cont<String, Int> {
   *     ensure(true) { "this will not fail" }
   *     println("ensure(true) passes")
   *     ensure(false) { "failed" }
   *     TODO("The program will never reach this point")
   *   }.toEither()
   * //sampleEnd
   * }
   * // println: "ensure(true) passes"
   * // res: Either.Left("failed")
   * ```
   */
  public suspend fun ensure(value: Boolean, shift: () -> R): Unit =
    if (value) Unit else shift(shift())
}

// Monadic version of kotlin.requireNotNull
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so top-level.
public suspend fun <R, B : Any> ContEffect<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}

// We create a `Token` for every scope, so we can properly differentiate between nested scopes
private class ShiftCancellationException(val token: Token, val value: Any?) :
  CancellationException("Shifted Continuation")

// Class that represents a unique token by hash comparison
private class Token {
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}

// Reification of Cont program
@JvmInline
private value class Continuation<R, A>(private val f: suspend ContEffect<R>.() -> A) : Cont<R, A> {
  override suspend fun <B> fold(f: suspend (R) -> B, g: suspend (A) -> B): B =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val token = Token()
      val effect = object : ContEffect<R> {
        // Shift away from this Continuation by intercepting it, and completing it with ShiftCancellationException
        // This is needed because this function will never yield a result,
        // so it needs to be cancelled to properly support coroutine cancellation
        override suspend fun <B> shift(r: R): B =
          //Some interesting consequences of how Continuation Cancellation works in Kotlin.
          // We have to throw CancellationException to signal the Continuation was cancelled, and we shifted away.
          // This however also means that the user can try/catch shift and recover from the CancellationException and thus effectively recovering from the cancellation/shift.
          // This means try/catch is also capable of recovering from monadic errors.
          // See: ContSpec - try/catch tests
          throw ShiftCancellationException(token, f(r))
      }

      try {
        suspend { g(f(effect)) }.startCoroutineUninterceptedOrReturn(Continuation(cont.context) { res ->
          res.fold(cont::resume) { throwable ->
            if (throwable is ShiftCancellationException && token == throwable.token) cont.resume(throwable.value as B)
            else cont.resumeWith(res)
          }
        })
      } catch (e: ShiftCancellationException) {
        if (token == e.token) e.value else throw e
      }
    }
}
