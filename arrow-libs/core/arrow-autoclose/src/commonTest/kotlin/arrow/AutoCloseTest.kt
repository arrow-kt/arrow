package arrow

import arrow.atomic.AtomicBoolean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test

class AutoCloseTest {

  @Test
  fun canInstallResource() = runTest {
    var throwable: Throwable? = RuntimeException("Dummy exception")
    var wasActive = false
    val res = Resource()

    autoCloseScope {
      val r = autoClose({ res }) { r, e ->
        throwable = e
        r.shutdown()
      }
      wasActive = r.isActive()
    }

    throwable shouldBe null
    wasActive shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun canHandleWithFailingAutoClose() = runTest {
    var throwable: Throwable? = RuntimeException("Dummy exception")
    var wasActive = false
    val error = RuntimeException("BOOM!")
    val res = Resource()

    shouldThrow<RuntimeException> {
      autoCloseScope {
        val r = autoClose({ res }) { r, e ->
          throwable = e
          r.shutdown()
        }
        wasActive = r.isActive()
        throw error
      }
    } shouldBe error

    throwable shouldBe error
    wasActive shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun addsSuppressedErrors() = runTest {
    var throwable: Throwable? = RuntimeException("Dummy exception")
    var wasActive = false
    val error = RuntimeException("BOOM!")
    val error2 = RuntimeException("BOOM 2!")
    val error3 = RuntimeException("BOOM 3!")
    val res = Resource()

    val e = shouldThrow<RuntimeException> {
      autoCloseScope {
        val r = autoClose({ res }) { r, e ->
          throwable = e
          r.shutdown()
          throw error2
        }
        autoClose({ Resource() }) { _, _ -> throw error3 }
        wasActive = r.isActive()
        throw error
      }
    }

    e shouldBe error
    e.suppressedExceptions shouldBe listOf(error3, error2)
    throwable shouldBe error
    wasActive shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun handlesAcquireFailure() = runTest {
    var throwable: Throwable? = RuntimeException("Dummy exception")
    val error = RuntimeException("BOOM!")
    val error2 = RuntimeException("BOOM 2!")

    val e = shouldThrow<RuntimeException> {
      autoCloseScope {
        autoClose({ Resource() }) { r, e ->
          throwable = e
          r.shutdown()
          throw error2
        }
        autoClose<Int>({ throw error }) { _, _ -> }
      }
    }
    e shouldBe error
    e.suppressedExceptions shouldBe listOf(error2)
    throwable shouldBe error
  }

  @Test
  fun canInstallAutoCloseable() = runTest {
    var wasActive = false
    val res = Resource()

    autoCloseScope {
      val r = install(res)
      wasActive = r.isActive()
    }

    wasActive shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun closeTheAutoScopeOnCancellation() = runTest {
    var wasActive = false
    val res = Resource()

    shouldThrow<CancellationException> {
      autoCloseScope {
        val r = install(res)
        wasActive = r.isActive()
        throw CancellationException("BOOM!")
      }
    }.message shouldBe "BOOM!"

    wasActive shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun closeTheAutoScopeOnNonLocalReturn() = runTest {
    var wasActive = false
    val res = Resource()

    run {
      autoCloseScope {
        val r = install(res)
        wasActive = r.isActive()
        return@run
      }
    }

    wasActive shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun closeInReversedOrder() = runTest {
    val res1 = Resource()
    val res2 = Resource()
    val res3 = Resource()

    val wasActive = Channel<Boolean>(Channel.UNLIMITED)
    val closed = Channel<Resource>(Channel.UNLIMITED)

    autoCloseScope {
      val r1 = autoClose({ res1 }) { r, _ ->
        closed.trySend(r).getOrThrow()
        r.shutdown()
      }
      val r2 = autoClose({ res2 }) { r, _ ->
        closed.trySend(r).getOrThrow()
        r.shutdown()
      }
      val r3 = autoClose({ res3 }) { r, _ ->
        closed.trySend(r).getOrThrow()
        r.shutdown()
      }

      wasActive.trySend(r1.isActive()).getOrThrow()
      wasActive.trySend(r2.isActive()).getOrThrow()
      wasActive.trySend(r3.isActive()).getOrThrow()
      wasActive.close()
    }

    wasActive.toList() shouldBe listOf(true, true, true)
    closed.receive() shouldBe res3
    closed.receive() shouldBe res2
    closed.receive() shouldBe res1
    closed.cancel()
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
