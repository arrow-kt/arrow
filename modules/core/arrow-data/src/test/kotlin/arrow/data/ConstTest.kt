package arrow.data

import arrow.instances.monoid
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {
  init {

    testLaws(
      ContravariantLaws.laws(Const.contravariant(), ::Const, Eq.any()),
      TraverseFilterLaws.laws(Const.traverseFilter(), Const.applicative(Int.monoid()), { Const(it) }, Eq.any()),
      ApplicativeLaws.laws(Const.applicative(Int.monoid()), Eq.any()),
      EqLaws.laws(Const.eq<Int, Int>(Eq.any()), { Const(it) }),
      ShowLaws.laws(Const.show(), Const.eq<Int, Int>(Eq.any()), { Const(it) })
    )
  }
}
