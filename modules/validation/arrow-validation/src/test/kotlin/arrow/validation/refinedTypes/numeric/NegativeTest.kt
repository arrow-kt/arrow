package arrow.validation.refinedTypes.numeric

import arrow.core.extensions.order
import arrow.test.UnitSpec
import arrow.test.generators.greaterEqual
import arrow.test.generators.lessThan
import arrow.validation.refinedTypes.numeric.validated.negative.negative
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class NegativeTest : UnitSpec() {
  init {

    "Should create Negative for every x < 0" {
      forAll(Gen.lessThan(0)) { x: Int ->
        x.negative(Int.order()).isValid
      }
    }

    "Should not create Negative for any x >= 0" {
      forAll(Gen.greaterEqual(0)) { x: Int ->
        x.negative(Int.order()).isInvalid
      }
    }
  }
}
