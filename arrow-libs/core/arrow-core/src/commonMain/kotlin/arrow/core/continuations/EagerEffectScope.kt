package arrow.core.continuations

import arrow.core.Either
import arrow.core.EmptyValue
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

/** Context of the [EagerEffect] DSL. */
@RestrictsSuspension
public interface EagerEffectScope<in R> {

  /** Short-circuit the [EagerEffect] computation with value [R].
   *
   * ```kotlin
   * import arrow.core.continuations.eagerEffect
   * import io.kotest.assertions.fail
   * import io.kotest.matchers.shouldBe
   *
   * fun main() {
   *   eagerEffect<String, Int> {
   *     shift("SHIFT ME")
   *   }.fold({ it shouldBe "SHIFT ME" }, { fail("Computation never finishes") })
   * }
   * ```
   * <!--- KNIT example-eager-effect-scope-01.kt -->
   */
  public suspend fun <B> shift(r: R): B

  /**
   * Runs the [EagerEffect] to finish, returning [B] or [shift] in case of [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.EagerEffect
   * import arrow.core.continuations.eagerEffect
   * import arrow.core.identity
   * import io.kotest.matchers.shouldBe
   *
   * fun <E, A> Either<E, A>.toEagerEffect(): EagerEffect<E, A> = eagerEffect {
   *   fold({ e -> shift(e) }, ::identity)
   * }
   *
   * fun main() {
   *   val either = Either.Left("failed")
   *   eagerEffect<String, Int> {
   *     val x: Int = either.toEagerEffect().bind()
   *     x
   *   }.toEither() shouldBe either
   * }
   * ```
   * <!--- KNIT example-eager-effect-scope-02.kt -->
   */
  public suspend fun <B> EagerEffect<R, B>.bind(): B {
    var left: Any? = EmptyValue
    var right: Any? = EmptyValue
    fold({ r -> left = r }, { a -> right = a })
    return if (left === EmptyValue) EmptyValue.unbox(right) else shift(EmptyValue.unbox(left))
  }

  /**
   * Folds [Either] into [EagerEffect], by returning [B] or a shift with [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.eagerEffect
   * import io.kotest.matchers.shouldBe
   *
   * fun main() {
   *   val either = Either.Right(9)
   *   eagerEffect<String, Int> {
   *     val x: Int = either.bind()
   *     x
   *   }.toEither() shouldBe either
   * }
   * ```
   * <!--- KNIT example-eager-effect-scope-03.kt -->
   */
  public suspend fun <B> Either<R, B>.bind(): B =
    when (this) {
      is Either.Left -> shift(value)
      is Either.Right -> value
    }

  /**
   * Folds [Validated] into [EagerEffect], by returning [B] or a shift with [R].
   *
   * ```kotlin
   * import arrow.core.Validated
   * import arrow.core.continuations.eagerEffect
   * import io.kotest.matchers.shouldBe
   *
   * fun main() {
   *   val validated = Validated.Valid(40)
   *   eagerEffect<String, Int> {
   *     val x: Int = validated.bind()
   *     x
   *   }.toValidated() shouldBe validated
   * }
   * ```
   * <!--- KNIT example-eager-effect-scope-04.kt -->
   */
  public suspend fun <B> Validated<R, B>.bind(): B =
    when (this) {
      is Validated.Valid -> value
      is Validated.Invalid -> shift(value)
    }

  /**
   * Folds [Result] into [EagerEffect], by returning [B] or a transforming [Throwable] into [R] and
   * shifting the result.
   *
   * ```kotlin
   * import arrow.core.continuations.eagerEffect
   * import arrow.core.identity
   * import io.kotest.matchers.shouldBe
   *
   * private val default = "failed"
   * fun main() {
   *   val result = Result.success(1)
   *   eagerEffect<String, Int> {
   *     val x: Int = result.bind { _: Throwable -> default }
   *     x
   *   }.fold({ default }, ::identity) shouldBe result.getOrElse { default }
   * }
   * ```
   * <!--- KNIT example-eager-effect-scope-05.kt -->
   */
  public suspend fun <B> Result<B>.bind(transform: (Throwable) -> R): B =
    fold(::identity) { throwable -> shift(transform(throwable)) }

  /**
   * Folds [Option] into [EagerEffect], by returning [B] or a transforming [None] into [R] and shifting the
   * result.
   *
   * ```kotlin
   * import arrow.core.None
   * import arrow.core.Option
   * import arrow.core.continuations.eagerEffect
   * import arrow.core.getOrElse
   * import arrow.core.identity
   * import io.kotest.matchers.shouldBe
   *
   * private val default = "failed"
   * fun main() {
   *   val option: Option<Int> = None
   *   eagerEffect<String, Int> {
   *     val x: Int = option.bind { default }
   *     x
   *   }.fold({ default }, ::identity) shouldBe option.getOrElse { default }
   * }
   * ```
   * <!--- KNIT example-eager-effect-scope-06.kt -->
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
   * import arrow.core.continuations.eagerEffect
   * import io.kotest.matchers.shouldBe
   *
   * fun main() {
   *   val condition = true
   *   val failure = "failed"
   *   val int = 4
   *   eagerEffect<String, Int> {
   *     ensure(condition) { failure }
   *     int
   *   }.toEither() shouldBe if(condition) Either.Right(int) else Either.Left(failure)
   * }
   * ```
   * <!--- KNIT example-eager-effect-scope-07.kt -->
   */
  public suspend fun ensure(condition: Boolean, shift: () -> R): Unit =
    if (condition) Unit else shift(shift())
}

/**
 * Ensure that [value] is not `null`. if it's non-null it will be smart-casted and returned if it's
 * `false` it will `shift` with the provided value [R]. Monadic version of [kotlin.requireNotNull].
 *
 * ```kotlin
 * import arrow.core.continuations.eagerEffect
 * import arrow.core.continuations.ensureNotNull
 * import arrow.core.left
 * import arrow.core.right
 * import io.kotest.matchers.shouldBe
 *
 * fun main() {
 *   val failure = "failed"
 *   val int: Int? = null
 *   eagerEffect<String, Int> {
 *     ensureNotNull(int) { failure }
 *   }.toEither() shouldBe (int?.right() ?: failure.left())
 * }
 * ```
 * <!--- KNIT example-eager-effect-scope-08.kt -->
 */
@OptIn(ExperimentalContracts::class)
public suspend fun <R, B : Any> EagerEffectScope<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}
