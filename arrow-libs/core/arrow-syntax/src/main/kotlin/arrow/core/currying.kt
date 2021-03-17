package arrow.core

fun <P1, P2, R> ((P1, P2) -> R).curried(): (P1) -> (P2) -> R =
  { p1: P1 -> { p2: P2 -> this(p1, p2) } }

fun <P1, P2, P3, R> ((P1, P2, P3) -> R).curried(): (P1) -> (P2) -> (P3) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> this(p1, p2, p3) } } }

fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> this(p1, p2, p3, p4) } } } }

fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> this(p1, p2, p3, p4, p5) } } } } }

fun <P1, P2, R> ((P1) -> (P2) -> R).uncurried(): (P1, P2) -> R = { p1: P1, p2: P2 -> this(p1)(p2) }

fun <P1, P2, P3, R> ((P1) -> (P2) -> (P3) -> R).uncurried(): (P1, P2, P3) -> R =
  { p1: P1, p2: P2, p3: P3 -> this(p1)(p2)(p3) }

fun <P1, P2, P3, P4, R> ((P1) -> (P2) -> (P3) -> (P4) -> R).uncurried(): (P1, P2, P3, P4) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4 -> this(p1)(p2)(p3)(p4) }

fun <P1, P2, P3, P4, P5, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> R).uncurried(): (P1, P2, P3, P4, P5) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1)(p2)(p3)(p4)(p5) }

@JvmName("curriedEffect")
fun <P1, P2, R> (suspend (P1, P2) -> R).curried(): (P1) -> suspend (P2) -> R = { p1: P1 -> { p2: P2 -> this(p1, p2) } }

@JvmName("curriedEffect")
fun <P1, P2, P3, R> (suspend (P1, P2, P3) -> R).curried(): (P1) -> (P2) -> suspend (P3) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> this(p1, p2, p3) } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, R> (suspend (P1, P2, P3, P4) -> R).curried(): (P1) -> (P2) -> (P3) -> suspend (P4) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> this(p1, p2, p3, p4) } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, R> (suspend (P1, P2, P3, P4, P5) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> suspend (P5) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> this(p1, p2, p3, p4, p5) } } } } }

@JvmName("uncurriedEffect")
fun <P1, P2, R> ((P1) -> suspend (P2) -> R).uncurried(): suspend (P1, P2) -> R = { p1: P1, p2: P2 -> this(p1)(p2) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, R> ((P1) -> (P2) -> suspend (P3) -> R).uncurried(): suspend (P1, P2, P3) -> R =
  { p1: P1, p2: P2, p3: P3 -> this(p1)(p2)(p3) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, R> ((P1) -> (P2) -> (P3) -> suspend (P4) -> R).uncurried(): suspend (P1, P2, P3, P4) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4 -> this(p1)(p2)(p3)(p4) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, R> ((P1) -> (P2) -> (P3) -> (P4) -> suspend (P5) -> R).uncurried(): suspend (P1, P2, P3, P4, P5) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1)(p2)(p3)(p4)(p5) }
