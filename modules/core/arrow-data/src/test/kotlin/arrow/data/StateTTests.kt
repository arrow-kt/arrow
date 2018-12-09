package arrow.data

import arrow.Kind
import arrow.core.Either
import arrow.core.ForTry
import arrow.core.Try
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.instances.io.applicativeError.attempt
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.monad.monad
import arrow.effects.instances.statet.async.async
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
import arrow.test.laws.AsyncLaws
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

  private fun IOEQ(): Eq<StateTOf<ForIO, Int, Int>> = Eq { a, b ->
    a.runM(IO.monad(), 1).attempt().unsafeRunSync() == b.runM(IO.monad(), 1).attempt().unsafeRunSync()
  }

  private fun IOEitherEQ(): Eq<StateTOf<ForIO, Int, Either<Throwable, Int>>> = Eq { a, b ->
    a.runM(IO.monad(), 1).attempt().unsafeRunSync() == b.runM(IO.monad(), 1).attempt().unsafeRunSync()
  }

  init {

    testLaws(
      MonadStateLaws.laws(M, EQ, EQ_UNIT),
      AsyncLaws.laws<StateTPartialOf<ForIO, Int>>(StateT.async(IO.async()), IOEQ(), IOEitherEQ()),
      SemigroupKLaws.laws(
        StateT.semigroupK<ForListK, Int>(ListK.monad(), ListK.semigroupK()),
        StateT.applicative<ForListK, Int>(ListK.monad()),
        EQ_LIST),
      MonadCombineLaws.laws(StateT.monadCombine<ForListK, Int>(ListK.monadCombine()),
        { StateT.liftF(ListK.monad(), ListK.just(it)) },
        { StateT.liftF(ListK.monad(), ListK.just({ s: Int -> s * 2 })) },
        EQ_LIST)
    )
  }

}
