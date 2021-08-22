package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean

class BooleanTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(Monoid.boolean(), Arb.boolean())
    )
  }
}
