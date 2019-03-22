package arrow.effects

import arrow.Kind
import arrow.core.getOrHandle
import arrow.core.left
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.effects.extensions.catchfx.applicativeError.attempt
import arrow.effects.extensions.catchfx.applicativeError.raiseError
import arrow.effects.extensions.catchfx.concurrent.concurrent
import arrow.effects.extensions.catchfx.fx.fx
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.error.CatchFx
import arrow.effects.suspended.error.CatchFxPartialOf
import arrow.effects.suspended.error.toFx
import arrow.effects.suspended.error.fix
import arrow.test.UnitSpec
import arrow.test.laws.ConcurrentLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class CatchFxTest : UnitSpec() {

  fun <A> EQ(): Eq<Kind<CatchFxPartialOf<Throwable>, A>> = Eq { a, b ->
    unsafe {
      runBlocking {
        CatchFx<Throwable, Boolean> {
          try {
            (a.fix().fa() == b.fix().fa()).right()
          } catch (e: Throwable) {
            val errA = try {
              a.fix().fa()
              throw IllegalArgumentException()
            } catch (err: Throwable) {
              err.nonFatalOrThrow()
            }
            val errB = try {
              b.fix().fa()
              throw IllegalStateException()
            } catch (err: Throwable) {
              err.nonFatalOrThrow()
            }
            println("Found errors: $errA and $errB")
            (errA.message == errB.message).right()
          }
        }.toFx()
      }.getOrHandle { throw it }
    }
  }

  init {
    "CatchFx fx blocks" {
      val program: CatchFx<TestUserError, Int> =
        fx {
          val a = !effect { 1 }
          val b = !effect { 1 }
          a + b
        }
      unsafe { runBlocking { program.attempt().toFx() } } shouldBe 2.right().right()
    }

    "CatchFx fx error" {
      val program: CatchFx<TestUserError, Int> =
        fx {
          val a = !effect { 1 }
          val b: Int = !TestUserError.raiseError<TestUserError, Int>()
          a + b
        }
      unsafe { runBlocking { program.attempt().toFx() } } shouldBe TestUserError.left().right()
    }

    testLaws(ConcurrentLaws.laws(CatchFx.concurrent(), EQ(), EQ(), EQ()))
  }

}

object TestUserError