package arrow.ap.objects.generic

import arrow.DerivingTarget
import arrow.core.Option
import arrow.product

@product([DerivingTarget.EQ])
data class Eq(val field: String, val option: Option<String>) {
  companion object
}
