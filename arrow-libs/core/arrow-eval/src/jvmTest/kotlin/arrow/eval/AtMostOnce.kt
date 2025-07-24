package arrow.eval

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class AtMostOnce {
  @Test
  fun atMostOnceNoSuspend(): Unit = runBlocking {
    var executionCount = 0

    val name: Eval<String> =
      Eval.atMostOnce {
        executionCount++
        Thread.sleep(1000)
        "John"
      }

    Array(20) {
      Thread { name() }.also { it.start() }
    }.forEach {
      it.join()
    }

    executionCount shouldBe 1
  }

  @Test
  fun atMostOnceSuspend(): Unit = runBlocking {
    var executionCount = 0

    val name: SuspendEval<String> =
      SuspendEval.atMostOnce {
        executionCount++
        delay(1.seconds)
        "John"
      }

    awaitAll(*Array(20) { async { name.run() } })
    executionCount shouldBe 1
  }
}
