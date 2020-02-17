package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.generators.byte
import arrow.test.generators.short
import arrow.test.laws.SemiringLaws
import io.kotlintest.properties.Gen

class NumberSemiringTest : UnitSpec() {

  init {
    testLaws(SemiringLaws.laws(Byte.semiring(), Gen.byte(), Byte.eq()))
    testLaws(SemiringLaws.laws(Double.semiring(), Gen.double(), Double.eq()))
    testLaws(SemiringLaws.laws(Int.semiring(), Gen.int(), Int.eq()))
    testLaws(SemiringLaws.laws(Short.semiring(), Gen.short(), Short.eq()))
    testLaws(SemiringLaws.laws(Float.semiring(), Gen.float(), Float.eq()))
    testLaws(SemiringLaws.laws(Long.semiring(), Gen.long(), Long.eq()))
  }
}
