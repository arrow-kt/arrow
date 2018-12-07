package arrow.core

import arrow.instances.const.divisible.divisible
import arrow.instances.monoid
import arrow.test.UnitSpec
import arrow.test.laws.DivisibleLaws
import arrow.typeclasses.Const
import arrow.typeclasses.Eq
import arrow.typeclasses.fix
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {

  init {
    testLaws(
      DivisibleLaws.laws(Const.divisible(Int.monoid()), { i -> Const(i) }, Eq { a, b ->
        a.fix().value == b.fix().value
      })
    )
  }
}
