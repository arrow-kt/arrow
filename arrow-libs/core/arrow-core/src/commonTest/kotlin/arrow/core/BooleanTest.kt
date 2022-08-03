package arrow.core

import arrow.typeclasses.Monoid
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arrow.core.MonoidLaws
import io.kotest.property.arrow.laws.testLaws

class BooleanTest : StringSpec() {
  init {
    testLaws(
      MonoidLaws.laws(Monoid.boolean(), Arb.boolean())
    )
  }
}
