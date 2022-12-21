package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.option
import arrow.optics.Prism
import arrow.optics.test.laws.PrismLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull

class OptionTest : UnitSpec() {

  init {

    testLaws(
      "Prism some - ",
      PrismLaws.laws(
        prism = Prism.some(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Prism none - ",
      PrismLaws.laws(
        prism = Prism.none(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.constant(Unit),
        funcGen = Arb.functionAToB(Arb.constant(Unit)),
      )
    )
  }
}
