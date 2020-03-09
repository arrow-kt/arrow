package arrow.optics.instances

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Try
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.`try`.each.each
import arrow.core.test.UnitSpec
import arrow.core.test.generators.`try`
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class TryInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = Try.each<String>().each(),
      aGen = Gen.`try`(Gen.string()),
      bGen = Gen.string(),
      funcGen = Gen.functionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))
  }
}
