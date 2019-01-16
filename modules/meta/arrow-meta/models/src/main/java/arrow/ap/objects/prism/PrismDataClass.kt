package arrow.ap.objects.prism

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.PRISM])
data class PrismDataClass(val field: String, val nullable: String?, val option: Option<String>) {
  companion object
}
