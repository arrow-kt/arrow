package arrow.effects

import arrow.core.*
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.extensions.io.monad.binding
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class MVarTest : UnitSpec() {

  init {

    fun tests(label: String, mvar: MVarPartialOf<ForIO>): Unit {
      "$label - empty; put; isNotEmpty; take; put; take" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          binding {
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
          binding {
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
        binding {
          val av = mvar.empty<Int>().bind()

          val f1 = av.take().startF(Dispatchers.Default).bind()
          av.put(10).bind()

          val f2 = av.take().startF(Dispatchers.Default).bind()
          av.put(20).bind()

          val aa = f1.join().bind()
          val bb = f2.join().bind()

          setOf(aa, bb)
        }.equalUnderTheLaw(IO.just(setOf(10, 20)), EQ(timeout = 1.seconds))
      }

      "$label - empty; put; put; put; take; take; take" {
        binding {
          val av = mvar.empty<Int>().bind()

          val f1 = av.put(10).startF(Dispatchers.Default).bind()
          val f2 = av.put(20).startF(Dispatchers.Default).bind()
          val f3 = av.put(30).startF(Dispatchers.Default).bind()

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
        binding {
          val av = mvar.empty<Int>().bind()

          val f1 = av.take().startF(Dispatchers.Default).bind()
          val f2 = av.take().startF(Dispatchers.Default).bind()
          val f3 = av.take().startF(Dispatchers.Default).bind()

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
          binding {
            val av = mvar.of(a).bind()
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
          binding {
            val av = mvar.of(i).bind()
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
        val task = mvar.of(1).flatMap { ch -> loop(count, 0, ch) }
        task.equalUnderTheLaw(IO.just(count), EQ())
      }
    }

    tests("UncancelableMVar", MVar(IO.async()))
    tests("CancelableMVar", MVar(IO.concurrent()))

  }

}
