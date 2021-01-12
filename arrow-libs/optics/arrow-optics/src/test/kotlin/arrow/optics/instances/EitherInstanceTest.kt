package arrow.optics.instances

import arrow.core.Option
import arrow.core.ListK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.optics.Traversal
import arrow.optics.either
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class EitherInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = Traversal.either(),
      aGen = Gen.either(Gen.string(), Gen.int()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))
  }
}
