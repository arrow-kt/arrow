package arrow.optics

import arrow.data.MapK
import arrow.data.SetK
import arrow.instances.setk.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genMapK
import arrow.test.generators.genSetK
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapTest : UnitSpec() {

  init {

    testLaws(IsoLaws.laws(
      iso = MapK.toSetK(),
      aGen = genMapK(Gen.string(), Gen.create { Unit }),
      bGen = genSetK(Gen.string()),
      funcGen = genFunctionAToB(genSetK(Gen.string())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = SetK.monoid()
    ))
  }

}