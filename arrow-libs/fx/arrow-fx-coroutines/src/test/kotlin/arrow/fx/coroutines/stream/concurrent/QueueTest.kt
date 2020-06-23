package arrow.fx.coroutines.stream.concurrent

import arrow.core.Option
import arrow.fx.coroutines.StreamSpec
import arrow.fx.coroutines.parTupledN
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.append
import arrow.fx.coroutines.stream.compile
import arrow.fx.coroutines.stream.noneTerminate
import arrow.fx.coroutines.stream.terminateOnNone
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts

class QueueTest : StreamSpec(spec = {

  "unbounded producer/consumer" {
    checkAll(Arb.stream(Arb.int())) { s ->
      val expected = s.compile().toList()
      val n = expected.size
      val q = Queue.unbounded<Int>()

      parTupledN({
        q.dequeue()
          .take(n)
          .compile()
          .toList()
      }, {
        s.through(q.enqueue())
          .compile()
          .drain()
      }).first shouldBe expected
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

  "circularBuffer" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, maxSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val expected = s.compile().toList().takeLast(maxSize)

      val q = Queue.circularBuffer<Option<Int>>(maxSize + 1)

      s.noneTerminate()
        .effectMap { q.enqueue1(it) }
        .drain()
        .append { q.dequeue().terminateOnNone() }
        .compile()
        .toList() shouldBe expected
    }
  }

  "dequeueBatch circularBuffer" {
    checkAll(Arb.stream(Arb.int()), Arb.positiveInts(), Arb.positiveInts()) { s, maxSize0, batchSize0 ->
      val maxSize = maxSize0 % 20 + 1
      val batchSize = batchSize0 % 20 + 1
      val expected = s.compile().toList().takeLast(maxSize)
      val q = Queue.circularBuffer<Option<Int>>(maxSize + 1)

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
})
