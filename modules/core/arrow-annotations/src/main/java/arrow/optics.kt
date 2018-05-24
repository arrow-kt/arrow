package arrow.optics

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

/**
 * Annotation for generating Optics for a `data class`, `sealed class` or top-level functions.
 * @param targets takes an array of [OpticsTarget] or no argument if all applicable targets should be generated.
 */
@Retention(SOURCE)
@Target(CLASS, FUNCTION)
annotation class optics(val targets: Array<OpticsTarget> = [])

/**
 * [optics] argument. To be generated target.
 *   - [ISO] applicable to `data class A` and generates an [arrow.optics.Iso] to represent `A` in its generic form `TupleN`.
 *   - [LENS] applicable to `data class A` and generates [arrow.optics.Lens] for every constructor property of `A`.
 *   - [PRISM] applicable to `sealed class A` and generates [arrow.optics.Prism] for every subtype of `A`.
 *   - [OPTIONAL] applicable to `data class A` and generates [arrow.optics.Optional] for every nullable or [arrow.core.Option] constructor property of `A`.
 *   - [DSL] applicable to `data class A` and top-level functions to generate Optics DSL support.
 */
enum class OpticsTarget {
  ISO,
  LENS,
  PRISM,
  OPTIONAL,
  DSL
}