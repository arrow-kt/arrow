package arrow.data

import arrow.Kind
import arrow.core.ForTry
import arrow.core.Try
import arrow.instances.`try`.monad.monad
import arrow.instances.listk.monad.monad
import arrow.instances.listk.semigroupK.semigroupK
import arrow.instances.statet.applicative.applicative
import arrow.instances.statet.semigroupK.semigroupK
import arrow.mtl.instances.StateTMonadStateInstance
import arrow.mtl.instances.listk.monadCombine.monadCombine
import arrow.mtl.instances.statet.monadCombine.monadCombine
import arrow.mtl.instances.statet.monadState.monadState
import arrow.test.UnitSpec
import arrow.test.laws.MonadCombineLaws
import arrow.test.laws.MonadStateLaws
import arrow.test.laws.SemigroupKLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateTTests : UnitSpec() {

  val M: StateTMonadStateInstance<ForTry, Int> = StateT.monadState(Try.monad())

  val EQ: Eq<StateTOf<ForTry, Int, Int>> = Eq { a, b ->
    a.runM(Try.monad(), 1) == b.runM(Try.monad(), 1)
  }

  val EQ_UNIT: Eq<StateTOf<ForTry, Int, Unit>> = Eq { a, b ->
    a.runM(Try.monad(), 1) == b.runM(Try.monad(), 1)
  }

  val EQ_LIST: Eq<Kind<StateTPartialOf<ForListK, Int>, Int>> = Eq { a, b ->
    a.runM(ListK.monad(), 1) == b.runM(ListK.monad(), 1)
  }

  init {

    testLaws(
      MonadStateLaws.laws(M, EQ, EQ_UNIT),
      SemigroupKLaws.laws(
        StateT.semigroupK<ForListK, Int>(ListK.monad(), ListK.semigroupK()),
        StateT.applicative<ForListK, Int>(ListK.monad()),
        EQ_LIST),
      MonadCombineLaws.laws(StateT.monadCombine<ForListK, Int>(ListK.monadCombine()),
        { StateT.lift(ListK.monad(), ListK.just(it)) },
        { StateT.lift(ListK.monad(), ListK.just({ s: Int -> s * 2 })) },
        EQ_LIST)
    )
  }

}
