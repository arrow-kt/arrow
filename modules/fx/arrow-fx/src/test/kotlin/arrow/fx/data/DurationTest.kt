package arrow.fx.data

import arrow.fx.typeclasses.Duration
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.test.generators.timeUnit
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class DurationTest : UnitSpec() {

  init {
    "plus should be commutative" {
      forAll(Gen.intSmall(), Gen.timeUnit(), Gen.intSmall(), Gen.timeUnit()) { i, u, j, v ->
        val a = Duration(i.toLong(), u)
        val b = Duration(j.toLong(), v)
        a + b == b + a
      }
    }
  }
}
