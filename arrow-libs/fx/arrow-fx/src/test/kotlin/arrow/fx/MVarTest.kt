package arrow.fx

import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple7
import arrow.core.extensions.eq
import arrow.core.test.laws.equalUnderTheLaw
import arrow.core.toT
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.concurrent.parSequence
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.extensions.io.monad.followedBy
import arrow.fx.typeclasses.milliseconds
import arrow.fx.test.eq.eq
import arrow.fx.test.laws.shouldBeEq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class MVarTest : ArrowFxSpec() {

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
          }.equalUnderTheLaw(IO.just(Tuple4(true, true, a, b)), IO.eq())
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
          }.equalUnderTheLaw(IO.just(Tuple7(true, true, false, true, Some(a), None, c)), IO.eq())
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
        }.shouldBeEq(IO.just(setOf(10, 20)), IO.eq())
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
        }.shouldBeEq(IO.just(setOf(10, 20, 30)), IO.eq())
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
        }.shouldBeEq(IO.just(setOf(10, 20, 30)), IO.eq())
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
          }.equalUnderTheLaw(IO.just(Tuple3(true, a, b)), IO.eq())
        }
      }

      "$label - initial; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          IO.fx {
            val av = mvar.just(a).bind()
            val isEmpty = av.isEmpty().bind()
            val r1 = av.take().bind()
            av.put(b).bind()
            val r2 = av.take().bind()
            Tuple3(isEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple3(false, a, b)), IO.eq())
        }
      }

      "$label - initial; read; take" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val av = mvar.just(i).bind()
            val read = av.read().bind()
            val take = av.take().bind()
            read toT take
          }.equalUnderTheLaw(IO.just(i toT i), IO.eq())
        }
      }

      "$label - empty; read; put" {
        forAll(Gen.int()) { a ->
          IO.fx {
            val av = mvar.empty<Int>().bind()
            val read = av.read().fork().bind()
            av.put(a).bind()
            read.join().bind()
          }.equalUnderTheLaw(IO.just(a), IO.eq())
        }
      }

      "$label - put(null) works" {
        val task = mvar.empty<String?>().flatMap { mvar ->
          mvar.put(null).flatMap { mvar.read() }
        }

        task.equalUnderTheLaw(IO.just(null), IO.eq())
      }

      "$label - take/put test is stack safe" {
        fun loop(n: Int, acc: Int, ch: MVar<ForIO, Int>): IO<Int> =
          if (n <= 0) IO.just(acc) else
            ch.take().flatMap { x ->
              ch.put(1).flatMap { loop(n - 1, acc + x, ch) }
            }

        val count = 10000
        val task = mvar.just(1).flatMap { ch -> loop(count, 0, ch) }
        task.equalUnderTheLaw(IO.just(count), IO.eq())
      }

      "$label - stack overflow test" {
        val count = 10_000

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
            val f1 = producerTask.fork().bind()
            val f2 = consumerTask.fork().bind()
            f1.join().bind()
            f2.join().bind()
          }
        }

        mvar.just(Option(0)).flatMap(::exec)
          .unsafeRunSync() shouldBe count.toLong() * (count - 1) / 2
      }

      "$label - producer-consumer parallel loop" {
        fun producer(ch: Channel<Long>, list: List<Long>): IO<Unit> =
          when {
            list.isEmpty() -> ch.put(None).fix() // we are done!
            else -> ch.put(Some(list.first())).flatMap { producer(ch, list.drop(1)) } // next please
          }

        fun consumer(ch: Channel<Long>, sum: Long): IO<Long> =
          ch.take().flatMap {
            it.fold({
              IO.just(sum) // we are done!
            }, { x ->
              consumer(ch, sum + x) // next please
            })
          }

        val count = 10000L
        IO.fx {
          val channel = mvar.just(Option(0L)).bind()
          val producerFiber = producer(channel, (0L until count).toList()).fork().bind()
          val consumerFiber = consumer(channel, 0L).fork().bind()
          producerFiber.join().bind()
          consumerFiber.join().bind()
        }.equalUnderTheLaw(IO.just(count * (count - 1) / 2), IO.eq(Long.eq()))
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
          val channel = mvar.empty<Int>().bind()
          val (count, reads, writes) = testStackSequential(channel)
          writes.fork().bind()
          val r = reads.bind()
          effect { r shouldBe count }.bind()
        }.equalUnderTheLaw(IO.unit, IO.eq())
      }

      "$label - take is stack safe when repeated sequentially" {
        IO.fx {
          val channel = mvar.empty<Int>().bind()
          val (count, reads, writes) = testStackSequential(channel)
          val fr = reads.fork().bind()
          writes.bind()
          val r = fr.join().bind()
          effect { r shouldBe count }.bind()
        }.equalUnderTheLaw(IO.unit, IO.eq())
      }

      "$label - concurrent take and put" {
        val count = 1_000
        IO.fx {
          val mVar = mvar.empty<Int>().bind()
          val ref = Ref(0).bind()
          val takes = (0 until count).map {
            mVar.read().map2(mVar.take()) { (a, b) -> a + b }.flatMap { x -> ref.update { it + x } }
          }.parSequence()
          val puts = (0 until count).map { mVar.put(1) }.parSequence()
          val f1 = takes.fork().bind()
          val f2 = puts.fork().bind()
          f1.join().bind()
          f2.join().bind()
          ref.get().bind()
        }.equalUnderTheLaw(IO.just(count * 2), IO.eq())
      }
    }

    fun concurrentTests(label: String, mvar: MVarFactory<ForIO>) {
      tests(label, mvar)

      "$label - put is cancellable" {
        IO.fx {
          val mVar = mvar.just(0).bind()
          mVar.put(1).fork().bind()
          val p2 = mVar.put(2).fork().bind()
          mVar.put(3).fork().bind()
          IO.sleep(10.milliseconds).bind() // Give put callbacks a chance to register
          p2.cancel().bind()
          mVar.take().bind()
          val r1 = mVar.take().bind()
          val r3 = mVar.take().bind()
          setOf(r1, r3)
        }.equalUnderTheLaw(IO.just(setOf(1, 3)), IO.eq())
      }

      "$label - take is cancellable" {
        IO.fx {
          val mVar = mvar.empty<Int>().bind()
          val t1 = mVar.take().fork().bind()
          val t2 = mVar.take().fork().bind()
          val t3 = mVar.take().fork().bind()
          IO.sleep(10.milliseconds).bind() // Give take callbacks a chance to register
          t2.cancel().bind()
          mVar.put(1).bind()
          mVar.put(3).bind()
          val r1 = t1.join().bind()
          val r3 = t3.join().bind()
          setOf(r1, r3)
        }.equalUnderTheLaw(IO.just(setOf(1, 3)), IO.eq())
      }

      "$label - read is cancellable" {
        IO.fx {
          val mVar = mvar.empty<Int>().bind()
          val finished = Promise<Int>().bind()
          val fiber = mVar.read().flatMap(finished::complete).fork().bind()
          IO.sleep(100.milliseconds).bind() // Give read callback a chance to register
          fiber.cancel().bind()
          mVar.put(10).bind()
          val fallback = sleep(200.milliseconds).followedBy(IO.just(0))
          val res = IO.raceN(finished.get(), fallback).bind()
        }.equalUnderTheLaw(IO.just(Right(0)), IO.eq())
      }
    }

    tests("UncancellableMVar", MVar.factoryUncancellable(IO.async()))
    concurrentTests("CancellableMVar", MVar.factoryCancellable(IO.concurrent()))
  }
}

// Signaling option, because we need to detect completion
private typealias Channel<A> = MVar<ForIO, Option<A>>
