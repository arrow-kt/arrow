package arrow.optics.instances

import arrow.optics.typeclasses.Snoc
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.laws.testLaws
import io.kotest.property.arrow.optics.PrismLaws

class SnocInstanceTest : StringSpec() {
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
