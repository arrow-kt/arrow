package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.ForId
import arrow.core.ForOption
import arrow.core.Id
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.either.eqK.eqK
import arrow.core.extensions.either.monad.monad
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.toT
import arrow.mtl.extensions.accumt.alternative.alternative
import arrow.mtl.extensions.accumt.applicative.applicative
import arrow.mtl.extensions.accumt.applicativeError.applicativeError
import arrow.mtl.extensions.accumt.functor.functor
import arrow.mtl.extensions.accumt.monad.monad
import arrow.mtl.extensions.accumt.monadError.monadError
import arrow.mtl.extensions.accumt.monadTrans.monadTrans
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.generators.intSmall
import arrow.test.generators.tuple2
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ApplicativeErrorLaws
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.FunctorLaws
import arrow.test.laws.MonadErrorLaws
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
      ),

      AlternativeLaws.laws<AccumTPartialOf<Int, ForOption>>(
        AccumT.alternative(Option.alternative(), Option.monad(), Int.monoid()),
        AccumT.genK(Option.genK(), Gen.int()),
        AccumT.eqK(Option.monad(), Option.eqK(), Int.eq(), 10)
      ),

      ApplicativeErrorLaws.laws(
        AccumT.applicativeError(Either.monadError<String>(), Int.monoid()),
        AccumT.genK(Either.genK(Gen.string()), Gen.int()),
        Gen.string(),
        AccumT.eqK(Either.monad(), Either.eqK(String.eq()), Int.eq(), 10)
      ),

      MonadErrorLaws.laws<AccumTPartialOf<Int, EitherPartialOf<String>>, String>(
        AccumT.monadError(Int.monoid(), Either.monadError()),
        AccumT.genK(Either.genK(Gen.string()), Gen.int()),
        Gen.string(),
        AccumT.eqK(Either.monad(), Either.eqK(String.eq()), Int.eq(), 10)
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
      MF.just(s1 toT a)
    }

    val mf = AccumT(MF) { _: S ->
      MF.just(s2 toT { a: A -> a })
    }

    val ls = accumT.ap(MS, MF, mf).execAccumT(MF, s3)
    val rs = MF.just(MS.run { s1.combine(s2) })

    ls.equalUnderTheLaw(rs, eq)
  }

private fun <S, F> AccumT.Companion.genK(genkF: GenK<F>, genS: Gen<S>) =
  object : GenK<AccumTPartialOf<S, F>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<AccumTPartialOf<S, F>, A>> =
      genkF.genK(genkF.genK(Gen.tuple2(genS, gen)).map {
        { _: S -> it }
      }).map {
        AccumT(it)
      }
  }

private fun <S, F> AccumT.Companion.eqK(MF: Monad<F>, eqkF: EqK<F>, eqS: Eq<S>, s: S) =
  object : EqK<AccumTPartialOf<S, F>> {
    override fun <A> Kind<AccumTPartialOf<S, F>, A>.eqK(other: Kind<AccumTPartialOf<S, F>, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        it.first.runAccumT(MF, s) to it.second.runAccumT(MF, s)
      }.let {
        eqkF.liftEq(Tuple2.eq(eqS, EQ)).run {
          it.first.eqv(it.second)
        }
      }
  }
