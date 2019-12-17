package arrow.mtl

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ForTry
import arrow.core.ListK
import arrow.core.Try
import arrow.core.extensions.`try`.monad.monad
import arrow.core.extensions.listk.monad.monad
import arrow.core.extensions.listk.monadCombine.monadCombine
import arrow.core.extensions.listk.semigroupK.semigroupK
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.monad
import arrow.fx.mtl.statet.async.async
import arrow.mtl.extensions.StateTMonadState
import arrow.mtl.extensions.statet.applicative.applicative
import arrow.mtl.extensions.statet.monadCombine.monadCombine
import arrow.mtl.extensions.statet.monadState.monadState
import arrow.mtl.extensions.statet.semigroupK.semigroupK
import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.test.laws.MonadCombineLaws
import arrow.test.laws.MonadStateLaws
import arrow.test.laws.SemigroupKLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

class StateTTests : UnitSpec() {

  val M: StateTMonadState<ForTry, Int> = StateT.monadState(Try.monad())

  val listkEQK = object : EqK<StateTPartialOf<ForListK, Int>> {
    override fun <A> Kind<StateTPartialOf<ForListK, Int>, A>.eqK(other: Kind<StateTPartialOf<ForListK, Int>, A>, EQ: Eq<A>): Boolean =
      (this.runM(ListK.monad(), 1) to other.runM(ListK.monad(), 1)).let {
        Eq.any().run { it.first.eqv(it.second) }
      }
  }

  val ioEQK = object : EqK<StateTPartialOf<ForIO, Int>> {
    override fun <A> Kind<StateTPartialOf<ForIO, Int>, A>.eqK(other: Kind<StateTPartialOf<ForIO, Int>, A>, EQ: Eq<A>): Boolean =
      this.fix().runM(IO.monad(), 1).attempt().unsafeRunSync() == other.fix().runM(IO.monad(), 1).attempt().unsafeRunSync()
  }

  val tryEQK = object : EqK<StateTPartialOf<ForTry, Int>> {
    override fun <A> Kind<StateTPartialOf<ForTry, Int>, A>.eqK(other: Kind<StateTPartialOf<ForTry, Int>, A>, EQ: Eq<A>): Boolean =
      this.fix().runM(Try.monad(), 1) == other.fix().runM(Try.monad(), 1)
  }

  init {
    testLaws(
      MonadStateLaws.laws(M, tryEQK),
      AsyncLaws.laws<StateTPartialOf<ForIO, Int>>(StateT.async(IO.async()), ioEQK),
      SemigroupKLaws.laws(
        StateT.semigroupK<ForListK, Int>(ListK.monad(), ListK.semigroupK()),
        Gen.int().map { StateT.applicative<ForListK, Int>(ListK.monad()).just(it) } as Gen<Kind<StateTPartialOf<ForListK, Int>, Int>>,
        listkEQK),
      MonadCombineLaws.laws(StateT.monadCombine<ForListK, Int>(ListK.monadCombine()),
        { StateT.liftF(ListK.monad(), ListK.just(it)) },
        { StateT.liftF(ListK.monad(), ListK.just { s: Int -> s * 2 }) },
        listkEQK)
    )
  }
}
