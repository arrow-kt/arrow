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
        aGen = Gen.nonEmptyList(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.nonEmptyListTail(),
        aGen = Gen.nonEmptyList(Gen.string()),
        bGen = Arb.list(Gen.string()),
        funcGen = Gen.functionAToB(Arb.list(Gen.string())),
      )
    )
  }
}
