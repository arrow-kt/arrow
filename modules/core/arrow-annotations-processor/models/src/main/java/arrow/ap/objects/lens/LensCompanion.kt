package arrow.ap.objects.lens

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.LENS])
data class LensCompanion(val field: String, val nullable: String?, val option: Option<String>)