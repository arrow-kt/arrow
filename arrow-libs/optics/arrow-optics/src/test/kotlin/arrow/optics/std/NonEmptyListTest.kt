package arrow.optics.std

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.optics.Lens
import arrow.optics.test.laws.LensLaws
import io.kotest.property.Arb

class NonEmptyListTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lens = Lens.nonEmptyListHead(),
        aGen = Gen.nonEmptyList(Arb.string()),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.nonEmptyListTail(),
        aGen = Gen.nonEmptyList(Arb.string()),
        bGen = Arb.list(Arb.string()),
        funcGen = Arb.functionAToB(Arb.list(Arb.string())),
      )
    )
  }
}
