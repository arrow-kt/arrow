package arrow.fx.resilience

import arrow.core.Either
import arrow.fx.resilience.CircuitBreaker.OpeningStrategy
import arrow.fx.resilience.CircuitBreaker.OpeningStrategy.SlidingWindow
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
import kotlin.test.assertFalse
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
    val cb = CircuitBreaker(resetTimeout = resetTimeout, openingStrategy = OpeningStrategy.Count(maxFailures),)
    var effect = 0
    val iterations = stackSafeIteration()
    Schedule.recurs<Unit>(iterations.toLong()).repeat {
      cb.protectOrThrow { withContext(Dispatchers.Default) { effect += 1 } }
    }
    assertEquals(iterations + 1, effect)
  }

  @Test
  fun shouldWorkForSuccessfulImmediateTasks(): TestResult = runTest {
    val cb = CircuitBreaker(resetTimeout = resetTimeout, openingStrategy = OpeningStrategy.Count(maxFailures),)
    var effect = 0
    val iterations = stackSafeIteration()
    Schedule.recurs<Unit>(iterations.toLong()).repeat {
      cb.protectOrThrow { effect += 1 }
    }
    assertEquals(iterations + 1, effect)
  }

  @Test
  fun staysClosedAfterLessThanMaxFailures(): TestResult = runTest {
    val cb = CircuitBreaker(resetTimeout = resetTimeout, openingStrategy = OpeningStrategy.Count(maxFailures),)

    val result = recurAndCollect<Either<Throwable, Unit>>(4).repeat {
      Either.catch { cb.protectOrThrow { throw dummy } }
    }

    assertTrue(result.all { it == Either.Left(dummy) })
    assertTrue(cb.state() is CircuitBreaker.State.Closed)
  }

  @Test
  fun closedCircuitBreakerResetsFailureCountAfterSuccess(): TestResult = runTest {
    val cb = CircuitBreaker(resetTimeout = resetTimeout, openingStrategy = OpeningStrategy.Count(maxFailures),)

    val result = recurAndCollect<Either<Throwable, Unit>>(4).repeat {
      Either.catch { cb.protectOrThrow { throw dummy } }
    }

    assertTrue(result.all { it == Either.Left(dummy) })
    assertTrue(cb.state() is CircuitBreaker.State.Closed)
    assertEquals(1, cb.protectOrThrow { 1 })
    assertTrue(cb.state() is CircuitBreaker.State.Closed)
  }

  @Test
  fun circuitBreakerOpensAfterMaxFailures(): TestResult = runTest {
    val cb = CircuitBreaker(resetTimeout = resetTimeout, openingStrategy = OpeningStrategy.Count(maxFailures),)

    val result = recurAndCollect<Either<Throwable, Unit>>(4).repeat {
      Either.catch { cb.protectOrThrow { throw dummy } }
    }

    assertTrue(result.all { it == Either.Left(dummy) })

    assertTrue(cb.state() is CircuitBreaker.State.Closed)

    assertEquals(Either.Left(dummy), Either.catch { cb.protectOrThrow { throw dummy } })

    assert(cb.state()) { s: CircuitBreaker.State.Open -> assertEquals(resetTimeout, s.resetTimeout) }
  }

  @Test
  fun circuitBreakerCanBeClosedAgainAfterWaitingResetTimeOut(): TestResult = runTest {
    var openedCount = 0
    var closedCount = 0
    var halfOpenCount = 0
    var rejectedCount = 0

    val timeSource = TestTimeSource()

    val cb = CircuitBreaker(
      resetTimeout = resetTimeout,
      openingStrategy = OpeningStrategy.Count(maxFailures),
      exponentialBackoffFactor = exponentialBackoffFactor,
      maxResetTimeout = maxTimeout,
      timeSource = timeSource
    ).doOnOpen { openedCount += 1 }
      .doOnClosed { closedCount += 1 }
      .doOnHalfOpen { halfOpenCount += 1 }
      .doOnRejectedTask { rejectedCount += 1 }

    // CircuitBreaker opens after 5 failures
    recurAndCollect<Unit>(5).repeat { Either.catch { cb.protectOrThrow { throw dummy } } }

    assert(cb.state()) { s: CircuitBreaker.State.Open -> assertEquals(resetTimeout, s.resetTimeout) }

    // If CircuitBreaker is Open our tasks our rejected
    assertFailsWith<CircuitBreaker.ExecutionRejected> {
      cb.protectOrThrow { throw dummy }
    }

    // After resetTimeout passes, CB should still be Open, and we should be able to reset to Closed.
    timeSource += resetTimeout + 10.milliseconds

    assert(cb.state()) { s: CircuitBreaker.State.Open -> assertEquals(resetTimeout, s.resetTimeout) }

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

    assert(cb.state()) { s: CircuitBreaker.State.HalfOpen -> assertEquals(resetTimeout, s.resetTimeout) }

    // Rejects all other tasks in HalfOpen
    assertFailsWith<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }
    assertFailsWith<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }

    // Once we complete `protect`, the circuit breaker will go back to closer state
    delayProtectLatch.complete(Unit)
    stateAssertionLatch.await()

    // Circuit breaker should be reset after successful task.
    assertTrue(cb.state() is CircuitBreaker.State.Closed)

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
      resetTimeout = resetTimeout,
      openingStrategy = OpeningStrategy.Count(maxFailures),
      exponentialBackoffFactor = 2.0,
      maxResetTimeout = maxTimeout,
      timeSource = timeSource
    ).doOnOpen { openedCount += 1 }
      .doOnClosed { closedCount += 1 }
      .doOnHalfOpen { halfOpenCount += 1 }
      .doOnRejectedTask { rejectedCount += 1 }

    // CircuitBreaker opens after 5 failures
    recurAndCollect<Unit>(5).repeat { Either.catch { cb.protectOrThrow { throw dummy } } }

    assert(cb.state()) { s: CircuitBreaker.State.Open -> assertEquals(resetTimeout, s.resetTimeout) }

    assertTrue(cb.state() is CircuitBreaker.State.Open)
    // If CircuitBreaker is Open our tasks our rejected
    assertFailsWith<CircuitBreaker.ExecutionRejected> {
      cb.protectOrThrow { throw dummy }
    }

    // After resetTimeout passes, CB should still be Open, and we should be able to reset to Closed.
    timeSource += resetTimeout + 10.milliseconds

    assert(cb.state()) { s: CircuitBreaker.State.Open -> assertEquals(resetTimeout, s.resetTimeout) }

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

    assert(cb.state()) { s: CircuitBreaker.State.HalfOpen -> assertEquals(resetTimeout, s.resetTimeout) }

    // Rejects all other tasks in HalfOpen
    assertFailsWith<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }
    assertFailsWith<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }

    // Once we complete `protect`, the circuit breaker will go back to closer state
    delayProtectLatch.complete(Unit)
    stateAssertionLatch.await()

    // Circuit breaker should've stayed open on failure after timeOutReset
    // resetTimeout should've applied
    assert(cb.state()) { s: CircuitBreaker.State.Open -> assertEquals(resetTimeout * exponentialBackoffFactor, s.resetTimeout) }

    assertEquals(3, rejectedCount) // 3 tasks were rejected in total
    assertEquals(2, openedCount) // Circuit breaker opened twice
    assertEquals(1, halfOpenCount) // Circuit breaker went into halfOpen once
    assertEquals(0, closedCount) // Circuit breaker closed once after it opened
  }

  @Test
  fun shouldBeStackSafeForSuccessfulAsyncTasks(): TestResult = runTest {
    val result = stackSafeSuspend(
      CircuitBreaker(resetTimeout = 1.minutes, OpeningStrategy.Count(maxFailures = 5)),
      stackSafeIteration(), 0
    )

    assertEquals(stackSafeIteration(), result)
  }

  @Test
  fun shouldBeStackSafeForSuccessfulImmediateTasks(): TestResult = runTest {
    val result = stackSafeImmediate(
      CircuitBreaker(resetTimeout = 1.minutes, OpeningStrategy.Count(maxFailures = 5)),
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
        CircuitBreaker(resetTimeout, OpeningStrategy.Count(maxFailures), exponentialBackoffFactor, maxResetTimeout)
      }

      assertFailsWith<IllegalArgumentException> {
        CircuitBreaker(
          resetTimeout,
          OpeningStrategy.Count(maxFailures),
          exponentialBackoffFactor,
          maxResetTimeout
        )
      }

      assertFailsWith<IllegalArgumentException> {
        CircuitBreaker(
          resetTimeout,
          OpeningStrategy.Count(maxFailures),
          exponentialBackoffFactor,
          maxResetTimeout
        )
      }
    }
  }

  @Test
  fun slidingWindowStrategyShouldKeepClosed(): TestResult = runTest {
    val timeSource = TestTimeSource()
    val windowDuration = 200.milliseconds
    val stepDuration = 40.milliseconds
    val maxFailures = 5
    var openingStrategy: OpeningStrategy = SlidingWindow(timeSource, windowDuration, maxFailures)
    val schedule = Schedule.spaced<Unit>(stepDuration) and Schedule.recurs(10)

    schedule.repeat {
      timeSource += stepDuration
      openingStrategy = openingStrategy.trackFailure(timeSource.markNow())
      val shouldOpen = openingStrategy.shouldOpen()
      assertFalse(shouldOpen, "The circuit breaker should keep closed")
    }

  }

  @Test
  fun slidingWindowStrategyShouldOpen(): TestResult = runTest {
    val timeSource = TestTimeSource()
    val windowDuration = 200.milliseconds
    val stepDuration = 39.milliseconds
    val maxFailures = 5
    var openingStrategy: OpeningStrategy = SlidingWindow(timeSource, windowDuration, maxFailures)
    val schedule = Schedule.spaced<Unit>(stepDuration) and Schedule.recurs(5)

    schedule.repeat {
      timeSource += stepDuration
      openingStrategy = openingStrategy.trackFailure(timeSource.markNow())
    }

    assertTrue(openingStrategy.shouldOpen(), "The circuit breaker should open after reaching max failures")

    timeSource += stepDuration
    openingStrategy = openingStrategy.trackFailure(timeSource.markNow())

    assertTrue(openingStrategy.shouldOpen(), "The circuit breaker should still open")
  }

  @Test
  fun slidingWindowStrategyShouldCloseAfterResetTimeout(): TestResult = runTest {
    val timeSource = TestTimeSource()
    val windowDuration = 200.milliseconds
    val stepDuration = 39.milliseconds
    val maxFailures = 5
    var openingStrategy: OpeningStrategy = SlidingWindow(timeSource, windowDuration, maxFailures)
    val schedule = Schedule.spaced<Unit>(stepDuration) and Schedule.recurs(5)

    schedule.repeat {
      timeSource += stepDuration
      openingStrategy = openingStrategy.trackFailure(timeSource.markNow())
    }

    assertTrue(openingStrategy.shouldOpen(), "The circuit breaker should open after reaching max failures")

    timeSource += windowDuration

    assertFalse(openingStrategy.shouldOpen(), "The circuit breaker should close after reset timeout")
  }
}

private data class ConstructorValues(
  val maxFailures: Int = 1,
  val resetTimeout: Duration = 1.seconds,
  val exponentialBackoffFactor: Double = 1.0,
  val maxResetTimeout: Duration = Duration.INFINITE,
)

inline fun <reified A, reified B : A> assert(expected: A, block: (b: B) -> Unit): Unit =
  if (expected is B) block(expected)
  else fail("Expected ${B::class.simpleName} but found ${expected!!::class.simpleName}")


/**
 * Recurs the effect [n] times, and collects the output along the way for easy asserting.
 */
fun <A> recurAndCollect(n: Long): Schedule<A, List<A>> =
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
