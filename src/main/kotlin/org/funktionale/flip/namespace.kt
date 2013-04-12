package org.funktionale.flip

public fun<P1, P2, R> Function1<P1, Function1<P2, R>>.flip(): (P2) -> (P1) -> R {
    return {(p2: P2) -> {(p1: P1)  -> this(p1)(p2) } }
}


/*public fun<P1, P2, P3, R> Function1<P1, Function1<P2, Function1<P3, R>>>.flip(): (P3) -> (P2) -> (P1) -> R {
    return {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3) } } }
}


public fun<P1, P2, P3, P4, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, R>>>>.flip(): (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4) } } } }
}


public fun<P1, P2, P3, P4, P5, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, R>>>>>.flip(): (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5) } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, R>>>>>>.flip(): (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6) } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, R>>>>>>>.flip(): (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7) } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, R>>>>>>>>.flip(): (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8) } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, R>>>>>>>>>.flip(): (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9) } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, R>>>>>>>>>>.flip(): (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10) } } } } } } } } } }
}



public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, R>>>>>>>>>>>.flip(): (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11) } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, R>>>>>>>>>>>>.flip(): (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12) } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, R>>>>>>>>>>>>>.flip(): (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13) } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, R>>>>>>>>>>>>>>.flip(): (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14) } } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, Function1<P15, R>>>>>>>>>>>>>>>.flip(): (P15) -> (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p15: P15) -> {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15) } } } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, Function1<P15, Function1<P16, R>>>>>>>>>>>>>>>>.flip(): (P16) -> (P15) -> (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p16: P16) -> {(p15: P15) -> {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16) } } } } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, Function1<P15, Function1<P16, Function1<P17, R>>>>>>>>>>>>>>>>>.flip(): (P17) -> (P16) -> (P15) -> (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p17: P17) -> {(p16: P16) -> {(p15: P15) -> {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17) } } } } } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, Function1<P15, Function1<P16, Function1<P17, Function1<P18, R>>>>>>>>>>>>>>>>>>.flip(): (P18) -> (P17) -> (P16) -> (P15) -> (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p18: P18) -> {(p17: P17) -> {(p16: P16) -> {(p15: P15) -> {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18) } } } } } } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, Function1<P15, Function1<P16, Function1<P17, Function1<P18, Function1<P19, R>>>>>>>>>>>>>>>>>>>.flip(): (P19) -> (P18) -> (P17) -> (P16) -> (P15) -> (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p19: P19) -> {(p18: P18) -> {(p17: P17) -> {(p16: P16) -> {(p15: P15) -> {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19) } } } } } } } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, Function1<P15, Function1<P16, Function1<P17, Function1<P18, Function1<P19, Function1<P20, R>>>>>>>>>>>>>>>>>>>>.flip(): (P20) -> (P19) -> (P18) -> (P17) -> (P16) -> (P15) -> (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p20: P20) -> {(p19: P19) -> {(p18: P18) -> {(p17: P17) -> {(p16: P16) -> {(p15: P15) -> {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20) } } } } } } } } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, Function1<P15, Function1<P16, Function1<P17, Function1<P18, Function1<P19, Function1<P20, Function1<P21, R>>>>>>>>>>>>>>>>>>>>>.flip(): (P21) -> (P20) -> (P19) -> (P18) -> (P17) -> (P16) -> (P15) -> (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p21: P21) -> {(p20: P20) -> {(p19: P19) -> {(p18: P18) -> {(p17: P17) -> {(p16: P16) -> {(p15: P15) -> {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20)(p21) } } } } } } } } } } } } } } } } } } } } }
}


public fun<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> Function1<P1, Function1<P2, Function1<P3, Function1<P4, Function1<P5, Function1<P6, Function1<P7, Function1<P8, Function1<P9, Function1<P10, Function1<P11, Function1<P12, Function1<P13, Function1<P14, Function1<P15, Function1<P16, Function1<P17, Function1<P18, Function1<P19, Function1<P20, Function1<P21, Function1<P22, R>>>>>>>>>>>>>>>>>>>>>>.flip(): (P22) -> (P21) -> (P20) -> (P19) -> (P18) -> (P17) -> (P16) -> (P15) -> (P14) -> (P13) -> (P12) -> (P11) -> (P10) -> (P9) -> (P8) -> (P7) -> (P6) -> (P5) -> (P4) -> (P3) -> (P2) -> (P1) -> R {
    return {(p22: P22) -> {(p21: P21) -> {(p20: P20) -> {(p19: P19) -> {(p18: P18) -> {(p17: P17) -> {(p16: P16) -> {(p15: P15) -> {(p14: P14) -> {(p13: P13) -> {(p12: P12) -> {(p11: P11) -> {(p10: P10) -> {(p9: P9) -> {(p8: P8) -> {(p7: P7) -> {(p6: P6) -> {(p5: P5) -> {(p4: P4) -> {(p3: P3) -> {(p2: P2) -> {(p1: P1)  -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20)(p21)(p22) } } } } } } } } } } } } } } } } } } } } } }
}*/
