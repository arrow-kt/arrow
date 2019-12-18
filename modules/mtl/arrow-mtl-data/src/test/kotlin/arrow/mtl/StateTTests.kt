package arrow.mtl

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ForOption
import arrow.core.ForTry
import arrow.core.ListK
import arrow.core.Option
import arrow.core.Try
import arrow.core.Tuple2
import arrow.core.extensions.`try`.monad.monad
import arrow.core.extensions.eq
import arrow.core.extensions.listk.eqK.eqK
import arrow.core.extensions.listk.monad.monad
import arrow.core.extensions.listk.monadCombine.monadCombine
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.semigroupK.semigroupK
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.fix
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.monad
import arrow.fx.mtl.statet.async.async
import arrow.mtl.extensions.StateTMonadState
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

  fun <F, S> eqk(EQKF: EqK<F>, EQS: Eq<S>, M: Monad<F>, s: S) = object : EqK<StateTPartialOf<F, S>> {
    override fun <A> Kind<StateTPartialOf<F, S>, A>.eqK(other: Kind<StateTPartialOf<F, S>, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        val ls = it.first.runM(M, s)
        val rs = it.second.runM(M, s)

        EQKF.liftEq(Tuple2.eq(EQS, EQ)).run {
          ls.eqv(rs)
        }
      }
  }

  val listkStateEQK = eqk(ListK.eqK(), Int.eq(), ListK.monad(), 1)

  val optionStateEQK = eqk(Option.eqK(), Int.eq(), Option.monad(), 1)

  val ioStateEQK = eqk(IO.eqK(), Int.eq(), IO.monad(), 1)

  val tryEQK = object : EqK<ForTry> {
    override fun <A> Kind<ForTry, A>.eqK(other: Kind<ForTry, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        Option.eq(EQ).run {
          it.first.toOption().eqv(it.second.toOption())
        }
      }
  }

  val tryStateEqK: EqK<Kind<Kind<ForStateT, ForTry>, Int>> = eqk(tryEQK, Int.eq(), Try.monad(), 1)

  fun <F, S> genk(genkF: GenK<F>, genS: Gen<S>) = object : GenK<StateTPartialOf<F, S>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<StateTPartialOf<F, S>, A>> =
      genkF.genK(genkF.genK(Gen.tuple2(genS, gen)).map { state ->
        val stateTFun: StateTFun<F, S, A> = { _: S -> state }
        stateTFun
      }).map {
        StateT(it)
      }
  }

  init {
    testLaws(
      MonadStateLaws.laws(M, tryStateEqK),
      AsyncLaws.laws<StateTPartialOf<ForIO, Int>>(StateT.async(IO.async()), ioStateEQK),

      SemigroupKLaws.laws(
        StateT.semigroupK<ForOption, Int>(Option.monad(), Option.semigroupK()),
        genk(Option.genK(), Gen.int()),
        optionStateEQK),
      MonadCombineLaws.laws(StateT.monadCombine<ForListK, Int>(ListK.monadCombine()),
        { StateT.liftF(ListK.monad(), ListK.just(it)) },
        { StateT.liftF(ListK.monad(), ListK.just { s: Int -> s * 2 }) },
        listkStateEQK)
    )
  }
}
