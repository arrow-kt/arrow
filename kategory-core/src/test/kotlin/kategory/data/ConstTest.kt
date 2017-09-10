package kategory

import io.kotlintest.KTestJUnitRunner
import kategory.laws.TraverseFilterLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {
    init {
        testLaws(TraverseFilterLaws.laws(Const.traverseFilter(IntMonoid), Const.applicative(IntMonoid), { Const(it) }, Eq.any()))
        testLaws(ApplicativeLaws.laws(Const.applicative(IntMonoid), Eq.any()))
    }
}
