package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.instances.io.applicativeError.attempt
import arrow.effects.instances.io.bracket.bracket
import arrow.effects.instances.kleisli.bracket.bracket
import arrow.instances.`try`.monadError.monadError
import arrow.instances.id.monad.monad
import arrow.instances.kleisli.contravariant.contravariant
import arrow.instances.kleisli.monadError.monadError
import arrow.test.UnitSpec
import arrow.test.laws.BracketLaws
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.MonadErrorLaws
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@Suppress("TestFunctionName")
@RunWith(KTestJUnitRunner::class)
class KleisliTest : UnitSpec() {
  private fun <A> TryEQ(): Eq<KleisliOf<ForTry, Int, A>> = Eq { a, b ->
    a.fix().run(1) == b.fix().run(1)
  }

  private fun <A> ConestTryEQ(): Eq<Kind<Conested<Kind<ForKleisli, ForTry>, A>, Int>> = Eq { a, b ->
    a.counnest().fix().run(1) == b.counnest().fix().run(1)
  }

  private fun IOEQ(): Eq<Kind<KleisliPartialOf<ForIO, Int>, Int>> = Eq { a, b ->
    a.fix().run(1).attempt().unsafeRunSync() == b.fix().run(1).attempt().unsafeRunSync()
  }

  private fun IOEitherEQ(): Eq<Kind<KleisliPartialOf<ForIO, Int>, Either<Throwable, Int>>> = Eq { a, b ->
    a.fix().run(1).attempt().unsafeRunSync() == b.fix().run(1).attempt().unsafeRunSync()
  }

  init {

    testLaws(
      BracketLaws.laws(
        BF = Kleisli.bracket<ForIO, Int, Throwable>(IO.bracket()),
        EQ = IOEQ(),
        EQ_EITHER = IOEitherEQ(),
        EQERR = IOEQ()
      ),
      ContravariantLaws.laws(Kleisli.contravariant(), { Kleisli { x: Int -> Try.just(x) }.conest() }, ConestTryEQ()),
      MonadErrorLaws.laws(Kleisli.monadError<ForTry, Int, Throwable>(Try.monadError()), TryEQ(), TryEQ())
    )

    "andThen should continue sequence" {
      val kleisli: Kleisli<ForId, Int, Int> = Kleisli { a: Int -> Id(a) }

      kleisli.andThen(Id.monad(), Id(3)).run(0).fix().value shouldBe 3

      kleisli.andThen(Id.monad()) { b -> Id(b + 1) }.run(0).fix().value shouldBe 1
    }
  }
}
