package arrow.core.computations.suspended

/**
 * Running A? in the context of [nullable]
 *
 * ```
 * nullable {
 *   val one = 1.invoke() // using invoke
 *   val bigger = (one.takeIf{ it > 1 }).invoke() // using invoke on expression
 *   bigger
 * }
 * ```
 */
// TODO: this will become interface fun when they support suspend in the next release
interface BindSyntax {
  suspend operator fun <A> A?.invoke(): A
}
