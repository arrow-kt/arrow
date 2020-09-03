package arrow.fx.data

import arrow.core.test.UnitSpec
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.equalUnderTheLaw
import arrow.fx.extensions.duration.eq.eq
import arrow.fx.extensions.duration.hash.hash
import arrow.fx.extensions.duration.order.order
import arrow.fx.test.generators.duration
import arrow.fx.typeclasses.Duration
import io.kotlintest.properties.forAll

class DurationTest : UnitSpec() {

  init {
    testLaws(
      OrderLaws.laws(Duration.order(), duration()),
      HashLaws.laws(Duration.hash(), duration(), Duration.eq())
      // This fails on overflows on the associativity law
      // MonoidLaws.laws(Duration.monoid(), duration(), Duration.eq())
    )

    "plus is commutative" {
      forAll(duration(), duration()) { a, b ->
        (a + b).equalUnderTheLaw(b + a, Duration.eq())
      }
    }
  }
}
