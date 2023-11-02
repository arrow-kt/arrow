package arrow.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TupleTest : StringSpec({

  "shortToString" {
    checkAll(
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int()
    ) { a, b, c, d, e, f, g, h, i ->
      Tuple4(a, b, c, d).toString() shouldBe "($a, $b, $c, $d)"
      Tuple5(a, b, c, d, e).toString() shouldBe "($a, $b, $c, $d, $e)"
      Tuple6(a, b, c, d, e, f).toString() shouldBe "($a, $b, $c, $d, $e, $f)"
      Tuple7(a, b, c, d, e, f, g).toString() shouldBe "($a, $b, $c, $d, $e, $f, $g)"
      Tuple8(a, b, c, d, e, f, g, h).toString() shouldBe "($a, $b, $c, $d, $e, $f, $g, $h)"
      Tuple9(a, b, c, d, e, f, g, h, i).toString() shouldBe "($a, $b, $c, $d, $e, $f, $g, $h, $i)"
    }
  }

  "plus" {
    checkAll(
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int()
    ) { a, b, c, d, e, f, g, h, i ->
      Pair(a, b) + c shouldBe Triple(a, b, c)
      Triple(a, b, c) + d shouldBe Tuple4(a, b, c, d)
      Tuple4(a, b, c, d) + e shouldBe Tuple5(a, b, c, d, e)
      Tuple5(a, b, c, d, e) + f shouldBe Tuple6(a, b, c, d, e, f)
      Tuple6(a, b, c, d, e, f) + g shouldBe Tuple7(a, b, c, d, e, f, g)
      Tuple7(a, b, c, d, e, f, g) + h shouldBe Tuple8(a, b, c, d, e, f, g, h)
      Tuple8(a, b, c, d, e, f, g, h) + i shouldBe Tuple9(a, b, c, d, e, f, g, h, i)
    }
  }

  "compareTo(equals)" {
    checkAll(
      Arb.int(),
      Arb.string(),
      Arb.double(),
      Arb.int(),
      Arb.boolean(),
      Arb.int(),
      Arb.char(),
      Arb.int(),
      Arb.long()
    ) { a, b, c, d, e, f, g, h, i ->
      Pair(a, b).compareTo(Pair(a, b)) shouldBe 0
      Triple(a, b, c).compareTo(Triple(a, b, c)) shouldBe 0
      Tuple4(a, b, c, d).compareTo(Tuple4(a, b, c, d)) shouldBe 0
      Tuple5(a, b, c, d, e).compareTo(Tuple5(a, b, c, d, e)) shouldBe 0
      Tuple6(a, b, c, d, e, f).compareTo(Tuple6(a, b, c, d, e, f)) shouldBe 0
      Tuple7(a, b, c, d, e, f, g).compareTo(Tuple7(a, b, c, d, e, f, g)) shouldBe 0
      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a, b, c, d, e, f, g, h)) shouldBe 0
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b, c, d, e, f, g, h, i)) shouldBe 0
    }
  }

  "compareTo(not equals)" {
    checkAll(
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded()
    ) { a, b, c, d, e, f, g, h, i ->
      Pair(a, b).compareTo(Pair(a + 1, b)) shouldBe -1
      Triple(a, b, c).compareTo(Triple(a + 1, b, c)) shouldBe -1
      Tuple4(a, b, c, d).compareTo(Tuple4(a - 1, b, c, d)) shouldBe 1
      Tuple5(a, b, c, d, e).compareTo(Tuple5(a - 1, b, c, d, e)) shouldBe 1
      Tuple6(a, b, c, d, e, f).compareTo(Tuple6(a - 1, b, c, d, e, f)) shouldBe 1
      Tuple7(a, b, c, d, e, f, g).compareTo(Tuple7(a - 1, b, c, d, e, f, g)) shouldBe 1
      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a + 1, b, c, d, e, f, g, h)) shouldBe -1
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a + 1, b, c, d, e, f, g, h, i)) shouldBe -1
    }
  }

  "compareTo(deep not equals)" {
    checkAll(
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded(),
      Arb.intOpenEnded()
    ) { a, b, c, d, e, f, g, h, i ->
      Pair(a, b).compareTo(Pair(a, b + 1)) shouldBe -1

      Triple(a, b, c).compareTo(Triple(a, b + 1, c)) shouldBe -1
      Triple(a, b, c).compareTo(Triple(a, b, c + 1)) shouldBe -1

      Tuple4(a, b, c, d).compareTo(Tuple4(a, b + 1, c, d)) shouldBe -1
      Tuple4(a, b, c, d).compareTo(Tuple4(a, b, c + 1, d)) shouldBe -1
      Tuple4(a, b, c, d).compareTo(Tuple4(a, b, c, d + 1)) shouldBe -1

      Tuple5(a, b, c, d, e).compareTo(Tuple5(a, b + 1, c, d, e)) shouldBe -1
      Tuple5(a, b, c, d, e).compareTo(Tuple5(a, b, c + 1, d, e)) shouldBe -1
      Tuple5(a, b, c, d, e).compareTo(Tuple5(a, b, c, d + 1, e)) shouldBe -1
      Tuple5(a, b, c, d, e).compareTo(Tuple5(a, b, c, d, e + 1)) shouldBe -1

      Tuple6(a, b, c, d, e, f).compareTo(Tuple6(a, b + 1, c, d, e, f)) shouldBe -1
      Tuple6(a, b, c, d, e, f).compareTo(Tuple6(a, b, c + 1, d, e, f)) shouldBe -1
      Tuple6(a, b, c, d, e, f).compareTo(Tuple6(a, b, c, d + 1, e, f)) shouldBe -1
      Tuple6(a, b, c, d, e, f).compareTo(Tuple6(a, b, c, d, e + 1, f)) shouldBe -1
      Tuple6(a, b, c, d, e, f).compareTo(Tuple6(a, b, c, d, e, f + 1)) shouldBe -1

      Tuple7(a, b, c, d, e, f, g).compareTo(Tuple7(a, b + 1, c, d, e, f, g)) shouldBe -1
      Tuple7(a, b, c, d, e, f, g).compareTo(Tuple7(a, b, c + 1, d, e, f, g)) shouldBe -1
      Tuple7(a, b, c, d, e, f, g).compareTo(Tuple7(a, b, c, d + 1, e, f, g)) shouldBe -1
      Tuple7(a, b, c, d, e, f, g).compareTo(Tuple7(a, b, c, d, e + 1, f, g)) shouldBe -1
      Tuple7(a, b, c, d, e, f, g).compareTo(Tuple7(a, b, c, d, e, f + 1, g)) shouldBe -1
      Tuple7(a, b, c, d, e, f, g).compareTo(Tuple7(a, b, c, d, e, f, g + 1)) shouldBe -1

      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a, b + 1, c, d, e, f, g, h)) shouldBe -1
      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a, b, c + 1, d, e, f, g, h)) shouldBe -1
      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a, b, c, d + 1, e, f, g, h)) shouldBe -1
      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a, b, c, d, e + 1, f, g, h)) shouldBe -1
      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a, b, c, d, e, f + 1, g, h)) shouldBe -1
      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a, b, c, d, e, f, g + 1, h)) shouldBe -1
      Tuple8(a, b, c, d, e, f, g, h).compareTo(Tuple8(a, b, c, d, e, f, g, h + 1)) shouldBe -1

      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b + 1, c, d, e, f, g, h, i)) shouldBe -1
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b, c + 1, d, e, f, g, h, i)) shouldBe -1
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b, c, d + 1, e, f, g, h, i)) shouldBe -1
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b, c, d, e + 1, f, g, h, i)) shouldBe -1
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b, c, d, e, f + 1, g, h, i)) shouldBe -1
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b, c, d, e, f, g + 1, h, i)) shouldBe -1
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b, c, d, e, f, g, h + 1, i)) shouldBe -1
      Tuple9(a, b, c, d, e, f, g, h, i).compareTo(Tuple9(a, b, c, d, e, f, g, h, i + 1)) shouldBe -1
    }
  }
})

private fun Arb.Companion.intOpenEnded() = Arb.int(Int.MIN_VALUE + 1, Int.MAX_VALUE - 1)
