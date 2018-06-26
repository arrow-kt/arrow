package arrow.optics.instances

import arrow.core.*
import arrow.data.*
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
class ListInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = ListK.each<String>().each(),
      aGen = genListK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = ListInstances.each<String>().each(),
      aGen = Gen.list(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = ListK.filterIndex<String>().filter { true },
      aGen = genListK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQListB = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(TraversalLaws.laws(
      traversal = ListInstances.filterIndex<String>().filter { true },
      aGen = Gen.list(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQListB = Eq.any(),
      EQOptionB = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = ListK.index<String>().index(5),
      aGen = genListK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = ListInstances.index<String>().index(5),
      aGen = Gen.list(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

  }
}
