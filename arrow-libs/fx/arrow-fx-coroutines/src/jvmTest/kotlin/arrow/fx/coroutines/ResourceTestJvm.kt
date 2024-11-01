package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean
import java.lang.AutoCloseable
import java.io.Closeable
import io.kotest.property.Arb
import io.kotest.property.checkAll
import kotlin.test.DefaultAsserter.fail
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ResourceTestJvm {

  class AutoCloseableJvmTest : AutoCloseable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  class CloseableTest : Closeable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  @Test fun autoCloseableJvmCloses() = runTest {
      val t = AutoCloseableJvmTest()
      resourceScope {
        autoCloseable { t }
      }

      t.didClose.get() shouldBe true
  }

  @Test fun autoCloseableJvmClosesOnError() = runTest {
    checkAll(10, Arb.throwable()) { throwable ->
      val t = AutoCloseableJvmTest()

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
    val error = shouldThrow<LinkageError> {
      resourceScope {
        install({  }) { _, _ -> fail("Should never come here") }
        throw LinkageError("BOOM!")
      }
    }
    error.message shouldBe "BOOM!"
    error.suppressedExceptions.shouldBeEmpty()
  }

  @Test
  fun blowTheScopeOnFatalInRelease() = runTest {
    var isLastClosed = false
    val error = shouldThrow<LinkageError> {
      resourceScope {
        install({  }) { _, _ -> fail("Should never come here") }
        onRelease { throw LinkageError("BOOM!") }
        onClose { isLastClosed = true }
        onClose { throw RuntimeException() }
      }
    }
    error.message shouldBe "BOOM!"
    error.suppressedExceptions.shouldBeEmpty()
    isLastClosed shouldBe true
  }
}
