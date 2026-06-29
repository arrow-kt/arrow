package arrow.core

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PartialsTest {

  fun arb(): Arb<String> = Arb.string(1)

  operator fun <T> List<T>.component6() = get(5)
  operator fun <T> List<T>.component7() = get(6)
  operator fun <T> List<T>.component8() = get(7)
  operator fun <T> List<T>.component9() = get(8)
  operator fun <T> List<T>.component10() = get(9)
  operator fun <T> List<T>.component11() = get(10)
  operator fun <T> List<T>.component12() = get(11)
  operator fun <T> List<T>.component13() = get(12)
  operator fun <T> List<T>.component14() = get(13)
  operator fun <T> List<T>.component15() = get(14)
  operator fun <T> List<T>.component16() = get(15)
  operator fun <T> List<T>.component17() = get(16)
  operator fun <T> List<T>.component18() = get(17)
  operator fun <T> List<T>.component19() = get(18)
  operator fun <T> List<T>.component20() = get(19)
  operator fun <T> List<T>.component21() = get(20)
  operator fun <T> List<T>.component22() = get(21)

  // region arity 1

  @Test
  fun partially1of1() = runTest {
    checkAll(arb()) { a1 ->
      val f = { p1: String -> p1 }
      f.partially1(a1)() shouldBe f(a1)
    }
  }

  // endregion

  // region arity 2

  @Test
  fun partially1of2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val f = { p1: String, p2: String -> p1 + p2 }
      f.partially1(a1)(a2) shouldBe f(a1, a2)
    }
  }

  @Test
  fun partially2of2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val f = { p1: String, p2: String -> p1 + p2 }
      f.partially2(a2)(a1) shouldBe f(a1, a2)
    }
  }

  @Test
  fun suspendPartially1of2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val f: suspend (String, String) -> String = { p1, p2 -> p1 + p2 }
      f.partially1(a1)(a2) shouldBe f(a1, a2)
    }
  }

  @Test
  fun suspendPartially2of2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val f: suspend (String, String) -> String = { p1, p2 -> p1 + p2 }
      f.partially2(a2)(a1) shouldBe f(a1, a2)
    }
  }

  // endregion

  // region arity 3

  @Test
  fun partially1of3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val f = { p1: String, p2: String, p3: String -> p1 + p2 + p3 }
      f.partially1(a1)(a2, a3) shouldBe f(a1, a2, a3)
    }
  }

  @Test
  fun partially2of3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val f = { p1: String, p2: String, p3: String -> p1 + p2 + p3 }
      f.partially2(a2)(a1, a3) shouldBe f(a1, a2, a3)
    }
  }

  @Test
  fun partially3of3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val f = { p1: String, p2: String, p3: String -> p1 + p2 + p3 }
      f.partially3(a3)(a1, a2) shouldBe f(a1, a2, a3)
    }
  }

  @Test
  fun suspendPartially1of3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val f: suspend (String, String, String) -> String = { p1, p2, p3 -> p1 + p2 + p3 }
      f.partially1(a1)(a2, a3) shouldBe f(a1, a2, a3)
    }
  }

  @Test
  fun suspendPartially3of3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val f: suspend (String, String, String) -> String = { p1, p2, p3 -> p1 + p2 + p3 }
      f.partially3(a3)(a1, a2) shouldBe f(a1, a2, a3)
    }
  }

  // endregion

  // region arity 4

  @Test
  fun partially1of4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val f = { p1: String, p2: String, p3: String, p4: String -> p1 + p2 + p3 + p4 }
      f.partially1(a1)(a2, a3, a4) shouldBe f(a1, a2, a3, a4)
    }
  }

  @Test
  fun partially2of4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val f = { p1: String, p2: String, p3: String, p4: String -> p1 + p2 + p3 + p4 }
      f.partially2(a2)(a1, a3, a4) shouldBe f(a1, a2, a3, a4)
    }
  }

  @Test
  fun partially3of4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val f = { p1: String, p2: String, p3: String, p4: String -> p1 + p2 + p3 + p4 }
      f.partially3(a3)(a1, a2, a4) shouldBe f(a1, a2, a3, a4)
    }
  }

  @Test
  fun partially4of4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val f = { p1: String, p2: String, p3: String, p4: String -> p1 + p2 + p3 + p4 }
      f.partially4(a4)(a1, a2, a3) shouldBe f(a1, a2, a3, a4)
    }
  }

  // endregion

  // region arity 5

  @Test
  fun partially1of5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String -> p1 + p2 + p3 + p4 + p5 }
      f.partially1(a1)(a2, a3, a4, a5) shouldBe f(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun partially2of5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String -> p1 + p2 + p3 + p4 + p5 }
      f.partially2(a2)(a1, a3, a4, a5) shouldBe f(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun partially3of5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String -> p1 + p2 + p3 + p4 + p5 }
      f.partially3(a3)(a1, a2, a4, a5) shouldBe f(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun partially4of5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String -> p1 + p2 + p3 + p4 + p5 }
      f.partially4(a4)(a1, a2, a3, a5) shouldBe f(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun partially5of5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String -> p1 + p2 + p3 + p4 + p5 }
      f.partially5(a5)(a1, a2, a3, a4) shouldBe f(a1, a2, a3, a4, a5)
    }
  }

  // endregion

  // region arity 6

  @Test
  fun partially1of6() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String -> p1 + p2 + p3 + p4 + p5 + p6 }
      f.partially1(a1)(a2, a3, a4, a5, a6) shouldBe f(a1, a2, a3, a4, a5, a6)
    }
  }

  @Test
  fun partially6of6() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String -> p1 + p2 + p3 + p4 + p5 + p6 }
      f.partially6(a6)(a1, a2, a3, a4, a5) shouldBe f(a1, a2, a3, a4, a5, a6)
    }
  }

  // endregion

  // region arity 7

  @Test
  fun partially1of7() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7) shouldBe f(a1, a2, a3, a4, a5, a6, a7)
    }
  }

  @Test
  fun partially7of7() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
      f.partially7(a7)(a1, a2, a3, a4, a5, a6) shouldBe f(a1, a2, a3, a4, a5, a6, a7)
    }
  }

  // endregion

  // region arity 8

  @Test
  fun partially1of8() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8)
    }
  }

  @Test
  fun partially8of8() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 }
      f.partially8(a8)(a1, a2, a3, a4, a5, a6, a7) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8)
    }
  }

  // endregion

  // region arity 9

  @Test
  fun partially1of9() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }
  }

  @Test
  fun partially9of9() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 }
      f.partially9(a9)(a1, a2, a3, a4, a5, a6, a7, a8) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }
  }

  // endregion

  // region arity 10

  @Test
  fun partially1of10() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }
  }

  @Test
  fun partially10of10() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 }
      f.partially10(a10)(a1, a2, a3, a4, a5, a6, a7, a8, a9) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }
  }

  // endregion

  // region arity 11

  @Test
  fun partially1of11() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }
  }

  @Test
  fun partially11of11() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 }
      f.partially11(a11)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }
  }

  // endregion

  // region arity 12

  @Test
  fun partially1of12() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
  }

  @Test
  fun partially12of12() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 }
      f.partially12(a12)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
  }

  // endregion

  // region arity 13

  @Test
  fun partially1of13() = runTest {
    checkAll(Arb.list(arb(), 13..13)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  @Test
  fun partially13of13() = runTest {
    checkAll(Arb.list(arb(), 13..13)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      f.partially13(a13)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  // endregion

  // region arity 14

  @Test
  fun partially1of14() = runTest {
    checkAll(Arb.list(arb(), 14..14)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  @Test
  fun partially14of14() = runTest {
    checkAll(Arb.list(arb(), 14..14)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      f.partially14(a14)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  // endregion

  // region arity 15

  @Test
  fun partially1of15() = runTest {
    checkAll(Arb.list(arb(), 15..15)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  @Test
  fun partially15of15() = runTest {
    checkAll(Arb.list(arb(), 15..15)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      f.partially15(a15)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  // endregion

  // region arity 16

  @Test
  fun partially1of16() = runTest {
    checkAll(Arb.list(arb(), 16..16)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  @Test
  fun partially16of16() = runTest {
    checkAll(Arb.list(arb(), 16..16)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      f.partially16(a16)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  // endregion

  // region arity 17

  @Test
  fun partially1of17() = runTest {
    checkAll(Arb.list(arb(), 17..17)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  @Test
  fun partially17of17() = runTest {
    checkAll(Arb.list(arb(), 17..17)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      f.partially17(a17)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  // endregion

  // region arity 18

  @Test
  fun partially1of18() = runTest {
    checkAll(Arb.list(arb(), 18..18)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  @Test
  fun partially18of18() = runTest {
    checkAll(Arb.list(arb(), 18..18)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      f.partially18(a18)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  // endregion

  // region arity 19

  @Test
  fun partially1of19() = runTest {
    checkAll(Arb.list(arb(), 19..19)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  @Test
  fun partially19of19() = runTest {
    checkAll(Arb.list(arb(), 19..19)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      f.partially19(a19)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  // endregion

  // region arity 20

  @Test
  fun partially1of20() = runTest {
    checkAll(Arb.list(arb(), 20..20)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  @Test
  fun partially20of20() = runTest {
    checkAll(Arb.list(arb(), 20..20)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      f.partially20(a20)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  // endregion

  // region arity 21

  @Test
  fun partially1of21() = runTest {
    checkAll(Arb.list(arb(), 21..21)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  @Test
  fun partially21of21() = runTest {
    checkAll(Arb.list(arb(), 21..21)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      f.partially21(a21)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  // endregion

  // region arity 22

  @Test
  fun partially1of22() = runTest {
    checkAll(Arb.list(arb(), 22..22)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String, p22: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      f.partially1(a1)(a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  @Test
  fun partially22of22() = runTest {
    checkAll(Arb.list(arb(), 22..22)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22) = a
      val f = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String, p22: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      f.partially22(a22)(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21) shouldBe f(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  // endregion
}
