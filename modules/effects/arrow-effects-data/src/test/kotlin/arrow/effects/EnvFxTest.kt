package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.suspended.env.*
import arrow.effects.suspended.env.envfx.applicativeError.attempt
import arrow.effects.suspended.env.envfx.applicativeError.raiseError
import arrow.effects.suspended.env.envfx.concurrent.concurrent
import arrow.effects.suspended.env.envfx.fx.fx
import arrow.effects.suspended.fx.fx.unsafeRun.runBlocking
import arrow.test.UnitSpec
import arrow.test.laws.ConcurrentLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class EnvFxTest : UnitSpec() {

  fun <A> EQ(): Eq<Kind<EnvFxPartialOf<Int, Throwable>, A>> = Eq { a, b ->
    unsafe {
      runBlocking {
        EnvFx<Int, Throwable, Boolean> {
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
      val program: EnvFx<Int, TestUserError, Int> =
        fx {
          val a = !effect { 1 }
          val b = !effect { 1 }
          a + b
        }
      unsafe { runBlocking { program.attempt().toFx(0) } } shouldBe 2.right().right()
    }

    "Rio fx error" {
      val program: EnvFx<Int, TestUserError, Int> =
        fx {
          val a = !effect { 1 }
          val b = !TestUserError.raiseError<Int, TestUserError, Int>()
          a + b
        }
      unsafe { runBlocking { program.attempt().toFx(0) } } shouldBe TestUserError.left().right()
    }

    "Rio fx deps" {
      fun <R> program(): EnvFx<R, CustomError, Unit>
        where R : Service1, R : Service2 =
        env {
          fx {
            !effect { foo() }
            !effect { bar() }
          }
        }

      val result: Either<CustomError, Unit> = unsafe { runBlocking { program<Module>().toFx(Module.impl()) } }
      result shouldBe Unit.right()
    }

    testLaws(ConcurrentLaws.laws(EnvFx.concurrent(), EQ(), EQ(), EQ()))
  }

}