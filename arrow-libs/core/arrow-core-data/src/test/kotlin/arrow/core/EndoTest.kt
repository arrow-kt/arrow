package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.endo
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen

class EndoTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(Monoid.endo(), Gen.endo(Gen.int())) { a, b ->
        a.f(1) == b.f(1)
      }
    )
  }
}
