package arrow.extensions

import arrow.core.extensions.semiring
import arrow.test.UnitSpec
import arrow.test.laws.SemiringLaws
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class NumberSemiringTest : UnitSpec() {

    companion object {
        private const val A = 2
        private const val B = 3
        private const val C = 4
    }

    init {
        testLaws(SemiringLaws.laws(Byte.semiring(), A.toByte(), B.toByte(), C.toByte()))
        testLaws(SemiringLaws.laws(Double.semiring(), A.toDouble(), B.toDouble(), C.toDouble()))
        testLaws(SemiringLaws.laws(Int.semiring(), A, B, C))
        testLaws(SemiringLaws.laws(Short.semiring(), A.toShort(), B.toShort(), C.toShort()))
        testLaws(SemiringLaws.laws(Float.semiring(), A.toFloat(), B.toFloat(), C.toFloat()))

        "maybeCombineMultiplicate() should return one() when called on null receiver" {
            Int.semiring().run {
                null.maybeCombineMultiplicate(1).shouldBe(Int.semiring().one())
            }
        }

        "maybeCombineMultiplicate() should calculate 6 when called on 2 and 3" {
            Int.semiring().run {
                2.maybeCombineMultiplicate(3).shouldBe(6)
            }
        }

        "maybeCombineMultiplicate() should return receiver value when argument is null" {
            Int.semiring().run {
                val receiverValue = 2
                receiverValue.maybeCombineMultiplicate(null).shouldBe(receiverValue)
            }
        }

        "maybeCombineMultiplicate() should return null when receiver and argument are both null" {
            Int.semiring().run {
                val receiverValue = 2
                receiverValue.maybeCombineMultiplicate(null).shouldBe(receiverValue)
            }
        }
    }
}
