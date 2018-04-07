package arrow.ap.objects

import arrow.OpticsTarget
import arrow.core.Option
import arrow.optic

@optic([OpticsTarget.OPTIONAL])
sealed class OptionalSealed(val field: String, val nullable: String?, val option: Option<String>) {
  data class Optional2(val a: String?) : OptionalSealed("", null, Option.empty())
}