package arrow.ap.objects.optional

import arrow.core.None
import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.OPTIONAL])
public sealed class OptionalSealed(
  public val field: String,
  public val nullable: String?,
  public val option: Option<String>
) {
  public data class Optional2(val a: String?) : OptionalSealed("", null, None)
  public companion object
}
