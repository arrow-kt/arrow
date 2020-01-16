package arrow.integrations.kotlinx

import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class CoroutinesIntegrationTest : UnitSpec() {

  init {
    "test1" {
      forAll(Gen.int()) { i ->
        IO.fx {
          val scope = CoroutineScope(IO.dispatchers().default())​
          val promise = !Promise<Int>()
          scope.launchIO {
            sleeper().onCancel(promise.complete(i))
          }
          !sleep(2.seconds)
          !effect { scope.cancel() }
          !promise.get().waitFor(5.seconds)
        } == IO { i }

      }
    }
  }
}

//suspend fun main(): Unit = IO.fx {
//    val (_, cancel) = !sleeper()
//        .onCancel(IO.effect { println("\nSomeone made me wake up from my nap...") })
//        .fork()
//
//    !sleep(5.seconds)
//    !cancel
//}.suspended()

fun sleeper(): IO<Unit> = IO.fx {
  !effect { println("I am sleepy. I'm going to nap") }
  !sleep(2.seconds)
  !effect { println("2 second nap.. Going to sleep some more") }
  !sleeper()
}
