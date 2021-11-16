package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.None
import arrow.core.Option
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

public fun interface NullableEffect<A> : Effect<A?> {
  public suspend fun <B> B?.bind(): B =
    this ?: control().shift(null)

  public suspend fun <B> Option<B>.bind(): B =
    orNull().bind()

  /**
   * Ensure check if the [value] is `true`,
   * and if it is it allows the `nullable { }` binding to continue.
   * In case it is `false`, then it short-circuits the binding and returns `null`.
   *
   * ```kotlin:ank
   * import arrow.core.computations.nullable
   *
   * //sampleStart
   * suspend fun main() {
   *   nullable<Int> {
   *     ensure(true)
   *     println("ensure(true) passes")
   *     ensure(false)
   *     1
   *   }
   * //sampleEnd
   *   .let(::println)
   * }
   * // println: "ensure(true) passes"
   * // res: null
   * ```
 * <!--- KNIT example-nullable-computations-01.kt -->
   */
  public suspend fun ensure(value: Boolean): Unit =
    if (value) Unit else control().shift(null)
}

/**
 * Ensures that [value] is not null.
 * When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null.
 * Otherwise, if the [value] is null then the [option] binding will short-circuit with [None].
 *
 * ```kotlin:ank
 * import arrow.core.computations.nullable
 *
 * //sampleStart
 * suspend fun main() {
 *   nullable<Int> {
 *     val x: Int? = 1
 *     ensureNotNull(x)
 *     println(x)
 *     ensureNotNull(null)
 *   }
 * //sampleEnd
 *   .let(::println)
 * }
 * // println: "1"
 * // res: null
 * ```
 * <!--- KNIT example-nullable-computations-02.kt -->
 */
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> NullableEffect<*>.ensureNotNull(value: B?): B {
  contract {
    returns() implies (value != null)
  }

  return value ?: control().shift(null)
}

@RestrictsSuspension
public fun interface RestrictedNullableEffect<A> : NullableEffect<A>

@Suppress("ClassName")
public object nullable {
  public inline fun <A> eager(crossinline func: suspend RestrictedNullableEffect<A>.() -> A?): A? =
    Effect.restricted(eff = { RestrictedNullableEffect { it } }, f = func, just = { it })

  public suspend inline operator fun <A> invoke(crossinline func: suspend NullableEffect<*>.() -> A?): A? =
    Effect.suspended(eff = { NullableEffect { it } }, f = func, just = { it })
}
