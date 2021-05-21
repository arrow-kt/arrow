package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.optics.Traversal
import arrow.optics.test.laws.TraversalLaws
import io.kotest.property.Arb

class EitherInstanceTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.either(),
        aGen = Gen.either(Arb.string(), Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )
  }
}
