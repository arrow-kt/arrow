package arrow.optics

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

@Retention(SOURCE)
@Target(CLASS)
/**
 * Empty arrays means "Everything that matches annotated class"
 */
annotation class optics(val targets: Array<OpticsTarget> = [])

enum class OpticsTarget {
  ISO, LENS, PRISM, OPTIONAL, DSL
}
