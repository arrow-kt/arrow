package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.Iso
import arrow.optics.test.laws.IsoLaws
import io.kotlintest.properties.Gen

class MapTest : UnitSpec() {

  init {
    testLaws(IsoLaws.laws(
      iso = Iso.mapToSet(),
      aGen = Gen.map(Gen.string(), Gen.create { Unit }),
      bGen = Gen.set(Gen.string()),
      funcGen = Gen.functionAToB(Gen.set(Gen.string())),
    ))
  }
}
