package arrow.validation.refinedTypes.numeric

import arrow.instances.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.nonNegative.nonNegative
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonNegativeTest : UnitSpec() {
  init {

    "Should create NonNegative for every x >= 0" {
      forAll(GreaterEqualTest.GreaterEqualGen(0)) { x: Int ->
        x.nonNegative(Int.order()).isValid
      }
    }

    "Should not create NonNegative for any x < 0" {
      forAll(LessTest.LessThanGen(0)) { x: Int ->
        x.nonNegative(Int.order()).isInvalid
      }
    }

  }
}