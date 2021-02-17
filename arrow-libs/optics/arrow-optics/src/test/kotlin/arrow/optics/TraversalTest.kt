package arrow.optics

import arrow.core.Option
import arrow.core.extensions.option.eq.eq
import arrow.core.toT
import arrow.core.ListK
import arrow.core.extensions.listk.eq.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.tuple2
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class TraversalTest : UnitSpec() {

  init {
    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.list(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = Eq.any()
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
      traversal = Traversal({ it.a }, { it.b }, { a, b, _ -> a toT b }),
      aGen = Gen.tuple2(Gen.float(), Gen.float()),
      bGen = Gen.float(),
      funcGen = Gen.functionAToB(Gen.float()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))
  }
}
