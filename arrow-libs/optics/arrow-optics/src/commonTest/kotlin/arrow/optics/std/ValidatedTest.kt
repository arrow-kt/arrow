package arrow.optics.std

import arrow.optics.Iso
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.either
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.core.validated
import io.kotest.property.arrow.laws.testLaws
import io.kotest.property.arrow.optics.IsoLaws

class ValidatedTest : StringSpec() {

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
