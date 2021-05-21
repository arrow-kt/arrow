package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.Iso
import arrow.optics.test.laws.IsoLaws
import io.kotest.property.Arb

class MapTest : UnitSpec() {

  init {
    testLaws(IsoLaws.laws(
      iso = Iso.mapToSet(),
      aGen = Gen.map(Arb.string(), Gen.create { Unit }),
      bGen = Gen.set(Arb.string()),
      funcGen = Arb.functionAToB(Gen.set(Arb.string())),
    ))
  }
}
