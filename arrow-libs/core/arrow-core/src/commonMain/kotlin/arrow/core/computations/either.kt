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

  /**
   * Ensure check if the [value] is `true`,
   * and if it is it allows the `either { }` binding to continue.
   * In case it is `false`, then it short-circuits the binding and returns
   * the provided value by [orLeft] inside an [Either.Left].
   *
   * ```kotlin:ank:playground
   * import arrow.core.computations.either
   *
   * //sampleStart
   * suspend fun main() {
   *   either<String, Int> {
   *     ensure(true) { "" }
   *     println("ensure(true) passes")
   *     ensure(false) { "failed" }
   *     1
   *   }
   * //sampleEnd
   *   .let(::println)
   * }
   * // println: "ensure(true) passes"
   * // res: Either.Left("failed")
   * ```
   */
  public suspend fun ensure(value: Boolean, orLeft: () -> E): Unit =
    if (value) Unit else orLeft().left().bind()
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
