package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class BooleanTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(Monoid.boolean(), Gen.bool())
    )
  }
}
