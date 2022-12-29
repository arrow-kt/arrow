package arrow.core.computations

import arrow.core.Either
import arrow.core.identity

/**
 * DSL Receiver Syntax for [result].
 */
@Deprecated("$deprecatedInFavorOfEagerEffectScope\nThis object introduces dangerous behavior and will be removed in the next version: https://github.com/arrow-kt/arrow/issues/2547")
public object ResultEffect {

  @Deprecated("$deprecatedInFavorOfEagerEffectScope\nThis object introduces dangerous behavior and will be removed in the next version: https://github.com/arrow-kt/arrow/issues/2547")
  public fun <A> Result<A>.bind(): A =
    getOrThrow()

  @Deprecated("$deprecatedInFavorOfEagerEffectScope\nThis object introduces dangerous behavior and will be removed in the next version: https://github.com/arrow-kt/arrow/issues/2547")
  public fun <A> Either<Throwable, A>.bind(): A =
    fold({ throw it }, ::identity)

  @Deprecated(deprecateInFavorOfEffectOrEagerEffect, ReplaceWith("result", "arrow.core.continuations.result"))
  @Suppress("ClassName")
  public object result {

    /**
     * Provides a computation block for [Result] which is build on top of Kotlin's Result Std operations.
     *
     * ```kotlin
     * import arrow.core.*
     * import arrow.core.computations.ResultEffect.result
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
    @Deprecated(deprecateInFavorOfEffect, ReplaceWith("result.eager(block)", "arrow.core.continuations.result"))
    public inline operator fun <A> invoke(block: ResultEffect.() -> A): Result<A> =
      kotlin.runCatching { block(ResultEffect) }
  }
}
