package arrow.ap.objects

import arrow.core.Option
import arrow.optionals

@optionals
data class Optional(val field: String, val nullable: String?, val option: Option<String>)