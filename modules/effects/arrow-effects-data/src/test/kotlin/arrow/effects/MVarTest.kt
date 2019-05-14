package arrow.effects

import arrow.Kind
import arrow.core.None
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple7
import arrow.effects.extensions.fx.async.async
import arrow.effects.extensions.fx.concurrent.concurrent
import arrow.effects.extensions.fx.fx.fx
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.fx.fx
import arrow.effects.suspended.fx.Fx
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class MVarTest : UnitSpec() {

  init {

    fun <F> arrow.effects.typeclasses.suspended.concurrent.Fx<F>.tests(label: String, EQ: Eq<Kind<F, Boolean>>, mvar: MVarFactory<F>) = concurrent().run {

      "$label - empty; put; isNotEmpty; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx {
            val av = !mvar.empty<Int>()
            val isEmpty = !av.isEmpty()
            !av.put(a)
            val isNotEmpty = !av.isNotEmpty()
            val r1 = !av.take()
            !av.put(b)
            val r2 = !av.take()
            Tuple4(isEmpty, isNotEmpty, r1, r2) == Tuple4(true, true, a, b)
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - empty; tryPut; tryPut; isNotEmpty; tryTake; tryTake; put; take" {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          fx {
            val av = !mvar.empty<Int>()
            val isEmpty = !av.isEmpty()
            val p1 = !av.tryPut(a)
            val p2 = !av.tryPut(b)
            val isNotEmpty = !av.isNotEmpty()
            val r1 = !av.tryTake()
            val r2 = !av.tryTake()
            !av.put(c)
            val r3 = !av.take()
            Tuple7(isEmpty, p1, p2, isNotEmpty, r1, r2, r3) == Tuple7(true, true, false, true, Some(a), None, c)
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      // "$label - empty; take; put; take; put" {
      //   fx {
      //     val av = !mvar.empty<Int>()
      //
      //     val f1 = !av.take().fork()
      //     !av.put(10)
      //
      //     val f2 = !av.take().fork()
      //     !av.put(20)
      //
      //     val aa = !f1.join()
      //     val bb = !f2.join()
      //
      //     setOf(aa, bb) == setOf(10, 20)
      //   }.equalUnderTheLaw(just(true), EQ)
      // }

      // TODO issue #
      // "$label - empty; put; put; put; take; take; take" {
      //   fx {
      //     val av = !mvar.empty<Int>()
      //
      //     val f1 = !av.put(10).fork()
      //     val f2 = !av.put(20).fork()
      //     val f3 = !av.put(30).fork()
      //
      //     val aa = !av.take()
      //     val bb = !av.take()
      //     val cc = !av.take()
      //
      //     !f1.join()
      //     !f2.join()
      //     !f3.join()
      //
      //     setOf(aa, bb, cc) == setOf(10, 20, 30)
      //   }.equalUnderTheLaw(just(true), EQ)
      // }

      // "$label - empty; take; take; take; put; put; put" {
      //   fx {
      //     val av = !mvar.empty<Int>()
      //
      //     val f1 = !av.take().fork()
      //     val f2 = !av.take().fork()
      //     val f3 = !av.take().fork()
      //
      //     !av.put(10)
      //     !av.put(20)
      //     !av.put(30)
      //
      //     val aa = !f1.join()
      //     val bb = !f2.join()
      //     val cc = !f3.join()
      //
      //     setOf(aa, bb, cc) == setOf(10, 20, 30)
      //   }.equalUnderTheLaw(just(true), EQ)
      // }

      "$label - initial; isNotEmpty; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx {
            val av = !mvar.just(a)
            val isNotEmpty = !av.isNotEmpty()
            val r1 = !av.take()
            !av.put(b)
            val r2 = !av.take()

            Tuple3(isNotEmpty, r1, r2) == Tuple3(true, a, b)
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - initial; read; take" {
        forAll(Gen.int()) { i ->
          fx {
            val av = !mvar.just(i)
            val read = !av.read()
            val take = !av.take()
            Tuple2(read, take) == Tuple2(i, i)
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - put(null) works" {
        mvar.empty<String?>().flatMap { mvar ->
          mvar.put(null).flatMap {
            mvar.read().flatMap { r ->
              delay { r == null }
            }
          }
        }.equalUnderTheLaw(just(true), EQ) shouldBe true
      }

      "$label - take/put test is stack safe" {
        fun loop(n: Int, acc: Int, ch: MVar<F, Int>): Kind<F, Int> =
          if (n <= 0) just(acc) else
            ch.take().flatMap { x ->
              ch.put(1).flatMap { loop(n - 1, acc + x, ch) }
            }

        val count = 10000
        val task = mvar.just(1).flatMap { ch -> loop(count, 0, ch) }
          .map { it == count }
        task.equalUnderTheLaw(just(true), EQ)
      }
    }

    IO.fx().tests("IO - UncancelableMVar", IO_EQ(), MVar.factoryUncancelable(IO.async()))
    IO.fx().tests("IO - CancelableMVar", IO_EQ(), MVar.factoryCancelable(IO.concurrent()))
    Fx.fx().tests("Fx - UncancelableMVar", EQ(), MVar.factoryUncancelable(Fx.async()))
    Fx.fx().tests("Fx - CancelableMVar", EQ(), MVar.factoryCancelable(Fx.concurrent()))
  }
}
