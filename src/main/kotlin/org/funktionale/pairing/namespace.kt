package org.funktionale.pairing

fun<P1, P2, R> Function2<P1, P2, R>.paired(): (Pair<P1, P2>) -> R {
    return { pair: Pair<P1, P2> -> this(pair.component1(), pair.component2()) }
}

fun<P1, P2, R> Function1<Pair<P1, P2>, R>.unpaired(): (P1, P2) -> R {
    return { p1: P1, p2: P2 -> this(p1 to p2) }
}
