package arrow.integrations.kotlinx

import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.generators.throwable
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineScope

@Suppress("IMPLICIT_NOTHING_AS_TYPE_PARAMETER")
@UseExperimental(ExperimentalCoroutinesApi::class)
class CoroutinesIntegrationTest : UnitSpec() {

  init {
    "suspended should throw" {
      forAll(Gen.throwable()) { e ->
        val ceh = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(ceh + TestCoroutineDispatcher())

        suspend fun asd(): Unit = throw e
        scope.launch {
          asd()
        }
        ceh.uncaughtExceptions[0] shouldBe e
        true
      }
    }

    "suspendedCancellable rethrows exceptions" {
      forAll(Gen.throwable()) { e ->
        val ceh = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(ceh + TestCoroutineDispatcher())

        scope.launch {
          IO { throw e }.suspendCancellable()
        }
        val caughtException = ceh.uncaughtExceptions[0]
        println("$caughtException == $e")
        caughtException shouldBeSameInstanceAs e
        true
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
          !promise.get().waitFor(1.seconds)
        }.unsafeRunSync() == i
      }
    }
  }
}
