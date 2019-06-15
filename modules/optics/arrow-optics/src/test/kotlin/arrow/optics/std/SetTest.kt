package arrow.optics

import arrow.core.SetK
import arrow.core.SetExtensions
import arrow.core.extensions.setk.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.genSetK
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class SetTest : UnitSpec() {

  init {

    testLaws(IsoLaws.laws(
      iso = SetExtensions.toSetK(),
      aGen = Gen.set(Gen.int()),
      bGen = Gen.genSetK(Gen.int()),
      funcGen = Gen.functionAToB(Gen.genSetK(Gen.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = SetK.monoid()
    ))
  }
}
