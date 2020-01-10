package arrow.fx

import arrow.Kind
import arrow.core.None
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Left
import arrow.core.extensions.list.traverse.traverse
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeKOf
import arrow.fx.rx2.extensions.concurrent
import arrow.fx.rx2.extensions.singlek.applicativeError.attempt
import arrow.fx.rx2.value
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.milliseconds
import arrow.test.UnitSpec
import arrow.test.generators.nonEmptyList
import arrow.test.generators.tuple2
import arrow.test.generators.tuple3
import arrow.test.laws.equalUnderTheLaw
import arrow.test.laws.forFew
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlin.coroutines.CoroutineContext

class QueueTest : UnitSpec() {

  init {

    fun <F> Concurrent<F>.tests(
      label: String,
      ctx: CoroutineContext = IO.dispatchers<Nothing>().default(),
      queue: Concurrent<F>.(Int) -> Kind<F, Queue<F, Int>>,
      EQ: Eq<Kind<F, Unit>>
    ) {

      fun Kind<F, Unit>.test(): Boolean =
        equalUnderTheLaw(unit(), EQ)

      "$label - make a queue the add values then retrieve in the same order" {
        forFew(3, Gen.nonEmptyList(Gen.int(), 50)) { l ->
          fx.concurrent {
            !effect { }
            val q = !queue(l.size)
            !l.traverse(this@tests, q::offer)
            val nl = !(1..l.size).toList().traverse(this@tests) { q.take() }
            !effect { nl shouldBe l }
          }.test()
        }
      }

      "$label - offer and take a number of values in the same order" {
        forFew(3, Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          fx.concurrent {
            val q = !queue(3)
            !q.offer(t.a)
            !q.offer(t.b)
            !q.offer(t.c)
            val first = !q.take()
            val second = !q.take()
            val third = !q.take()
            !effect { Tuple3(first, second, third) shouldBe t }
          }.test()
        }
      }

      "$label - time out taking from an empty queue" {
        fx.concurrent {
          val wontComplete = queue(10).flatMap(Queue<F, Int>::take)
          val start = !effect { System.currentTimeMillis() }
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          !effect { received shouldBe None }
          !effect { (elapsed >= 100) shouldBe true }
        }.test()
      }

      "$label - time out offering to a queue at capacity" {
        fx.concurrent {
          val q = !queue(1)
          !q.offer(1)
          val start = !effect { System.currentTimeMillis() }
          val wontComplete = q.offer(2)
          val received = !wontComplete.map { Some(it) }
            .waitFor(100.milliseconds, default = just(None))
          val elapsed = !effect { System.currentTimeMillis() - start }
          !effect { received shouldBe None }
          !effect { (elapsed >= 100) shouldBe true }
        }.test()
      }

      "$label - suspended take calls on an empty queue complete when offer calls made to queue" {
        forFew(3, Gen.int()) { i ->
          fx.concurrent {
            val q = !queue(3)
            val first = !q.take().fork(ctx)
            !q.offer(i)
            val res = !first.join()
            !effect { res shouldBe i }
          }.test()
        }
      }

      "$label - multiple take calls on an empty queue complete when until as many offer calls made to queue" {
        forFew(3, Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          fx.concurrent {
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
            !effect { setOf(firstValue, secondValue, thirdValue) shouldBe setOf(t.a, t.b, t.c) }
          }.test()
        }
      }

      "$label - suspended offers called on an full queue complete when take calls made to queue" {
        forFew(3, Gen.tuple2(Gen.int(), Gen.int())) { t ->
          fx.concurrent {
            val q = !queue(1)
            !q.offer(t.a)
            !q.offer(t.b).fork(ctx)
            val first = !q.take()
            val second = !q.take()
            !effect { Tuple2(first, second) shouldBe t }
          }.test()
        }
      }

      "$label - multiple offer calls on an full queue complete when as many take calls are made to queue" {
        forFew(3, Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
          fx.concurrent {
            val q = !queue(1)
            !q.offer(t.a)
            !q.offer(t.b).fork(ctx)
            !q.offer(t.c).fork(ctx)
            val first = !q.take()
            val second = !q.take()
            val third = !q.take()
            !effect { setOf(first, second, third) shouldBe setOf(t.a, t.b, t.c) }
          }.test()
        }
      }

      "$label - taking from a shutdown queue creates a QueueShutdown error" {
        forAll(Gen.int()) { i ->
          fx.concurrent {
            val res = !fx.concurrent {
              val q = !queue(10)
              !q.offer(i)
              !q.shutdown()
              !q.take()
            }.attempt()

            !effect { res shouldBe Left(QueueShutdown) }
          }.test()
        }
      }

      "$label - offering to a shutdown queue creates a QueueShutdown error" {
        forAll(Gen.int()) { i ->
          fx.concurrent {
            val res = !fx.concurrent {
              val q = !queue(10)
              !q.shutdown()
              !q.offer(i)
            }.attempt()

            !effect { res shouldBe Left(QueueShutdown) }
          }.test()
        }
      }

      "$label - joining a forked, incompleted take call on a shutdown queue creates a  QueueShutdown error" {
        fx.concurrent {
          val res = !fx.concurrent {
            val q = !queue(10)
            val t = !q.take().fork(ctx)
            !q.shutdown()
            !t.join()
          }.attempt()
          !effect { res shouldBe Left(QueueShutdown) }
        }.test()
      }

      "$label - joining a forked offer call made to a shut down queue creates a QueueShutdown error" {
        forFew(3, Gen.int()) { i ->
          fx.concurrent {
            val res = !fx.concurrent {
              val q = !queue(1)
              !q.offer(i)
              val o = !q.offer(i).fork(ctx)
              !q.shutdown()
              !o.join()
            }.attempt()

            !effect { res shouldBe Left(QueueShutdown) }
          }.test()
        }
      }

      "$label - create a shutdown hook completing a promise, then shutdown the queue, the promise should be completed" {
        fx.concurrent {
          val q = !queue(10)
          val p = !Promise<F, Boolean>(this@tests)
          !(q.awaitShutdown().followedBy(p.complete(true))).fork()
          !q.shutdown()
          !effect { p.get() shouldBe true }
        }.test()
      }

      "$label - create a shutdown hook completing a promise twice, then shutdown the queue, both promises should be completed" {
        fx.concurrent {
          val q = !queue(10)
          val p1 = !Promise<F, Boolean>(this@tests)
          val p2 = !Promise<F, Boolean>(this@tests)
          !(q.awaitShutdown().followedBy(p1.complete(true))).fork()
          !(q.awaitShutdown().followedBy(p2.complete(true))).fork()
          !q.shutdown()
          val res = !map(p1.get(), p2.get()) { (p1, p2) -> p1 && p2 }
          !effect { res shouldBe true }
        }.test()
      }

      "$label - shut it down, create a shutdown hook completing a promise, the promise should be completed immediately" {
        fx.concurrent {
          val q = !queue(10)
          !q.shutdown()
          val p = !Promise<F, Boolean>(this@tests)
          !(q.awaitShutdown().followedBy(p.complete(true))).fork()
          !effect { p.get() shouldBe true }
        }.test()
      }
    }

    MaybeK.concurrent().tests(
      label = "MaybeK: BoundedQueue",
      queue = { capacity -> Queue.bounded(capacity, MaybeK.concurrent()) },
      EQ = MaybeK.eq()
    )
  }
}

private fun <T> MaybeK.Companion.eq(): Eq<MaybeKOf<T>> = object : Eq<MaybeKOf<T>> {
  override fun MaybeKOf<T>.eqv(b: MaybeKOf<T>): Boolean {
    val res1 = arrow.core.Try { value().timeout(5, java.util.concurrent.TimeUnit.SECONDS).blockingGet() }
    val res2 = arrow.core.Try { b.value().timeout(5, java.util.concurrent.TimeUnit.SECONDS).blockingGet() }
    return res1.fold({ t1 ->
      res2.fold({ t2 ->
        (t1::class.java == t2::class.java)
      }, { false })
    }, { v1 ->
      res2.fold({ false }, {
        v1 == it
      })
    })
  }
}
