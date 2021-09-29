package arrow.ap.objects.iso

import arrow.core.None
import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.ISO])
public sealed class IsoSealed(
  public val field: String,
  public val nullable: String?,
  public val option: Option<String>
) {
  public data class IsoSealed2(val a: String?) : IsoSealed("", null, None)
  public companion object
}
