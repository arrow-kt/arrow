package arrow.core

infix fun <P1, P2, IP, R> ((P1, P2) -> IP).andThen(f: (IP) -> R): (P1, P2) -> R =
  AndThen2(this).andThen(f)

infix fun <IP, R> (() -> IP).andThen(f: (IP) -> R): () -> R =
  AndThen0(this).andThen(f)

infix fun <P1, IP, R> ((P1) -> IP).andThen(f: (IP) -> R): (P1) -> R =
  AndThen1(this).andThen(f)

infix fun <IP, R, P1> ((IP) -> R).compose(f: (P1) -> IP): (P1) -> R =
  AndThen1(this).compose(f)
