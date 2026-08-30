package arrow

import arrow.atomic.AtomicBoolean
import arrow.core.ControlCancellationException
import arrow.core.InternalArrowApi
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.assertionCounter
import io.kotest.common.reflection.bestName
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test

@OptIn(InternalArrowApi::class)
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
        val _ = autoClose({ Resource() }) { _, _ -> throw error3 }
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
        val _ = autoClose({ Resource() }) { r, e ->
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

    val _ = autoCloseScope {
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

  @Test
  fun normalRaise() = shouldAutoCloseWithSecond({}) { throw ControlCancellationException("second") }

  @Test
  fun returnRaise() = shouldAutoCloseWithSecond({ return }) { throw ControlCancellationException("second") }

  @Test
  fun raiseRaise() = shouldAutoCloseWithSecond({ throw ControlCancellationException("first") }) { throw ControlCancellationException("second") }

  @Test
  fun cancelRaise() = shouldAutoCloseWithFirst({ throw CancellationException("first") }) { throw ControlCancellationException("second") }

  @Test
  fun throwRaise() = shouldAutoCloseWithFirst({ throw RuntimeException("first") }) { throw ControlCancellationException("second") }

  @Test
  fun normalCancel() = shouldAutoCloseWithSecond({}) { throw CancellationException("second") }

  @Test
  fun returnCancel() = shouldAutoCloseWithSecond({ return }) { throw CancellationException("second") }

  @Test
  fun raiseCancel() = shouldAutoCloseWithSecond({ throw ControlCancellationException("first") }) { throw CancellationException("second") }

  @Test
  fun cancelCancel() = shouldAutoCloseWithFirst({ throw CancellationException("first") }) { throw CancellationException("second") }

  @Test
  fun throwCancel() = shouldAutoCloseWithFirst({ throw RuntimeException("first") }) { throw CancellationException("second") }

  @Test
  fun normalThrow() = shouldAutoCloseWithSecond({}) { throw RuntimeException("second") }

  @Test
  fun returnThrow() = shouldAutoCloseWithSecond({ return }) { throw RuntimeException("second") }

  @Test
  fun raiseThrow() = shouldAutoCloseWithSecond({ throw ControlCancellationException("first") }) { throw RuntimeException("second") }

  @Test
  fun cancelThrow() = shouldAutoCloseWithFirst({ throw CancellationException("first") }) { throw RuntimeException("second") }

  @Test
  fun throwThrow() = shouldAutoCloseWithFirst({ throw RuntimeException("first") }) { throw RuntimeException("second") }
}

// copied from Kotest so we can inline it
inline fun <reified T : Throwable> shouldThrow(block: () -> Any?): T {
  assertionCounter.inc()
  val expectedExceptionClass = T::class
  val thrownThrowable = try {
    val _ = block()
    null  // Can't throw failure here directly, as it would be caught by the catch clause, and it's an AssertionError, which is a special case
  } catch (thrown: Throwable) {
    thrown
  }

  return when (thrownThrowable) {
    null -> throw AssertionErrorBuilder.create()
      .withMessage("Expected exception ${expectedExceptionClass.bestName()} but no exception was thrown.")
      .build()
    is T -> thrownThrowable
    is AssertionError -> throw thrownThrowable
    else -> throw AssertionErrorBuilder.create()
      .withMessage("Expected exception ${expectedExceptionClass.bestName()} but a ${thrownThrowable::class.simpleName} was thrown instead.")
      .withCause(thrownThrowable)
      .build()
  }
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

private suspend fun <T> CompletableDeferred<T>.shouldHaveCompleted(): T {
  isCompleted shouldBe true
  return await()
}

private inline fun shouldAutoCloseWithFirst(first: () -> Unit, crossinline second: () -> Nothing) {
  var firstThrowable: Throwable? = null
  lateinit var secondThrowable: Throwable
  try {
    autoCloseScope {
      onClose {
        it shouldBe firstThrowable
        peekThrowable(second) { secondThrowable = it }
      }
      peekThrowable(first) { firstThrowable = it }
    }
  } catch (e: Throwable) {
    e shouldBe firstThrowable
    e.suppressedExceptions shouldHaveSingleElement secondThrowable
  } finally {
    val _ = secondThrowable // ensure that onClose ran
  }
}

private inline fun shouldAutoCloseWithSecond(first: () -> Unit, crossinline second: () -> Nothing) {
  var firstThrowable: Throwable? = null
  lateinit var secondThrowable: Throwable
  var finishedWithThrowable = false
  try {
    autoCloseScope {
      onClose {
        it shouldBe firstThrowable
        peekThrowable(second) { secondThrowable = it }
      }
      peekThrowable(first) { firstThrowable = it }
    }
  } catch (e: Throwable) {
    e shouldBe secondThrowable
    if (firstThrowable != null) e.suppressedExceptions shouldHaveSingleElement firstThrowable
    finishedWithThrowable = true
  } finally {
    finishedWithThrowable shouldBe true // otherwise, we finished with first somehow, either non-locally, or with Unit
  }
}

private inline fun <R> peekThrowable(block: () -> R, peek: (Throwable) -> Unit): R = try {
  block()
} catch (e: Throwable) {
  peek(e)
  throw e
}
