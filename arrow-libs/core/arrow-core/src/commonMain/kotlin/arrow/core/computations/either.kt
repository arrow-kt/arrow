package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Validated
import arrow.core.left
import arrow.core.right
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

public fun interface EitherEffect<E, A> : Effect<Either<E, A>> {

  public suspend fun <B> Either<E, B>.bind(): B =
    when (this) {
      is Either.Right -> value
      is Left -> control().shift(this@bind)
    }

  public suspend fun <B> Validated<E, B>.bind(): B =
    when (this) {
      is Validated.Valid -> value
      is Validated.Invalid -> control().shift(Left(value))
    }

  public suspend fun ensure(boolean: Boolean, orLeft: () -> E): Unit =
    if (boolean) Unit else orLeft().left().bind()
}

@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <E, B : Any> EitherEffect<E, *>.ensureNotNull(value: B?, orLeft: () -> E): B {
  contract {
    returns() implies (value != null)
  }

  return value ?: orLeft().left().bind()
}

@RestrictsSuspension
public fun interface RestrictedEitherEffect<E, A> : EitherEffect<E, A>

@Suppress("ClassName")
public object either {
  public inline fun <E, A> eager(crossinline c: suspend RestrictedEitherEffect<E, *>.() -> A): Either<E, A> =
    Effect.restricted(eff = { RestrictedEitherEffect { it } }, f = c, just = { it.right() })

  public suspend inline operator fun <E, A> invoke(crossinline c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
    Effect.suspended(eff = { EitherEffect { it } }, f = c, just = { it.right() })
}
