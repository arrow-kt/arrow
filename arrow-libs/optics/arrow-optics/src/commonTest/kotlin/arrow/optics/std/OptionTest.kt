package arrow.optics.std

import arrow.optics.Iso
import arrow.optics.Prism
import arrow.optics.test.either
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.option
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import kotlin.test.Test

class OptionTest {

  @Test
  fun someLaws() =
    testLaws(
      PrismLaws(
        prism = Prism.some(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

  @Test
  fun noneLaws() =
    testLaws(
      PrismLaws(
        prism = Prism.none(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.constant(Unit),
        funcGen = Arb.functionAToB(Arb.constant(Unit)),
      )
    )

  @Test
  fun isoToNullable() =
    testLaws(
      IsoLaws(
        iso = Iso.optionToNullable<Int>().reverse(),
        aGen = Arb.int().orNull(),
        bGen = Arb.option(Arb.int()),
        funcGen = Arb.functionAToB(Arb.option(Arb.int()))
      )
    )

  @Test
  fun isoToEither() =
    testLaws(
      IsoLaws(
        iso = Iso.optionToEither(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.either(Arb.constant(Unit), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.constant(Unit), Arb.int())),
      )
    )

}
