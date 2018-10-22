package arrow.optics.instances

import arrow.data.*
import arrow.instances.eq
import arrow.instances.sequencek.eq.eq
import arrow.optics.instances.sequencek.index.index
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
      optional = SequenceK.index<String>().index(5),
      aGen = genSequenceK(Gen.string()),
      bGen = Gen.string(),
      funcGen = genFunctionAToB(Gen.string()),
      EQOptionB = Eq.any(),
      EQA = SequenceK.eq(String.eq())
    ))

  }
}
