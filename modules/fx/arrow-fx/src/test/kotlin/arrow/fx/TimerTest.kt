package arrow.fx

import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.test.UnitSpec
import arrow.test.laws.TimerLaws
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class TimerTest : UnitSpec() {
  init {
    testLaws(TimerLaws.laws(IO.async(), Timer.invoke(IO.concurrent()), EQ()))
  }
}
