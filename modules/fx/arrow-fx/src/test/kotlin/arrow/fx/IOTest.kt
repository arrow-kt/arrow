package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple4
import arrow.core.right
import arrow.fx.IO.Companion.just
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.concurrent.parMapN
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.extensions.io.monad.map
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.milliseconds
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.concurrency.SideEffect
import arrow.test.laws.ConcurrentLaws
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.newSingleThreadContext
import org.junit.runner.RunWith
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.EmptyCoroutineContext

@RunWith(KotlinTestRunner::class)
@kotlinx.coroutines.ObsoleteCoroutinesApi
class IOTest : UnitSpec() {

  private val other = newSingleThreadContext("other")
  private val all = newSingleThreadContext("all")

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

    "should yield immediate successful effect value" {
      val run = IO.effect { 1 }.unsafeRunSync()

      val expected = 1

      run shouldBe expected
    }

    "should yield immediate successful pure value" {
      val run = just(1).unsafeRunSync()

      val expected = 1

      run shouldBe expected
    }

    "should throw immediate failure by raiseError" {
      try {
        IO.raiseError(MyException()).unsafeRunSync()
        fail("")
      } catch (myException: MyException) {
        // Success
      } catch (throwable: Throwable) {
        fail("Should only throw MyException")
      }
    }

    "should return immediate value by uncancelable" {
      val run = just(1).uncancelable().unsafeRunSync()

      val expected = 1

      run shouldBe expected
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
      val never = just<Int?>(null)
      val received = never.unsafeRunTimed(100.milliseconds)

      received shouldBe Some(null)
    }

    "should return a null value from unsafeRunSync" {
      val value = just<Int?>(null).unsafeRunSync()

      value shouldBe null
    }

    "should complete when running a pure value with unsafeRunAsync" {
      val expected = 0
      just(expected).unsafeRunAsync { either ->
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
      IO.raiseError(MyException()).unsafeRunAsync { either ->
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
      val ioa = IO { throw exception }
      ioa.unsafeRunAsync { either ->
        either.fold({ it shouldBe exception }, { fail("") })
      }
    }

    "should not catch exceptions within run block with unsafeRunAsync" {
      try {
        val exception = MyException()
        val ioa = IO { throw exception }
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
      just(expected).runAsync { either ->
        either.fold({ fail("") }, { IO(IO.rethrow) { it shouldBe expected } })
      }
    }

    "should complete when running a return value with runAsync" {
      val expected = 0
      IO { expected }.runAsync { either ->
        either.fold({ fail("") }, { IO { it shouldBe expected } })
      }
    }

    "should return an error when running an exception with runAsync" {
      IO.raiseError(MyException()).runAsync { either ->
        either.fold({
          when (it) {
            is MyException -> {
              IO(IO.rethrow) { }
            }
            else -> fail("Should only throw MyException")
          }
        }, { fail("") })
      }
    }

    "should return exceptions within main block with runAsync" {
      val exception = MyException()
      val ioa = IO { throw exception }
      ioa.runAsync { either ->
        either.fold({ IO { it shouldBe exception } }, { fail("") })
      }
    }

    "should catch exceptions within run block with runAsync" {
      try {
        val exception = MyException()
        val ioa = IO { throw exception }
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
      val run = just(1).map { it + 1 }.unsafeRunSync()

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

    "effect is called on every run call" { all
      val sideEffect = SideEffect()
      val io = IO.effect { sideEffect.increment(); 1 }
      io.unsafeRunSync()
      io.unsafeRunSync()

      sideEffect.counter shouldBe 2
    }

    "effect is called on the correct ctx" {
      val io = IO.effect(all) { Thread.currentThread().name }
      io.unsafeRunSync() shouldBe "all"
    }

    "CoroutineContext state should be correctly managed between boundaries" {
      val ctxA = TestContext()
      val ctxB = CoroutineName("ctxB")
      // We have to explicitly reference kotlin.coroutines.coroutineContext since `TestContext` overrides this property.
      IO.effect { kotlin.coroutines.coroutineContext shouldBe EmptyCoroutineContext }
        .continueOn(ctxA)
        .flatMap { IO.effect { kotlin.coroutines.coroutineContext shouldBe ctxA } }
        .continueOn(ctxB)
        .flatMap { IO.effect { kotlin.coroutines.coroutineContext shouldBe ctxB } }
        .unsafeRunSync()
    }

    "fx can switch execution context state across not/bind" {
      val program = IO.fx {
        val ctx = !effect { kotlin.coroutines.coroutineContext }
        !effect { ctx shouldBe EmptyCoroutineContext }
        continueOn(all)
        val ctx2 = !effect { Thread.currentThread().name }
        !effect { ctx2 shouldBe "all" }
      }

      program.unsafeRunSync()
    }

    "fx can pass context state across not/bind" {
      val program = IO.fx {
        val ctx = !effect { kotlin.coroutines.coroutineContext }
        !effect { ctx shouldBe EmptyCoroutineContext }
        continueOn(CoroutineName("Simon"))
        val ctx2 = !effect { kotlin.coroutines.coroutineContext }
        !effect { ctx2 shouldBe CoroutineName("Simon") }
      }

      program.unsafeRunSync()
    }

    "fx will respect thread switching across not/bind" {
      val program = IO.fx {
        continueOn(all)
        val initialThread = !effect { Thread.currentThread().name }
        !(0..130).map { i -> suspend { i } }.sequence()
        val continuedThread = !effect { Thread.currentThread().name }
        continuedThread shouldBe initialThread
      }

      program.unsafeRunSync()
    }

    "unsafeRunTimed times out with None result" {
      val never = IO.async().never<Unit>().fix()
      val result = never.unsafeRunTimed(100.milliseconds)
      result shouldBe None
    }

    "parallel execution with single threaded context makes all IOs start at the same time" {
      val order = mutableListOf<Long>()

      fun makePar(num: Long): IO<Throwable, Long> =
        IO(newSingleThreadContext("$num")) {
          // Sleep according to my number
          Thread.sleep(num * 100)
        }.map {
          // Add myself to order list
          order.add(num)
          num
        }

      val result =
        all.parMapN(
          makePar(6), makePar(3), makePar(2), makePar(4), makePar(1), makePar(5)) { six, tree, two, four, one, five -> listOf(six, tree, two, four, one, five) }
          .unsafeRunSync()

      result shouldBe listOf(6L, 3, 2, 4, 1, 5)
      order.toList() shouldBe listOf(1L, 2, 3, 4, 5, 6)
    }

    "parallel execution preserves order for synchronous IOs" {
      val order = mutableListOf<Long>()

      fun IO<Throwable, Long>.order() =
        map {
          order.add(it)
          it
        }

      fun makePar(num: Long) =
        IO.concurrent()
          .sleep((num * 100).milliseconds)
          .map { num }.order()

      val result =
        all.parMapN(
          makePar(6), just(1L).order(), makePar(4), IO.defer { just(2L) }.order(), makePar(5), IO { 3L }.order()) { six, one, four, two, five, three -> listOf(six, one, four, two, five, three) }
          .unsafeRunSync()

      result shouldBe listOf(6L, 1, 4, 2, 5, 3)
      order.toList() shouldBe listOf(1L, 2, 3, 4, 5, 6)
    }

    "parallel mapping is done in the expected CoroutineContext" {
      fun makePar(num: Long) =
        IO(newSingleThreadContext("$num")) {
          // Sleep according to my number
          Thread.sleep(num * 100)
          num
        }

      val result =
        all.parMapN(
          makePar(6), just(1L), makePar(4), IO.defer { just(2L) }, makePar(5), IO { 3L }) { _, _, _, _, _, _ ->
          Thread.currentThread().name
        }.unsafeRunSync()

      // Will always result in "6" since it will always finish last (sleeps longest by makePar).
      result shouldBe "6"
    }

    "parallel IO#defer, IO#suspend and IO#async are run in the expected CoroutineContext" {
      val result =
        all.parMapN(
          IO { Thread.currentThread().name },
          IO.defer { just(Thread.currentThread().name) },
          IO.async<String> { cb -> cb(Thread.currentThread().name.right()) },
          IO(other) { Thread.currentThread().name },
          ::Tuple4)
          .unsafeRunSync()

      result shouldBe Tuple4("all", "all", "all", "other")
    }

    "unsafeRunAsyncCancellable should cancel correctly" {
      IO.async { cb: (Either<Throwable, Int>) -> Unit ->
        val cancel =
          IO(all) { }
            .flatMap { IO.async<Int> { cb -> Thread.sleep(500); cb(1.right()) } }
            .unsafeRunAsyncCancellable(OnCancel.Silent) {
              cb(it)
            }
        IO(other) { }
          .unsafeRunAsync { cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe None
    }

    "unsafeRunAsyncCancellable should throw the appropriate exception" {
      IO.async<Throwable> { cb ->
        val cancel =
          IO(all) { }
            .flatMap { IO.async<Int> { cb -> Thread.sleep(500); cb(1.right()) } }
            .unsafeRunAsyncCancellable(OnCancel.ThrowCancellationException) {
              it.fold({ t -> cb(t.right()) }, { })
            }
        IO(other) { }
          .unsafeRunAsync { cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe Some(OnCancel.CancellationException)
    }

    "IOFrame should always be called when using IO.Bind" {
      val ThrowableAsStringFrame = object : IOFrame<Throwable, Any?, IOOf<Throwable, String>> {
        override fun invoke(a: Any?) = just(a.toString())

        override fun recover(e: Throwable) = just(e.message ?: "")
      }

      forAll(Gen.string()) { message ->
        IO.Bind(IO.raiseError(RuntimeException(message)), ThrowableAsStringFrame as (Int) -> IO<Throwable, String>)
          .unsafeRunSync() == message
      }
    }

    "unsafeRunAsyncCancellable can cancel even for infinite asyncs" {
      IO.async { cb: (Either<Throwable, Int>) -> Unit ->
        val cancel =
          IO(all) { }
            .flatMap { IO.async<Int> { Thread.sleep(5000); } }
            .unsafeRunAsyncCancellable(OnCancel.ThrowCancellationException) {
              cb(it)
            }
        IO(other) { Thread.sleep(500); }
          .unsafeRunAsync { cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe None
    }

    "IO.binding should for comprehend over IO" {
      val result = IO.fx {
        val (x) = IO.just(1)
        val y = !IO { x + 1 }
        y
      }.fix()
      result.unsafeRunSync() shouldBe 2
    }

    "IO bracket cancellation should release resource with cancel exit status" {
      Promise.uncancelable<ForIO, ExitCase<Throwable>>(IO.async()).flatMap { p ->
        just(0L)
          .bracketCase(
            use = { IO.never },
            release = { _, exitCase -> p.complete(exitCase) }
          )
          .unsafeRunAsyncCancellable { }
          .invoke() // cancel immediately

        p.get()
      }.unsafeRunSync() shouldBe ExitCase.Canceled
    }

    "Cancelable should run CancelToken" {
      Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { p ->
        IO.concurrent().cancelable {
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

    "IO should cancel cancelable on dispose" {
      Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { latch ->
        IO {
          IO.cancelable<Unit> {
            latch.complete(Unit)
          }.unsafeRunAsyncCancellable { }
            .invoke()
        }.flatMap { latch.get() }
      }.unsafeRunSync()
    }

    "Bracket should be stack safe" {
      val size = 5000

      fun ioBracketLoop(i: Int): IO<Throwable, Int> =
        IO.unit.bracket(use = { just(i + 1) }, release = { IO.unit }).flatMap { ii ->
          if (ii < size) ioBracketLoop(ii)
          else just(ii)
        }

      just(1).flatMap { ioBracketLoop(0) }.unsafeRunSync() shouldBe size
    }

    "GuaranteeCase should be stack safe" {
      val size = 5000

      fun ioGuaranteeCase(i: Int): IO<Throwable, Int> =
        IO.unit.guaranteeCase { IO.unit }.flatMap {
          val ii = i + 1
          if (ii < size) ioGuaranteeCase(ii)
          else just(ii)
        }

      just(1).flatMap { ioGuaranteeCase(0) }.unsafeRunSync() shouldBe size
    }

    "Async should be stack safe" {
      val size = 5000

      fun ioAsync(i: Int): IO<Throwable, Int> = IO.async<Int> { cb ->
        cb(Right(i))
      }.flatMap { ii ->
        if (ii < size) ioAsync(ii + 1)
        else just(ii)
      }

      just(1).flatMap(::ioAsync).unsafeRunSync() shouldBe size
    }
  }
}

/** Represents a unique identifier context using object equality. */
internal class TestContext : AbstractCoroutineContextElement(TestContext) {
  companion object Key : kotlin.coroutines.CoroutineContext.Key<CoroutineName>
  override fun toString(): String = "TestContext(${Integer.toHexString(hashCode())})"
}
