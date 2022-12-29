package arrow.core

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.testLaws
import arrow.typeclasses.Monoid
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean

class BooleanTest : StringSpec({
    testLaws(
      MonoidLaws.laws(Monoid.boolean(), Arb.boolean())
    )
})
