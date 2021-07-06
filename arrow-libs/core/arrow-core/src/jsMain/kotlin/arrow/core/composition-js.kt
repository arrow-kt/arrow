package arrow.core

actual infix fun <P1, P2, IP, R> ((P1, P2) -> IP).andThen(f: (IP) -> R): (P1, P2) -> R =
  { p1, p2 -> f(this(p1, p2)) }

actual infix fun <IP, R> (() -> IP).andThen(f: (IP) -> R): () -> R =
  { f(this()) }

actual infix fun <P1, IP, R> ((P1) -> IP).andThen(f: (IP) -> R): (P1) -> R =
  { p1 -> f(this(p1)) }

actual infix fun <IP, R, P1> ((IP) -> R).compose(f: (P1) -> IP): (P1) -> R =
  { p1 -> this(f(p1)) }
