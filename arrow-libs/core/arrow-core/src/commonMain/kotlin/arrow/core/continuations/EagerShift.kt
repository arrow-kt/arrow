package arrow.core.continuations

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension
import kotlin.experimental.ExperimentalTypeInference

/**
 * [EagerShift] is a variant of [Shift] that is annotated with [RestrictsSuspension].
 * [RestrictsSuspension] is a Kotlin compiler supported annotation that prevents foreign suspending functions,
 * or in other words the only `suspend` functions that can be called in `suspend RestrictedShift<R>.() -> A`
 * are methods defined on [EagerShift]. This is done so that you can run an [EagerEffect] without requiring `suspend`.
 *
 * So the following code will not compile, and result in:
 * "Restricted suspending functions can only invoke member or extension suspending functions on their restricted coroutine scope"
 *
 * ```
 * val effect: suspend RestrictedShift<String>.() -> Int = {
 *   delay(1000)
 *   1
 * }
 * ```
 *
 * But the following will even though `shift` is a `suspend fun`.
 *
 * ```kotlin
 * import arrow.core.continuations.EagerShift
 * import arrow.core.continuations.toEither
 *
 * val effect: suspend EagerShift<String>.() -> Int = {
 *   val x = shift<Int>("error")
 *   1
 * }
 * val res = effect.toEither() // Either.Left("error")
 * ```
 * <!--- KNIT example-eager-shift-01.kt -->
 */
@RestrictsSuspension
public interface EagerShift<in R> {

  /** Short-circuit the [EagerEffect] computation with value [R].
   *
   * ```kotlin
   * import arrow.core.continuations.eagerEffect
   * import arrow.core.continuations.fold
   * import io.kotest.assertions.fail
   * import io.kotest.matchers.shouldBe
   *
   * fun main() {
   *   eagerEffect<String, Int> {
   *     shift("SHIFT ME")
   *   }.fold({ it shouldBe "SHIFT ME" }, { fail("Computation never finishes") })
   * }
   * ```
   * <!--- KNIT example-eager-shift-02.kt -->
   */
  public suspend fun <B> shift(r: R): B

  /**
   * Runs the [EagerEffect] to finish, returning [B] or [shift] in case of [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.EagerEffect
   * import arrow.core.continuations.eagerEffect
   * import arrow.core.continuations.toEither
   * import io.kotest.matchers.shouldBe
   *
   * fun main() {
   *   eagerEffect<String, Int> {
   *     val x: Int = shift<Int>("error")
   *     x + 1
   *   }.toEither() shouldBe Either.Left("error")
   * }
   * ```
   * <!--- KNIT example-eager-shift-03.kt -->
   */
  public suspend fun <B> EagerEffect<R, B>.bind(): B =
    invoke(this@EagerShift)

  /**
   * Folds [Either] into [EagerEffect], by returning [B] or a shift with [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.eagerEffect
   * import arrow.core.continuations.toEither
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
   * <!--- KNIT example-eager-shift-04.kt -->
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
   * import arrow.core.continuations.toValidated
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
   * <!--- KNIT example-eager-shift-05.kt -->
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
   * import arrow.core.continuations.fold
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
   * <!--- KNIT example-eager-shift-06.kt -->
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
   * import arrow.core.continuations.fold
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
   * <!--- KNIT example-eager-shift-07.kt -->
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
   * import arrow.core.continuations.toEither
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
   * <!--- KNIT example-eager-shift-08.kt -->
   */
  public suspend fun ensure(condition: Boolean, shift: () -> R): Unit =
    if (condition) Unit else shift(shift())
  
  @OptIn(ExperimentalTypeInference::class)
  @EffectDSL
  public suspend infix fun <E, A> (suspend EagerShift<E>.() -> A).catch(
    @BuilderInference resolve: suspend EagerShift<R>.(E) -> A,
  ): A = catch<E, R, A>(resolve).bind()
  
  @OptIn(ExperimentalTypeInference::class)
  @EffectDSL
  public suspend fun <E, A> (suspend EagerShift<E>.() -> A).catch(
    @BuilderInference recover: suspend EagerShift<R>.(Throwable) -> A,
    @BuilderInference resolve: suspend EagerShift<R>.(E) -> A,
  ): A = catch<E, R, A>(resolve).attempt(recover)
  
  @OptIn(ExperimentalTypeInference::class)
  @EffectDSL
  public suspend fun <A> (suspend EagerShift<R>.() -> A).attempt(
    @BuilderInference recover: suspend EagerShift<R>.(Throwable) -> A,
  ): A = attempt<R, A>(recover).bind()
}

/**
 * Ensure that [value] is not `null`. if it's non-null it will be smart-casted and returned if it's
 * `false` it will `shift` with the provided value [R]. Monadic version of [kotlin.requireNotNull].
 *
 * ```kotlin
 * import arrow.core.continuations.eagerEffect
 * import arrow.core.continuations.toEither
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
 * <!--- KNIT example-eager-shift-09.kt -->
 */
@OptIn(ExperimentalContracts::class)
public suspend fun <R, B : Any> EagerShift<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}
