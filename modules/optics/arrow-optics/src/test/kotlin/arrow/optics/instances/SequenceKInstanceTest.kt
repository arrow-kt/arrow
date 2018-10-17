package arrow.optics.instances

import arrow.core.*
import arrow.data.*
import arrow.instances.eq
import arrow.instances.listk.eq.eq
import arrow.instances.option.eq.eq
import arrow.instances.sequencek.eq.eq
import arrow.optics.instances.sequencek.each.each
import arrow.optics.instances.sequencek.filterIndex.filterIndex
import arrow.optics.instances.sequencek.index.index
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
