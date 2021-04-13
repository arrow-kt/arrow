@file:JvmName("Currying")
package arrow.core

fun <P1, P2, R> ((P1, P2) -> R).curried(): (P1) -> (P2) -> R =
  { p1: P1 -> { p2: P2 -> this(p1, p2) } }

fun <P1, P2, P3, R> ((P1, P2, P3) -> R).curried(): (P1) -> (P2) -> (P3) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> this(p1, p2, p3) } } }

fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> this(p1, p2, p3, p4) } } } }

fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> this(p1, p2, p3, p4, p5) } } } } }

fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> this(p1, p2, p3, p4, p5, p6) } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) } } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) } } } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) } } } } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) } } } } } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) } } } } } } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) } } } } } } } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) } } } } } } } } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> { p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) } } } } } } } } } } } } } } } } } } } } }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> (P22) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> { p21: P21 -> { p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) } } } } } } } } } } } } } } } } } } } } } }

fun <P1, P2, R> ((P1) -> (P2) -> R).uncurried(): (P1, P2) -> R = { p1: P1, p2: P2 -> this(p1)(p2) }

fun <P1, P2, P3, R> ((P1) -> (P2) -> (P3) -> R).uncurried(): (P1, P2, P3) -> R =
  { p1: P1, p2: P2, p3: P3 -> this(p1)(p2)(p3) }

fun <P1, P2, P3, P4, R> ((P1) -> (P2) -> (P3) -> (P4) -> R).uncurried(): (P1, P2, P3, P4) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4 -> this(p1)(p2)(p3)(p4) }

fun <P1, P2, P3, P4, P5, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> R).uncurried(): (P1, P2, P3, P4, P5) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1)(p2)(p3)(p4)(p5) }

fun <P1, P2, P3, P4, P5, P6, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> R).uncurried(): (P1, P2, P3, P4, P5, P6) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1)(p2)(p3)(p4)(p5)(p6) }

fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20)(p21) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> (P22) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20)(p21)(p22) }

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

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, R> (suspend (P1, P2, P3, P4, P5, P6) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> suspend (P6) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> this(p1, p2, p3, p4, p5, p6) } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, R> (suspend (P1, P2, P3, P4, P5, P6, P7) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> suspend (P7) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> suspend (P8) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> suspend (P9) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> suspend (P10) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> suspend (P11) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> suspend (P12) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> suspend (P13) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> suspend (P14) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) } } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> suspend (P15) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) } } } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> suspend (P16) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) } } } } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> suspend (P17) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) } } } } } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> suspend (P18) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) } } } } } } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> suspend (P19) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) } } } } } } } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> suspend (P20) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) } } } } } } } } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> suspend (P21) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> { p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) } } } } } } } } } } } } } } } } } } } } }

@JvmName("curriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> (suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> suspend (P22) -> R =
  { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> { p21: P21 -> { p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) } } } } } } } } } } } } } } } } } } } } } }

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

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> suspend (P6) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1)(p2)(p3)(p4)(p5)(p6) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> suspend (P7) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> suspend (P8) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> suspend (P9) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> suspend (P10) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> suspend (P11) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> suspend (P12) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> suspend (P13) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> suspend (P14) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> suspend (P15) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> suspend (P16) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> suspend (P17) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> suspend (P18) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> suspend (P19) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> suspend (P20) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20) }

@JvmName("uncurriedEffect")
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> suspend (P21) -> R).uncurried(): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R =
  { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20)(p21) }
