package arrow.effects

import arrow.core.Either
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.extensions.io.fx.fx
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.internal.IOFrame
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.milliseconds
import arrow.test.UnitSpec
import arrow.test.concurrency.SideEffect
import arrow.test.laws.ConcurrentLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.newSingleThreadContext
import org.junit.runner.RunWith
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.EmptyCoroutineContext

@RunWith(KotlinTestRunner::class)
class IOTest : UnitSpec() {

  init {
    testLaws(ConcurrentLaws.laws(IO.concurrent(), FX_EQ(), FX_EQ(), FX_EQ()))

    class MyException : Exception() {
      override fun fillInStackTrace(): Throwable = this
    }

    "just should yield immediately" {
      val expected = 1
      val run = IO.unsafeRunBlocking(IO.just(expected))
      run shouldBe expected
    }

    "just - can return a null value from unsafeRunBlocking" {
      val never = IO.just<Int?>(null)
      val received = IO.unsafeRunBlocking(never)
      received shouldBe null
    }

    "invoke - should yield invoke value" {
      val expected = 1
      val run = IO.unsafeRunBlocking(IO { expected })
      run shouldBe expected
    }

    "invoke - should defer evaluation until run" {
      var run = false
      val ioa = IO { run = true }
      run shouldBe false
      IO.unsafeRunBlocking(ioa)
      run shouldBe true
    }

    "invoke - should throw exceptions within main block" {
      val exception = MyException()

      shouldThrow<MyException> {
        IO.unsafeRunBlocking(IO { throw exception })
      } shouldBe exception
    }

    "raiseError - should throw immediate failure" {
      val exception = MyException()

      shouldThrow<MyException> {
        IO.unsafeRunBlocking(IO.raiseError<Int>(exception))
      } shouldBe exception
    }

    "unsafeRunBlocking should run in EmptyCoroutineContext" {
      // I was surprised by this behavior but nested suspend scopes inherit the coroutineContext from the outer scope.
      IO.unsafeRunBlocking(IO {
        kotlin.coroutines.coroutineContext shouldBe EmptyCoroutineContext
      })
    }

    "ContinueOn should switch threads" {
      IO.unsafeRunBlocking(
        IO.unit
          .continueOn(newSingleThreadContext("test"))
          .flatMap { IO.lazy { Thread.currentThread().name shouldBe "test" } }
      )
    }

    "UpdateContext should switch threads" {
      IO.unsafeRunBlocking(
        IO.unit
          .updateContext { newSingleThreadContext("test") }
          .flatMap { IO.lazy { Thread.currentThread().name shouldBe "test" } }
      )
    }

    "CoroutineContext state should be correctly managed between boundaries" {
      val ctxA = TestContext()
      val ctxB = CoroutineName("ctxB")
      // We have to explicitly reference kotlin.coroutines.coroutineContext since `TestContext` overrides this property.
      IO.unsafeRunBlocking(IO { kotlin.coroutines.coroutineContext shouldBe EmptyCoroutineContext }
        .continueOn(ctxA)
        .flatMap { IO { kotlin.coroutines.coroutineContext shouldBe ctxA } }
        .continueOn(ctxB)
        .flatMap { IO { kotlin.coroutines.coroutineContext shouldBe ctxB } })
    }

    "updateContext can update and overwrite context" {
      val ctxA = TestContext()
      val ctxB = CoroutineName("ctxB")

      IO.unsafeRunBlocking(IO { kotlin.coroutines.coroutineContext shouldBe EmptyCoroutineContext }
        .updateContext { ctxA }
        .flatMap { IO { kotlin.coroutines.coroutineContext shouldBe ctxA } }
        .updateContext { it + ctxB }
        .flatMap { IO { kotlin.coroutines.coroutineContext shouldBe (ctxA + ctxB) } }
      )
    }

    "fx can switch execution context state across not/bind" {
      val program = fx {
        val ctx = !effect { kotlin.coroutines.coroutineContext }
        !effect { ctx shouldBe EmptyCoroutineContext }
        continueOn(newSingleThreadContext("test"))
        val ctx2 = !effect { Thread.currentThread().name }
        !effect { ctx2 shouldBe "test" }
      }

      IO.unsafeRunBlocking(program)
    }

    "fx **cannot** pass context state across not/bind" {
      val program = fx {
        val ctx = !effect { kotlin.coroutines.coroutineContext }
        !effect { ctx shouldBe EmptyCoroutineContext }
        continueOn(CoroutineName("Simon")) // this is immediately lost and useless.
        val ctx2 = !effect { kotlin.coroutines.coroutineContext }
        !effect { ctx2 shouldBe EmptyCoroutineContext }
      }

      IO.unsafeRunBlocking(program)
    }

    "fx will respect thread switching across not/bind" {
      val program = fx {
        continueOn(newSingleThreadContext("start"))
        val initialThread = !effect { Thread.currentThread().name }
        !(0..130).map { i -> suspend { i } }.sequence()
        val continuedThread = !effect { Thread.currentThread().name }
        continuedThread shouldBe initialThread
      }

      IO.unsafeRunBlocking(program)
    }

    "unsafeRunTimed times out with None result" {
      val never: IO<Unit> = IO.never
      val result = IO.unsafeRunTimed(never, 100.milliseconds)
      result shouldBe None
    }

    "unsafeRunTimed should time out on unending unsafeRunTimed" {
      val never: IO<Int> = IO.never
      val start = System.currentTimeMillis()
      val received = IO.unsafeRunTimed(never, 100.milliseconds)
      val elapsed = System.currentTimeMillis() - start

      received shouldBe None
      (elapsed >= 100) shouldBe true
    }

    "unsafeRunTimed should return a null value from unsafeRunTimed" {
      val never = IO.just<Int?>(null)
      val received = IO.unsafeRunTimed(never, 100.milliseconds)

      received shouldBe Some(null)
    }

    "unsafeRunNonBlocking should return a pure value" {
      val expected = 1
      IO.unsafeRunNonBlocking(IO.just(expected)) { either ->
        either.fold({ fail("unsafeRunNonBlocking should not receive $it for a pure value") }, { it shouldBe expected })
      }
    }

    "unsafeRunNonBlocking should return a suspended value" {
      val expected = 1
      IO.unsafeRunNonBlocking(IO { expected }) { either ->
        either.fold({ fail("unsafeRunNonBlocking should not receive $it for a pure value") }, { it shouldBe expected })
      }
    }

    "unsafeRunNonBlocking should return an error when running raiseError" {
      val exception = MyException()

      IO.unsafeRunNonBlocking(IO.raiseError<Int>(exception)) { either ->
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
      val ioa = IO<Int> { throw exception }
      IO.unsafeRunNonBlocking(ioa) { either ->
        either.fold({ it shouldBe exception }, { fail("") })
      }
    }

    "unsafeRunNonBlocking should not catch exceptions after it ran" {
      val exception = MyException()
      val fx = IO<Int> { throw exception }

      shouldThrow<MyException> {
        IO.unsafeRunNonBlocking(fx) { either ->
          either.fold({ throw it }, { fail("unsafeRunNonBlocking should not receive $it for a suspended exception") })
        }
      } shouldBe exception
    }

    "unsafeRunNonBlockingCancellable should throw the appropriate exception" {
      val program = IO.async<Throwable> { _, cb ->
        val cancel = IO.unsafeRunNonBlockingCancellable(
          IO(newSingleThreadContext("RunThread")) { }.flatMap { IO.never },
          OnCancel.ThrowCancellationException) {
          it.fold({ t -> cb(t.right()) }, { })
        }

        IO.unsafeRunNonBlocking(IO(newSingleThreadContext("CancelThread")) { }) { cancel() }
      }

      IO.unsafeRunBlocking(program) shouldBe OnCancel.CancellationException
    }

    "unsafeRunNonBlockingCancellable can cancel even for infinite asyncs" {
      val program = IO.async { _, cb: (Either<Throwable, Int>) -> Unit ->
        val cancel = IO.unsafeRunNonBlockingCancellable(
          IO(newSingleThreadContext("RunThread")) { }
            .flatMap { IO.async<Int> { _, _ -> Thread.sleep(5000); } },
          OnCancel.ThrowCancellationException) {
          cb(it)
        }

        IO.unsafeRunNonBlocking(
          IO(newSingleThreadContext("CancelThread")) { Thread.sleep(500); }
        ) { cancel() }
      }
      IO.unsafeRunBlocking(program.attempt()).fold({ it shouldBe OnCancel.CancellationException }, { fail("Should fail, got $it instead") })
    }

    "runNonBlocking should defer running" {
      var run = false
      val safeRun = IO.runNonBlocking(IO { run = true }) {
        IO.unit
      }

      run shouldBe false
      IO.unsafeRunBlocking(safeRun)
      run shouldBe true
    }

    "runNonBlocking should return a pure value" {
      val expected = 1
      val safeRun = IO.runNonBlocking(IO.just(expected)) { either ->
        IO { either.fold({ fail("unsafeRunNonBlocking should not receive $it for a pure value") }, { it shouldBe expected }) }
      }
      IO.unsafeRunBlocking(safeRun)
    }

    "runNonBlocking should return a suspended value" {
      val expected = 1
      val safeRun = IO.runNonBlocking(IO { expected }) { either ->
        IO { either.fold({ fail("unsafeRunNonBlocking should not receive $it for a pure value") }, { it shouldBe expected }) }
      }

      IO.unsafeRunBlocking(safeRun)
    }

    "runNonBlocking should return an error when running raiseError" {
      val exception = MyException()

      val safeRun = IO.runNonBlocking(IO.raiseError<Int>(exception)) { either ->
        IO {
          either.fold({
            when (it) {
              is MyException -> it shouldBe exception
              else -> fail("Should only throw MyException")
            }
          }, { fail("") })
        }
      }

      IO.unsafeRunBlocking(safeRun)
    }

    "runNonBlocking should return an error when running a suspended exception" {
      val exception = MyException()
      val ioa = IO<Int> { throw exception }
      val safeRun = IO.runNonBlocking(ioa) { either ->
        IO { either.fold({ it shouldBe exception }, { fail("") }) }
      }

      IO.unsafeRunBlocking(safeRun)
    }

    "runNonBlocking should not catch exceptions after it ran" {
      val exception = MyException()
      val fx = IO<Int> { throw exception }

      shouldThrow<MyException> {
        val safeRun = IO.runNonBlocking(fx) { either ->
          IO { either.fold({ throw it }, { fail("unsafeRunNonBlocking should not receive $it for a suspended exception") }) }
        }

        IO.unsafeRunBlocking(safeRun)
      } shouldBe exception
    }

    "IO `map` stack safe" {
      val size = 500000
      fun mapStackSafe(): IO<Int> =
        (0 until size).fold(IO { 0 }) { acc, _ -> acc.map { it + 1 } }
      unsafe { runBlocking { IO { mapStackSafe()() } } } shouldBe size
    }

    "IO `flatMap` stack safe on right bind" {
      val size = 500000
      fun flatMapStackSafe(): IO<Int> =
        (0 until size).fold(IO { 0 }) { acc, _ -> acc.flatMap { IO.just(it + 1) } }
      unsafe { runBlocking { IO { flatMapStackSafe()() } } } shouldBe size
    }

    "IO `flatMap` stackSafe on left bind" {
      val size = 500000
      val dummy = MyException()

      fun fxLoopNotHappy(size: Int, i: Int): IO<Int> =
        if (i < size) {
          IO { throw dummy }.attempt().flatMap {
            it.fold({ fxLoopNotHappy(size, i + 1) }, IO.Companion::just)
          }
        } else IO.just(1)

      IO.unsafeRunBlocking(fxLoopNotHappy(size, 0))
    }

    "invoke is called on every run call" {
      val sideEffect = SideEffect()
      val io = IO { sideEffect.increment(); 1 }
      IO.unsafeRunBlocking(io)
      IO.unsafeRunBlocking(io)

      sideEffect.counter shouldBe 2
    }

    "IO should be able to be attempted" {
      val e = RuntimeException("Boom!")
      IO.unsafeRunBlocking(IO.raiseError<String>(e).attempt()) shouldBe Either.Left(e)
    }

    "IOFrame should always call recover on IO.Bind with RaiseError" {
      val ThrowableAsStringFrame = object : IOFrame<Any?, IOOf<String>> {
        override fun invoke(a: Any?) = fail("IOFrame should never call invoke on a failed value")
        override fun recover(e: Throwable) = IO.just(e.message ?: "")
      }

      forAll(Gen.string()) { message ->
        IO.unsafeRunBlocking(
          IO.FlatMap(IO.raiseError(RuntimeException(message)), ThrowableAsStringFrame as (Int) -> IO<String>)
        ) == message
      }
    }

    "IOFrame should always call invoke on IO.Bind with Pure" {
      val ThrowableAsStringFrame = object : IOFrame<Int, IOOf<Int>> {
        override fun invoke(a: Int) = IO { a + 1 }
        override fun recover(e: Throwable) = fail("IOFrame should never call recover on succesful value")
      }

      IO.unsafeRunBlocking(
        IO.FlatMap(IO.Pure(1), ThrowableAsStringFrame as (Int) -> IO<Int>)
      ) shouldBe 2
    }

    "should be able to handle error" {
      val e = RuntimeException("Boom!")

      unsafe {
        runBlocking {
          IO.raiseError<String>(e)
            .handleErrorWith { IO { it.message!! } }
        }
      } shouldBe e.message
    }

    "attempt - pure" {
      val value = 1
      val f = { i: Int -> i + 1 }
      IO.unsafeRunBlocking(IO.just(value).map(f).attempt()) shouldBe Right(f(value))
    }

    "attempt - error" {
      val e = RuntimeException("Boom!")

      IO.unsafeRunBlocking(IO.raiseError<String>(e)
        .map { it.toUpperCase() }
        .flatMap { IO { it } }
        .attempt()) shouldBe e.left()
    }

    "handleErrorW - error" {
      val e = RuntimeException("Boom!")

      IO.unsafeRunBlocking(IO.raiseError<String>(e)
        .map { it.toUpperCase() }
        .flatMap { IO { it } }
        .handleErrorWith { IO { it.message!! } }) shouldBe "Boom!"
    }

    "bracket cancellation should release resource with cancel exit status" {
      val program = Promise.uncancelable<ForIO, ExitCase<Throwable>>(IO.async()).flatMap { p ->
        IO.unsafeRunNonBlockingCancellable(
          IO.just(0L)
            .bracketCase(
              use = { IO.never },
              release = { _, exitCase -> p.complete(exitCase) }
            )
        ) {}
          .invoke() // cancel immediately

        p.get()
      }

      IO.unsafeRunBlocking(program) shouldBe ExitCase.Canceled
    }

    "should cancel KindConnection on dispose" {
      val program = Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { latch ->
        IO {
          IO.unsafeRunNonBlockingCancellable(
            IO.async<Unit> { conn, _ ->
              conn.push(latch.complete(Unit))
            }) { }
            .invoke()
        }.flatMap { latch.get() }
      }

      IO.unsafeRunBlocking(program)
    }

    "Bracket should be stack safe" {
      val size = 5000

      fun fxBracketLoop(i: Int): IO<Int> =
        IO.unit.bracket(use = { IO.just(i + 1) }, release = { IO.unit }).flatMap { ii ->
          if (ii < size) fxBracketLoop(ii)
          else IO.just(ii)
        }

      IO.unsafeRunBlocking(IO.just(0).flatMap(::fxBracketLoop)) shouldBe size
    }

    "GuaranteeCase should be stack safe" {
      val size = 5000

      fun ioGuaranteeCase(i: Int): IO<Int> =
        IO.unit.guaranteeCase { IO.unit }.flatMap {
          val ii = i + 1
          if (ii < size) ioGuaranteeCase(ii)
          else IO.just(ii)
        }

      IO.unsafeRunBlocking(IO.just(0).flatMap(::ioGuaranteeCase)) shouldBe size
    }

//    "KindConnection can cancel upstream" {
//      val program = Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { latch ->
//        IO.unsafeRunNonBlockingCancellable(
//          IO.async<Unit> { conn, cb ->
//            conn.push(latch.complete(Unit))
//            cb(Right(Unit))
//          }.flatMap {
//            IO.async<Unit> { conn, _ ->
//              IO.unsafeRunBlocking(conn.cancel())
//            }
//          }
//        ) { }
//
//        latch.get()
//      }
//
//      IO.unsafeRunBlocking(program)
//    }
  }
}

class TestContext : AbstractCoroutineContextElement(TestContext) {
  companion object Key : kotlin.coroutines.CoroutineContext.Key<CoroutineName>
}

fun <A> FX_EQ(): Eq<IOOf<A>> = Eq { a, b ->
  unsafe {
    runBlocking {
      IO {
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
          errA == errB
        }
      }
    }
  }
}
