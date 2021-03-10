package arrow.syntax.function

import arrow.core.andThen as _andThen
import arrow.core.compose as _compose

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("this andThen f", "arrow.core.andThen")
)
infix fun <P1, P2, IP, R> ((P1, P2) -> IP).andThen(f: (IP) -> R): (P1, P2) -> R =
 this _andThen f

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("this andThen f", "arrow.core.andThen")
)
infix fun <P1, IP, R> ((P1) -> IP).andThen(f: (IP) -> R): (P1) -> R =
  this _andThen f

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("this andThen f", "arrow.core.andThen")
)
infix fun <IP, R> (() -> IP).andThen(f: (IP) -> R): () -> R =
  this _andThen f

@Deprecated(
  "Use andThen instead of forwardCompose.",
  ReplaceWith("this andThen f", "arrow.core.andThen")
)
infix fun <P1, P2, IP, R> ((P1, P2) -> IP).forwardCompose(f: (IP) -> R) =
  this _andThen f

@Deprecated(
  "Use andThen instead of forwardCompose.",
  ReplaceWith("this andThen f", "arrow.core.andThen")
)
infix fun <P1, IP, R> ((P1) -> IP).forwardCompose(f: (IP) -> R): (P1) -> R =
  this _andThen f

@Deprecated(
  "Use andThen instead of forwardCompose.",
  ReplaceWith("this andThen f", "arrow.core.andThen")
)
infix fun <IP, R> (() -> IP).forwardCompose(f: (IP) -> R): () -> R =
  this _andThen f

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("this compose f", "arrow.core.compose")
)
infix fun <IP, R, P1> ((IP) -> R).compose(f: (P1) -> IP): (P1) -> R =
  this _compose f
