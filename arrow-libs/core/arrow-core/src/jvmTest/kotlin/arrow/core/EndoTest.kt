package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.endo
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class EndoTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(Monoid.endo(), Arb.endo(Arb.int())) { a, b ->
        a.f(1) == b.f(1)
      }
    )
  }
}
