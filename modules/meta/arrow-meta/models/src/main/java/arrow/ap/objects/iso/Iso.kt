package arrow.ap.objects.iso

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.ISO])
data class Iso(val field: String, val nullable: String?, val option: Option<String>) {
  companion object
}
