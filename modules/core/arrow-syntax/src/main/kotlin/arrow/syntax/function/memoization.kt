package arrow.syntax.function

import arrow.syntax.internal.Platform.newConcurrentMap

fun <R> (() -> R).memoize(): () -> R = object : () -> R {
  private val m = MemoizedHandler<() -> R, MemoizeKey0<R>, R>(this@memoize)
  override fun invoke(): R = m(MemoizeKey0(0))
}

fun <P1, R> ((P1) -> R).memoize(): (P1) -> R = object : (P1) -> R {
  private val m = MemoizedHandler<((P1) -> R), MemoizeKey1<P1, R>, R>(this@memoize)
  override fun invoke(p1: P1) = m(MemoizeKey1(p1))
}

fun <P1, P2, R> ((P1, P2) -> R).memoize(): (P1, P2) -> R = object : (P1, P2) -> R {
  private val m = MemoizedHandler<((P1, P2) -> R), MemoizeKey2<P1, P2, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2) = m(MemoizeKey2(p1, p2))
}

fun <P1, P2, P3, R> ((P1, P2, P3) -> R).memoize(): (P1, P2, P3) -> R = object : (P1, P2, P3) -> R {
  private val m = MemoizedHandler<((P1, P2, P3) -> R), MemoizeKey3<P1, P2, P3, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3) = m(MemoizeKey3(p1, p2, p3))
}

fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).memoize(): (P1, P2, P3, P4) -> R = object : (P1, P2, P3, P4) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4) -> R), MemoizeKey4<P1, P2, P3, P4, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4) = m(MemoizeKey4(p1, p2, p3, p4))
}

fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).memoize(): (P1, P2, P3, P4, P5) -> R = object : (P1, P2, P3, P4, P5) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5) -> R), MemoizeKey5<P1, P2, P3, P4, P5, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) = m(MemoizeKey5(p1, p2, p3, p4, p5))
}

fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).memoize(): (P1, P2, P3, P4, P5, P6) -> R = object : (P1, P2, P3, P4, P5, P6) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6) -> R), MemoizeKey6<P1, P2, P3, P4, P5, P6, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6) = m(MemoizeKey6(p1, p2, p3, p4, p5, p6))
}

fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7) -> R = object : (P1, P2, P3, P4, P5, P6, P7) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7) -> R), MemoizeKey7<P1, P2, P3, P4, P5, P6, P7, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7) = m(MemoizeKey7(p1, p2, p3, p4, p5, p6, p7))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8) -> R), MemoizeKey8<P1, P2, P3, P4, P5, P6, P7, P8, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8) = m(MemoizeKey8(p1, p2, p3, p4, p5, p6, p7, p8))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R), MemoizeKey9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9) = m(MemoizeKey9(p1, p2, p3, p4, p5, p6, p7, p8, p9))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R), MemoizeKey10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10) = m(MemoizeKey10(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R), MemoizeKey11<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11) = m(MemoizeKey11(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R), MemoizeKey12<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12) = m(MemoizeKey12(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R), MemoizeKey13<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13) = m(MemoizeKey13(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R), MemoizeKey14<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14) = m(MemoizeKey14(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R), MemoizeKey15<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15) = m(MemoizeKey15(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R), MemoizeKey16<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16) = m(MemoizeKey16(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R), MemoizeKey17<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17) = m(MemoizeKey17(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R), MemoizeKey18<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18) = m(MemoizeKey18(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R), MemoizeKey19<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19) = m(MemoizeKey19(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R), MemoizeKey20<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20) = m(MemoizeKey20(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R), MemoizeKey21<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21) = m(MemoizeKey21(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21))
}

fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R = object : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R {
  private val m = MemoizedHandler<((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R), MemoizeKey22<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R>, R>(this@memoize)
  override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13, p14: P14, p15: P15, p16: P16, p17: P17, p18: P18, p19: P19, p20: P20, p21: P21, p22: P22) = m(MemoizeKey22(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22))
}

private interface MemoizedCall<in F, out R> {
  operator fun invoke(f: F): R
}

private data class MemoizeKey0<R>(val p1: Byte) : MemoizedCall<() -> R, R> {
  override fun invoke(f: () -> R): R = f()
}

private data class MemoizeKey1<out P1, R>(val p1: P1) : MemoizedCall<(P1) -> R, R> {
  override fun invoke(f: (P1) -> R) = f(p1)
}

private data class MemoizeKey2<out P1, out P2, R>(val p1: P1, val p2: P2) : MemoizedCall<(P1, P2) -> R, R> {
  override fun invoke(f: (P1, P2) -> R) = f(p1, p2)
}

private data class MemoizeKey3<out P1, out P2, out P3, R>(val p1: P1, val p2: P2, val p3: P3) : MemoizedCall<(P1, P2, P3) -> R, R> {
  override fun invoke(f: (P1, P2, P3) -> R) = f(p1, p2, p3)
}

private data class MemoizeKey4<out P1, out P2, out P3, out P4, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4) : MemoizedCall<(P1, P2, P3, P4) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4) -> R) = f(p1, p2, p3, p4)
}

private data class MemoizeKey5<out P1, out P2, out P3, out P4, out P5, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5) : MemoizedCall<(P1, P2, P3, P4, P5) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5) -> R) = f(p1, p2, p3, p4, p5)
}

private data class MemoizeKey6<out P1, out P2, out P3, out P4, out P5, out P6, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6) : MemoizedCall<(P1, P2, P3, P4, P5, P6) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6) -> R) = f(p1, p2, p3, p4, p5, p6)
}

private data class MemoizeKey7<out P1, out P2, out P3, out P4, out P5, out P6, out P7, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7) -> R) = f(p1, p2, p3, p4, p5, p6, p7)
}

private data class MemoizeKey8<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8)
}

private data class MemoizeKey9<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9)
}

private data class MemoizeKey10<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
}

private data class MemoizeKey11<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11)
}

private data class MemoizeKey12<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
}

private data class MemoizeKey13<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)
}

private data class MemoizeKey14<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)
}

private data class MemoizeKey15<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, out P15, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14, val p15: P15) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)
}

private data class MemoizeKey16<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, out P15, out P16, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14, val p15: P15, val p16: P16) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16)
}

private data class MemoizeKey17<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, out P15, out P16, out P17, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14, val p15: P15, val p16: P16, val p17: P17) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17)
}

private data class MemoizeKey18<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, out P15, out P16, out P17, out P18, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14, val p15: P15, val p16: P16, val p17: P17, val p18: P18) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18)
}

private data class MemoizeKey19<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, out P15, out P16, out P17, out P18, out P19, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14, val p15: P15, val p16: P16, val p17: P17, val p18: P18, val p19: P19) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19)
}

private data class MemoizeKey20<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, out P15, out P16, out P17, out P18, out P19, out P20, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14, val p15: P15, val p16: P16, val p17: P17, val p18: P18, val p19: P19, val p20: P20) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20)
}

private data class MemoizeKey21<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, out P15, out P16, out P17, out P18, out P19, out P20, out P21, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14, val p15: P15, val p16: P16, val p17: P17, val p18: P18, val p19: P19, val p20: P20, val p21: P21) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21)
}

private data class MemoizeKey22<out P1, out P2, out P3, out P4, out P5, out P6, out P7, out P8, out P9, out P10, out P11, out P12, out P13, out P14, out P15, out P16, out P17, out P18, out P19, out P20, out P21, out P22, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4, val p5: P5, val p6: P6, val p7: P7, val p8: P8, val p9: P9, val p10: P10, val p11: P11, val p12: P12, val p13: P13, val p14: P14, val p15: P15, val p16: P16, val p17: P17, val p18: P18, val p19: P19, val p20: P20, val p21: P21, val p22: P22) : MemoizedCall<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R) = f(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22)
}

private class MemoizedHandler<F, in K : MemoizedCall<F, R>, out R>(val f: F) {
  private val m = newConcurrentMap<K, R>()
  operator fun invoke(k: K): R = m[k] ?: run { m.putSafely(k, k(f)) }
}
