package arrow.ap.objects.optional

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.OPTIONAL])
public data class Optional(val field: String, val nullable: String?, val option: Option<String>) {
  public companion object
}
