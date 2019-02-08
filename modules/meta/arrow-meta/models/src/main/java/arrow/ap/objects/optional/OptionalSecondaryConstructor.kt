package arrow.ap.objects.optional

import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.OPTIONAL])
data class OptionalSecondaryConstructor(val field: String?) {
  constructor(number: Int?): this(number?.toString())
  companion object
}