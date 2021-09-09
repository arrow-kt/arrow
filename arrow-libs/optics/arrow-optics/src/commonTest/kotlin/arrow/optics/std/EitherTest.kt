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

class EitherTest : UnitSpec() {

  init {
    testLaws(
      IsoLaws.laws(
        iso = Iso.eitherToValidated(),
        aGen = Arb.either(Arb.string(), Arb.int()),
        bGen = Arb.validated(Arb.string(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.validated(Arb.string(), Arb.int())),
      )
    )
  }
}
