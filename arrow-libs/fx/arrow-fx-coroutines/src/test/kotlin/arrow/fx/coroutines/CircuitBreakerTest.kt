package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.lang.RuntimeException

class CircuitBreakerTest : ArrowFxSpec(spec = {

  val dummy = RuntimeException("dummy")
  val maxFailures = 5
  val exponentialBackoffFactor = 2.0
  val resetTimeout = 200.milliseconds
  val maxTimeout = 600.milliseconds

  "should work for successful async tasks" {
    val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)!!
    var effect = 0
    repeat(Schedule.recurs(10_000)) {
      cb.protect { evalOn(ComputationPool) { effect += 1 } }
    }
    effect shouldBe 10_001
  }

  "should work for successful immediate tasks" {
    val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)!!
    var effect = 0
    repeat(Schedule.recurs(10_000)) {
      cb.protect { effect += 1 }
    }
    effect shouldBe 10_001
  }

  "Circuit breaker stays closed after less than maxFailures" {
    val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)!!

    repeat(recurAndCollect(3)) {
      Either.catch { cb.protect { throw dummy } }
    } shouldBe (0..3).map { Either.Left(dummy) }

    cb.state() shouldBe CircuitBreaker.State.Closed(4)
  }

  "Closed circuit breaker resets failure count after success" {
    val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)!!

    repeat(recurAndCollect(3)) {
      Either.catch { cb.protect { throw dummy } }
    } shouldBe (0..3).map { Either.Left(dummy) }

    cb.state() shouldBe CircuitBreaker.State.Closed(4)

    cb.protect { 1 } shouldBe 1

    cb.state() shouldBe CircuitBreaker.State.Closed(0)
  }

  "Circuit breaker opens after max failures" {
    val cb = CircuitBreaker.of(maxFailures = maxFailures, resetTimeout = resetTimeout)!!

    repeat(recurAndCollect(3)) {
      Either.catch { cb.protect { throw dummy } }
    } shouldBe (0..3).map { Either.Left(dummy) }

    cb.state() shouldBe CircuitBreaker.State.Closed(4)

    Either.catch { cb.protect { throw dummy } } shouldBe Either.Left(dummy)

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        s.resetTimeout shouldBe resetTimeout
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
    )!!.doOnOpen { openedCount += 1 }
      .doOnClosed { closedCount += 1 }
      .doOnHalfOpen { halfOpenCount += 1 }
      .doOnRejectedTask { rejectedCount += 1 }

    // CircuitBreaker opens after 4 failures
    repeat(recurAndCollect(4)) { Either.catch { cb.protect { throw dummy } } }

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        s.resetTimeout shouldBe resetTimeout
      }
      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    // If CircuitBreaker is Open our tasks our rejected
    shouldThrow<CircuitBreaker.ExecutionRejected> {
      cb.protect { throw dummy }
    }

    // After resetTimeout passes, CB should still be Open, and we should be able to reset to Closed.
    sleep(resetTimeout + 10.milliseconds)

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        s.resetTimeout shouldBe resetTimeout
      }
      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    val checkHalfOpen = Promise<Unit>()
    val delayProtectLatch = Promise<Unit>()
    val stateAssertionLatch = Promise<Unit>()

    ForkAndForget { // Successful tasks puts circuit breaker back in HalfOpen
      cb.protect {
        checkHalfOpen.complete(Unit)
        delayProtectLatch.get()
      } // Delay protect, to inspect HalfOpen state.
      stateAssertionLatch.complete(Unit)
    }

    checkHalfOpen.get()

    when (val s = cb.state()) {
      is CircuitBreaker.State.HalfOpen -> {
        s.resetTimeout shouldBe resetTimeout
      }
      else -> fail("Invalid state: Expect CircuitBreaker.State.HalfOpen but found $s")
    }

    // Rejects all other tasks in HalfOpen
    shouldThrow<CircuitBreaker.ExecutionRejected> { cb.protect { throw dummy } }
    shouldThrow<CircuitBreaker.ExecutionRejected> { cb.protect { throw dummy } }

    // Once we complete `protect`, the circuitbreaker will go back to closer state
    delayProtectLatch.complete(Unit)
    stateAssertionLatch.get()

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
    )!!.doOnOpen { openedCount += 1 }
      .doOnClosed { closedCount += 1 }
      .doOnHalfOpen { halfOpenCount += 1 }
      .doOnRejectedTask { rejectedCount += 1 }

    // CircuitBreaker opens after 4 failures
    repeat(recurAndCollect(4)) { Either.catch { cb.protect { throw dummy } } }

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        s.resetTimeout shouldBe resetTimeout
      }
      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    // If CircuitBreaker is Open our tasks our rejected
    shouldThrow<CircuitBreaker.ExecutionRejected> {
      cb.protect { throw dummy }
    }

    // After resetTimeout passes, CB should still be Open, and we should be able to reset to Closed.
    sleep(resetTimeout + 10.milliseconds)

    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        s.resetTimeout shouldBe resetTimeout
      }
      else -> fail("Invalid state: Expect CircuitBreaker.State.Open but found $s")
    }

    val checkHalfOpen = Promise<Unit>()
    val delayProtectLatch = Promise<Unit>()
    val stateAssertionLatch = Promise<Unit>()

    ForkAndForget { // Successful tasks puts circuit breaker back in HalfOpen
      // Delay protect, to inspect HalfOpen state.
      Either.catch {
        cb.protect {
          checkHalfOpen.complete(Unit)
          delayProtectLatch.get(); throw dummy
        }
      }
      stateAssertionLatch.complete(Unit)
    }

    checkHalfOpen.get()

    when (val s = cb.state()) {
      is CircuitBreaker.State.HalfOpen -> {
        s.resetTimeout shouldBe resetTimeout
      }
      else -> fail("Invalid state: Expect CircuitBreaker.State.HalfOpen but found $s")
    }

    // Rejects all other tasks in HalfOpen
    shouldThrow<CircuitBreaker.ExecutionRejected> { cb.protect { throw dummy } }
    shouldThrow<CircuitBreaker.ExecutionRejected> { cb.protect { throw dummy } }

    // Once we complete `protect`, the circuitbreaker will go back to closer state
    delayProtectLatch.complete(Unit)
    stateAssertionLatch.get()

    // Circuit breaker should've stayed open on failure after timeOutReset
    // resetTimeout should've applied
    when (val s = cb.state()) {
      is CircuitBreaker.State.Open -> {
        s.resetTimeout shouldBe (resetTimeout * exponentialBackoffFactor.toInt())
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
      CircuitBreaker.of(maxFailures = 5, resetTimeout = 1.minutes)!!,
      20_000, 0
    ) shouldBe 20_000
  }

  "should be stack safe for successful immediate tasks" {
    stackSafeImmediate(
      CircuitBreaker.of(maxFailures = 5, resetTimeout = 1.minutes)!!,
      20_000, 0
    ) shouldBe 20_000
  }
})

/**
 * Recurs the effect [n] times, and collects the output along the way for easy asserting.
 */
fun <A> recurAndCollect(n: Int): Schedule<A, List<A>> =
  Schedule.recurs<A>(n).zipRight(Schedule.identity<A>().collect())

tailrec suspend fun stackSafeSuspend(cb: CircuitBreaker, n: Int, acc: Int): Int =
  if (n > 0) {
    val s = cb.protect { evalOn(ComputationPool) { acc + 1 } }
    stackSafeSuspend(cb, n - 1, s)
  } else acc

tailrec suspend fun stackSafeImmediate(cb: CircuitBreaker, n: Int, acc: Int): Int =
  if (n > 0) {
    val s = cb.protect { acc + 1 }
    stackSafeImmediate(cb, n - 1, s)
  } else acc
