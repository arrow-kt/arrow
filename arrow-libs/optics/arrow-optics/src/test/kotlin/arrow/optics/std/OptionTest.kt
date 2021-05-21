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
      aGen = Gen.option(Gen.int()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
    ))

    testLaws(PrismLaws.laws(
      prism = Prism.none(),
      aGen = Gen.option(Gen.int()),
      bGen = Gen.create { Unit },
      funcGen = Gen.functionAToB(Gen.create { Unit }),
    ))

    testLaws(IsoLaws.laws(
      iso = Iso.optionToNullable<Int>().reverse(),
      aGen = Gen.int().orNull(),
      bGen = Gen.option(Gen.int()),
      funcGen = Gen.functionAToB(Gen.option(Gen.int()))
    ))

    testLaws(IsoLaws.laws(
      iso = Iso.optionToEither(),
      aGen = Gen.option(Gen.int()),
      bGen = Gen.either(Gen.create { Unit }, Gen.int()),
      funcGen = Gen.functionAToB(Gen.either(Gen.create { Unit }, Gen.int())),
      )
    )
  }
}
