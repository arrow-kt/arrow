package arrow.optics.instances

import arrow.core.Option
import arrow.core.eq
import arrow.data.*
import arrow.instances.eq
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genSequenceK
import arrow.test.laws.OptionalLaws
import arrow.test.laws.TraversalLaws
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class SequenceKInstanceTest : UnitSpec() {

  init {

    testLaws(TraversalLaws.laws(
      traversal = SequenceK.each<String>().each(),
      aGen = genSequenceK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = SequenceK.eq(String.eq()),
      EQOptionB = Option.eq(String.eq()),
      EQListB = ListK.eq(String.eq())
    ))

    testLaws(TraversalLaws.laws(
      traversal = SequenceK.filterIndex<String>().filter { true },
      aGen = genSequenceK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQA = SequenceK.eq(String.eq()),
      EQListB = ListK.eq(String.eq()),
      EQOptionB = Option.eq(String.eq())
    ))

    testLaws(OptionalLaws.laws(
      optional = SequenceK.index<String>().index(5),
      aGen = genSequenceK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Option.eq(String.eq()),
      EQA = SequenceK.eq(String.eq())
    ))

  }
}
