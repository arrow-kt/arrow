package arrow.optics

import arrow.data.MapK
import arrow.data.SetK
import arrow.data.eq
import arrow.data.monoid
import arrow.instances.*
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genMap
import arrow.test.generators.genMapK
import arrow.test.generators.genSetK
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MapInstancesTest : UnitSpec() {

  init {
    testLaws(IsoLaws.laws(
      iso = mapToMapK(),
      aGen = genMap(Gen.string(), Gen.int()),
      bGen = genMapK(Gen.string(), Gen.int()),
      funcGen = genFunctionAToB(genMapK(Gen.string(), Gen.int())),
      EQA = Eq.any(),
      EQB = MapK.eq(String.eq(), Int.eq()),
      bMonoid = MapK.monoid<String, Int>(Int.monoid())
    ))

    testLaws(IsoLaws.laws(
      iso = mapKToSetK(),
      aGen = genMapK(Gen.string(), Gen.create { Unit }),
      bGen = genSetK(Gen.string()),
      funcGen = genFunctionAToB(genSetK(Gen.string())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = SetK.monoid()
    ))
  }

}