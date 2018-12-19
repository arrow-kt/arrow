package arrow.validation.refinedTypes.numeric

import arrow.instances.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.nonPositive.nonPositive
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonPositiveTest : UnitSpec() {
  init {

    "Should create NonPositive for every x <= 0" {
      forAll(LessEqualTest.LessEqualGen(0)) { x: Int ->
        x.nonPositive(Int.order()).isValid
      }
    }

    "Should not create NonPositive for any x > 0" {
      forAll(GreaterTest.GreaterGen(0)) { x: Int ->
        x.nonPositive(Int.order()).isInvalid
      }
    }

  }
}