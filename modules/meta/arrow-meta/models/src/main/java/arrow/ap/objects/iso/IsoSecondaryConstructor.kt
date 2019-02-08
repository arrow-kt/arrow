package arrow.ap.objects.iso

import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.ISO])
data class IsoSecondaryConstructor(val fieldNumber: Int, val fieldString: String) {
  constructor(number: Int): this(number, number.toString())
  companion object
}