package arrow.fx

import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple7
import arrow.core.test.UnitSpec
import arrow.core.toT
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.concurrent.parSequence
import arrow.fx.typeclasses.milliseconds
import arrow.fx.test.laws.equalUnderTheLaw
import arrow.fx.typeclasses.ConcurrentSyntax
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class MVarTest : UnitSpec() {

  fun <A> fx(c: suspend ConcurrentSyntax<IOPartialOf<Nothing>>.() -> A): IO<Nothing, A> =
    IO.concurrent<Nothing>().fx.concurrent(c).fix()

  init {

    fun tests(label: String, mvar: MVarFactory<IOPartialOf<Nothing>>) {
      "$label - empty; put; isNotEmpty; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx {
            val av = mvar.empty<Int>().bind()
            val isEmpty = av.isEmpty().bind()
            av.put(a).bind()
            val isNotEmpty = av.isNotEmpty().bind()
            val r1 = av.take().bind()
            av.put(b).bind()
            val r2 = av.take().bind()
            Tuple4(isEmpty, isNotEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple4(true, true, a, b)))
        }
      }

      "$label - empty; tryPut; tryPut; isNotEmpty; tryTake; tryTake; put; take" {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          fx {
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
          }.equalUnderTheLaw(IO.just(Tuple7(true, true, false, true, Some(a), None, c)))
        }
      }

      "$label - empty; take; put; take; put" {
        fx {
          val av = mvar.empty<Int>().bind()

          val f1 = av.take().fork().bind()
          av.put(10).bind()

          val f2 = av.take().fork().bind()
          av.put(20).bind()

          val aa = f1.join().bind()
          val bb = f2.join().bind()

          setOf(aa, bb)
        }.equalUnderTheLaw(IO.just(setOf(10, 20)))
      }

      "$label - empty; put; put; put; take; take; take" {
        fx {
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
        }.equalUnderTheLaw(IO.just(setOf(10, 20, 30)))
      }

      "$label - empty; take; take; take; put; put; put" {
        fx {
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
        }.equalUnderTheLaw(IO.just(setOf(10, 20, 30)))
      }

      "$label - initial; isNotEmpty; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx {
            val av = mvar.just(a).bind()
            val isNotEmpty = av.isNotEmpty().bind()
            val r1 = av.take().bind()
            av.put(b).bind()
            val r2 = av.take().bind()

            Tuple3(isNotEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple3(true, a, b)))
        }
      }

      "$label - initial; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx {
            val av = !mvar.just(a)
            val isEmpty = !av.isEmpty()
            val r1 = !av.take()
            !av.put(b)
            val r2 = !av.take()
            Tuple3(isEmpty, r1, r2)
          }.equalUnderTheLaw(IO.just(Tuple3(false, a, b)))
        }
      }

      "$label - initial; read; take" {
        forAll(Gen.int()) { i ->
          fx {
            val av = mvar.just(i).bind()
            val read = av.read().bind()
            val take = av.take().bind()
            read toT take
          }.equalUnderTheLaw(IO.just(i toT i))
        }
      }

      "$label - empty; read; put" {
        forAll(Gen.int()) { a ->
          fx {
            val av = !mvar.empty<Int>()
            val read = !av.read().fork()
            !av.put(a)
            !read.join()
          }.equalUnderTheLaw(IO.just(a))
        }
      }

      "$label - put(null) works" {
        val task = mvar.empty<String?>().flatMap { mvar ->
          mvar.put(null).flatMap { mvar.read() }
        }

        task.equalUnderTheLaw(IO.just(null))
      }

      "$label - take/put test is stack safe" {
        fun loop(n: Int, acc: Int, ch: MVar<IOPartialOf<Nothing>, Int>): IO<Nothing, Int> =
          if (n <= 0) IO.just(acc) else
            ch.take().flatMap { x ->
              ch.put(1).flatMap { loop(n - 1, acc + x, ch) }
            }

        val count = 10000
        val task = mvar.just(1).flatMap { ch -> loop(count, 0, ch) }
        task.equalUnderTheLaw(IO.just(count))
      }

      "$label - stack overflow test" {
        val count = 10_000

        fun consumer(ch: Channel<Int>, sum: Long): IO<Nothing, Long> =
          ch.take().flatMap {
            it.fold({
              IO.just(sum) // we are done!
            }, { x ->
              // next please
              consumer(ch, sum + x)
            })
          }

        fun exec(channel: Channel<Int>): IO<Nothing, Long> {
          val consumerTask = consumer(channel, 0L)
          val tasks = (0 until count).map { i -> channel.put(Some(i)) }
          val producerTask = tasks.parSequence().flatMap { channel.put(None) }

          return IO.fx<Nothing, Long> {
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
        fun producer(ch: Channel<Long>, list: List<Long>): IO<Nothing, Unit> =
          when {
            list.isEmpty() -> ch.put(None).fix() // we are done!
            else -> ch.put(Some(list.first())).flatMap { producer(ch, list.drop(1)) } // next please
          }

        fun consumer(ch: Channel<Long>, sum: Long): IO<Nothing, Long> =
          ch.take().flatMap {
            it.fold({
              IO.just(sum) // we are done!
            }, { x ->
              consumer(ch, sum + x) // next please
            })
          }

        val count = 10000L
        fx {
          val channel = !mvar.just(Option(0L))
          val producerFiber = !producer(channel, (0L until count).toList()).fork()
          val consumerFiber = !consumer(channel, 0L).fork()
          !producerFiber.join()
          !consumerFiber.join()
        }.equalUnderTheLaw(IO.just(count * (count - 1) / 2))
      }

      fun testStackSequential(channel: MVar<IOPartialOf<Nothing>, Int>): Tuple3<Int, IO<Nothing, Int>, IO<Nothing, Unit>> {
        val count = 10000

        fun readLoop(n: Int, acc: Int): IO<Nothing, Int> =
          if (n > 0) channel.read().followedBy(channel.take().flatMap { readLoop(n - 1, acc + 1) })
          else IO.just(acc)

        fun writeLoop(n: Int): IO<Nothing, Unit> =
          if (n > 0) channel.put(1).flatMap { writeLoop(n - 1) }
          else IO.just(Unit)

        return Tuple3(count, readLoop(count, 0), writeLoop(count))
      }

      "$label - put is stack safe when repeated sequentially" {
        fx {
          val channel = !mvar.empty<Int>()
          val (count, reads, writes) = testStackSequential(channel)
          !writes.fork()
          val r = !reads
          !effect { r shouldBe count }
        }.equalUnderTheLaw(IO.unit)
      }

      "$label - take is stack safe when repeated sequentially" {
        fx {
          val channel = !mvar.empty<Int>()
          val (count, reads, writes) = testStackSequential(channel)
          val fr = !reads.fork()
          !writes
          val r = !fr.join()
          !effect { r shouldBe count }
        }.equalUnderTheLaw(IO.unit)
      }

      "$label - concurrent take and put" {
        val count = 1_000
        fx {
          val mVar = !mvar.empty<Int>()
          val ref = !Ref<Int>(0)
          val takes = (0 until count).map { mVar.read().map2(mVar.take()) { (a, b) -> a + b }.flatMap { x -> ref.update { it + x } } }.parSequence()
          val puts = (0 until count).map { mVar.put(1) }.parSequence()
          val f1 = !takes.fork()
          val f2 = !puts.fork()
          !f1.join()
          !f2.join()
          !ref.get()
        }.equalUnderTheLaw(IO.just(count * 2))
      }
    }

    fun concurrentTests(label: String, mvar: MVarFactory<IOPartialOf<Nothing>>) {
      tests(label, mvar)

      "$label - put is cancellable" {
        fx {
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
        }.equalUnderTheLaw(IO.just(setOf(1, 3)))
      }

      "$label - take is cancellable" {
        fx {
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
        }.equalUnderTheLaw(IO.just(setOf(1, 3)))
      }

      "$label - read is cancellable" {
        fx {
          val mVar = !mvar.empty<Int>()
          val finished = !Promise<Int>()
          val fiber = !mVar.read().flatMap(finished::complete).fork()
          !IO.sleep(100.milliseconds) // Give read callback a chance to register
          !fiber.cancel()
          !mVar.put(10)
          val fallback = IO.sleep(200.milliseconds).followedBy(IO.just(0))
          !IO.raceN(finished.get(), fallback)
        }.equalUnderTheLaw(IO.just(Right(0)))
      }
    }

    tests("UncancellableMVar", MVar.factoryUncancellable(IO.async()))
    concurrentTests("CancellableMVar", MVar.factoryCancellable(IO.concurrent()))
  }
}

// Signaling option, because we need to detect completion
private typealias Channel<A> = MVar<IOPartialOf<Nothing>, Option<A>>
