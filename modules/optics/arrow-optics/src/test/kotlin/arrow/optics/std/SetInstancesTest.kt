package arrow.optics

import arrow.data.SetK
import arrow.data.k
import arrow.data.monoid
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetInstancesTest : UnitSpec() {

  init {

    testLaws(IsoLaws.laws(
      iso = setToSetK(),
      aGen = Gen.set(Gen.int()),
      bGen = Gen.create { Gen.set(Gen.int()).generate().k() },
      funcGen = genFunctionAToB(Gen.create { Gen.set(Gen.int()).generate().k() }),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = SetK.monoid()
    ))

  }

}