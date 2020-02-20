package arrow.optics.instances

import arrow.core.Option
import arrow.core.ListK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.option.each.each
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.option
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class OptionInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = Option.each<String>().each(),
      aGen = Gen.option(Gen.string()),
      bGen = Gen.string(),
      funcGen = Gen.functionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))
  }
}
