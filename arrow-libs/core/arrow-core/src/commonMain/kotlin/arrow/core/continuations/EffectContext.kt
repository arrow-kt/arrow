package arrow.core.continuations

import arrow.core.Either
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

/** Context of the [Effect] DSL. */
public interface EffectContext<R> {
  /**
   * Short-circuit the [Effect] computation with value [R].
   * ```kotlin
   * import arrow.core.continuations.effect
   * import io.kotest.assertions.fail
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun main() {
   *   effect<String, Int> {
   *     shift("SHIFT ME")
   *   }.fold({ it shouldBe "SHIFT ME" }, { fail("Computation never finishes") })
   * }
   * ```
   * <!--- KNIT example-effect-context-01.kt -->
   */
  public suspend fun <B> shift(r: R): B

  /**
   * Runs the [Effect] to finish, returning [B] or [shift] in case of [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.Effect
   * import arrow.core.continuations.effect
   * import arrow.core.identity
   * import io.kotest.matchers.shouldBe
   *
   * fun <E, A> Either<E, A>.toCont(): Effect<E, A> = effect {
   *   fold({ e -> shift(e) }, ::identity)
   * }
   *
   * suspend fun main() {
   *   val either = Either.Left("failed")
   *   effect<String, Int> {
   *     val x: Int = either.toCont().bind()
   *     x
   *   }.toEither() shouldBe either
   * }
   * ```
   * <!--- KNIT example-effect-context-02.kt -->
   */
  public suspend fun <B> Effect<R, B>.bind(): B = fold(this@EffectContext::shift, ::identity)

  /**
   * Folds [Either] into [Effect], by returning [B] or a shift with [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.effect
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun main() {
   *   val either = Either.Right(9)
   *   effect<String, Int> {
   *     val x: Int = either.bind()
   *     x
   *   }.toEither() shouldBe either
   * }
   * ```
   * <!--- KNIT example-effect-context-03.kt -->
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
   * import arrow.core.Validated
   * import arrow.core.continuations.effect
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun main() {
   *   val validated = Validated.Valid(40)
   *   effect<String, Int> {
   *     val x: Int = validated.bind()
   *     x
   *   }.toValidated() shouldBe validated
   * }
   * ```
   * <!--- KNIT example-effect-context-04.kt -->
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
   * import arrow.core.continuations.effect
   * import arrow.core.identity
   * import io.kotest.matchers.shouldBe
   *
   * private val default = "failed"
   * suspend fun main() {
   *   val result = Result.success(1)
   *   effect<String, Int> {
   *     val x: Int = result.bind { _: Throwable -> default }
   *     x
   *   }.fold({ default }, ::identity) shouldBe result.getOrElse { default }
   * }
   * ```
   * <!--- KNIT example-effect-context-05.kt -->
   */
  public suspend fun <B> Result<B>.bind(transform: (Throwable) -> R): B =
    fold(::identity) { throwable -> shift(transform(throwable)) }

  /**
   * Folds [Option] into [Effect], by returning [B] or a transforming [None] into [R] and shifting the
   * result.
   *
   * ```kotlin
   * import arrow.core.None
   * import arrow.core.Option
   * import arrow.core.continuations.effect
   * import arrow.core.getOrElse
   * import arrow.core.identity
   * import io.kotest.matchers.shouldBe
   *
   * private val default = "failed"
   * suspend fun main() {
   *   val option: Option<Int> = None
   *   effect<String, Int> {
   *     val x: Int = option.bind { default }
   *     x
   *   }.fold({ default }, ::identity) shouldBe option.getOrElse { default }
   * }
   * ```
   * <!--- KNIT example-effect-context-06.kt -->
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
   * import arrow.core.Either
   * import arrow.core.continuations.effect
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun main() {
   *   val condition = true
   *   val failure = "failed"
   *   val int = 4
   *   effect<String, Int> {
   *     ensure(condition) { failure }
   *     int
   *   }.toEither() shouldBe if(condition) Either.Right(int) else Either.Left(failure)
   * }
   * ```
   * <!--- KNIT example-effect-context-07.kt -->
   */
  public suspend fun ensure(condition: Boolean, shift: () -> R): Unit =
    if (condition) Unit else shift(shift())
}

/**
 * Ensure that [value] is not `null`. if it's non-null it will be smart-casted and returned if it's
 * `false` it will `shift` with the provided value [R]. Monadic version of [kotlin.requireNotNull].
 *
 * ```kotlin
 * import arrow.core.continuations.effect
 * import arrow.core.continuations.ensureNotNull
 * import arrow.core.left
 * import arrow.core.right
 * import io.kotest.matchers.shouldBe
 *
 * suspend fun main() {
 *   val failure = "failed"
 *   val int: Int? = null
 *   effect<String, Int> {
 *     ensureNotNull(int) { failure }
 *   }.toEither() shouldBe (int?.right() ?: failure.left())
 * }
 * ```
 * <!--- KNIT example-effect-context-08.kt -->
 */
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so top-level.
public suspend fun <R, B : Any> EffectContext<R>.ensureNotNull(value: B?, shift: () -> R): B {
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

// Class that represents a unique token by hash comparison
internal class Token {
  override fun toString(): String = "Token(${hashCode().toUInt().toString(16)})"
}

// change to inline value class after fix in 1.6.20
internal data class EffectImpl<R, A>(private val f: suspend EffectContext<R>.() -> A) : Effect<R, A> {

  @Suppress("MoveLambdaOutsideParentheses")
  override fun attempt(): Effect<R, Result<A>> = EffectImpl({
    try {
      Result.success(f())
    } catch (e: Throwable) {
      Result.failure(e.nonFatalOrThrow())
    }
  })

  // We create a `Token` for fold Continuation, so we can properly differentiate between nested
  // folds
  override suspend fun <B> fold(recover: suspend (R) -> B, transform: suspend (A) -> B): B =
    suspendCoroutineUninterceptedOrReturn { continuation ->
      val token = Token()
      val effectContext: EffectContext<R> =
        object : EffectContext<R> {
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
        suspend { transform(f(effectContext)) }
          .startCoroutineUninterceptedOrReturn(FoldContinuation(token, continuation.context, continuation))
      } catch (e: Internal) {
        if (token == e.token) {
          val f: suspend () -> B = { e.recover(e.shifted) as B }
          f.startCoroutineUninterceptedOrReturn(continuation)
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

