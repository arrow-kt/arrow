package arrow.optics.std

import arrow.core.SetExtensions
import arrow.core.SetK
import arrow.core.extensions.setk.monoid.monoid
import arrow.optics.toSetK
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.genSetK
import arrow.optics.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class SetTest : UnitSpec() {

  init {

    testLaws(
      IsoLaws.laws(
        iso = SetExtensions.toSetK(),
        aGen = Gen.set(Gen.int()),
        bGen = Gen.genSetK(Gen.int()),
        funcGen = Gen.functionAToB(Gen.genSetK(Gen.int())),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = SetK.monoid()
      )
    )
  }
}
