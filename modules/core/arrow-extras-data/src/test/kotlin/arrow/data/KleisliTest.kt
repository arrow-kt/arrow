package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.attempt
import arrow.effects.extensions.io.bracket.bracket
import arrow.effects.extensions.kleisli.bracket.bracket
import arrow.core.extensions.`try`.monadError.monadError
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.data.extensions.kleisli.contravariant.contravariant
import arrow.data.extensions.kleisli.divisible.divisible
import arrow.data.extensions.kleisli.monadError.monadError
import arrow.test.UnitSpec
import arrow.test.laws.BracketLaws
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadErrorLaws
import arrow.typeclasses.*
import io.kotlintest.shouldBe
import io.kotlintest.runner.junit4.KotlinTestRunner
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

      kleisli.andThen(Id.monad(), Id(3)).run(0).value() shouldBe 3

      kleisli.andThen(Id.monad()) { b -> Id(b + 1) }.run(0).value() shouldBe 1
    }
  }
}
