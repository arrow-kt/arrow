package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.generators.byteSmall
import arrow.test.generators.doubleSmall
import arrow.test.generators.floatSmall
import arrow.test.generators.intSmall
import arrow.test.generators.longSmall
import arrow.test.generators.shortSmall
import arrow.test.laws.SemiringLaws
import io.kotlintest.properties.Gen

class NumberSemiringTest : UnitSpec() {

  init {
    testLaws(SemiringLaws.laws(Byte.semiring(), Gen.byteSmall(), Byte.eq()))
    testLaws(SemiringLaws.laws(Double.semiring(), Gen.doubleSmall(), Double.eq()))
    testLaws(SemiringLaws.laws(Int.semiring(), Gen.intSmall(), Int.eq()))
    testLaws(SemiringLaws.laws(Short.semiring(), Gen.shortSmall(), Short.eq()))
    testLaws(SemiringLaws.laws(Float.semiring(), Gen.floatSmall(), Float.eq()))
    testLaws(SemiringLaws.laws(Long.semiring(), Gen.longSmall(), Long.eq()))
  }
}
