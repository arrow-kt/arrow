package arrow.optics

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

@Retention(SOURCE)
@Target(CLASS)
/**
 * ```kotlin
 * val x = 1
 * ```
 * <!--- KNIT example-arrow-annotations-01.kt -->
 *
 * Empty arrays means "Everything that matches annotated class"
 */
public annotation class optics(val targets: Array<OpticsTarget> = emptyArray())

public enum class OpticsTarget {
  ISO, LENS, PRISM, OPTIONAL, DSL
}
