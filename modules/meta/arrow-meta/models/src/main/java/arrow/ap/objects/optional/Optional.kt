package arrow.ap.objects.optional

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.OPTIONAL])
data class Optional(val field: String, val nullable: String?, val option: Option<String>) {
  companion object
}
