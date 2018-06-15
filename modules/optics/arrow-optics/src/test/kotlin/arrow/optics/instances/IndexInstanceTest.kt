package arrow.optics.instances

import arrow.data.*
import arrow.instances.eq
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IndexInstanceTest : UnitSpec() {

  init {
    testLaws(OptionalLaws.laws(
      optional = ListK.index<String>().index(5),
      aGen = genListK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = ListIndexInstance<String>().index(5),
      aGen = Gen.list(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = NonEmptyList.index<String>().index(5),
      aGen = genNonEmptyList(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = SequenceK.index<String>().index(5),
      aGen = genSequenceK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Eq.any(),
      EQA = SequenceK.eq(String.eq())
    ))

    testLaws(OptionalLaws.laws(
      optional = MapK.index<String, Int>().index(Gen.string().generate()),
      aGen = genMapK(Gen.string(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
    ))

    testLaws(OptionalLaws.laws(
      optional = MapIndexInstance<String, Int>().index(Gen.string().generate()),
      aGen = Gen.map(Gen.string(), Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQOptionB = Eq.any(),
      EQA = Eq.any()
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
