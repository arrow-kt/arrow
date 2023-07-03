package arrow.core.continuations

import arrow.core.Either
import arrow.core.EmptyValue
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.ValidatedDeprMsg
import arrow.core.identity
import arrow.core.raise.fold
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

/** Context of the [Effect] DSL. */
@Deprecated(
  "Use the arrow.core.raise.Raise type instead, which is more general and can be used to raise typed errors or _logical failures_\n" +
    "The Raise<R> type is source compatible, a simple find & replace of arrow.core.continuations.* to arrow.core.raise.* will do the trick.",
  ReplaceWith("Raise<R>", "arrow.core.raise.Raise")
)
public interface EffectScope<in R> {
  /**
   * Short-circuit the [Effect] computation with value [R].
   *
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
  public suspend fun <B> Effect<R, B>.bind(): B = fold(this@EffectScope::shift, ::identity)
  
  /**
   * Runs the [EagerEffect] to finish, returning [B] or [shift] in case of [R],
   * bridging eager computations into suspending.
   *
   * ```kotlin
   * import arrow.core.Either
   * import arrow.core.continuations.EagerEffect
   * import arrow.core.continuations.eagerEffect
   * import arrow.core.continuations.effect
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
  
  public suspend fun <B> arrow.core.raise.Effect<R, B>.bind(): B =
    fold(this@EffectScope::shift, ::identity)
  
  public suspend fun <B> arrow.core.raise.EagerEffect<R, B>.bind(): B =
    fold({ shift(it) }, ::identity)
  
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
  @Deprecated(ValidatedDeprMsg, ReplaceWith("toEither().bind()"))
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
   * Encloses an action for which you want to catch any `shift`.
   * [attempt] is used in combination with [catch].
   *
   * ```
   * attempt { ... } catch { ... }
   * ```
   *
   * The [f] may `shift` into a different `EffectScope`, giving
   * the chance for a later [catch] to change the shifted value.
   * This is useful to simulate re-throwing of exceptions.
   */
  @Deprecated(
    "Use the arrow.core.raise.Raise type instead, which is more general and can be used to  and can be used to raise typed errors or _logical failures_\n" +
      "The Raise<R> type is source compatible, a simple find & replace of arrow.core.continuations.* to arrow.core.raise.* will do the trick. Add missing imports and you're good to go!",
    ReplaceWith("effect(f)", "arrow.core.raise.effect")
  )
  @OptIn(ExperimentalTypeInference::class)
  public suspend fun <E, A> attempt(
    @BuilderInference
    f: suspend EffectScope<E>.() -> A,
  ): suspend EffectScope<E>.() -> A = f
  
  /**
   * When the [Effect] has shifted with [R] it will [recover]
   * the shifted value to [A], and when it ran the computation to
   * completion it will return the value [A].
   * [catch] is used in combination with [attempt].
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
   *     val z =
   *      attempt { None.bind { "Option was empty" } } catch { 0 }
   *     x + y + z
   *   }.fold({ fail("Shift can never be the result") }, { it shouldBe 3 })
   * }
   * ```
   * <!--- KNIT example-effect-scope-09.kt -->
   */
  @Deprecated(
    "This method is renamed to recover in the new Raise type."  +
      "Apply the ReplaceWith refactor, and then a simple find & replace of arrow.core.continuations.* to arrow.core.raise.* will do the trick. Add missing imports and you're good to go!",
    ReplaceWith("recover(f)")
  )
  public suspend infix fun <E, A> (suspend EffectScope<E>.() -> A).catch(
    recover: suspend EffectScope<R>.(E) -> A,
  ): A = effect(this).fold({ recover(it) }, ::identity)
  
  public suspend infix fun <E, A> (suspend EffectScope<E>.() -> A).recover(
    recover: suspend EffectScope<R>.(E) -> A,
  ): A = effect(this).fold({ recover(it) }, ::identity)
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
 * <!--- KNIT example-effect-scope-10.kt -->
 */
@Deprecated(
  "Use the arrow.core.raise.Raise type instead, which is more general and can be used to  and can be used to raise typed errors or _logical failures_\n" +
    "The Raise<R> type is source compatible, a simple find & replace of arrow.core.continuations.* to arrow.core.raise.* will do the trick.",
  ReplaceWith("ensureNotNull(value, shift)", "arrow.core.raise.ensureNotNull")
)
@OptIn(ExperimentalContracts::class)
public suspend fun <R, B : Any> EffectScope<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}

