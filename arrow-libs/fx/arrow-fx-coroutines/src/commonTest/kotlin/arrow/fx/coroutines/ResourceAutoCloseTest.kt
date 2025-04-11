@file:OptIn(ExperimentalAtomicApi::class)

package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.Test

class ResourceAutoCloseTest {

  class AutoCloseableTest : AutoCloseable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.store(true)
  }

  @Test
  fun autoCloseableCloses() = runTest {
    val t = AutoCloseableTest()
    resourceScope {
      autoCloseable { t }
    }

    t.didClose.load() shouldBe true
  }

  @Test
  fun autoCloseableClosesOnError() = runTest {
    checkAll(10, Arb.throwable()) { throwable ->
      val t = AutoCloseableTest()

      shouldThrow<Exception> {
        resourceScope {
          autoCloseable { t }
          throw throwable
        }
      } shouldBe throwable

      t.didClose.load() shouldBe true
    }
  }


  @Test
  fun autoClosableIsNonCancellable() = runTest {
    val t = AutoCloseableTest()
    val exit = CompletableDeferred<ExitCase>()
    val waitingToBeCancelled = CompletableDeferred<Unit>()
    val cancelled = CompletableDeferred<Unit>()

    val job = launch {
      resourceScope {
        onRelease { require(exit.complete(it)) }
        autoCloseable {
          waitingToBeCancelled.complete(Unit)
          cancelled.await()
          t
        }
        yield()
      }
    }

    waitingToBeCancelled.await()
    job.cancel("BOOM!")
    cancelled.complete(Unit)
    job.join()

    t.didClose.load() shouldBe true
    exit.shouldHaveCompleted()
      .shouldBeTypeOf<ExitCase.Cancelled>()
      .exception
      .message shouldBe "BOOM!"
  }
}
