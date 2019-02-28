package arrow.effects

import arrow.core.*
import arrow.effects.IO.Companion.just
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.extensions.io.concurrent.parMapN
import arrow.effects.extensions.io.fx.fx
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.milliseconds
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.concurrency.SideEffect
import arrow.test.laws.ConcurrentLaws
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import kotlinx.coroutines.newSingleThreadContext
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
@kotlinx.coroutines.ObsoleteCoroutinesApi
class IOTest : UnitSpec() {

  init {
    testLaws(ConcurrentLaws.laws(IO.concurrent(), EQ(), EQ(), EQ()))

    "should defer evaluation until run" {
      var run = false
      val ioa = IO { run = true }
      run shouldBe false
      ioa.unsafeRunSync()
      run shouldBe true
    }

    class MyException : Exception()

    "should catch exceptions within main block" {
      val exception = MyException()
      val ioa = IO { throw exception }
      val result: Either<Throwable, Nothing> = ioa.attempt().unsafeRunSync()

      val expected = Left(exception)

      result shouldBe expected
    }

    "should yield immediate successful invoke value" {
      val run = IO { 1 }.unsafeRunSync()

      val expected = 1

      run shouldBe expected
    }

    "should yield immediate successful pure value" {
      val run = IO.just(1).unsafeRunSync()

      val expected = 1

      run shouldBe expected
    }

    "should throw immediate failure by raiseError" {
      try {
        IO.raiseError<Int>(MyException()).unsafeRunSync()
        fail("")
      } catch (myException: MyException) {
        // Success
      } catch (throwable: Throwable) {
        fail("Should only throw MyException")
      }
    }

    "should time out on unending unsafeRunTimed" {
      val never = IO.async().never<Int>().fix()
      val start = System.currentTimeMillis()
      val received = never.unsafeRunTimed(100.milliseconds)
      val elapsed = System.currentTimeMillis() - start

      received shouldBe None
      (elapsed >= 100) shouldBe true
    }

    "should return a null value from unsafeRunTimed" {
      val never = IO.just<Int?>(null)
      val received = never.unsafeRunTimed(100.milliseconds)

      received shouldBe Some(null)
    }

    "should return a null value from unsafeRunSync" {
      val value = IO.just<Int?>(null).unsafeRunSync()

      value shouldBe null
    }

    "should complete when running a pure value with unsafeRunAsync" {
      val expected = 0
      IO.just(expected).unsafeRunAsync { either ->
        either.fold({ fail("") }, { it shouldBe expected })
      }
    }


    "should complete when running a return value with unsafeRunAsync" {
      val expected = 0
      IO { expected }.unsafeRunAsync { either ->
        either.fold({ fail("") }, { it shouldBe expected })
      }
    }

    "should return an error when running an exception with unsafeRunAsync" {
      IO.raiseError<Int>(MyException()).unsafeRunAsync { either ->
        either.fold({
          when (it) {
            is MyException -> {
            }
            else -> fail("Should only throw MyException")
          }
        }, { fail("") })
      }
    }

    "should return exceptions within main block with unsafeRunAsync" {
      val exception = MyException()
      val ioa = IO<Int> { throw exception }
      ioa.unsafeRunAsync { either ->
        either.fold({ it shouldBe exception }, { fail("") })
      }
    }

    "should not catch exceptions within run block with unsafeRunAsync" {
      try {
        val exception = MyException()
        val ioa = IO<Int> { throw exception }
        ioa.unsafeRunAsync { either ->
          either.fold({ throw exception }, { fail("") })
        }
        fail("Should rethrow the exception")
      } catch (myException: MyException) {
        // Success
      } catch (throwable: Throwable) {
        fail("Should only throw MyException")
      }
    }

    "should complete when running a pure value with runAsync" {
      val expected = 0
      IO.just(expected).runAsync { either ->
        either.fold({ fail("") }, { IO { it shouldBe expected } })
      }
    }

    "should complete when running a return value with runAsync" {
      val expected = 0
      IO { expected }.runAsync { either ->
        either.fold({ fail("") }, { IO { it shouldBe expected } })
      }
    }

    "should return an error when running an exception with runAsync" {
      IO.raiseError<Int>(MyException()).runAsync { either ->
        either.fold({
          when (it) {
            is MyException -> {
              IO { }
            }
            else -> fail("Should only throw MyException")
          }
        }, { fail("") })
      }
    }

    "should return exceptions within main block with runAsync" {
      val exception = MyException()
      val ioa = IO<Int> { throw exception }
      ioa.runAsync { either ->
        either.fold({ IO { it shouldBe exception } }, { fail("") })
      }
    }

    "should catch exceptions within run block with runAsync" {
      try {
        val exception = MyException()
        val ioa = IO<Int> { throw exception }
        ioa.runAsync { either ->
          either.fold({ throw it }, { fail("") })
        }.unsafeRunSync()
        fail("Should rethrow the exception")
      } catch (throwable: AssertionError) {
        fail("${throwable.message}")
      } catch (throwable: Throwable) {
        // Success
      }
    }


    "should map values correctly on success" {
      val run = IO.just(1).map { it + 1 }.unsafeRunSync()

      val expected = 2

      run shouldBe expected
    }

    "should flatMap values correctly on success" {
      val run = just(1).flatMap { num -> IO { num + 1 } }.unsafeRunSync()

      val expected = 2

      run shouldBe expected
    }

    "invoke is called on every run call" {
      val sideEffect = SideEffect()
      val io = IO { sideEffect.increment(); 1 }
      io.unsafeRunSync()
      io.unsafeRunSync()

      sideEffect.counter shouldBe 2
    }

    "unsafeRunTimed times out with None result" {
      val never = IO.async().never<Unit>().fix()
      val result = never.unsafeRunTimed(100.milliseconds)
      result shouldBe None
    }

    "parallel execution makes all IOs start at the same time" {
      val order = mutableListOf<Long>()

      fun makePar(num: Long) =
        IO(newSingleThreadContext("$num")) {
          // Sleep according to my number
          Thread.sleep(num * 40)
        }.map {
          // Add myself to order list
          order.add(num)
          num
        }

      val result =
        newSingleThreadContext("all").parMapN(
          makePar(6), makePar(3), makePar(2), makePar(4), makePar(1), makePar(5))
        { six, tree, two, four, one, five -> listOf(six, tree, two, four, one, five) }
          .unsafeRunSync()

      result shouldBe listOf(6L, 3, 2, 4, 1, 5)
      order.toList() shouldBe listOf(1L, 2, 3, 4, 5, 6)
    }

    "parallel execution preserves order for synchronous IOs" {
      val order = mutableListOf<Long>()

      fun IO<Long>.order() =
        map {
          order.add(it)
          it
        }

      fun makePar(num: Long) =
        IO(newSingleThreadContext("$num")) {
          // Sleep according to my number
          Thread.sleep(num * 30)
          num
        }.order()

      val result =
        newSingleThreadContext("all").parMapN(
          makePar(6), IO.just(1L).order(), makePar(4), IO.defer { IO.just(2L) }.order(), makePar(5), IO { 3L }.order())
        { six, one, four, two, five, three -> listOf(six, one, four, two, five, three) }
          .unsafeRunSync()

      result shouldBe listOf(6L, 1, 4, 2, 5, 3)
      order.toList() shouldBe listOf(1L, 2, 3, 4, 5, 6)
    }

    "parallel mapping is done in the expected CoroutineContext" {
      fun makePar(num: Long) =
        IO(newSingleThreadContext("$num")) {
          // Sleep according to my number
          Thread.sleep(num * 20)
          num
        }

      val result =
        newSingleThreadContext("all").parMapN(
          makePar(6), IO.just(1L), makePar(4), IO.defer { IO.just(2L) }, makePar(5), IO { 3L })
        { _, _, _, _, _, _ ->
          Thread.currentThread().name
        }.unsafeRunSync()

      result shouldBe "all"
    }

    "parallel IO#defer, IO#suspend and IO#async are run in the expected CoroutineContext" {
      val result =
        newSingleThreadContext("here").parMapN(
          IO { Thread.currentThread().name },
          IO.defer { IO.just(Thread.currentThread().name) },
          IO.async<String> { _, cb -> cb(Thread.currentThread().name.right()) },
          ::Tuple3)
          .unsafeRunSync()

      result shouldBe Tuple3("here", "here", "here")
    }

    "unsafeRunAsyncCancellable should cancel correctly" {
      IO.async { _, cb: (Either<Throwable, Int>) -> Unit ->
        val cancel =
          IO(newSingleThreadContext("RunThread")) { }
            .flatMap { IO.async<Int> { _, cb -> Thread.sleep(500); cb(1.right()) } }
            .unsafeRunAsyncCancellable(OnCancel.Silent) {
              cb(it)
            }
        IO(newSingleThreadContext("CancelThread")) { }
          .unsafeRunAsync { cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe None
    }

    "unsafeRunAsyncCancellable should throw the appropriate exception" {
      IO.async<Throwable> { _, cb ->
        val cancel =
          IO(newSingleThreadContext("RunThread")) { }
            .flatMap { IO.async<Int> { _, cb -> Thread.sleep(500); cb(1.right()) } }
            .unsafeRunAsyncCancellable(OnCancel.ThrowCancellationException) {
              it.fold({ t -> cb(t.right()) }, { })
            }
        IO(newSingleThreadContext("CancelThread")) { }
          .unsafeRunAsync { cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe Some(OnCancel.CancellationException)
    }

    "IOFrame should always be called when using IO.Bind" {
      val ThrowableAsStringFrame = object : IOFrame<Any?, IOOf<String>> {
        override fun invoke(a: Any?) = just(a.toString())

        override fun recover(e: Throwable) = just(e.message ?: "")

      }

      forAll(Gen.string()) { message ->
        IO.Bind(IO.raiseError(RuntimeException(message)), ThrowableAsStringFrame as (Int) -> IO<String>)
          .unsafeRunSync() == message
      }
    }

    "unsafeRunAsyncCancellable can cancel even for infinite asyncs" {
      IO.async { _, cb: (Either<Throwable, Int>) -> Unit ->
        val cancel =
          IO(newSingleThreadContext("RunThread")) { }
            .flatMap { IO.async<Int> { _, _ -> Thread.sleep(5000); } }
            .unsafeRunAsyncCancellable(OnCancel.ThrowCancellationException) {
              cb(it)
            }
        IO(newSingleThreadContext("CancelThread")) { Thread.sleep(500); }
          .unsafeRunAsync { cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe None
    }

    "IO.binding should for comprehend over IO" {
      val result = fx {
        val (x) = IO.just(1)
        val y = bind { IO { x + 1 } }
        y
      }.fix()
      result.unsafeRunSync() shouldBe 2
    }

    "IO bracket cancellation should release resource with cancel exit status" {
      Promise.uncancelable<ForIO, ExitCase<Throwable>>(IO.async()).flatMap { p ->
        IO.just(0L)
          .bracketCase(
            use = { IO.never },
            release = { _, exitCase -> p.complete(exitCase) }
          )
          .unsafeRunAsyncCancellable { }
          .invoke() //cancel immediately

        p.get()
      }.unsafeRunSync() shouldBe ExitCase.Canceled
    }

    "Cancelable should run CancelToken" {
      Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { p ->
        IO.concurrent().cancelable<Unit> {
          p.complete(Unit)
        }.fix()
          .unsafeRunAsyncCancellable { }
          .invoke()

        p.get()
      }.unsafeRunSync() shouldBe Unit
    }

    "CancelableF should run CancelToken" {
      Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { p ->
        IO.concurrent().cancelableF<Unit> {
          IO { p.complete(Unit) }
        }.fix()
          .unsafeRunAsyncCancellable { }
          .invoke()

        p.get()
      }.unsafeRunSync() shouldBe Unit
    }

    "IO should cancel KindConnection on dispose" {
      Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { latch ->
        IO {
          IO.async<Unit> { conn, _ ->
            conn.push(latch.complete(Unit))
          }.unsafeRunAsyncCancellable { }
            .invoke()
        }.flatMap { latch.get() }
      }.unsafeRunSync()
    }

    "KindConnection can cancel upstream" {
      Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { latch ->
        IO.async<Unit> { conn, cb ->
          conn.push(latch.complete(Unit))
          cb(Right(Unit))
        }.flatMap {
          IO.async<Unit> { conn, _ ->
            conn.cancel().fix().unsafeRunAsync { }
          }
        }.unsafeRunAsyncCancellable { }
        latch.get()
      }.unsafeRunSync()
    }

  }
}

object Error : Throwable()
