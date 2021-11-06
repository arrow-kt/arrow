package arrow.core.computations

import arrow.continuations.Effect
import arrow.continuations.generic.DelimitedScope
import arrow.core.Cont
import arrow.core.ContEffect
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Validated
import arrow.core.cont
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

@Deprecated("Use ContEffect<E> instead")
public interface EitherEffect<E, A> : Effect<Either<E, A>>, ContEffect<E> {
  override fun control(): DelimitedScope<Either<E, A>> =
    object : DelimitedScope<Either<E, A>> {
      override suspend fun <B> shift(r: Either<E, A>): B =
        when (r) {
          is Left -> this@EitherEffect.shift(r.value)
          is Either.Right ->
            TODO("What to do here? This should never be called??")
        }
    }

  public override suspend fun <B> Either<E, B>.bind(): B =
    when (this) {
      is Either.Right -> value
      is Left -> shift(value)
    }

  public override suspend fun <B> Validated<E, B>.bind(): B =
    when (this) {
      is Validated.Valid -> value
      is Validated.Invalid -> shift(value)
    }

  public override suspend fun <B> Result<B>.bind(transform: (Throwable) -> E): B =
    fold(::identity) { throwable ->
      shift(transform(throwable))
    }

  public override suspend fun ensure(value: Boolean, orLeft: () -> E): Unit =
    if (value) Unit else shift(orLeft())
}

/**
 * Ensures that [value] is not null.
 * When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null.
 * Otherwise, if the [value] is null then the [either] binding will short-circuit with [orLeft] inside of [Either.Left].
 *
 * ```kotlin:ank
 * import arrow.core.computations.either
 * import arrow.core.computations.ensureNotNull
 *
 * //sampleStart
 * suspend fun main() {
 *   either<String, Int> {
 *     val x: Int? = 1
 *     ensureNotNull(x) { "passes" }
 *     println(x)
 *     ensureNotNull(null) { "failed" }
 *   }
 * //sampleEnd
 *   .let(::println)
 * }
 * // println: "1"
 * // res: Either.Left("failed")
 * ```
 */
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <E, B : Any> EitherEffect<E, *>.ensureNotNull(value: B?, orLeft: () -> E): B {
  contract { returns() implies (value != null) }
  return value ?: orLeft().left().bind()
}

// RestrictedEitherEffect cannot implement EitherEffect
// Since EitherEffect defines a suspending capabilities
@RestrictsSuspension
public interface RestrictedEitherEffect<E, A> : EitherEffect<E, A> {
  @Deprecated(
    "Cont cannot be bound by restricted suspension",
    level = DeprecationLevel.ERROR
  )
  override suspend fun <B> Cont<E, B>.bind(): B = TODO()
}

@Suppress("ClassName")
public object either {
  public inline fun <E, A> eager(crossinline c: suspend RestrictedEitherEffect<E, *>.() -> A): Either<E, A> =
    Effect.restricted(eff = { delimitedScope ->
      object : RestrictedEitherEffect<E, A>, ContEffect<E> {
        override suspend fun <B> shift(r: E): B =
          delimitedScope.shift(r.left())
      }
    }, f = c, just = { it.right() })

  public suspend inline operator fun <E, A> invoke(crossinline c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
    cont<E, A> {
      c(object : EitherEffect<E, A>, ContEffect<E> by this {})
    }.toEither()
}
