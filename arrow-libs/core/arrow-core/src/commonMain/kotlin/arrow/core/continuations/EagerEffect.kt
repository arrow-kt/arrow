package arrow.core.continuations

import arrow.core.Either
import arrow.core.Ior
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.RestrictsSuspension

/**
 * [RestrictsSuspension] version of [Effect]. This version runs eagerly, and can be used in
 * non-suspending code.
 * An [effect] computation interoperates with an [EagerEffect] via `bind`.
 * @see Effect
 */
public interface EagerEffect<out R, out A> {

  /**
   * Runs the non-suspending computation by creating a [Continuation] with an [EmptyCoroutineContext],
   * and running the `fold` function over the computation.
   *
   * When the [EagerEffect] has shifted with [R] it will [recover] the shifted value to [B], and when it
   * ran the computation to completion it will [transform] the value [A] to [B].
   *
   * ```kotlin
   * import arrow.core.continuations.eagerEffect
   * import io.kotest.matchers.shouldBe
   *
   * fun main() {
   *   val shift = eagerEffect<String, Int> {
   *     shift("Hello, World!")
   *   }.fold({ str: String -> str }, { int -> int.toString() })
   *   shift shouldBe "Hello, World!"
   *
   *   val res = eagerEffect<String, Int> {
   *     1000
   *   }.fold({ str: String -> str.length }, { int -> int })
   *   res shouldBe 1000
   * }
   * ```
   * <!--- KNIT example-eager-effect-01.kt -->
   */
  public fun <B> fold(recover: (R) -> B, transform: (A) -> B): B

  /**
   * Like `fold` but also allows folding over any unexpected [Throwable] that might have occurred.
   * @see fold
   */
  public fun <B> fold(
    error: (error: Throwable) -> B,
    recover: (shifted: R) -> B,
    transform: (value: A) -> B
  ): B =
    try {
      fold(recover, transform)
    } catch (e: Throwable) {
      error(e.nonFatalOrThrow())
    }

  /**
   * [fold] the [EagerEffect] into an [Ior]. Where the shifted value [R] is mapped to [Ior.Left], and
   * result value [A] is mapped to [Ior.Right].
   */
  public fun toIor(): Ior<R, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

  /**
   * [fold] the [EagerEffect] into an [Either]. Where the shifted value [R] is mapped to [Either.Left], and
   * result value [A] is mapped to [Either.Right].
   */
  public fun toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

  /**
   * [fold] the [EagerEffect] into an [A?]. Where the shifted value [R] is mapped to
   * [null], and result value [A].
   */
  public fun orNull(): A? = fold({ null }, ::identity)

  /**
   * [fold] the [EagerEffect] into an [Option]. Where the shifted value [R] is mapped to [Option] by the
   * provided function [orElse], and result value [A] is mapped to [Some].
   */
  public fun toOption(orElse: (R) -> Option<@UnsafeVariance A>): Option<A> =
    fold(orElse, ::Some)

  @Deprecated(deprecateMonadAppFunctorOperators, ReplaceWith("flatMap { eagerEffect { f(it) } }"))
  public fun <B> map(f: (A) -> B): EagerEffect<R, B> = flatMap { a -> eagerEffect { f(a) } }

  @Deprecated(deprecateMonadAppFunctorOperators)
  public fun <B> flatMap(f: (A) -> EagerEffect<@UnsafeVariance R, B>): EagerEffect<R, B> = eagerEffect {
    f(bind()).bind()
  }

  public fun attempt(): EagerEffect<R, Result<A>> = eagerEffect {
    kotlin.runCatching { bind() }
  }

  public fun handleError(f: (R) -> @UnsafeVariance A): EagerEffect<Nothing, A> = eagerEffect {
    fold(f, ::identity)
  }

  public fun <R2> handleErrorWith(f: (R) -> EagerEffect<R2, @UnsafeVariance A>): EagerEffect<R2, A> =
    eagerEffect {
      toEither().fold({ r -> f(r).bind() }, ::identity)
    }

  public fun <B> redeem(f: (R) -> B, g: (A) -> B): EagerEffect<Nothing, B> = eagerEffect {
    fold(f, g)
  }

  public fun <R2, B> redeemWith(
    f: (R) -> EagerEffect<R2, B>,
    g: (A) -> EagerEffect<R2, B>
  ): EagerEffect<R2, B> = eagerEffect { fold(f, g).bind() }
}

@PublishedApi
internal class Eager(val token: Token, val shifted: Any?, val recover: (Any?) -> Any?) :
  ShiftCancellationException() {
  override fun toString(): String = "ShiftCancellationException($message)"
}

/**
 * DSL for constructing `EagerEffect<R, A>` values
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Validated
 * import arrow.core.continuations.eagerEffect
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 *
 * fun main() {
 *   eagerEffect<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z = Option(3).bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ fail("Shift can never be the result") }, { it shouldBe 6 })
 *
 *   eagerEffect<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z: Int = None.bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
 * }
 * ```
 * <!--- KNIT example-eager-effect-02.kt -->
 */
public fun <R, A> eagerEffect(f: suspend EagerEffectScope<R>.() -> A): EagerEffect<R, A> = DefaultEagerEffect(f)

private class DefaultEagerEffect<R, A>(private val f: suspend EagerEffectScope<R>.() -> A) : EagerEffect<R, A> {
  override fun <B> fold(recover: (R) -> B, transform: (A) -> B): B {
    val token = Token()
    val eagerEffectScope =
      object : EagerEffectScope<R> {
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
          // See: EagerEffectSpec - try/catch tests
          throw Eager(token, r, recover as (Any?) -> Any?)
      }

    return try {
      suspend { transform(f(eagerEffectScope)) }
        .startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { result ->
          result.getOrElse { throwable ->
            if (throwable is Eager && token == throwable.token) {
              throwable.recover(throwable.shifted) as B
            } else throw throwable
          }
        }) as B
    } catch (e: Eager) {
      if (token == e.token) e.recover(e.shifted) as B
      else throw e
    }
  }
}

private const val deprecateMonadAppFunctorOperators: String = "Operators related to Functor, Applicative or Monad hierarchies are being deprecated in favor of bind"

public fun <A> EagerEffect<A, A>.merge(): A = fold(::identity, ::identity)
