package arrow.fx.coroutines

import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RaiseRacingTest {
  @Test
  fun immediateWinner() = runTest {
    val result = either<String, Int> {
      racing {
        race { 1 }
      }
    }
    assertEquals(1.right(), result)
  }

  @Test
  fun racerCanSuspend() = runTest {
    val result = either<String, Int> {
      racing {
        race { yield(); 1 }
      }
    }
    assertEquals(1.right(), result)
  }

  @Test
  fun testRaceLosersCancellation() = runTest {
    val slowRacerCancelled = CompletableDeferred<CancellationException>()

    val result = either<String, String> {
      racing {
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
    }

    assertEquals("fast".right(), result)
    withTimeoutOrNull(600.seconds) { slowRacerCancelled.await() }
  }

  @Test
  fun testRaceLosersCancellationFinalizers() = runTest {
    val launchFinalizerCalled = CompletableDeferred<Boolean>()
    val asyncFinalizerCalled = CompletableDeferred<Boolean>()
    val latch = CompletableDeferred<Unit>()

    val result = either<String, String> {
      racing {
        race {
          backgroundScope.launch {
            try {
              awaitCancellation()
            } finally {
              launchFinalizerCalled.complete(true)
            }
          }
          backgroundScope.async {
            try {
              awaitCancellation()
            } finally {
              asyncFinalizerCalled.complete(true)
            }
          }
          latch.complete(Unit)
          awaitCancellation()
        }
        raceOrThrow {
          withTimeoutOrNull(600.milliseconds) { latch.await() }
          "fast"
        }
      }
    }

    assertEquals("fast".right(), result)
    withTimeoutOrNull(600.milliseconds) { launchFinalizerCalled.await() }
    withTimeoutOrNull(600.milliseconds) { asyncFinalizerCalled.await() }
  }

  @Test
  fun testExceptionHandling() = runTest {
    val exceptionHandled = CompletableDeferred<Throwable>()

    val result = either<String, String> {
      val handler = CoroutineExceptionHandler { _, exception -> exceptionHandled.complete(exception) }
      racing {
        race(handler) { throw IllegalStateException("Test exception") }
        race(handler) { "success" }
      }
    }

    assertEquals("success".right(), result)
    val exception = withTimeoutOrNull(600.seconds) { exceptionHandled.await() }
    assertIs<IllegalStateException>(exception)
    assertEquals("Test exception", exception.message)
  }

  @Test
  fun testRaiseHandling() = runTest {
    val raiseHandled = CompletableDeferred<String>()

    val result = either<String, String> {
      val handleRaise: RaiseHandler<String> = { _, raised ->
        raiseHandled.complete(raised)
      }
      racing {
        race(handleRaise) { raise("Test error") }
        race(handleRaise) { "success" }
      }
    }

    assertEquals("success".right(), result)
    val error = withTimeoutOrNull(600.seconds) { raiseHandled.await() }
    assertEquals("Test error", error)
  }

  @Test
  fun testRaceOrThrow() = runTest {
    assertFailsWith<IllegalStateException> {
      either<String, String> {
        racing {
          raceOrThrow { throw IllegalStateException("Test exception") }
          race { awaitCancellation() }
        }
      }
    }
  }

  @Test
  fun testRaceOrRaise() = runTest {
    val result = either {
      racing {
        race { raise("Test error") }
        race { "success" }
      }
    }
    assertEquals("Test error".left(), result)
  }

  @Test
  fun testRaceOrFail() = runTest {
    val result = either {
      racing {
        raceOrThrow { raise("Test error") }
        race { "success" }
      }
    }
    assertEquals("Test error".left(), result)
  }

  @Test
  fun testNestedRacing() = runTest {
    val result = either<String, String> {
      racing {
        race {
          racing {
            race { awaitCancellation() }
            race { "inner fast" }
          }
        }
        race { awaitCancellation() }
      }
    }

    assertEquals("inner fast".right(), result)
  }

  @Test
  fun testCoroutineExceptionHandler() = runTest {
    val exceptionHandled = CompletableDeferred<Throwable>()
    val handler = CoroutineExceptionHandler { _, exception ->
      exceptionHandled.complete(exception)
    }

    val result = withContext(handler) {
      either<String, String> {
        racing {
          race { throw RuntimeException("Test exception from CEH") }
          race { "success" }
        }
      }
    }

    assertEquals("success".right(), result)
    val exception = withTimeoutOrNull(600.milliseconds) { exceptionHandled.await() }
    assertIs<RuntimeException>(exception)
    assertEquals("Test exception from CEH", exception.message)
  }

  @Test
  fun testNestedRacingErrorPropagation() = runTest {
    // This test verifies how exceptions and raised errors are handled in nested racing blocks
    val innerExceptionHandled = CompletableDeferred<Throwable>()
    val innerRaiseHandled = CompletableDeferred<String>()
    val outerExceptionHandled = CompletableDeferred<Throwable>()
    val innerRacingCompleted = CompletableDeferred<Boolean>()

    val result = either<String, String> {
      val handler = CoroutineExceptionHandler { _, exception -> outerExceptionHandled.complete(exception) }
      racing {
        race(handler) {
          val innerHandler = CoroutineExceptionHandler { _, exception -> innerExceptionHandled.complete(exception) }
          val raiseHandler: RaiseHandler<String> = { _, raised ->
            innerRaiseHandled.complete(raised)
            innerRacingCompleted.complete(true)
          }
          racing {
            race(raiseHandler, innerHandler) { raise("Inner error") }
            race(raiseHandler, innerHandler) { awaitCancellation() }
          }

          // The inner racing block should handle the raised error and continue
          // This line should not be reached because the inner racing block never completes normally
        }
        race(handler) {
          // Wait for the inner racing to handle the error
          innerRacingCompleted.await()
          "outer success"
        }
      }
    }

    assertEquals("outer success".right(), result)

    // Verify that the inner error was handled by the inner racing block
    val innerError = withTimeoutOrNull(600.milliseconds) { innerRaiseHandled.await() }
    assertEquals("Inner error", innerError)

    // Verify that the inner racing block handled the error and continued
    assertTrue(innerRacingCompleted.await(), "Inner racing should handle the error")

    // The outer racing block should not receive any exception
    // because the inner racing block handles it and never completes normally
    val outerException = withTimeoutOrNull(100.milliseconds) { outerExceptionHandled.await() }
    assertEquals(null, outerException, "Outer exception handler should not receive any exception")
  }

  @Test
  fun testMultipleRacersThrowingExceptions() = runTest {
    val exceptionsHandled = mutableListOf<Throwable>()
    val exceptionHandlerCalled = CompletableDeferred<Boolean>()

    val result = either<String, String> {
      val handler = CoroutineExceptionHandler { _, exception ->
        exceptionsHandled.add(exception)
        if (exceptionsHandled.size >= 2) {
          exceptionHandlerCalled.complete(true)
        }
      }
      racing {
        race(handler) { throw IllegalStateException("First exception") }
        race(handler) { throw RuntimeException("Second exception") }
        race(handler) {
          yield() // Give time for the other racers to throw
          "success"
        }
      }
    }

    assertEquals("success".right(), result)
    assertTrue(withTimeout(600.milliseconds) { exceptionHandlerCalled.await() })
    assertEquals(2, exceptionsHandled.size)
    assertTrue(exceptionsHandled.any { it is IllegalStateException && it.message == "First exception" })
    assertTrue(exceptionsHandled.any { it is RuntimeException && it.message == "Second exception" })
  }

  @Test
  fun testMultipleRacersRaisingErrors() = runTest {
    val errorsHandled = mutableListOf<String>()
    val errorHandlerCalled = CompletableDeferred<Boolean>()

    val result = either<String, String> {
      val raiseHandler: RaiseHandler<String> = { _, raised ->
        errorsHandled.add(raised)
        if (errorsHandled.size >= 2) {
          errorHandlerCalled.complete(true)
        }
      }
      racing {
        race(raiseHandler) { raise("First error") }
        race(raiseHandler) { raise("Second error") }
        race(raiseHandler) {
          yield() // Give time for the other racers to raise
          "success"
        }
      }
    }

    assertEquals("success".right(), result)
    assertTrue(withTimeout(600.milliseconds) { errorHandlerCalled.await() })
    assertEquals(2, errorsHandled.size)
    assertTrue(errorsHandled.contains("First error"))
    assertTrue(errorsHandled.contains("Second error"))
  }

  @Test
  fun testMixedErrorsAndExceptions() = runTest {
    val errorsHandled = mutableListOf<String>()
    val exceptionsHandled = mutableListOf<Throwable>()
    val handlersComplete = CompletableDeferred<Boolean>()

    val result = either<String, String> {
      val raiseHandler: RaiseHandler<String> = { _, raised ->
        errorsHandled.add(raised)
        if (errorsHandled.isNotEmpty() && exceptionsHandled.isNotEmpty()) {
          handlersComplete.complete(true)
        }
      }
      val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        exceptionsHandled.add(exception)
        if (errorsHandled.isNotEmpty() && exceptionsHandled.isNotEmpty()) {
          handlersComplete.complete(true)
        }
      }
      racing {
        race(raiseHandler, exceptionHandler) { raise("Raised error") }
        race(raiseHandler, exceptionHandler) { throw RuntimeException("Thrown exception") }
        race(raiseHandler, exceptionHandler) {
          yield() // Give time for the other racers to raise/throw
          "success"
        }
      }
    }

    assertEquals("success".right(), result)
    assertTrue(withTimeout(600.milliseconds) { handlersComplete.await() })
    assertEquals(1, errorsHandled.size)
    assertEquals(1, exceptionsHandled.size)
    assertEquals("Raised error", errorsHandled[0])
    assertIs<RuntimeException>(exceptionsHandled[0])
    assertEquals("Thrown exception", exceptionsHandled[0].message)
  }
}
