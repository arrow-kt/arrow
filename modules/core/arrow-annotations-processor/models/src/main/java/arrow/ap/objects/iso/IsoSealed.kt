package arrow.ap.objects.iso

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.ISO])
sealed class IsoSealed(val field: String, val nullable: String?, val option: Option<String>) {
  companion object {}
  data class IsoSealed2(val a: String?) : IsoSealed("", null, Option.empty())
  companion object
}