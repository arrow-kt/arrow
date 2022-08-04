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
import kotlin.experimental.ExperimentalTypeInference

/**
 * Marks functions that are running in DSL mode.
 *
 * For example,
 *  the function `catch` exists as an extension on `Effect<E, A>` but it also exists as a DSL inside `Shift`.
 *  To make it visually more clear, we mark it as a DSL inside Kotlin / IDEA.
 *  This way `catch` will appear as a keyword when inside the `Shift` DSL.
 */
@DslMarker
public annotation class ShiftMarker

/** Context of the [Effect] DSL. */
public interface Shift<in R> {
  /**
   * Short-circuit the [Effect] computation with value [R].
   *
   * ```kotlin
   * import arrow.core.continuations.effect
   * import arrow.core.continuations.fold
   * import io.kotest.assertions.fail
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun main() {
   *   effect<String, Int> {
   *     shift("SHIFT ME")
   *   }.fold({ it shouldBe "SHIFT ME" }, { fail("Computation never finishes") })
   * }
   * ```
   * <!--- KNIT example-effect-scope-01.kt -->
   */
  public suspend fun <B> shift(r: R): B
  
  /**
   * Runs the [Effect] to finish, returning [B] or [shift] in case of [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.Effect
   * import arrow.core.continuations.effect
   * import arrow.core.continuations.fold
   * import arrow.core.continuations.toEither
   * import arrow.core.identity
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun <E, A> Either<E, A>.toEffect(): Effect<E, A> = effect {
   *   fold({ e -> shift(e) }, ::identity)
   * }
   *
   * suspend fun main() {
   *   val either = Either.Left("failed")
   *   effect<String, Int> {
   *     val x: Int = either.toEffect().bind()
   *     x
   *   }.toEither() shouldBe either
   * }
   * ```
   * <!--- KNIT example-effect-scope-02.kt -->
   */
  public suspend fun <B> Effect<R, B>.bind(): B =
    invoke(this@Shift)
  
  /**
   * Runs the [EagerEffect] to finish, returning [B] or [shift] in case of [R],
   * bridging eager computations into suspending.
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.EagerEffect
   * import arrow.core.continuations.eagerEffect
   * import arrow.core.continuations.effect
   * import arrow.core.continuations.toEither
   * import arrow.core.identity
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun <E, A> Either<E, A>.toEagerEffect(): EagerEffect<E, A> = eagerEffect {
   *   fold({ e -> shift(e) }, ::identity)
   * }
   *
   * suspend fun main() {
   *   val either = Either.Left("failed")
   *   effect<String, Int> {
   *     val x: Int = either.toEagerEffect().bind()
   *     x
   *   }.toEither() shouldBe either
   * }
   * ```
   * <!--- KNIT example-effect-scope-03.kt -->
   */
  public suspend fun <B> EagerEffect<R, B>.bind(): B {
    var left: Any? = EmptyValue
    var right: Any? = EmptyValue
    fold({ r -> left = r }, { a -> right = a })
    return if (left === EmptyValue) EmptyValue.unbox(right) else shift(EmptyValue.unbox(left))
  }
  
  /**
   * Folds [Either] into [Effect], by returning [B] or a shift with [R].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.effect
   * import arrow.core.continuations.toEither
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
   * <!--- KNIT example-effect-scope-04.kt -->
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
   * import arrow.core.continuations.toValidated
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
   * <!--- KNIT example-effect-scope-05.kt -->
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
   * import arrow.core.continuations.fold
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
   * <!--- KNIT example-effect-scope-06.kt -->
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
   * import arrow.core.continuations.fold
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
   * <!--- KNIT example-effect-scope-07.kt -->
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
   * import arrow.core.continuations.toEither
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
   * <!--- KNIT example-effect-scope-08.kt -->
   */
  public suspend fun ensure(condition: Boolean, shift: () -> R): Unit =
    if (condition) Unit else shift(shift())
  
  /**
   * When the [Effect] has shifted with [R] it will [resolve]
   * the shifted value to [A], and when it ran the computation to
   * completion it will return the value [A].
   * [catch] is used in combination with [effect].
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.None
   * import arrow.core.Option
   * import arrow.core.Validated
   * import arrow.core.continuations.effect
   * import arrow.core.continuations.fold
   * import io.kotest.assertions.fail
   * import io.kotest.matchers.shouldBe
   *
   * suspend fun main() {
   *   effect<String, Int> {
   *     val x = Either.Right(1).bind()
   *     val y = Validated.Valid(2).bind()
   *     val z =
   *      effect { None.bind { "Option was empty" } } catch { 0 }
   *     x + y + z
   *   }.fold({ fail("Shift can never be the result") }, { it shouldBe 3 })
   * }
   * ```
   * <!--- KNIT example-effect-scope-09.kt -->
   */
  @ShiftMarker
  public suspend infix fun <E, A> (suspend Shift<E>.() -> A).catch(
    resolve: suspend Shift<R>.(E) -> A,
  ): A = catch<E, R, A>(resolve).bind()
  
  // @ShiftMarker
  // public suspend fun <E, A> (suspend Shift<E>.() -> A).catch(
  //   recover: suspend Shift<R>.(Throwable) -> A,
  //   resolve: suspend Shift<R>.(E) -> A,
  // ): A = catch<E, R, A>(resolve).attempt(recover)
  
  @ShiftMarker
  public suspend fun <A> (suspend Shift<R>.() -> A).attempt(
    recover: suspend Shift<R>.(Throwable) -> A,
  ): A = attempt<R, A>(recover).bind()
}

// @ShiftMarker
// context(Shift<R>)
// public suspend inline fun <reified T : Throwable, R, A> (suspend Shift<R>.() -> A).attempt(
//   recover: suspend Shift<R>.(Throwable) -> A
// ): A = TODO()

/**
 * Ensure that [value] is not `null`. if it's non-null it will be smart-casted and returned if it's
 * `false` it will `shift` with the provided value [R]. Monadic version of [kotlin.requireNotNull].
 *
 * ```kotlin
 * import arrow.core.continuations.effect
 * import arrow.core.continuations.toEither
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
 * <!--- KNIT example-effect-scope-10.kt -->
 */
@OptIn(ExperimentalContracts::class)
public suspend fun <R, B : Any> Shift<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}

