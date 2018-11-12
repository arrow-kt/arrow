package arrow.typeclasses

import arrow.instances.monoid
import arrow.instances.const.applicative.applicative
import arrow.instances.const.eq.eq
import arrow.instances.const.show.show
import arrow.mtl.instances.const.traverseFilter.traverseFilter
import arrow.test.UnitSpec
import arrow.test.laws.*
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {
  init {
    Int.monoid().run {
      testLaws(
        TraverseFilterLaws.laws(Const.traverseFilter(), Const.applicative(this), { Const(it) }, Eq.any()),
        ApplicativeLaws.laws(Const.applicative(this), Eq.any()),
        EqLaws.laws(Const.eq<Int, Int>(Eq.any())) { Const(it) },
        ShowLaws.laws(Const.show(), Const.eq<Int, Int>(Eq.any())) { Const(it) }
      )
    }
  }
}
