package arrow.effects

import arrow.core.*
import arrow.effects.extensions.fx.bracket.bracket
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.*
import arrow.test.UnitSpec
import arrow.test.laws.BracketLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.fail
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.junit.runner.RunWith
import java.lang.RuntimeException
import java.util.concurrent.CountDownLatch
import kotlin.properties.Delegates

@RunWith(KotlinTestRunner::class)
class FxTest : UnitSpec() {

  init {
    testLaws(BracketLaws.laws(Fx.bracket(), FX_EQ(), FX_EQ(), FX_EQ()))

    class MyException : Exception() {
      override fun fillInStackTrace(): Throwable = this
    }

    "just should yield immediately" {
      val expected = 1
      val run = Fx.unsafeRunBlocking(Fx.just(expected))
      run shouldBe expected
    }

    "just - can return a null value from unsafeRunBlocking" {
      val never = Fx.just<Int?>(null)
      val received = Fx.unsafeRunBlocking(never)
      received shouldBe null
    }

    "invoke - should yield invoke value" {
      val expected = 1
      val run = Fx.unsafeRunBlocking(Fx { expected })
      run shouldBe expected
    }

    "invoke - should defer evaluation until run" {
      var run = false
      val ioa = Fx { run = true }
      run shouldBe false
      Fx.unsafeRunBlocking(ioa)
      run shouldBe true
    }

    "invoke - should throw exceptions within main block" {
      val exception = MyException()

      shouldThrow<MyException> {
        Fx.unsafeRunBlocking(Fx { throw exception })
      } shouldBe exception
    }

    "raiseError - should throw immediate failure" {
      val exception = MyException()

      shouldThrow<MyException> {
        Fx.unsafeRunBlocking(Fx.raiseError<Int>(exception))
      } shouldBe exception
    }

    "unsafeRunNonBlocking should return a pure value" {
      val expected = 1
      Fx.unsafeRunNonBlocking(Fx.just(expected)) { either ->
        either.fold({ fail("unsafeRunNonBlocking should not receive $it for a pure value") }, { it shouldBe expected })
      }
    }

    "unsafeRunNonBlocking should return a suspended value" {
      val expected = 1
      Fx.unsafeRunNonBlocking(Fx { expected }) { either ->
        either.fold({ fail("unsafeRunNonBlocking should not receive $it for a pure value") }, { it shouldBe expected })
      }
    }

    "unsafeRunNonBlocking should return an error when running raiseError" {
      val exception = MyException()

      Fx.unsafeRunNonBlocking(Fx.raiseError<Int>(exception)) { either ->
        either.fold({
          when (it) {
            is MyException -> it shouldBe exception
            else -> fail("Should only throw MyException")
          }
        }, { fail("") })
      }
    }

    "unsafeRunNonBlocking should return an error when running a suspended exception" {
      val exception = MyException()
      val ioa = Fx<Int> { throw exception }
      Fx.unsafeRunNonBlocking(ioa) { either ->
        either.fold({ it shouldBe exception }, { fail("") })
      }
    }

    "unsafeRunNonBlocking should not catch exceptions after it ran" {
      val exception = MyException()
      val fx = Fx<Int> { throw exception }

      shouldThrow<MyException> {
        Fx.unsafeRunNonBlocking(fx) { either ->
          either.fold({ throw it }, { fail("unsafeRunNonBlocking should not receive $it for a suspended exception") })
        }
      } shouldBe exception
    }

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

      Fx.unsafeRunNonBlockingCancellable(
        Fx { ctxA = kotlin.coroutines.coroutineContext[CancelContext]!!.connection }
          .flatMap { Fx { ctxB = kotlin.coroutines.coroutineContext[CancelContext]!!.connection }.uncancelable() }
          .flatMap { Fx { ctxC = kotlin.coroutines.coroutineContext[CancelContext]!!.connection } }
          .flatMap { Fx { ctxA == ctxC && ctxA != ctxB && ctxB is KindConnection.Uncancelable } }
      ) {
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