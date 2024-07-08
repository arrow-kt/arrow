package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import java.util.concurrent.atomic.AtomicBoolean
import java.lang.AutoCloseable
import java.io.Closeable
import io.kotest.property.Arb
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlin.test.DefaultAsserter.fail
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ResourceTestJvm {

  class AutoCloseableTest : AutoCloseable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  class CloseableTest : Closeable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  @Test fun autoCloseableCloses() = runTest {
      val t = AutoCloseableTest()
      resourceScope {
        autoCloseable { t }
      }

      t.didClose.get() shouldBe true
  }

  @Test fun autoCloseableClosesOnError() = runTest {
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

  @Test fun closeableCloses() = runTest {
      val t = CloseableTest()

      resourceScope {
        closeable { t }
      }

      t.didClose.get() shouldBe true
  }

  @Test fun closeableClosesOnError() = runTest {
    checkAll(10, Arb.throwable()) { throwable ->
      val t = CloseableTest()

      shouldThrow<Exception> {
        resourceScope {
          closeable { t }
          throw throwable
        }
      } shouldBe throwable

      t.didClose.get() shouldBe true
    }
  }

  @Test
  fun blowTheScopeOnFatal() = runTest {
    shouldThrow<LinkageError> {
      resourceScope {
        install({  }) { _, _ -> fail("Should never come here") }
        throw LinkageError("BOOM!")
      }
    }.message shouldBe "BOOM!"
  }
}
