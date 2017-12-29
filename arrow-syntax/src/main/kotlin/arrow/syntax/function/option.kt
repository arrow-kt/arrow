package arrow.syntax.function

import arrow.*

fun <P1, R> ((P1) -> R).optionLift(): (Option<P1>) -> Option<R> = { it.map(this) }