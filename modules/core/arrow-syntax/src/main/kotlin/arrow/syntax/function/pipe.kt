package arrow.syntax.function

infix fun <P1, R> P1.pipeLazy(t: (P1) -> R): () -> R = { t(this) }

infix inline fun <P1, R> P1.pipe(t: (P1) -> R): R = t(this)

infix fun <P1, P2, R> P1.pipe2(t: (P1, P2) -> R): (P2) -> R = { p2 -> t(this, p2) }

infix fun <P1, P2, P3, R> P1.pipe3(t: (P1, P2, P3) -> R): (P2, P3) -> R = { p2, p3 -> t(this, p2, p3) }

infix fun <P1, P2, P3, P4, R> P1.pipe4(t: (P1, P2, P3, P4) -> R): (P2, P3, P4) -> R = { p2, p3, p4 -> t(this, p2, p3, p4) }

infix fun <P1, P2, P3, P4, P5, R> P1.pipe5(t: (P1, P2, P3, P4, P5) -> R): (P2, P3, P4, P5) -> R = { p2, p3, p4, p5 -> t(this, p2, p3, p4, p5) }

infix fun <P1, P2, P3, P4, P5, P6, R> P1.pipe6(t: (P1, P2, P3, P4, P5, P6) -> R): (P2, P3, P4, P5, P6) -> R = { p2, p3, p4, p5, p6 -> t(this, p2, p3, p4, p5, p6) }

infix fun <P1, P2, P3, P4, P5, P6, P7, R> P1.pipe7(t: (P1, P2, P3, P4, P5, P6, P7) -> R): (P2, P3, P4, P5, P6, P7) -> R = { p2, p3, p4, p5, p6, p7 -> t(this, p2, p3, p4, p5, p6, p7) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, R> P1.pipe8(t: (P1, P2, P3, P4, P5, P6, P7, P8) -> R): (P2, P3, P4, P5, P6, P7, P8) -> R = { p2, p3, p4, p5, p6, p7, p8 -> t(this, p2, p3, p4, p5, p6, p7, p8) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> P1.pipe9(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R): (P2, P3, P4, P5, P6, P7, P8, P9) -> R = { p2, p3, p4, p5, p6, p7, p8, p9 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> P1.pipe10(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> P1.pipe11(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> P1.pipe12(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> P1.pipe13(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> P1.pipe14(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> P1.pipe15(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> P1.pipe16(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> P1.pipe17(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> P1.pipe18(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> P1.pipe19(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> P1.pipe20(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> P1.pipe21(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

infix fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> P1.pipe22(t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }
