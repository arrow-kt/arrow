package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CyclicBarrierSpec {
  @Test
  fun shouldRaiseAnExceptionWhenConstructedWithNegativeOrZeroCapacity() = runTest {
    checkAll(10, Arb.int(Int.MIN_VALUE, 0)) { i ->
      shouldThrow<IllegalArgumentException> { CyclicBarrier(i) }.message shouldBe
        "Cyclic barrier must be constructed with positive non-zero capacity $i but was $i > 0"
    }
  }

  @Test
  fun barrierOfCapacity1IsANoOp() = runTest {
    checkAll(10, Arb.constant(Unit)) {
      val barrier = CyclicBarrier(1)
      barrier.await()
    }
  }

  @Test
  fun awaitingAllInParallelResumesAllCoroutines() = runTestUsingDefaultDispatcher {
    checkAll(10, Arb.int(1, 20)) { i ->
      val barrier = CyclicBarrier(i)
      (0 until i).parMap { barrier.await() }
    }
  }

  @Test
  fun shouldResetOnceFull() = runTestUsingDefaultDispatcher {
    checkAll(10, Arb.constant(Unit)) {
      val barrier = CyclicBarrier(2)
      parZip({ barrier.await() }, { barrier.await() }) { _, _ -> }
      barrier.capacity shouldBe 2
    }
  }

  @Test
  fun executesRunnableOnceFull() = runTestUsingDefaultDispatcher {
    var barrierRunnableInvoked = false
    val barrier = CyclicBarrier(2) { barrierRunnableInvoked = true }
    parZip({ barrier.await() }, { barrier.await() }) { _, _ -> }
    barrier.capacity shouldBe 2
    barrierRunnableInvoked shouldBe true
  }

  @Test
  fun awaitIsCancelable() = runTest {
    checkAll(10, Arb.int(2, Int.MAX_VALUE)) { i ->
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

  @Test
  fun shouldCleanUpUponCancellationOfAwait() = runTest {
    checkAll(10, Arb.constant(Unit)) {
      val barrier = CyclicBarrier(2)
      launch(start = CoroutineStart.UNDISPATCHED) { barrier.await() }.cancelAndJoin()
    }
  }

  @Test
  fun resetCancelsAllAwaiting() = runTest {
    checkAll(10, Arb.int(2, 20)) { i ->
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

  @Test
  fun shouldCleanUpUponReset() = runTestUsingDefaultDispatcher {
    checkAll(10, Arb.int(2, 20)) { i ->
      val barrier = CyclicBarrier(i)
      val exitCase = CompletableDeferred<ExitCase>()

      launch(start = CoroutineStart.UNDISPATCHED) {
        guaranteeCase({ barrier.await() }, exitCase::complete)
      }

      barrier.reset()

      (0 until i).parMap { barrier.await() }
    }
  }

  @Test
  fun raceFiberCancelAndBarrierFull() = runTestUsingDefaultDispatcher {
    checkAll(10, Arb.constant(Unit)) {
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

  @Test
  fun reset() = runTest {
    checkAll(10, Arb.int(2..10)) { n ->
      val barrier = CyclicBarrier(n)

      val exits = (0 until n - 1).map { CompletableDeferred<ExitCase>() }

      val jobs = (0 until n - 1).map { i ->
        launch(start = CoroutineStart.UNDISPATCHED) {
          guaranteeCase({ barrier.await() }, exits[i]::complete)
        }
      }

      barrier.reset()

      exits.zip(jobs) { exitCase, job ->
        exitCase.await().shouldBeTypeOf<ExitCase.Cancelled>()
        job.isCancelled shouldBe true
      }
    }
  }
}
