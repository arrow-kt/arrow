package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
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
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test

class CountDownLatchSpec {
  @Test
  fun shouldRaiseAnExceptionWhenConstructedWithNegativeOrZeroCapacity() = runTest {
    checkAll(Arb.long(Long.MIN_VALUE, 0)) { i ->
      shouldThrow<IllegalArgumentException> { CountDownLatch(i) }.message shouldBe
        "CountDownLatch must be constructed with positive non-zero initial count, but was $i"
    }
  }

  @Test
  fun releaseAndThenAwaitShouldComplete() = runTest {
    checkAll(Arb.long(1, 100)) { count ->
      val latch = CountDownLatch(count)
      repeat(count.toInt()) { latch.countDown() }
      latch.await() shouldBe Unit
    }
  }

  @Test
  fun awaitAndThenReleaseShouldComplete() = runTest {
    checkAll(Arb.long(1, 100)) { count ->
      val latch = CountDownLatch(count)
      val job = launch { latch.await() }
      repeat(count.toInt()) { latch.countDown() }
      job.join() shouldBe Unit
    }
  }

  @Test
  fun awaitWithMoreThanOneLatchUnreleasedShouldBlock() = runTest {
    checkAll(Arb.long(1, 100)) { count ->
      val latch = CountDownLatch(count)
      repeat(count.toInt() - 1) { latch.countDown() }
      withTimeoutOrNull(1) { latch.await() }.shouldBeNull()
      latch.count() shouldBe 1
    }
  }

  @Test
  fun multipleAwaitsShouldAllComplete() = runTest {
    checkAll(Arb.long(1, 100)) { count ->
      val latch = CountDownLatch(count)
      val jobs = (0 until count).map { launch { latch.await() } }
      repeat(count.toInt()) { latch.countDown() }
      jobs.joinAll()
    }
  }

  @Test
  fun shouldReleaseWhenLatchesEqualsZero() = runTest {
    val latch = CountDownLatch(1)
    latch.countDown()
    latch.countDown()
  }

  @Test
  fun awaitIsCancelable() = runTest {
    val latch = CountDownLatch(1)
    val exit = CompletableDeferred<ExitCase>()
    val job = launch(start = CoroutineStart.UNDISPATCHED) {
      guaranteeCase({ latch.await() }, exit::complete)
    }
    job.cancelAndJoin()
    exit.isCompleted shouldBe true
    exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
  }
}
