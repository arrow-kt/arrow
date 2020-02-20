package arrow.optics.instances

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Try
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.optics.extensions.`try`.each.each
import arrow.test.UnitSpec
import arrow.test.generators.`try`
import arrow.test.generators.functionAToB
import arrow.test.laws.TraversalLaws
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
