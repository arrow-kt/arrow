package arrow.fx

import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.extensions.list.traverse.traverse
import arrow.core.extensions.nonemptylist.traverse.traverse
import arrow.core.fix
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.tuple2
import arrow.core.test.generators.tuple3
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.test.laws.equalUnderTheLaw
import arrow.fx.typeclasses.milliseconds
import io.kotlintest.fail
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlin.coroutines.CoroutineContext

class QueueTest : ArrowFxSpec(iterations = 100) {

  init {

    fun allStrategyTests(
      label: String,
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {

      "$label - make a queue the add values then retrieve in the same order" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).bind()
            l.traverse(IO.applicative(), q::offer).bind()
            (1..l.size).toList().traverse(IO.applicative()) { q.take() }.bind()
          }.equalUnderTheLaw(IO.just(l.toList()))
        }
      }

      "$label - queue can be filled at once with enough capacity" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).bind()
            val succeed = q.tryOfferAll(l.toList()).bind()
            val res = q.takeAll().bind()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(true, l.toList())))
        }
      }

      "$label - queue can be filled at once over capacity with takers" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).bind()
            val (join, _) = q.take().fork().bind()
            IO.sleep(50.milliseconds).bind() // Registered first, should receive first element of `tryOfferAll`

            val succeed = q.tryOfferAll(listOf(500) + l.toList()).bind()
            val res = q.takeAll().bind()
            val head = join.bind()
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
            val q = queue(capacity).bind()
            val succeed = q.tryOfferAll(l).bind()
            val all = q.takeAll().bind()
            Tuple2(succeed, all)
          }.equalUnderTheLaw(IO.just(Tuple2(true, l)))
        }
      }

      "$label - takeAll takes all values from a Queue" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).bind()
            l.traverse(IO.applicative(), q::offer).bind()
            val res = q.takeAll().bind()
            val after = q.takeAll().bind()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList(), emptyList())))
        }
      }

      "$label - peekAll reads all values from a Queue without removing them" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).bind()
            l.traverse(IO.applicative(), q::offer).bind()
            val res = q.peekAll().bind()
            val after = q.peekAll().bind()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList(), l.toList())))
        }
      }

      "$label - empty queue takeAll is empty" {
        forAll(Gen.positiveIntegers()) { capacity ->
          IO.fx {
            val q = queue(capacity).bind()
            q.takeAll().bind()
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - empty queue peekAll is empty" {
        forAll(Gen.positiveIntegers()) { capacity ->
          IO.fx {
            val q = queue(capacity).bind()
            q.peekAll().bind()
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - time out taking from an empty queue" {
        forAll(Gen.int()) {
          IO.fx {
            val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::take)
            val received = wontComplete.map { Some(it) }
              .waitFor(100.milliseconds, default = just(None)).bind()
            received shouldBe None
          }.equalUnderTheLaw(IO.unit)
        }
      }

      "$label - suspended take calls on an empty queue complete when offer calls made to queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = queue(3).bind()
            val first = q.take().fork(ctx).bind()
            q.offer(i).bind()
            first.join().bind()
          }.equalUnderTheLaw(IO.just(i))
        }
      }

      "$label - multiple take calls on an empty queue complete when until as many offer calls made to queue" {
        forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = queue(3).bind()
            val first = q.take().fork(ctx).bind()
            val second = q.take().fork(ctx).bind()
            val third = q.take().fork(ctx).bind()
            q.offer(t.a).bind()
            q.offer(t.b).bind()
            q.offer(t.c).bind()
            val firstValue = first.join().bind()
            val secondValue = second.join().bind()
            val thirdValue = third.join().bind()
            setOf(firstValue, secondValue, thirdValue)
          }.equalUnderTheLaw(IO.just(setOf(t.a, t.b, t.c)))
        }
      }

      "$label - time out peeking from an empty queue" {
        forAll(Gen.int()) {
          IO.fx {
            val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::peek)
            val received = wontComplete.map { Some(it) }
              .waitFor(100.milliseconds, default = just(None)).bind()
            received shouldBe None
          }.equalUnderTheLaw(IO.unit)
        }
      }

      "$label - suspended peek calls on an empty queue complete when offer calls made to queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = queue(3).bind()
            val first = q.peek().fork(ctx).bind()
            q.offer(i).bind()
            first.join().bind()
          }.equalUnderTheLaw(IO.just(i))
        }
      }

      "$label - multiple peek calls offerAll is cancelable an empty queue all complete with the first value is received" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = queue(1).bind()
            val first = q.peek().fork(ctx).bind()
            val second = q.peek().fork(ctx).bind()
            val third = q.peek().fork(ctx).bind()
            q.offer(i).bind()
            val firstValue = first.join().bind()
            val secondValue = second.join().bind()
            val thirdValue = third.join().bind()
            setOf(firstValue, secondValue, thirdValue)
          }.equalUnderTheLaw(IO.just(setOf(i, i, i)))
        }
      }

      "$label - peek does not remove value from Queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = queue(10).bind()
            q.offer(i).bind()
            val peeked = q.peek().bind()
            val took = q.takeAll().bind()
            Tuple2(peeked, took)
          }.equalUnderTheLaw(IO.just(Tuple2(i, listOf(i))))
        }
      }

      "$label - tryTake on an empty Queue returns None" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(10).bind()
            q.tryTake().bind()
          }.equalUnderTheLaw(IO.just(None))
        }
      }

      "$label - tryPeek on an empty Queue returns None" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(10).bind()
            q.tryPeek().bind()
          }.equalUnderTheLaw(IO.just(None))
        }
      }

      "$label - take is cancelable" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            val t1 = q.take().fork().bind()
            val t2 = q.take().fork().bind()
            val t3 = q.take().fork().bind()
            IO.sleep(50.milliseconds).bind() // Give take callbacks a chance to register
            t2.cancel().bind()
            q.offer(1).bind()
            q.offer(3).bind()
            val r1 = t1.join().bind()
            val r3 = t3.join().bind()
            val size = q.size().bind()
            Tuple2(setOf(r1, r3), size)
          }.equalUnderTheLaw(IO.just(Tuple2(setOf(1, 3), 0)))
        }
      }

      "$label - peek is cancelable" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            val finished = Promise<Int>().bind()
            val fiber = q.peek().flatMap(finished::complete).fork().bind()
            sleep(50.milliseconds).bind() // Give read callback a chance to register
            fiber.cancel().bind()
            q.offer(10).bind()
            val fallback = sleep(50.milliseconds).followedBy(IO.just(0))
            IO.raceN(finished.get(), fallback).bind()
          }.equalUnderTheLaw(IO.just(Right(0)))
        }
      }

      "$label - takeAll returns emptyList with waiting suspended takers" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            val (_, cancel) = q.take().fork().bind()
            sleep(50.milliseconds).bind()
            val res = q.takeAll().bind()
            cancel.bind()
            res
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - peekAll returns emptyList with waiting suspended takers" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            val (_, cancel) = q.take().fork().bind()
            sleep(50.milliseconds).bind()
            val res = q.peekAll().bind()
            cancel.bind()
            res
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - offerAll offers all values with waiting suspended takers, and within capacity".config(enabled = false) {
        forAll(
          Gen.nonEmptyList(Gen.int()).filter { it.size in 1..50 },
          Gen.choose(52, 100)
        ) { l, capacity ->
          IO.fx {
            val q = queue(capacity).bind()
            val (_, cancel) = q.take().fork().bind()
            IO.sleep(50.milliseconds).bind() // Give take callbacks a chance to register

            q.offerAll(l.toList()).bind()
            cancel.bind()
            q.peekAll().bind()
          }.equalUnderTheLaw(IO.just(l.toList().drop(1)))
        }
      }

      "$label - offerAll can offer empty" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            q.offer(1).bind()
            q.offerAll(emptyList()).bind()
            q.peekAll().bind()
          }.equalUnderTheLaw(IO.just(listOf(1)))
        }
      }
    }

    fun strategyAtCapacityTests(
      label: String,
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      "$label - tryOffer returns false over capacity" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            q.offer(1).bind()
            q.tryOffer(2).bind()
          }.equalUnderTheLaw(IO.just(false))
        }
      }

      "$label - tryOfferAll over capacity" {
        forAll(Gen.list(Gen.int()).filter { it.size > 1 }) { l ->
          IO.fx {
            val q = queue(1).bind()
            val succeed = q.tryOfferAll(l).bind()
            val res = q.peekAll().bind()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(false, emptyList())))
        }
      }

      "$label - can take and offer at capacity".config(enabled = false) {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            val (join, _) = q.take().fork().bind()
            val succeed = q.tryOfferAll(1, 2).bind()
            val a = q.take().bind()
            val b = join.bind()
            Tuple2(succeed, setOf(a, b))
          }.equalUnderTheLaw(IO.just(Tuple2(true, setOf(1, 2))))
        }
      }
    }

    fun boundedStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      val label = "BoundedQueue"
      strategyAtCapacityTests(label, queue)

      "$label - time out offering to a queue at capacity" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            q.offer(1).bind()
            val wontComplete = q.offer(2)
            val received = wontComplete.map { Some(it) }
              .waitFor(100.milliseconds, default = just(None)).bind()
            received shouldBe None
          }.equalUnderTheLaw(IO.unit)
        }
      }

      "$label - time out offering multiple values to a queue at capacity" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(3).bind()
            val wontComplete = q.offerAll(1, 2, 3, 4)
            val received = wontComplete.map { Some(it) }
              .waitFor(100.milliseconds, default = just(None)).bind()
            received shouldBe None
          }.equalUnderTheLaw(IO.unit)
        }
      }

      "$label - queue cannot be filled at once without enough capacity" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).bind()
            val succeed = q.tryOfferAll(l.toList() + 1).bind()
            val res = q.takeAll().bind()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(false, emptyList())))
        }
      }

      "$label - can offerAll at capacity with take" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            val (join, _) = q.take().fork().bind()
            IO.sleep(50.milliseconds).bind()
            q.offerAll(1, 2).bind()
            val a = q.take().bind()
            val b = join.bind()
            setOf(a, b)
          }.equalUnderTheLaw(IO.just(setOf(1, 2)))
        }
      }

      "$label - can tryOfferAll at capacity with take" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            val (join, _) = q.take().fork().bind()
            IO.sleep(50.milliseconds).bind()
            val succeed = q.tryOfferAll(1, 2).bind()
            val a = q.take().bind()
            val b = join.bind()
            Tuple2(succeed, setOf(a, b))
          }.equalUnderTheLaw(IO.just(Tuple2(true, setOf(1, 2))))
        }
      }

      // offerAll(fa).fork() + offerAll(fb).fork() <==> queue(fa + fb) OR queue(fb + fa)
      "$label - offerAll is atomic" {
        forAll(Gen.nonEmptyList(Gen.int()), Gen.nonEmptyList(Gen.int())) { fa, fb ->
          IO.fx {
            val q = queue(fa.size + fb.size).bind()
            q.offerAll(fa.toList()).fork().bind()
            q.offerAll(fb.toList()).fork().bind()

            IO.sleep(50.milliseconds).bind()

            val res = q.takeAll().bind()
            res == (fa.toList() + fb.toList()) || res == (fb.toList() + fa.toList())
          }.equalUnderTheLaw(IO.just(true))
        }
      }

      // To test outstanding offers, we need to `offer` more elements to the queue than we have capacity
      "$label - takeAll takes all values, including outstanding offers".config(enabled = false) {
        forAll(50,
          Gen.nonEmptyList(Gen.int()).filter { it.size in 51..100 },
          Gen.choose(1, 50)
        ) { l, capacity ->
          IO.fx {
            val q = queue(capacity).bind()
            l.parTraverse(NonEmptyList.traverse(), q::offer).fork().bind()
            IO.sleep(50.milliseconds).bind() // Give take callbacks a chance to register

            val res = q.takeAll().map(Iterable<Int>::toSet).bind()
            val after = q.peekAll().bind()
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
            val q = queue(capacity).bind()
            l.parTraverse(NonEmptyList.traverse(), q::offer).fork().bind()
            IO.sleep(50.milliseconds).bind() // Give take callbacks a chance to register

            val res = q.peekAll().map(Iterable<Int>::toSet).bind()
            val after = q.peekAll().map(Iterable<Int>::toSet).bind()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList().toSet(), l.toList().toSet())))
        }
      }

      // Offer only gets scheduled for Bounded Queues, others apply strategy.
      "$label - offer is cancelable" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            q.offer(0).bind()
            q.offer(1).fork().bind()
            val p2 = q.offer(2).fork().bind()
            q.offer(3).fork().bind()

            IO.sleep(50.milliseconds).bind() // Give put callbacks a chance to register

            p2.cancel().bind()

            q.take().bind()
            val r1 = q.take().bind()
            val r3 = q.take().bind()

            setOf(r1, r3)
          }.equalUnderTheLaw(IO.just(setOf(1, 3)))
        }
      }

      // OfferAll only gets scheduled for Bounded Queues, others apply strategy.
      "$label - offerAll is cancelable" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            q.offer(0).bind()
            q.offer(1).fork().bind()
            val p2 = q.offerAll(2, 3).fork().bind()
            q.offer(4).fork().bind()

            IO.sleep(50.milliseconds).bind() // Give put callbacks a chance to register

            p2.cancel().bind()

            q.take().bind()
            val r1 = q.take().bind()
            val r3 = q.take().bind()

            setOf(r1, r3)
          }.equalUnderTheLaw(IO.just(setOf(1, 4)))
        }
      }

      "$label - tryOffer returns false at capacity" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).bind()
            q.offer(1).bind()
            q.tryOffer(2).bind()
          }.equalUnderTheLaw(IO.just(false))
        }
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
            val q = queue(1).bind()
            q.offer(t.a).bind()
            val (join, _) = q.offer(t.b).fork(ctx).bind()
            val first = q.take().bind()
            val second = q.take().bind()
            join.bind() // Check if fiber completed
            Tuple2(first, second)
          }.equalUnderTheLaw(IO.just(t))
        }
      }

      "$label - multiple offer calls on an full queue complete when as many take calls are made to queue" {
        forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = queue(1).bind()
            q.offer(t.a).bind()
            val (join, _) = q.offer(t.b).fork(ctx).bind()
            val (join2, _) = q.offer(t.c).fork(ctx).bind()
            val first = q.take().bind()
            val second = q.take().bind()
            val third = q.take().bind()
            join.bind() // Check if fiber completed
            join2.bind() // Check if fiber completed
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
      strategyAtCapacityTests(label, queue)

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
            val q = queue(capacity).bind()
            q.offerAll(xs.toList()).bind()
            q.peekAll().bind()
          }.equalUnderTheLaw(IO.just(xs.toList().drop(xs.size - capacity)))
        }
      }
    }

    fun droppingStrategyTests(
      ctx: CoroutineContext = IO.dispatchers().default(),
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {
      val label = "DroppingQueue"
      strategyAtCapacityTests(label, queue)

      "$label - capacity must be a positive integer" {
        queue(0).attempt().suspended().fold(
          { err -> err.shouldBeInstanceOf<IllegalArgumentException>() },
          { fail("Expected Left<IllegalArgumentException>") }
        )
      }

      "$label - drops elements offered to a queue at capacity" {
        forAll(Gen.int(), Gen.int(), Gen.nonEmptyList(Gen.int())) { x, x2, xs ->
          IO.fx {
            val q = queue(xs.size).bind()
            xs.traverse(IO.applicative(), q::offer).bind()
            q.offer(x).bind() // this `x` should be dropped
            val taken = (1..xs.size).toList().traverse(IO.applicative()) { q.take() }.bind()
            q.offer(x2).bind()
            val taken2 = q.take().bind()
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
            val q = queue(capacity).bind()
            q.offerAll(xs.toList()).bind()
            q.peekAll().bind()
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
