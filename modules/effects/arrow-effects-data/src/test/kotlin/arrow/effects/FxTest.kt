package arrow.effects

import arrow.effects.extensions.fx.concurrent.concurrent
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.FxOf
import arrow.effects.suspended.fx.not
import arrow.test.UnitSpec
import arrow.test.laws.ConcurrentLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class FxTest : UnitSpec() {

  init {
//    testLaws(ConcurrentLaws.laws(Fx.concurrent(), EQ(), EQ(), EQ()))

    "Fx `map` stack safe" {
      val size = 500000
      fun mapStackSafe(): Fx<Int> =
        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.map { it + 1 } }
      unsafe { runBlocking { Fx { mapStackSafe()() } } } shouldBe size
    }

//    "Fx `flatMap` stack safe" {
//      val size = 500000
//      fun flatMapStackSafe(): Fx<Int> =
//        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.flatMap { Fx.just(it + 1) } }
//      unsafe { runBlocking { Fx { flatMapStackSafe()() } } } shouldBe size
//    }

  }

  fun <A> EQ(): Eq<FxOf<A>> = Eq { a, b ->
    unsafe {
      runBlocking {
        Fx {
          try {
            !a == !b
          } catch (e: Throwable) {
            val errA = try {
              !a
              throw IllegalArgumentException()
            } catch (err: Throwable) {
              err
            }
            val errB = try {
              !b
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


}