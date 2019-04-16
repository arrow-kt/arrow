package arrow.effects

import arrow.core.*
import arrow.effects.extensions.fx.async.async
import arrow.effects.extensions.fx.bracket.bracket
import arrow.effects.extensions.fx.fx.fx
import arrow.effects.extensions.fx.monad.flatMap
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.suspended.fx.*
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.concurrency.SideEffect
import arrow.test.laws.AsyncLaws
import arrow.test.laws.BracketLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.TestContext
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.newSingleThreadContext
import org.junit.runner.RunWith
import java.lang.RuntimeException
import java.util.concurrent.CountDownLatch
import kotlin.properties.Delegates

@RunWith(KotlinTestRunner::class)
class FxTest : UnitSpec() {

  init {
    testLaws(AsyncLaws.laws(Fx.async(), FX_EQ(), FX_EQ()))

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

    "unsafeRunNonBlockingCancellable should cancel correctly" {
      val program = Fx.async { _, cb: (Either<Throwable, Int>) -> Unit ->
        val cancel = Fx.unsafeRunNonBlockingCancellable(
          Fx(newSingleThreadContext("RunThread"), { Unit })
            .flatMap { Fx.async<Int> { _, cb -> Thread.sleep(500); cb(1.right()) } }
        ) { cb(it) }

        Fx.unsafeRunNonBlocking(Fx(newSingleThreadContext("CancelThread")) { }) { cancel() }
      }

      Fx.unsafeRunBlocking(program) shouldBe None
    }

    "unsafeRunNonBlockingCancellable should throw the appropriate exception" {
      val program = Fx.async<Throwable> { _, cb ->
        val cancel = Fx.unsafeRunNonBlockingCancellable(
          Fx(newSingleThreadContext("RunThread")) { }
            .flatMap { Fx.async<Int> { _, cb -> Thread.sleep(500); cb(1.right()) } },
          OnCancel.ThrowCancellationException) {
          it.fold({ t -> cb(t.right()) }, { })
        }

        Fx.unsafeRunNonBlocking(Fx(newSingleThreadContext("CancelThread")) { }) { cancel() }
      }

      Fx.unsafeRunBlocking(program) shouldBe OnCancel.CancellationException
    }

    "unsafeRunNonBlockingCancellable can cancel even for infinite asyncs" {
      val program = Fx.async { _, cb: (Either<Throwable, Int>) -> Unit ->
        val cancel = Fx.unsafeRunNonBlockingCancellable(
          Fx(newSingleThreadContext("RunThread")) { }
            .flatMap { Fx.async<Int> { _, _ -> Thread.sleep(5000); } },
          OnCancel.ThrowCancellationException) {
          cb(it)
        }

        Fx.unsafeRunNonBlocking(
          Fx(newSingleThreadContext("CancelThread")) { Thread.sleep(500); }
        ) { cancel() }
      }
      Fx.unsafeRunBlocking(program) shouldBe None
    }

    "runNonBlocking should defer running" {
      var run = false
      val safeRun = Fx.runNonBlocking(Fx { run = true }) {
        Fx.unit
      }

      run shouldBe false
      Fx.unsafeRunBlocking(safeRun)
      run shouldBe true
    }

    "runNonBlocking should return a pure value" {
      val expected = 1
      val safeRun = Fx.runNonBlocking(Fx.just(expected)) { either ->
        Fx { either.fold({ fail("unsafeRunNonBlocking should not receive $it for a pure value") }, { it shouldBe expected }) }
      }
      Fx.unsafeRunBlocking(safeRun)
    }

//    "runNonBlocking should return a suspended value" {
//      val expected = 1
//      val safeRun = Fx.runNonBlocking(Fx { expected }) { either ->
//        Fx { either.fold({ fail("unsafeRunNonBlocking should not receive $it for a pure value") }, { it shouldBe expected }) }
//      }
//
//      Fx.unsafeRunBlocking(safeRun)
//    }

    "runNonBlocking should return an error when running raiseError" {
      val exception = MyException()

      val safeRun = Fx.runNonBlocking(Fx.raiseError<Int>(exception)) { either ->
        Fx {
          either.fold({
            when (it) {
              is MyException -> it shouldBe exception
              else -> fail("Should only throw MyException")
            }
          }, { fail("") })
        }
      }

      Fx.unsafeRunBlocking(safeRun)
    }

//    "runNonBlocking should return an error when running a suspended exception" {
//      val exception = MyException()
//      val ioa = Fx<Int> { throw exception }
//      val safeRun = Fx.runNonBlocking(ioa) { either ->
//        Fx { either.fold({ it shouldBe exception }, { fail("") }) }
//      }
//
//      Fx.unsafeRunBlocking(safeRun)
//    }

//    "runNonBlocking should not catch exceptions after it ran" {
//      val exception = MyException()
//      val fx = Fx<Int> { throw exception }
//
//      shouldThrow<MyException> {
//        val safeRun = Fx.runNonBlocking(fx) { either ->
//          Fx { either.fold({ throw it }, { fail("unsafeRunNonBlocking should not receive $it for a suspended exception") }) }
//        }
//
//        Fx.unsafeRunBlocking(safeRun)
//      } shouldBe exception
//    }

    "Fx `map` stack safe" {
      val size = 500000
      fun mapStackSafe(): Fx<Int> =
        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.map { it + 1 } }
      unsafe { runBlocking { Fx { mapStackSafe()() } } } shouldBe size
    }

    "Fx `flatMap` stack safe on right bind" {
      val size = 500000
      fun flatMapStackSafe(): Fx<Int> =
        (0 until size).fold(Fx { 0 }) { acc, _ -> acc.flatMap { Fx.just(it + 1) } }
      unsafe { runBlocking { Fx { flatMapStackSafe()() } } } shouldBe size
    }

    "Fx `flatMap` stackSafe on left bind" {
      val size = 500000
      val dummy = MyException()

      fun fxLoopNotHappy(size: Int, i: Int): Fx<Int> =
        if (i < size) {
          Fx { throw dummy }.attempt().flatMap {
            it.fold({ fxLoopNotHappy(size, i + 1) }, Fx.Companion::just)
          }
        } else Fx.just(1)

      Fx.unsafeRunBlocking(fxLoopNotHappy(size, 0))
    }

    "invoke is called on every run call" {
      val sideEffect = SideEffect()
      val io = Fx { sideEffect.increment(); 1 }
      Fx.unsafeRunBlocking(io)
      Fx.unsafeRunBlocking(io)

      sideEffect.counter shouldBe 2
    }

    "Fx should be able to be attempted" {
      val e = RuntimeException("Boom!")
      Fx.unsafeRunBlocking(Fx.raiseError<String>(e).attempt()) shouldBe Either.Left(e)
    }

    "FxFrame should always call recover on Fx.Bind with RaiseError" {
      val ThrowableAsStringFrame = object : FxFrame<Any?, FxOf<String>> {
        override fun invoke(a: Any?) = fail("FxFrame should never call invoke on a failed value")
        override fun recover(e: Throwable) = Fx.just(e.message ?: "")

      }

      forAll(Gen.string()) { message ->
        Fx.unsafeRunBlocking(
          Fx.FlatMap(Fx.raiseError(RuntimeException(message)), ThrowableAsStringFrame as (Int) -> Fx<String>, 0)
        ) == message
      }
    }

    "FxFrame should always call invoke on Fx.Bind with Pure" {
      val ThrowableAsStringFrame = object : FxFrame<Int, FxOf<Int>> {
        override fun invoke(a: Int) = Fx { a + 1 }
        override fun recover(e: Throwable) = fail("FxFrame should never call recover on succesful value")

      }

      Fx.unsafeRunBlocking(
        Fx.FlatMap(Fx.Pure(1, 0), ThrowableAsStringFrame as (Int) -> Fx<Int>, 0)
      ) shouldBe 2
    }

    "should be able to handle error" {
      val e = RuntimeException("Boom!")

      unsafe {
        runBlocking {
          Fx.raiseError<String>(e)
            .handleErrorWith { Fx { it.message!! } }
        }
      } shouldBe e.message
    }

    "attempt - pure" {
      val value = 1
      val f = { i: Int -> i + 1 }
      Fx.unsafeRunBlocking(Fx.just(value).map(f).attempt()) shouldBe Right(f(value))
    }

    "attempt - error" {
      val e = RuntimeException("Boom!")

      Fx.unsafeRunBlocking(Fx.raiseError<String>(e)
        .map { it.toUpperCase() }
        .flatMap { Fx { it } }
        .attempt()) shouldBe e.left()
    }

    "handleErrorW - error" {
      val e = RuntimeException("Boom!")

      Fx.unsafeRunBlocking(Fx.raiseError<String>(e)
        .map { it.toUpperCase() }
        .flatMap { Fx { it } }
        .handleErrorWith { Fx { it.message!! } }) shouldBe "Boom!"
    }

    "bracket cancellation should release resource with cancel exit status" {
      val program = Promise.uncancelable<ForFx, ExitCase<Throwable>>(Fx.async()).flatMap { p ->
        Fx.unsafeRunNonBlockingCancellable(
          Fx.just(0L)
            .bracketCase(
              use = { Fx.never },
              release = { _, exitCase -> p.complete(exitCase) }
            )
        ) {}
          .invoke() //cancel immediately

        p.get()
      }

      Fx.unsafeRunBlocking(program) shouldBe ExitCase.Canceled
    }

    "should cancel KindConnection on dispose" {
      val program = Promise.uncancelable<ForFx, Unit>(Fx.async()).flatMap { latch ->
        Fx {
          Fx.unsafeRunNonBlockingCancellable(
            Fx.async<Unit> { conn, _ ->
              conn.push(latch.complete(Unit))
            }) { }
            .invoke()
        }.flatMap { latch.get() }
      }

      Fx.unsafeRunBlocking(program)
    }

    "KindConnection can cancel upstream" {
      val program = Promise.uncancelable<ForFx, Unit>(Fx.async()).flatMap { latch ->
        Fx.unsafeRunNonBlockingCancellable(
          Fx.async<Unit> { conn, cb ->
            conn.push(latch.complete(Unit))
            cb(Right(Unit))
          }.flatMap {
            Fx.async<Unit> { conn, _ ->
              Fx.unsafeRunBlocking(conn.cancel())
            }
          }
        ) { }

        latch.get()
      }

      Fx.unsafeRunBlocking(program)
    }

    "fx should stay within same context" {
      val program = fx {
        continueOn(newSingleThreadContext("start"))
        val initialThread = !effect { Thread.currentThread().name }
        !(0..130).map { i -> suspend { i } }.sequence()
        val continuedThread = !effect { Thread.currentThread().name }
        continuedThread shouldBe initialThread
      }

      Fx.unsafeRunBlocking(program)
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