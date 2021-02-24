package arrow.optics.std

import arrow.core.extensions.monoid
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.IsoLaws
import arrow.optics.toValue
import arrow.optics.typeclasses.Id
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class IdInstancesTest : UnitSpec() {

  init {
    testLaws(
      IsoLaws.laws(
        iso = Id.toValue(),
        aGen = Gen.int().map { Id(it) },
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        bMonoid = Int.monoid()
      )
    )
  }
}
