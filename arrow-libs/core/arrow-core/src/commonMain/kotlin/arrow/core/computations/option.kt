package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.None
import arrow.core.Option
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

@Deprecated(
  "OptionEffect<A> is replaced with arrow.core.raise.OptionRaise",
  ReplaceWith("OptionRaise", "arrow.core.raise.OptionRaise")
)
public fun interface OptionEffect<A> : Effect<Option<A>> {
  public suspend fun <B> Option<B>.bind(): B =
    fold({ control().shift(None) }, ::identity)

  /**
   * Ensure check if the [value] is `true`,
   * and if it is it allows the `option { }` binding to continue.
   * In case it is `false`, then it short-circuits the binding and returns [None].
   *
   * ```kotlin
   * import arrow.core.computations.option
   *
   * //sampleStart
   * suspend fun main() {
   *   option<Int> {
   *     ensure(true)
   *     println("ensure(true) passes")
   *     ensure(false)
   *     1
   *   }
   * //sampleEnd
   *   .let(::println)
   * }
   * // println: "ensure(true) passes"
   * // res: None
   * ```
   * <!--- KNIT example-option-computations-01.kt -->
   */
  public suspend fun ensure(value: Boolean): Unit =
    if (value) Unit else control().shift(None)
}

/**
 * Ensures that [value] is not null.
 * When the value is not null, then it will be returned as non null and the check value is now smart-checked to non-null.
 * Otherwise, if the [value] is null then the [option] binding will short-circuit with [None].
 *
 * ```kotlin
 * import arrow.core.computations.option
 * import arrow.core.computations.ensureNotNull
 *
 * //sampleStart
 * suspend fun main() {
 *   option<Int> {
 *     val x: Int? = 1
 *     ensureNotNull(x)
 *     println(x)
 *     ensureNotNull(null)
 *   }
 * //sampleEnd
 *   .let(::println)
 * }
 * // println: "1"
 * // res: None
 * ```
 * <!--- KNIT example-option-computations-02.kt -->
 */
@Deprecated(
  "Replaced by Raise, replace arrow.core.computations.ensureNotNull to arrow.core.raise.ensureNotNull",
  ReplaceWith(
    "ensureNotNull(value)",
    "import arrow.core.raise.ensureNotNull"
  )
)
@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> OptionEffect<*>.ensureNotNull(value: B?): B {
  contract {
    returns() implies (value != null)
  }

  return value ?: (this as OptionEffect<Any?>).control().shift(None)
}

@Deprecated(
  "RestrictedEitherEffect is replaced with arrow.core.raise.OptionRaise",
  ReplaceWith("OptionRaise", "arrow.core.raise.OptionRaise")
)@RestrictsSuspension
public fun interface RestrictedOptionEffect<A> : OptionEffect<A>

@Deprecated(
  optionDSLDeprecation,
  ReplaceWith("option", "arrow.core.raise.option")
)
@Suppress("ClassName")
public object option {
  @Deprecated(
    optionDSLDeprecation,
    ReplaceWith("option(func)", "arrow.core.raise.option")
  )
  public inline fun <A> eager(crossinline func: suspend RestrictedOptionEffect<A>.() -> A): Option<A> =
    Effect.restricted(eff = { RestrictedOptionEffect { it } }, f = func, just = { Option.fromNullable(it) })
  
  @Deprecated(
    optionDSLDeprecation,
    ReplaceWith("option(func)", "arrow.core.raise.option")
  )  public suspend inline operator fun <A> invoke(crossinline func: suspend OptionEffect<*>.() -> A?): Option<A> =
    Effect.suspended(eff = { OptionEffect { it } }, f = func, just = { Option.fromNullable(it) })
}

private const val optionDSLDeprecation =
  "The option DSL has been moved to arrow.core.raise.option.\n" +
    "Replace import arrow.core.computations.* with arrow.core.raise.*"
