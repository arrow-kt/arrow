package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.PrismLaws
import arrow.optics.typeclasses.Cons
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string

class ConsInstanceTest : UnitSpec() {
  init {
    testLaws(
      "Const list - ",
      PrismLaws.laws(
        prism = Cons.list<Int>().cons(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.pair(Arb.int(), Arb.list(Arb.int())),
        funcGen = Arb.functionAToB(Arb.pair(Arb.int(), Arb.list(Arb.int()))),
      )
    )

    testLaws(
      "Cons string - ",
      PrismLaws.laws(
        prism = Cons.string().cons(),
        aGen = Arb.string(),
        bGen = Arb.pair(Arb.char(), Arb.string()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.char(), Arb.string())),
      )
    )
  }
}
