package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean
import java.lang.AutoCloseable
import java.io.Closeable
import io.kotest.property.Arb
import io.kotest.property.checkAll

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
      val t = AutoCloseableTest()

      Resource.fromAutoCloseable { t }
        .use {}

      t.didClose.get() shouldBe true
  }

  "AutoCloseable closes on error" {
    checkAll(Arb.throwable()) { throwable ->
      val t = AutoCloseableTest()

      shouldThrow<Exception> {
        Resource.fromAutoCloseable { t }
          .use { throw throwable }
      } shouldBe throwable

      t.didClose.get() shouldBe true
    }
  }

  "Closeable closes" {
      val t = CloseableTest()

      Resource.fromCloseable { t }
        .use {}

      t.didClose.get() shouldBe true
  }

  "Closeable closes on error" {
    checkAll(Arb.throwable()) { throwable ->
      val t = CloseableTest()

      shouldThrow<Exception> {
        Resource.fromCloseable { t }
          .use { throw throwable }
      } shouldBe throwable

      t.didClose.get() shouldBe true
    }
  }
})
