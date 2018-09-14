package arrow.data

import arrow.core.ForId
import arrow.core.Id
import arrow.core.comonad
import arrow.instances.ForDay
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class DayTest : UnitSpec() {
  init {

    val cf = { x: Int -> Day(Id.just(x), Id.just(x), Int::plus) }

    val EQ: Eq<DayOf<ForId, ForId, Int, Int, Int>> = Eq { a, b ->
      a.fix().extract(Id.comonad(), Id.comonad()) == b.fix().extract(Id.comonad(), Id.comonad())
    }

    ForDay<ForId, ForId, Int, Int>(Id.comonad(), Id.comonad()) extensions {
      testLaws(
        ComonadLaws.laws(Day.comonad<ForId, ForId, Int, Int>(Id.comonad(), Id.comonad()), cf, EQ)
      )
    }
  }
}