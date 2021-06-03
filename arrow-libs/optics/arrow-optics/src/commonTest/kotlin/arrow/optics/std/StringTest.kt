package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.optics.Iso
import arrow.optics.test.laws.IsoLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

class StringTest : UnitSpec() {

  init {

    testLaws(
      "Iso string to list - ",
      IsoLaws.laws(
        iso = Iso.stringToList(),
        aGen = Arb.string(),
        bGen = Arb.list(Arb.char()),
        funcGen = Arb.list(Arb.char()).map { list -> { chars: List<Char> -> list + chars } },
      )
    )
  }
}
