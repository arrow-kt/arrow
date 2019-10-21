package arrow.optics

import arrow.core.MapK
import arrow.core.SetK
import arrow.core.extensions.setk.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.genSetK
import arrow.test.generators.mapK
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class MapTest : UnitSpec() {

  init {

    testLaws(IsoLaws.laws(
      iso = MapK.toSetK(),
      aGen = Gen.mapK(Gen.string(), Gen.create { Unit }),
      bGen = Gen.genSetK(Gen.string()),
      funcGen = Gen.functionAToB(Gen.genSetK(Gen.string())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = SetK.monoid()
    ))
  }
}
