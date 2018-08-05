package arrow.syntax.function

infix inline fun <P1, R> P1.pipe(t: (P1) -> R): R = t(this)

infix inline fun <P1, R> P1.pipeLazy(crossinline t: (P1) -> R): () -> R = { t(this) }

infix inline fun <P1, P2, R> P1.pipe2(crossinline t: (P1, P2) -> R): (P2) -> R = { p2 -> t(this, p2) }

infix inline fun <P1, P2, P3, R> P1.pipe3(crossinline t: (P1, P2, P3) -> R): (P2, P3) -> R = { p2, p3 -> t(this, p2, p3) }

infix inline fun <P1, P2, P3, P4, R> P1.pipe4(crossinline t: (P1, P2, P3, P4) -> R): (P2, P3, P4) -> R = { p2, p3, p4 -> t(this, p2, p3, p4) }

infix inline fun <P1, P2, P3, P4, P5, R> P1.pipe5(crossinline t: (P1, P2, P3, P4, P5) -> R): (P2, P3, P4, P5) -> R = { p2, p3, p4, p5 -> t(this, p2, p3, p4, p5) }

infix inline fun <P1, P2, P3, P4, P5, P6, R> P1.pipe6(crossinline t: (P1, P2, P3, P4, P5, P6) -> R): (P2, P3, P4, P5, P6) -> R = { p2, p3, p4, p5, p6 -> t(this, p2, p3, p4, p5, p6) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, R> P1.pipe7(crossinline t: (P1, P2, P3, P4, P5, P6, P7) -> R): (P2, P3, P4, P5, P6, P7) -> R = { p2, p3, p4, p5, p6, p7 -> t(this, p2, p3, p4, p5, p6, p7) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, R> P1.pipe8(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8) -> R): (P2, P3, P4, P5, P6, P7, P8) -> R = { p2, p3, p4, p5, p6, p7, p8 -> t(this, p2, p3, p4, p5, p6, p7, p8) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> P1.pipe9(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R): (P2, P3, P4, P5, P6, P7, P8, P9) -> R = { p2, p3, p4, p5, p6, p7, p8, p9 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> P1.pipe10(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> P1.pipe11(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> P1.pipe12(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> P1.pipe13(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> P1.pipe14(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> P1.pipe15(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> P1.pipe16(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> P1.pipe17(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> P1.pipe18(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> P1.pipe19(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> P1.pipe20(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> P1.pipe21(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }

infix inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> P1.pipe22(crossinline t: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R): (P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R = { p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22 -> t(this, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }
