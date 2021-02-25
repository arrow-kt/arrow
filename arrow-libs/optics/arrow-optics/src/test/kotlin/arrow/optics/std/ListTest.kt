package arrow.optics.std

import arrow.core.nonEmptyList
import arrow.core.option
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.option
import arrow.optics.Iso
import arrow.optics.Optional
import arrow.optics.listHead
import arrow.optics.listTail
import arrow.optics.listToOptionNel
import arrow.optics.test.laws.IsoLaws
import arrow.optics.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotlintest.properties.Gen

class ListTest : UnitSpec() {

  init {

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listHead(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listTail(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.list(Gen.int()),
        funcGen = Gen.functionAToB(Gen.list(Gen.int())),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      IsoLaws.laws(
        iso = Iso.listToOptionNel(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.option(Gen.nonEmptyList(Gen.int())),
        funcGen = Gen.functionAToB(Gen.option(Gen.nonEmptyList(Gen.int()))),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = Monoid.option(Semigroup.nonEmptyList())
      )
    )
  }
}
