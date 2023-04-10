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
    name: String,
    zero: F,
    combine: (F, F) -> F,
    one: F,
    combineMultiplicate: (F, F) -> F,
    GEN: Arb<F>,
    eq: (F, F) -> Boolean = { a, b -> a == b }
  ) {
    testLaws(SemiringLaws(name, zero, combine, one, combineMultiplicate, GEN, eq))
    testLaws(MonoidLaws(name, zero, combine, GEN, eq))
  }

    testAllLaws("Byte", 0, { x, y -> (x + y).toByte() }, 1, { x, y -> (x * y).toByte() }, Arb.byte())
    testAllLaws("Short", 0, { x, y -> (x + y).toShort() }, 1, { x, y -> (x * y).toShort() }, Arb.short())
    testAllLaws("Int", 0, Int::plus, 1, Int::times, Arb.int())
    testAllLaws("Long", 0, Long::plus, 1, Long::times, Arb.long())

})
