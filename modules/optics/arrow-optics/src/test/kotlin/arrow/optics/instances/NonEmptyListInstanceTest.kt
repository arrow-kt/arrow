package arrow.optics.instances

import arrow.core.Option
import arrow.core.eq
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
class NonEmptyListInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = NonEmptyList.each<String>().each(),
      aGen = genNonEmptyList(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Eq.any(),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(TraversalLaws.laws(
      traversal = NonEmptyList.filterIndex<String>().filter { true },
      aGen = genNonEmptyList(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(OptionalLaws.laws(
      optional = NonEmptyList.index<String>().index(5),
      aGen = genNonEmptyList(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

  }
}
