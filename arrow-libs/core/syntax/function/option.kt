package arrow.syntax.function

import arrow.core.Option

fun <P1, R> ((P1) -> R).optionLift(): (Option<P1>) -> Option<R> = { it.map(this) }
