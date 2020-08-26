package arrow.fx.coroutines.stream.concurrent

import arrow.core.Option
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.StreamSpec
import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.seconds
import arrow.fx.coroutines.sleep
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.append
import arrow.fx.coroutines.stream.noneTerminate
import arrow.fx.coroutines.stream.parJoinUnbounded
import arrow.fx.coroutines.stream.terminateOnNone
import arrow.fx.coroutines.stream.toList
import arrow.fx.coroutines.timeOutOrNull
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts
import kotlin.math.max

class QueueTest : StreamSpec(spec = {

  "Queue with capacity can always take tryOffer1" {
    checkAll(Arb.int()) { i ->
      val q = Queue.unbounded<Int>()

      q.tryOffer1(i) shouldBe true
      q.dequeue().take(1).toList() shouldBe listOf(i)
    }
  }

  "Outstanding taker received tryOffer1 value" {
    checkAll(Arb.int()) { i ->
      val q = Queue.unbounded<Int>()
      val start = Promise<Unit>()

      val f = ForkAndForget {
        start.complete(Unit)
        q.dequeue1()
      }

      start.get()

      q.tryOffer1(i) shouldBe true
      f.join() shouldBe i
    }
  }

  "unbounded producer/consumer" {
    checkAll(Arb.stream(Arb.int())) { s ->
      val expected = s.toList()
      val n = expected.size
      val q = Queue.unbounded<Int>()

      Stream(
        q.dequeue(),
        s.through(q.enqueue()).void()
      ).parJoinUnbounded()
        .take(n)
        .toList() shouldBe expected
    }
  }

  "dequeueAvailable" {
    checkAll(Arb.stream(Arb.int())) { s ->
      val expected = s.toList()

      val q = Queue.unbounded<Option<Int>>()

      val res = s.noneTerminate()
        .through(q.enqueue())
        .void()
        .append {
          q.dequeueChunk(Int.MAX_VALUE)
            .terminateOnNone()
            .chunks()
        }
        .toList()

      assertSoftly {
        res.size shouldBeLessThan 2
        res.flatMap { it.toList() } shouldBe expected
      }
    }
  }

  "dequeueBatch unbounded" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, batchSize0 ->
      val batchSize = batchSize0 % 20 + 1
      val expected = s.toList()
      val q = Queue.unbounded<Option<Int>>()

      s.noneTerminate()
        .effectMap { q.enqueue1(it) }
        .void()
        .append {
          Stream.constant(batchSize)
            .through(q.dequeueBatch())
            .terminateOnNone()
        }
        .toList() shouldBe expected
    }
  }

  "Queue.sliding - accepts maxSize elements while sliding over capacity" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, maxSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val expected = s.toList().takeLast(maxSize)

      val q = Queue.sliding<Option<Int>>(maxSize)

      s.noneTerminate()
        .effectMap { q.enqueue1(it) }
        .void()
        .append { q.dequeue().terminateOnNone() }
        .toList() shouldBe expected
    }
  }

  "Queue.sliding - dequeueBatch" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts(), Arb.positiveInts()) { s, maxSize0, batchSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val batchSize = batchSize0 % 20 + 1
      val expected = s.toList().takeLast(maxSize)
      val q = Queue.sliding<Option<Int>>(maxSize)

      s.noneTerminate()
        .effectMap { q.enqueue1(it) }
        .void().append {
          Stream.constant(batchSize)
            .through(q.dequeueBatch())
            .terminateOnNone()
        }
        .toList() shouldBe expected
    }
  }

  "Queue.dropping - accepts maxSize elements while dropping over capacity" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, maxSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val expected = s.toList().take(maxSize)

      val q = Queue.dropping<Int>(maxSize)

      s.effectMap { q.enqueue1(it) }
        .void()
        .append {
          q.dequeue().take(expected.size)
        }
        .toList() shouldBe expected
    }
  }

  "Queue.dropping - dequeueBatch" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts(), Arb.positiveInts()) { s, maxSize0, batchSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val batchSize = batchSize0 % 20 + 1
      val expected = s.toList().take(maxSize)
      val q = Queue.dropping<Int>(maxSize)

      s.effectMap { q.enqueue1(it) }
        .void().append {
          Stream.constant(batchSize)
            .through(q.dequeueBatch())
            .take(expected.size)
        }
        .toList() shouldBe expected
    }
  }

  "dequeue releases subscriber on " - {
    "interrupt" {
      val q = Queue.unbounded<Int>()
      q.dequeue().interruptAfter(100.milliseconds).void()
      q.enqueue1(1)
      q.enqueue1(2)
      q.dequeue1() shouldBe 1
    }

    "cancel" {
      val q = Queue.unbounded<Int>()
      timeOutOrNull(100.milliseconds) {
        q.dequeue1()
      } shouldBe null
      q.enqueue1(1)
      q.enqueue1(2)
      q.dequeue1() shouldBe 1
    }
  }

  "Queue.bounded with capacity can always take tryOffer1" {
    checkAll(Arb.int()) { i ->
      val q = Queue.bounded<Int>(1)

      q.tryOffer1(i) shouldBe true
      q.dequeue().take(1).toList() shouldBe listOf(i)
    }
  }

  "Queue.bounded with outstanding taker received tryOffer1 value" {
    checkAll(Arb.int()) { i ->
      val q = Queue.bounded<Int>(1)
      val start = Promise<Unit>()

      val f = ForkAndForget {
        start.complete(Unit)
        q.dequeue1()
      }

      start.get()

      q.tryOffer1(i) shouldBe true
      f.join() shouldBe i
    }
  }

  "Queue.bounded producer/consumer" {
    checkAll(Arb.stream(Arb.int())) { s ->
      val expected = s.toList()
      val n = expected.size
      val q = Queue.bounded<Int>(n)

      Stream(
        q.dequeue(),
        s.through(q.enqueue()).void()
      ).parJoinUnbounded()
        .take(n)
        .toList() shouldBe expected
    }
  }

  "Queue.bounded none terminating producer/consumer" {
    checkAll(Arb.stream(Arb.int())) { s ->
      val expected = s.toList()

      // Queue.bounded requires minimum size of 1
      val n = max(expected.size, 1)
      val q = Queue.bounded<Option<Int>>(n)

      q.dequeue()
        .terminateOnNone()
        .concurrently(
          s.noneTerminate()
            .through(q.enqueue())
        ).toList() shouldBe s.toList()
    }
  }

  "Queue.bounded(0) with outstanding taker cannot offer" {
    val q = Queue.bounded<Int>(0)
    val taker = ForkConnected { q.dequeue1() }
    sleep(1.seconds)
    q.tryOffer1(1) shouldBe false
  }

  "Queue.synchronous consumer / producer" {
    checkAll(Arb.stream(Arb.int())) { s ->
      val q = Queue.synchronous<Option<Int>>()

      q.dequeue()
        .terminateOnNone()
        .concurrently(
          s.noneTerminate()
            .through(q.enqueue())
        ).toList() shouldBe s.toList()
    }
  }

  "Queue.synchronous without outstanding taker cannot tryOffer1" {
    val q = Queue.synchronous<Int>()
    q.tryOffer1(1) shouldBe false
  }

  "Queue.synchronous with outstanding taker can tryOffer1" {
    val q = Queue.synchronous<Int>()
    ForkConnected { q.dequeue1() }
    sleep(1.seconds)
    q.tryOffer1(1) shouldBe true
  }
})
