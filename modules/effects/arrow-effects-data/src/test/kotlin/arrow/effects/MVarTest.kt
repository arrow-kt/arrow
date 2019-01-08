package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.monad.binding
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.typeclasses.seconds
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.effects.extensions.io.monad.F
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MVarTest : UnitSpec() {

  fun <A> EQ(): Eq<Kind<ForIO, A>> = Eq { a, b ->
    arrow.core.Option.eq(arrow.core.Either.eq(Eq.any(), Eq.any())).run {
      a.fix().attempt().unsafeRunTimed(60.seconds).eqv(b.fix().attempt().unsafeRunTimed(60.seconds))
    }
  }

  val mvar = MVar(IO.async())

  init {

    "empty; put; isNotEmpty; take; put; take" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        val task = F {
          val av = mvar.empty<Int>().bind()
          val isEmpty = av.isEmpty().bind()
          av.put(a).bind()
          val isNotEmpty = av.isNotEmpty().bind()
          val r1 = av.take().bind()
          av.put(b).bind()
          val r2 = av.take().bind()
          Tuple4(isEmpty, isNotEmpty, r1, r2)
        }

        task.equalUnderTheLaw(IO.just(Tuple4(true, true, a, b)), EQ())
      }
    }

    "empty; tryPut; tryPut; isNotEmpty; tryTake; tryTake; put; take" {
      forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
        val task = F {
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
        }

        task.equalUnderTheLaw(IO.just(Tuple7(true, true, false, true, Some(a), None, c)), EQ())
      }
    }

    "initial; isNotEmpty; take; put; take" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        val task = F {
          val av = mvar.of(a).bind()
          val isNotEmpty = av.isNotEmpty().bind()
          val r1 = av.take().bind()
          av.put(b).bind()
          val r2 = av.take().bind()

          Tuple3(isNotEmpty, r1, r2)
        }

        task.equalUnderTheLaw(IO.just(Tuple3(true, a, b)), EQ())
      }
    }

    "initial; read; take" {
      forAll(Gen.int()) { i ->
        val task = F {
          val av = mvar.of(i).bind()
          val read = av.read().bind()
          val take = av.take().bind()
          read toT take
        }

        task.equalUnderTheLaw(IO.just(i toT i), EQ())
      }
    }

    "put(null) works" {
      val task = mvar.empty<String?>().flatMap { mvar ->
        mvar.put(null).flatMap { mvar.read() }
      }

      task.equalUnderTheLaw(IO.just(null), EQ())
    }

    "take/put test is stack safe" {
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

}