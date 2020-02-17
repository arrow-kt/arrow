package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.ForConst
import arrow.core.ForId
import arrow.core.ForOption
import arrow.core.ForTry
import arrow.core.Id
import arrow.core.Option
import arrow.core.Try
import arrow.core.extensions.`try`.eqK.eqK
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.const.eqK.eqK
import arrow.core.extensions.eq
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.eqK.eqK
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.fx.mtl.concurrent
import arrow.fx.mtl.timer
import arrow.mtl.extensions.kleisli.alternative.alternative
import arrow.mtl.extensions.kleisli.applicative.applicative
import arrow.mtl.extensions.kleisli.contravariant.contravariant
import arrow.mtl.extensions.kleisli.divisible.divisible
import arrow.mtl.extensions.kleisli.eqK.eqK
import arrow.mtl.extensions.kleisli.functor.functor
import arrow.mtl.extensions.kleisli.monad.monad
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.DivisibleLaws
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

  fun conestTryGENK() = object : GenK<Conested<Kind<ForKleisli, ForTry>, Int>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<Conested<Kind<ForKleisli, ForTry>, Int>, A>> = gen.map {
      Kleisli { it: Int ->
        Try.just(it)
      }.conest()
    } as Gen<Kind<Conested<Kind<ForKleisli, ForTry>, Int>, A>>
  }

  init {
    fun <F, D> genK(genkF: GenK<F>) = object : GenK<KleisliPartialOf<F, D>> {
      override fun <A> genK(gen: Gen<A>): Gen<Kind<KleisliPartialOf<F, D>, A>> = genkF.genK(gen).map { k ->
        Kleisli { _: D -> k }
      }
    }

    val optionEQK = Kleisli.eqK(Option.eqK(), 0)

    val ioEQK: EqK<Kind<Kind<ForKleisli, ForIO>, Int>> = Kleisli.eqK(IO.eqK(), 1)

    val tryEQK: EqK<Kind<Kind<ForKleisli, ForTry>, Int>> =
      Kleisli.eqK(Try.eqK(), 1)

    val constEQK: EqK<Kind<Kind<ForKleisli, Kind<ForConst, Int>>, Int>> = Kleisli.eqK(Const.eqK(Int.eq()), 1)

    testLaws(
      AlternativeLaws.laws(
        Kleisli.alternative<ForOption, Int>(Option.alternative()),
        genK<ForOption, Int>(Option.genK()),
        optionEQK
      ),
      ConcurrentLaws.laws(
        Kleisli.concurrent<ForIO, Int>(IO.concurrent()),
        Kleisli.timer<ForIO, Int>(IO.concurrent()),
        Kleisli.functor<ForIO, Int>(IO.functor()),
        Kleisli.applicative<ForIO, Int>(IO.applicative()),
        Kleisli.monad<ForIO, Int>(IO.monad()),
        genK<ForIO, Int>(IO.genK()),
        ioEQK
      ),
      ContravariantLaws.laws(Kleisli.contravariant(), conestTryGENK(), conestTryEQK()),
      DivisibleLaws.laws(
        Kleisli.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        genK<ConstPartialOf<Int>, Int>(Const.genK(Gen.int())),
        constEQK
      )
    )

    "andThen should continue sequence" {
      val kleisli: Kleisli<ForId, Int, Int> = Kleisli { a: Int -> Id(a) }

      kleisli.andThen(Id.monad(), Id(3)).run(0) shouldBe Id(3)

      kleisli.andThen(Id.monad()) { b -> Id(b + 1) }.run(0) shouldBe Id(1)
    }
  }
}
