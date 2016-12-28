/*
 * Copyright 2013 - 2016 Mario Arias
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.funktionale.complement

fun Function0<Boolean>.complement(): () -> Boolean {
    return { !this() }
}

fun <P1> Function1<P1, Boolean>.complement(): (P1) -> Boolean {
    return { p1: P1 -> !this(p1) }
}

fun <P1, P2> Function2<P1, P2, Boolean>.complement(): (P1, P2) -> Boolean {
    return { p1: P1, p2: P2 -> !this(p1, p2) }
}

fun <P1, P2, P3> Function3<P1, P2, P3, Boolean>.complement(): (P1, P2, P3) -> Boolean {
    return { p1: P1, p2: P2, p3: P3 -> !this(p1, p2, p3) }
}

fun <P1, P2, P3, P4> Function4<P1, P2, P3, P4, Boolean>.complement(): (P1, P2, P3, P4) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4 -> !this(p1, p2, p3, p4) }
}

fun <P1, P2, P3, P4, P5> Function5<P1, P2, P3, P4, P5, Boolean>.complement(): (P1, P2, P3, P4, P5) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> !this(p1, p2, p3, p4, p5) }
}

fun <P1, P2, P3, P4, P5, P6> Function6<P1, P2, P3, P4, P5, P6, Boolean>.complement(): (P1, P2, P3, P4, P5, P6) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> !this(p1, p2, p3, p4, p5, p6) }
}

fun <P1, P2, P3, P4, P5, P6, P7> Function7<P1, P2, P3, P4, P5, P6, P7, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> !this(p1, p2, p3, p4, p5, p6, p7) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8> Function8<P1, P2, P3, P4, P5, P6, P7, P8, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> !this(p1, p2, p3, p4, p5, p6, p7, p8) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9> Function9<P1, P2, P3, P4, P5, P6, P7, P8, P9, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> Function10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> Function11<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> Function12<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> Function13<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> Function14<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> Function15<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16> Function16<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17> Function17<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18> Function18<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19> Function19<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20> Function20<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21> Function21<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) }
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22> Function22<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, Boolean>.complement(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> Boolean {
    return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> !this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) }
}