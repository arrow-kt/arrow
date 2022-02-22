package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.None
import arrow.core.Option
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

@Deprecated("NullableEffect is being replaced by arrow.core.continuations.OptionEffectScope")
public fun interface NullableEffect<A> : Effect<A?> {
  @Deprecated(deprecateInFavorOfOptionEffectScope)
  public suspend fun <B> B?.bind(): B =
    this ?: control().shift(null)

  @Deprecated(deprecateInFavorOfOptionEffectScope)
  public suspend fun <B> Option<B>.bind(): B =
    orNull().bind()

  /**
   * Ensure check if the [value] is `true`,
   * and if it is it allows the `nullable { }` binding to continue.
   * In case it is `false`, then it short-circuits the binding and returns `null`.
   *
   * ```kotlin
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
  @Deprecated(deprecateInFavorOfOptionEffectScope)
  public suspend fun ensure(value: Boolean): Unit =
    if (value) Unit else control().shift(null)
}

/**
 * Ensures that [value] is not null.
 * When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null.
 * Otherwise, if the [value] is null then the [option] binding will short-circuit with [None].
 *
 * ```kotlin
 * import arrow.core.computations.nullable
 * import arrow.core.computations.ensureNotNull
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
@Deprecated(deprecateInFavorOfOptionEffectScope)
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> NullableEffect<*>.ensureNotNull(value: B?): B {
  contract {
    returns() implies (value != null)
  }

  return value ?: control().shift(null)
}

@Deprecated(deprecateInFavorOfOptionEagerEffectScope)
@RestrictsSuspension
public fun interface RestrictedNullableEffect<A> : NullableEffect<A>

@Deprecated(deprecateInFavorOfEffectOrEagerEffect, ReplaceWith("option", "arrow.core.continuations.option"))
@Suppress("ClassName")
public object nullable {
  @Deprecated(deprecateInFavorOfEagerEffect, ReplaceWith("option.eager(func)", "arrow.core.continuations.option"))
  public inline fun <A> eager(crossinline func: suspend RestrictedNullableEffect<A>.() -> A?): A? =
    Effect.restricted(eff = { RestrictedNullableEffect { it } }, f = func, just = { it })

  @Deprecated(deprecateInFavorOfEffect, ReplaceWith("option(func)", "arrow.core.continuations.option"))
  public suspend inline operator fun <A> invoke(crossinline func: suspend NullableEffect<*>.() -> A?): A? =
    Effect.suspended(eff = { NullableEffect { it } }, f = func, just = { it })
}

internal const val deprecateInFavorOfOptionEffectScope: String =
  "Is being replaced by arrow.core.continuations.OptionEffectScope"

internal const val deprecateInFavorOfOptionEagerEffectScope: String =
  "Is being replaced by arrow.core.continuations.OptionEagerEffectScope"
