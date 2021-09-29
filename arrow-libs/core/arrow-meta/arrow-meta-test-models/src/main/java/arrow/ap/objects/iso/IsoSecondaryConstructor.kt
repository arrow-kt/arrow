package arrow.ap.objects.iso

import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.ISO])
public data class IsoSecondaryConstructor(val fieldNumber: Int, val fieldString: String) {
  public constructor(number: Int) : this(number, number.toString())
  public companion object
}
