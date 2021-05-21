package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.validated
import arrow.optics.Iso
import arrow.optics.test.laws.IsoLaws
import io.kotest.property.Arb

class ValidatedTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = Iso.validatedToEither(),
        aGen = Gen.validated(Arb.string(), Arb.int()),
        bGen = Gen.either(Arb.string(), Arb.int()),
        funcGen = Arb.functionAToB(Gen.either(Arb.string(), Arb.int())),
      )
    )
  }
}
