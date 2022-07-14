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
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

/** Context of the [Effect] DSL. */
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
  public suspend fun <B> Effect<R, B>.bind(): B =
    when (this) {
      is DefaultEffect -> f(this@EffectScope)
    }
  
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
 * <!--- KNIT example-effect-scope-09.kt -->
 */
@OptIn(ExperimentalContracts::class)
public suspend fun <R, B : Any> EffectScope<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}

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
 * <!--- KNIT example-effect-02.kt -->
 */
public fun <R, A> effect(f: suspend EffectScope<R>.() -> A): Effect<R, A> = DefaultEffect(f)

private class DefaultEffect<R, A>(val f: suspend EffectScope<R>.() -> A) : Effect<R, A> {
  // We create a `Token` for fold Continuation, so we can properly differentiate between nested
  // folds
  override suspend fun <B> fold(recover: suspend (R) -> B, transform: suspend (A) -> B): B =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val token = Token()
      val effectScope =
        object : EffectScope<R> {
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
            // See: EffectSpec - try/catch tests
            throw Suspend(token, r, recover as suspend (Any?) -> Any?)
        }
      
      try {
        suspend { transform(f(effectScope)) }
          .startCoroutineUninterceptedOrReturn(FoldContinuation(token, cont.context, cont))
      } catch (e: Suspend) {
        if (token == e.token) {
          val f: suspend () -> B = { e.recover(e.shifted) as B }
          f.startCoroutineUninterceptedOrReturn(cont)
        } else throw e
      }
    }
}
