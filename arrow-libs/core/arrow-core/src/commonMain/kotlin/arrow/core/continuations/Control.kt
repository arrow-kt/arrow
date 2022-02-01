package arrow.core.continuations

import arrow.core.Either
import arrow.core.EmptyValue
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
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.jvm.JvmInline

/**
 * DSL for constructing Effect<R, A> values
 *
 * ```kotlin
 * import arrow.core.*
 * import arrow.core.computations.*
 *
 * fun main() {
 *   control<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z = Option(3).bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ fail("Shift can never be the result") }, { it shouldBe 6 })
 *
 *   control<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z: Int = None.bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
 * }
 * ```
 * <!--- KNIT example-control-01.kt -->
 */
public fun <R, A> control(f: suspend ControlEffect<R>.() -> A): Effect<R, A> = EffectImpl(f)

/**
 * [Effect] represents a suspending computation that runs will either
 * - Complete with a value of [A].
 * - Short-circuit with a value of [R].
 *
 * So [Effect] is defined by [fold], to map both values of [R] and [A] to a value of `B`.
 */
public interface Effect<R, A> {
  /**
   * Runs the suspending computation by creating a [Continuation], and running the `fold` function
   * over the computation.
   *
   * When the [Effect] has shifted with [R] it will [recover] the shifted value to [B], and when it
   * ran the computation to completion it will [transform] the value [A] to [B].
   *
   * ```kotlin
   * import arrow.core.*
   * import arrow.core.computations.*
   *
   * fun main() {
   *   val shift = control<String, Int> {
   *     shift("Hello, World!")
   *   }.fold({ str: String -> str }, { int -> int.toString() })
   *   shift shouldBe "Hello, World!"
   *
   *   val res = control<String, Int> {
   *     1000
   *   }.fold({ str: String -> str.length }, { int -> int })
   *   res shouldBe 1000
   * }
   * ```
   * <!--- KNIT example-control-02.kt -->
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
   * [fold] the [Effect] into an [Either]. Where the shifted value [R] is mapped to [Either.Left], and
   * result value [A] is mapped to [Either.Right].
   */
  public suspend fun toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

  /**
   * [fold] the [Effect] into an [Ior]. Where the shifted value [R] is mapped to [Ior.Left], and
   * result value [A] is mapped to [Ior.Right].
   */
  public suspend fun toIor(): Ior<R, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

  /**
   * [fold] the [Effect] into an [Validated]. Where the shifted value [R] is mapped to
   * [Validated.Invalid], and result value [A] is mapped to [Validated.Valid].
   */
  public suspend fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  /**
   * [fold] the [Effect] into an [Result]. Where the shifted value [R] is mapped to [Result] by the
   * provided function [orElse], and result value [A] is mapped to [Result.success].
   */
  public suspend fun toResult(orElse: suspend (R) -> Result<A>): Result<A> =
    fold(orElse) { a -> Result.success(a) }

  /**
   * [fold] the [Effect] into an [Option]. Where the shifted value [R] is mapped to [Option] by the
   * provided function [orElse], and result value [A] is mapped to [Some].
   */
  public suspend fun toOption(orElse: suspend (R) -> Option<A>): Option<A> = fold(orElse, ::Some)

  /** Runs the [Effect] and captures any [NonFatal] exception into [Result]. */
  public fun attempt(): Effect<R, Result<A>> = control {
    try {
      Result.success(bind())
    } catch (e: Throwable) {
      Result.failure(e.nonFatalOrThrow())
    }
  }

  public fun handleError(recover: suspend (R) -> A): Effect<Nothing, A> = control {
    fold(recover, ::identity)
  }

  public fun <R2> handleErrorWith(recover: suspend (R) -> Effect<R2, A>): Effect<R2, A> = control {
    fold({ recover(it).bind() }, ::identity)
  }

  public fun <B> redeem(recover: suspend (R) -> B, transform: suspend (A) -> B): Effect<Nothing, B> =
    control {
      fold(recover, transform)
    }

  public fun <R2, B> redeemWith(
    recover: suspend (R) -> Effect<R2, B>,
    transform: suspend (A) -> Effect<R2, B>
  ): Effect<R2, B> = control { fold(recover, transform).bind() }
}

/** Context of the [Effect] DSL. */
public interface ControlEffect<R> {
  /**
   * Short-circuit the [Effect] computation with value [R].
   * ```kotlin
   * import arrow.core.*
   * import arrow.core.computations.*
   *
   * suspend fun main() {
   *   control<String, Int> {
   *     shift("SHIFT ME")
   *   }.fold({ it shouldBe str }, { fail("Computation never finishes") })
   * }
   * ```
   * <!--- KNIT example-control-03.kt -->
   */
  public suspend fun <B> shift(r: R): B

  /**
   * Runs the [Effect] to finish, returning [B] or [shift] in case of [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.Either.Left
   * import arrow.core.continuations.*
   *
   * fun <E, A> Either<E, A>.toCont(): Effect<E, A> = control {
   *   fold({ e -> shift(e) }, ::identity)
   * }
   *
   * fun main() {
   *   val either = Either.Left(24)
   *   control<String, Int> {
   *     val x: Int = either.toCont().bind()
   *     x
   *   }.toEither() shouldBe either
   * }
   * ```
   * <!--- KNIT example-control-04.kt -->
   */
  public suspend fun <B> Effect<R, B>.bind(): B = fold(this@ControlEffect::shift, ::identity)

  /**
   * Folds [Either] into [Effect], by returning [B] or a shift with [R].
   *
   * ```kotlin
   * import arrow.core.*
   * import arrow.core.continuations.*
   *
   * fun main() {
   *   val either = Either.Right("shift")
   *   control<String, Int> {
   *     val x: Int = either.bind()
   *     x
   *   }.toEither() shouldBe either
   * }
   * ```
   * <!--- KNIT example-control-05.kt -->
   */
  public suspend fun <B> Either<R, B>.bind(): B =
    when (this) {
      is Either.Left -> shift(value)
      is Either.Right -> value
    }

  /**
   * Folds [Validated] into [Effect], by returning [B] or a shift with [R].
   *
   * ```kotlin
   * import arrow.core.*
   * import arrow.core.computations.*
   *
   * fun main() {
   *   val validated = Validated.Valid(40)
   *   control<String, Int> {
   *     val x: Int = validated.bind()
   *     x
   *   }.toValidated() shouldBe validated
   * }
   * ```
   * <!--- KNIT example-control-06.kt -->
   */
  public suspend fun <B> Validated<R, B>.bind(): B =
    when (this) {
      is Validated.Valid -> value
      is Validated.Invalid -> shift(value)
    }

  /**
   * Folds [Result] into [Effect], by returning [B] or a transforming [Throwable] into [R] and
   * shifting the result.
   *
   * ```kotlin
   * private val default = "failed"
   * fun main() {
   *   val result = Result.success(1)
   *   control<String, Int> {
   *     val x: Int = result.bind { _: Throwable -> default }
   *     x
   *   }.fold({ default }, ::identity) shouldBe result.getOrElse { default }
   * }
   * ```
   * <!--- KNIT example-control-07.kt -->
   */
  public suspend fun <B> Result<B>.bind(transform: (Throwable) -> R): B =
    fold(::identity) { throwable -> shift(transform(throwable)) }

  /**
   * Folds [Option] into [Effect], by returning [B] or a transforming [None] into [R] and shifting the
   * result.
   *
   * ```kotlin
   * import arrow.core.*
   * import arrow.core.continuations.*
   *
   * private val default = "failed"
   * fun main() {
   *   val option: Option<Int> = None
   *   control<String, Int> {
   *     val x: Int = option.bind { default }
   *     x
   *   }.fold({ default }, ::identity) shouldBe option.getOrElse { default }
   * }
   * ```
   * <!--- KNIT example-control-08.kt -->
   */
  public suspend fun <B> Option<B>.bind(shift: () -> R): B =
    when (this) {
      None -> shift(shift())
      is Some -> value
    }

  /**
   * ensure that condition is `true`, if it's `false` it will `shift` with the provided value [R].
   * Monadic version of [kotlin.require].
   *
   * ```kotlin
   * import arrow.core.*
   * import arrow.core.continuations.*
   *
   * fun main() {
   *   val condition = true
   *   val failure = "failed"
   *   val int = 4
   *   control<String, Int> {
   *     ensure(condition) { failure }
   *     int
   *   }.toEither() shouldBe if(condition) Either.Right(int) else Either.Left(failure)
   * }
   * ```
   * <!--- KNIT example-control-09.kt -->
   */
  public suspend fun ensure(condition: Boolean, shift: () -> R): Unit =
    if (condition) Unit else shift(shift())
}

/**
 * Ensure that [value] is not `null`. if it's non-null it will be smart-casted and returned if it's
 * `false` it will `shift` with the provided value [R]. Monadic version of [kotlin.requireNotNull].
 *
 * ```kotlin
 * import arrow.core.*
 * import arrow.core.computations.*
 *
 * fun main() {
 *   val failure = "failed"
 *   val int: Int? = null
 *   control<String, Int> {
 *     ensureNotNull(int) { failure }
 *   }.toEither() shouldBe (int?.right() ?: failure.left())
 * }
 * ```
 * <!--- KNIT example-control-10.kt -->
 */
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so top-level.
public suspend fun <R, B : Any> ControlEffect<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}

/**
 * **AVOID USING THIS TYPE, it's meant for low-level cancellation code** When in need in low-level
 * code, you can use this type to differentiate between a foreign [CancellationException] and the
 * one from [Effect].
 */
public sealed class ShiftCancellationException : CancellationException("Shifted Continuation")

//  Holds `R` and `suspend (R) -> B`, the exception that wins the race, will get to execute
// `recover`.
private class Internal(val token: Token, val shifted: Any?, val recover: suspend (Any?) -> Any?) :
  ShiftCancellationException() {
  override fun toString(): String = "ShiftCancellationException($message)"
}

private class Eager(val token: Token, val shifted: Any?, val recover: (Any?) -> Any?) :
  ShiftCancellationException() {
  override fun toString(): String = "ShiftCancellationException($message)"
}

// Class that represents a unique token by hash comparison
private class Token {
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}

@JvmInline
private value class EffectImpl<R, A>(private val f: suspend ControlEffect<R>.() -> A) : Effect<R, A> {

  override fun attempt(): Effect<R, Result<A>> = EffectImpl {
    try {
      Result.success(f())
    } catch (e: Throwable) {
      Result.failure(e.nonFatalOrThrow())
    }
  }

  // We create a `Token` for fold Continuation, so we can properly differentiate between nested
  // folds
  override suspend fun <B> fold(recover: suspend (R) -> B, transform: suspend (A) -> B): B =
    suspendCoroutineUninterceptedOrReturn { control ->
      val token = Token()
      val effect =
        object : ControlEffect<R> {
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
          .startCoroutineUninterceptedOrReturn(FoldContinuation(token, control.context, control))
      } catch (e: Internal) {
        if (token == e.token) {
          val f: suspend () -> B = { e.recover(e.shifted) as B }
          f.startCoroutineUninterceptedOrReturn(control)
        } else throw e
      }
    }
}

/**
 * Continuation that runs the `recover` function, after attempting to calculate [B]. In case we
 * encounter a `shift` after suspension, we will receive [Result.failure] with
 * [ShiftCancellationException]. In that case we still need to run `suspend (R) -> B`, which is what
 * we do inside the body of this `Continuation`, and we complete the [parent] [Continuation] with
 * the result.
 */
private class FoldContinuation<B>(
  private val token: Token,
  override val context: CoroutineContext,
  private val parent: Continuation<B>
) : Continuation<B> {
  override fun resumeWith(result: Result<B>) {
    result.fold(parent::resume) { throwable ->
      if (throwable is Internal && token == throwable.token) {
        val f: suspend () -> B = { throwable.recover(throwable.shifted) as B }
        when (val res = f.startCoroutineUninterceptedOrReturn(parent)) {
          COROUTINE_SUSPENDED -> Unit
          else -> parent.resume(res as B)
        }
      } else parent.resumeWith(result)
    }
  }
}

@JvmInline
internal value class EagerControlDsl<R, A>(
  private val control: suspend EagerControlEffect<R>.() -> A
) : EagerControl<R, A> {
  override fun <B> fold(recover: (R) -> B, transform: (A) -> B): B {
    val token = Token()
    val effect =
      object : EagerControlEffect<R> {
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

    return try {
      var result: Any? = EmptyValue
      suspend { transform(control(effect)) }
        .startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) {
          it.fold({ result = it }) { throwable ->
            if (throwable is Eager && token == throwable.token) {
              throwable.recover(throwable.shifted) as B
            } else throw throwable
          }
        })
      result as B
    } catch (e: Eager) {
      if (token == e.token) e.recover(e.shifted) as B
      else throw e
    }
  }
}
