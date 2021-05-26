package arrow.core.extensions

import arrow.core.eqv
import arrow.core.test.UnitSpec
import arrow.core.test.generators.byteSmall
import arrow.core.test.generators.floatSmall
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.longSmall
import arrow.core.test.generators.shortSmall
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.SemiringLaws
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semiring
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.arbitrary.numericFloats
import io.kotest.property.arbitrary.short
import io.kotest.property.checkAll

class NumberInstancesTest : UnitSpec() {

  fun <F> testAllLaws(
    SG: Semiring<F>,
    M: Monoid<F>,
    GEN: Arb<F>,
    eq: (F, F) -> Boolean = { a, b -> a == b }
  ) {
    testLaws(SemiringLaws.laws(SG, GEN, eq))
    testLaws(MonoidLaws.laws(M, GEN, eq))
  }

  init {
    testAllLaws(Semiring.byte(), Monoid.byte(), Arb.byteSmall())
    testAllLaws(Semiring.short(), Monoid.short(), Arb.shortSmall())
    testAllLaws(Semiring.int(), Monoid.int(), Arb.intSmall())
    testAllLaws(Semiring.long(), Monoid.long(), Arb.longSmall())
    MonoidLaws.laws(Monoid.float(), Arb.floatSmall(), Float::eqv)
    // TODO Semiring laws failing with == or Float::eqv
    // testAllLaws(Semiring.float(), Monoid.float(), Arb.floatSmall(), Float::eqv)
    // TODO Semiring/Monoid laws failing with == or Double::eqv
    // testAllLaws(Semiring.double(), Monoid.double(), Arb.doubleSmall(), Double::eqv)

    /** Semigroup specific instance check */

    "should semigroup with the instance passed - int" {
      checkAll { value: Int ->
        val seen = Monoid.int().run { value.combine(value) }
        val expected = value + value

        expected shouldBe seen
      }
    }

    "should semigroup with the instance passed - float" {
      checkAll(Arb.numericFloats()) { value: Float ->
        val seen = Monoid.float().run { value.combine(value) }
        val expected = value + value

        expected shouldBe seen
      }
    }

    "should semigroup with the instance passed - double" {
      checkAll(Arb.numericDoubles()) { value: Double ->
        val seen = Monoid.double().run { value.combine(value) }
        val expected = value + value

        expected shouldBe seen
      }
    }

    "should semigroup with the instance passed - long" {
      checkAll { value: Long ->
        val seen = Monoid.long().run { value.combine(value) }
        val expected = value + value

        expected shouldBe seen
      }
    }

    "should semigroup with the instance passed - short" {
      checkAll(Arb.short()) { value: Short ->
        val seen = Monoid.short().run { value.combine(value) }
        val expected = (value + value).toShort()

        expected shouldBe seen
      }
    }

    "should semigroup with the instance passed - byte" {
      checkAll(Arb.byte()) { value: Byte ->
        val seen = Monoid.byte().run { value.combine(value) }
        val expected = (value + value).toByte()

        expected shouldBe seen
      }
    }
  }
}
