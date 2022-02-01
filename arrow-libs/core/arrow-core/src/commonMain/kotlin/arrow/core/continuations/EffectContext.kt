package arrow.core.continuations

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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
   * <!--- KNIT example-effect-01.kt -->
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
   * <!--- KNIT example-effect-02.kt -->
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
   * <!--- KNIT example-effect-03.kt -->
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
   * <!--- KNIT example-effect-04.kt -->
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
   * <!--- KNIT example-effect-05.kt -->
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
   * <!--- KNIT example-effect-06.kt -->
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
   * <!--- KNIT example-effect-07.kt -->
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
 * <!--- KNIT example-effect-08.kt -->
 */
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so top-level.
public suspend fun <R, B : Any> EffectContext<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}
