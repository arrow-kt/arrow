package org.funktionale.pairing

fun <P1, P2, R> Function2<P1, P2, R>.paired(): (Pair<P1, P2>) -> R = { pair: Pair<P1, P2> ->
    this(pair.component1(), pair.component2())
}

fun <P1, P2, R> Function1<Pair<P1, P2>, R>.unpaired(): (P1, P2) -> R = { p1: P1, p2: P2 ->
    this(p1 to p2)
}

fun <P1, P2, P3, R> Function3<P1, P2, P3, R>.tripled(): (Triple<P1, P2, P3>) -> R = { triple: Triple<P1, P2, P3> ->
    this(triple.component1(), triple.component2(), triple.component3())
}

fun <P1, P2, P3, R> Function1<Triple<P1, P2, P3>, R>.untripled(): (P1, P2, P3) -> R = { p1: P1, p2: P2, p3: P3 ->
    this(Triple(p1, p2, p3))
}
