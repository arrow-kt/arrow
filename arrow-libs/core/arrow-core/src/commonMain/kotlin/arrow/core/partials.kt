@file:JvmName("Partials")
package arrow.core

import kotlin.jvm.JvmName

fun <P1, R> ((P1) -> R).partially1(p1: P1): () -> R =
  { this(p1) }

fun <P1, P2, R> ((P1, P2) -> R).partially1(p1: P1): (P2) -> R =
  { p2: P2 -> this(p1, p2) }

fun <P1, P2, R> ((P1, P2) -> R).partially2(p2: P2): (P1) -> R =
  { p1: P1 -> this(p1, p2) }

fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partially1(p1: P1): (P2, P3) -> R =
  { p2: P2, p3: P3 -> this(p1, p2, p3) }

fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partially2(p2: P2): (P1, P3) -> R =
  { p1: P1, p3: P3 -> this(p1, p2, p3) }

fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partially3(p3: P3): (P1, P2) -> R =
  { p1: P1, p2: P2 -> this(p1, p2, p3) }

fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).partially1(p1: P1): (P2, P3, P4) -> R =
  { p2: P2, p3: P3, p4: P4 -> this(p1, p2, p3, p4) }

fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).partially2(p2: P2): (P1, P3, P4) -> R =
  { p1: P1, p3: P3, p4: P4 -> this(p1, p2, p3, p4) }

fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).partially3(p3: P3): (P1, P2, P4) -> R =
  { p1: P1, p2: P2, p4: P4 -> this(p1, p2, p3, p4) }

fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).partially4(p4: P4): (P1, P2, P3) -> R =
  { p1: P1, p2: P2, p3: P3 -> this(p1, p2, p3, p4) }

fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially1(p1: P1): (P2, P3, P4, P5) -> R =
  { p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially2(p2: P2): (P1, P3, P4, P5) -> R =
  { p1: P1, p3: P3, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially3(p3: P3): (P1, P2, P4, P5) -> R =
  { p1: P1, p2: P2, p4: P4, p5: P5 -> this(p1, p2, p3, p4, p5) }

fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially4(p4: P4): (P1, P2, P3, P5) -> R =
  { p1: P1, p2: P2, p3: P3, p5: P5 -> this(p1, p2, p3, p4, p5) }

fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).partially5(p5: P5): (P1, P2, P3, P4) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4 -> this(p1, p2, p3, p4, p5) }
