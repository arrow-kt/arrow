package arrow.fx.coroutines

import io.kotest.core.spec.style.StringSpec
import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit.NANOSECONDS
import kotlin.time.ExperimentalTime

@ExperimentalTime
class CircuitBreakerTest : StringSpec({
    val dummy = RuntimeException("dummy")
    val maxFailures = 5
    val exponentialBackoffFactor = 2.0
    val resetTimeout = 200.milliseconds
    val maxTimeout = 600.milliseconds

    "should work for successful async tasks" {
      val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)
      var effect = 0
      Schedule.recurs<Unit>(10_000).repeat {
        cb.protectOrThrow { withContext(Dispatchers.Default) { effect += 1 } }
      }
      effect shouldBe 10_001
    }

    "should work for successful immediate tasks" {
      val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)
      var effect = 0
      Schedule.recurs<Unit>(10_000).repeat {
        cb.protectOrThrow { effect += 1 }
      }
      effect shouldBe 10_001
    }

    "Circuit breaker stays closed after less than maxFailures" {
      val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)

      recurAndCollect<Either<Throwable, Unit>>(3).repeat {
        Either.catch { cb.protectOrThrow { throw dummy } }
      } shouldBe (0..3).map { Either.Left(dummy) }

      cb.state() shouldBe CircuitBreaker.State.Closed(4)
    }

    "Closed circuit breaker resets failure count after success" {
      val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)

      recurAndCollect<Either<Throwable, Unit>>(3).repeat {
        Either.catch { cb.protectOrThrow { throw dummy } }
      } shouldBe (0..3).map { Either.Left(dummy) }

      cb.state() shouldBe CircuitBreaker.State.Closed(4)

      cb.protectOrThrow { 1 } shouldBe 1

      cb.state() shouldBe CircuitBreaker.State.Closed(0)
    }

    "Circuit breaker opens after max failures" {
      val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)

      recurAndCollect<Either<Throwable, Unit>>(3).repeat {
        Either.catch { cb.protectOrThrow { throw dummy } }
      } shouldBe (0..3).map { Either.Left(dummy) }

      cb.state() shouldBe CircuitBreaker.State.Closed(4)

      Either.catch { cb.protectOrThrow { throw dummy } } shouldBe Either.Left(dummy)

      when (val s = cb.state()) {
        is CircuitBreaker.State.Open -> {
          s.resetTimeoutNanos shouldBe resetTimeout.toDouble(NANOSECONDS)
        }
        else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
      }
    }

    "Circuit breaker can be closed again after waiting resetTimeOut" {
      var openedCount = 0
      var closedCount = 0
      var halfOpenCount = 0
      var rejectedCount = 0

      val cb = CircuitBreaker.of(
        maxFailures = maxFailures,
        resetTimeout = resetTimeout,
        exponentialBackoffFactor = exponentialBackoffFactor,
        maxResetTimeout = maxTimeout
      ).doOnOpen { openedCount += 1 }
        .doOnClosed { closedCount += 1 }
        .doOnHalfOpen { halfOpenCount += 1 }
        .doOnRejectedTask { rejectedCount += 1 }

      // CircuitBreaker opens after 4 failures
      recurAndCollect<Unit>(4).repeat { Either.catch { cb.protectOrThrow { throw dummy } } }

      when (val s = cb.state()) {
        is CircuitBreaker.State.Open -> {
          s.resetTimeoutNanos shouldBe resetTimeout.toDouble(NANOSECONDS)
        }
        else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
      }

      // If CircuitBreaker is Open our tasks our rejected
      shouldThrow<CircuitBreaker.ExecutionRejected> {
        cb.protectOrThrow { throw dummy }
      }

      // After resetTimeout passes, CB should still be Open, and we should be able to reset to Closed.
      delay(resetTimeout + 10.milliseconds)

      when (val s = cb.state()) {
        is CircuitBreaker.State.Open -> {
          s.resetTimeoutNanos shouldBe resetTimeout.toDouble(NANOSECONDS)
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
          s.resetTimeoutNanos shouldBe resetTimeout.toDouble(NANOSECONDS)
        }
        else -> fail("Invalid state: Expect CircuitBreaker.State.HalfOpen but found $s")
      }

      // Rejects all other tasks in HalfOpen
      shouldThrow<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }
      shouldThrow<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }

      // Once we complete `protect`, the circuit breaker will go back to closer state
      delayProtectLatch.complete(Unit)
      stateAssertionLatch.await()

      // Circuit breaker should be reset after successful task.
      cb.state() shouldBe CircuitBreaker.State.Closed(0)

      rejectedCount shouldBe 3 // 3 tasks were rejected in total
      openedCount shouldBe 1 // Circuit breaker opened once
      halfOpenCount shouldBe 1 // Circuit breaker went into halfOpen once
      closedCount shouldBe 1 // Circuit breaker closed once after it opened
    }

    "Circuit breaker stays open with failure after resetTimeOut" {
      var openedCount = 0
      var closedCount = 0
      var halfOpenCount = 0
      var rejectedCount = 0

      val cb = CircuitBreaker.of(
        maxFailures = maxFailures,
        resetTimeout = resetTimeout,
        exponentialBackoffFactor = 2.0,
        maxResetTimeout = maxTimeout
      ).doOnOpen { openedCount += 1 }
        .doOnClosed { closedCount += 1 }
        .doOnHalfOpen { halfOpenCount += 1 }
        .doOnRejectedTask { rejectedCount += 1 }

      // CircuitBreaker opens after 4 failures
      recurAndCollect<Unit>(4).repeat { Either.catch { cb.protectOrThrow { throw dummy } } }

      when (val s = cb.state()) {
        is CircuitBreaker.State.Open -> {
          s.resetTimeoutNanos shouldBe resetTimeout.toDouble(NANOSECONDS)
        }
        else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
      }

      // If CircuitBreaker is Open our tasks our rejected
      shouldThrow<CircuitBreaker.ExecutionRejected> {
        cb.protectOrThrow { throw dummy }
      }

      // After resetTimeout passes, CB should still be Open, and we should be able to reset to Closed.
      delay(resetTimeout + 10.milliseconds)

      when (val s = cb.state()) {
        is CircuitBreaker.State.Open -> {
          s.resetTimeoutNanos shouldBe resetTimeout.toDouble(NANOSECONDS)
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
          s.resetTimeoutNanos shouldBe resetTimeout.toDouble(NANOSECONDS)
        }
        else -> fail("Invalid state: Expect CircuitBreaker.State.HalfOpen but found $s")
      }

      // Rejects all other tasks in HalfOpen
      shouldThrow<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }
      shouldThrow<CircuitBreaker.ExecutionRejected> { cb.protectOrThrow { throw dummy } }

      // Once we complete `protect`, the circuit breaker will go back to closer state
      delayProtectLatch.complete(Unit)
      stateAssertionLatch.await()

      // Circuit breaker should've stayed open on failure after timeOutReset
      // resetTimeout should've applied
      when (val s = cb.state()) {
        is CircuitBreaker.State.Open -> {
          s.resetTimeoutNanos shouldBe (resetTimeout * exponentialBackoffFactor).toDouble(NANOSECONDS)
        }
        else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
      }

      rejectedCount shouldBe 3 // 3 tasks were rejected in total
      openedCount shouldBe 2 // Circuit breaker opened once
      halfOpenCount shouldBe 1 // Circuit breaker went into halfOpen once
      closedCount shouldBe 0 // Circuit breaker closed once after it opened
    }

    "should be stack safe for successful async tasks" {
      stackSafeSuspend(
        CircuitBreaker.of(maxFailures = 5, resetTimeout = 1.minutes),
        stackSafeIteration(), 0
      ) shouldBe stackSafeIteration()
    }

    "should be stack safe for successful immediate tasks" {
      stackSafeImmediate(
        CircuitBreaker.of(maxFailures = 5, resetTimeout = 1.minutes),
        stackSafeIteration(), 0
      ) shouldBe stackSafeIteration()
    }
  }
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
