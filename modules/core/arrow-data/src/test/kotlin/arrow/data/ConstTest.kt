package arrow.data

import arrow.instances.monoid
import arrow.mtl.instances.*
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.EqLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseFilterLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {
  init {
    ForConst(Int.monoid()) extensions {
      testLaws(
        TraverseFilterLaws.laws(this, this, { Const(it) }, Eq.any()),
        ApplicativeLaws.laws(this, Eq.any()),
        EqLaws.laws(Const.eq<Int, Int>(Eq.any()), { Const(it) }),
        ShowLaws.laws(Const.show(), Const.eq<Int, Int>(Eq.any()), { Const(it) })
      )
    }
  }
}
