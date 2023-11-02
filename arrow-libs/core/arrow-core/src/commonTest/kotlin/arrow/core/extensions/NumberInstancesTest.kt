package arrow.core.extensions

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.SemiringLaws
import arrow.core.test.testLawsCommon
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import kotlin.test.Test

class NumberInstancesTest {

  fun <F> testAllLaws(
    name: String,
    zero: F,
    combine: (F, F) -> F,
    one: F,
    combineMultiplicate: (F, F) -> F,
    GEN: Arb<F>,
    eq: (F, F) -> Boolean = { a, b -> a == b }
  ) {
    testLawsCommon(SemiringLaws(name, zero, combine, one, combineMultiplicate, GEN, eq))
    testLawsCommon(MonoidLaws(name, zero, combine, GEN, eq))
  }

  @Test fun testByteLaws() =
    testAllLaws("Byte", 0, { x, y -> (x + y).toByte() }, 1, { x, y -> (x * y).toByte() }, Arb.byte())

  @Test fun testShortLaws() =
    testAllLaws("Short", 0, { x, y -> (x + y).toShort() }, 1, { x, y -> (x * y).toShort() }, Arb.short())

  @Test fun testIntLaws() =
    testAllLaws("Int", 0, Int::plus, 1, Int::times, Arb.int())

  @Test fun testLongLaws() =
    testAllLaws("Long", 0, Long::plus, 1, Long::times, Arb.long())
}
