package arrow.core

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CurryingTest {

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

  @Suppress("Assigned_value_is_never_read")
  @Test
  fun listDestructuring() = runTest {
    val a = List(22) { it }
    val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22) = a
    var i = 0
    a1 shouldBe i++
    a2 shouldBe i++
    a3 shouldBe i++
    a4 shouldBe i++
    a5 shouldBe i++
    a6 shouldBe i++
    a7 shouldBe i++
    a8 shouldBe i++
    a9 shouldBe i++
    a10 shouldBe i++
    a11 shouldBe i++
    a12 shouldBe i++
    a13 shouldBe i++
    a14 shouldBe i++
    a15 shouldBe i++
    a16 shouldBe i++
    a17 shouldBe i++
    a18 shouldBe i++
    a19 shouldBe i++
    a20 shouldBe i++
    a21 shouldBe i++
    a22 shouldBe i++
  }

  //region curried

  @Test
  fun curry2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val add = { p1: String, p2: String -> p1 + p2 }
      add.curried()(a1)(a2) shouldBe add(a1, a2)
    }
  }

  @Test
  fun curry3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val add = { p1: String, p2: String, p3: String -> p1 + p2 + p3 }
      add.curried()(a1)(a2)(a3) shouldBe add(a1, a2, a3)
    }
  }

  @Test
  fun curry4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val add = { p1: String, p2: String, p3: String, p4: String -> p1 + p2 + p3 + p4 }
      add.curried()(a1)(a2)(a3)(a4) shouldBe add(a1, a2, a3, a4)
    }
  }

  @Test
  fun curry5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String -> p1 + p2 + p3 + p4 + p5 }
      add.curried()(a1)(a2)(a3)(a4)(a5) shouldBe add(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun curry6() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String -> p1 + p2 + p3 + p4 + p5 + p6 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6) shouldBe add(a1, a2, a3, a4, a5, a6)
    }
  }

  @Test
  fun curry7() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7) shouldBe add(a1, a2, a3, a4, a5, a6, a7)
    }
  }

  @Test
  fun curry8() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8)
    }
  }

  @Test
  fun curry9() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }
  }

  @Test
  fun curry10() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }
  }

  @Test
  fun curry11() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }
  }

  @Test
  fun curry12() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
  }

  @Test
  fun curry13() = runTest {
    checkAll(Arb.list(arb(), 13..13)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  @Test
  fun curry14() = runTest {
    checkAll(Arb.list(arb(), 14..14)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  @Test
  fun curry15() = runTest {
    checkAll(Arb.list(arb(), 15..15)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  @Test
  fun curry16() = runTest {
    checkAll(Arb.list(arb(), 16..16)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  @Test
  fun curry17() = runTest {
    checkAll(Arb.list(arb(), 17..17)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  @Test
  fun curry18() = runTest {
    checkAll(Arb.list(arb(), 18..18)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  @Test
  fun curry19() = runTest {
    checkAll(Arb.list(arb(), 19..19)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  @Test
  fun curry20() = runTest {
    checkAll(Arb.list(arb(), 20..20)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  @Test
  fun curry21() = runTest {
    checkAll(Arb.list(arb(), 21..21)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  @Test
  fun curry22() = runTest {
    checkAll(Arb.list(arb(), 22..22)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String, p22: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21)(a22) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  //endregion

  // region uncurried

  @Test
  fun uncurry2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val add = { p1: String, p2: String -> p1 + p2 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2) shouldBe curriedAdd.uncurried()(a1, a2)
    }
  }

  @Test
  fun uncurry3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val add = { p1: String, p2: String, p3: String -> p1 + p2 + p3 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3) shouldBe curriedAdd.uncurried()(a1, a2, a3)
    }
  }

  @Test
  fun uncurry4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val add = { p1: String, p2: String, p3: String, p4: String -> p1 + p2 + p3 + p4 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4)
    }
  }

  @Test
  fun uncurry5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String -> p1 + p2 + p3 + p4 + p5 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun uncurry6() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String -> p1 + p2 + p3 + p4 + p5 + p6 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6)
    }
  }

  @Test
  fun uncurry7() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7)
    }
  }

  @Test
  fun uncurry8() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8)
    }
  }

  @Test
  fun uncurry9() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }
  }

  @Test
  fun uncurry10() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }
  }

  @Test
  fun uncurry11() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }
  }

  @Test
  fun uncurry12() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
  }

  @Test
  fun uncurry13() = runTest {
    checkAll(Arb.list(arb(), 13..13)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  @Test
  fun uncurry14() = runTest {
    checkAll(Arb.list(arb(), 14..14)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  @Test
  fun uncurry15() = runTest {
    checkAll(Arb.list(arb(), 15..15)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  @Test
  fun uncurry16() = runTest {
    checkAll(Arb.list(arb(), 16..16)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  @Test
  fun uncurry17() = runTest {
    checkAll(Arb.list(arb(), 17..17)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  @Test
  fun uncurry18() = runTest {
    checkAll(Arb.list(arb(), 18..18)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  @Test
  fun uncurry19() = runTest {
    checkAll(Arb.list(arb(), 19..19)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  @Test
  fun uncurry20() = runTest {
    checkAll(Arb.list(arb(), 20..20)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  @Test
  fun uncurry21() = runTest {
    checkAll(Arb.list(arb(), 21..21)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  @Test
  fun uncurry22() = runTest {
    checkAll(Arb.list(arb(), 22..22)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22) = a
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String, p22: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21)(a22) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  // endregion

  // region curried effect

  @Test
  fun curryEffect2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val add: suspend (String, String) -> String = { p1, p2 -> p1 + p2 }
      add.curried()(a1)(a2) shouldBe add(a1, a2)
    }
  }

  @Test
  fun curryEffect3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val add: suspend (String, String, String) -> String = { p1, p2, p3 -> p1 + p2 + p3 }
      add.curried()(a1)(a2)(a3) shouldBe add(a1, a2, a3)
    }
  }

  @Test
  fun curryEffect4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val add: suspend (String, String, String, String) -> String = { p1, p2, p3, p4 -> p1 + p2 + p3 + p4 }
      add.curried()(a1)(a2)(a3)(a4) shouldBe add(a1, a2, a3, a4)
    }
  }

  @Test
  fun curryEffect5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val add: suspend (String, String, String, String, String) -> String = { p1, p2, p3, p4, p5 -> p1 + p2 + p3 + p4 + p5 }
      add.curried()(a1)(a2)(a3)(a4)(a5) shouldBe add(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun curryEffect6() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6 ->
      val add: suspend (String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6 -> p1 + p2 + p3 + p4 + p5 + p6 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6) shouldBe add(a1, a2, a3, a4, a5, a6)
    }
  }

  @Test
  fun curryEffect7() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7 ->
      val add: suspend (String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7) shouldBe add(a1, a2, a3, a4, a5, a6, a7)
    }
  }

  @Test
  fun curryEffect8() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8 ->
      val add: suspend (String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8)
    }
  }

  @Test
  fun curryEffect9() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9 ->
      val add: suspend (String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }
  }

  @Test
  fun curryEffect10() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }
  }

  @Test
  fun curryEffect11() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }
  }

  @Test
  fun curryEffect12() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
  }

  @Test
  fun curryEffect13() = runTest {
    checkAll(Arb.list(arb(), 13..13)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  @Test
  fun curryEffect14() = runTest {
    checkAll(Arb.list(arb(), 14..14)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  @Test
  fun curryEffect15() = runTest {
    checkAll(Arb.list(arb(), 15..15)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  @Test
  fun curryEffect16() = runTest {
    checkAll(Arb.list(arb(), 16..16)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  @Test
  fun curryEffect17() = runTest {
    checkAll(Arb.list(arb(), 17..17)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  @Test
  fun curryEffect18() = runTest {
    checkAll(Arb.list(arb(), 18..18)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  @Test
  fun curryEffect19() = runTest {
    checkAll(Arb.list(arb(), 19..19)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  @Test
  fun curryEffect20() = runTest {
    checkAll(Arb.list(arb(), 20..20)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  @Test
  fun curryEffect21() = runTest {
    checkAll(Arb.list(arb(), 21..21)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  @Test
  fun curryEffect22() = runTest {
    checkAll(Arb.list(arb(), 22..22)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21)(a22) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  // endregion

  // region uncurried effect

  @Test
  fun curryUncurryEffect2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val add: suspend (String, String) -> String = { p1, p2 -> p1 + p2 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2) shouldBe curriedAdd.uncurried()(a1, a2)
    }
  }

  @Test
  fun curryUncurryEffect3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val add: suspend (String, String, String) -> String = { p1, p2, p3 -> p1 + p2 + p3 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3) shouldBe curriedAdd.uncurried()(a1, a2, a3)
    }
  }

  @Test
  fun curryUncurryEffect4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val add: suspend (String, String, String, String) -> String = { p1, p2, p3, p4 -> p1 + p2 + p3 + p4 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4)
    }
  }

  @Test
  fun curryUncurryEffect5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val add: suspend (String, String, String, String, String) -> String = { p1, p2, p3, p4, p5 -> p1 + p2 + p3 + p4 + p5 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun curryUncurryEffect6() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6 ->
      val add: suspend (String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6 -> p1 + p2 + p3 + p4 + p5 + p6 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6)
    }
  }

  @Test
  fun curryUncurryEffect7() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7 ->
      val add: suspend (String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7)
    }
  }

  @Test
  fun curryUncurryEffect8() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8 ->
      val add: suspend (String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8)
    }
  }

  @Test
  fun curryUncurryEffect9() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9 ->
      val add: suspend (String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }
  }

  @Test
  fun curryUncurryEffect10() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }
  }

  @Test
  fun curryUncurryEffect11() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }
  }

  @Test
  fun curryUncurryEffect12() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
  }

  @Test
  fun curryUncurryEffect13() = runTest {
    checkAll(Arb.list(arb(), 13..13)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  @Test
  fun curryUncurryEffect14() = runTest {
    checkAll(Arb.list(arb(), 14..14)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  @Test
  fun curryUncurryEffect15() = runTest {
    checkAll(Arb.list(arb(), 15..15)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  @Test
  fun curryUncurryEffect16() = runTest {
    checkAll(Arb.list(arb(), 16..16)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  @Test
  fun curryUncurryEffect17() = runTest {
    checkAll(Arb.list(arb(), 17..17)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  @Test
  fun curryUncurryEffect18() = runTest {
    checkAll(Arb.list(arb(), 18..18)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  @Test
  fun curryUncurryEffect19() = runTest {
    checkAll(Arb.list(arb(), 19..19)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  @Test
  fun curryUncurryEffect20() = runTest {
    checkAll(Arb.list(arb(), 20..20)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  @Test
  fun curryUncurryEffect21() = runTest {
    checkAll(Arb.list(arb(), 21..21)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  @Test
  fun curryUncurryEffect22() = runTest {
    checkAll(Arb.list(arb(), 22..22)) { a ->
      val (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22) = a
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21)(a22) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  // endregion
}
