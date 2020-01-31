package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.either.eqK.eqK
import arrow.core.extensions.either.functor.functor
import arrow.core.extensions.either.monad.monad
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.toT
import arrow.fx.IO
import arrow.fx.extensions.io.monadIO.monadIO
import arrow.fx.fix
import arrow.fx.mtl.accumt.monadIO.monadIO
import arrow.mtl.extensions.accumt.alternative.alternative
import arrow.mtl.extensions.accumt.functor.functor
import arrow.mtl.extensions.accumt.monad.monad
import arrow.mtl.extensions.accumt.monadError.monadError
import arrow.mtl.extensions.accumt.monadState.monadState
import arrow.mtl.extensions.accumt.monadTrans.monadTrans
import arrow.mtl.extensions.accumt.monadWriter.monadWriter
import arrow.mtl.extensions.statet.monad.monad
import arrow.mtl.extensions.statet.monadState.monadState
import arrow.mtl.extensions.writert.eqK.eqK
import arrow.mtl.extensions.writert.monad.monad
import arrow.mtl.extensions.writert.monadWriter.monadWriter
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.generators.throwable
import arrow.test.generators.tuple2
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.MonadErrorLaws
import arrow.test.laws.MonadStateLaws
import arrow.test.laws.MonadTransLaws
import arrow.test.laws.MonadWriterLaws
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class AccumTTest : UnitSpec() {
  init {

    testLaws(

      MonadTransLaws.laws(
        AccumT.monadTrans(String.monoid()),
        Id.monad(),
        AccumT.monad(String.monoid(), Id.monad()),
        Id.genK(),
        AccumT.eqK(Id.monad(), Id.eqK(), String.eq(), "hello")
      ),

      AlternativeLaws.laws(
        AccumT.alternative(Option.alternative(), Option.monad(), Int.monoid()),
        AccumT.genK(Option.genK(), Gen.int()),
        AccumT.eqK(Option.monad(), Option.eqK(), Int.eq(), 10)
      ),

      MonadErrorLaws.laws<AccumTPartialOf<Int, EitherPartialOf<Throwable>>>(
        AccumT.monadError(Int.monoid(), Either.monadError()),
        AccumT.functor(Either.functor()),
        AccumT.monad(Int.monoid(), Either.monad()),
        AccumT.monad(Int.monoid(), Either.monad()),
        AccumT.genK(Either.genK(Gen.throwable()), Gen.int()),
        AccumT.eqK(Either.monad(), Either.eqK(Eq.any()) as EqK<EitherPartialOf<Throwable>>, Int.eq(), 10)
      ),

      MonadStateLaws.laws(
        AccumT.monadState<Int, Int, StateTPartialOf<ForId, Int>>(StateT.monadState(Id.monad()), Int.monoid()),
        AccumT.genK(StateT.genK(Id.genK(), Gen.int()), Gen.int()),
        AccumT.eqK<Int, StateTPartialOf<ForId, Int>>(
          StateT.monad(Id.monad()),
          StateT.eqK(Id.eqK(), Int.eq(), Id.monad(), 1),
          Int.eq(),
          1
        )
      ),

      MonadWriterLaws.laws(
        AccumT.monadWriter(WriterT.monadWriter(Id.monad(), String.monoid()), String.monoid()),
        AccumT.monadWriter(WriterT.monadWriter(Id.monad(), String.monoid()), String.monoid()),
        String.monoid(),
        Gen.string(),
        AccumT.genK(WriterT.genK(Id.genK(), Gen.string()), Gen.string()),
        AccumT.eqK(WriterT.monad(Id.monad(), String.monoid()), WriterT.eqK(Id.eqK(), String.eq()), String.eq(), ""),
        String.eq()
      )
    )

    "AccumT: flatMap combines State" {
      flatMapCombinesState(
        String.monoid(),
        Id.monad(),
        Gen.string(),
        Gen.bool(),
        Id.eqK().liftEq(String.eq())
      )
    }

    "AccumT: ap combines State" {
      apCombinesState(
        String.monoid(),
        Id.monad(),
        Gen.string(),
        Gen.bool(),
        Id.eqK().liftEq(String.eq())
      )
    }

    "AccumT: monadIO" {
      val accumT = AccumT.monadIO(IO.monadIO(), String.monoid()).run {
        IO.just(1).liftIO().fix()
      }

      val ls = accumT.runAccumT("1").fix().unsafeRunSync()

      ls shouldBe ("" toT 1)
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

    val accumT = AccumT { _: S ->
      MF.just(s1 toT a)
    }

    val mf = AccumT { _: S ->
      MF.just(s2 toT { a: A -> a })
    }

    val ls = accumT.ap(MS, MF, mf).execAccumT(MF, s3)
    val rs = MF.just(MS.run { s1.combine(s2) })

    ls.equalUnderTheLaw(rs, eq)
  }

private fun <S, F> AccumT.Companion.genK(genkF: GenK<F>, genS: Gen<S>) =
  object : GenK<AccumTPartialOf<S, F>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<AccumTPartialOf<S, F>, A>> =
      genkF.genK(Gen.tuple2(genS, gen)).map {
        AccumT { _: S -> it }
      }
  }

private fun <S, F> AccumT.Companion.eqK(MF: Monad<F>, eqkF: EqK<F>, eqS: Eq<S>, s: S) =
  object : EqK<AccumTPartialOf<S, F>> {
    override fun <A> Kind<AccumTPartialOf<S, F>, A>.eqK(other: Kind<AccumTPartialOf<S, F>, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        it.first.runAccumT(s) to it.second.runAccumT(s)
      }.let {
        eqkF.liftEq(Tuple2.eq(eqS, EQ)).run {
          it.first.eqv(it.second)
        }
      }
  }
