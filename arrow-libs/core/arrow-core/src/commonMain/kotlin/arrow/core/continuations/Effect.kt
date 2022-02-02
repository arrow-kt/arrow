package arrow.core.continuations

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonFatal
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import kotlin.coroutines.Continuation

/**
 * DSL for constructing Effect<R, A> values
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Validated
 * import arrow.core.continuations.effect
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 *
 * suspend fun main() {
 *   effect<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z = Option(3).bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ fail("Shift can never be the result") }, { it shouldBe 6 })
 *
 *   effect<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z: Int = None.bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
 * }
 * ```
 * <!--- KNIT example-effect-01.kt -->
 */
public fun <R, A> effect(f: suspend EffectContext<R>.() -> A): Effect<R, A> = EffectImpl(f)

/**
 * [Effect] represents a suspending computation that runs and will either
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
   * import arrow.core.continuations.effect
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun main() {
   *   val shift = effect<String, Int> {
   *     shift("Hello, World!")
   *   }.fold({ str: String -> str }, { int -> int.toString() })
   *   shift shouldBe "Hello, World!"
   *
   *   val res = effect<String, Int> {
   *     1000
   *   }.fold({ str: String -> str.length }, { int -> int })
   *   res shouldBe 1000
   * }
   * ```
   * <!--- KNIT example-effect-02.kt -->
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
   * [fold] the [Effect] into an [Option]. Where the shifted value [R] is mapped to [Option] by the
   * provided function [orElse], and result value [A] is mapped to [Some].
   */
  public suspend fun toOption(orElse: suspend (R) -> Option<A>): Option<A> = fold(orElse, ::Some)

  /**
   * [fold] the [Effect] into [A?].
   * Where the shifted value [R] is mapped to [null] or results with a value [A].
   */
  public suspend fun orNull(): A? =
    fold({ null }, ::identity)

  /** Runs the [Effect] and captures any [NonFatal] exception into [Result]. */
  public fun attempt(): Effect<R, Result<A>> = effect {
    try {
      Result.success(bind())
    } catch (e: Throwable) {
      Result.failure(e.nonFatalOrThrow())
    }
  }

  public fun handleError(recover: suspend (R) -> A): Effect<Nothing, A> = effect {
    fold(recover, ::identity)
  }

  public fun <R2> handleErrorWith(recover: suspend (R) -> Effect<R2, A>): Effect<R2, A> = effect {
    fold({ recover(it).bind() }, ::identity)
  }

  public fun <B> redeem(recover: suspend (R) -> B, transform: suspend (A) -> B): Effect<Nothing, B> =
    effect {
      fold(recover, transform)
    }

  public fun <R2, B> redeemWith(
    recover: suspend (R) -> Effect<R2, B>,
    transform: suspend (A) -> Effect<R2, B>
  ): Effect<R2, B> = effect { fold(recover, transform).bind() }
}
