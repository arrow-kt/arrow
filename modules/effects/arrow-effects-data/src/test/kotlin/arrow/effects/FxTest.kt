package arrow.effects

import arrow.core.Either
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.FxOf
import arrow.effects.suspended.fx.not
import arrow.test.UnitSpec
import arrow.test.generators.throwable
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith
import java.lang.RuntimeException

@RunWith(KotlinTestRunner::class)
class FxTest : UnitSpec() {

  init {
//    testLaws(ConcurrentLaws.laws(Fx.concurrent(), EQ(), EQ(), EQ()))

//    "Fx `map` stack safe" {
//      val size = 500000
//      fun mapStackSafe(): Fx<Int> =
//        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.map { it + 1 } }
//      unsafe { runBlocking { Fx { mapStackSafe()() } } } shouldBe size
//    }

//    "Fx `flatMap` stack safe" {
//      val size = 500000
//      fun flatMapStackSafe(): Fx<Int> =
//        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.flatMap { Fx.just(it + 1) } }
//      unsafe { runBlocking { Fx { flatMapStackSafe()() } } } shouldBe size
//    }

//    "Fx should be able to recover from error" {
//      val e = RuntimeException("Boom!")
//      unsafe {
//        runBlocking {
//          Fx.raiseError<String>(e).attempt()
//        }
//      } shouldBe Either.Left(e)
//    }

    "Fx should be able to handle error" {
      val e = RuntimeException("Boom!")

      unsafe {
        runBlocking {
          Fx.raiseError<String>(e)
            .handleErrorWith { Fx { it.message!! } }
        }
      } shouldBe e.message
    }
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