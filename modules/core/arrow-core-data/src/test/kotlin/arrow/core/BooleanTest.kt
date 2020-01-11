package arrow.core

import arrow.core.extensions.AndMonoid
import arrow.core.extensions.eq
import arrow.test.UnitSpec
import arrow.test.laws.MonoidLaws
import io.kotlintest.properties.Gen

class BooleanTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(AndMonoid, Gen.bool(), Boolean.eq())
    )
  }
}
