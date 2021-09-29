package arrow.ap.objects.lens

import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.LENS])
public data class LensSecondaryConstructor(val field: String) {
  public constructor(number: Int) : this(number.toString())
  public companion object
}
