package arrow

import arrow.atomic.AtomicBoolean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AutoCloseJvmTest {

  @Test
  fun blowTheAutoScopeOnFatal() = runTest {
    val wasActive = CompletableDeferred<Boolean>()
    val res = Resource()

    shouldThrow<LinkageError> {
      autoCloseScope {
        val r = install(res)
        wasActive.complete(r.isActive())
        throw LinkageError("BOOM!")
      }
    }.message shouldBe "BOOM!"

    wasActive.await() shouldBe true
    res.isActive() shouldBe true
  }

  private class Resource : AutoCloseable {
    private val isActive = AtomicBoolean(true)

    fun isActive(): Boolean = isActive.get()

    fun shutdown() {
      require(isActive.compareAndSet(expected = true, new = false)) {
        "Already shut down"
      }
    }

    override fun close() {
      shutdown()
    }
  }
}
