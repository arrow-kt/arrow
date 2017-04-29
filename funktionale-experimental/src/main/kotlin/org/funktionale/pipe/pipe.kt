package org.funktionale.pipe

infix inline fun <P1, R> P1.pipe(t: (P1) -> R): R = t(this)

infix inline fun <P1, P2, P3> P1.pipe2(crossinline t: (P1, P2) -> P3): (P2) -> P3 = { p2 -> t(this, p2) }

infix inline fun <P1, P2, P3, P4> P1.pipe3(crossinline t: (P1, P2, P3) -> P4): (P2, P3) -> P4 = { p2, p3 -> t(this, p2, p3) }

infix inline fun <P1, P2, P3, P4, P5> P1.pipe4(crossinline t: (P1, P2, P3, P4) -> P5): (P2, P3, P4) -> P5 = { p2, p3, p4 -> t(this, p2, p3, p4) }

infix inline fun <P1, P2, P3, P4, P5, P6> P1.pipe5(crossinline t: (P1, P2, P3, P4, P5) -> P6): (P2, P3, P4, P5) -> P6 = { p2, p3, p4, p5 -> t(this, p2, p3, p4, p5) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7> P1.pipe6(crossinline t: (P1, P2, P3, P4, P5, P6) -> P7): (P2, P3, P4, P5, P6) -> P7 = { p2, p3, p4, p5, p6 -> t(this, p2, p3, p4, p5, p6) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8> P1.pipe7(crossinline t: (P1, P2, P3, P4, P5, P6, P7) -> P8): (P2, P3, P4, P5, P6, P7) -> P8 = { p2, p3, p4, p5, p6, p7 -> t(this, p2, p3, p4, p5, p6, p7) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9> P1.pipe8(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8) -> P9): (P2, P3, P4, P5, P6, P7, P8) -> P9 = { p2, p3, p4, p5, p6, p7, p8 -> t(this, p2, p3, p4, p5, p6, p7, p8) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> P1.pipe9(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> P10): (P2, P3, P4, P5, P6, P7, P8, P9) -> P10 = { p2, p3, p4, p5, p6, p7, p8, p9 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> P1.pipe10(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> P11): (P2, P3, P4, P5, P6, P7, P8, P9, P10) -> P11 = { p2, p3, p4, p5, p6, p7, p8, p9, p10 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> P1.pipe11(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> P12): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> P12 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> P1.pipe12(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> P13): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> P13 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> P1.pipe13(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> P14): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> P14 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> P1.pipe14(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> P15): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> P15 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16> P1.pipe15(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> P16): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> P16 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17> P1.pipe16(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> P17): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> P17 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18> P1.pipe17(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> P18): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> P18 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19> P1.pipe18(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> P19): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> P19 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20> P1.pipe19(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> P20): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> P20 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21> P1.pipe20(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> P21): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> P21 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22> P1.pipe21(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> P22): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> P22 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, P23> P1.pipe22(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> P23): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> P23 = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }
