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
interface BindSyntax<F> : Invoke<F> {

  @Deprecated("This operator can have problems when you do not capture the value, please use () or invoke() instead", ReplaceWith("invoke()"))
  suspend fun <A> Kind<F, A>.bind(): A =
    invoke()

  @Deprecated("This operator can have problems when you do not capture the value, please use () or invoke() instead", ReplaceWith("invoke()"))
  suspend operator fun <A> Kind<F, A>.not(): A =
    invoke()

  // TODO remove it completely
  @Deprecated("This operator can have problems when you do not capture the value, please use () or invoke() instead", ReplaceWith("invoke()"))
  suspend operator fun <A> Kind<F, A>.component1(): A =
    invoke()
}

// TODO: make it fun interface when suspend fun is allowed inside
interface Invoke<F> {
  suspend operator fun <A> Kind<F, A>.invoke(): A
}
