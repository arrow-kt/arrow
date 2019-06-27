package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.ForId
import arrow.core.ForTry
import arrow.core.Id
import arrow.core.Try
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.bracket.bracket
import arrow.fx.mtl.kleisli.bracket.bracket
import arrow.core.extensions.`try`.monadError.monadError
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.mtl.extensions.kleisli.contravariant.contravariant
import arrow.mtl.extensions.kleisli.divisible.divisible
import arrow.mtl.extensions.kleisli.monadError.monadError
import arrow.test.UnitSpec
import arrow.test.laws.BracketLaws
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadErrorLaws
import arrow.typeclasses.Conested
import arrow.typeclasses.Const
import arrow.typeclasses.ConstPartialOf
import arrow.typeclasses.Eq
import arrow.typeclasses.conest
import arrow.typeclasses.const
import arrow.typeclasses.counnest
import arrow.typeclasses.value
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class KleisliTest : UnitSpec() {
  private fun <A> TryEQ(): Eq<KleisliOf<ForTry, Int, A>> = Eq { a, b ->
    a.run(1) == b.run(1)
  }

  private fun <A> ConestTryEQ(): Eq<Kind<Conested<Kind<ForKleisli, ForTry>, A>, Int>> = Eq { a, b ->
    a.counnest().run(1) == b.counnest().run(1)
  }

  private fun IOEQ(): Eq<Kind<KleisliPartialOf<ForIO, Int>, Int>> = Eq { a, b ->
    a.run(1).attempt().unsafeRunSync() == b.run(1).attempt().unsafeRunSync()
  }

  private fun IOEitherEQ(): Eq<Kind<KleisliPartialOf<ForIO, Int>, Either<Throwable, Int>>> = Eq { a, b ->
    a.run(1).attempt().unsafeRunSync() == b.run(1).attempt().unsafeRunSync()
  }

  init {

    testLaws(
      BracketLaws.laws(
        Kleisli.bracket<ForIO, Int, Throwable>(IO.bracket()),
        EQ = IOEQ(),
        EQ_EITHER = IOEitherEQ(),
        EQERR = IOEQ()
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
