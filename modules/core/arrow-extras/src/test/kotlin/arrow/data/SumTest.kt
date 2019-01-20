package arrow.data

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.fix
import arrow.core.extensions.id.comonad.comonad
import arrow.core.extensions.id.eq.eq
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.hash.hash
import arrow.data.extensions.sum.comonad.comonad
import arrow.data.extensions.sum.eq.eq
import arrow.data.extensions.sum.hash.hash
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.HashLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class SumTest : UnitSpec() {
  init {

    val cf = { x: Int -> Sum.left(Id.just(x), Id.just(x)) }

    val EQ: Eq<Kind<SumPartialOf<ForId, ForId>, Int>> = Eq { a, b ->
      a.fix().extract(Id.comonad(), Id.comonad()) == b.fix().extract(Id.comonad(), Id.comonad())
    }

    val IDEQ = Eq<Kind<ForId, Int>> { a, b -> Id.eq(Int.eq()).run { a.fix().eqv(b.fix()) } }
    val IDH = Hash<Kind<ForId, Int>> { Id.hash(Int.hash()).run { it.fix().hash() } }

    testLaws(
      ComonadLaws.laws(Sum.comonad(Id.comonad(), Id.comonad()), cf, EQ),
      HashLaws.laws(Sum.hash(IDH, IDH), Sum.eq(IDEQ, IDEQ), cf)
    )

    val abSum = Sum.left(Id.just("A"), Id.just("B"))

    "Sum extract should return the view of the current side" {
      abSum.extract(Id.comonad(), Id.comonad()) shouldBe "A"
    }

    "Sum changeSide should return the same Sum with desired side" {
      val sum = abSum.changeSide(Sum.Side.Right)

      sum.extract(Id.comonad(), Id.comonad()) shouldBe "B"
    }

    "Sum extend should transform view type" {
      val asciiValueFromLetter = { x: String -> x.first().toInt() }
      val sum = abSum.coflatmap(Id.comonad(), Id.comonad()) {
        when (it.side) {
          is Sum.Side.Left -> asciiValueFromLetter(it.left.fix().extract())
          is Sum.Side.Right -> asciiValueFromLetter(it.right.fix().extract())
        }
      }

      sum.extract(Id.comonad(), Id.comonad()) shouldBe 65
    }

    "Sum map should transform view type" {
      val asciiValueFromLetter = { x: String -> x.first().toInt() }
      val sum = abSum.map(Id.functor(), Id.functor(), asciiValueFromLetter)

      sum.extract(Id.comonad(), Id.comonad()) shouldBe 65
    }

  }
}