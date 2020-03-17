package arrow.fx

import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Right
import arrow.core.extensions.list.traverse.traverse
import arrow.core.extensions.nonemptylist.traverse.traverse
import arrow.core.fix
import arrow.core.test.UnitSpec
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.tuple2
import arrow.core.test.generators.tuple3
import arrow.fx.test.laws.equalUnderTheLaw
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.typeclasses.milliseconds
import io.kotlintest.fail
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
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
            !(1..l.size).toList().traverse(IO.applicative()) { q.take() }
          }.equalUnderTheLaw(IO.just(l.toList()))
        }
      }

      "$label - queue can be filled at once with enough capacity" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = !queue(l.size)
            val succeed = !q.tryOfferAll(l.toList())
            val res = !q.takeAll()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(true, l.toList())))
        }
      }

      "$label - queue can be filled at once over capacity with takers" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = !queue(l.size)
            val (join, _) = !q.take().fork()
            !IO.sleep(50.milliseconds) // Registered first, should receive first element of `tryOfferAll`

            val succeed = !q.tryOfferAll(listOf(500) + l.toList())
            val res = !q.takeAll()
            val head = !join
            Tuple3(succeed, res, head)
          }.equalUnderTheLaw(IO.just(Tuple3(true, l.toList(), 500)))
        }
      }

      "$label - tryOfferAll under capacity" {
        forAll(
          Gen.list(Gen.int()).filter { it.size <= 100 },
          Gen.int().filter { it > 100 }
        ) { l, capacity ->
          IO.fx {
            val q = !queue(capacity)
            val succeed = !q.tryOfferAll(l)
            val all = !q.takeAll()
            Tuple2(succeed, all)
          }.equalUnderTheLaw(IO.just(Tuple2(true, l)))
        }
      }

      "$label - takeAll takes all values from a Queue" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = !queue(l.size)
            !l.traverse(IO.applicative(), q::offer)
            val res = !q.takeAll()
            val after = !q.takeAll()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList(), emptyList())))
        }
      }

      "$label - peekAll reads all values from a Queue without removing them" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = !queue(l.size)
            !l.traverse(IO.applicative(), q::offer)
            val res = !q.peekAll()
            val after = !q.peekAll()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList(), l.toList())))
        }
      }

      "$label - empty queue takeAll is empty" {
        forAll(Gen.positiveIntegers()) { capacity ->
          IO.fx {
            val q = !queue(capacity)
            !q.takeAll()
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - empty queue peekAll is empty" {
        forAll(Gen.positiveIntegers()) { capacity ->
          IO.fx {
            val q = !queue(capacity)
            !q.peekAll()
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - time out taking from an empty queue" {
        IO.fx {
          val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::take)
          val start = !effect { System.currentTimeMillis() }
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          Tuple2(received, (elapsed >= 100))
        }.equalUnderTheLaw(IO.just(Tuple2(None, true)))
      }

      "$label - suspended take calls on an empty queue complete when offer calls made to queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(3)
            val first = !q.take().fork(ctx)
            !q.offer(i)
            !first.join()
          }.equalUnderTheLaw(IO.just(i))
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
          }.equalUnderTheLaw(IO.just(setOf(t.a, t.b, t.c)))
        }
      }

      "$label - time out peeking from an empty queue" {
        IO.fx {
          val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::peek)
          val start = !effect { System.currentTimeMillis() }
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          Tuple2(received, (elapsed >= 100))
        }.equalUnderTheLaw(IO.just(Tuple2(None, true)))
      }

      "$label - suspended peek calls on an empty queue complete when offer calls made to queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(3)
            val first = !q.peek().fork(ctx)
            !q.offer(i)
            !first.join()
          }.equalUnderTheLaw(IO.just(i))
        }
      }

      "$label - multiple peek calls offerAll is cancelable an empty queue all complete with the first value is received" {
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
            setOf(firstValue, secondValue, thirdValue)
          }.equalUnderTheLaw(IO.just(setOf(i, i, i)))
        }
      }

      "$label - peek does not remove value from Queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(10)
            !q.offer(i)
            val peeked = !q.peek()
            val took = !q.takeAll()
            Tuple2(peeked, took)
          }.equalUnderTheLaw(IO.just(Tuple2(i, listOf(i))))
        }
      }

      "$label - tryTake on an empty Queue returns None" {
        IO.fx {
          val q = !queue(10)
          !q.tryTake()
        }.equalUnderTheLaw(IO.just(None))
      }

      "$label - tryPeek on an empty Queue returns None" {
        IO.fx {
          val q = !queue(10)
          !q.tryPeek()
        }.equalUnderTheLaw(IO.just(None))
      }

      "$label - take is cancelable" {
        IO.fx {
          val q = !queue(1)
          val t1 = !q.take().fork()
          val t2 = !q.take().fork()
          val t3 = !q.take().fork()
          !IO.sleep(50.milliseconds) // Give take callbacks a chance to register
          !t2.cancel()
          !q.offer(1)
          !q.offer(3)
          val r1 = !t1.join()
          val r3 = !t3.join()
          val size = !q.size()
          Tuple2(setOf(r1, r3), size)
        }.equalUnderTheLaw(IO.just(Tuple2(setOf(1, 3), 0)))
      }

      "$label - peek is cancelable" {
        IO.fx {
          val q = !queue(1)
          val finished = !Promise<Int>()
          val fiber = !q.peek().flatMap(finished::complete).fork()
          !IO.sleep(50.milliseconds) // Give read callback a chance to register
          !fiber.cancel()
          !q.offer(10)
          val fallback = sleep(200.milliseconds).followedBy(IO.just(0))
          !IO.raceN(finished.get(), fallback)
        }.equalUnderTheLaw(IO.just(Right(0)))
      }

      "$label - takeAll returns emptyList with waiting suspended takers" {
        IO.fx {
          val q = !queue(1)
          val (_, cancel) = !q.take().fork()
          val res = !q.takeAll()
          !cancel
          res
        }.equalUnderTheLaw(IO.just(emptyList()))
      }

      "$label - peekAll returns emptyList with waiting suspended takers" {
        IO.fx {
          val q = !queue(1)
          val (_, cancel) = !q.take().fork()
          val res = !q.peekAll()
          !cancel
          res
        }.equalUnderTheLaw(IO.just(emptyList()))
      }

      "$label - offerAll offers all values with waiting suspended takers, and within capacity" {
        forAll(50,
          Gen.nonEmptyList(Gen.int()).filter { it.size in 1..50 },
          Gen.choose(52, 100)
        ) { l, capacity ->
          IO.fx {
            val q = !queue(capacity)
            val (_, cancel) = !q.take().fork()
            !IO.sleep(50.milliseconds) // Give take callbacks a chance to register

            !q.offerAll(l.toList())
            !cancel
            !q.peekAll()
          }.equalUnderTheLaw(IO.just(l.toList().drop(1)))
        }
      }

      "$label - offerAll can offer empty" {
        IO.fx {
          val q = !queue(1)
          !q.offer(1)
          !q.offerAll(emptyList())
          !q.peekAll()
        }.equalUnderTheLaw(IO.just(listOf(1)))
      }
    }

    fun strategyAtCapacityTests(
      label: String,
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      "$label - tryOffer returns false over capacity" {
        IO.fx {
          val q = !queue(1)
          !q.offer(1)
          !q.tryOffer(2)
        }.equalUnderTheLaw(IO.just(false))
      }

      "$label - tryOfferAll over capacity" {
        forAll(Gen.list(Gen.int()).filter { it.size > 1 }) { l ->
          IO.fx {
            val q = !queue(1)
            val succeed = !q.tryOfferAll(l)
            val res = !q.peekAll()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(false, emptyList())))
        }
      }

      "$label - can take and offer at capacity" {
        IO.fx {
          val q = !queue(1)
          val (join, _) = !q.take().fork()
          !IO.sleep(50.milliseconds)
          val succeed = !q.tryOfferAll(1, 2)
          val a = !q.take()
          val b = !join
          Tuple2(succeed, setOf(a, b))
        }.equalUnderTheLaw(IO.just(Tuple2(true, setOf(1, 2))))
      }
    }

    fun boundedStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      val label = "BoundedQueue"
      allStrategyTests(label, ctx, queue)
      strategyAtCapacityTests(label, ctx, queue)

      "$label - time out offering to a queue at capacity" {
        IO.fx {
          val q = !queue(1)
          !q.offer(1)
          val start = !effect { System.currentTimeMillis() }
          val wontComplete = q.offer(2)
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          Tuple2(received, (elapsed >= 100))
        }.equalUnderTheLaw(IO.just(Tuple2(None, true)))
      }

      "$label - time out offering multiple values to a queue at capacity" {
        IO.fx {
          val q = !queue(3)
          val start = !effect { System.currentTimeMillis() }
          val wontComplete = q.offerAll(1, 2, 3, 4)
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          Tuple2(received, (elapsed >= 100))
        }.equalUnderTheLaw(IO.just(Tuple2(None, true)))
      }

      "$label - queue cannot be filled at once without enough capacity" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = !queue(l.size)
            val succeed = !q.tryOfferAll(l.toList() + 1)
            val res = !q.takeAll()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(false, emptyList())))
        }
      }

      "$label - can offerAll at capacity with take" {
        IO.fx {
          val q = !queue(1)
          val (join, _) = !q.take().fork()
          !IO.sleep(50.milliseconds)
          !q.offerAll(1, 2)
          val a = !q.take()
          val b = !join
          setOf(a, b)
        }.equalUnderTheLaw(IO.just(setOf(1, 2)))
      }

      "$label - can tryOfferAll at capacity with take" {
        IO.fx {
          val q = !queue(1)
          val (join, _) = !q.take().fork()
          !IO.sleep(50.milliseconds)
          val succeed = !q.tryOfferAll(1, 2)
          val a = !q.take()
          val b = !join
          Tuple2(succeed, setOf(a, b))
        }.equalUnderTheLaw(IO.just(Tuple2(true, setOf(1, 2))))
      }

      // offerAll(fa).fork() + offerAll(fb).fork() <==> queue(fa + fb) OR queue(fb + fa)
      "$label - offerAll is atomic" {
        forAll(Gen.nonEmptyList(Gen.int()), Gen.nonEmptyList(Gen.int())) { fa, fb ->
          IO.fx {
            val q = !queue(fa.size + fb.size)
            !q.offerAll(fa.toList()).fork()
            !q.offerAll(fb.toList()).fork()

            !IO.sleep(50.milliseconds)

            val res = !q.takeAll()
            res == (fa.toList() + fb.toList()) || res == (fb.toList() + fa.toList())
          }.equalUnderTheLaw(IO.just(true))
        }
      }

      // To test outstanding offers, we need to `offer` more elements to the queue than we have capacity
      "$label - takeAll takes all values, including outstanding offers" {
        forAll(50,
          Gen.nonEmptyList(Gen.int()).filter { it.size in 51..100 },
          Gen.choose(1, 50)
        ) { l, capacity ->
          IO.fx {
            val q = !queue(capacity)
            !l.parTraverse(NonEmptyList.traverse(), q::offer).fork()
            !IO.sleep(50.milliseconds) // Give take callbacks a chance to register

            val res = !q.takeAll().map(Iterable<Int>::toSet)
            val after = !q.peekAll()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList().toSet(), emptyList())))
        }
      }

      // To test outstanding offers, we need to `offer` more elements to the queue than we have capacity
      "$label - peekAll reads all values, including outstanding offers" {
        forAll(50,
          Gen.nonEmptyList(Gen.int()).filter { it.size in 51..100 },
          Gen.choose(1, 50)
        ) { l, capacity ->
          IO.fx {
            val q = !queue(capacity)
            !l.parTraverse(NonEmptyList.traverse(), q::offer).fork()
            !IO.sleep(50.milliseconds) // Give take callbacks a chance to register

            val res = !q.peekAll().map(Iterable<Int>::toSet)
            val after = !q.peekAll().map(Iterable<Int>::toSet)
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList().toSet(), l.toList().toSet())))
        }
      }

      // Offer only gets scheduled for Bounded Queues, others apply strategy.
      "$label - offer is cancelable" {
        IO.fx {
          val q = !queue(1)
          !q.offer(0)
          !q.offer(1).fork()
          val p2 = !q.offer(2).fork()
          !q.offer(3).fork()

          !IO.sleep(50.milliseconds) // Give put callbacks a chance to register

          !p2.cancel()

          !q.take()
          val r1 = !q.take()
          val r3 = !q.take()

          setOf(r1, r3)
        }.equalUnderTheLaw(IO.just(setOf(1, 3)))
      }

      // OfferAll only gets scheduled for Bounded Queues, others apply strategy.
      "$label - offerAll is cancelable" {
        IO.fx {
          val q = !queue(1)
          !q.offer(0)
          !q.offer(1).fork()
          val p2 = !q.offerAll(2, 3).fork()
          !q.offer(4).fork()

          !IO.sleep(50.milliseconds) // Give put callbacks a chance to register

          !p2.cancel()

          !q.take()
          val r1 = !q.take()
          val r3 = !q.take()

          setOf(r1, r3)
        }.equalUnderTheLaw(IO.just(setOf(1, 4)))
      }

      "$label - tryOffer returns false at capacity" {
        IO.fx {
          val q = !queue(1)
          !q.offer(1)
          !q.tryOffer(2)
        }.equalUnderTheLaw(IO.just(false))
      }

      "$label - capacity must be a positive integer" {
        queue(0).attempt().suspended().fold(
          { err -> err.shouldBeInstanceOf<IllegalArgumentException>() },
          { fail("Expected Left<IllegalArgumentException>") }
        )
      }

      "$label - suspended offers called on a full queue complete when take calls made to queue" {
        forAll(Gen.tuple2(Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = !queue(1)
            !q.offer(t.a)
            val (join, _) = !q.offer(t.b).fork(ctx)
            val first = !q.take()
            val second = !q.take()
            !join // Check if fiber completed
            Tuple2(first, second)
          }.equalUnderTheLaw(IO.just(t))
        }
      }

      "$label - multiple offer calls on an full queue complete when as many take calls are made to queue" {
        forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = !queue(1)
            !q.offer(t.a)
            val (join, _) = !q.offer(t.b).fork(ctx)
            val (join2, _) = !q.offer(t.c).fork(ctx)
            val first = !q.take()
            val second = !q.take()
            val third = !q.take()
            !join // Check if fiber completed
            !join2 // Check if fiber completed
            setOf(first, second, third)
          }.equalUnderTheLaw(IO.just(setOf(t.a, t.b, t.c)))
        }
      }
    }

    fun slidingStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      val label = "SlidingQueue"
      allStrategyTests(label, ctx, queue)
      strategyAtCapacityTests(label, ctx, queue)

      "$label - capacity must be a positive integer" {
        queue(0).attempt().suspended().fold(
          { err -> err.shouldBeInstanceOf<IllegalArgumentException>() },
          { fail("Expected Left<IllegalArgumentException>") }
        )
      }

      "$label - slides elements offered to queue at capacity" {
        forAll(
          Gen.choose(1, 50),
          Gen.nonEmptyList(Gen.int()).filter { it.size > 50 }
        ) { capacity, xs ->
          IO.fx {
            val q = !queue(capacity)
            !q.offerAll(xs.toList())
            !q.peekAll()
          }.equalUnderTheLaw(IO.just(xs.toList().drop(xs.size - capacity)))
        }
      }
    }

    fun droppingStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      val label = "DroppingQueue"
      allStrategyTests(label, ctx, queue)
      strategyAtCapacityTests(label, ctx, queue)

      "$label - capacity must be a positive integer" {
        queue(0).attempt().suspended().fold(
          { err -> err.shouldBeInstanceOf<IllegalArgumentException>() },
          { fail("Expected Left<IllegalArgumentException>") }
        )
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
          }.equalUnderTheLaw(IO.just(xs.toList() + x2))
        }
      }

      "$label - drops elements offered to queue at capacity" {
        forAll(
          Gen.choose(1, 50),
          Gen.nonEmptyList(Gen.int()).filter { it.size > 50 }
        ) { capacity, xs ->
          IO.fx {
            val q = !queue(capacity)
            !q.offerAll(xs.toList())
            !q.peekAll()
          }.equalUnderTheLaw(IO.just(xs.toList().take(capacity)))
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
