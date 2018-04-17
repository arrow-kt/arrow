package arrow.data

import arrow.instances.monoid
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.EqLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseFilterLaws
import arrow.typeclasses.*

import org.junit.runner.RunWith


class ConstTest : UnitSpec() {
  init {

    testLaws(
      TraverseFilterLaws.laws(Const.traverseFilter(), Const.applicative(Int.monoid()), { Const(it) }, Eq.any()),
      ApplicativeLaws.laws(Const.applicative(Int.monoid()), Eq.any()),
      EqLaws.laws(Const.eq<Int, Int>(Eq.any()), { Const(it) }),
      ShowLaws.laws(Const.show(), Const.eq<Int, Int>(Eq.any()), { Const(it) })
    )
  }
}
