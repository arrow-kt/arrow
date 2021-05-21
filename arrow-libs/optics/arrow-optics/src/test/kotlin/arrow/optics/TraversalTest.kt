package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import io.kotest.property.Arb

class TraversalTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Arb.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      ),

      SetterLaws.laws(
        setter = Traversal.list(),
        aGen = Arb.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal({ it.first }, { it.second }, { a, b, _ -> a to b }),
        aGen = Gen.pair(Gen.float(), Gen.float()),
        bGen = Gen.float(),
        funcGen = Gen.functionAToB(Gen.float()),
      )
    )
  }
}
