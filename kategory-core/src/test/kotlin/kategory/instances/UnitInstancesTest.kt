package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import kategory.laws.EqLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class UnitInstancesTest : UnitSpec() {
    init {
        "instances can be resolved implicitly" {
            semigroup<Unit>() shouldNotBe null
            monoid<Unit>() shouldNotBe null
            eq<Unit>() shouldNotBe null
        }

        testLaws(
                MonoidLaws.laws(monoid<Unit>(), { _: Int -> Unit }, eq()),
                SemigroupLaws.laws(semigroup<Unit>(), { _: Int -> Unit }, eq()),
                EqLaws.laws { Unit }
        )
    }
}
