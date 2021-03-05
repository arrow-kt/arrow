package arrow.syntax.function

import arrow.core.Option

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("Option.lift(this)", "arrow.core.Option")
)
fun <P1, R> ((P1) -> R).optionLift(): (Option<P1>) -> Option<R> =
  Option.lift(this)
