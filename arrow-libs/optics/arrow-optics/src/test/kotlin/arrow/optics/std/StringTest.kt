package arrow.optics.std

import arrow.optics.test.generators.char
import arrow.core.test.UnitSpec
import arrow.optics.Iso
import arrow.optics.test.laws.IsoLaws
import io.kotest.property.Arb

class StringTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = Iso.stringToList(),
        aGen = Gen.string(),
        bGen = Arb.list(Gen.char()),
        funcGen = Arb.list(Gen.char()).map { list -> { chars: List<Char> -> list + chars } },
      )
    )
  }
}
