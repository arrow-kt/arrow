package arrow.core.computations

import arrow.core.Either
import arrow.core.Validated
import arrow.core.identity

/**
 * DSL Receiver Syntax for [result].
 */
public object ResultEffect {

  public fun <A> Result<A>.bind(): A =
    getOrThrow()

  public fun <A> Either<Throwable, A>.bind(): A =
    fold({ throw it }, ::identity)

  public fun <A> Validated<Throwable, A>.bind(): A =
    fold({ throw it }, ::identity)
}

@Suppress("ClassName")
public object result {

  /**
   * Provides a computation block for [Result] which is build on top of Kotlin's Result Std operations.
   *
   * ```kotlin:ank
   * import arrow.core.*
   *
   * fun main() {
   *   result { // We can safely use assertion based operation inside blocks
   *     kotlin.require(false) { "Boom" }
   *   } // Result.Failure(IllegalArgumentException("Boom"))
   *
   *   result {
   *     Result.failure(RuntimeException("Boom"))
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
  public inline operator fun <A> invoke(block: ResultEffect.() -> A): Result<A> =
    kotlin.runCatching { block(ResultEffect) }
}
