package arrow.fx.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RacingTest {
  @Test
  fun immediateWinner() = runTest {
    val result = racing {
      race { 1 }
    }
    assertEquals(1, result)
  }

  @Test
  fun racerCanSuspend() = runTest {
    val result = racing {
      race {
        yield()
        1
      }
    }
    assertEquals(1, result)
  }

  @Test
  fun testMultipleRacers() = runTest {
    val result = racing {
      race { awaitCancellation() }
      race { "fast" }
    }
    assertEquals("fast", result)
  }

  @Test
  fun testRaceLosersCancellation() = runTest {
    val slowRacerCancelled = CompletableDeferred<CancellationException>()

    val result = racing {
      race {
        try {
          awaitCancellation()
        } catch (e: CancellationException) {
          slowRacerCancelled.complete(e)
          throw e
        }
      }
      race {
        yield()
        "fast"
      }
    }

    assertEquals("fast", result)
    withTimeoutOrNull(600.seconds) { slowRacerCancelled.await() }
  }

  @Test
  fun testRaceLosersCancellationFinalizers() = runTest {
    val launchFinalizerCalled = CompletableDeferred<Boolean>()
    val asyncFinalizerCalled = CompletableDeferred<Boolean>()
    val suspendFinalizerCalled = CompletableDeferred<Boolean>()

    val result = racing {
      race {
        launch {
          try {
            awaitCancellation()
          } finally {
            launchFinalizerCalled.complete(true)
          }
        }
        async {
          try {
            awaitCancellation()
          } finally {
            asyncFinalizerCalled.complete(true)
          }
        }

        try {
          awaitCancellation()
        } finally {
          suspendFinalizerCalled.complete(true)
        }
      }
      race {
        yield()
        "fast"
      }
    }

    assertEquals("fast", result)
    withTimeoutOrNull(600.seconds) { launchFinalizerCalled.await() }
    withTimeoutOrNull(600.seconds) { asyncFinalizerCalled.await() }
    withTimeoutOrNull(600.seconds) { suspendFinalizerCalled.await() }
  }

  @Test
  fun testExceptionHandling() = runTest {
    val exceptionHandled = CompletableDeferred<Throwable>()

    val handleException = CoroutineExceptionHandler { _, exception -> exceptionHandled.complete(exception) }
    val result = racing {
      race(handleException) { throw IllegalStateException("Test exception") }
      race(handleException) { "success" }
    }

    assertEquals("success", result)
    val exception = withTimeoutOrNull(600.seconds) { exceptionHandled.await() }
    assertIs<IllegalStateException>(exception)
    assertEquals("Test exception", exception.message)
  }

  @Test
  fun testRaceOrThrow() = runTest {
    assertFailsWith<IllegalStateException> {
      racing {
        raceOrThrow { throw IllegalStateException("Test exception") }
        race { awaitCancellation() }
      }
    }
  }

  @Test
  fun testCancellationPropagation() = runTest {
    val latch = CompletableDeferred<Unit>()
    val cancelled = CompletableDeferred<CancellationException>()
    val job = launch {
      racing {
        race {
          latch.complete(Unit)
          try {
            awaitCancellation()
          } catch (e: CancellationException) {
            cancelled.complete(e)
          }
        }
      }
    }

    latch.await()
    job.cancelAndJoin()

    withTimeoutOrNull(600.milliseconds) { cancelled.await() }
  }

  @Test
  fun testNestedRacing() = runTest {
    val result = racing {
      race {
        racing {
          race { awaitCancellation() }
          race { "inner fast" }
        }
      }
      race { awaitCancellation() }
    }

    assertEquals("inner fast", result)
  }

  @Test
  fun testCoroutineExceptionHandler() = runTest {
    val exceptionHandled = CompletableDeferred<Throwable>()
    val handler = CoroutineExceptionHandler { _, exception ->
      exceptionHandled.complete(exception)
    }

    val result = withContext(handler) {
      racing {
        race { throw RuntimeException("Test exception from CEH") }
        race { "success" }
      }
    }

    assertEquals("success", result)
    val exception = withTimeoutOrNull(600.milliseconds) { exceptionHandled.await() }
    assertIs<RuntimeException>(exception)
    assertEquals("Test exception from CEH", exception.message)
  }

  @Test
  fun testNestedRacingErrorPropagation() = runTest {
    // This test verifies how exceptions are handled in nested racing blocks
    val innerExceptionHandled = CompletableDeferred<Throwable>()
    val outerExceptionHandled = CompletableDeferred<Throwable>()
    val innerRacingCompleted = CompletableDeferred<Boolean>()

    val handler = CoroutineExceptionHandler { _, exception -> outerExceptionHandled.complete(exception) }

    val result = racing {
      race(handler) {
        val innerHandler = CoroutineExceptionHandler { _, exception ->
          innerExceptionHandled.complete(exception)
          innerRacingCompleted.complete(true)
        }
        racing {
          race(innerHandler) { throw IllegalArgumentException("Inner exception") }
          race(innerHandler) { awaitCancellation() }
        }

        // The inner racing block should handle the exception and continue
        // This line should not be reached because the inner racing block never completes normally
      }
      race(handler) {
        // Wait for the inner racing to handle the exception
        innerRacingCompleted.await()
        "outer success"
      }
    }

    assertEquals("outer success", result)

    // Verify that the inner exception was handled by the inner racing block
    val innerException = withTimeoutOrNull(600.milliseconds) { innerExceptionHandled.await() }
    assertIs<IllegalArgumentException>(innerException)
    assertEquals("Inner exception", innerException.message)

    // Verify that the inner racing block handled the exception and continued
    assertTrue(innerRacingCompleted.await(), "Inner racing should handle the exception")

    // The outer racing block should not receive any exception
    // because the inner racing block handles it and never completes normally
    val outerException = withTimeoutOrNull(100.milliseconds) { outerExceptionHandled.await() }
    assertEquals(null, outerException, "Outer exception handler should not receive any exception")
  }

  @Test
  fun testMultipleRacersThrowingExceptions() = runTest {
    val exceptionsHandled = mutableListOf<Throwable>()
    val exceptionHandlerCalled = CompletableDeferred<Boolean>()

    val handler = CoroutineExceptionHandler { _, exception ->
      exceptionsHandled.add(exception)
      if (exceptionsHandled.size >= 2) {
        exceptionHandlerCalled.complete(true)
      }
    }
    val result = racing {
      race(handler) { throw IllegalStateException("First exception") }
      race(handler) { throw RuntimeException("Second exception") }
      race(handler) {
        yield() // Give time for the other racers to throw
        "success"
      }
    }

    assertEquals("success", result)
    assertTrue(withTimeoutOrNull(600.milliseconds) { exceptionHandlerCalled.await() } == true)
    assertEquals(2, exceptionsHandled.size)
    assertTrue(exceptionsHandled.any { it is IllegalStateException && it.message == "First exception" })
    assertTrue(exceptionsHandled.any { it is RuntimeException && it.message == "Second exception" })
  }

  @Test
  fun testDifferentExceptionTypes() = runTest {
    val exceptionsHandled = mutableListOf<Throwable>()

    withContext(CoroutineExceptionHandler { _, exception ->
      exceptionsHandled.add(exception)
    }) {
      val result = racing {
        race { throw IllegalArgumentException("Argument exception") }
        race { throw NullPointerException("NPE exception") }
        race { throw IllegalStateException("State exception") }
        race {
          yield() // Give time for the other racers to throw
          "success"
        }
      }

      assertEquals("success", result)
    }
    assertEquals(3, exceptionsHandled.size)
    assertTrue(exceptionsHandled.any { it is IllegalArgumentException })
    assertTrue(exceptionsHandled.any { it is NullPointerException })
    assertTrue(exceptionsHandled.any { it is IllegalStateException })
  }

  @Test
  fun testCancellationVsOtherExceptions() = runTest {
    val nonCancellationExceptionHandled = CompletableDeferred<Throwable>()
    val cancellationExceptionThrown = CompletableDeferred<Boolean>()

    val handler = CoroutineExceptionHandler { _, exception ->
      if (exception !is CancellationException) {
        nonCancellationExceptionHandled.complete(exception)
      }
    }
    val result = racing {
      race(handler) {
        try {
          awaitCancellation()
        } catch (e: CancellationException) {
          cancellationExceptionThrown.complete(true)
          throw e
        }
      }
      race(handler) { throw RuntimeException("Non-cancellation exception") }
      race(handler) {
        yield() // Give time for the other racers to throw
        "success"
      }
    }

    assertEquals("success", result)
    val exception = withTimeoutOrNull(600.milliseconds) { nonCancellationExceptionHandled.await() }
    assertIs<RuntimeException>(exception)
    assertEquals("Non-cancellation exception", exception.message)
    assertTrue(withTimeoutOrNull(600.milliseconds) { cancellationExceptionThrown.await() } == true)
  }
}
