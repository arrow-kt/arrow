package arrow.fx.stm

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random

class TQueueTest : StringSpec({
    "writing to a queue adds an element" {
      val tq = TQueue.new<Int>()
      atomically { tq.write(10) }
      atomically { tq.flush() } shouldBe listOf(10)
    }
    "reading from a queue should retry if the queue is empty" {
      val tq = TQueue.new<Int>()
      atomically {
        stm { tq.read().let { true } } orElse { false }
      } shouldBe false
    }
    "reading from a queue should remove that value" {
      val tq = TQueue.new<Int>()
      atomically { tq.write(10); tq.write(20) }
      atomically { tq.read() } shouldBe 10
      atomically { tq.flush() } shouldBe listOf(20)
    }
    "tryRead behaves like read if there are values to read" {
      val tq = TQueue.new<Int>()
      atomically { tq.write(10) }
      atomically { tq.tryRead() } shouldBe 10
      atomically { tq.flush() } shouldBe emptyList()
    }
    "tryRead returns null if the queue is empty" {
      val tq = TQueue.new<Int>()
      atomically { tq.tryRead() } shouldBe null
    }
    "flush empties the entire queue and returns it" {
      val tq = TQueue.new<Int>()
      atomically { tq.write(20); tq.write(30); tq.write(40) }
      atomically { tq.flush() } shouldBe listOf(20, 30, 40)
      atomically { tq.flush() } shouldBe emptyList()
    }
    "reading/flushing should work after mixed reads/writes" {
      val tq = TQueue.new<Int>()
      atomically { tq.write(20); tq.write(30); tq.peek(); tq.write(40) }
      atomically { tq.read() } shouldBe 20
      atomically { tq.flush() } shouldBe listOf(30, 40)

      atomically { tq.write(20); tq.write(30); tq.peek(); tq.write(40) }
      atomically { tq.flush() } shouldBe listOf(20, 30, 40)
      atomically { tq.flush() } shouldBe emptyList()
    }
    "peek should leave the queue unchanged" {
      val tq = TQueue.new<Int>()
      atomically { tq.write(20); tq.write(30); tq.write(40) }
      atomically { tq.peek() } shouldBeExactly 20
      atomically { tq.flush() } shouldBe listOf(20, 30, 40)
    }
    "peek should retry if the queue is empty" {
      val tq = TQueue.new<Int>()
      atomically {
        stm { tq.peek().let { true } } orElse { false }
      } shouldBe false
    }
    "tryPeek should behave like peek if there are elements" {
      val tq = TQueue.new<Int>()
      atomically { tq.write(20); tq.write(30); tq.write(40) }
      atomically { tq.peek() } shouldBeExactly
        atomically { tq.tryPeek()!! }
      atomically { tq.flush() } shouldBe listOf(20, 30, 40)
    }
    "tryPeek should return null if the queue is empty" {
      val tq = TQueue.new<Int>()
      atomically { tq.tryPeek() } shouldBe null
    }
    "isEmpty and isNotEmpty should work correctly" {
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
    "size should return the correct amount" {
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
    "writeFront should work correctly" {
      val tq = TQueue.new<Int>()
      atomically { tq.writeFront(203) }
      atomically { tq.peek() } shouldBeExactly 203
      atomically { tq.writeFront(50) }
      atomically { tq.peek() } shouldBeExactly 50
      atomically { tq.flush() } shouldBe listOf(50, 203)
    }
    "removeAll should work" {
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
)
