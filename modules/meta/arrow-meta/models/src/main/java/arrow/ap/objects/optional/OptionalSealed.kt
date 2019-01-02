package arrow.ap.objects.optional

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.OPTIONAL])
sealed class OptionalSealed(val field: String, val nullable: String?, val option: Option<String>) {
  data class Optional2(val a: String?) : OptionalSealed("", null, Option.empty())
  companion object
}