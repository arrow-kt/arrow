package arrow.optics.instances

import arrow.core.*
import arrow.data.ListK
import arrow.data.eq
import arrow.optics.typeclasses.FilterIndex
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.OptionalLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StringInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = String.each().each(),
      aGen = Gen.string(),
      bGen = genChars(),
      funcGen = genFunctionAToB(genChars()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = FilterIndex.filterIndex(String.filterIndex()) { true },
      aGen = Gen.string(),
      bGen = genChars(),
      funcGen = genFunctionAToB(genChars()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(OptionalLaws.laws(
      optional = String.index().index(5),
      aGen = Gen.string(),
      bGen = genChars(),
      funcGen = genFunctionAToB(genChars()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

  }
}
