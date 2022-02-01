package arrow.core.continuations

import arrow.core.Either
import arrow.core.Validated
import arrow.core.identity

public interface EagerEffect<R, A> {
  public fun <B> fold(recover: (R) -> B, transform: (A) -> B): B

  public fun toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

  public fun toValidated(): Validated<R, A> =
    fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

  public fun <B> map(f: (A) -> B): EagerEffect<R, B> = flatMap { a -> eagerEffect { f(a) } }

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

/**
 * `RestrictsSuspension` version of `Effect<R, A>`. This version runs eagerly, can can be used in
 * non-suspending code.
 */
public fun <R, A> eagerEffect(f: suspend EagerEffectContext<R>.() -> A): EagerEffect<R, A> =
  EagerEffectDsl(f)

