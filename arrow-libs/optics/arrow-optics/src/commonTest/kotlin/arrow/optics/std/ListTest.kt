package arrow.optics.std

import arrow.optics.Iso
import arrow.optics.Optional
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.nonEmptyList
import arrow.optics.test.option
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import kotlin.test.Test

class ListTest {

  @Test
  fun headLaws() =
    testLaws(
      OptionalLaws(
        optional = Optional.listHead(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      ),
      TraversalLaws(
        traversal = Optional.listHead(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test
  fun tailLaws() =
    testLaws(
      OptionalLaws(
        optional = Optional.listTail(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.list(Arb.int()),
        funcGen = Arb.functionAToB(Arb.list(Arb.int())),
      )
    )

  @Test
  fun isoToOptionNelLaws() =
    testLaws(
      IsoLaws(
        iso = Iso.listToOptionNel(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.option(Arb.nonEmptyList(Arb.int())),
        funcGen = Arb.functionAToB(Arb.option(Arb.nonEmptyList(Arb.int()))),
      )
    )

}
