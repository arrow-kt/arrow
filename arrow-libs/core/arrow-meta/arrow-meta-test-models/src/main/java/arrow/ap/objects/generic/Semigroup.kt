package arrow.ap.objects.generic

import arrow.DerivingTarget
import arrow.core.Option
import arrow.product

@product([DerivingTarget.SEMIGROUP])
data class Semigroup(val field: String, val option: Option<String>) {
  companion object
}
