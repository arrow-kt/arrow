package arrow.fx.coroutines.stream.concurrent

import arrow.core.Option
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.StreamSpec
import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.append
import arrow.fx.coroutines.stream.compile
import arrow.fx.coroutines.stream.noneTerminate
import arrow.fx.coroutines.stream.parJoinUnbounded
import arrow.fx.coroutines.stream.terminateOnNone
import arrow.fx.coroutines.timeOutOrNull
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts

class QueueTest : StreamSpec(spec = {

  "Queue with capacity can always take tryOffer1" {
    checkAll(Arb.int()) { i ->
      val q = Queue.unbounded<Int>()

      q.tryOffer1(i) shouldBe true
      q.dequeue().take(1).compile().toList() shouldBe listOf(i)
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
      val expected = s.compile().toList()
      val n = expected.size
      val q = Queue.unbounded<Int>()

      Stream(
        q.dequeue(),
        s.through(q.enqueue()).drain()
      ).parJoinUnbounded()
        .take(n)
        .compile()
        .toList() shouldBe expected
    }
  }

  "dequeueAvailable" {
    checkAll(Arb.stream(Arb.int())) { s ->
      val expected = s.compile().toList()

      val q = Queue.unbounded<Option<Int>>()

      val res = s.noneTerminate()
        .through(q.enqueue())
        .drain()
        .append {
          q.dequeueChunk(Int.MAX_VALUE)
            .terminateOnNone()
            .chunks()
        }
        .compile()
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
      val expected = s.compile().toList()
      val q = Queue.unbounded<Option<Int>>()

      s.noneTerminate()
        .effectMap { q.enqueue1(it) }
        .drain()
        .append {
          Stream.constant(batchSize)
            .through(q.dequeueBatch())
            .terminateOnNone()
        }
        .compile()
        .toList() shouldBe expected
    }
  }

  "Queue.sliding - accepts maxSize elements while sliding over capacity" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, maxSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val expected = s.compile().toList().takeLast(maxSize)

      val q = Queue.sliding<Option<Int>>(maxSize)

      s.noneTerminate()
        .effectMap { q.enqueue1(it) }
        .drain()
        .append { q.dequeue().terminateOnNone() }
        .compile()
        .toList() shouldBe expected
    }
  }

  "Queue.sliding - dequeueBatch" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts(), Arb.positiveInts()) { s, maxSize0, batchSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val batchSize = batchSize0 % 20 + 1
      val expected = s.compile().toList().takeLast(maxSize)
      val q = Queue.sliding<Option<Int>>(maxSize)

      s.noneTerminate()
        .effectMap { q.enqueue1(it) }
        .drain().append {
          Stream.constant(batchSize)
            .through(q.dequeueBatch())
            .terminateOnNone()
        }
        .compile()
        .toList() shouldBe expected
    }
  }

  "Queue.dropping - accepts maxSize elements while dropping over capacity" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, maxSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val expected = s.compile().toList().take(maxSize)

      val q = Queue.dropping<Int>(maxSize)

      s.effectMap { q.enqueue1(it) }
        .drain()
        .append {
          q.dequeue().take(expected.size)
        }
        .compile()
        .toList() shouldBe expected
    }
  }

  "Queue.dropping - dequeueBatch" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts(), Arb.positiveInts()) { s, maxSize0, batchSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val batchSize = batchSize0 % 20 + 1
      val expected = s.compile().toList().take(maxSize)
      val q = Queue.dropping<Int>(maxSize)

      s.effectMap { q.enqueue1(it) }
        .drain().append {
          Stream.constant(batchSize)
            .through(q.dequeueBatch())
            .take(expected.size)
        }
        .compile()
        .toList() shouldBe expected
    }
  }

  "dequeue releases subscriber on " - {
    "interrupt" {
      val q = Queue.unbounded<Int>()
      q.dequeue().interruptAfter(100.milliseconds).compile().drain()
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
})
