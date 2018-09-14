package arrow.data

import arrow.core.ForId
import arrow.core.Id
import arrow.core.applicative
import arrow.core.comonad
import arrow.instances.ForDay
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.ComonadLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class DayTest : UnitSpec() {
  init {

    val cf = { x: Int -> Day.just(Id.applicative(), Id.applicative(), x) }

    val EQ: Eq<DayOf<ForId, ForId, Int>> = Eq { a, b ->
      a.fix().extract(Id.comonad(), Id.comonad()) == b.fix().extract(Id.comonad(), Id.comonad())
    }

    ForDay(Id.applicative(), Id.applicative(), Id.comonad(), Id.comonad()) extensions {
      testLaws(
        ApplicativeLaws.laws(Day.applicative(Id.applicative(), Id.applicative()), EQ),
        ComonadLaws.laws(Day.comonad(Id.comonad(), Id.comonad()), cf, EQ)
      )
    }
  }
}
