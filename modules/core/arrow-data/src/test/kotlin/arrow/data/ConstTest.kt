package arrow.data

import arrow.instances.IntMonoidInstance
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.EqLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseFilterLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {
  init {

    testLaws(
      TraverseFilterLaws.laws(Const.traverseFilter(), Const.applicative(IntMonoidInstance), { Const(it) }, Eq.any()),
      ApplicativeLaws.laws(Const.applicative(IntMonoidInstance), Eq.any()),
      EqLaws.laws(Const.eq<Int, Int>(Eq.any()), { Const(it) }),
      ShowLaws.laws(Const.show(), Const.eq<Int, Int>(Eq.any()), { Const(it) })
    )
  }
}
