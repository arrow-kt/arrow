package arrow.ap.objects.optional

import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.OPTIONAL])
public data class OptionalSecondaryConstructor(val field: String?) {
  public constructor(number: Int?) : this(number?.toString())
  public companion object
}
