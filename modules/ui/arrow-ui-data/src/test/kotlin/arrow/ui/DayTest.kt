package arrow.ui

import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.Tuple2Of
import arrow.ui.extensions.day.applicative.applicative
import arrow.ui.extensions.day.comonad.comonad
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.comonad.comonad
import arrow.core.fix
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.ComonadLaws
import arrow.typeclasses.Eq
import io.kotlintest.shouldBe

class DayTest : UnitSpec() {
  init {

    val cf = { x: Int -> Day(Id(x), Id(0)) { xx, yy -> xx + yy } }

    val EQ: Eq<DayOf<ForId, ForId, Int>> = Eq { a, b ->
      a.fix().extract(Id.comonad(), Id.comonad()) == b.fix().extract(Id.comonad(), Id.comonad())
    }

    testLaws(
      ApplicativeLaws.laws(Day.applicative(Id.applicative(), Id.applicative()), EQ),
      ComonadLaws.laws(Day.comonad(Id.comonad(), Id.comonad()), cf, EQ)
    )

    val get: (Int, Int) -> Tuple2<Int, Int> = { left, right -> Tuple2(left, right) }
    val day = Day(Id.just(1), Id.just(1), get)
    val compareSides = { left: Int, right: Int ->
      when {
        left > right -> "Left is greater"
        right > left -> "Right is greater"
        else -> "Both sides are equal"
      }
    }

    "Day extract should return the result of calling get with both sides" {
      day.extract(Id.comonad(), Id.comonad()) shouldBe Tuple2(1, 1)
    }

    @Suppress("ExplicitItLambdaParameter") // Required at runtime or else test fails
    "Day coflatmap should transform result type" {
      val d = day.coflatMap(Id.comonad(), Id.comonad()) { it: DayOf<ForId, ForId, Tuple2Of<Int, Int>> ->
        val (left, right) = it.fix().extract(Id.comonad(), Id.comonad()).fix()
        compareSides(left, right)
      }

      d.extract(Id.comonad(), Id.comonad()) shouldBe "Both sides are equal"
    }

    "Day map should transform result type" {
      val d = day.map {
        val (left, right) = it
        compareSides(left, right)
      }

      d.extract(Id.comonad(), Id.comonad()) shouldBe "Both sides are equal"
    }

    @Suppress("ExplicitItLambdaParameter") // Required at runtime or else test fails
    "Day coflatMapLazy should transform result type" {
      val d = day.coflatMapLazy(Id.comonad(), Id.comonad()) { it: DayOf<ForId, ForId, Tuple2Of<Int, Int>> ->
        val (left, right) = it.fix().extract(Id.comonad(), Id.comonad()).fix()
        compareSides(left, right)
      }

      d.extract(Id.comonad(), Id.comonad()) shouldBe "Both sides are equal"
    }

    "Day mapLazy should transform result type" {
      val d = day.mapLazy {
        val (left, right) = it
        compareSides(left, right)
      }

      d.extract(Id.comonad(), Id.comonad()) shouldBe "Both sides are equal"
    }
  }
}
