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

package org.funktionale.currying


fun <P1, P2, R> ((P1, P2) -> R).curried(): (P1) -> (P2) -> R {
	return { p1: P1 -> { p2: P2 -> this(p1, p2) } }
}


fun <P1, P2, P3, R> ((P1, P2, P3) -> R).curried(): (P1) -> (P2) -> (P3) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> this(p1, p2, p3) } } }
}


fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> this(p1, p2, p3, p4) } } } }
}


fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> this(p1, p2, p3, p4, p5) } } } } }
}


fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> this(p1, p2, p3, p4, p5, p6) } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> this(p1, p2, p3, p4, p5, p6, p7) } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> this(p1, p2, p3, p4, p5, p6, p7, p8) } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9) } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) } } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) } } } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) } } } } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) } } } } } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) } } } } } } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) } } } } } } } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20) } } } } } } } } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> { p21: P21 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21) } } } } } } } } } } } } } } } } } } } } }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).curried(): (P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> (P22) -> R {
	return { p1: P1 -> { p2: P2 -> { p3: P3 -> { p4: P4 -> { p5: P5 -> { p6: P6 -> { p7: P7 -> { p8: P8 -> { p9: P9 -> { p10: P10 -> { p11: P11 -> { p12: P12 -> { p13: P13 -> { p14: P14 -> { p15: P15 -> { p16: P16 -> { p17: P17 -> { p18: P18 -> { p19: P19 -> { p20: P20 -> { p21: P21 -> { p22: P22 -> this(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22) } } } } } } } } } } } } } } } } } } } } } }
}

fun <P1, P2, R> ((P1) -> (P2) -> R).uncurried(): (P1, P2) -> R {
	return { p1: P1, p2: P2 -> this(p1)(p2) }
}


fun <P1, P2, P3, R> ((P1) -> (P2) -> (P3) -> R).uncurried(): (P1, P2, P3) -> R {
	return { p1: P1, p2: P2, p3: P3 -> this(p1)(p2)(p3) }
}


fun <P1, P2, P3, P4, R> ((P1) -> (P2) -> (P3) -> (P4) -> R).uncurried(): (P1, P2, P3, P4) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4 -> this(p1)(p2)(p3)(p4) }
}


fun <P1, P2, P3, P4, P5, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> R).uncurried(): (P1, P2, P3, P4, P5) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> this(p1)(p2)(p3)(p4)(p5) }
}


fun <P1, P2, P3, P4, P5, P6, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> R).uncurried(): (P1, P2, P3, P4, P5, P6) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6 -> this(p1)(p2)(p3)(p4)(p5)(p6) }
}


fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20)(p21) }
}


fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1) -> (P2) -> (P3) -> (P4) -> (P5) -> (P6) -> (P7) -> (P8) -> (P9) -> (P10) -> (P11) -> (P12) -> (P13) -> (P14) -> (P15) -> (P16) -> (P17) -> (P18) -> (P19) -> (P20) -> (P21) -> (P22) -> R).uncurried(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R {
	return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22 -> this(p1)(p2)(p3)(p4)(p5)(p6)(p7)(p8)(p9)(p10)(p11)(p12)(p13)(p14)(p15)(p16)(p17)(p18)(p19)(p20)(p21)(p22) }
}
