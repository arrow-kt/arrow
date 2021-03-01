package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class TraversalTest : UnitSpec() {

  init {

    val listKTraverse = Traversal.list<Int>()

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any()
      ),

      SetterLaws.laws(
        setter = Traversal.list(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any()
      )
    )

    testLaws(TraversalLaws.laws(
      traversal = Traversal({ it.first }, { it.second }, { a, b, _ -> a to b }),
      aGen = Gen.pair(Gen.float(), Gen.float()),
      bGen = Gen.float(),
      funcGen = Gen.functionAToB(Gen.float()),
      EQA = Eq.any()
    ))
  }
}
