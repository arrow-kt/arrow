package arrow.extensions

import arrow.core.extensions.eq
import arrow.core.extensions.semiring
import arrow.test.UnitSpec
import arrow.test.laws.SemiringLaws
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class NumberSemiringTest : UnitSpec() {

    companion object {
        private const val A = 2
        private const val B = 3
        private const val C = 4
    }

    init {
        testLaws(SemiringLaws.laws(Byte.semiring(), A.toByte(), B.toByte(), C.toByte(), Byte.eq()))
        testLaws(SemiringLaws.laws(Double.semiring(), A.toDouble(), B.toDouble(), C.toDouble(), Double.eq()))
        testLaws(SemiringLaws.laws(Int.semiring(), A, B, C, Int.eq()))
        testLaws(SemiringLaws.laws(Short.semiring(), A.toShort(), B.toShort(), C.toShort(), Short.eq()))
        testLaws(SemiringLaws.laws(Float.semiring(), A.toFloat(), B.toFloat(), C.toFloat(), Float.eq()))
    }
}
