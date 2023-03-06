package arrow.fx.resilience

import arrow.core.Either
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class CircuitBreakerTest {
  val dummy = RuntimeException("dummy")
  val maxFailures = 5
  val exponentialBackoffFactor = 2.0
  val resetTimeout = 500.milliseconds
  val maxTimeout = 1000.milliseconds

  @Test
  fun shouldWorkForSuccessfulAsyncTasks(): TestResult = runTest {
    val cb = CircuitBreaker(maxFailures = maxFailures, resetTimeout = resetTimeout)
    var effect = 0
    Schedule.recurs<Unit>(10_000).repeat {
      cb.protectOrThrow { withContext(Dispatchers.Default) { effect += 1 } }
    }
    assertEquals(10_001, effect)
  }

  @Test
  fun shouldWorkForSuccessfulImmediateTasks(): TestResult = runTest {
    val cb = CircuitBreaker(maxFailures = maxFailures, resetTimeout = resetTimeout)
    var effect = 0
    Schedule.recurs<Unit>(10_000).repeat {
      cb.protectOrThrow { effect += 1 }
    }
    assertEquals(10_001, effect)
  }

  @Test
  fun staysClosedAfterLessThanMaxFailures(): TestResult = runTest {
    val cb = CircuitBreaker(maxFailures = maxFailures, resetTimeout = resetTimeout)

    val result = recurAndCollect<Either<Throwable, Unit>>(4).repeat {
      Either.catch { cb.protectOrThrow { throw dummy } }
    }

    assertTrue(result.all { it == Either.Left(dummy) })
    assertEquals(CircuitBreaker.State.Closed(5), cb.state())
  }

  @Test
  fun closedCircuitBreakerResetsFailureCountAfterSuccess(): TestResult = runTest {
    val cb = CircuitBreaker(maxFailures = maxFailures, resetTimeout = resetTimeout)

    val result = recurAndCollect<Either<Throwable, Unit>>(4).repeat {
      Either.catch { cb.protectOrThrow { throw dummy } }
    }

    assertTrue(result.all { it == Either.Left(dummy) })
    assertEquals(CircuitBreaker.State.Closed(5), cb.state())
    assertEquals(1, cb.protectOrThrow { 1 })
    assertEquals(CircuitBreaker.State.Closed(0), cb.state())
  }

  @Test
  fun circuitBreakerOpensAfterMaxFailures(): TestResult = runTest {
    val cb = CircuitBreaker(maxFailures = maxFailures, resetTimeout = resetTimeout)

    val result = recurAndCollect<Either<Throwable, Unit>>(4).repeat {
      Either.catch { cb.protectOrThrow { throw dummy } }
    }

    assertTrue(result.all { it == Either.Left(dummy) })

    assertEquals(CircuitBreaker.State.Closed(5), cb.state())

    assertEquals(Either.Left(dummy), Either.catch { cb.protectOrThrow { throw dummy } })

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        assertEquals(resetTimeout, s.resetTimeout)
      }

      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }
  }

  @Test
  fun circuitBreakerCanBeClosedAgainAfterWaitingResetTimeOut(): TestResult = runTest {
    var openedCount = 0
    var closedCount = 0
    var halfOpenCount = 0
    var rejectedCount = 0

    val timeSource = TestTimeSource()

    val cb = CircuitBreaker(
      maxFailures = maxFailures,
      resetTimeout = resetTimeout,
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeout = maxTimeout,
      timeSource = timeSource
    ).doOnOpen { openedCount += 1 }
      .doOnClosed { closedCount += 1 }
      .doOnHalfOpen { halfOpenCount += 1 }
      .doOnRejectedTask { rejectedCount += 1 }

    // CircuitBreaker opens after 5 failures
    recurAndCollect<Unit>(5).repeat { Either.catch { cb.protectOrThrow { throw dummy } } }

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        assertEquals(resetTimeout, s.resetTimeout)
      }

      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    // If CircuitBreaker is Open our tasks our rejected
    assertFailsWith<CircuitBreaker.ExecutionRejected> {
      cb.protectOrThrow { throw dummy }
    }

    // After resetTimeout passes, CB should still be Open, and we should be able to reset to Closed.
    timeSource += resetTimeout + 10.milliseconds

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        assertEquals(resetTimeout, s.resetTimeout)
      }

      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    val checkHalfOpen = CompletableDeferred<Unit>()
    val delayProtectLatch = CompletableDeferred<Unit>()
    val stateAssertionLatch = CompletableDeferred<Unit>()

    @Suppress("DeferredResultUnused")
    async { // Successful tasks puts circuit breaker back in HalfOpen
      cb.protectOrThrow {
        checkHalfOpen.complete(Unit)
        delayProtectLatch.await()
      } // Delay protect, to inspect HalfOpen state.
      stateAssertionLatch.complete(Unit)
    }

    checkHalfOpen.await()

    when (val s = cb.state()) {
      is CircuitBreaker.State.HalfOpen -> {
        assertEquals(resetTimeout, s.resetTimeout)
      }

      else -> fail("Invalid state: Expect CircuitBreaker.State.HalfOpen but found $s")
    }

    // Rejects all other tasks in HalfOpen
    assertFailsWith<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }
    assertFailsWith<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }

    // Once we complete `protect`, the circuit breaker will go back to closer state
    delayProtectLatch.complete(Unit)
    stateAssertionLatch.await()

    // Circuit breaker should be reset after successful task.
    assertEquals(CircuitBreaker.State.Closed(0), cb.state())

    assertEquals(3, rejectedCount) // 3 tasks were rejected in total
    assertEquals(1, openedCount) // Circuit breaker opened once
    assertEquals(1, halfOpenCount) // Circuit breaker went into halfOpen once
    assertEquals(1, closedCount) // Circuit breaker closed once after it opened
  }

  @Test
  fun circuitBreakerStaysOpenWithFailureAfterResetTimeOut(): TestResult = runTest {
    var openedCount = 0
    var closedCount = 0
    var halfOpenCount = 0
    var rejectedCount = 0

    val timeSource = TestTimeSource()

    val cb = CircuitBreaker(
      maxFailures = maxFailures,
      resetTimeout = resetTimeout,
      exponentialBackoffFactor = 2.0,
      maxResetTimeout = maxTimeout,
      timeSource = timeSource
    ).doOnOpen { openedCount += 1 }
      .doOnClosed { closedCount += 1 }
      .doOnHalfOpen { halfOpenCount += 1 }
      .doOnRejectedTask { rejectedCount += 1 }

    // CircuitBreaker opens after 5 failures
    recurAndCollect<Unit>(5).repeat { Either.catch { cb.protectOrThrow { throw dummy } } }

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        assertEquals(resetTimeout, s.resetTimeout)
      }

      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    assertTrue(cb.state() is CircuitBreaker.State.Open)
    // If CircuitBreaker is Open our tasks our rejected
    assertFailsWith<CircuitBreaker.ExecutionRejected> {
      cb.protectOrThrow { throw dummy }
    }

    // After resetTimeout passes, CB should still be Open, and we should be able to reset to Closed.
    timeSource += resetTimeout + 10.milliseconds

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        assertEquals(resetTimeout, s.resetTimeout)
      }

      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    val checkHalfOpen = CompletableDeferred<Unit>()
    val delayProtectLatch = CompletableDeferred<Unit>()
    val stateAssertionLatch = CompletableDeferred<Unit>()

    @Suppress("DeferredResultUnused")
    async { // Successful tasks puts circuit breaker back in HalfOpen
      // Delay protect, to inspect HalfOpen state.
      Either.catch {
        cb.protectOrThrow {
          checkHalfOpen.complete(Unit)
          delayProtectLatch.await(); throw dummy
        }
      }
      stateAssertionLatch.complete(Unit)
    }

    checkHalfOpen.await()

    when (val s = cb.state()) {
      is CircuitBreaker.State.HalfOpen -> {
        assertEquals(resetTimeout, s.resetTimeout)
      }

      else -> fail("Invalid state: Expect CircuitBreaker.State.HalfOpen but found $s")
    }

    // Rejects all other tasks in HalfOpen
    assertFailsWith<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }
    assertFailsWith<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }

    // Once we complete `protect`, the circuit breaker will go back to closer state
    delayProtectLatch.complete(Unit)
    stateAssertionLatch.await()

    // Circuit breaker should've stayed open on failure after timeOutReset
    // resetTimeout should've applied
    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        assertEquals(resetTimeout * exponentialBackoffFactor, s.resetTimeout)
      }

      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    assertEquals(3, rejectedCount) // 3 tasks were rejected in total
    assertEquals(2, openedCount) // Circuit breaker opened twice
    assertEquals(1, halfOpenCount) // Circuit breaker went into halfOpen once
    assertEquals(0, closedCount) // Circuit breaker closed once after it opened
  }

  @Test
  fun shouldBeStackSafeForSuccessfulAsyncTasks(): TestResult = runTest {
    val result = stackSafeSuspend(
      CircuitBreaker(maxFailures = 5, resetTimeout = 1.minutes),
      stackSafeIteration(), 0
    )

    assertEquals(stackSafeIteration(), result)
  }

  @Test
  fun shouldBeStackSafeForSuccessfulImmediateTasks(): TestResult = runTest {
    val result = stackSafeImmediate(
      CircuitBreaker(maxFailures = 5, resetTimeout = 1.minutes),
      stackSafeIteration(), 0
    )

    assertEquals(stackSafeIteration(), result)
  }

  @Test
  fun shouldRequireValidConstructorValues(): TestResult = runTest {
    listOf(
      ConstructorValues(maxFailures = -1),
      ConstructorValues(resetTimeout = Duration.ZERO),
      ConstructorValues(resetTimeout = (-1).seconds),
      ConstructorValues(exponentialBackoffFactor = 0.0),
      ConstructorValues(exponentialBackoffFactor = -1.0),
      ConstructorValues(maxResetTimeout = Duration.ZERO),
      ConstructorValues(maxResetTimeout = (-1).seconds),
    ).forEach { (maxFailures, resetTimeout, exponentialBackoffFactor, maxResetTimeout) ->
      assertFailsWith<IllegalArgumentException> {
        CircuitBreaker(maxFailures, resetTimeout, exponentialBackoffFactor, maxResetTimeout)
      }

      assertFailsWith<IllegalArgumentException> {
        CircuitBreaker(
          maxFailures,
          resetTimeout,
          exponentialBackoffFactor,
          maxResetTimeout
        )
      }

      assertFailsWith<IllegalArgumentException> {
        CircuitBreaker(
          maxFailures,
          resetTimeout,
          exponentialBackoffFactor,
          maxResetTimeout
        )
      }
    }
  }
}

private data class ConstructorValues(
  val maxFailures: Int = 1,
  val resetTimeout: Duration = 1.seconds,
  val exponentialBackoffFactor: Double = 1.0,
  val maxResetTimeout: Duration = Duration.INFINITE,
)

/**
 * Recurs the effect [n] times, and collects the output along the way for easy asserting.
 */
fun <A> recurAndCollect(n: Int): Schedule<A, List<A>> =
  Schedule.recurs<A>(n).zipRight(Schedule.identity<A>().collect())

tailrec suspend fun stackSafeSuspend(cb: CircuitBreaker, n: Int, acc: Int): Int =
  if (n > 0) {
    val s = cb.protectOrThrow { withContext(Dispatchers.Default) { acc + 1 } }
    stackSafeSuspend(cb, n - 1, s)
  } else acc

tailrec suspend fun stackSafeImmediate(cb: CircuitBreaker, n: Int, acc: Int): Int =
  if (n > 0) {
    val s = cb.protectOrThrow { acc + 1 }
    stackSafeImmediate(cb, n - 1, s)
  } else acc
