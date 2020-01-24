// package arrow.fx
//
// import arrow.Kind
// import arrow.core.None
// import arrow.core.Some
// import arrow.core.Tuple2
// import arrow.core.Tuple3
// import arrow.core.Left
// import arrow.core.extensions.list.traverse.traverse
// import arrow.core.fix
// import arrow.fx.extensions.io.concurrent.concurrent
// import arrow.fx.extensions.io.dispatchers.dispatchers
// import arrow.fx.typeclasses.Concurrent
// import arrow.fx.rx2.MaybeK
// import arrow.fx.rx2.MaybeKOf
// import arrow.fx.rx2.extensions.concurrent
// import arrow.fx.rx2.value
// import arrow.fx.typeclasses.milliseconds
// import arrow.test.UnitSpec
// import arrow.test.generators.nonEmptyList
// import arrow.test.generators.tuple2
// import arrow.test.generators.tuple3
// import arrow.test.generators.unit
// import io.kotlintest.fail
// import io.kotlintest.matchers.types.shouldBeInstanceOf
// import arrow.test.laws.equalUnderTheLaw
// import arrow.test.laws.forFew
// import arrow.typeclasses.Eq
// import io.kotlintest.properties.Gen
// import io.kotlintest.properties.forAll
// import io.kotlintest.shouldBe
// import kotlin.coroutines.CoroutineContext
// import kotlin.time.ExperimentalTime
// import kotlin.time.measureTimedValue
//
// class QueueTest : UnitSpec() {
//
//   init {
//
//     fun <F> Concurrent<F>.tests(
//       label: String,
//       ctx: CoroutineContext = IO.dispatchers<Nothing>().default(),
//       factory: QueueFactory<F>,
//       EQ: Eq<Kind<F, Unit>>
//     ) {
//
//       fun Kind<F, Unit>.test(): Boolean =
//         equalUnderTheLaw(unit(), EQ)
//
//       "$label - offer and take a number of values in the same order" {
//         forFew(6, Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
//           fx.concurrent {
//             val q = !factory.unbounded<Int>()
//             !q.offer(t.a)
//             !q.offer(t.b)
//             !q.offer(t.c)
//             val first = !q.take()
//             val second = !q.take()
//             val third = !q.take()
//             !effect { Tuple3(first, second, third) shouldBe t }
//           }.test()
//         }
//       }
//
//       "$label - time out taking from an empty queue" {
//         fx.concurrent {
//           val wontComplete = factory.unbounded<Int>().flatMap(Queue<F, Int>::take)
//           val start = !effect { System.currentTimeMillis() }
//           val received = !wontComplete.map { Some(it) }
//             .waitFor(100.milliseconds, default = just(None))
//           val elapsed = !effect { System.currentTimeMillis() - start }
//           !effect { received shouldBe None }
//           !effect { (elapsed >= 100) shouldBe true }
//         }.test()
//       }
//
//       "$label - suspended take calls on an empty queue complete when offer calls made to queue" {
//         forFew(10, Gen.int()) { i ->
//           fx.concurrent {
//             val q = !factory.unbounded<Int>()
//             val first = !q.take().fork(ctx)
//             !q.offer(i)
//             val res = !first.join()
//             !effect { res shouldBe i }
//           }.test()
//         }
//       }
//
//       "$label - multiple take calls on an empty queue complete when until as many offer calls made to queue" {
//         forFew(6, Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
//           fx.concurrent {
//             val q = !factory.unbounded<Int>()
//             val first = !q.take().fork(ctx)
//             val second = !q.take().fork(ctx)
//             val third = !q.take().fork(ctx)
//             !q.offer(t.a)
//             !q.offer(t.b)
//             !q.offer(t.c)
//             val firstValue = !first.join()
//             val secondValue = !second.join()
//             val thirdValue = !third.join()
//             !effect { setOf(firstValue, secondValue, thirdValue) shouldBe setOf(t.a, t.b, t.c) }
//           }.test()
//         }
//       }
//
//       "$label - taking from a shutdown queue creates a QueueShutdown error" {
//         forAll(Gen.int()) { i ->
//           fx.concurrent {
//             val res = !fx.concurrent {
//               val q = !factory.unbounded<Int>()
//               !q.offer(i)
//               !q.shutdown()
//               !q.take()
//             }.attempt()
//
//             !effect { res shouldBe Left(QueueShutdown) }
//           }.test()
//         }
//       }
//
//       "$label - offering to a shutdown queue creates a QueueShutdown error" {
//         forAll(Gen.int()) { i ->
//           fx.concurrent {
//             val res = !fx.concurrent {
//               val q = !factory.unbounded<Int>()
//               !q.shutdown()
//               !q.offer(i)
//             }.attempt()
//
//             !effect { res shouldBe Left(QueueShutdown) }
//           }.test()
//         }
//       }
//
//       "$label - joining a forked, incomplete take call on a shutdown queue creates a QueueShutdown error" {
//         fx.concurrent {
//           val res = !fx.concurrent {
//             val q = !factory.unbounded<Int>()
//             val t = !q.take().fork(ctx)
//             !q.shutdown()
//             !t.join()
//           }.attempt()
//           !effect { res shouldBe Left(QueueShutdown) }
//         }.test()
//       }
//
//       "$label - create a shutdown hook completing a promise, then shutdown the queue, the promise should be completed" {
//         fx.concurrent {
//           val q = !factory.unbounded<Int>()
//           val p = !Promise<F, Boolean>(this@tests)
//           !(q.awaitShutdown().followedBy(p.complete(true))).fork()
//           !q.shutdown()
//           val res = !p.get()
//           !effect { res shouldBe true }
//         }.test()
//       }
//
//       "$label - shut it down, create a shutdown hook completing a promise, the promise should be completed immediately" {
//         fx.concurrent {
//           val q = !factory.unbounded<Int>()
//           !q.shutdown()
//           val p = !Promise<F, Boolean>(this@tests)
//           !(q.awaitShutdown().followedBy(p.complete(true))).fork()
//           !effect { p.get() shouldBe true }
//         }.test()
//       }
//
//       "$label - drops elements offered to a queue at capacity" {
//         forFew(6, Gen.int(), Gen.int(), Gen.int()) { x, x2, x3 ->
//           fx.concurrent {
//             val q = !factory.dropping<Int>(1)
//             !q.offer(x)
//             !q.offer(x2) // this `x2` should be dropped
//             val taken = !q.take()
//             !q.offer(x3)
//             val taken2 = !q.take()
//             val res = Tuple2(taken, taken2)
//             !effect { res shouldBe Tuple2(x, x3) }
//           }.test()
//         }
//       }
//
//       "$label - offering to a zero capacity queue with a pending taker" {
//         forFew(3, Gen.int()) { x ->
//           fx.concurrent {
//             val q = !factory.dropping<Int>(0)
//             val taker = !q.take().fork(ctx)
//             // Wait for the forked `take` to complete by checking the queue `size`,
//             // otherwise the test will suspend indefinitely if `take` occurs after `offer`.
//             !q.size().repeat<F, Int, Int>(this, Schedule.doUntil(this) { it == -1 })
//             !q.offer(x)
//             val res = !taker.join()
//             !effect { res shouldBe x }
//           }.test()
//         }
//       }
//
//
//       "$label - time out offering to a queue at capacity" {
//         fx.concurrent {
//           val q = !factory.bounded<Int>(1)
//           !q.offer(1)
//           val start = !effect { System.currentTimeMillis() }
//           val wontComplete = q.offer(2)
//           val received = !wontComplete.map { Some(it) }
//             .waitFor(100.milliseconds, default = just(None))
//           val elapsed = !effect { System.currentTimeMillis() - start }
//           !effect { received shouldBe None }
//           !effect { (elapsed >= 100) shouldBe true }
//         }.test()
//       }
//
//       "$label - offering to a 0 capacity queue in deficit honours blocking strategy" {
//         fx.concurrent {
//           val q = !factory.bounded<Int>(0)
//           // flip from initial Surplus state to Deficit
//           val first = !q.take().fork(ctx)
//           // then clear previous taker while staying in Deficit
//           !q.offer(1)
//           !first.join()
//           val start = !effect { System.currentTimeMillis() }
//           val wontComplete = q.offer(2)
//           val received = !wontComplete.map { Some(it) }
//             .waitFor(100.milliseconds, default = just(None))
//           val elapsed = !effect { System.currentTimeMillis() - start }
//           !effect { received shouldBe None }
//           !effect { (elapsed >= 100) shouldBe true }
//         }.test()
//       }
//
//       "$label - suspended offers called on an full queue complete when take calls made to queue" {
//         forFew(3, Gen.tuple2(Gen.int(), Gen.int())) { t ->
//           fx.concurrent {
//             val q = !factory.bounded<Int>(1)
//             !q.offer(t.a)
//             !q.offer(t.b).fork(ctx)
//             val first = !q.take()
//             val second = !q.take()
//             !effect { Tuple2(first, second) shouldBe t }
//           }.test()
//         }
//       }
//
//       "$label - multiple offer calls on an full queue complete when as many take calls are made to queue" {
//         forAll(Gen.tuple3(Gen.int(), Gen.int(), Gen.int())) { t ->
//           fx.concurrent {
//             val q = !factory.bounded<Int>(1)
//             !q.offer(t.a)
//             !q.offer(t.b).fork()
//             !q.offer(t.c).fork()
//
//             val first = !q.take()
//             val second = !q.take()
//             val third = !q.take()
//
//             val took = Tuple3(first, second, third)
//             val res = setOf(first, second, third)
//
//             val expected = setOf(t.a, t.b, t.c)
//             !effect { println("$took shouldBe $expected") }
//             !effect { res shouldBe expected }
//           }.test()
//         }
//       }
//
//       "$label - capacity must be a positive integer" {
//         factory.sliding<Int>(0).attempt().flatMap { res ->
//           effect {
//             res.fold(
//               { err -> err.shouldBeInstanceOf<IllegalArgumentException>() },
//               { fail("Expected Left<IllegalArgumentException>") }
//             )
//           }
//         }.test()
//       }
//
//       "$label - removes first element after offering to a queue at capacity" {
//         forFew(3, Gen.int(), Gen.nonEmptyList(Gen.int())) { x, xs ->
//           fx.concurrent {
//             val q = !factory.sliding<Int>(xs.size)
//             !q.offer(x)
//             !xs.traverse(this, q::offer)
//             val taken = !(1..xs.size).toList().traverse(this) { q.take() }
//             !effect { taken shouldBe xs.toList() }
//           }.test()
//         }
//       }
//     }
//
//     IO.concurrent<Nothing>().run {
//       // tests(
//       //   label = "IO Queue test",
//       //   factory = Queue.factory(IO.concurrent()),
//       //   EQ = IO_EQ()
//       // )
//       // boundedStrategyTests(queue = { capacity -> Queue.bounded(capacity, this) }, EQ = IO_EQ())
//       // slidingStrategyTests(queue = { capacity -> Queue.sliding(capacity, this) }, EQ = IO_EQ())
//       // droppingStrategyTests(queue = { capacity -> Queue.dropping(capacity, this) }, EQ = IO_EQ())
//       // unboundedStrategyTests(queue = { Queue.unbounded(this) }, EQ = IO_EQ())
//     }
//
//     MaybeK.concurrent().run {
//       tests(
//         label = "MaybeK Queue test",
//         factory = Queue.factory(MaybeK.concurrent()),
//         EQ = MaybeK.eq()
//       )
//       // boundedStrategyTests(queue = { capacity -> Queue.bounded(capacity, this) }, EQ = MaybeK.eq())
//       // slidingStrategyTests(queue = { capacity -> Queue.sliding(capacity, this) }, EQ = MaybeK.eq())
//       // droppingStrategyTests(queue = { capacity -> Queue.dropping(capacity, this) }, EQ = MaybeK.eq())
//       // unboundedStrategyTests(queue = { Queue.unbounded(this) }, EQ = IO_EQ())
//     }
//   }
// }
//
// fun <F> Queue.Companion.factory(CF: Concurrent<F>): QueueFactory<F> =
//   object : QueueFactory<F> {
//     override fun CF(): Concurrent<F> = CF
//   }
//
// interface QueueFactory<F> {
//   fun CF(): Concurrent<F>
//
//   fun <A> bounded(capacity: Int): Kind<F, Queue<F, A>> =
//     Queue.bounded(capacity, CF())
//
//   fun <A> sliding(capacity: Int): Kind<F, Queue<F, A>> =
//     Queue.sliding(capacity, CF())
//
//   fun <A> dropping(capacity: Int): Kind<F, Queue<F, A>> =
//     Queue.dropping(capacity, CF())
//
//   fun <A> unbounded(): Kind<F, Queue<F, A>> =
//     Queue.unbounded(CF())
// }
//
// private fun <T> MaybeK.Companion.eq(): Eq<MaybeKOf<T>> = object : Eq<MaybeKOf<T>> {
//   override fun MaybeKOf<T>.eqv(b: MaybeKOf<T>): Boolean {
//     val res1 = arrow.core.Try { value().timeout(5, java.util.concurrent.TimeUnit.SECONDS).blockingGet() }
//     val res2 = arrow.core.Try { b.value().timeout(5, java.util.concurrent.TimeUnit.SECONDS).blockingGet() }
//     return res1.fold({ t1 ->
//       res2.fold({ t2 ->
//         (t1::class.java == t2::class.java)
//       }, { false })
//     }, { v1 ->
//       res2.fold({ false }, {
//         v1 == it
//       })
//     })
//   }
// }
