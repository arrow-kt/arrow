package com.jereksel.test

import arrow.core.Option
import arrow.lenses
import arrow.optionals

@optionals
@lenses data class OptionalTest(val name: String, val company: Int, val optionalName: String?, val optionalCompany: Option<String>)

