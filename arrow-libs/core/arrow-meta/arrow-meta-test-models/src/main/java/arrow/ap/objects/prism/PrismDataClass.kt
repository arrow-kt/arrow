package arrow.ap.objects.prism

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.PRISM])
public data class PrismDataClass(
  public val field: String,
  public val nullable: String?,
  public val option: Option<String>
) {
  public companion object
}
