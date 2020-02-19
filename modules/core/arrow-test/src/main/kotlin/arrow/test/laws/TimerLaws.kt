package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.fx.Timer
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.milliseconds
import arrow.fx.typeclasses.seconds
import arrow.test.generators.intSmall
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

object TimerLaws {

  // TODO move to Arrow-effects and figure out a acceptable API for timeMilis/timeNano for MPP.
  interface Clock<F> {
    fun timeMillis(): Kind<F, Long>
    fun timeNano(): Kind<F, Long>

    companion object {
      operator fun <F> invoke(AS: Async<F>): Clock<F> = object : Clock<F> {
        override fun timeMillis(): Kind<F, Long> =
          AS.effect { System.currentTimeMillis() }

        override fun timeNano(): Kind<F, Long> =
          AS.effect { System.nanoTime() }
      }
    }
  }

  fun <F> laws(AS: Async<F>, T: Timer<F>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Boolean.eq())

    return listOf(
      Law("Timer Laws: sleep should last specified time") { AS.sleepShouldLastSpecifiedTime(T, Clock(AS), EQ) },
      Law("Timer Laws: negative sleep should be immediate") { AS.negativeSleepShouldBeImmediate(T, EQ) }
    )
  }

  fun <F> Async<F>.sleepShouldLastSpecifiedTime(
    T: Timer<F>,
    C: Clock<F>,
    EQ: Eq<Kind<F, Boolean>>
  ) = forFew(25, Gen.intSmall()) {
    val length = 100L
    val lhs = fx.async {
      val start = !C.timeNano()
      !T.sleep(length.milliseconds)
      val end = !C.timeNano()
      (end - start) >= length
    }

    lhs.equalUnderTheLaw(just(true), EQ)
  }

  fun <F> Async<F>.negativeSleepShouldBeImmediate(
    T: Timer<F>,
    EQ: Eq<Kind<F, Boolean>>
  ) {
    T.sleep((-10).seconds)
      .map { it == Unit }
      .equalUnderTheLaw(just(true), EQ)
  }
}
