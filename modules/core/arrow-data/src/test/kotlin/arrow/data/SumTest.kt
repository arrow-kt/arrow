package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.instances.ForSum
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SumTest : UnitSpec() {
  init {

    val cf = { x: Int -> Sum.left(Id.just(x), Id.just(x)) }

    val EQ: Eq<Kind<SumPartialOf<ForId, ForId>, Int>> = Eq { a, b ->
      a.fix().extract(Id.comonad(), Id.comonad()) == b.fix().extract(Id.comonad(), Id.comonad())
    }

    ForSum<ForId, ForId>(Id.comonad(), Id.comonad()) extensions {
      testLaws(
        ComonadLaws.laws(Sum.comonad(Id.comonad(), Id.comonad()), cf, EQ)
      )
    }

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