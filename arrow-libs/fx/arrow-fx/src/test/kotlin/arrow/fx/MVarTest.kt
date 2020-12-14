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
            val av = mvar.empty<Int>().invoke()
            val isEmpty = av.isEmpty().invoke()
            av.put(a).invoke()
            val isNotEmpty = av.isNotEmpty().invoke()
            val r1 = av.take().invoke()
            av.put(b).invoke()
            val r2 = av.take().invoke()
            Tuple4(isEmpty, isNotEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple4(true, true, a, b)), IO.eq())
        }
      }

      "$label - empty; tryPut; tryPut; isNotEmpty; tryTake; tryTake; put; take" {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          IO.fx {
            val av = mvar.empty<Int>().invoke()
            val isEmpty = av.isEmpty().invoke()
            val p1 = av.tryPut(a).invoke()
            val p2 = av.tryPut(b).invoke()
            val isNotEmpty = av.isNotEmpty().invoke()
            val r1 = av.tryTake().invoke()
            val r2 = av.tryTake().invoke()
            av.put(c).invoke()
            val r3 = av.take().invoke()
            Tuple7(isEmpty, p1, p2, isNotEmpty, r1, r2, r3)
          }.equalUnderTheLaw(IO.just(Tuple7(true, true, false, true, Some(a), None, c)), IO.eq())
        }
      }

      "$label - empty; take; put; take; put" {
        IO.fx {
          val av = mvar.empty<Int>().invoke()

          val f1 = av.take().fork().invoke()
          av.put(10).invoke()

          val f2 = av.take().fork().invoke()
          av.put(20).invoke()

          val aa = f1.join().invoke()
          val bb = f2.join().invoke()

          setOf(aa, bb)
        }.shouldBeEq(IO.just(setOf(10, 20)), IO.eq())
      }

      "$label - empty; put; put; put; take; take; take" {
        IO.fx {
          val av = mvar.empty<Int>().invoke()

          val f1 = av.put(10).fork().invoke()
          val f2 = av.put(20).fork().invoke()
          val f3 = av.put(30).fork().invoke()

          val aa = av.take().invoke()
          val bb = av.take().invoke()
          val cc = av.take().invoke()

          f1.join().invoke()
          f2.join().invoke()
          f3.join().invoke()

          setOf(aa, bb, cc)
        }.shouldBeEq(IO.just(setOf(10, 20, 30)), IO.eq())
      }

      "$label - empty; take; take; take; put; put; put" {
        IO.fx {
          val av = mvar.empty<Int>().invoke()

          val f1 = av.take().fork().invoke()
          val f2 = av.take().fork().invoke()
          val f3 = av.take().fork().invoke()

          av.put(10).invoke()
          av.put(20).invoke()
          av.put(30).invoke()

          val aa = f1.join().invoke()
          val bb = f2.join().invoke()
          val cc = f3.join().invoke()

          setOf(aa, bb, cc)
        }.shouldBeEq(IO.just(setOf(10, 20, 30)), IO.eq())
      }

      "$label - initial; isNotEmpty; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          IO.fx {
            val av = mvar.just(a).invoke()
            val isNotEmpty = av.isNotEmpty().invoke()
            val r1 = av.take().invoke()
            av.put(b).invoke()
            val r2 = av.take().invoke()

            Tuple3(isNotEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple3(true, a, b)), IO.eq())
        }
      }

      "$label - initial; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          IO.fx {
            val av = mvar.just(a).invoke()
            val isEmpty = av.isEmpty().invoke()
            val r1 = av.take().invoke()
            av.put(b).invoke()
            val r2 = av.take().invoke()
            Tuple3(isEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple3(false, a, b)), IO.eq())
        }
      }

      "$label - initial; read; take" {
        forAll(Gen.int()) { i ->
          IO.fx {
            val av = mvar.just(i).invoke()
            val read = av.read().invoke()
            val take = av.take().invoke()
            read toT take
          }.equalUnderTheLaw(IO.just(i toT i), IO.eq())
        }
      }

      "$label - empty; read; put" {
        forAll(Gen.int()) { a ->
          IO.fx {
            val av = mvar.empty<Int>().invoke()
            val read = av.read().fork().invoke()
            av.put(a).invoke()
            read.join().invoke()
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
            val f1 = producerTask.fork().invoke()
            val f2 = consumerTask.fork().invoke()
            f1.join().invoke()
            f2.join().invoke()
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
          val channel = mvar.just(Option(0L)).invoke()
          val producerFiber = producer(channel, (0L until count).toList()).fork().invoke()
          val consumerFiber = consumer(channel, 0L).fork().invoke()
          producerFiber.join().invoke()
          consumerFiber.join().invoke()
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
          val channel = mvar.empty<Int>().invoke()
          val (count, reads, writes) = testStackSequential(channel)
          writes.fork().invoke()
          val r = reads.invoke()
          effect { r shouldBe count }.invoke()
        }.equalUnderTheLaw(IO.unit, IO.eq())
      }

      "$label - take is stack safe when repeated sequentially" {
        IO.fx {
          val channel = mvar.empty<Int>().invoke()
          val (count, reads, writes) = testStackSequential(channel)
          val fr = reads.fork().invoke()
          writes.invoke()
          val r = fr.join().invoke()
          effect { r shouldBe count }.invoke()
        }.equalUnderTheLaw(IO.unit, IO.eq())
      }

      "$label - concurrent take and put" {
        val count = 1_000
        IO.fx {
          val mVar = mvar.empty<Int>().invoke()
          val ref = Ref(0).invoke()
          val takes = (0 until count).map {
            mVar.read().map2(mVar.take()) { (a, b) -> a + b }.flatMap { x -> ref.update { it + x } }
          }.parSequence()
          val puts = (0 until count).map { mVar.put(1) }.parSequence()
          val f1 = takes.fork().invoke()
          val f2 = puts.fork().invoke()
          f1.join().invoke()
          f2.join().invoke()
          ref.get().invoke()
        }.equalUnderTheLaw(IO.just(count * 2), IO.eq())
      }
    }

    fun concurrentTests(label: String, mvar: MVarFactory<ForIO>) {
      tests(label, mvar)

      "$label - put is cancellable" {
        IO.fx {
          val mVar = mvar.just(0).invoke()
          mVar.put(1).fork().invoke()
          val p2 = mVar.put(2).fork().invoke()
          mVar.put(3).fork().invoke()
          IO.sleep(10.milliseconds).invoke() // Give put callbacks a chance to register
          p2.cancel().invoke()
          mVar.take().invoke()
          val r1 = mVar.take().invoke()
          val r3 = mVar.take().invoke()
          setOf(r1, r3)
        }.equalUnderTheLaw(IO.just(setOf(1, 3)), IO.eq())
      }

      "$label - take is cancellable" {
        IO.fx {
          val mVar = mvar.empty<Int>().invoke()
          val t1 = mVar.take().fork().invoke()
          val t2 = mVar.take().fork().invoke()
          val t3 = mVar.take().fork().invoke()
          IO.sleep(10.milliseconds).invoke() // Give take callbacks a chance to register
          t2.cancel().invoke()
          mVar.put(1).invoke()
          mVar.put(3).invoke()
          val r1 = t1.join().invoke()
          val r3 = t3.join().invoke()
          setOf(r1, r3)
        }.equalUnderTheLaw(IO.just(setOf(1, 3)), IO.eq())
      }

      "$label - read is cancellable" {
        IO.fx {
          val mVar = mvar.empty<Int>().invoke()
          val finished = Promise<Int>().invoke()
          val fiber = mVar.read().flatMap(finished::complete).fork().invoke()
          IO.sleep(100.milliseconds).invoke() // Give read callback a chance to register
          fiber.cancel().invoke()
          mVar.put(10).invoke()
          val fallback = sleep(200.milliseconds).followedBy(IO.just(0))
          val res = IO.raceN(finished.get(), fallback).invoke()
        }.equalUnderTheLaw(IO.just(Right(0)), IO.eq())
      }
    }

    tests("UncancellableMVar", MVar.factoryUncancellable(IO.async()))
    concurrentTests("CancellableMVar", MVar.factoryCancellable(IO.concurrent()))
  }
}

// Signaling option, because we need to detect completion
private typealias Channel<A> = MVar<ForIO, Option<A>>
