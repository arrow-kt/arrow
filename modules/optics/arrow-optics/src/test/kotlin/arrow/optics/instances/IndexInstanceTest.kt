package arrow.optics.instances

import arrow.core.extensions.eq
import arrow.core.SequenceK
import arrow.core.extensions.sequencek.eq.eq
import arrow.optics.extensions.sequencek.index.index
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.sequenceK
import arrow.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class IndexInstanceTest : UnitSpec() {

  init {

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { SequenceK.index<String>().index(it) },
        aGen = Gen.sequenceK(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = SequenceK.eq(String.eq())
      )
    )
  }
}
