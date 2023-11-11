package arrow.core.extensions

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.SemiringLaws
import arrow.core.test.testLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import kotlin.test.Test

class NumberInstancesTest {

  private fun <F> testAllLawsSemiring(
    name: String,
    zero: F,
    combine: (F, F) -> F,
    one: F,
    combineMultiplicate: (F, F) -> F,
    gen: Arb<F>,
    eq: (F, F) -> Boolean = { a, b -> a == b }
  ) = testLaws(SemiringLaws(name, zero, combine, one, combineMultiplicate, gen, eq))

  private fun <F> testAllLawsMonoid(
    name: String,
    zero: F,
    combine: (F, F) -> F,
    gen: Arb<F>,
    eq: (F, F) -> Boolean = { a, b -> a == b }
  ) = testLaws(MonoidLaws(name, zero, combine, gen, eq))

  @Test fun testByteSemiring() =
    testAllLawsSemiring("Byte", 0, { x, y -> (x + y).toByte() }, 1, { x, y -> (x * y).toByte() }, Arb.byte())

  @Test fun testShortSemiring() =
    testAllLawsSemiring("Short", 0, { x, y -> (x + y).toShort() }, 1, { x, y -> (x * y).toShort() }, Arb.short())

  @Test fun testIntSemiring() =
    testAllLawsSemiring("Int", 0, Int::plus, 1, Int::times, Arb.int())

  @Test fun testLongSemiring() =
    testAllLawsSemiring("Long", 0, Long::plus, 1, Long::times, Arb.long())

  @Test fun testByteMonoid() =
    testAllLawsMonoid("Byte", 0, { x, y -> (x + y).toByte() }, Arb.byte())

  @Test fun testShortMonoid() =
    testAllLawsMonoid("Short", 0, { x, y -> (x + y).toShort() }, Arb.short())

  @Test fun testIntMonoid() =
    testAllLawsMonoid("Int", 0, Int::plus, Arb.int())

  @Test fun testLongMonoid() =
    testAllLawsMonoid("Long", 0, Long::plus, Arb.long())
}
