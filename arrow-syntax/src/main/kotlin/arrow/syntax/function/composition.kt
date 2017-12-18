package arrow.syntax.function

infix fun <P1, IP, R> ((P1) -> IP).andThen(f: (IP) -> R): (P1) -> R = forwardCompose(f)

infix fun <IP, R> (() -> IP).andThen(f: (IP) -> R): () -> R = forwardCompose(f)

infix fun <P1, IP, R> ((P1) -> IP).forwardCompose(f: (IP) -> R): (P1) -> R = { p1: P1 -> f(this(p1)) }

infix fun <IP, R> (() -> IP).forwardCompose(f: (IP) -> R): () -> R = { f(this()) }

infix fun <IP, R, P1> ((IP) -> R).compose(f: (P1) -> IP): (P1) -> R = { p1: P1 -> this(f(p1)) }
