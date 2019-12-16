package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.ForId
import arrow.core.ForOption
import arrow.core.ForTry
import arrow.core.Id
import arrow.core.Option
import arrow.core.Try
import arrow.core.const
import arrow.core.extensions.`try`.monadError.monadError
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.some
import arrow.core.value
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.mtl.concurrent
import arrow.mtl.extensions.kleisli.alternative.alternative
import arrow.mtl.extensions.kleisli.contravariant.contravariant
import arrow.mtl.extensions.kleisli.divisible.divisible
import arrow.mtl.extensions.kleisli.monadError.monadError
import arrow.test.UnitSpec
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadErrorLaws
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import io.kotlintest.shouldBe

class KleisliTest : UnitSpec() {
  private fun <A> TryEQ(): Eq<KleisliOf<ForTry, Int, A>> = Eq { a, b ->
    a.run(1) == b.run(1)
  }

  private fun <A> ConestTryEQ(): Eq<Kind<Conested<Kind<ForKleisli, ForTry>, A>, Int>> = Eq { a, b ->
    a.counnest().run(1) == b.counnest().run(1)
  }

  private fun <A> IOEQ(): Eq<Kind<KleisliPartialOf<ForIO, Int>, A>> = Eq { a, b ->
    a.run(1).attempt().unsafeRunSync() == b.run(1).attempt().unsafeRunSync()
  }

  init {

    testLaws(
      AlternativeLaws.laws(
        Kleisli.alternative<ForOption, Int>(Option.alternative()),
        { i -> Kleisli { i.some() } },
        { i -> Kleisli { { j: Int -> i + j }.some() } },
        Eq { a, b -> a.fix().run(0) == b.fix().run(0) }
      ),
      ConcurrentLaws.laws(
        Kleisli.concurrent<ForIO, Int>(IO.concurrent()),
        IOEQ(),
        IOEQ(),
        IOEQ()
      ),
      ContravariantLaws.laws(Kleisli.contravariant(), { Kleisli { x: Int -> Try.just(x) }.conest() }, ConestTryEQ()),
      DivisibleLaws.laws(
        Kleisli.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        { Kleisli { it.const() } },
        Eq { a, b -> a.run(1).value() == b.run(1).value() }
      ),
      MonadErrorLaws.laws(Kleisli.monadError<ForTry, Int, Throwable>(Try.monadError()), TryEQ(), TryEQ())
    )

    "andThen should continue sequence" {
      val kleisli: Kleisli<ForId, Int, Int> = Kleisli { a: Int -> Id(a) }

      kleisli.andThen(Id.monad(), Id(3)).run(0) shouldBe 3

      kleisli.andThen(Id.monad()) { b -> Id(b + 1) }.run(0) shouldBe 1
    }
  }
}
