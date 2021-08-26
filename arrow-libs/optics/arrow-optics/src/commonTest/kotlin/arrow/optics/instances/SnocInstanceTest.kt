package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.PrismLaws
import arrow.optics.typeclasses.Snoc
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string

class SnocInstanceTest : UnitSpec() {
  init {
    testLaws(
      "Snoc list - ",
      PrismLaws.laws(
        prism = Snoc.list<Int>().snoc(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.pair(Arb.list(Arb.int()), Arb.int()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.list(Arb.int()), Arb.int())),
      )
    )
    testLaws(
      "Snoc string - ",
      PrismLaws.laws(
        prism = Snoc.string().snoc(),
        aGen = Arb.string(),
        bGen = Arb.pair(Arb.string(), Arb.char()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.string(), Arb.char())),
      )
    )
  }
}
