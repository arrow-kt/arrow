package arrow.optics

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Empty arrays means "Everything that matches annotated class"
 */
@Retention(SOURCE)
@Target(CLASS)
public annotation class optics(val targets: Array<OpticsTarget> = emptyArray()) {
  @Retention(SOURCE)
  @Target(CLASS)
  public annotation class copy()
}

public enum class OpticsTarget {
  ISO, LENS, PRISM, OPTIONAL, DSL
}

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
  "Potentially unsafe optic because of a covariant type argument. " +
    "Getting it always fine, but setting may not respect the type bounds.",
  RequiresOptIn.Level.ERROR
)
public annotation class PotentiallyUnsafeCovariantOptic
