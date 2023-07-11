package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class CyclicBarrierSpec : StringSpec({
  "should raise an exception when constructed with a negative or zero capacity" {
    checkAll(Arb.int(Int.MIN_VALUE, 0)) { i ->
      shouldThrow<IllegalArgumentException> { CyclicBarrier(i) }.message shouldBe
        "Cyclic barrier must be constructed with positive non-zero capacity $i but was $i > 0"
    }
  }

  "barrier of capacity 1 is a no op" {
    checkAll(Arb.constant(Unit)) {
      val barrier = CyclicBarrier(1)
      barrier.await()
    }
  }

  "awaiting all in parallel resumes all coroutines" {
    checkAll(Arb.int(1, 100)) { i ->
      val barrier = CyclicBarrier(i)
      (0 until i).parMap { barrier.await() }
    }
  }

  "should reset once full" {
    checkAll(Arb.constant(Unit)) {
      val barrier = CyclicBarrier(2)
      parZip({ barrier.await() }, { barrier.await() }) { _, _ -> }
      barrier.capacity shouldBe 2
    }
  }

  "executes runnable once full" {
    var barrierRunnableInvoked = false
    val barrier = CyclicBarrier(2) { barrierRunnableInvoked = true }
    parZip({ barrier.await() }, { barrier.await() }) { _, _ -> }
    barrier.capacity shouldBe 2
    barrierRunnableInvoked shouldBe true
  }

  "await is cancelable" {
    checkAll(Arb.int(2, Int.MAX_VALUE)) { i ->
      val barrier = CyclicBarrier(i)
      val exitCase = CompletableDeferred<ExitCase>()

      val job =
        launch(start = CoroutineStart.UNDISPATCHED) {
          guaranteeCase({ barrier.await() }, exitCase::complete)
        }

      job.cancelAndJoin()
      exitCase.isCompleted shouldBe true
      exitCase.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }
  }

  "should clean up upon cancellation of await" {
    checkAll(Arb.constant(Unit)) {
      val barrier = CyclicBarrier(2)
      launch(start = CoroutineStart.UNDISPATCHED) { barrier.await() }.cancelAndJoin()
    }
  }

  "reset cancels all awaiting" {
    checkAll(Arb.int(2, 100)) { i ->
      val barrier = CyclicBarrier(i)
      val exitCase = CompletableDeferred<ExitCase>()

      val jobs =
        (1 until i).map {
          launch(start = CoroutineStart.UNDISPATCHED) {
            guaranteeCase({ barrier.await() }, exitCase::complete)
          }
        }

      barrier.reset()
      jobs.map { it.isCancelled shouldBe true }
    }
  }

  "should clean up upon reset" {
    checkAll(Arb.int(2, 100)) { i ->
      val barrier = CyclicBarrier(i)
      val exitCase = CompletableDeferred<ExitCase>()

      launch(start = CoroutineStart.UNDISPATCHED) {
        guaranteeCase({ barrier.await() }, exitCase::complete)
      }

      barrier.reset()

      (0 until i).parMap { barrier.await() }
    }
  }

  "race fiber cancel and barrier full" {
    checkAll(Arb.constant(Unit)) {
      val barrier = CyclicBarrier(2)
      val job = launch(start = CoroutineStart.UNDISPATCHED) { barrier.await() }
      when (raceN({ barrier.await() }, { job.cancelAndJoin() })) {
        // without the epoch check in CyclicBarrier, a late cancellation would increment the count
        // after the barrier has already reset, causing this code to never terminate (test times out)
        is Either.Left -> parZip({ barrier.await() }, { barrier.await() }) { _, _ -> }
        is Either.Right -> Unit
      }
    }
  }

  "reset" {
    checkAll(Arb.int(2..10)) { n ->
      val barrier = CyclicBarrier(n)

      val exits = (0 until n - 1).map { CompletableDeferred<ExitCase>() }

      val jobs = (0 until n - 1).map { i ->
        launch(start = CoroutineStart.UNDISPATCHED) {
          guaranteeCase(barrier::await, exits[i]::complete)
        }
      }

      barrier.reset()

      exits.zip(jobs) { exitCase, job ->
        exitCase.await().shouldBeTypeOf<ExitCase.Cancelled>()
        job.isCancelled shouldBe true
      }
    }
  }
})
