package arrow.ap.objects.lens

import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.LENS])
data class LensSecondaryConstructor(val field: String) {
  constructor(number: Int) : this(number.toString())
  companion object
}
