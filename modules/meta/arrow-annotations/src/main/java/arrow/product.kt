package arrow

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

@Retention(SOURCE)
@Target(CLASS)
/**
 * Empty arrays means "Everything that matches annotated class"
 */
annotation class product(val deriving: Array<DerivingTarget> = [])

enum class DerivingTarget {
  SEMIGROUP, MONOID, TUPLED, HLIST, APPLICATIVE, EQ, SHOW
}
