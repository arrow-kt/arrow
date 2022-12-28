package arrow.optics.std

import arrow.optics.Iso
import arrow.optics.test.either
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.validated
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string

class ValidatedTest : StringSpec({

    testLaws(
      "Iso validated to either - ",
      IsoLaws.laws(
        iso = Iso.validatedToEither(),
        aGen = Arb.validated(Arb.string(), Arb.int()),
        bGen = Arb.either(Arb.string(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.string(), Arb.int())),
      )
    )

})
