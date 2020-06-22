package arrow.fx

import arrow.core.test.laws.equalUnderTheLaw
import arrow.fx.internal.TimeoutException
import arrow.fx.typeclasses.milliseconds
import arrow.fx.test.eq.eq
import arrow.fx.test.laws.shouldBeEq
import arrow.fx.test.laws.shouldNotBeEq
import io.kotlintest.shouldThrow

class EqTest : ArrowFxSpec() {

  init {
    "Should pass pure equal values" {
      IO.just(true).shouldBeEq(IO.just(true), IO.eq())
    }

    "Should fail for pure non-equal values" {
      IO.just(true).shouldNotBeEq(IO.just(false), IO.eq())
    }

    "Times out" {
      shouldThrow<TimeoutException> {
        IO.never.equalUnderTheLaw(IO.just(1), IO.eq(timeout = 10.milliseconds))
      }
    }
  }
}
