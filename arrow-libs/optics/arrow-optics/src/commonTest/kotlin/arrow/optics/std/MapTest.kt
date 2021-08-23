package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.Iso
import arrow.optics.test.laws.IsoLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string

class MapTest : UnitSpec() {

  init {
    testLaws(
      IsoLaws.laws(
        iso = Iso.mapToSet(),
        aGen = Arb.map(Arb.string(), Arb.constant(Unit)),
        bGen = Arb.set(Arb.string()),
        funcGen = Arb.functionAToB(Arb.set(Arb.string())),
      )
    )
  }
}
