package arrow.optics

import kotlinx.library.watchdog.ExemptionReason
import kotlinx.library.watchdog.IntentionallyMutableCollection
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Instructs the Arrow Optics compiler plugin to generate optics for the annotated class.
 *
 * @param [targets] Empty arrays means "Everything that matches annotated class"
 */
@Retention(SOURCE)
@Target(CLASS)
public annotation class optics(@IntentionallyMutableCollection(reason = ExemptionReason.API_DESIGN) val targets: Array<OpticsTarget> = emptyArray()) {
  /**
   * Instructs the Arrow Optics compiler plugin to generate an optics-based `copy`.
   */
  @Retention(SOURCE)
  @Target(CLASS)
  public annotation class copy()
}

/**
 * Defines the kinds of optics that the Arrow Optics compiler plugin should generate.
 */
public enum class OpticsTarget {
  ISO, LENS, PRISM, OPTIONAL, DSL
}

/**
 * Declares that the optics is potentially unsafe optic because of a covariant type argument.
 * Getting it always fine, but setting may not respect the type bounds.
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
  "Potentially unsafe optic because of a covariant type argument. " +
    "Getting it always fine, but setting may not respect the type bounds.",
  RequiresOptIn.Level.ERROR
)
public annotation class PotentiallyUnsafeCovariantOptic
