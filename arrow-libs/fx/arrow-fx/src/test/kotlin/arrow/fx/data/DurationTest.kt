package arrow.fx.data

import arrow.core.test.UnitSpec
import io.kotlintest.properties.forAll
import arrow.fx.test.generators.duration

class DurationTest : UnitSpec() {

  init {
    "plus should be commutative" {
      forAll(duration(), duration()) { a, b ->
        a + b == b + a
      }
    }

    "comparison should correct in both directions" {
      forAll(duration(), duration()) { a, b ->
        a < b == b > a
      }
    }
  }
}
