package arrow.effects

import arrow.effects.typeclasses.suspended.Fx
import arrow.effects.typeclasses.suspended.FxOf
import arrow.effects.typeclasses.suspended.fx.concurrent.concurrent
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking
import arrow.effects.typeclasses.suspended.invoke
import arrow.test.UnitSpec
import arrow.test.laws.ConcurrentLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class FxTest : UnitSpec() {

  fun <A> EQ(): Eq<FxOf<A>> = Eq { a, b ->
    unsafe {
      runBlocking {
        Fx {
          try {
            a.invoke() == b.invoke()
          } catch (e: Throwable) {
            val errA = try {
              a.invoke()
              throw IllegalArgumentException()
            } catch (err: Throwable) {
              err
            }
            val errB = try {
              b.invoke()
              throw IllegalStateException()
            } catch (err: Throwable) {
              err
            }
            println("Found errors: $errA and $errB")
            errA == errB
          }
        }
      }
    }
  }

  init {
    testLaws(ConcurrentLaws.laws(Fx.concurrent(), EQ(), EQ(), EQ()))
  }

}