package arrow.optics

import arrow.data.SetK
import arrow.core.SetInstances
import arrow.instances.setk.monoid.monoid
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genSetK
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetTest : UnitSpec() {

  init {

    testLaws(IsoLaws.laws(
      iso = SetInstances.toSetK(),
      aGen = Gen.set(Gen.int()),
      bGen = genSetK(Gen.int()),
      funcGen = genFunctionAToB(genSetK(Gen.int())),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = SetK.monoid()
    ))

  }

}