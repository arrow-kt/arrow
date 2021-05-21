package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.option
import arrow.optics.Iso
import arrow.optics.Prism
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.PrismLaws
import io.kotest.property.Arb

class OptionTest : UnitSpec() {

  init {

    testLaws(PrismLaws.laws(
      prism = Prism.some(),
      aGen = Gen.option(Arb.int()),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
    ))

    testLaws(PrismLaws.laws(
      prism = Prism.none(),
      aGen = Gen.option(Arb.int()),
      bGen = Gen.create { Unit },
      funcGen = Arb.functionAToB(Gen.create { Unit }),
    ))

    testLaws(IsoLaws.laws(
      iso = Iso.optionToNullable<Int>().reverse(),
      aGen = Arb.int().orNull(),
      bGen = Gen.option(Arb.int()),
      funcGen = Arb.functionAToB(Gen.option(Arb.int()))
    ))

    testLaws(IsoLaws.laws(
      iso = Iso.optionToEither(),
      aGen = Gen.option(Arb.int()),
      bGen = Gen.either(Gen.create { Unit }, Arb.int()),
      funcGen = Arb.functionAToB(Gen.either(Gen.create { Unit }, Arb.int())),
      )
    )
  }
}
