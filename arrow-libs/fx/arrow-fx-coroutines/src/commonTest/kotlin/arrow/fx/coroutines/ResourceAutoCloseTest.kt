package arrow.fx.coroutines

import arrow.atomic.AtomicBoolean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ResourceAutoCloseTest {

  class AutoCloseableTest : AutoCloseable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  @Test
  fun autoCloseableCloses() = runTest {
    val t = AutoCloseableTest()
    resourceScope {
      autoCloseable { t }
    }

    t.didClose.get() shouldBe true
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

      t.didClose.get() shouldBe true
    }
  }
}
