package arrow.optics.std

import arrow.optics.Iso
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.testLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import kotlin.test.Test

class MapTest {

  @Test
  fun mapToSetLaws() = testLaws(
    IsoLaws(
      iso = Iso.mapToSet(),
      aGen = Arb.map(Arb.string(), Arb.constant(Unit), maxSize = 20),
      bGen = Arb.set(Arb.string(), range = 0 .. 20),
      funcGen = Arb.functionAToB(Arb.set(Arb.string(), range = 0 .. 20)),
    )
  )

}
