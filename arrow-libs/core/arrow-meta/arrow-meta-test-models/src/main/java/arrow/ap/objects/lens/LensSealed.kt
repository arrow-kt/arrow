package arrow.ap.objects.lens

import arrow.core.None
import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.LENS])
public sealed class LensSealed(
  public val field: String,
  public val nullable: String?,
  public val option: Option<String>
) {
  public data class Lens2(val a: String?) : LensSealed("", null, None)
  public companion object
}
