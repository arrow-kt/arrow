package arrow

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonFatal
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.jvm.JvmInline

/**
 * DSL for constructing Cont<R, A> values
 *
 * <!--- TEST_NAME ContTest -->
 * ```kotlin
 * suspend fun test() {
 *   cont<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z = Option(3).bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ fail("Shift can never be the result") }, { it shouldBe 6 })
 *
 *   cont<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z: Int = None.bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
 * }
 * ```
 * <!--- KNIT example-cont-01.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public fun <R, A> cont(f: suspend ContEffect<R>.() -> A): Cont<R, A> = ContImpl(f)

/**
 * [Cont] represents a suspending computation that runs will either
 * - Complete with a value of [A].
 * - Short-circuit with a value of [R].
 *
 * So [Cont] is defined by [fold], to map both values of [R] and [A] to a value of `B`.
 */
public interface Cont<R, A> {
  /**
   * Runs the suspending computation by creating a [Continuation], and running the `fold` function
   * over the computation.
   *
   * When the [Cont] has shifted with [R] it will [recover] the shifted value to [B], and when it
   * ran the computation to completion it will [transform] the value [A] to [B].
   *
   * ```kotlin
   * suspend fun test() {
   *   val shift = cont<String, Int> {
   *     shift("Hello, World!")
   *   }.fold({ str: String -> str }, { int -> int.toString() })
   *   shift shouldBe "Hello, World!"
   *
   *   val res = cont<String, Int> {
   *     1000
   *   }.fold({ str: String -> str.length }, { int -> int })
   *   res shouldBe 1000
   * }
   * ```
   * <!--- KNIT example-cont-02.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public suspend fun <B> fold(
    recover: suspend (shifted: R) -> B,
    transform: suspend (value: A) -> B
  ): B

  /**
   * Like `fold` but also allows folding over any unexpected [Throwable] that might have occurred.
   * @see fold
   */
  public suspend fun <B> fold(
    error: suspend (error: Throwable) -> B,
    recover: suspend (shifted: R) -> B,
    transform: suspend (value: A) -> B
  ): B =
    try {
      fold(recover, transform)
    } catch (e: Throwable) {
      error(e.nonFatalOrThrow())
    }

  /**
   * [fold] the [Cont] into an [Either]. Where the shifted value [R] is mapped to [Either.Left], and
   * result value [A] is mapped to [Either.Right].
   */
  public suspend fun toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

  /**
   * [fold] the [Cont] into an [Ior]. Where the shifted value [R] is mapped to [Ior.Left], and
   * result value [A] is mapped to [Ior.Right].
   */
  public suspend fun toIor(): Ior<R, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

  /**
   * [fold] the [Cont] into an [Validated]. Where the shifted value [R] is mapped to
   * [Validated.Invalid], and result value [A] is mapped to [Validated.Valid].
   */
  public suspend fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  /**
   * [fold] the [Cont] into an [Option]. Where the shifted value [R] is mapped to [Option] by the
   * provided function [orElse], and result value [A] is mapped to [Some].
   */
  public suspend fun toOption(orElse: suspend (R) -> Option<A>): Option<A> = fold(orElse, ::Some)

  /** Runs the [Cont] and captures any [NonFatal] exception into [Result]. */
  public fun attempt(): Cont<R, Result<A>> = cont {
    try {
      Result.success(bind())
    } catch (e: Throwable) {
      Result.failure(e.nonFatalOrThrow())
    }
  }

  /** Maps the values [A] with the [transform] function into [B]. */
  public fun <B> map(transform: suspend (A) -> B): Cont<R, B> = cont {
    fold(this::shift, transform)
  }

  /** Maps the values [A] with the [transform] function into another [Cont] effect. */
  public fun <B> flatMap(transform: suspend (A) -> Cont<R, B>): Cont<R, B> = cont {
    fold(this::shift, transform).bind()
  }

  public fun handleError(recover: suspend (R) -> A): Cont<Nothing, A> = cont {
    fold(recover, ::identity)
  }

  public fun <R2> handleErrorWith(recover: suspend (R) -> Cont<R2, A>): Cont<R2, A> = cont {
    fold({ recover(it).bind() }, ::identity)
  }

  public fun <B> redeem(recover: suspend (R) -> B, transform: suspend (A) -> B): Cont<Nothing, B> =
    cont { fold(recover, transform) }

  public fun <R2, B> redeemWith(
    recover: suspend (R) -> Cont<R2, B>,
    transform: suspend (A) -> Cont<R2, B>
  ): Cont<R2, B> = cont { fold(recover, transform).bind() }
}

public fun <R, A, B> Iterable<A>.traverseCont(transform: (A) -> Cont<R, B>): Cont<R, List<B>> =
  cont {
    map { transform(it).bind() }
  }

public fun <R, A> Iterable<Cont<R, A>>.sequence(): Cont<R, List<A>> = traverseCont(::identity)

/** Context of the [Cont] DSL. */
public interface ContEffect<R> {
  /** Short-circuit the [Cont] computation with value [R]. */
  public suspend fun <B> shift(r: R): B

  public suspend fun <B> Cont<R, B>.bind(): B = fold(this@ContEffect::shift, ::identity)

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
    fold(::identity) { throwable -> shift(transform(throwable)) }

  public suspend fun <B> Option<B>.bind(shift: () -> R): B =
    when (this) {
      None -> shift(shift())
      is Some -> value
    }

  // Monadic version of kotlin.require
  public suspend fun ensure(value: Boolean, shift: () -> R): Unit =
    if (value) Unit else shift(shift())
}

// Monadic version of kotlin.requireNotNull
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so top-level.
public suspend fun <R, B : Any> ContEffect<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}

public sealed class ShiftCancellationException : CancellationException("Shifted Continuation")

private class Internal(val token: Token, val shifted: Any?, val fold: suspend (Any?) -> Any?) :
  ShiftCancellationException() {
  override fun toString(): String = "ShiftCancellationException($message)"
}

// Class that represents a unique token by hash comparison
private class Token {
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}

@JvmInline
private value class ContImpl<R, A>(private val f: suspend ContEffect<R>.() -> A) : Cont<R, A> {

  override fun attempt(): Cont<R, Result<A>> = ContImpl {
    try {
      Result.success(f())
    } catch (e: Throwable) {
      Result.failure(e.nonFatalOrThrow())
    }
  }

  override fun <B> map(transform: suspend (A) -> B): Cont<R, B> = ContImpl { transform(f()) }

  override fun <B> flatMap(transform: suspend (A) -> Cont<R, B>): Cont<R, B> = ContImpl {
    transform(f()).bind()
  }

  // We create a `Token` for fold Continuation, so we can properly differentiate between nested
  // folds
  override suspend fun <B> fold(recover: suspend (R) -> B, transform: suspend (A) -> B): B =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val token = Token()
      val effect =
        object : ContEffect<R> {
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
            throw Internal(token, r, recover as suspend (Any?) -> Any?)
        }

      try {
        suspend { transform(f(effect)) }
          .startCoroutineUninterceptedOrReturn(FoldContinuation(token, cont.context, cont))
      } catch (e: Internal) {
        if (token == e.token) {
          val f: suspend () -> B = { e.fold(e.shifted) as B }
          f.startCoroutineUninterceptedOrReturn(cont)
        } else throw e
      }
    }
}

private class FoldContinuation<B>(
  private val token: Token,
  override val context: CoroutineContext,
  private val cont: Continuation<B>
) : Continuation<B> {
  override fun resumeWith(result: Result<B>) {
    result.fold(cont::resume) { throwable ->
      if (throwable is Internal && token == throwable.token) {
        val f: suspend () -> B = { throwable.fold(throwable.shifted) as B }
        when (val res = f.startCoroutineUninterceptedOrReturn(cont)) {
          COROUTINE_SUSPENDED -> Unit
          else -> cont.resume(res as B)
        }
      } else cont.resumeWith(result)
    }
  }
}
