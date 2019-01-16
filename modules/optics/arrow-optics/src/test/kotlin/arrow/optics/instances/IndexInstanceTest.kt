package arrow.optics.instances

import arrow.core.extensions.eq
import arrow.data.*
import arrow.data.extensions.sequencek.eq.eq
import arrow.optics.extensions.sequencek.index.index
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class IndexInstanceTest : UnitSpec() {

  init {

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { SequenceK.index<String>().index(it) },
        aGen = genSequenceK(Gen.string()),
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQOptionB = Eq.any(),
        EQA = SequenceK.eq(String.eq())
      )
    )

  }
}
