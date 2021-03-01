package arrow.optics.std

import arrow.core.Validated
import arrow.optics.toEither
import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.validated
import arrow.optics.test.laws.IsoLaws
import io.kotlintest.properties.Gen

class ValidatedTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = Validated.toEither(),
        aGen = Gen.validated(Gen.string(), Gen.int()),
        bGen = Gen.either(Gen.string(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.either(Gen.string(), Gen.int())),
      )
    )
  }
}
