package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class SetKTest : UnitSpec() {

  init {
    testLaws(
      MonoidLaws.laws(Monoid.set(), Gen.set(Gen.int()))
    )
  }
}
