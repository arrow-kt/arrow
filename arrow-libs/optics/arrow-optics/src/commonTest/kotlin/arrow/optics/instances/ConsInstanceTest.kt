package arrow.optics.instances

import arrow.optics.test.functionAToB
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.typeclasses.Cons
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import kotlin.test.Test

class ConsInstanceTest {
  @Test
  fun consListLaws() =
    testLaws(
      PrismLaws(
        prism = Cons.list<Int>().cons(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.pair(Arb.int(), Arb.list(Arb.int())),
        funcGen = Arb.functionAToB(Arb.pair(Arb.int(), Arb.list(Arb.int()))),
      )
    )

  @Test
  fun consStringLaws() =
    testLaws(
      PrismLaws(
        prism = Cons.string().cons(),
        aGen = Arb.string(),
        bGen = Arb.pair(Arb.char(), Arb.string()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.char(), Arb.string())),
      )
    )
}
