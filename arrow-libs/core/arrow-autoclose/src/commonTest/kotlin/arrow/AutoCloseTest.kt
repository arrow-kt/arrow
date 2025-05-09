package arrow

import arrow.atomic.AtomicBoolean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test

class AutoCloseTest {

  @Test
  fun canInstallResource() = runTest {
    val promise = CompletableDeferred<Throwable?>()
    val wasActive = CompletableDeferred<Boolean>()
    val res = Resource()

    autoCloseScope {
      val r = autoClose({ res }) { r, e ->
        require(promise.complete(e))
        r.shutdown()
      }
      require(wasActive.complete(r.isActive()))
    }

    promise.shouldHaveCompleted() shouldBe null
    wasActive.shouldHaveCompleted() shouldBe true
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
          require(promise.complete(e))
          r.shutdown()
        }
        require(wasActive.complete(r.isActive()))
        throw error
      }
    } shouldBe error

    promise.shouldHaveCompleted() shouldBe error
    wasActive.shouldHaveCompleted() shouldBe true
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
          require(promise.complete(e))
          r.shutdown()
          throw error2
        }
        autoClose({ Resource() }) { _, _ -> throw error3 }
        require(wasActive.complete(r.isActive()))
        throw error
      }
    }

    e shouldBe error
    e.suppressedExceptions shouldBe listOf(error3, error2)
    promise.shouldHaveCompleted() shouldBe error
    wasActive.shouldHaveCompleted() shouldBe true
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
          require(promise.complete(e))
          r.shutdown()
          throw error2
        }
        autoClose<Int>({ throw error }) { _, _ -> }
      }
    }
    e shouldBe error
    e.suppressedExceptions shouldBe listOf(error2)
    promise.shouldHaveCompleted() shouldBe error
  }

  @Test
  fun canInstallAutoCloseable() = runTest {
    val wasActive = CompletableDeferred<Boolean>()
    val res = Resource()

    autoCloseScope {
      val r = install(res)
      require(wasActive.complete(r.isActive()))
    }

    wasActive.shouldHaveCompleted() shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun closeTheAutoScopeOnCancellation() = runTest {
    val wasActive = CompletableDeferred<Boolean>()
    val res = Resource()

    shouldThrow<CancellationException> {
      autoCloseScope {
        val r = install(res)
        require(wasActive.complete(r.isActive()))
        throw CancellationException("BOOM!")
      }
    }.message shouldBe "BOOM!"

    wasActive.shouldHaveCompleted() shouldBe true
    res.isActive() shouldBe false
  }

  @Test
  fun closeTheAutoScopeOnNonLocalReturn() = runTest {
    val wasActive = CompletableDeferred<Boolean>()
    val res = Resource()

    run {
      autoCloseScope {
        val r = install(res)
        require(wasActive.complete(r.isActive()))
        return@run
      }
    }

    wasActive.shouldHaveCompleted() shouldBe true
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


  @OptIn(ExperimentalStdlibApi::class) // 'AutoCloseable' in stdlib < 2.0
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

  private suspend fun <T> CompletableDeferred<T>.shouldHaveCompleted(): T {
    isCompleted shouldBe true
    return await()
  }
}
