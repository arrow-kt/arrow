package arrow.integrations.kotlinx

import arrow.core.Right
import arrow.core.Some
import arrow.fx.IO
import arrow.fx.IOResult
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.effectMap
import arrow.fx.flatMap
import arrow.fx.onCancel
import arrow.fx.typeclasses.milliseconds
import arrow.fx.typeclasses.seconds
import arrow.fx.unsafeRunAsync
import arrow.fx.unsafeRunSync
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
      forAll(Gen.throwable()) { expected ->
        val ceh = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(ceh + TestCoroutineDispatcher())

        scope.launch {
          IO { throw expected }.suspendCancellable()
        }

        val actual = ceh.uncaughtExceptions[0]
        // suspendCancellableCoroutine copy and re-throws the exception so we need to compare the type
        // see https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/src/internal/StackTraceRecovery.kt#L68
        actual::class == expected::class
      }
    }

    "suspendedCancellable should resume with right block" {
      forAll(Gen.int()) { i ->
        val ceh = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(ceh + TestCoroutineDispatcher())
        scope.launch {
          val first = IO { i + 1 }.suspendCancellable()
          val second = IO { first + 1 }.suspendCancellable()
          val third = IO { second + 1 }.suspendCancellable()

          third shouldBe i + 3
        }

        ceh.uncaughtExceptions.isEmpty()
      }
    }

    "scope cancellation should cancel suspendedCancellable IO" {
      forAll(Gen.int()) { i ->
        IO.fx<Nothing, Int> {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val promise = !Promise<Int>()
          !IO.effect {
            scope.launch {
              IO.cancelable<Nothing, Unit> { promise.complete(i) }.suspendCancellable()
            }
          }
          !IO.effect { scope.cancel() }
          !promise.get()
        }.unsafeRunTimed(500.milliseconds) == Some(Right(i))
      }
    }

    "suspendCancellable can cancel even for infinite asyncs" {
      IO.async { cb: (IOResult<Nothing, Int>) -> Unit ->
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        scope.launch {
          IO.never
            .onCancel(IO { cb(IOResult.Success(1)) })
            .suspendCancellable()
        }
        IO.sleep(500.milliseconds)
          .unsafeRunAsync { scope.cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe Some(Right(1))
    }

    // --------------- unsafeRunScoped ---------------

    "should rethrow exceptions within run block with unsafeRunScoped" {
      forAll(Gen.throwable()) { e ->
        try {
          val scope = TestCoroutineScope(TestCoroutineDispatcher())
          val ioa = IO<Int> { throw e }
          ioa.unsafeRunScoped(scope) { result ->
            result.fold({ throw it }, { fail("") }, { fail("") })
          }
          fail("Should rethrow the exception")
        } catch (throwable: Throwable) {
          throwable == e
        }
      }
    }

    "unsafeRunScoped should cancel correctly" {
      forAll(Gen.int()) { i ->
        IO.fx<Nothing, Int> {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val promise = !Promise<Int>()
          !IO.effect {
            IO.cancelable<Nothing, Unit> { promise.complete(i) }.unsafeRunScoped(scope) { }
          }
          !IO.effect { scope.cancel() }
          !promise.get()
        }.unsafeRunTimed(500.milliseconds) == Some(Right(i))
      }
    }

    "unsafeRunScoped can cancel even for infinite asyncs" {
        IO.fx<Nothing, Int> {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val promise = !Promise<Int>()
          !IO.effect {
            IO(all) { -1 }.flatMap { IO.never }.onCancel(promise.complete(1)).unsafeRunScoped(scope) { }
          }
          !IO.sleep(500.milliseconds).effectMap { scope.cancel() }
          !promise.get()
        }.unsafeRunTimed(2.seconds) shouldBe Some(Right(1))
    }

    "should complete when running a pure value with unsafeRunScoped" {
      forAll(Gen.int()) { i ->
        val scope = TestCoroutineScope(TestCoroutineDispatcher())
        IO.async<Nothing, Int> { cb ->
          IO.just(i).unsafeRunScoped(scope) { result ->
            result.fold({ fail("") }, { fail("") }, { cb(IOResult.Success(it)) })
          }
        }.unsafeRunSync() == i
      }
    }
  }
}
