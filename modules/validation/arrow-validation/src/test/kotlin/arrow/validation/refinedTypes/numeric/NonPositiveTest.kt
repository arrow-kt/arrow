package arrow.validation.refinedTypes.numeric

import arrow.core.extensions.order
import arrow.test.UnitSpec
import arrow.test.generators.genGreaterThan
import arrow.test.generators.genLessEqual
import arrow.validation.refinedTypes.numeric.validated.nonPositive.nonPositive
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class NonPositiveTest : UnitSpec() {
  init {

    "Should create NonPositive for every x <= 0" {
      forAll(genLessEqual(0)) { x: Int ->
        x.nonPositive(Int.order()).isValid
      }
    }

    "Should not create NonPositive for any x > 0" {
      forAll(genGreaterThan(0)) { x: Int ->
        x.nonPositive(Int.order()).isInvalid
      }
    }

  }
}