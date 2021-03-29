package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.typeclasses.At
import io.kotlintest.properties.Gen

class SetInstanceTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lensGen = Gen.string().map { At.set<String>().at(it) },
        aGen = Gen.set(Gen.string()),
        bGen = Gen.bool(),
        funcGen = Gen.functionAToB(Gen.bool()),
      )
    )
  }
}
