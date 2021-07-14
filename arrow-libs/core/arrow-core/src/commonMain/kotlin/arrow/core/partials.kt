@file:JvmName("Partials")
package arrow.core

import kotlin.jvm.JvmName

public fun <P1, R> ((P1) -> R).partially1(p1: P1): () -> R =
  { this(p1) }

public fun <P1, P2, R> ((P1, P2) -> R).partially1(p1: P1): (P2) -> R =
  { p2: P2 -> this(p1, p2) }

public fun <P1, P2, R> ((P1, P2) -> R).partially2(p2: P2): (P1) -> R =
  { p1: P1 -> this(p1, p2) }

public fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partially1(p1: P1): (P2, P3) -> R =
  { p2: P2, p3: P3 -> this(p1, p2, p3) }

public fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partially2(p2: P2): (P1, P3) -> R =
  { p1: P1, p3: P3 -> this(p1, p2, p3) }

public fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partially3(p3: P3): (P1, P2) -> R =
  { p1: P1, p2: P2 -> this(p1, p2, p3) }

public fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).partially1(p1: P1): (P2, P3, P4) -> R =
  { p2: P2, p3: P3, p4: P4 -> this(p1, p2, p3, p4) }

public fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).partially2(p2: P2): (P1, P3, P4) -> R =
  { p1: P1, p3: P3, p4: P4 -> this(p1, p2, p3, p4) }

public fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).partially3(p3: P3): (P1, P2, P4) -> R =
  { p1: P1, p2: P2, p4: P4 -> this(p1, p2, p3, p4) }

public fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).partially4(p4: P4): (P1, P2, P3) -> R =
  { p1: P1, p2: P2, p3: P3 -> this(p1, p2, p3, p4) }

public fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially1(p1: P1): (P2, P3, P4, P5) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

public fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially2(p2: P2): (P1, P3, P4, P5) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

public fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially3(p3: P3): (P1, P2, P4, P5) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

public fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially4(p4: P4): (P1, P2, P3, P5) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5 -> this(p1, p2, p3, p4, p5) }

public fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially5(p5: P5): (P1, P2, P3, P4) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4 -> this(p1, p2, p3, p4, p5) }

public fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

public fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

public fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

public fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

public fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

public fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5, p6) }

public fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

public fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

public fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

public fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

public fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

public fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

public fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6, p7) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially15(p15: P15): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially15(p15: P15): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially16(p16: P16): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially15(p15: P15): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially16(p16: P16): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially17(p17: P17): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially15(p15: P15): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially16(p16: P16): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially17(p17: P17): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially18(p18: P18): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially15(p15: P15): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially16(p16: P16): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially17(p17: P17): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially18(p18: P18): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially19(p19: P19): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially15(p15: P15): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially16(p16: P16): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially17(p17: P17): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially18(p18: P18): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially19(p19: P19): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially20(p20: P20): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially15(p15: P15): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially16(p16: P16): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially17(p17: P17): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially18(p18: P18): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially19(p19: P19): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially20(p20: P20): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially21(p21: P21): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially1(p1: P1): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially2(p2: P2): (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially3(p3: P3): (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially4(p4: P4): (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially5(p5: P5): (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially6(p6: P6): (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially7(p7: P7): (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially8(p8: P8): (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially9(p9: P9): (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially10(p10: P10): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially11(p11: P11): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially12(p12: P12): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially13(p13: P13): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially14(p14: P14): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially15(p15: P15): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially16(p16: P16): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially17(p17: P17): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially18(p18: P18): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially19(p19: P19): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially20(p20: P20): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially21(p21: P21): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially22(p22: P22): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially1Effect")
public fun <P1, R> (suspend (P1) -> R).partially1(p1: P1): suspend () -> R =
  { this(p1) }

@JvmName("partially1Effect")
public fun <P1, P2, R> (suspend (P1, P2) -> R).partially1(p1: P1): suspend (P2) -> R =
  { p2: P2 -> this(p1, p2) }

@JvmName("partially2Effect")
public fun <P1, P2, R> (suspend (P1, P2) -> R).partially2(p2: P2): suspend (P1) -> R =
  { p1: P1 -> this(p1, p2) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, R> (suspend (P1, P2, P3) -> R).partially1(p1: P1): suspend (P2, P3) -> R =
  { p2: P2, p3: P3 -> this(p1, p2, p3) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, R> (suspend (P1, P2, P3) -> R).partially2(p2: P2): suspend (P1, P3) -> R =
  { p1: P1, p3: P3 -> this(p1, p2, p3) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, R> (suspend (P1, P2, P3) -> R).partially3(p3: P3): suspend (P1, P2) -> R =
  { p1: P1, p2: P2 -> this(p1, p2, p3) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, R> (suspend (P1, P2, P3, P4) -> R).partially1(p1: P1): suspend (P2, P3, P4) -> R =
  { p2: P2, p3: P3, p4: P4 -> this(p1, p2, p3, p4) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, R> (suspend (P1, P2, P3, P4) -> R).partially2(p2: P2): suspend (P1, P3, P4) -> R =
  { p1: P1, p3: P3, p4: P4 -> this(p1, p2, p3, p4) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, R> (suspend (P1, P2, P3, P4) -> R).partially3(p3: P3): suspend (P1, P2, P4) -> R =
  { p1: P1, p2: P2, p4: P4 -> this(p1, p2, p3, p4) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, R> (suspend (P1, P2, P3, P4) -> R).partially4(p4: P4): suspend (P1, P2, P3) -> R =
  { p1: P1, p2: P2, p3: P3 -> this(p1, p2, p3, p4) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, R> (suspend (P1, P2, P3, P4, P5) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, R> (suspend (P1, P2, P3, P4, P5) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, R> (suspend (P1, P2, P3, P4, P5) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, R> (suspend (P1, P2, P3, P4, P5) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5 -> this(p1, p2, p3, p4, p5) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, R> (suspend (P1, P2, P3, P4, P5) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4 -> this(p1, p2, p3, p4, p5) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, R> (suspend (P1, P2, P3, P4, P5, P6) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, R> (suspend (P1, P2, P3, P4, P5, P6) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, R> (suspend (P1, P2, P3, P4, P5, P6) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, R> (suspend (P1, P2, P3, P4, P5, P6) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, R> (suspend (P1, P2, P3, P4, P5, P6) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6 -> this(p1, p2, p3, p4, p5, p6) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, R> (suspend (P1, P2, P3, P4, P5, P6) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5, p6) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, R> (suspend (P1, P2, P3, P4, P5, P6, P7) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, R> (suspend (P1, P2, P3, P4, P5, P6, P7) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, R> (suspend (P1, P2, P3, P4, P5, P6, P7) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, R> (suspend (P1, P2, P3, P4, P5, P6, P7) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, R> (suspend (P1, P2, P3, P4, P5, P6, P7) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, R> (suspend (P1, P2, P3, P4, P5, P6, P7) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, R> (suspend (P1, P2, P3, P4, P5, P6, P7) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1, p2, p3, p4, p5, p6, p7) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7, p8) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially15Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).partially15(p15: P15): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially15Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially15(p15: P15): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially16Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).partially16(p16: P16): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially15Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially15(p15: P15): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially16Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially16(p16: P16): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially17Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).partially17(p17: P17): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially15Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially15(p15: P15): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially16Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially16(p16: P16): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially17Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially17(p17: P17): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially18Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).partially18(p18: P18): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially15Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially15(p15: P15): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially16Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially16(p16: P16): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially17Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially17(p17: P17): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially18Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially18(p18: P18): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially19Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).partially19(p19: P19): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially15Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially15(p15: P15): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially16Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially16(p16: P16): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially17Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially17(p17: P17): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially18Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially18(p18: P18): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially19Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially19(p19: P19): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially20Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).partially20(p20: P20): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially15Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially15(p15: P15): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially16Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially16(p16: P16): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially17Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially17(p17: P17): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially18Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially18(p18: P18): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially19Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially19(p19: P19): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially20Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially20(p20: P20): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially21Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).partially21(p21: P21): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

@JvmName("partially1Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially1(p1: P1): suspend (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially2Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially2(p2: P2): suspend (P1, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially3Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially3(p3: P3): suspend (P1, P2, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially4Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially4(p4: P4): suspend (P1, P2, P3, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially5Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially5(p5: P5): suspend (P1, P2, P3, P4, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially6Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially6(p6: P6): suspend (P1, P2, P3, P4, P5, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially7Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially7(p7: P7): suspend (P1, P2, P3, P4, P5, P6, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially8Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially8(p8: P8): suspend (P1, P2, P3, P4, P5, P6, P7, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially9Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially9(p9: P9): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially10Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially10(p10: P10): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially11Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially11(p11: P11): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially12Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially12(p12: P12): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially13Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially13(p13: P13): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially14Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially14(p14: P14): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially15Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially15(p15: P15): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially16Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially16(p16: P16): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially17Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially17(p17: P17): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially18Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially18(p18: P18): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially19Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially19(p19: P19): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p20: P20, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially20Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially20(p20: P20): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p21: P21, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially21Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially21(p21: P21): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }

@JvmName("partially22Effect")
public fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).partially22(p22: P22): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }
