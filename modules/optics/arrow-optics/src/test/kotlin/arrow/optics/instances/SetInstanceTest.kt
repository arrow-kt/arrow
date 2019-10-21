package arrow.optics.instances

import arrow.core.extensions.eq
import arrow.core.SetK
import arrow.core.extensions.setk.eq.eq
import arrow.optics.extensions.SetAt
import arrow.optics.extensions.setk.at.at
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.genSetK
import arrow.test.laws.LensLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class SetInstanceTest : UnitSpec() {

  object AndMonoid : Monoid<Boolean> {
    override fun Boolean.combine(b: Boolean): Boolean = this && b
    override fun empty(): Boolean = true
  }

  init {

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { SetK.at<String>().at(it) },
        aGen = Gen.genSetK(Gen.string()),
        bGen = Gen.bool(),
        funcGen = Gen.functionAToB(Gen.bool()),
        EQA = SetK.eq(String.eq()),
        EQB = Eq.any(),
        MB = AndMonoid
      )
    )

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { SetAt<String>().at(it) },
        aGen = Gen.set(Gen.string()),
        bGen = Gen.bool(),
        funcGen = Gen.functionAToB(Gen.bool()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = AndMonoid
      )
    )
  }
}
