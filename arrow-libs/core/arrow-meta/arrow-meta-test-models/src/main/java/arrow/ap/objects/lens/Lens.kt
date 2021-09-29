package arrow.ap.objects.lens

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.LENS])
public data class Lens(val field: String, val nullable: String?, val option: Option<String>) {
  public companion object
}
