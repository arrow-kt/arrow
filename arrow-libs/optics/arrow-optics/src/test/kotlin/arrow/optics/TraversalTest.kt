package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.generators.iterable
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import io.kotlintest.properties.Gen

class TraversalTest : UnitSpec() {

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Gen.iterable(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      ),

      SetterLaws.laws(
        setter = Traversal.list(),
        aGen = Gen.iterable(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(TraversalLaws.laws(
      traversal = Traversal({ it.first }, { it.second }, { a, b, _ -> a to b }),
      aGen = Gen.pair(Gen.float(), Gen.float()),
      bGen = Gen.float(),
      funcGen = Gen.functionAToB(Gen.float()),
    ))
  }
}
