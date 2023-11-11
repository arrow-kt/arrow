package arrow.core

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.testLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import kotlin.test.Test

class BooleanTest{
  @Test fun monoidLaws() =
    testLaws(
      MonoidLaws("Boolean", true, { x, y -> x && y }, Arb.boolean())
    )
}
