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

        "IdMonad.binding should for comprehend over all values of multiple Ids" {
            Id.monad().binding {
                val x = Id(1).bind()
                val y = bind { Id(2) }
                yields(x + y)
            } shouldBe Id(3)
        }

        "IdComonad.cobinding should for comprehend over all values of multiple Ids" {
            Id.comonad().cobinding {
                val x = Id(1).extract()
                val y = extract { Id(2) }
                x + y
            } shouldBe 3
        }

        "IdComonad.duplicate should create an instance of Id<Id<A>>" {
            Id.comonad().duplicate(Id(3)) shouldBe Id(Id(3))
        }
    }
}
