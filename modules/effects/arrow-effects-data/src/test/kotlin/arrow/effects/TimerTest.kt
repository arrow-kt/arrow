package arrow.effects

import arrow.Kind
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.milliseconds
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class TimerTest : UnitSpec() {
  init {

    fun <F> Concurrent<F>.test(label: String, EQ: Eq<Kind<F, Boolean>>) {
      "Timer[$label].sleep(10.ms)" {
        val (io, _) = bindingConcurrent {
          val start = !effect { System.currentTimeMillis() }
          !timer().sleep(10.milliseconds)
          val end = !effect { System.currentTimeMillis() }
          (end - start) >= 10L
        }

        io.equalUnderTheLaw(just(true), EQ)
      }

      "Timer[$label].sleep(negative)" {
        timer().sleep((-10).seconds)
          .map { it == Unit }
          .equalUnderTheLaw(just(true), EQ)
      }
    }

    IO.concurrent().test("ForIO", EQ())
  }
}
