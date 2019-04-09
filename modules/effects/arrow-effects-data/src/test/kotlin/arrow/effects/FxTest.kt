package arrow.effects

import arrow.core.Either
import arrow.core.Right
import arrow.core.left
import arrow.effects.extensions.fx.bracket.bracket
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.extensions.runNonBlockingCancellable
import arrow.effects.suspended.fx.*
import arrow.test.UnitSpec
import arrow.test.laws.BracketLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith
import java.lang.RuntimeException
import java.util.concurrent.CountDownLatch
import kotlin.properties.Delegates

@RunWith(KotlinTestRunner::class)
class FxTest : UnitSpec() {

  init {
    testLaws(BracketLaws.laws(Fx.bracket(), FX_EQ(), FX_EQ(), FX_EQ()))

    "Fx `map` stack safe" {
      val size = 500000
      fun mapStackSafe(): Fx<Int> =
        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.map { it + 1 } }
      unsafe { runBlocking { Fx { mapStackSafe()() } } } shouldBe size
    }

    "Fx `flatMap` stack safe" {
      val size = 500000
      fun flatMapStackSafe(): Fx<Int> =
        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.flatMap { Fx.just(it + 1) } }
      unsafe { runBlocking { Fx { flatMapStackSafe()() } } } shouldBe size
    }

    "Fx should be able to be attempted" {
      val e = RuntimeException("Boom!")
      unsafe {
        runBlocking {
          Fx.raiseError<String>(e).attempt()
        }
      } shouldBe Either.Left(e)
    }

    "Fx should be able to handle error" {
      val e = RuntimeException("Boom!")

      unsafe {
        runBlocking {
          Fx.raiseError<String>(e)
            .handleErrorWith { Fx { it.message!! } }
        }
      } shouldBe e.message
    }

    "Fx attempt - value" {
      val value = 1
      val f = { i: Int -> i + 1 }
      Fx.unsafeRunBlocking(Fx.just(value).map(f).attempt()) shouldBe Right(f(value))
    }

    "Fx attempt - error" {
      val e = RuntimeException("Boom!")

      Fx.unsafeRunBlocking(Fx.raiseError<String>(e)
        .map { it.toUpperCase() }
        .flatMap { Fx { it } }
        .attempt()) shouldBe e.left()
    }

    "Fx handleErrorW - error" {
      val e = RuntimeException("Boom!")

      Fx.unsafeRunBlocking(Fx.raiseError<String>(e)
        .map { it.toUpperCase() }
        .flatMap { Fx { it } }
        .handleErrorWith { Fx { it.message!! } }) shouldBe "Boom!"
    }

    "Fx should be able to become uncancelable" {
      var ctxA: KindConnection<ForFx> by Delegates.notNull()
      var ctxB: KindConnection<ForFx> by Delegates.notNull()
      var ctxC: KindConnection<ForFx> by Delegates.notNull()
      val latch = CountDownLatch(1)
      var result: Either<Throwable, Boolean>? = null

      Fx { ctxA = kotlin.coroutines.coroutineContext[CancelContext]!!.connection }
        .flatMap { Fx { ctxB = kotlin.coroutines.coroutineContext[CancelContext]!!.connection }.uncancelable() }
        .flatMap { Fx { ctxC = kotlin.coroutines.coroutineContext[CancelContext]!!.connection } }
        .flatMap { Fx { ctxA == ctxC && ctxA != ctxB && ctxB is KindConnection.Uncancelable } }
        .runNonBlockingCancellable {
          result = it
          latch.countDown()
        }

      latch.await()
      result shouldBe Right(true)
    }
  }
}

fun <A> FX_EQ(): Eq<FxOf<A>> = Eq { a, b ->
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