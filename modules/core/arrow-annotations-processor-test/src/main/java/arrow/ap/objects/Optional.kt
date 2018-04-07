package arrow.ap.objects

import arrow.OpticsTarget
import arrow.core.Option
import arrow.optic

@optic([(OpticsTarget.OPTIONAL)])
data class Optional(val field: String, val nullable: String?, val option: Option<String>)