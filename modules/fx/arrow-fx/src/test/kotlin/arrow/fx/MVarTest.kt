package arrow.fx

import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple7
import arrow.core.toT
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.concurrent.parSequence
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.extensions.io.monad.followedBy
import arrow.fx.typeclasses.milliseconds
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class MVarTest : UnitSpec() {

  init {

    fun tests(label: String, mvar: MVarFactory<ForIO>) {
      "$label - empty; put; isNotEmpty; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          IO.fx {
            val av = mvar.empty<Int>().bind()
            val isEmpty = av.isEmpty().bind()
            av.put(a).bind()
            val isNotEmpty = av.isNotEmpty().bind()
            val r1 = av.take().bind()
            av.put(b).bind()
            val r2 = av.take().bind()
            Tuple4(isEmpty, isNotEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple4(true, true, a, b)), EQ())
        }
      }

      "$label - empty; tryPut; tryPut; isNotEmpty; tryTake; tryTake; put; take" {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          IO.fx {
            val av = mvar.empty<Int>().bind()
            val isEmpty = av.isEmpty().bind()
            val p1 = av.tryPut(a).bind()
            val p2 = av.tryPut(b).bind()
            val isNotEmpty = av.isNotEmpty().bind()
            val r1 = av.tryTake().bind()
            val r2 = av.tryTake().bind()
            av.put(c).bind()
            val r3 = av.take().bind()
            Tuple7(isEmpty, p1, p2, isNotEmpty, r1, r2, r3)
          }.equalUnderTheLaw(IO.just(Tuple7(true, true, false, true, Some(a), None, c)), EQ())
        }
      }

      "$label - empty; take; put; take; put" {
        IO.fx {
          val av = mvar.empty<Int>().bind()

          val f1 = av.take().fork().bind()
          av.put(10).bind()

          val f2 = av.take().fork().bind()
          av.put(20).bind()

          val aa = f1.join().bind()
          val bb = f2.join().bind()

          setOf(aa, bb)
        }.equalUnderTheLaw(IO.just(setOf(10, 20)), EQ())
      }

      "$label - empty; put; put; put; take; take; take" {
        IO.fx {
          val av = mvar.empty<Int>().bind()

          val f1 = av.put(10).fork().bind()
          val f2 = av.put(20).fork().bind()
          val f3 = av.put(30).fork().bind()

          val aa = av.take().bind()
          val bb = av.take().bind()
          val cc = av.take().bind()

          f1.join().bind()
          f2.join().bind()
          f3.join().bind()

          setOf(aa, bb, cc)
        }.equalUnderTheLaw(IO.just(setOf(10, 20, 30)), EQ())
      }

      "$label - empty; take; take; take; put; put; put" {
        IO.fx {
          val av = mvar.empty<Int>().bind()

          val f1 = av.take().fork().bind()
          val f2 = av.take().fork().bind()
          val f3 = av.take().fork().bind()

          av.put(10).bind()
          av.put(20).bind()
          av.put(30).bind()

          val aa = f1.join().bind()
          val bb = f2.join().bind()
          val cc = f3.join().bind()

          setOf(aa, bb, cc)
        }.equalUnderTheLaw(IO.just(setOf(10, 20, 30)), EQ())
      }

      "$label - initial; isNotEmpty; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          IO.fx {
            val av = mvar.just(a).bind()
            val isNotEmpty = av.isNotEmpty().bind()
            val r1 = av.take().bind()
            av.put(b).bind()
            val r2 = av.take().bind()

            Tuple3(isNotEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple3(true, a, b)), EQ())
        }
      }

      "$label - initial; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          IO.fx {
            val av = !mvar.just(a)
            val isEmpty = !av.isEmpty()
            val r1 = !av.take()
            !av.put(b)
            val r2 = !av.take()
            Tuple3(isEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple3(false, a, b)), EQ())
        }
      }

      "$label - initial; read; take" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val av = mvar.just(i).bind()
            val read = av.read().bind()
            val take = av.take().bind()
            read toT take
          }.equalUnderTheLaw(IO.just(i toT i), EQ())
        }
      }

      "$label - empty; read; put" {
        forAll(Gen.int()) { a ->
          IO.fx {
            val av = !mvar.empty<Int>()
            val read = !av.read().fork()
            !av.put(a)
            !read.join()
          }.equalUnderTheLaw(IO.just(a), EQ())
        }
      }

      "$label - put(null) works" {
        val task = mvar.empty<String?>().flatMap { mvar ->
          mvar.put(null).flatMap { mvar.read() }
        }

        task.equalUnderTheLaw(IO.just(null), EQ())
      }

      "$label - take/put test is stack safe" {
        fun loop(n: Int, acc: Int, ch: MVar<ForIO, Int>): IO<Int> =
          if (n <= 0) IO.just(acc) else
            ch.take().flatMap { x ->
              ch.put(1).flatMap { loop(n - 1, acc + x, ch) }
            }

        val count = 10000
        val task = mvar.just(1).flatMap { ch -> loop(count, 0, ch) }
        task.equalUnderTheLaw(IO.just(count), EQ())
      }

      "!$label - stack overflow test" {
        // Ignored currently StackOverflows due to ListTraverse
        val count = 10000

        fun consumer(ch: Channel<Int>, sum: Long): IO<Long> =
          ch.take().flatMap {
            it.fold({
              IO.just(sum) // we are done!
            }, { x ->
              // next please
              consumer(ch, sum + x)
            })
          }

        fun exec(channel: Channel<Int>): IO<Long> {
          val consumerTask = consumer(channel, 0L)
          val tasks = (0 until count).map { i -> channel.put(Some(i)) }
          val producerTask = tasks.parSequence().flatMap { channel.put(None) }

          return IO.fx {
            val f1 = !producerTask.fork()
            val f2 = !consumerTask.fork()
            !f1.join()
            !f2.join()
          }
        }

        mvar.just(Option(0)).flatMap(::exec)
          .unsafeRunSync() shouldBe count.toLong() * (count - 1) / 2
      }

      "$label - producer-consumer parallel loop" {
        fun producer(ch: Channel<Int>, list: List<Int>): IO<Unit> =
          when {
            list.isEmpty() -> ch.put(None).fix() // we are done!
            else -> ch.put(Some(list.first())).flatMap { producer(ch, list.drop(1)) } // next please
          }

        fun consumer(ch: Channel<Int>, sum: Long): IO<Long> =
          ch.take().flatMap {
            it.fold({
              IO.just(sum) // we are done!
            }, { x ->
              consumer(ch, sum + x) // next please
            })
          }

        val count = 10000
        val sumTask = IO.fx {
          val channel = !mvar.just(Option(0))
          val producerFiber = !producer(channel, (0 until count).toList()).fork()
          val consumerFiber = !consumer(channel, 0L).fork()
          !producerFiber.join()
          !consumerFiber.join()
        }.equalUnderTheLaw(IO.just(count * (count - 1) / 2), EQ())
      }

      fun testStackSequential(channel: MVar<ForIO, Int>): Tuple3<Int, IO<Int>, IO<Unit>> {
        val count = 10000

        fun readLoop(n: Int, acc: Int): IO<Int> =
          if (n > 0) channel.read().followedBy(channel.take().flatMap { readLoop(n - 1, acc + 1) })
          else IO.just(acc)

        fun writeLoop(n: Int): IO<Unit> =
          if (n > 0) channel.put(1).flatMap { writeLoop(n - 1) }
          else IO.just(Unit)

        return Tuple3(count, readLoop(count, 0), writeLoop(count))
      }

      "$label - put is stack safe when repeated sequentially" {
        IO.fx {
          val channel = !mvar.empty<Int>()
          val (count, reads, writes) = testStackSequential(channel)
          !writes.fork()
          !reads
          !effect { reads shouldBe count }
        }.equalUnderTheLaw(IO.unit, EQ())
      }

      "$label - take is stack safe when repeated sequentially" {
        IO.fx {
          val channel = !mvar.empty<Int>()
          val (count, reads, writes) = testStackSequential(channel)
          val fr = !reads.fork()
          !writes
          val r = !fr.join()
          !effect { r shouldBe count }
        }.equalUnderTheLaw(IO.unit, EQ())
      }

      "$label - concurrent take and put" {
        val count = 10000
        IO.fx {
          val mvar = !mvar.empty<Int>()
          val ref = !Ref(0)
          val takes = (0 until count).map { mvar.read().map2(mvar.take()) { (a, b) -> a + b }.flatMap { x -> ref.update { it + x } } }.parSequence()
          val puts = (0 until count).map { mvar.put(1) }.parSequence()
          val f1 = !takes.fork()
          val f2 = !puts.fork()
          !f1.join()
          !f2.join()
          !ref.get()
        }.equalUnderTheLaw(IO.just(count), EQ())
      }
    }

    fun concurrentTests(label: String, mvar: MVarFactory<ForIO>) {
      tests(label, mvar)

      "$label - put is cancelable" {
        IO.fx {
          val mVar = !mvar.just(0)
          !mVar.put(1).fork()
          val p2 = !mVar.put(2).fork()
          !mVar.put(3).fork()
          !IO.sleep(10.milliseconds) // Give put callbacks a chance to register
          !p2.cancel()
          !mVar.take()
          val r1 = !mVar.take()
          val r3 = !mVar.take()
          setOf(r1, r3)
        }.equalUnderTheLaw(IO.just(setOf(1, 3)), EQ())
      }

      "$label - take is cancelable" {
        IO.fx {
          val mVar = !mvar.empty<Int>()
          val t1 = !mVar.take().fork()
          val t2 = !mVar.take().fork()
          val t3 = !mVar.take().fork()
          !IO.sleep(10.milliseconds) // Give take callbacks a chance to register
          !t2.cancel()
          !mVar.put(1)
          !mVar.put(3)
          val r1 = !t1.join()
          val r3 = !t3.join()
          setOf(r1, r3)
        }.equalUnderTheLaw(IO.just(setOf(1, 3)), EQ())
      }

      "$label - read is cancelable" {
        IO.fx {
          val mVar = !mvar.empty<Int>()
          val finished = !Promise<Int>()
          val fiber = !mVar.read().flatMap(finished::complete).fork()
          !IO.sleep(100.milliseconds) // Give read callback a chance to register
          !fiber.cancel()
          !mVar.put(10)
          val fallback = sleep(200.milliseconds).followedBy(IO.just(0))
          !IO.raceN(finished.get(), fallback)
        }.equalUnderTheLaw(IO.just(Right(0)), EQ())
      }
    }

    tests("UncancelableMVar", MVar.factoryUncancelable(IO.async()))
    concurrentTests("CancelableMVar", MVar.factoryCancelable(IO.concurrent()))
  }
}

// Signaling option, because we need to detect completion
private typealias Channel<A> = MVar<ForIO, Option<A>>
