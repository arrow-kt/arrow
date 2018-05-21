package arrow.ap.objects.prism

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.PRISM])
sealed class PrismWithoutCompanion(val field: String, val nullable: String?, val option: Option<String>) {
  data class PrismSealed1(private val a: String?) : PrismWithoutCompanion("", a, Option.empty())
  data class PrismSealed2(private val b: String?) : PrismWithoutCompanion("", b, Option.empty())
}
