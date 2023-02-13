package arrow.optics.std

import arrow.optics.Iso
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.testLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

class StringTest : StringSpec({

    testLaws(
      "Iso string to list - ",
      IsoLaws.laws(
        iso = Iso.stringToList(),
        aGen = Arb.string(),
        bGen = Arb.list(Arb.char()),
        funcGen = Arb.list(Arb.char()).map { list -> { chars: List<Char> -> list + chars } },
      )
    )

})
