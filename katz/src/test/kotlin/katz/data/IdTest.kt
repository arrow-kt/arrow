package katz.data

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import katz.Id
import katz.UnitSpec
import katz.binding
import katz.cobinding
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
    init {
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
                val x = Id(1).bind()
                val y = !Id(2)
                val z = bind { Id(3) }
                yields(Id(x + y + z))
            } shouldBe 6
        }
    }
}
