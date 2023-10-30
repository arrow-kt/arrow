package arrow.core

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.testLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean

class BooleanTest {
    testLaws(
      MonoidLaws("Boolean", true, { x, y -> x && y }, Arb.boolean())
    )
}
