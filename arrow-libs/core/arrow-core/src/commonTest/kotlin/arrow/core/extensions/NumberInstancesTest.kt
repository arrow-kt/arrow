package arrow.core.extensions

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.SemiringLaws
import arrow.core.test.testLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short

class NumberInstancesTest : StringSpec({

  fun <F> testAllLaws(
    zero: F,
    combine: (F, F) -> F,
    one: F,
    combineMultiplicate: (F, F) -> F,
    GEN: Arb<F>,
    eq: (F, F) -> Boolean = { a, b -> a == b }
  ) {
    testLaws(SemiringLaws(zero, combine, one, combineMultiplicate, GEN, eq))
    testLaws(MonoidLaws(zero, combine, GEN, eq))
  }

    testAllLaws(0, { x, y -> (x + y).toByte() }, 1, { x, y -> (x * y).toByte() }, Arb.byte())
    testAllLaws(0, { x, y -> (x + y).toShort() }, 1, { x, y -> (x * y).toShort() }, Arb.short())
    testAllLaws(0, { x, y -> x + y }, 1, { x, y -> x * y }, Arb.int())
    testAllLaws(0, { x, y -> x + y }, 1, { x, y -> x * y }, Arb.long())

})
