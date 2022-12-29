package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class CountDownLatchSpec : StringSpec({
  "should raise an exception when constructed with a negative or zero capacity" {
    checkAll(Arb.long(Long.MIN_VALUE, 0)) { i ->
      shouldThrow<IllegalArgumentException> { CountDownLatch(i) }.message shouldBe
        "CountDownLatch must be constructed with positive non-zero initial count, but was $i"
    }
  }
  
  "release and then await should complete" {
    checkAll(Arb.long(1, 100)) { count ->
      val latch = CountDownLatch(count)
      repeat(count.toInt()) { latch.countDown() }
      latch.await() shouldBe Unit
    }
  }
  
  "await and then release should complete" {
    checkAll(Arb.long(1, 100)) { count ->
      val latch = CountDownLatch(count)
      val job = launch { latch.await() }
      repeat(count.toInt()) { latch.countDown() }
      job.join() shouldBe Unit
    }
  }
  
  "await with > 1 latch unreleased should block" {
    checkAll(Arb.long(1, 100)) { count ->
      val latch = CountDownLatch(count)
      repeat(count.toInt() - 1) { latch.countDown() }
      withTimeoutOrNull(1) { latch.await() }.shouldBeNull()
      latch.count() shouldBe 1
    }
  }
  
  "multiple awaits should all complete" {
    checkAll(Arb.long(1, 100)) { count ->
      val latch = CountDownLatch(count)
      val jobs = (0 until count).map { launch { latch.await() } }
      repeat(count.toInt()) { latch.countDown() }
      jobs.joinAll()
    }
  }
  
  "should release when latches == 0" {
    val latch = CountDownLatch(1)
    latch.countDown()
    latch.countDown()
  }
  
  "await is cancelable" {
    val latch = CountDownLatch(1)
    val exit = CompletableDeferred<ExitCase>()
    val job = launch(start = CoroutineStart.UNDISPATCHED) {
      guaranteeCase({ latch.await() }, exit::complete)
    }
    job.cancelAndJoin()
    exit.isCompleted shouldBe true
    exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
  }
})
