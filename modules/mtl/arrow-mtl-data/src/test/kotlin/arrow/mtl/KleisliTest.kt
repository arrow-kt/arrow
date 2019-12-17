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
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.mtl.concurrent
import arrow.mtl.extensions.kleisli.alternative.alternative
import arrow.mtl.extensions.kleisli.contravariant.contravariant
import arrow.mtl.extensions.kleisli.divisible.divisible
import arrow.mtl.extensions.kleisli.monadError.monadError
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ConcurrentLaws
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

  private fun conestTryEQK() = object : EqK<Conested<Kind<ForKleisli, ForTry>, Int>> {
    override fun <A> Kind<Conested<Kind<ForKleisli, ForTry>, Int>, A>.eqK(other: Kind<Conested<Kind<ForKleisli, ForTry>, Int>, A>, EQ: Eq<A>): Boolean {
      return this.counnest().run(1) == other.counnest().run(1)
    }
  }

  private fun <A> IOEQ(): Eq<Kind<KleisliPartialOf<ForIO, Int>, A>> = Eq { a, b ->
    a.run(1).attempt().unsafeRunSync() == b.run(1).attempt().unsafeRunSync()
  }

  init {

    fun <F, D> genk(genkF: GenK<F>) = object : GenK<KleisliPartialOf<F, D>> {
      override fun <A> genK(gen: Gen<A>): Gen<Kind<KleisliPartialOf<F, D>, A>> = genkF.genK(gen).map { k ->
        Kleisli { _: D -> k }
      }
    }

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

    val constEQK = object : EqK<KleisliPartialOf<ConstPartialOf<Int>, Int>> {
      override fun <A> Kind<KleisliPartialOf<ConstPartialOf<Int>, Int>, A>.eqK(other: Kind<KleisliPartialOf<ConstPartialOf<Int>, Int>, A>, EQ: Eq<A>): Boolean =
        (this.fix() to other.fix()).let { (a, b) ->
          a.run(1).value() == b.run(1).value()
        }
    }

    testLaws(
      AlternativeLaws.laws(
        Kleisli.alternative<ForOption, Int>(Option.alternative()),
        { i -> Kleisli { i.some() } },
        { i -> Kleisli { { j: Int -> i + j }.some() } },
        optionEQK
      ),
      ConcurrentLaws.laws(
        Kleisli.concurrent<ForIO, Int>(IO.concurrent()),
        IOEQ(),
        IOEQ(),
        IOEQ()
      ),
      ContravariantLaws.laws(Kleisli.contravariant(), Gen.int().map { Kleisli { x: Int -> Try.just(x) }.conest() }, conestTryEQK()),
      DivisibleLaws.laws(
        Kleisli.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        genk<ConstPartialOf<Int>, Int>(Const.genK(Gen.int())),
        constEQK
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
