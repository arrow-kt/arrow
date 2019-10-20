package arrow.fx

import arrow.core.extensions.list.traverse.traverse
import arrow.core.fix
import arrow.core.Tuple3
import arrow.core.Tuple2
import arrow.core.Left
import arrow.core.None
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.typeclasses.milliseconds
import arrow.test.UnitSpec
import arrow.test.generators.tuple2
import arrow.test.generators.tuple3
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class QueueTest : UnitSpec() {

  init {

    fun tests(
      label: String,
      ctx: CoroutineContext = Dispatchers.Default,
      queue: (Int) -> IO<Queue<ForIO, Int>>
    ) {

      "$label - make a queue the add values then retrieve in the same order" {
        forAll(Gen.list(Gen.int())) { l ->
          IO.fx {
            val q = !queue(l.size)
            !l.traverse(IO.applicative(), q::offer)
            val nl = !(1..l.size).toList().traverse(IO.applicative()) { q.take() }
            nl.fix()
          }.unsafeRunSync() == l
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
        val wontComplete = queue(10).flatMap(Queue<ForIO, Int>::take)
        val start = System.currentTimeMillis()
        val received = wontComplete.unsafeRunTimed(100.milliseconds)
        val elapsed = System.currentTimeMillis() - start

        received shouldBe None
        (elapsed >= 100) shouldBe true
      }

      "$label - time out offering to a queue at capacity" {
        val wontComplete = IO.fx {
          val q = !queue(1)
          !q.offer(1)
          !q.offer(2)
        }
        val start = System.currentTimeMillis()
        val received = wontComplete.unsafeRunTimed(100.milliseconds)
        val elapsed = System.currentTimeMillis() - start

        received shouldBe None
        (elapsed >= 100) shouldBe true
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

      "$label - offering to a shutdown queue creates a QueueShutdown error" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val q = !queue(10)
            !q.shutdown()
            !q.offer(i)
          }.attempt().unsafeRunSync() == Left(QueueShutdown)
        }
      }

      "$label - joining a forked, incompleted take call on a shutdown queue creates a  QueueShutdown error" {
        IO.fx {
          val q = !queue(10)
          val t = !q.take().fork(ctx)
          !q.shutdown()
          !t.join()
        }.attempt().unsafeRunSync() shouldBe Left(QueueShutdown)
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
          !map(p1.get(), p2.get()) { (p1, p2) -> p1 && p2 }
        }.unsafeRunSync()
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
    }

    tests("BoundedQueue", queue =
    { capacity -> Queue.bounded<ForIO, Int>(capacity, IO.concurrent()).fix() })
  }
}
