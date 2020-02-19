package arrow.fx

import arrow.core.None
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Left
import arrow.core.Tuple4
import arrow.core.extensions.list.traverse.traverse
import arrow.core.fix
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.typeclasses.milliseconds
import arrow.test.UnitSpec
import arrow.test.generators.nonEmptyList
import arrow.test.generators.tuple2
import arrow.test.generators.tuple3
import arrow.test.laws.equalUnderTheLaw
import io.kotlintest.fail
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlin.coroutines.CoroutineContext

class QueueTest : UnitSpec() {

  init {

    fun allStrategyTests(
      label: String,
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {

      "$label - make a queue the add values then retrieve in the same order" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = !queue(l.size)
            !l.traverse(IO.applicative(), q::offer)
            val nl = !(1..l.size).toList().traverse(IO.applicative()) { q.take() }
            nl.fix()
          }.unsafeRunSync() == l.toList()
        }
      }

      "$label - offer and take a number of values in the same order" {
        forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = !queue(3)
            !q.offer(t.a)
            !q.offer(t.b)
            !q.offer(t.c)
            val first = !q.take()
            val second = !q.take()
            val third = !q.take()
            Tuple3(first, second, third)
          }.unsafeRunSync() == t
        }
      }

      "$label - time out taking from an empty queue" {
        IO.fx {
          val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::take)
          val start = !effect { System.currentTimeMillis() }
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          !effect { received shouldBe None }
          !effect { (elapsed >= 100) shouldBe true }
        }.unsafeRunSync()
      }

      "$label - suspended take calls on an empty queue complete when offer calls made to queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(3)
            val first = !q.take().fork(ctx)
            !q.offer(i)
            !first.join()
          }.unsafeRunSync() == i
        }
      }

      "$label - multiple take calls on an empty queue complete when until as many offer calls made to queue" {
        forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = !queue(3)
            val first = !q.take().fork(ctx)
            val second = !q.take().fork(ctx)
            val third = !q.take().fork(ctx)
            !q.offer(t.a)
            !q.offer(t.b)
            !q.offer(t.c)
            val firstValue = !first.join()
            val secondValue = !second.join()
            val thirdValue = !third.join()
            setOf(firstValue, secondValue, thirdValue)
          }.unsafeRunSync() == setOf(t.a, t.b, t.c)
        }
      }

      "$label - taking from a shutdown queue creates a QueueShutdown error" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(10)
            !q.offer(i)
            !q.shutdown()
            !q.take()
          }.attempt().unsafeRunSync() == Left(QueueShutdown)
        }
      }

      "$label - time out peeking from an empty queue" {
        IO.fx {
          val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::peek)
          val start = !effect { System.currentTimeMillis() }
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          !effect { received shouldBe None }
          !effect { (elapsed >= 100) shouldBe true }
        }.equalUnderTheLaw(IO.unit, EQ())
      }

      "$label - suspended peek calls on an empty queue complete when offer calls made to queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(3)
            val first = !q.peek().fork(ctx)
            !q.offer(i)
            val res = !first.join()
            !effect { res shouldBe i }
          }.equalUnderTheLaw(IO.unit, EQ())
        }
      }

      "$label - multiple peek calls on an empty queue all complete with the first value is received" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(1)
            val first = !q.peek().fork(ctx)
            val second = !q.peek().fork(ctx)
            val third = !q.peek().fork(ctx)
            !q.offer(i)
            val firstValue = !first.join()
            val secondValue = !second.join()
            val thirdValue = !third.join()
            !effect { setOf(firstValue, secondValue, thirdValue) shouldBe setOf(i, i, i) }
          }.equalUnderTheLaw(IO.unit, EQ())
        }
      }

      "$label - peeking a shutdown queue creates a QueueShutdown error" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(10)
            !q.offer(i)
            !q.shutdown()
            !q.peek()
          }.attempt().unsafeRunSync() == Left(QueueShutdown)
        }
      }

      "$label - peek does not remove value from Queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(10)
            val size1 = !q.size()
            !q.offer(i)
            val size2 = !q.size()
            val peeked = !q.peek()
            val size3 = !q.size()
            !effect { Tuple4(size1, size2, peeked, size3) shouldBe Tuple4(0, 1, i, 1) }
          }.equalUnderTheLaw(IO.unit, EQ())
        }
      }

      "$label - offering to a shutdown queue creates a QueueShutdown error" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(10)
            !q.shutdown()
            !q.offer(i)
          }.attempt().unsafeRunSync() == Left(QueueShutdown)
        }
      }

      "$label - joining a forked, incomplete take call on a shutdown queue creates a QueueShutdown error" {
        IO.fx {
          val q = !queue(10)
          val t = !q.take().fork(ctx)
          !q.shutdown()
          !t.join()
        }.attempt().unsafeRunSync() shouldBe Left(QueueShutdown)
      }

      "$label - create a shutdown hook completing a promise, then shutdown the queue, the promise should be completed" {
        IO.fx {
          val q = !queue(10)
          val p = !Promise<ForIO, Boolean>(IO.concurrent())
          !(q.awaitShutdown().followedBy(p.complete(true))).fork()
          !q.shutdown()
          !p.get()
        }.unsafeRunSync()
      }

      "$label - create a shutdown hook completing a promise twice, then shutdown the queue, both promises should be completed" {
        IO.fx {
          val q = !queue(10)
          val p1 = !Promise<ForIO, Boolean>(IO.concurrent())
          val p2 = !Promise<ForIO, Boolean>(IO.concurrent())
          !(q.awaitShutdown().followedBy(p1.complete(true))).fork()
          !(q.awaitShutdown().followedBy(p2.complete(true))).fork()
          !q.shutdown()
          !mapN(p1.get(), p2.get()) { (p1, p2) -> p1 && p2 }
        }.suspended()
      }

      "$label - shut it down, create a shutdown hook completing a promise, the promise should be completed immediately" {
        IO.fx {
          val q = !queue(10)
          !q.shutdown()
          val p = !Promise<ForIO, Boolean>(IO.concurrent())
          !(q.awaitShutdown().followedBy(p.complete(true))).fork()
          !p.get()
        }.unsafeRunSync()
      }

      "$label - tryOffer on a shutdown Queue returns false" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(10)
            !q.shutdown()
            val offered = !q.tryOffer(i)
            !effect { offered shouldBe false }
          }.equalUnderTheLaw(IO.unit, EQ())
        }
      }

      "$label - tryTake on a shutdown Queue returns false" {
        IO.fx {
          val q = !queue(10)
          !q.shutdown()
          val took = !q.tryTake()
          !effect { took shouldBe None }
        }.equalUnderTheLaw(IO.unit, EQ())
      }

      "$label - tryPeek on a shutdown Queue returns false" {
        IO.fx {
          val q = !queue(10)
          !q.shutdown()
          val peeked = !q.tryPeek()
          !effect { peeked shouldBe None }
        }.equalUnderTheLaw(IO.unit, EQ())
      }

      "$label - tryTake on an empty Queue returns false" {
        IO.fx {
          val q = !queue(10)
          val took = !q.tryTake()
          !effect { took shouldBe None }
        }.equalUnderTheLaw(IO.unit, EQ())
      }

      "$label - tryPeek on an empty Queue returns false" {
        IO.fx {
          val q = !queue(10)
          val peeked = !q.tryPeek()
          !effect { peeked shouldBe None }
        }.equalUnderTheLaw(IO.unit, EQ())
      }
    }

    fun boundedStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      val label = "BoundedQueue"
      allStrategyTests(label, ctx, queue)

      "$label - time out offering to a queue at capacity" {
        IO.fx {
          val q = !queue(1)
          !q.offer(1)
          val start = !effect { System.currentTimeMillis() }
          val wontComplete = q.offer(2)
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          !effect { received shouldBe None }
          !effect { (elapsed >= 100) shouldBe true }
        }.unsafeRunSync()
      }

      "$label - tryOffer returns false at capacity" {
        IO.fx {
          val q = !queue(1)
          !q.offer(1)
          val offered = !q.tryOffer(2)
          !effect { offered shouldBe false }
        }.unsafeRunSync()
      }

      "$label - capacity must be a positive integer" {
        queue(0).attempt().suspended().fold(
          { err -> err.shouldBeInstanceOf<IllegalArgumentException>() },
          { fail("Expected Left<IllegalArgumentException>") }
        )
      }

      "$label - suspended offers called on an full queue complete when take calls made to queue" {
        forAll(Gen.tuple2(Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = !queue(1)
            !q.offer(t.a)
            !q.offer(t.b).fork(ctx)
            val first = !q.take()
            val second = !q.take()
            Tuple2(first, second)
          }.unsafeRunSync() == t
        }
      }

      "$label - multiple offer calls on an full queue complete when as many take calls are made to queue" {
        forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = !queue(1)
            !q.offer(t.a)
            !q.offer(t.b).fork(ctx)
            !q.offer(t.c).fork(ctx)
            val first = !q.take()
            val second = !q.take()
            val third = !q.take()
            setOf(first, second, third)
          }.unsafeRunSync() == setOf(t.a, t.b, t.c)
        }
      }

      "$label - joining a forked offer call made to a shut down queue creates a QueueShutdown error" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(1)
            !q.offer(i)
            val o = !q.offer(i).fork(ctx)
            !q.shutdown()
            !o.join()
          }.attempt().unsafeRunSync() == Left(QueueShutdown)
        }
      }
    }

    fun slidingStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      val label = "SlidingQueue"
      allStrategyTests(label, ctx, queue)

      "$label - capacity must be a positive integer" {
        queue(0).attempt().suspended().fold(
          { err -> err.shouldBeInstanceOf<IllegalArgumentException>() },
          { fail("Expected Left<IllegalArgumentException>") }
        )
      }

      "!$label - tryOffer returns false at capacity" {
        IO.fx {
          val q = !queue(1)
          !q.offer(1)
          val offered = !q.tryOffer(2)
          !effect { offered shouldBe false }
        }.unsafeRunSync()
      }

      "$label - removes first element after offering to a queue at capacity" {
        forAll(Gen.int(), Gen.nonEmptyList(Gen.int())) { x, xs ->
          IO.fx {
            val q = !queue(xs.size)
            !q.offer(x)
            !xs.traverse(IO.applicative(), q::offer)
            val taken = !(1..xs.size).toList().traverse(IO.applicative()) { q.take() }
            taken.fix()
          }.unsafeRunSync() == xs.toList()
        }
      }
    }

    fun droppingStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      val label = "DroppingQueue"

      allStrategyTests(label, ctx, queue)

      "$label - capacity must be a positive integer" {
        queue(0).attempt().suspended().fold(
          { err -> err.shouldBeInstanceOf<IllegalArgumentException>() },
          { fail("Expected Left<IllegalArgumentException>") }
        )
      }

      "!$label - tryOffer returns false at capacity" {
        IO.fx {
          val q = !queue(1)
          !q.offer(1)
          val offered = !q.tryOffer(2)
          !effect { offered shouldBe false }
        }.unsafeRunSync()
      }

      "$label - drops elements offered to a queue at capacity" {
        forAll(Gen.int(), Gen.int(), Gen.nonEmptyList(Gen.int())) { x, x2, xs ->
          IO.fx {
            val q = !queue(xs.size)
            !xs.traverse(IO.applicative(), q::offer)
            !q.offer(x) // this `x` should be dropped
            val taken = !(1..xs.size).toList().traverse(IO.applicative()) { q.take() }
            !q.offer(x2)
            val taken2 = !q.take()
            taken.fix() + taken2
          }.unsafeRunSync() == xs.toList() + x2
        }
      }
    }

    fun unboundedStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      allStrategyTests("UnboundedQueue", ctx, queue)
    }

    boundedStrategyTests { capacity -> Queue.bounded<ForIO, Int>(capacity, IO.concurrent()).fix() }

    slidingStrategyTests { capacity -> Queue.sliding<ForIO, Int>(capacity, IO.concurrent()).fix() }

    droppingStrategyTests { capacity -> Queue.dropping<ForIO, Int>(capacity, IO.concurrent()).fix() }

    unboundedStrategyTests { Queue.unbounded<ForIO, Int>(IO.concurrent()).fix() }
  }
}
