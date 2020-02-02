package arrow.integrations.kotlinx

import arrow.core.Either
import arrow.core.None
import arrow.core.right
import arrow.core.some
import arrow.fx.IO
import arrow.fx.IOOf
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.bracket.guaranteeCase
import arrow.fx.typeclasses.ExitCase
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
        IO.fx {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val promise = !Promise<Int>()
          !effect {
            scope.launch {
              IO.cancelable<Unit> { promise.complete(i) }.suspendCancellable()
            }
          }
          !effect { scope.cancel() }
          !promise.get()
        }.unsafeRunSync() == i
      }
    }

    "suspendCancellable can cancel even for infinite asyncs" {
      IO.async { cb: (Either<Throwable, Int>) -> Unit ->
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        scope.launch {
          IO.async<Int> { }
            .onCancel(IO { cb(1.right()) })
            .suspendCancellable()
        }
        IO(other) { Thread.sleep(500) }
          .unsafeRunAsync { scope.cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe 1.some()
    }

    // --------------- unsafeRunScoped ---------------

    "should rethrow exceptions within run block with unsafeRunScoped" {
      forAll(Gen.throwable()) { e ->
        try {
          val scope = TestCoroutineScope(TestCoroutineDispatcher())
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
        IO(all) { -1 }
          .flatMap { IO.async<Int> { Thread.sleep(5000); } }
          .onCancel(IO { cb(1.right()) })
          .unsafeRunScoped(scope) {
            cb(it)
          }
        IO(other) { Thread.sleep(500) }
          .unsafeRunAsync { scope.cancel() }
      }.unsafeRunTimed(2.seconds) shouldBe 1.some()
    }

    "should complete when running a pure value with unsafeRunScoped" {
      forAll(Gen.int()) { i ->
        val scope = TestCoroutineScope(TestCoroutineDispatcher())
        IO.async<Int> { cb ->
          IO.just(i).unsafeRunScoped(scope) { either ->
            either.fold({ fail("") }, { cb(it.right()) })
          }
        }.unsafeRunSync() == i
      }
    }
  }
}

fun <A> IOOf<A>.onCancel(token: IOOf<Unit>): IO<A> =
  guaranteeCase { case ->
    when (case) {
      ExitCase.Canceled -> token
      else -> IO.unit
    }
  }
