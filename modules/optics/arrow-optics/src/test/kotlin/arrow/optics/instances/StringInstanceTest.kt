package arrow.optics.instances

import arrow.core.Option
import arrow.core.eq
import arrow.data.ListK
import arrow.data.eq
import arrow.test.UnitSpec
import arrow.test.generators.genChar
import arrow.test.generators.genFunctionAToB
import arrow.test.laws.OptionalLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class StringInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = String.each().each(),
      aGen = Gen.string(),
      bGen = genChar(),
      funcGen = genFunctionAToB(genChar()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = String.filterIndex().filter { true },
      aGen = Gen.string(),
      bGen = genChar(),
      funcGen = genFunctionAToB(genChar()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(OptionalLaws.laws(
      optional = String.index().index(5),
      aGen = Gen.string(),
      bGen = genChar(),
      funcGen = genFunctionAToB(genChar()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

  }
}
