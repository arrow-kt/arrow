package arrow.core.continuations

import arrow.core.Either
import arrow.core.Ior
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity

/**
 * `RestrictsSuspension` version of `Effect<R, A>`. This version runs eagerly, can can be used in
 * non-suspending code.
 */
public fun <R, A> eagerEffect(f: suspend EagerEffectContext<R>.() -> A): EagerEffect<R, A> =
  EagerEffectDsl(f)

public interface EagerEffect<R, A> {
  public fun <B> fold(recover: (R) -> B, transform: (A) -> B): B

  /**
   * [fold] the [EagerEffect] into an [Either]. Where the shifted value [R] is mapped to [Either.Left], and
   * result value [A] is mapped to [Either.Right].
   */
  public fun toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

  /**
   * [fold] the [EagerEffect] into an [Ior]. Where the shifted value [R] is mapped to [Ior.Left], and
   * result value [A] is mapped to [Ior.Right].
   */
  public suspend fun toIor(): Ior<R, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

  /**
   * [fold] the [EagerEffect] into an [Validated]. Where the shifted value [R] is mapped to
   * [Validated.Invalid], and result value [A] is mapped to [Validated.Valid].
   */
  public fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  /**
   * [fold] the [EagerEffect] into an [Option]. Where the shifted value [R] is mapped to [Option] by the
   * provided function [orElse], and result value [A] is mapped to [Some].
   */
  public suspend fun toOption(orElse: (R) -> Option<A>): Option<A> =
    fold(orElse, ::Some)

  /**
   * [fold] the [Effect] into [A?].
   * Where the shifted value [R] is mapped to [null] or results with a value [A].
   */
  public suspend fun orNull(): A? =
    fold({null}, ::identity)

  public fun <B> map(f: (A) -> B): EagerEffect<R, B> =
    flatMap { a -> eagerEffect { f(a) } }

  public fun <B> flatMap(f: (A) -> EagerEffect<R, B>): EagerEffect<R, B> = eagerEffect {
    f(bind()).bind()
  }

  public fun attempt(): EagerEffect<R, Result<A>> = eagerEffect {
    kotlin.runCatching { bind() }
  }

  public fun handleError(f: (R) -> A): EagerEffect<Nothing, A> = eagerEffect {
    fold(f, ::identity)
  }

  public fun <R2> handleErrorWith(f: (R) -> EagerEffect<R2, A>): EagerEffect<R2, A> =
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
