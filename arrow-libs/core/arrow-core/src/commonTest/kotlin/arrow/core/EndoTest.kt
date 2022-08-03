package arrow.core

import arrow.typeclasses.Monoid
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arrow.core.MonoidLaws
import io.kotest.property.arrow.core.endo
import io.kotest.property.arrow.laws.testLaws

class EndoTest : StringSpec() {
  init {
    testLaws(
      MonoidLaws.laws(Monoid.endo(), Arb.endo(Arb.int())) { a, b ->
        a.f(1) == b.f(1)
      }
    )
  }
}
