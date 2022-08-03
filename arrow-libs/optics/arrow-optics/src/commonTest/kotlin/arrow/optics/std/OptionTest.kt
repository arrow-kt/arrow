package arrow.optics.std

import arrow.optics.Iso
import arrow.optics.Prism
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arrow.core.either
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.core.option
import io.kotest.property.arrow.laws.testLaws
import io.kotest.property.arrow.optics.IsoLaws
import io.kotest.property.arrow.optics.PrismLaws

class OptionTest : StringSpec() {

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

    testLaws(
      "Iso option to nullable - ",
      IsoLaws.laws(
        iso = Iso.optionToNullable<Int>().reverse(),
        aGen = Arb.int().orNull(),
        bGen = Arb.option(Arb.int()),
        funcGen = Arb.functionAToB(Arb.option(Arb.int()))
      )
    )

    testLaws(
      "Iso option to either - ",
      IsoLaws.laws(
        iso = Iso.optionToEither(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.either(Arb.constant(Unit), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.constant(Unit), Arb.int())),
      )
    )
  }
}
