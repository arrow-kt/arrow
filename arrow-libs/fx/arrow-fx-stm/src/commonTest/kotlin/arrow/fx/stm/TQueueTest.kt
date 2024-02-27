package arrow.fx.stm

import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test

class TQueueTest {

  @Test fun writingToAQueueAddsAnElement() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.write(10) }
    atomically { tq.flush() } shouldBe listOf(10)
  }

  @Test fun readingFromAQueueShouldRetryIfTheQueueIsEmpty() = runTest {
    val tq = TQueue.new<Int>()
    atomically {
      stm { tq.read().let { true } } orElse { false }
    } shouldBe false
  }

  @Test fun readingFromAQueueShouldRemoveThatValue() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.write(10); tq.write(20) }
    atomically { tq.read() } shouldBe 10
    atomically { tq.flush() } shouldBe listOf(20)
  }

  @Test fun tryReadBehavesLikeReadIfThereAreValuesToRead() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.write(10) }
    atomically { tq.tryRead() } shouldBe 10
    atomically { tq.flush() } shouldBe emptyList()
  }

  @Test fun tryReadReturnsNullIfTheQueueIsEmpty() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.tryRead() } shouldBe null
  }

  @Test fun flushEmptiesTheEntireQueueAndReturnsIt() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.write(20); tq.write(30); tq.write(40) }
    atomically { tq.flush() } shouldBe listOf(20, 30, 40)
    atomically { tq.flush() } shouldBe emptyList()
  }

  @Test fun readingFlushingShouldWorkAfterMixedReadsWrites() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.write(20); tq.write(30); tq.peek(); tq.write(40) }
    atomically { tq.read() } shouldBe 20
    atomically { tq.flush() } shouldBe listOf(30, 40)

    atomically { tq.write(20); tq.write(30); tq.peek(); tq.write(40) }
    atomically { tq.flush() } shouldBe listOf(20, 30, 40)
    atomically { tq.flush() } shouldBe emptyList()
  }

  @Test fun peekShouldLeaveTheQueueUnchanged() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.write(20); tq.write(30); tq.write(40) }
    atomically { tq.peek() } shouldBeExactly 20
    atomically { tq.flush() } shouldBe listOf(20, 30, 40)
  }

  @Test fun peekShouldRetryIfTheQueueIsEmpty() = runTest {
    val tq = TQueue.new<Int>()
    atomically {
      stm { tq.peek().let { true } } orElse { false }
    } shouldBe false
  }

  @Test fun tryPeekShouldBehaveLikePeekIfThereAreElements() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.write(20); tq.write(30); tq.write(40) }
    atomically { tq.peek() } shouldBeExactly
      atomically { tq.tryPeek()!! }
    atomically { tq.flush() } shouldBe listOf(20, 30, 40)
  }

  @Test fun tryPeekShouldReturnNullIfTheQueueIsEmpty() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.tryPeek() } shouldBe null
  }

  @Test fun isEmptyAndIsNotEmptyShouldWorkCorrectly() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.isEmpty() } shouldBe true
    atomically { tq.isNotEmpty() } shouldBe false
    atomically { tq.write(20) }
    atomically { tq.isEmpty() } shouldBe false
    atomically { tq.isNotEmpty() } shouldBe true
    atomically { tq.peek(); tq.write(30) }
    atomically { tq.isEmpty() } shouldBe false
    atomically { tq.isNotEmpty() } shouldBe true
  }

  @Test fun sizeShouldReturnTheCorrectAmount() = runTest {
    checkAll(Arb.int(0..50)) { i ->
      val tq = TQueue.new<Int>()
      atomically {
        for (j in 0..i) {
          // read to swap read and write lists randomly
          if (Random.nextFloat() > 0.9) tq.tryPeek()
          tq.write(j)
        }
      }
      atomically { tq.size() } shouldBeExactly i + 1
    }
  }

  @Test fun writeFrontShouldWorkCorrectly() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.writeFront(203) }
    atomically { tq.peek() } shouldBeExactly 203
    atomically { tq.writeFront(50) }
    atomically { tq.peek() } shouldBeExactly 50
    atomically { tq.flush() } shouldBe listOf(50, 203)
  }

  @Test fun removeAllShouldWork() = runTest {
    val tq = TQueue.new<Int>()
    atomically { tq.removeAll { true } }
    atomically { tq.flush() } shouldBe emptyList()

    atomically {
      for (i in 0..100) {
        tq.write(i)
      }
      tq.removeAll { it.rem(2) == 0 }
      tq.flush()
    } shouldBe (0..100).filter { it.rem(2) == 0 }
  }
}
