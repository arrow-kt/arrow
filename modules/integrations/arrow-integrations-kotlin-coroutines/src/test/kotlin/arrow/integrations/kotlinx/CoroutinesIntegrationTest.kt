package arrow.integrations.kotlinx

import arrow.core.Left
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.generators.throwable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class CoroutinesIntegrationTest : UnitSpec() {
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

    "given IO when exception occurs should return Either.Left" {
      forAll(Gen.throwable()) { error ->
        IO.fx {
          val scope = CoroutineScope(IO.dispatchers().default())
          !effect {
            scope.launchIO {
              IO { throw error }.unsafeRunSync()
            }
          }
        }.attempt().unsafeRunSync() == Left(error)
      }
    }
  }
}
