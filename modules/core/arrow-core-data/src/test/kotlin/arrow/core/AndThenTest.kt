package arrow.core

import arrow.Kind
import arrow.core.extensions.andthen.applicative.applicative
import arrow.core.extensions.andthen.category.category
import arrow.core.extensions.andthen.contravariant.contravariant
import arrow.core.extensions.andthen.functor.functor
import arrow.core.extensions.andthen.monad.monad
import arrow.core.extensions.andthen.monoid.monoid
import arrow.core.extensions.andthen.profunctor.profunctor
import arrow.core.extensions.list.foldable.foldLeft
import arrow.core.extensions.monoid
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.test.laws.CategoryLaws
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.ProfunctorLaws
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class AndThenTest : UnitSpec() {

  val EQ: Eq<AndThenOf<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  val conestedEQK = object : EqK<Conested<ForAndThen, Int>> {
    override fun <A> Kind<Conested<ForAndThen, Int>, A>.eqK(other: Kind<Conested<ForAndThen, Int>, A>, EQ: Eq<A>): Boolean =
      this@eqK.counnest().invoke(1) == other.counnest().invoke(1)
  }

  fun conestedGENK() = object : GenK<Conested<ForAndThen, Int>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<Conested<ForAndThen, Int>, A>> = gen.map {
      AndThen.just<Int, A>(it).conest()
    } as Gen<Kind<Conested<ForAndThen, Int>, A>>
  }

  init {

    testLaws(
      MonadLaws.laws(AndThen.monad(), AndThen.functor(), AndThen.applicative(), AndThen.monad(), AndThen.genK<Int>(), AndThen.eqK<Int>()),
      MonoidLaws.laws(AndThen.monoid<Int, Int>(Int.monoid()), Gen.int().map { i -> AndThen<Int, Int> { i } }, EQ),
      ContravariantLaws.laws(AndThen.contravariant<Int>(), conestedGENK(), conestedEQK),
      ProfunctorLaws.laws(AndThen.profunctor(), { AndThen.just(it) }, EQ),
      CategoryLaws.laws(AndThen.category(), { AndThen.just(it) }, EQ)
    )

    "compose a chain of functions with andThen should be same with AndThen" {
      forAll(Gen.int(), Gen.list(Gen.functionAToB<Int, Int>(Gen.int()))) { i, fs ->
        val result = fs.map(AndThen.Companion::invoke)
          .fold(AndThen<Int, Int>(::identity)) { acc, b ->
            acc.andThen(b)
          }.invoke(i)

        val expect = fs.fold({ x: Int -> x }) { acc, b ->
          acc.andThen(b)
        }.invoke(i)

        result == expect
      }
    }

    "compose a chain of function with compose should be same with AndThen" {
      forAll(Gen.int(), Gen.list(Gen.functionAToB<Int, Int>(Gen.int()))) { i, fs ->
        val result = fs.map(AndThen.Companion::invoke)
          .fold(AndThen<Int, Int>(::identity)) { acc, b ->
            acc.compose(b)
          }.invoke(i)

        val expect = fs.fold({ x: Int -> x }) { acc, b ->
          acc.compose(b)
        }.invoke(i)

        result == expect
      }
    }

    val count = 500000

    "andThen is stack safe" {
      val result = (0 until count).toList().foldLeft(AndThen<Int, Int>(::identity)) { acc, _ ->
        acc.andThen { it + 1 }
      }.invoke(0)

      result shouldBe count
    }

    "compose is stack safe" {
      val result = (0 until count).toList().foldLeft(AndThen<Int, Int>(::identity)) { acc, _ ->
        acc.compose { it + 1 }
      }.invoke(0)

      result shouldBe count
    }

    "toString is stack safe" {
      (0 until count).toList().foldLeft(AndThen<Int, Int>(::identity)) { acc, _ ->
        acc.compose { it + 1 }
      }.toString() shouldBe "AndThen.Concat(...)"
    }
  }
}

private fun <A> AndThen.Companion.eqK() = object : EqK<AndThenPartialOf<A>> {
  override fun <B> Kind<AndThenPartialOf<A>, B>.eqK(other: Kind<AndThenPartialOf<A>, B>, EQ: Eq<B>): Boolean =
    (this.fix() to other.fix()).let { (ls, rs) ->
      EQ.run {
        ls(1).eqv(rs(1))
      }
    }
}

private fun <A> AndThen.Companion.genK() = object : GenK<AndThenPartialOf<A>> {
  override fun <B> genK(gen: Gen<B>): Gen<Kind<AndThenPartialOf<A>, B>> =
    gen.map {
      AndThen.just<A, B>(it)
    }
}
