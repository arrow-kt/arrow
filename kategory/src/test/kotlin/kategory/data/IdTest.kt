package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
    init {
        testLaws(MonadLaws.laws(Id.monad(), Eq.any()))
        testLaws(TraverseLaws.laws(Id.traverse(), Id.functor(), ::Id, Eq.any()))
        testLaws(ComonadLaws.laws(Id.comonad(), ::Id, Eq.any()))
    }
}
