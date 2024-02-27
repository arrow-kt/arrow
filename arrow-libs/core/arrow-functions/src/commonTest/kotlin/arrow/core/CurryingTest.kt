package arrow.core

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class CurryingTest {

  fun arb(): Arb<String> = Arb.string(1)


  //region curried

  @Test
  fun curry2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val add = { p1:  String, p2:  String -> p1 + p2 }
      add.curried()(a1)(a2) shouldBe add(a1, a2)
    }
  }

  @Test
  fun curry3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val add = { p1:  String, p2:  String, p3:  String -> p1 + p2 + p3 }
      add.curried()(a1)(a2)(a3) shouldBe add(a1, a2, a3)
    }
  }

  @Test
  fun curry4() = runTest {
    checkAll(arb(), arb(), arb(), arb()) { a1, a2, a3, a4 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String -> p1 + p2 + p3 + p4 }
      add.curried()(a1)(a2)(a3)(a4) shouldBe add(a1, a2, a3, a4)
    }
  }

  @Test
  fun curry5() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String -> p1 + p2 + p3 + p4 + p5 }
      add.curried()(a1)(a2)(a3)(a4)(a5) shouldBe add(a1, a2, a3, a4, a5)
    }
  }

  @Test
  fun curry6() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String -> p1 + p2 + p3 + p4 + p5 + p6 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6) shouldBe add(a1, a2, a3, a4, a5, a6)
    }
  }

  @Test
  fun curry7() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7) shouldBe add(a1, a2, a3, a4, a5, a6, a7)
    }
  }

  @Test
  fun curry8() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8)
    }
  }

  @Test
  fun curry9() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9)
    }
  }

  @Test
  fun curry10() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
    }
  }

  @Test
  fun curry11() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
    }
  }

  @Test
  fun curry12() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
  }

  /*

  @Test
  fun curry13() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  @Test
  fun curry14() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  @Test
  fun curry15() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  @Test
  fun curry16() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  @Test
  fun curry17() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  @Test
  fun curry18() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  @Test
  fun curry19() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  @Test
  fun curry20() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  @Test
  fun curry21() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  @Test
  fun curry22() = runTest {
    checkAll(arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22 ->
      val add = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String, p22: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21)(a22) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  */

  //endregion

  // region uncurried

  @Test
  fun uncurry2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val add = { p1:  String, p2:  String -> p1 + p2 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2) shouldBe curriedAdd.uncurried()(a1, a2)
    }
  }

  @Test
  fun uncurry3() = runTest {
    checkAll(arb(), arb(), arb()) { a1, a2, a3 ->
      val add = { p1:  String, p2:  String, p3: String -> p1 + p2 + p3 }
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
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String-> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12)
    }
  }

  /*

  "A 13-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String-> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  "A 14-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  "A 15-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  "A 16-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String-> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  "A 17-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  "A 18-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  "A 19-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  "A 20-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  "A 21-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  "A 22-arity curried function returns the same result as the function after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22 ->
      val add = { p1: String, p2: String, p3: String, p4: String, p5: String, p6: String, p7: String, p8: String, p9: String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String, p20: String, p21: String, p22: String  -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21)(a22) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  */

  // endregion

  // region curried effect

  @Test
  fun curryEffect2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val add: suspend (String, String) -> String = {  p1, p2 -> p1 + p2 }
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
      val add: suspend (String, String, String, String, String) -> String  = { p1, p2, p3, p4, p5 -> p1 + p2 + p3 + p4 + p5 }
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
      val add: suspend (String, String, String, String, String, String, String) -> String  = { p1, p2, p3, p4, p5, p6, p7 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
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

  /*

  "A 13-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  "A 14-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  "A 15-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  "A 16-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  "A 17-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  "A 18-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  "A 19-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  "A 20-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  "A 21-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  "A 22-arity curried effect returns the same result as the effect before being curried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      add.curried()(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21)(a22) shouldBe add(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  */

  // endregion

  // region uncurried effect

  @Test
  fun curryUncurryEffect2() = runTest {
    checkAll(arb(), arb()) { a1, a2 ->
      val add: suspend (String, String) -> String = {  p1, p2 -> p1 + p2 }
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
      val add: suspend (String, String, String, String, String) -> String  = { p1, p2, p3, p4, p5 -> p1 + p2 + p3 + p4 + p5 }
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
      val add: suspend (String, String, String, String, String, String, String) -> String  = { p1, p2, p3, p4, p5, p6, p7 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 }
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

  /*

  "A 13-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13)
    }
  }

  "A 14-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14)
    }
  }

  "A 15-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15)
    }
  }

  "A 16-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16)
    }
  }

  "A 17-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17)
    }
  }

  "A 18-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18)
    }
  }

  "A 19-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1:  String, p2:  String, p3:  String, p4:  String, p5:  String, p6:  String, p7:  String, p8:  String, p9:  String, p10: String, p11: String, p12: String, p13: String, p14: String, p15: String, p16: String, p17: String, p18: String, p19: String -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19)
    }
  }

  "A 20-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20)
    }
  }

  "A 21-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21)
    }
  }

  "A 22-arity curried effect returns the same result as the effect after being uncurried" {
    checkAll(PropTestConfig(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb(), arb()) { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22 ->
      val add: suspend (String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String) -> String = { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22 -> p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 + p18 + p19 + p20 + p21 + p22 }
      val curriedAdd = add.curried()
      curriedAdd(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(a21)(a22) shouldBe curriedAdd.uncurried()(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22)
    }
  }

  */

  // endregion
}
