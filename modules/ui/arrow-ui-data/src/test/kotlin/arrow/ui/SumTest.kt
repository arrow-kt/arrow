package arrow.ui

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.ForConst
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.id.comonad.comonad
import arrow.core.extensions.id.eq.eq
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.hash.hash
import arrow.core.extensions.monoid
import arrow.core.fix
import arrow.core.value
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.HashLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Hash
import arrow.ui.extensions.sum.comonad.comonad
import arrow.ui.extensions.sum.divisible.divisible
import arrow.ui.extensions.sum.eq.eq
import arrow.ui.extensions.sum.hash.hash
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

class SumTest : UnitSpec() {
  init {

    val cf1 = { x: Int -> Sum.left(Id.just(x), Id.just(x)) }
    val g1 = Gen.int().map(cf1) as Gen<Kind<Kind<Kind<ForSum, ForId>, ForId>, Int>>

    val idEQ = object : EqK<SumPartialOf<ForId, ForId>> {
      override fun <A> Kind<SumPartialOf<ForId, ForId>, A>.eqK(other: Kind<SumPartialOf<ForId, ForId>, A>, EQ: Eq<A>): Boolean =
        this.fix().extract(Id.comonad(), Id.comonad()) == other.fix().extract(Id.comonad(), Id.comonad())
    }

    val IDEQ = Eq<Kind<ForId, Int>> { a, b -> Id.eq(Int.eq()).run { a.fix().eqv(b.fix()) } }
    val IDH = Hash<Kind<ForId, Int>> { Id.hash(Int.hash()).run { it.fix().hash() } }

    val cf2: (Int) -> Sum<Kind<ForConst, Int>, Kind<ForConst, Int>, Int> = { Sum.left(Const.just(it), Const.just(it)) }
    val g2 = Gen.int().map(cf2) as Gen<Kind<SumPartialOf<ConstPartialOf<Int>, ConstPartialOf<Int>>, Int>>

    val constEQK = object : EqK<SumPartialOf<ConstPartialOf<Int>, ConstPartialOf<Int>>> {
      override fun <A> Kind<SumPartialOf<ConstPartialOf<Int>, ConstPartialOf<Int>>, A>.eqK(other: Kind<SumPartialOf<ConstPartialOf<Int>, ConstPartialOf<Int>>, A>, EQ: Eq<A>): Boolean =
        (this.fix() to other.fix()).let {
          when (it.first.side) {
            is Sum.Side.Left -> when (it.second.side) {
              is Sum.Side.Left -> it.first.left.value() == it.second.left.value()
              else -> false
            }
            is Sum.Side.Right -> when (it.second.side) {
              is Sum.Side.Right -> it.first.right.value() == it.second.right.value()
              else -> false
            }
          }
        }
    }

    testLaws(
      DivisibleLaws.laws(
        Sum.divisible(Const.divisible(Int.monoid()), Const.divisible(Int.monoid())),
        g2,
        constEQK
      ),
      ComonadLaws.laws(Sum.comonad(Id.comonad(), Id.comonad()), g1, idEQ),
      HashLaws.laws(Sum.hash(IDH, IDH), Sum.eq(IDEQ, IDEQ), genSum())
    )

    val abSum = Sum.left(Id.just("A"), Id.just("B"))

    "Sum extract should return the view of the current side"
    {
      abSum.extract(Id.comonad(), Id.comonad()) shouldBe "A"
    }

    "Sum changeSide should return the same Sum with desired side"
    {
      val sum = abSum.changeSide(Sum.Side.Right)

      sum.extract(Id.comonad(), Id.comonad()) shouldBe "B"
    }

    "Sum extend should transform view type"
    {
      val asciiValueFromLetter = { x: String -> x.first().toInt() }
      val sum = abSum.coflatmap(Id.comonad(), Id.comonad()) {
        when (it.side) {
          is Sum.Side.Left -> asciiValueFromLetter(it.left.fix().extract())
          is Sum.Side.Right -> asciiValueFromLetter(it.right.fix().extract())
        }
      }

      sum.extract(Id.comonad(), Id.comonad()) shouldBe 65
    }

    "Sum map should transform view type"
    {
      val asciiValueFromLetter = { x: String -> x.first().toInt() }
      val sum = abSum.map(Id.functor(), Id.functor(), asciiValueFromLetter)

      sum.extract(Id.comonad(), Id.comonad()) shouldBe 65
    }
  }
}

private fun genSum(): Gen<Sum<ForId, ForId, Int>> =
  Gen.int().map {
    Sum.left(Id.just(it), Id.just(it))
  }
