package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {
    init {
        testLaws(TraverseLaws.laws(Const.traverse(IntMonoid), Const.applicative(IntMonoid), { Const(it) }, Eq.any()))
        testLaws(ApplicativeLaws.laws(Const.applicative(IntMonoid), Eq.any()))
    }
}

