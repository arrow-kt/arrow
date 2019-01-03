package arrow.validation.refinedTypes.numeric

import arrow.core.extensions.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.negative.negative
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NegativeTest : UnitSpec() {
  init {

    "Should create Negative for every x < 0" {
      forAll(LessTest.LessThanGen(0)) { x: Int ->
        x.negative(Int.order()).isValid
      }
    }

    "Should not create Negative for any x >= 0" {
      forAll(GreaterEqualTest.GreaterEqualGen(0)) { x: Int ->
        x.negative(Int.order()).isInvalid
      }
    }

  }
}
