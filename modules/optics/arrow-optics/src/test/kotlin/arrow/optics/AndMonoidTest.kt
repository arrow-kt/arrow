package arrow.optics

import arrow.core.extensions.eq
import arrow.test.UnitSpec
import arrow.test.laws.MonoidLaws
import io.kotlintest.properties.Gen

class AndMonoidTest : UnitSpec() {
  init {
      testLaws(
        MonoidLaws.laws(AndMonoid, Gen.bool(), Boolean.eq())
      )
  }
}
