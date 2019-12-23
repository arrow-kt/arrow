package arrow.mtl

import arrow.Kind
import arrow.core.ForOption
import arrow.core.ForTry
import arrow.core.Option
import arrow.core.Try
import arrow.core.Tuple2
import arrow.core.extensions.`try`.eqK.eqK
import arrow.core.extensions.`try`.functor.functor
import arrow.core.extensions.`try`.monad.monad
import arrow.core.extensions.eq
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.monadCombine.monadCombine
import arrow.core.extensions.option.semigroupK.semigroupK
import arrow.core.extensions.tuple2.eq.eq
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.fx.mtl.statet.async.async
import arrow.mtl.extensions.StateTMonadState
import arrow.mtl.extensions.statet.applicative.applicative
import arrow.mtl.extensions.statet.functor.functor
import arrow.mtl.extensions.statet.monad.monad
import arrow.mtl.extensions.statet.monadCombine.monadCombine
import arrow.mtl.extensions.statet.monadState.monadState
import arrow.mtl.extensions.statet.semigroupK.semigroupK
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.generators.tuple2
import arrow.test.laws.AsyncLaws
import arrow.test.laws.MonadCombineLaws
import arrow.test.laws.MonadStateLaws
import arrow.test.laws.SemigroupKLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen

class StateTTests : UnitSpec() {

  val M: StateTMonadState<ForTry, Int> = StateT.monadState(Try.monad())

  val optionStateEQK: EqK<StateTPartialOf<ForOption, Int>> = eqK(Option.eqK(), Int.eq(), Option.monad(), 1)
  val ioStateEQK: EqK<StateTPartialOf<ForIO, Int>> = eqK(IO.eqK(), Int.eq(), IO.monad(), 1)
  val tryStateEqK: EqK<Kind<Kind<ForStateT, ForTry>, Int>> = eqK(Try.eqK(), Int.eq(), Try.monad(), 1)

  init {
    testLaws(
      MonadStateLaws.laws(
        M,
        StateT.functor<ForTry, Int>(Try.functor()),
        StateT.applicative<ForTry, Int>(Try.monad()),
        StateT.monad<ForTry, Int>(Try.monad()),
        StateT.genK(Try.genK(), Gen.int()),
        tryStateEqK
      ),

      AsyncLaws.laws<StateTPartialOf<ForIO, Int>>(
        StateT.async(IO.async()),
        StateT.functor(IO.functor()),
        StateT.applicative(IO.monad()),
        StateT.monad(IO.monad()),
        StateT.genK(IO.genK(), Gen.int()),
        ioStateEQK
      ),

      SemigroupKLaws.laws(
        StateT.semigroupK<ForOption, Int>(Option.monad(), Option.semigroupK()),
        StateT.genK(Option.genK(), Gen.int()),
        optionStateEQK),

      MonadCombineLaws.laws(
        StateT.monadCombine<ForOption, Int>(Option.monadCombine()),
        StateT.functor<ForOption, Int>(Option.functor()),
        StateT.applicative<ForOption, Int>(Option.monad()),
        StateT.monad<ForOption, Int>(Option.monad()),
        StateT.genK(Option.genK(), Gen.int()),
        optionStateEQK)
    )
  }
}

private fun <F, S> eqK(EQKF: EqK<F>, EQS: Eq<S>, M: Monad<F>, s: S) = object : EqK<StateTPartialOf<F, S>> {
  override fun <A> Kind<StateTPartialOf<F, S>, A>.eqK(other: Kind<StateTPartialOf<F, S>, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      val ls = it.first.runM(M, s)
      val rs = it.second.runM(M, s)

      EQKF.liftEq(Tuple2.eq(EQS, EQ)).run {
        ls.eqv(rs)
      }
    }
}

private fun <F, S> StateT.Companion.genK(genkF: GenK<F>, genS: Gen<S>) = object : GenK<StateTPartialOf<F, S>> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<StateTPartialOf<F, S>, A>> =
    genkF.genK(genkF.genK(Gen.tuple2(genS, gen)).map { state ->
      val stateTFun: StateTFun<F, S, A> = { _: S -> state }
      stateTFun
    }).map {
      StateT(it)
    }
}
