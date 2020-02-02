package arrow.fx.data

import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.test.generators.timeUnit
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
class DurationTest : UnitSpec() {

  init {
    "plus should be commutative" {
      forAll(Gen.intSmall(), Gen.timeUnit(), Gen.intSmall(), Gen.timeUnit()) { i, u, j, v ->
        val a = i.toDuration(u)
        val b = j.toDuration(v)
        a + b == b + a
      }
    }
  }
}
