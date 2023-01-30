package arrow.core.computations

import arrow.core.Either
import arrow.core.Validated
import arrow.core.identity

/**
 * DSL Receiver Syntax for [result].
 */
@Deprecated(
  "ResultEffect is replaced with arrow.core.raise.ResultRaise",
  ReplaceWith("ResultRaise", "arrow.core.raise.ResultRaise")
)
public object ResultEffect {

  @Deprecated("This object introduces dangerous behavior.")
  public fun <A> Result<A>.bind(): A =
    getOrThrow()
  
  @Deprecated("This object introduces dangerous behavior.")
  public fun <A> Either<Throwable, A>.bind(): A =
    fold({ throw it }, ::identity)
  
  @Deprecated("This object introduces dangerous behavior.")
  public fun <A> Validated<Throwable, A>.bind(): A =
    fold({ throw it }, ::identity)
}

@Deprecated(resultDSLDeprecation, ReplaceWith("result", "arrow.core.raise.result"))
@Suppress("ClassName")
public object result {

  /**
   * Provides a computation block for [Result] which is build on top of Kotlin's Result Std operations.
   *
   * ```kotlin
   * import arrow.core.*
   * import arrow.core.computations.result
   *
   * fun main() {
   *   result { // We can safely use assertion based operation inside blocks
   *     kotlin.require(false) { "Boom" }
   *   } // Result.Failure<Int>(IllegalArgumentException("Boom"))
   *
   *   result {
   *     Result.failure<Int>(RuntimeException("Boom"))
   *       .recover { 1 }
   *       .bind()
   *   } // Result.Success(1)
   *
   *   result {
   *     val x = Result.success(1).bind()
   *     val y = Result.success(x + 1).bind()
   *     x + y
   *   } // Result.Success(3)
   * }
   * ```
   * <!--- KNIT example-result-computations-01.kt -->
   */
  @Deprecated(
    resultDSLDeprecation,
    ReplaceWith("result { block() }", "arrow.core.raise.result")
  )
  public inline operator fun <A> invoke(block: ResultEffect.() -> A): Result<A> =
    kotlin.runCatching { block(ResultEffect) }
}

private const val resultDSLDeprecation =
  "The result DSL has been moved to arrow.core.raise.result.\n" +
    "Replace import arrow.core.computations.* with arrow.core.raise.*"
