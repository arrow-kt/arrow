package arrow.optics.instances

import arrow.data.SetK
import arrow.instances.eq
import arrow.instances.setk.eq.eq
import arrow.optics.AndMonoid
import arrow.optics.instances.setk.at.at
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genSetK
import arrow.test.laws.LensLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetInstanceTest : UnitSpec() {

  init {

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
