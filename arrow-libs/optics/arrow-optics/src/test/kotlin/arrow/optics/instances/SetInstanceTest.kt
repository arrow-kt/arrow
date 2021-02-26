package arrow.optics.instances

import arrow.core.boolean
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.set
import arrow.optics.test.laws.LensLaws
import arrow.optics.typeclasses.At
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class SetInstanceTest : UnitSpec() {
  init {
    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { At.set<String>().at(it) },
        aGen = Gen.set(Gen.string()),
        bGen = Gen.bool(),
        funcGen = Gen.functionAToB(Gen.bool()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Monoid.boolean()
      )
    )
  }
}
