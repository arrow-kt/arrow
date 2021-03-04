package arrow.syntax.function

fun (() -> Boolean).complement(): () -> Boolean = { !this() }

fun <P1> ((P1) -> Boolean).complement(): (P1) -> Boolean = { p1: P1 -> !this(p1) }

fun <P1, P2> ((P1, P2) -> Boolean).complement(): (P1, P2) -> Boolean = { p1: P1, p2: P2 -> !this(p1, p2) }

fun <P1, P2, P3> ((P1, P2, P3) -> Boolean).complement(): (P1, P2, P3) -> Boolean = { p1: P1, p2: P2, p3: P3 -> !this(p1, p2, p3) }

fun <P1, P2, P3, P4> ((P1, P2, P3, P4) -> Boolean).complement(): (P1, P2, P3, P4) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4 -> !this(p1, p2, p3, p4) }

fun <P1, P2, P3, P4, P5> ((P1, P2, P3, P4, P5) -> Boolean).complement(): (P1, P2, P3, P4, P5) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> !this(p1, p2, p3, p4, p5) }

fun <P1, P2, P3, P4, P5, P6> ((P1, P2, P3, P4, P5, P6) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> !this(p1, p2, p3, p4, p5, p6) }

fun <P1, P2, P3, P4, P5, P6, P7> ((P1, P2, P3, P4, P5, P6, P7) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> !this(p1, p2, p3, p4, p5, p6, p7) }

fun <P1, P2, P3, P4, P5, P6, P7, P8> ((P1, P2, P3, P4, P5, P6, P7, P8) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> !this(p1, p2, p3, p4, p5, p6, p7, p8) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> Boolean).complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> Boolean = { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }
