package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean
import java.lang.AutoCloseable
import java.io.Closeable
import io.kotest.property.Arb

class ResourceTestJvm : ArrowFxSpec(spec = {

  class AutoCloseableTest() : AutoCloseable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  class CloseableTest() : Closeable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  "AutoCloseable closes" {
    checkAll {
      val t = AutoCloseableTest()

      Resource.autoCloseable { t }
        .use {}

      t.didClose.get() shouldBe true
    }
  }

  "AutoCloseable closes on error" {
    checkAll(Arb.throwable()) { throwable ->
      val t = AutoCloseableTest()

      shouldThrow<Exception> {
        Resource.autoCloseable { t }
          .use { throw throwable }
      } shouldBe throwable

      t.didClose.get() shouldBe true
    }
  }

  "Closeable closes" {
    checkAll() {
      val t = CloseableTest()

      Resource.closeable { t }
        .use {}

      t.didClose.get() shouldBe true
    }
  }

  "Closeable closes on error" {
    checkAll(Arb.throwable()) { throwable ->
      val t = CloseableTest()

      shouldThrow<Exception> {
        Resource.closeable { t }
          .use { throw throwable }
      } shouldBe throwable

      t.didClose.get() shouldBe true
    }
  }
})
