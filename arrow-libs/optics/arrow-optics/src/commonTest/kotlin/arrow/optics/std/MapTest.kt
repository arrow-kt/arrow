package arrow.optics.std

import arrow.optics.Iso
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.laws.testLaws
import io.kotest.property.arrow.optics.IsoLaws

class MapTest : StringSpec() {

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
