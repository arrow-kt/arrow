package arrow.fx

import arrow.core.None
import arrow.core.Some
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple7
import arrow.core.toT
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
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
        }.equalUnderTheLaw(IO.just(setOf(10, 20)), EQ(timeout = 1.seconds))
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
        }.equalUnderTheLaw(IO.just(setOf(10, 20, 30)), EQ(timeout = 1.seconds))
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
        }.equalUnderTheLaw(IO.just(setOf(10, 20, 30)), EQ(timeout = 1.seconds))
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
    }

    tests("UncancelableMVar", MVar.factoryUncancelable(IO.async()))
    tests("CancelableMVar", MVar.factoryUncancelable(IO.concurrent()))
  }
}
