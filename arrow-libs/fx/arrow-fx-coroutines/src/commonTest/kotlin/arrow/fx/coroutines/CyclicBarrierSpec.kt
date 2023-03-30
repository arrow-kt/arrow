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
  
  "should clean up upon cancelation of await" {
    checkAll(Arb.constant(Unit)) {
      val barrier = CyclicBarrier(2)
      launch(start = CoroutineStart.UNDISPATCHED) { barrier.await() }.cancelAndJoin()
      
      barrier.capacity shouldBe 2
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
})
