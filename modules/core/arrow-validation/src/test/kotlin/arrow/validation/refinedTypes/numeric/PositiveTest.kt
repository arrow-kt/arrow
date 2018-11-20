package arrow.validation.refinedTypes.numeric

import arrow.instances.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.positive.positive
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PositiveTest : UnitSpec() {
  init {

    "Should create Positive for every x > 0" {
      forAll(GreaterTest.GreaterGen(0)) { x: Int ->
        x.positive(Int.order()).isValid
      }
    }

    "Should not create Positive for any x <= 0" {
      forAll(LessEqualTest.LessEqualGen(0)) { x: Int ->
        x.positive(Int.order()).isInvalid
      }
    }

  }
}
