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
            val q = queue(l.size).invoke()
            l.traverse(IO.applicative(), q::offer).invoke()
            (1..l.size).toList().traverse(IO.applicative()) { q.take() }.invoke()
          }.equalUnderTheLaw(IO.just(l.toList()))
        }
      }

      "$label - queue can be filled at once with enough capacity" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size)()
            val succeed = q.tryOfferAll(l.toList())()
            val res = q.takeAll()()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(true, l.toList())))
        }
      }

      "$label - queue can be filled at once over capacity with takers" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size)()
            val (join, _) = q.take().fork()()
            IO.sleep(50.milliseconds)() // Registered first, should receive first element of `tryOfferAll`

            val succeed = q.tryOfferAll(listOf(500) + l.toList())()
            val res = q.takeAll()()
            val head = join()
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
            val q = queue(capacity).invoke()
            val succeed = q.tryOfferAll(l).invoke()
            val all = q.takeAll().invoke()
            Tuple2(succeed, all)
          }.equalUnderTheLaw(IO.just(Tuple2(true, l)))
        }
      }

      "$label - takeAll takes all values from a Queue" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).invoke()
            l.traverse(IO.applicative(), q::offer).invoke()
            val res = q.takeAll().invoke()
            val after = q.takeAll().invoke()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList(), emptyList())))
        }
      }

      "$label - peekAll reads all values from a Queue without removing them" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).invoke()
            l.traverse(IO.applicative(), q::offer).invoke()
            val res = q.peekAll().invoke()
            val after = q.peekAll().invoke()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList(), l.toList())))
        }
      }

      "$label - empty queue takeAll is empty" {
        forAll(Gen.positiveIntegers()) { capacity ->
          IO.fx {
            val q = queue(capacity).invoke()
            q.takeAll().invoke()
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - empty queue peekAll is empty" {
        forAll(Gen.positiveIntegers()) { capacity ->
          IO.fx {
            val q = queue(capacity).invoke()
            q.peekAll().invoke()
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - time out taking from an empty queue" {
        forAll(Gen.int()) {
          IO.fx {
            val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::take)
            val received = wontComplete.map { Some(it) }
              .waitFor(100.milliseconds, default = just(None)).invoke()
            received shouldBe None
          }.equalUnderTheLaw(IO.unit)
        }
      }

      "$label - suspended take calls on an empty queue complete when offer calls made to queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = queue(3).invoke()
            val first = q.take().fork(ctx).invoke()
            q.offer(i).invoke()
            first.join().invoke()
          }.equalUnderTheLaw(IO.just(i))
        }
      }

      "$label - multiple take calls on an empty queue complete when until as many offer calls made to queue" {
        forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = queue(3).invoke()
            val first = q.take().fork(ctx).invoke()
            val second = q.take().fork(ctx).invoke()
            val third = q.take().fork(ctx).invoke()
            q.offer(t.a).invoke()
            q.offer(t.b).invoke()
            q.offer(t.c).invoke()
            val firstValue = first.join().invoke()
            val secondValue = second.join().invoke()
            val thirdValue = third.join().invoke()
            setOf(firstValue, secondValue, thirdValue)
          }.equalUnderTheLaw(IO.just(setOf(t.a, t.b, t.c)))
        }
      }

      "$label - time out peeking from an empty queue" {
        forAll(Gen.int()) {
          IO.fx {
            val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::peek)
            val received = wontComplete.map { Some(it) }
              .waitFor(100.milliseconds, default = just(None)).invoke()
            received shouldBe None
          }.equalUnderTheLaw(IO.unit)
        }
      }

      "$label - suspended peek calls on an empty queue complete when offer calls made to queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = queue(3).invoke()
            val first = q.peek().fork(ctx).invoke()
            q.offer(i).invoke()
            first.join().invoke()
          }.equalUnderTheLaw(IO.just(i))
        }
      }

      "$label - multiple peek calls offerAll is cancelable an empty queue all complete with the first value is received" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = queue(1).invoke()
            val first = q.peek().fork(ctx).invoke()
            val second = q.peek().fork(ctx).invoke()
            val third = q.peek().fork(ctx).invoke()
            q.offer(i).invoke()
            val firstValue = first.join().invoke()
            val secondValue = second.join().invoke()
            val thirdValue = third.join().invoke()
            setOf(firstValue, secondValue, thirdValue)
          }.equalUnderTheLaw(IO.just(setOf(i, i, i)))
        }
      }

      "$label - peek does not remove value from Queue" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = queue(10).invoke()
            q.offer(i).invoke()
            val peeked = q.peek().invoke()
            val took = q.takeAll().invoke()
            Tuple2(peeked, took)
          }.equalUnderTheLaw(IO.just(Tuple2(i, listOf(i))))
        }
      }

      "$label - tryTake on an empty Queue returns None" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(10).invoke()
            q.tryTake().invoke()
          }.equalUnderTheLaw(IO.just(None))
        }
      }

      "$label - tryPeek on an empty Queue returns None" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(10).invoke()
            q.tryPeek().invoke()
          }.equalUnderTheLaw(IO.just(None))
        }
      }

      "$label - take is cancelable" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            val t1 = q.take().fork().invoke()
            val t2 = q.take().fork().invoke()
            val t3 = q.take().fork().invoke()
            IO.sleep(50.milliseconds).invoke() // Give take callbacks a chance to register
            t2.cancel().invoke()
            q.offer(1).invoke()
            q.offer(3).invoke()
            val r1 = t1.join().invoke()
            val r3 = t3.join().invoke()
            val size = q.size().invoke()
            Tuple2(setOf(r1, r3), size)
          }.equalUnderTheLaw(IO.just(Tuple2(setOf(1, 3), 0)))
        }
      }

      "$label - peek is cancelable" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            val finished = Promise<Int>().invoke()
            val fiber = q.peek().flatMap(finished::complete).fork().invoke()
            sleep(50.milliseconds).invoke() // Give read callback a chance to register
            fiber.cancel().invoke()
            q.offer(10).invoke()
            val fallback = sleep(50.milliseconds).followedBy(IO.just(0))
            IO.raceN(finished.get(), fallback).invoke()
          }.equalUnderTheLaw(IO.just(Right(0)))
        }
      }

      "$label - takeAll returns emptyList with waiting suspended takers" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            val (_, cancel) = q.take().fork().invoke()
            sleep(50.milliseconds).invoke()
            val res = q.takeAll().invoke()
            cancel.invoke()
            res
          }.equalUnderTheLaw(IO.just(emptyList()))
        }
      }

      "$label - peekAll returns emptyList with waiting suspended takers" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            val (_, cancel) = q.take().fork().invoke()
            sleep(50.milliseconds).invoke()
            val res = q.peekAll().invoke()
            cancel.invoke()
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
            val q = queue(capacity).invoke()
            val (_, cancel) = q.take().fork().invoke()
            IO.sleep(50.milliseconds).invoke() // Give take callbacks a chance to register

            q.offerAll(l.toList()).invoke()
            cancel.invoke()
            q.peekAll().invoke()
          }.equalUnderTheLaw(IO.just(l.toList().drop(1)))
        }
      }

      "$label - offerAll can offer empty" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            q.offer(1).invoke()
            q.offerAll(emptyList()).invoke()
            q.peekAll().invoke()
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
            val q = queue(1).invoke()
            q.offer(1).invoke()
            q.tryOffer(2).invoke()
          }.equalUnderTheLaw(IO.just(false))
        }
      }

      "$label - tryOfferAll over capacity" {
        forAll(Gen.list(Gen.int()).filter { it.size > 1 }) { l ->
          IO.fx {
            val q = queue(1).invoke()
            val succeed = q.tryOfferAll(l).invoke()
            val res = q.peekAll().invoke()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(false, emptyList())))
        }
      }

      "$label - can take and offer at capacity".config(enabled = false) {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            val (join, _) = q.take().fork().invoke()
            val succeed = q.tryOfferAll(1, 2).invoke()
            val a = q.take().invoke()
            val b = join.invoke()
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
            val q = queue(1).invoke()
            q.offer(1).invoke()
            val wontComplete = q.offer(2)
            val received = wontComplete.map { Some(it) }
              .waitFor(100.milliseconds, default = just(None)).invoke()
            received shouldBe None
          }.equalUnderTheLaw(IO.unit)
        }
      }

      "$label - time out offering multiple values to a queue at capacity" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(3).invoke()
            val wontComplete = q.offerAll(1, 2, 3, 4)
            val received = wontComplete.map { Some(it) }
              .waitFor(100.milliseconds, default = just(None)).invoke()
            received shouldBe None
          }.equalUnderTheLaw(IO.unit)
        }
      }

      "$label - queue cannot be filled at once without enough capacity" {
        forAll(Gen.nonEmptyList(Gen.int())) { l ->
          IO.fx {
            val q = queue(l.size).invoke()
            val succeed = q.tryOfferAll(l.toList() + 1).invoke()
            val res = q.takeAll().invoke()
            Tuple2(succeed, res)
          }.equalUnderTheLaw(IO.just(Tuple2(false, emptyList())))
        }
      }

      "$label - can offerAll at capacity with take" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            val (join, _) = q.take().fork().invoke()
            IO.sleep(50.milliseconds).invoke()
            q.offerAll(1, 2).invoke()
            val a = q.take().invoke()
            val b = join.invoke()
            setOf(a, b)
          }.equalUnderTheLaw(IO.just(setOf(1, 2)))
        }
      }

      "$label - can tryOfferAll at capacity with take" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            val (join, _) = q.take().fork().invoke()
            IO.sleep(50.milliseconds).invoke()
            val succeed = q.tryOfferAll(1, 2).invoke()
            val a = q.take().invoke()
            val b = join.invoke()
            Tuple2(succeed, setOf(a, b))
          }.equalUnderTheLaw(IO.just(Tuple2(true, setOf(1, 2))))
        }
      }

      // offerAll(fa).fork() + offerAll(fb).fork() <==> queue(fa + fb) OR queue(fb + fa)
      "$label - offerAll is atomic" {
        forAll(Gen.nonEmptyList(Gen.int()), Gen.nonEmptyList(Gen.int())) { fa, fb ->
          IO.fx {
            val q = queue(fa.size + fb.size).invoke()
            q.offerAll(fa.toList()).fork().invoke()
            q.offerAll(fb.toList()).fork().invoke()

            IO.sleep(50.milliseconds).invoke()

            val res = q.takeAll().invoke()
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
            val q = queue(capacity).invoke()
            l.parTraverse(NonEmptyList.traverse(), q::offer).fork().invoke()
            IO.sleep(50.milliseconds).invoke() // Give take callbacks a chance to register

            val res = q.takeAll().map(Iterable<Int>::toSet).invoke()
            val after = q.peekAll().invoke()
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
            val q = queue(capacity).invoke()
            l.parTraverse(NonEmptyList.traverse(), q::offer).fork().invoke()
            IO.sleep(50.milliseconds).invoke() // Give take callbacks a chance to register

            val res = q.peekAll().map(Iterable<Int>::toSet).invoke()
            val after = q.peekAll().map(Iterable<Int>::toSet).invoke()
            Tuple2(res, after)
          }.equalUnderTheLaw(IO.just(Tuple2(l.toList().toSet(), l.toList().toSet())))
        }
      }

      // Offer only gets scheduled for Bounded Queues, others apply strategy.
      "$label - offer is cancelable" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            q.offer(0).invoke()
            q.offer(1).fork().invoke()
            val p2 = q.offer(2).fork().invoke()
            q.offer(3).fork().invoke()

            IO.sleep(50.milliseconds).invoke() // Give put callbacks a chance to register

            p2.cancel().invoke()

            q.take().invoke()
            val r1 = q.take().invoke()
            val r3 = q.take().invoke()

            setOf(r1, r3)
          }.equalUnderTheLaw(IO.just(setOf(1, 3)))
        }
      }

      // OfferAll only gets scheduled for Bounded Queues, others apply strategy.
      "$label - offerAll is cancelable" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            q.offer(0).invoke()
            q.offer(1).fork().invoke()
            val p2 = q.offerAll(2, 3).fork().invoke()
            q.offer(4).fork().invoke()

            IO.sleep(50.milliseconds).invoke() // Give put callbacks a chance to register

            p2.cancel().invoke()

            q.take().invoke()
            val r1 = q.take().invoke()
            val r3 = q.take().invoke()

            setOf(r1, r3)
          }.equalUnderTheLaw(IO.just(setOf(1, 4)))
        }
      }

      "$label - tryOffer returns false at capacity" {
        forAll(Gen.int()) {
          IO.fx {
            val q = queue(1).invoke()
            q.offer(1).invoke()
            q.tryOffer(2).invoke()
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
            val q = queue(1).invoke()
            q.offer(t.a).invoke()
            val (join, _) = q.offer(t.b).fork(ctx).invoke()
            val first = q.take().invoke()
            val second = q.take().invoke()
            join.invoke() // Check if fiber completed
            Tuple2(first, second)
          }.equalUnderTheLaw(IO.just(t))
        }
      }

      "$label - multiple offer calls on an full queue complete when as many take calls are made to queue" {
        forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          IO.fx {
            val q = queue(1).invoke()
            q.offer(t.a).invoke()
            val (join, _) = q.offer(t.b).fork(ctx).invoke()
            val (join2, _) = q.offer(t.c).fork(ctx).invoke()
            val first = q.take().invoke()
            val second = q.take().invoke()
            val third = q.take().invoke()
            join.invoke() // Check if fiber completed
            join2.invoke() // Check if fiber completed
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
            val q = queue(capacity).invoke()
            q.offerAll(xs.toList()).invoke()
            q.peekAll().invoke()
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
            val q = queue(xs.size).invoke()
            xs.traverse(IO.applicative(), q::offer).invoke()
            q.offer(x).invoke() // this `x` should be dropped
            val taken = (1..xs.size).toList().traverse(IO.applicative()) { q.take() }.invoke()
            q.offer(x2).invoke()
            val taken2 = q.take().invoke()
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
            val q = queue(capacity).invoke()
            q.offerAll(xs.toList()).invoke()
            q.peekAll().invoke()
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
