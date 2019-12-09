package arrow.ap.objects.lens

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.LENS])
sealed class LensSealed(val field: String, val nullable: String?, val option: Option<String>) {
  data class Lens2(val a: String?) : LensSealed("", null, Option.empty())
  companion object
}
