package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicBoolean
import java.lang.AutoCloseable
import java.io.Closeable
import io.kotest.property.Arb
import io.kotest.property.checkAll

class ResourceTestJvm : StringSpec({

  class AutoCloseableTest : AutoCloseable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  class CloseableTest : Closeable {
    val didClose = AtomicBoolean(false)
    override fun close() = didClose.set(true)
  }

  "AutoCloseable closes" {
      val t = AutoCloseableTest()

      autoCloseable { t }.use {}

      t.didClose.get() shouldBe true
  }

  "AutoCloseable closes on error" {
    checkAll(Arb.throwable()) { throwable ->
      val t = AutoCloseableTest()

      shouldThrow<Exception> {
        autoCloseable { t }.use<Nothing> { throw throwable }
      } shouldBe throwable

      t.didClose.get() shouldBe true
    }
  }

  "Closeable closes" {
      val t = CloseableTest()

      closeable { t }.use {}

      t.didClose.get() shouldBe true
  }

  "Closeable closes on error" {
    checkAll(Arb.throwable()) { throwable ->
      val t = CloseableTest()

      shouldThrow<Exception> {
        closeable { t }.use<Nothing> { throw throwable }
      } shouldBe throwable

      t.didClose.get() shouldBe true
    }
  }
})
