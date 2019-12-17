package arrow.validation.refinedTypes.numeric

import arrow.core.extensions.order
import arrow.test.UnitSpec
import arrow.test.generators.greaterThan
import arrow.test.generators.lessEqual
import arrow.validation.refinedTypes.numeric.validated.greater.greater
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class GreaterTest : UnitSpec() {
  init {
    val min = 100

    "Can create Greater for every number greater than the min defined by instace" {
      forAll(Gen.greaterThan(min)) { x: Int ->
        x.greater(Int.order(), min).isValid
      }
    }

    "Can not create Greater for any number less or equal than the min defined by instance" {
      forAll(Gen.lessEqual(min)) { x: Int ->
        x.greater(Int.order(), min).isInvalid
      }
    }
  }
}
