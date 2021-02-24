package arrow.typeclasses.suspended

import arrow.Kind

/**
 * All possible approaches to running [Kind] in the context of [Fx]
 *
 * ```
 * fx {
 *   val one = just(1).bind() // using bind (deprecated)
 *   val (two) = just(one + 1) // using destructuring (deprecated)
 *   val three = !just(two + 1) // yelling at it (deprecated)
 *   val four = just(three + 1)() // using invoke
 * }
 * ```
 */
@Deprecated("Higher Kinded Types are deprecated, and so is polymorphic binding. Check arrow.core.computations for computations blocks for nullable, either, eval and const.")
interface BindSyntax<F>{

  suspend fun <A> Kind<F, A>.bind(): A

  @Deprecated("This operator is being deprecated due to confusion with Boolean, and unifying a single API. Use bind() instead.", ReplaceWith("bind()"))
  suspend operator fun <A> Kind<F, A>.not(): A =
    bind()

  @Deprecated("This operator can have problems when you do not capture the value, please use bind() instead", ReplaceWith("bind()"))
  suspend operator fun <A> Kind<F, A>.component1(): A =
    bind()
}
