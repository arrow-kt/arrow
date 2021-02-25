package arrow.core

import arrow.Kind
import arrow.Kind2
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class AndThenTest : UnitSpec() {

  val EQ: Eq<AndThenOf<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  init {

    testLaws(
      MonoidLaws.laws(
        object : Monoid<AndThen<Int, Int>> {
          override fun empty(): AndThen<Int, Int> = AndThen.id()
          override fun AndThen<Int, Int>.combine(b: AndThen<Int, Int>): AndThen<Int, Int> = this.andThen(b)
        },
        Gen.int().map { i -> AndThen { i } },
        EQ
      )
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
      val result = (0 until count).fold(AndThen<Int, Int>(::identity)) { acc, _ ->
        acc.andThen { it + 1 }
      }.invoke(0)

      result shouldBe count
    }

    "compose is stack safe" {
      val result = (0 until count).fold(AndThen<Int, Int>(::identity)) { acc, _ ->
        acc.compose { it + 1 }
      }.invoke(0)

      result shouldBe count
    }

    "toString is stack safe" {
      (0 until count).fold(AndThen<Int, Int>(::identity)) { acc, _ ->
        acc.compose { it + 1 }
      }.toString() shouldBe "AndThen.Concat(...)"
    }

    "flatMap is stacksafe" {
      val result = (0 until count).fold(AndThen<Int, Int>(::identity)) { acc, _ ->
        acc.flatMap { i -> AndThen { i + it } }
      }.invoke(1)

      result shouldBe (count + 1)
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

private fun AndThen.Companion.eqK2() = object : EqK2<ForAndThen> {
  override fun <A, B> Kind2<ForAndThen, A, B>.eqK(other: Kind2<ForAndThen, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
    (this.fix() to other.fix()).let {
      AndThen.eqK<A>().run {
        it.first.eqK(it.second, EQB)
      }
    }
}
