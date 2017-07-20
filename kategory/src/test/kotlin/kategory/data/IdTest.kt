package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
    init {

        testLaws(MonadLaws.laws(Id, Eq.any()))
        testLaws(TraverseLaws.laws(Id, Id, ::Id, Eq.any()))

        "IdMonad.binding should for comprehend over all values of multiple Ids" {
            Id.binding {
                val x = Id(1).bind()
                val y = !Id(2)
                val z = bind { Id(3) }
                yields(x + y + z)
            } shouldBe Id(6)
        }

        "IdComonad.cobinding should for comprehend over all values of multiple Ids" {
            Id.cobinding {
                val x = Id(1).extract()
                val y = !Id(2)
                val z = extract { Id(3) }
                yields(x + y + z)
            } shouldBe 6
        }

        "IdComonad.duplicate should create an instance of Id<Id<A>>" {
            Id.duplicate(Id(3)) shouldBe Id(Id(3))
        }
    }
}
