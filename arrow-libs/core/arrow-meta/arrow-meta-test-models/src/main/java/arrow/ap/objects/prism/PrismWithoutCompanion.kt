package arrow.ap.objects.prism

import arrow.core.None
import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.PRISM])
public sealed class PrismWithoutCompanion(
  public val field: String,
  public val nullable: String?,
  public val option: Option<String>
) {
  public data class PrismSealed1(private val a: String?) : PrismWithoutCompanion("", a, None)
  public data class PrismSealed2(private val b: String?) : PrismWithoutCompanion("", b, None)
}
