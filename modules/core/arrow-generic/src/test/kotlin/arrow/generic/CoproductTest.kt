package arrow.generic

import arrow.core.None
import arrow.core.Some
import arrow.generic.coproduct2.*
import arrow.generic.coproduct26.Coproduct26
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(KTestJUnitRunner::class)
class CoproductTest : UnitSpec() {
    init {
        "Coproducts should be generated up to 26" {
            var two: Coproduct2<Unit, Unit>? = null
            var twentysix: Coproduct26<Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit>? = null
        }

        "select should return None if value isn't correct type" {
            val coproduct2 = "String".cop<String, Long>()

            coproduct2.select<Long>() shouldBe None
        }

        "select returns Some if value is correct type" {
            val coproduct2 = "String".cop<String, Long>()

            coproduct2.select<String>() shouldBe Some("String")
        }

        "coproductOf(A) should equal cop<A, B>()" {
            "String".cop<String, Long>() shouldBe coproductOf<String, Long>("String")
        }

        "Coproduct2 should map over all types but only operate on the present instance" {
            val coproduct2 = "String".cop<Long, String>()

            coproduct2.map(
                    { 6L },
                    { BigDecimal.ONE }
            ) shouldBe 6L.cop<Long, BigDecimal>()
        }

        "Coproduct2 fold" {
            val coproduct2 = 100L.cop<Long, Int>()

            coproduct2.fold(
                    { "Long$it" },
                    { "Int$it"}
            ) shouldBe "Long100"
        }
    }
}