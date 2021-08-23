package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.validated
import arrow.optics.Iso
import arrow.optics.test.laws.IsoLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string

class ValidatedTest : UnitSpec() {

  init {

    testLaws(
      "Iso validated to either - ",
      IsoLaws.laws(
        iso = Iso.validatedToEither(),
        aGen = Arb.validated(Arb.string(), Arb.int()),
        bGen = Arb.either(Arb.string(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.string(), Arb.int())),
      )
    )
  }
}
