package arrow.effects

import arrow.effects.typeclasses.suspended.Fx
import arrow.effects.typeclasses.suspended.FxOf
import arrow.effects.typeclasses.suspended.fx.unsafeRun.runBlocking
import arrow.effects.typeclasses.suspended.invoke
import arrow.test.UnitSpec
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

            errA == errB
          }
        }
      }
    }
  }

  val dummy = object : RuntimeException("dummy") {
    override fun fillInStackTrace(): Throwable =
      this
  }

  fun loopNotHappy(size: Int, i: Int): Fx<Int> {
    println("loop($size, $i)")
    return if (i < size) {
      Fx.raiseError<Int>(dummy)
        .map { it + 1 }
        .attempt()
        .flatMap { either ->
          either.fold({ loopNotHappy(size, i + 1) }, { Fx.just(it) })
        }
    } else Fx.just(1)
  }

  init {
    //testLaws(ConcurrentLaws.laws(Fx.concurrent(), EQ(), EQ(), EQ()))
    "not happy" {
      val result = unsafe { runBlocking { loopNotHappy(1000, 0) } }
      println(result)
    }
  }

}