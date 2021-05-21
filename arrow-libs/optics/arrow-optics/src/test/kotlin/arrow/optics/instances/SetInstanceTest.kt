package arrow.optics.instances

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.LensLaws
import arrow.optics.typeclasses.At
import io.kotest.property.Arb

class SetInstanceTest : UnitSpec() {

  init {

    testLaws(
      LensLaws.laws(
        lensGen = Arb.string().map { At.set<String>().at(it) },
        aGen = Gen.set(Arb.string()),
        bGen = Gen.bool(),
        funcGen = Arb.functionAToB(Gen.bool()),
      )
    )
  }
}
