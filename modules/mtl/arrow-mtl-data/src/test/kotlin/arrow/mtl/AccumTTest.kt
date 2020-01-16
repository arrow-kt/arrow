package arrow.mtl

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.toT
import arrow.mtl.extensions.accumt.applicative.applicative
import arrow.mtl.extensions.accumt.functor.functor
import arrow.mtl.extensions.accumt.monad.monad
import arrow.mtl.extensions.accumt.monadTrans.monadTrans
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.generators.intSmall
import arrow.test.generators.tuple2
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.FunctorLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonadTransLaws
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class AccumTTest : UnitSpec() {
  init {
    testLaws(
      FunctorLaws.laws(
        AccumT.functor<Int, ForId>(Id.functor()),
        AccumT.genK(Id.genK(), Gen.intSmall()),
        AccumT.eqK(Id.monad(), Id.eqK(), Int.eq(), 123)
      ),

      ApplicativeLaws.laws(
        AccumT.applicative(Int.monoid(), Id.monad()),
        AccumT.functor<Int, ForId>(Id.functor()),
        AccumT.genK(Id.genK(), Gen.intSmall()),
        AccumT.eqK(Id.monad(), Id.eqK(), Int.eq(), 123)
      ),

      MonadLaws.laws(
        AccumT.monad(Int.monoid(), Id.monad()),
        AccumT.genK(Id.genK(), Gen.intSmall()),
        AccumT.eqK(Id.monad(), Id.eqK(), Int.eq(), 4711)
      ),

      MonadTransLaws.laws(
        AccumT.monadTrans(String.monoid()),
        Id.monad(),
        AccumT.monad(String.monoid(), Id.monad()),
        Id.genK(),
        AccumT.eqK(Id.monad(), Id.eqK(), String.eq(), "hello")
      )
    )

    "flatMap combines State" {
      flatMapCombinesState(
        String.monoid(),
        Id.monad(),
        Gen.string(),
        Gen.bool(),
        Id.eqK().liftEq(String.eq())
      )
    }

    "ap combines State" {
      apCombinesState(
        String.monoid(),
        Id.monad(),
        Gen.string(),
        Gen.bool(),
        Id.eqK().liftEq(String.eq())
      )
    }
  }
}

private fun <S, F, A> flatMapCombinesState(
  MS: Monoid<S>,
  MF: Monad<F>,
  GENS: Gen<S>,
  GENA: Gen<A>,
  eq: Eq<Kind<F, S>>
): Unit =
  forAll(GENS, GENS, GENS, GENA) { g1, g2, g3, a ->

    val accumT = AccumT.add(MF, g1)

    val ls = accumT.flatMap(MS, MF) {
      AccumT.add(MF, g2)
    }.execAccumT(MF, g3)

    val rs = MF.just(MS.run { g1.combine(g2) })

    ls.equalUnderTheLaw(rs, eq)
  }

private fun <S, F, A> apCombinesState(
  MS: Monoid<S>,
  MF: Monad<F>,
  GENS: Gen<S>,
  GENA: Gen<A>,
  eq: Eq<Kind<F, S>>
): Unit =
  forAll(GENS, GENS, GENS, GENA) { s1, s2, s3, a ->

    val accumT = AccumT(MF) { _: S ->
      MF.just(a toT s1)
    }

    val mf = AccumT(MF) { _: S ->
      MF.just({ a: A -> a } toT s2)
    }

    val ls = accumT.ap(MS, MF, mf).execAccumT(MF, s3)
    val rs = MF.just(MS.run { s1.combine(s2) })

    ls.equalUnderTheLaw(rs, eq)
  }

private fun <W, M> AccumT.Companion.genK(genkM: GenK<M>, genW: Gen<W>) =
  object : GenK<AccumTPartialOf<W, M>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<AccumTPartialOf<W, M>, A>> =
      genkM.genK(genkM.genK(Gen.tuple2(gen, genW)).map {
        { _: W -> it }
      }).map {
        AccumT(it)
      }
  }

private fun <W, M> AccumT.Companion.eqK(MM: Monad<M>, EQK: EqK<M>, EQw: Eq<W>, w: W) =
  object : EqK<AccumTPartialOf<W, M>> {
    override fun <A> Kind<AccumTPartialOf<W, M>, A>.eqK(other: Kind<AccumTPartialOf<W, M>, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        it.first.runAccumT(MM, w) to it.second.runAccumT(MM, w)
      }.let {
        EQK.liftEq(Tuple2.eq(EQ, EQw)).run {
          val result = it.first.eqv(it.second)

          if (!result) {
            println("${it.first} != ${it.second}}")
          }

          result
        }
      }
  }
