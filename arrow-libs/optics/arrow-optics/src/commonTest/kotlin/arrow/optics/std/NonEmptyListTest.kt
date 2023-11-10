package arrow.optics.std

import arrow.optics.Lens
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.nonEmptyList
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import kotlin.test.Test

class NonEmptyListTest {

  @Test
  fun headLaws() =
    testLaws(
      LensLaws(
        lens = Lens.nonEmptyListHead(),
        aGen = Arb.nonEmptyList(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test fun tailLaws() =
    testLaws(
      LensLaws(
        lens = Lens.nonEmptyListTail(),
        aGen = Arb.nonEmptyList(Arb.int()),
        bGen = Arb.list(Arb.int()),
        funcGen = Arb.functionAToB(Arb.list(Arb.int())),
      )
    )

}
