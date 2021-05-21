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
        aGen = Arb.list(Arb.int()),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listTail(),
        aGen = Arb.list(Arb.int()),
        bGen = Arb.list(Arb.int()),
        funcGen = Arb.functionAToB(Arb.list(Arb.int())),
      )
    )

    testLaws(IsoLaws.laws(
      iso = Iso.listToOptionNel(),
      aGen = Arb.list(Arb.int()),
      bGen = Gen.option(Gen.nonEmptyList(Arb.int())),
      funcGen = Arb.functionAToB(Gen.option(Gen.nonEmptyList(Arb.int()))),
    ))
  }
}
