package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.Either
import arrow.core.ForConst
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
import arrow.core.extensions.option.eq.eq
import arrow.core.fix
import arrow.core.some
import arrow.core.value
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.bracket.bracket
import arrow.fx.mtl.kleisli.bracket.bracket
import arrow.mtl.extensions.kleisli.alternative.alternative
import arrow.mtl.extensions.kleisli.contravariant.contravariant
import arrow.mtl.extensions.kleisli.divisible.divisible
import arrow.mtl.extensions.kleisli.monadError.monadError
import arrow.test.UnitSpec
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.BracketLaws
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadErrorLaws
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

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

    val cf: (Int) -> Kleisli<Kind<ForConst, Int>, Int, Int> = { Kleisli { it.const() } }
    val g = Gen.int().map(cf) as Gen<Kind<KleisliPartialOf<ConstPartialOf<Int>, Int>, Int>>

    val optionEQK = object : EqK<KleisliPartialOf<ForOption, Int>> {
      override fun <A> Kind<KleisliPartialOf<ForOption, Int>, A>.eqK(other: Kind<KleisliPartialOf<ForOption, Int>, A>, EQ: Eq<A>): Boolean =
        (this.fix().run(0).fix() to other.fix().run(0).fix()).let {
          Option.eq(EQ).run {
            it.first.eqv(it.second)
          }
        }
    }

    val ioEQK = object : EqK<KleisliPartialOf<ForIO, Int>> {
      override fun <A> Kind<KleisliPartialOf<ForIO, Int>, A>.eqK(other: Kind<KleisliPartialOf<ForIO, Int>, A>, EQ: Eq<A>): Boolean =
        (this.fix() to other.fix()).let {
          it.first.run(1).attempt().unsafeRunSync() == it.second.run(1).attempt().unsafeRunSync()
        }
    }

    val tryEQK = object : EqK<KleisliPartialOf<ForTry, Int>> {
      override fun <A> Kind<KleisliPartialOf<ForTry, Int>, A>.eqK(other: Kind<KleisliPartialOf<ForTry, Int>, A>, EQ: Eq<A>): Boolean =
        (this.fix() to other.fix()).let {
          it.first.run(1) == it.second.run(1)
        }
    }

    testLaws(
      AlternativeLaws.laws(
        Kleisli.alternative<ForOption, Int>(Option.alternative()),
        { i -> Kleisli { i.some() } },
        { i -> Kleisli { { j: Int -> i + j }.some() } },
        optionEQK
      ),
      BracketLaws.laws(
        Kleisli.bracket<ForIO, Int, Throwable>(IO.bracket()),
        ioEQK
      ),
      ContravariantLaws.laws(Kleisli.contravariant(), Gen.int().map { Kleisli { x: Int -> Try.just(x) }.conest() }, ConestTryEQ()),
      DivisibleLaws.laws(
        Kleisli.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        g,
        Eq { a, b -> a.run(1).value() == b.run(1).value() }
      ),
      MonadErrorLaws.laws(Kleisli.monadError<ForTry, Int, Throwable>(Try.monadError()), tryEQK)
    )

    "andThen should continue sequence" {
      val kleisli: Kleisli<ForId, Int, Int> = Kleisli { a: Int -> Id(a) }

      kleisli.andThen(Id.monad(), Id(3)).run(0) shouldBe 3

      kleisli.andThen(Id.monad()) { b -> Id(b + 1) }.run(0) shouldBe 1
    }
  }
}
