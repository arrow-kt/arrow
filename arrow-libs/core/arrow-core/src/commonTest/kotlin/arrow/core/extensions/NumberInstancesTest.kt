package arrow.core.extensions

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.SemiringLaws
import arrow.core.test.testLaws
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semiring
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.checkAll

class NumberInstancesTest : StringSpec({

  fun <F> testAllLaws(
    SG: Semiring<F>,
    M: Monoid<F>,
    GEN: Arb<F>,
    eq: (F, F) -> Boolean = { a, b -> a == b }
  ) {
    testLaws(SemiringLaws.laws(SG, GEN, eq))
    testLaws(MonoidLaws.laws(M, GEN, eq))
  }

    testAllLaws(Semiring.byte(), Monoid.byte(), Arb.byte())
    testAllLaws(Semiring.short(), Monoid.short(), Arb.short())
    testAllLaws(Semiring.int(), Monoid.int(), Arb.int())
    testAllLaws(Semiring.long(), Monoid.long(), Arb.long())

    /** Semigroup specific instance check */

    "should semigroup with the instance passed - int" {
      checkAll(Arb.int()) { value: Int ->
        val seen = Monoid.int().run { value.combine(value) }
        val expected = value + value

        expected shouldBe seen
      }
    }

    "should semigroup with the instance passed - long" {
      checkAll(Arb.long()) { value: Long ->
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
})
