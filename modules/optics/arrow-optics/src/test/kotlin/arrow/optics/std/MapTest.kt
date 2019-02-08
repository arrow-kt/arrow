package arrow.optics

import arrow.data.MapK
import arrow.data.SetK
import arrow.data.extensions.setk.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
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