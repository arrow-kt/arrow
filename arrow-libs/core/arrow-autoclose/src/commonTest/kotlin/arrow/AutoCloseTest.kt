package arrow

import arrow.atomic.AtomicBoolean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class AutoCloseTest {

  @Test
  fun canInstallResource() = runTest {
    val promise = CompletableDeferred<Throwable?>()
    val wasActive = CompletableDeferred<Boolean>()
    val res = Resource()

    autoCloseScope {
      val r = autoClose({ res }) { r, e ->
        promise.complete(e)
        r.shutdown()
      }
      wasActive.complete(r.isActive())
    }

    promise.await() shouldBe null
    wasActive.await() shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun canHandleWithFailingAutoClose() = runTest {
    val promise = CompletableDeferred<Throwable?>()
    val wasActive = CompletableDeferred<Boolean>()
    val error = RuntimeException("BOOM!")
    val res = Resource()

    shouldThrow<RuntimeException> {
      autoCloseScope {
        val r = autoClose({ res }) { r, e ->
          promise.complete(e)
          r.shutdown()
        }
        wasActive.complete(r.isActive())
        throw error
      }
    } shouldBe error

    promise.await() shouldBe error
    wasActive.await() shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun addsSuppressedErrors() = runTest {
    val promise = CompletableDeferred<Throwable?>()
    val wasActive = CompletableDeferred<Boolean>()
    val error = RuntimeException("BOOM!")
    val error2 = RuntimeException("BOOM 2!")
    val error3 = RuntimeException("BOOM 3!")
    val res = Resource()

    val e = shouldThrow<RuntimeException> {
      autoCloseScope {
        val r = autoClose({ res }) { r, e ->
          promise.complete(e)
          r.shutdown()
          throw error2
        }
        autoClose({ Resource() }) { _, _ -> throw error3 }
        wasActive.complete(r.isActive())
        throw error
      }
    }

    e shouldBe error
    e.suppressedExceptions shouldBe listOf(error2, error3)
    promise.await() shouldBe error
    wasActive.await() shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun handlesAcquireFailure() = runTest {
    val promise = CompletableDeferred<Throwable?>()
    val error = RuntimeException("BOOM!")
    val error2 = RuntimeException("BOOM 2!")

    val e = shouldThrow<RuntimeException> {
      autoCloseScope {
        autoClose({ Resource() }) { r, e ->
          promise.complete(e)
          r.shutdown()
          throw error2
        }
        autoClose<Int>({ throw error }) { _, _ -> }
      }
    }
    e shouldBe error
    e.suppressedExceptions shouldBe listOf(error2)
    promise.await() shouldBe error
  }

  @Test
  fun canInstallAutoCloseable() = runTest {
    val wasActive = CompletableDeferred<Boolean>()
    val res = Resource()

    autoCloseScope {
      val r = install(res)
      wasActive.complete(r.isActive())
    }

    wasActive.await() shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun closeTheAutoScopeOnCancellation() = runTest {
    val wasActive = CompletableDeferred<Boolean>()
    val res = Resource()

    shouldThrow<CancellationException> {
      autoCloseScope {
        val r = install(res)
        wasActive.complete(r.isActive())
        throw CancellationException("BOOM!")
      }
    }.message shouldBe "BOOM!"

    wasActive.await() shouldBe true
    res.isActive() shouldBe false
  }

  @OptIn(ExperimentalStdlibApi::class)
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
