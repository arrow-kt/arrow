package arrow.free

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple3
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.nonemptylist.applicative.applicative
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import arrow.free.extensions.FreeApplicativeApplicative
import arrow.free.extensions.FreeApplicativeEq
import arrow.free.extensions.freeapplicative.applicative.applicative
import arrow.free.extensions.freeapplicative.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.EqLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

sealed class OpsAp<out A> : Kind<OpsAp.F, A> {

  class F private constructor()

  data class Value(val a: Int) : OpsAp<Int>()
  data class Add(val a: Int, val y: Int) : OpsAp<Int>()
  data class Subtract(val a: Int, val y: Int) : OpsAp<Int>()

  companion object : FreeApplicativeApplicative<F> {
    fun value(n: Int): FreeApplicative<F, Int> = FreeApplicative.liftF(Value(n))
    fun add(n: Int, y: Int): FreeApplicative<F, Int> = FreeApplicative.liftF(Add(n, y))
    fun subtract(n: Int, y: Int): FreeApplicative<F, Int> = FreeApplicative.liftF(Subtract(n, y))
  }
}

fun <A> Kind<OpsAp.F, A>.fix(): OpsAp<A> = this as OpsAp<A>

class FreeApplicativeTest : UnitSpec() {

  private val program = OpsAp.tupled(OpsAp.value(1), OpsAp.add(3, 4), OpsAp.subtract(3, 4)).fix()

  init {

    val EQ: FreeApplicativeEq<OpsAp.F, ForId, Int> = FreeApplicative.eq(Id.monad(), idApInterpreter)

    fun EQK() = object : EqK<FreeApplicativePartialOf<OpsAp.F>> {
      override fun <A> Kind<FreeApplicativePartialOf<OpsAp.F>, A>.eqK(other: Kind<FreeApplicativePartialOf<OpsAp.F>, A>, EQ: Eq<A>): Boolean =
        (this.fix() to other.fix()).let {
          val EQ: FreeApplicativeEq<OpsAp.F, ForId, A> = FreeApplicative.eq(Id.monad(), idApInterpreter)

          EQ.run {
            it.first.eqv(it.second)
          }
        }
    }

    fun GENK() = object : GenK<FreeApplicativePartialOf<OpsAp.F>> {
      override fun <A> genK(gen: Gen<A>): Gen<Kind<FreeApplicativePartialOf<OpsAp.F>, A>> =
        gen.map { OpsAp.just(it) }
    }

    testLaws(
      EqLaws.laws(EQ, Gen.opsAp()),
      ApplicativeLaws.laws(OpsAp, GENK(), EQK()),
      ApplicativeLaws.laws(FreeApplicative.applicative(), GENK(), EQK())
    )

    "Can interpret an ADT as FreeApplicative operations" {
      val result: Tuple3<Int, Int, Int> = Tuple3(1, 7, -1)
      program.foldMap(optionApInterpreter, Option.applicative()).fix() shouldBe Some(result)
      program.foldMap(idApInterpreter, Id.applicative()).fix() shouldBe Id(result)
      program.foldMap(nonEmptyListApInterpreter, NonEmptyList.applicative()).fix() shouldBe NonEmptyList.of(result)
    }

    "fold is stack safe" {
      val loops = 10000
      val start = 333
      val r = FreeApplicative.liftF(NonEmptyList.of(start))
      val rr = (1..loops).toList().fold(r) { v, _ -> v.ap(FreeApplicative.liftF(NonEmptyList.of({ a: Int -> a + 1 }))) }
      rr.foldK(NonEmptyList.applicative()) shouldBe NonEmptyList.of(start + loops)
      val rx = (1..loops).toList().foldRight(r) { _, v -> v.ap(FreeApplicative.liftF(NonEmptyList.of({ a: Int -> a + 1 }))) }
      rx.foldK(NonEmptyList.applicative()) shouldBe NonEmptyList.of(start + loops)
    }
  }
}

private fun Gen.Companion.opsAp() =
  oneOf(valueGen, addGen, subtractGen)

private val valueGen = Gen.bind(Gen.int()) {
  OpsAp.value(it)
}

private val addGen = Gen.bind(Gen.int(), Gen.int()) { a, b ->
  OpsAp.add(a, b)
}

private val subtractGen = Gen.bind(Gen.int(), Gen.int()) { a, b ->
  OpsAp.subtract(a, b)
}
