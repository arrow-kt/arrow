package arrow.effects

import arrow.Kind
import arrow.core.getOrHandle
import arrow.core.left
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.effects.typeclasses.suspended.*
import arrow.effects.typeclasses.suspended.rio.applicativeError.attempt
import arrow.effects.typeclasses.suspended.rio.applicativeError.raiseError
import arrow.effects.typeclasses.suspended.rio.concurrent.concurrent
import arrow.effects.typeclasses.suspended.rio.fx.fx
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking
import arrow.test.UnitSpec
import arrow.test.laws.ConcurrentLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class RIOTest : UnitSpec() {

  fun <A> EQ(): Eq<Kind<RIOPartialOf<Int, Throwable>, A>> = Eq { a, b ->
    unsafe {
      runBlocking {
        RIO<Int, Throwable, Boolean> {
          try {
            (a.fix().fa(this) == b.fix().fa(this)).right()
          } catch (e: Throwable) {
            val errA = try {
              a.fix().fa(this)
              throw IllegalArgumentException()
            } catch (err: Throwable) {
              err.nonFatalOrThrow()
            }
            val errB = try {
              b.fix().fa(this)
              throw IllegalStateException()
            } catch (err: Throwable) {
              err.nonFatalOrThrow()
            }
            println("Found errors: $errA and $errB")
            (errA.message == errB.message).right()
          }
        }.toFx(0)
      }.getOrHandle { throw it }
    }
  }

  init {
    "Rio fx blocks" {
      val program: RIO<Int, TestUserError, Int> =
        fx {
          val a = !effect { 1 }
          val b = !effect { 1 }
          a + b
        }
      unsafe { runBlocking { program.attempt().toFx(0) } } shouldBe 2.right().right()
    }

    "Rio fx error" {
      val program: RIO<Int, TestUserError, Int> =
        fx {
          val a = !effect { 1 }
          val b: Int = !TestUserError.raiseError<Int, TestUserError, Int>()
          a + b
        }
      unsafe { runBlocking { program.attempt().toFx(0) } } shouldBe TestUserError.left().right()
    }

    testLaws(ConcurrentLaws.laws(RIO.concurrent(), EQ(), EQ(), EQ()))
  }

}
