package arrow.optics.std

import arrow.optics.Iso
import arrow.optics.Prism
import arrow.optics.test.either
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.testLaws
import arrow.optics.test.option
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull

class OptionTest : StringSpec({

    testLaws(
      "Prism some - ",
      PrismLaws(
        prism = Prism.some(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Prism none - ",
      PrismLaws(
        prism = Prism.none(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.constant(Unit),
        funcGen = Arb.functionAToB(Arb.constant(Unit)),
      )
    )

    testLaws(
      "Iso option to nullable - ",
      IsoLaws(
        iso = Iso.optionToNullable<Int>().reverse(),
        aGen = Arb.int().orNull(),
        bGen = Arb.option(Arb.int()),
        funcGen = Arb.functionAToB(Arb.option(Arb.int()))
      )
    )

    testLaws(
      "Iso option to either - ",
      IsoLaws(
        iso = Iso.optionToEither(),
        aGen = Arb.option(Arb.int()),
        bGen = Arb.either(Arb.constant(Unit), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.constant(Unit), Arb.int())),
      )
    )

})
