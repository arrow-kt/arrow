package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.generators.byte
import arrow.test.generators.short
import arrow.test.laws.SemiringLaws
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

class NumberSemiringTest : UnitSpec() {

    init {
        testLaws(SemiringLaws.laws(Byte.semiring(), Gen.byte(), Byte.eq()))
        testLaws(SemiringLaws.laws(Double.semiring(), Gen.double(), Double.eq()))
        testLaws(SemiringLaws.laws(Int.semiring(), Gen.int(), Int.eq()))
        testLaws(SemiringLaws.laws(Short.semiring(), Gen.short(), Short.eq()))
        testLaws(SemiringLaws.laws(Float.semiring(), Gen.float(), Float.eq()))

        "maybeCombineMultiplicate() should calculate 6 when called on 2 and 3" {
            Int.semiring().run {
                2.maybeCombineMultiplicate(3).shouldBe(6)
            }
        }

        "maybeCombineAddition() should calculate 3 when called on 1 and 2" {
            Int.semiring().run {
                1.maybeCombineAddition(2).shouldBe(3)
            }
        }
    }
}
