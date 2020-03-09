package arrow.core

import arrow.core.extensions.AndMonoid
import arrow.core.extensions.eq
import arrow.core.test.UnitSpec
import arrow.core.test.laws.MonoidLaws
import io.kotlintest.properties.Gen

class BooleanTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(AndMonoid, Gen.bool(), Boolean.eq())
    )
  }
}
