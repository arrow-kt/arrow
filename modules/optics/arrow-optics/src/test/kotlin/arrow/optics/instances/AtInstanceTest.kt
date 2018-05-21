package arrow.optics.instances

import arrow.core.Option
import arrow.core.monoid
import arrow.data.MapK
import arrow.data.SetK
import arrow.data.at
import arrow.data.eq
import arrow.instances.eq
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.optics.AndMonoid
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genMapK
import arrow.test.generators.genOption
import arrow.test.generators.genSetK
import arrow.test.laws.LensLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class AtInstanceTest : UnitSpec() {

  init {

    testLaws(LensLaws.laws(
      lens = MapK.at<String, Int>().at(Gen.string().generate()),
      aGen = genMapK(Gen.string(), Gen.int()),
      bGen = genOption(Gen.int()),
      funcGen = genFunctionAToB(genOption(Gen.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = Option.monoid(Int.monoid())
    ))

    testLaws(LensLaws.laws(
      lens = MapAtInstance<String, Int>().at(Gen.string().generate()),
      aGen = Gen.map(Gen.string(), Gen.int()),
      bGen = genOption(Gen.int()),
      funcGen = genFunctionAToB(genOption(Gen.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = Option.monoid(Int.semigroup())
    ))

    testLaws(LensLaws.laws(
      lens = SetK.at<String>().at(Gen.string().generate()),
      aGen = genSetK(Gen.string()),
      bGen = Gen.bool(),
      funcGen = genFunctionAToB(Gen.bool()),
      EQA = SetK.eq(String.eq()),
      EQB = Eq.any(),
      MB = AndMonoid
    ))

    testLaws(LensLaws.laws(
      lens = SetAtInstance<String>().at(Gen.string().generate()),
      aGen = Gen.set(Gen.string()),
      bGen = Gen.bool(),
      funcGen = genFunctionAToB(Gen.bool()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = AndMonoid
    ))
  }
}
