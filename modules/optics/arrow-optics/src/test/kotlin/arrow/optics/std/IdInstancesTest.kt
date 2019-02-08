package arrow.optics

import arrow.core.Id
import arrow.core.extensions.monoid
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.laws.IsoLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class IdInstancesTest : UnitSpec() {

  init {
    testLaws(IsoLaws.laws(
      iso = Id.toValue(),
      aGen = Gen.int().map { Id(it) },
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      bMonoid = Int.monoid()
    ))
  }

}
