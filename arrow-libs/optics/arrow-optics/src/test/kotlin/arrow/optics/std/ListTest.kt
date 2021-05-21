package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.option
import arrow.optics.Iso
import arrow.optics.Optional
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.OptionalLaws
import io.kotest.property.Arb

class ListTest : UnitSpec() {

  init {

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listHead(),
        aGen = Arb.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
      )
    )

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listTail(),
        aGen = Arb.list(Gen.int()),
        bGen = Arb.list(Gen.int()),
        funcGen = Gen.functionAToB(Arb.list(Gen.int())),
      )
    )

    testLaws(IsoLaws.laws(
      iso = Iso.listToOptionNel(),
      aGen = Arb.list(Gen.int()),
      bGen = Gen.option(Gen.nonEmptyList(Gen.int())),
      funcGen = Gen.functionAToB(Gen.option(Gen.nonEmptyList(Gen.int()))),
    ))
  }
}
