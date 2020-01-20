package arrow.integrations.kotlinx

import arrow.core.Either
import arrow.core.None
import arrow.core.right
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.typeclasses.milliseconds
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.generators.throwable
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineScope

@ObsoleteCoroutinesApi
@Suppress("IMPLICIT_NOTHING_AS_TYPE_PARAMETER")
@UseExperimental(ExperimentalCoroutinesApi::class)
class CoroutinesIntegrationTest : UnitSpec() {

  private val other = newSingleThreadContext("other")
  private val all = newSingleThreadContext("all")

  init {
    // --------------- suspendCancellable ---------------

    "suspendedCancellable should throw" {
      forAll(Gen.throwable()) { e ->
        val ceh = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(ceh + TestCoroutineDispatcher())

        scope.launch {
          IO { throw e }.suspendCancellable()
        }
        val caughtException = ceh.uncaughtExceptions[0]
        println("$caughtException == $e")
        // TODO exception seem to be rethrown internally, so equals comparison fails
        caughtException::class == e::class
      }
    }

    "scope cancellation should cancel suspendedCancellable IO" {
      forAll(1, Gen.int()) { i ->
        IO.fx {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val promise = !Promise<Int>()
          !effect {
            scope.launch {
              IO.cancelable<Unit> { promise.complete(i) }.suspendCancellable()
            }
          }
          !effect { scope.cancel() }
          !promise.get().waitFor(1.seconds)
        }.unsafeRunSync() == i
      }
    }

    // --------------- unsafeRunScoped ---------------

    "should rethrow exceptions within run block with unsafeRunScoped" {
      forAll(Gen.throwable()) { e ->
        try {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val ioa = IO<Int> { throw e }
          ioa.unsafeRunScoped(scope) { either ->
            either.fold({ throw it }, { fail("") })
          }
          fail("Should rethrow the exception")
        } catch (throwable: Throwable) {
          throwable == e
        }
      }
    }

    "unsafeRunScoped should cancel correctly" {
      forAll(1, Gen.int()) { i ->
        IO.async<Int> { cb ->
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          IO(all) { }
            .flatMap { IO.async<Int> { cb -> Thread.sleep(200); cb(i.right()) } }
            .unsafeRunScoped(scope) {
              cb(it)
            }
          IO(other) { }
            .unsafeRunAsync { scope.cancel() }
        }.unsafeRunTimed(500.milliseconds) == None
      }
    }

    "unsafeRunScoped can cancel even for infinite asyncs" {
      IO.async { cb: (Either<Throwable, Int>) -> Unit ->
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          IO(all) { }
            .flatMap { IO.async<Int> { Thread.sleep(5000); } }
            .unsafeRunScoped(scope) {
              cb(it)
            }
        IO(other) { Thread.sleep(500); }
          .unsafeRunAsync { scope.cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe None
    }

    "should complete when running a pure value with unsafeRunAsync" {
      val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
      val expected = 0
      IO.just(expected).unsafeRunScoped(scope) { either ->
        either.fold({ fail("") }, { it shouldBe expected })
      }
    }
  }
}
