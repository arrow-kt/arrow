package org.funktionale.pipe

infix inline fun <P1, R> P1.pipe(t: (P1) -> R): R = t(this)
infix inline fun <P1, P2, P3> P1.pipe2(crossinline t: (P1, P2) -> P3): (P2) -> P3 = { p2 -> t(this, p2) }
infix inline fun <P1, P2, P3, P4> P1.pipe3(crossinline t: (P1, P2, P3) -> P4): (P2, P3) -> P4 = { p2, p3 -> t(this, p2, p3) }
