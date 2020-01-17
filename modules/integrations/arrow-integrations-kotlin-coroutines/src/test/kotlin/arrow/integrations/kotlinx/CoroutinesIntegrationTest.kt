package arrow.integrations.kotlinx

import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class CoroutinesIntegrationTest : UnitSpec() {

  class MyException : Exception()

  init {
    "scope cancellation should cancel given IO" {
      // TODO currently failing with more iterations
      forAll(10, Gen.int()) {
        IO.fx {
          val scope = CoroutineScope(IO.dispatchers().default())
          val promise = !Promise<Unit>()
          !effect {
            scope.launchIO {
              IO.cancelable { promise.complete(Unit) }
            }
          }
//          !sleep(10.milliseconds)
          !effect { scope.cancel() }
          !promise.get().waitFor(2.seconds)
        }.unsafeRunSync() == Unit
      }
    }

    // TODO currently behaves the same way as coroutines
    "should rethrow exceptions within launchIO" {
      val exception = MyException()
      try {
        coroutineScope {
          launchIO {
            IO { throw exception }
            fail("Should rethrow the exception")
          }
        }
      } catch (myException: MyException) {
        // Success
      } catch (throwable: Throwable) {
        fail("Should only throw MyException but was $throwable")
      }
    }

    // DELETE
    "coroutines test" {
      val exception = MyException()
      try {
        coroutineScope {
          launch {
            suspend { throw exception }
            fail("Should rethrow the exception")
          }
        }
      } catch (myException: MyException) {
        // Success
      } catch (throwable: Throwable) {
        fail("Should only throw MyException but was $throwable")
      }
    }
  }
}
